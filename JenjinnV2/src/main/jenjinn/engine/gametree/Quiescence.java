/**
 * Copyright � 2017 Lhasa Limited
 * File created: 19 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.gametree;

import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.TerminationType;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.moves.ChessMove;

/**
 * @author ThomasB
 * @since 19 Sep 2017
 */
public class Quiescence
{
	private final BoardEvaluator evaluator;

	
	public short search(final BoardState root, int alpha, final int beta)
	{
		final short standPat = getEval(root);

		if (standPat >= beta)
		{
			assert (short) beta == beta;
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
			/*
			 * /!\ TERMINATION STATE SHOULD BE ADDED TO EVALUATION
			 * 		OR TAKEN INTO ACCOUNT HERE BEFORE SCORE SINCE THE 
			 * 		STATE COULD BE TERMINAL
			 */
			final int score = -search(newState, -beta, -alpha);

			if (score >= beta)
			{
				assert (short) beta == beta;
				return (short) beta;
			}
			if (score > alpha)
			{
				alpha = score;
			}
		}
		assert (short) alpha == alpha;
		return (short) alpha;
	}

	private void pruneMoves(final List<ChessMove> attackMoves)
	{
		// TODO - SEE, delta pruning etc.
	}

	private short getEval(final BoardState root)
	{
		final TerminationType tType = root.getTerminationState();
		if (tType == TerminationType.NOT_TERMINAL)
		{
			return evaluator.evaluate(root);
		}
		else
		{
			return tType.value;
		}
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