/**
 * Copyright © 2017 Lhasa Limited
 * File created: 20 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.boardstate.CastlingRights;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.Pawn;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public class StandardMove extends AbstractChessMoveImplV2
{
	StandardMove(final int start, final int target)
	{
		super(MoveType.STANDARD, start, target);
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		final ChessPiece movingPiece = state.getPieceAt(getStart(), state.getFriendlySide());
		final ChessPiece removedPiece = state.getPieceAt(getStart(), state.getEnemySide());

		final byte newFriendlySide = (byte) (1 - state.getFriendlySideValue());
		final byte newCastleRights = updateCastleRights(state.getCastleRights());
		final byte newEnPassantSquare = getNewEnPassantSquare(movingPiece);

		long newHash = updateGeneralHashFeatures(state, newCastleRights, newEnPassantSquare);
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getStart(), movingPiece);

		final long start = getStartBB(), target = getTargetBB();

		final long[] newPieceLocations = state.getPieceLocationsCopy();
		newPieceLocations[movingPiece.getIndex()] &= ~start;
		newPieceLocations[movingPiece.getIndex()] |= target;

		if (removedPiece != null)
		{
			newPieceLocations[removedPiece.getIndex()] &= ~target;
			newHash ^= BoardState.HASHER.getSquarePieceFeature(getTarget(), removedPiece);
		}

		final long newDevStatus = state.getDevelopmentStatus() & ~start;

		return new BoardStateImplV2(
				state.getNewRecentHashings(newHash),
				newFriendlySide,
				newCastleRights,
				state.getCastleStatus(),
				newEnPassantSquare,
				newDevStatus,
				newPieceLocations);
	}

	private byte getNewEnPassantSquare(final ChessPiece movingPiece)
	{
		if (movingPiece instanceof Pawn && Math.abs(getTarget() - getStart()) == 16)
		{
			return (byte) (getStart() + Math.signum(getTarget() - getStart()) * 8);
		}
		return -1;
	}

	byte updateCastleRights(byte oldRights)
	{
		if (oldRights > 0)
		{
			if (CastlingRights.STANDARD_MOVE_ERASURES.containsKey(getStart()))
			{
				oldRights &= ~CastlingRights.STANDARD_MOVE_ERASURES.get(getStart());
			}
			if (CastlingRights.STANDARD_MOVE_ERASURES.containsKey(getTarget()))
			{
				oldRights &= ~CastlingRights.STANDARD_MOVE_ERASURES.get(getTarget());
			}
		}
		return oldRights;
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