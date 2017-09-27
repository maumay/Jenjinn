/**
 * Copyright � 2017 Lhasa Limited
 * File created: 19 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.gametree;

import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.moves.ChessMove;

/**
 * @author ThomasB
 * @since 19 Sep 2017
 */
public class Quiescence
{
	static int maxDepth = 0, currentDepth = 0;

	private final BoardEvaluator evaluator;

	public short search(final BoardState root, int alpha, final int beta)
	{
		currentDepth++;
		if (currentDepth > maxDepth)
		{
			maxDepth = currentDepth;
			System.out.println("Newmax qdepth: " + maxDepth);
		}

		if (root.isTerminal())
		{
			currentDepth--;
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

		final List<ChessMove> attackMoves = root.getAttackMoves();
		pruneMoves(attackMoves);

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

	private void pruneMoves(final List<ChessMove> attackMoves)
	{
		// TODO - SEE, delta pruning etc.
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