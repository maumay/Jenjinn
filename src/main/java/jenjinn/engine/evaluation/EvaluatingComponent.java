/**
 * Copyright � 2017 Lhasa Limited
 * File created: 11 Aug 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.evaluation;

import jenjinn.engine.boardstate.BoardState;

/**
 * @author ThomasB
 * @since 11 Aug 2017
 */
public interface EvaluatingComponent
{
	/**
	 * Important that we evaluate like we are not in negamax framework here, i.e. positive
	 * is good for white and negative is good for black. Then the top level evaluator will
	 * handle the conversion.
	 * 
	 * @param state
	 * @return
	 */
	short evaluate(BoardState state);
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