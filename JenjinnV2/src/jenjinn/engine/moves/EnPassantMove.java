/**
 * Copyright © 2017 Lhasa Limited
 * File created: 20 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Side;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public class EnPassantMove extends AbstractChessMoveImplV2
{

	private static final EnPassantMove[][] EP_CACHE = generateEnPassantMoveCache();

	private static EnPassantMove[][] generateEnPassantMoveCache()
	{
		final EnPassantMove[][] cache = new EnPassantMove[8][4];
		// TODO Auto-generated method stub
		return cache;
	}

	private EnPassantMove(final int start, final int target)
	{
		super(MoveType.ENPASSANT, start, target);
	}

	public byte getEnPassantSquare()
	{
		return (byte) (getTarget() - Math.signum(getTarget() - getStart()) * 8);
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		final Side friendlySide = state.getFriendlySide();

		final long enPassantSquare = (1L << getEnPassantSquare());

		final long[] newPieceLocations = state.getPieceLocationsCopy();

		newPieceLocations[friendlySide.index()] &= ~getStartBB();
		newPieceLocations[friendlySide.index()] |= getTargetBB();
		newPieceLocations[friendlySide.otherSide().index()] &= ~enPassantSquare;

		long newHash = updateGeneralHashFeatures(state, state.getCastleRights(), (byte) -1);
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getStart(), ChessPiece.get(friendlySide.index()));
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getTarget(), ChessPiece.get(friendlySide.index()));
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getEnPassantSquare(), ChessPiece.get(friendlySide.otherSide().index()));

		return new BoardStateImplV2(
				state.getNewRecentHashings(newHash),
				(byte) (1 - state.getFriendlySideValue()),
				state.getCastleRights(),
				state.getCastleStatus(),
				(byte) -1,
				state.getDevelopmentStatus(),
				newPieceLocations);
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