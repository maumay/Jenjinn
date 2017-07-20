/**
 * Copyright © 2017 Lhasa Limited
 * File created: 20 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public interface ChessMove
{
	BoardState evolve(BoardState state);
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