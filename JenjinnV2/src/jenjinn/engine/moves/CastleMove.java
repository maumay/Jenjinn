/**
 * Copyright � 2017 Lhasa Limited
 * File created: 20 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.boardstate.CastlingRights;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Side;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public final class CastleMove extends AbstractChessMoveImplV2
{
	// INSTANCES
	public static final CastleMove WHITE_KINGSIDE = new CastleMove(3, 1, new int[] { 3, 1, 0, 2 });

	public static final CastleMove WHITE_QUEENSIDE = new CastleMove(3, 5, new int[] { 3, 5, 7, 4 });

	public static final CastleMove BLACK_KINGSIDE = new CastleMove(59, 57, new int[] { 59, 57, 56, 58 });

	public static final CastleMove BLACK_QUEENSIDE = new CastleMove(59, 61, new int[] { 59, 62, 63, 61 });
	
	public static CastleMove get(String name)
	{
		switch(name)
		{
		case "WHITE_KINGSIDE":
			return WHITE_KINGSIDE;
		case "WHITE_QUEENSIDE":
			return WHITE_QUEENSIDE;
		case "BLACK_KINGSIDE":
			return BLACK_KINGSIDE;
		case "BLACK_QUEENSIDE":
			return BLACK_QUEENSIDE;
		default:
			throw new IllegalArgumentException();
		}
	}

	// ------------------------------------------------------------------------------------------------
	/** The bit indices representing the 'from' and 'to' squares for the king in this castle move. */
	private final byte kingRemovalSquare, kingAdditionSquare;

	/** The bit indices representing the 'from' and 'to' squares for the rook in this castle move. */
	private final byte rookRemovalSquare, rookAdditionSquare;

	private CastleMove(final int start, final int target, final int[] requiredSquares)
	{
		super(MoveType.CASTLE, start, target);

		assert requiredSquares.length == 4;

		kingRemovalSquare = (byte) requiredSquares[0];
		kingAdditionSquare = (byte) requiredSquares[1];
		rookRemovalSquare = (byte) requiredSquares[2];
		rookAdditionSquare = (byte) requiredSquares[3];
	}

	public Side getMoveSide()
	{
		return kingRemovalSquare / 8 == 0 ? Side.W : Side.B;
	}

	public boolean isKingside()
	{
		return rookRemovalSquare % 8 == 0;
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		final Side moveSide = getMoveSide();

		final long[] newPiecePositions = state.getPieceLocationsCopy();

		newPiecePositions[5 + moveSide.index()] &= ~(1L << kingRemovalSquare);
		newPiecePositions[5 + moveSide.index()] |= (1L << kingAdditionSquare);

		newPiecePositions[3 + moveSide.index()] &= ~(1L << rookRemovalSquare);
		newPiecePositions[3 + moveSide.index()] |= (1L << rookAdditionSquare);

		final byte newCastleRights = updateCastleRights(state.getCastleRights(), moveSide);
		final byte newCastleStatus = updateCastleStatus(state.getCastleStatus(), moveSide);

		long newHash = updateGeneralHashFeatures(state, newCastleRights, BoardState.NO_ENPASSANT);
		newHash ^= BoardState.HASHER.getSquarePieceFeature(kingAdditionSquare, ChessPiece.get(5 + moveSide.index()));
		newHash ^= BoardState.HASHER.getSquarePieceFeature(kingRemovalSquare, ChessPiece.get(5 + moveSide.index()));
		newHash ^= BoardState.HASHER.getSquarePieceFeature(rookAdditionSquare, ChessPiece.get(3 + moveSide.index()));
		newHash ^= BoardState.HASHER.getSquarePieceFeature(rookRemovalSquare, ChessPiece.get(3 + moveSide.index()));

		return new BoardStateImplV2(
				state.getNewRecentHashings(newHash),
				1 - state.getFriendlySideValue(),
				newCastleRights,
				newCastleStatus,
				BoardState.NO_ENPASSANT,
				state.getClockValue() + 1,
				state.getDevelopmentStatus(),
				newPiecePositions);
	}

	private byte updateCastleRights(final byte oldRights, final Side moveSide)
	{
		final byte sideShift = (byte) (moveSide.isWhite() ? 0 : 2);
		final int toMovesRights = CastlingRights.VALUES.get(sideShift) | CastlingRights.VALUES.get(sideShift + 1);
		return (byte) (oldRights & ~toMovesRights);
	}

	private byte updateCastleStatus(final byte oldStatus, final Side moveSide)
	{
		final byte sideShift = (byte) (moveSide.isWhite() ? 0 : 2);
		final int kingsideShift = (byte) (isKingside() ? 0 : 1);
		return (byte) (oldStatus | CastlingRights.VALUES.get(sideShift + kingsideShift));
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