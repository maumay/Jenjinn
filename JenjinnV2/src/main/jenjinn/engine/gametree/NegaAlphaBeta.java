/**
 * Copyright � 2017 Lhasa Limited
 * File created: 13 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.gametree;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Infinity;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.moves.ChessMove;

/**
 * @author ThomasB
 * @since 13 Jul 2017
 */
public class NegaAlphaBeta implements MoveCalculator
{
	/**
	 * Only use a nega evaluator, i.e one that is signed depending
	 * on whether white or black is to move.
	 */
	private Quiescence quiescence;
	private int depth = 4;

	public NegaAlphaBeta(final BoardEvaluator eval)
	{
		this.quiescence = new Quiescence(eval);
	}

	public ChessMove getBestMoveFrom(final BoardState root, final int depth)
	{
		// Initialise variables
		ChessMove bestMove = null;
		int alpha = -Infinity.INT_INFINITY; // Here alpha is the calculated value of our best move.

		for (final ChessMove mv : root.getMoves())
		{
			final int bestReply = -nAlphaBeta(mv.evolve(root), -Infinity.INT_INFINITY, -alpha, depth - 1);

			if (bestReply > alpha) // We want to maximise the value of best reply
			{
				alpha = bestReply;
				bestMove = mv;
			}
		}
		return bestMove;
	}

	/**
	 * So now for both sides alpha is the minimum score we are guaranteed to be able
	 * to get and beta is the best score
	 *
	 * @param root
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @return
	 */
	public int nAlphaBeta(final BoardState root, int alpha, final int beta, final int depth)
	{
		if (depth == 0 || root.isTerminal())
		{
			return quiescence.search(root, alpha, beta);
		}

		for (final ChessMove mv : root.getMoves())
		{
			/* Let root.sideToMove = S. Then bestReply is the best score !S can achieve from
			 * the perspective of S, so the higher the score the better it is for S. */
			final int bestReply = -nAlphaBeta(mv.evolve(root), -beta, -alpha, depth - 1);

			if (bestReply >= beta)
			{
				return beta;
			}
			if (bestReply > alpha)
			{
				alpha = bestReply;
			}
		}
		return alpha;
	}

	@Override
	public ChessMove getBestMove(final BoardState root)
	{
		return getBestMoveFrom(root, depth);
	}

	@Override
	public void setSearchDepth(final int depth)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setEvaluator(final BoardEvaluator evaluator)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescriptor()
	{
		// TODO Auto-generated method stub
		return null;
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