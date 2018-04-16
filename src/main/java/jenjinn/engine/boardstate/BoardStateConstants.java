/**
 * Copyright © 2018 Lhasa Limited
 * File created: 16 Apr 2018 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.boardstate;

import jenjinn.engine.evaluation.PieceSquareTable;
import jenjinn.engine.evaluation.piecetablesimpl.EndGamePieceSquareTable;
import jenjinn.engine.evaluation.piecetablesimpl.MiddleGamePieceSquareTable;
import jenjinn.engine.zobristhashing.ZobristHasher;

/**
 * @author ThomasB
 * @since 16 Apr 2018
 */
public final class BoardStateConstants
{
	private static final PieceSquareTable MIDDLE_GAME_PIECE_SQUARE_TABLE = new MiddleGamePieceSquareTable();
	private static final PieceSquareTable END_GAME_PIECE_SQUARE_TABLE = new EndGamePieceSquareTable();
	private static final ZobristHasher BOARD_HASHER = ZobristHasher.getDefault();

	public static PieceSquareTable getMiddleGamePST()
	{
		return MIDDLE_GAME_PIECE_SQUARE_TABLE;
	}

	public static PieceSquareTable getEndGamePST()
	{
		return END_GAME_PIECE_SQUARE_TABLE;
	}

	public static ZobristHasher getStateHasher()
	{
		return BOARD_HASHER;
	}

	private BoardStateConstants() {}
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