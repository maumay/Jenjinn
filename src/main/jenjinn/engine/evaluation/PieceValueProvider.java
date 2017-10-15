package jenjinn.engine.evaluation;

import jenjinn.engine.evaluation.piecetablesimpl.EndGamePSTimplV1;
import jenjinn.engine.evaluation.piecetablesimpl.MiddleGamePSTimplV1;

public final class PieceValueProvider
{
	public static final short[] MGAME_VALUES = MiddleGamePSTimplV1.PIECE_VALUES;
	public static final short[] EGAME_VALUES = EndGamePSTimplV1.PIECE_VALUES;

	private PieceValueProvider()
	{
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