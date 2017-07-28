/**
 * Copyright © 2017 Lhasa Limited
 * File created: 28 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.evaluation;

import jenjinn.engine.boardstate.BoardState;

/**
 * Simple interface representing a piece square table which integrates PIECE VALUES
 * into the table as well as bonuses/penalties for positions. This means we don't
 * have to calculate material values separately. The scores are designed to be stored
 * in {@link BoardState} instances and updated incrementally during evolution.
 *
 * @author ThomasB
 * @since 28 Jul 2017
 */
public interface PieceSquareTable
{
	short getPieceSquareValue(byte pieceIndex, byte squareIndex);
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