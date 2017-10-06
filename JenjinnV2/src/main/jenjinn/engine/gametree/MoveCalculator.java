/**
 * Copyright © 2017 Lhasa Limited
 * File created: 14 Jul 2017 by thomasb
 * Creator : thomasb
 * Version : $Id$
 */
package jenjinn.engine.gametree;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.moves.ChessMove;

/**
 * We assume for now that an instance of this interface has a set depth
 * to which it calculates.
 *
 * @author thomasb
 * @since 14 Jul 2017
 */
public interface MoveCalculator
{
	/**
	 * Searches the game tree for the best available move from the root state
	 * for the side to move. This is a blocking method which can return a value
	 * early if required. I.e. we may time box the execution time.
	 *
	 * @param root
	 * @return
	 */
	ChessMove getBestMoveFrom(BoardState root);// throws InterruptedException;

	/**
	 * Set the search depth.
	 *
	 * @param depth
	 */
	void setSearchDepth(int depth);

	/**
	 * Set the {@link BoardEvaluator} which this instance uses to calculate
	 * scores for positions.
	 *
	 * @param evaluator
	 */
	void setEvaluator(BoardEvaluator evaluator);

	/**
	 * @return a String description of this {@link MoveCalculator}
	 */
	String getDescriptor();
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