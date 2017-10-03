/**
 * Copyright © 2017 Lhasa Limited
 * File created: 28 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.evaluation;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 * @since 28 Sep 2017
 */
public class SEE
{

	/**
	 *
	 */
	private SEE()
	{
	}

	public static List<ChessPiece> getAttdef(final BoardState state, final byte targsq)
	{
		final List<ChessPiece> attdef = new ArrayList<>();

		final long directAtt = 0L, xrayAtt = 0L, directDef = 0L, xrayDef = 0L;

		return attdef;
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