/**
 * Copyright � 2017 Lhasa Limited
 * File created: 19 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.gametree;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.evaluation.SEE;
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

	private final BoardEvaluator evaluator;
	private final SEE see = new SEE();

	public short search(final BoardState root, int alpha, final int beta)
	{
//		if (root.getHashing() == 1117970579444109206L)
//		{
//			System.out.println("Hi");
//		}
		
		currentDepth++;
		if (currentDepth > maxDepth)
		{
			maxDepth = currentDepth;
			System.out.println("Newmax qdepth: " + maxDepth);
		}

		if (root.isTerminal())
		{
			/* Not sure about this */
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
		if (alpha < standPat)
		{
			alpha = standPat;
		}

		final List<ChessMove> attackMoves = getMovesToProbe(root);
		
		for (final ChessMove mv : attackMoves)
		{
			if (currentDepth == 1)
			{
				System.out.println(mv.toString());
			}
			
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

	private List<ChessMove> getMovesToProbe(final BoardState state)
	{
		final short[] pValues = state.interpolatePieceValues();
		final List<ChessMove> mtp = new ArrayList<>(), attMvs = state.getAttackMoves();

		for (final ChessMove mv : attMvs)
		{
			//System.out.println(mv.toString());
			if (mv instanceof EnPassantMove ||
					see.isGoodExchange(mv.getTarget(), mv.getStart(), state, pValues))
			{
				mtp.add(mv);
				if (currentDepth == 1)
				{
					System.out.println(mv.toString());
				}
				
			}
		}
		if (currentDepth == 1)
		{
			System.out.println();
		}
		return mtp;
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