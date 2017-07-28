/**
 * Copyright � 2017 Lhasa Limited
 * File created: 20 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.CastlingRights;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public interface ChessMove
{
	/** Don't modify this obviously */
	byte[] PIECE_PHASES = { 0, 1, 1, 2, 4 };

	BoardState evolve(BoardState state);

	default String toRecordString()
	{
		throw new RuntimeException();
	}

	static ChessMove getFromReportString(final String string)
	{
		throw new RuntimeException();
	}

	default byte updatePiecePhase(final byte oldPhase, final ChessPiece removedPiece)
	{
		return (byte) (oldPhase + PIECE_PHASES[removedPiece.getIndex() % 6]);
	}

	default long updateGeneralHashFeatures(final BoardState oldState, final byte newCastleRights, final byte newEnPassantSquare)
	{
		long newHashing = oldState.getHashing() ^ BoardState.HASHER.getBlackToMove();

		// Can't gain castling rights, can only lose them.
		final byte castleRightsChange = (byte) (oldState.getCastleRights() & ~newCastleRights);
		if (castleRightsChange > 0)
		{
			for (int i = 0; i < 4; i++)
			{
				if ((CastlingRights.VALUES.get(i) & castleRightsChange) > 0)
				{
					newHashing ^= BoardState.HASHER.getCastleFeature(i);
				}
			}
		}

		if (oldState.getEnPassantSq() != BoardState.NO_ENPASSANT)
		{
			newHashing ^= BoardState.HASHER.getEnpassantFeature(oldState.getEnPassantSq() % 8);
		}
		if (newEnPassantSquare != BoardState.NO_ENPASSANT)
		{
			newHashing ^= BoardState.HASHER.getEnpassantFeature(newEnPassantSquare % 8);
		}

		return newHashing;
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