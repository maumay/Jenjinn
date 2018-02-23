package jenjinn.engine.evaluation;

import jenjinn.engine.boardstate.BoardState;

/**
 * @author ThomasB
 * @since 11 Aug 2017
 */
public interface EvaluatingComponent
{
	/**
	 * Important that we evaluate like we are not in negamax framework here, i.e.
	 * positive is good for white and negative is good for black. Then the top level
	 * evaluator will handle the conversion.
	 * 
	 * @param state
	 * @return
	 */
	short evaluate(BoardState state);
}
