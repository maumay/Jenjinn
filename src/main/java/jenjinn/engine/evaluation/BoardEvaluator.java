package jenjinn.engine.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.evaluation.componentimpl.DevelpmentV1;
import jenjinn.engine.evaluation.componentimpl.MobilityAndKingSafetyV2;
import jenjinn.engine.evaluation.componentimpl.PawnStructureV1;

/**
 * Simple interface representing an object which can take a {@link BoardState}
 * instance as an argument and calculate an unsigned score evaluation. By
 * unsigned we mean that no matter who it is to move, a higher score is better
 * for them.
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
	 * parameter and returning a 16bit integer value where the higher the score the
	 * better for the side which has the move and the lower the score the better for
	 * the opposition.
	 *
	 * @param state
	 *            is what we want to evaluate.
	 * @return the evaluation value
	 */
	public short evaluate(final BoardState state)
	{
		int score = 0;
		if (state.isTerminal()) {
			score = state.getTerminationState().value;
		}
		else {
			for (final EvaluatingComponent component : components) {
				score += component.evaluate(state);
			}
			score += evalPiecePositions(state);
		}
		assert (short) score == score;

		final int orientation = state.getFriendlySide().orientation();
		return (short) (orientation * score);
	}

	private short evalPiecePositions(final BoardState state)
	{
		final short midGameEval = state.getMidgamePositionalEval(), endGameEval = state.getEndgamePositionalEval();
		final short gamePhase = state.getGamePhase();
		return (short) (((midGameEval * (256 - gamePhase)) + (endGameEval * gamePhase)) / 256);
	}

	public static BoardEvaluator getDefault()
	{
		return new BoardEvaluator(
				Arrays.asList(new PawnStructureV1(), new MobilityAndKingSafetyV2(), new DevelpmentV1()));
	}
}