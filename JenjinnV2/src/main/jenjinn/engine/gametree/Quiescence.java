/**
 * Copyright � 2017 Lhasa Limited
 * File created: 19 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.gametree;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.evaluation.SEE;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnPassantMove;

/**
 * @author ThomasB
 * @since 19 Sep 2017
 */
public class Quiescence
{
	/* Interesting to track how deep quiescence probes. */
	static int maxDepth = 0, currentDepth = 0;

	/** Delta pruning safety margin. */
	private static final int DP_SAFETY_MARGIN = 200;

	private final BoardEvaluator evaluator;
	private final SEE see = new SEE();

	public short search(final BoardState root, int alpha, final int beta) throws InterruptedException
	{
		
		if (Thread.currentThread().isInterrupted())
		{
			throw new InterruptedException();
		}
		
		currentDepth++;
		if (currentDepth > maxDepth)
		{
			maxDepth = currentDepth;
			System.out.println("Newmax qdepth: " + maxDepth);
		}

		if (root.isTerminal())
		{
			currentDepth--;
			assert root.getTerminationState().matches(root.getFriendlySide());
			return (short) (root.getFriendlySide().orientation() * root.getTerminationState().value);
		}

		final short standPat = evaluator.evaluate(root);

		if (standPat >= beta)
		{
			assert (short) beta == beta;
			currentDepth--;
			return (short) beta;
		}

		// Rough delta prune
		final short[] pValues = root.interpolatePieceValues();
		/* big delta is the largest material swing */
		final int bigDelta = pValues[4] + (isPromotingPawn(root) ? pValues[4] - pValues[0] : 0);

		if (standPat < alpha - bigDelta)
		{
			// If we are here there is no way we will increase alpha so leave now
			currentDepth--;
			return (short) alpha;
		}

		if (alpha < standPat)
		{
			alpha = standPat;
		}

		final List<ChessMove> attackMoves = getMovesToProbe(root, pValues, standPat, alpha);

		for (final ChessMove mv : attackMoves)
		{
			final BoardState newState = mv.evolve(root);

			final int score = -search(newState, -beta, -alpha);

			if (score >= beta)
			{
				assert (short) beta == beta;
				currentDepth--;
				return (short) beta;
			}
			if (score > alpha)
			{
				alpha = score;
			}
		}
		currentDepth--;
		assert (short) alpha == alpha;
		return (short) alpha;
	}

	private List<ChessMove> getMovesToProbe(final BoardState state, final short[] pValues, final int standPat, final int alpha)
	{
		final List<ChessMove> mtp = new ArrayList<>(), attMvs = state.getAttackMoves();

		for (final ChessMove mv : attMvs)
		{
			if (mv instanceof EnPassantMove)
			{
				// Delta prune
				if (standPat >= alpha - (pValues[0] + DP_SAFETY_MARGIN))
				{
					mtp.add(mv);
				}
				continue;
			}

			final int targVal = pValues[state.getPieceAt(mv.getTarget(), state.getEnemySide()).index() % 6];

			if (standPat >= alpha - (targVal + DP_SAFETY_MARGIN) // Delta prune
					&& see.isGoodExchange(mv.getTarget(), mv.getStart(), state, pValues))
			{
				mtp.add(mv);
			}
		}
		return mtp;
	}

	private boolean isPromotingPawn(final BoardState state)
	{
		final Side friendly = state.getFriendlySide();
		final long enemys = state.getSideLocations(friendly.otherSide());
		final long friendlyPawns = state.getPieceLocations(friendly.index());
		final long seventhRank = 0b11111111L << (friendly.isWhite() ? 48 : 8);

		for (final byte loc : EngineUtils.getSetBits(friendlyPawns & seventhRank))
		{
			if ((BBDB.EBA[friendly.ordinal()][loc] & enemys) != 0)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 */
	public Quiescence(final BoardEvaluator evaluator)
	{
		this.evaluator = evaluator;
	}

	public BoardEvaluator getEvaluator()
	{
		return evaluator;
	}
}

/* ---------------------------------------------------------------------*
 * This software is the confidential and proprietary
 * information of Lhasa Limited
 * Granary Wharf House, 2 Canal Wharf, Leeds, LS11 5PS
 * ---
 * No part of this confidential information shall be disclosed
 * and it shall be used only in accordance with the terms of a
 * written license agreement entered into by holder of the information
 * with LHASA Ltd.
 * --------------------------------------------------------------------- */