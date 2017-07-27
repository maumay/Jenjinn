/**
 * Copyright © 2017 Lhasa Limited
 * File created: 27 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.evaluation;

import jenjinn.engine.boardstate.BoardState;

/**
 * @author ThomasB
 * @since 27 Jul 2017
 */
public interface BoardEvaluator
{
	/**
	 * A SYMMETRIC evaluation function taking a {@link BoardState} instance as a
	 * parameter and returning a 32bit integer value where the higher the score
	 * the better for the side which has the move and the lower the score the
	 * better for the opposition.
	 *
	 * @param state is what we want to evaluate.
	 * @return the evaluation value
	 */
	int evaluate(BoardState state);
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