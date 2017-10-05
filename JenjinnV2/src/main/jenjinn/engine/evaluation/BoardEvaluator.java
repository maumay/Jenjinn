/**
 * Copyright � 2017 Lhasa Limited
 * File created: 27 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.evaluation.componentimpl.KingSafetyV1;
import jenjinn.engine.evaluation.componentimpl.MobilityV1;
import jenjinn.engine.evaluation.componentimpl.PawnStructureV1;

/**
 * Simple interface representing an object which can take a {@link BoardState} instance
 * as an argument and calculate an unsigned score evaluation. By unsigned we mean that
 * no matter who it is to move, a higher score is better for them.
 *
 * @author ThomasB
 * @since 27 Jul 2017
 */
public class BoardEvaluator
{
	private final List<EvaluatingComponent> components;

	public BoardEvaluator(final List<EvaluatingComponent> components)
	{
		this.components = new ArrayList<>(components);
	}

	/**
	 * A NEGAMAX evaluation function taking a {@link BoardState} instance as a
	 * parameter and returning a 16bit integer value where the higher the score
	 * the better for the side which has the move and the lower the score the
	 * better for the opposition.
	 *
	 * @param state is what we want to evaluate.
	 * @return the evaluation value
	 */
	public short evaluate(final BoardState state)
	{
		final int orientation = state.getFriendlySide().orientation();
		
		if (state.isTerminal())
		{
			return (short) (orientation*state.getTerminationState().value);
		}

		int score = 0;
		for (final EvaluatingComponent component : components)
		{
			score += orientation * component.evaluate(state);
		}
		score += orientation * evalPiecePositions(state);

		assert (short) score == score;

		return (short) (state.getFriendlySide().orientation() * score);
	}

	private short evalPiecePositions(final BoardState state)
	{
		final short midGameEval = state.getMidgamePositionalEval(), endGameEval = state.getEndgamePositionalEval();
		final short gamePhase = state.getGamePhase();
		return (short) (((midGameEval * (256 - gamePhase)) + (endGameEval * gamePhase)) / 256);
	}

	public static BoardEvaluator getDefault()
	{
		return new BoardEvaluator(Arrays.asList());//new PawnStructureV1()));//new KingSafetyV1(), new MobilityV1(), ));
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