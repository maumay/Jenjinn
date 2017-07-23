/**
 * Copyright � 2017 Lhasa Limited
 * File created: 21 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 * @since 21 Jul 2017
 */
public class PromotionMove extends AbstractChessMoveImplV2
{
	public static PromotionMove get(final int start, final int target)
	{
		return new PromotionMove(start, target);
	}

	/**
	 * @param type
	 * @param start
	 * @param target
	 */
	private PromotionMove(final int start, final int target)
	{
		super(MoveType.PROMOTION, start, target);
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		final Side friendlySide = state.getFriendlySide();

		final ChessPiece removedPiece = state.getPieceAt(getTarget(), friendlySide.otherSide());

		final long[] newPieceLocations = state.getPieceLocationsCopy();

		newPieceLocations[friendlySide.index()] &= ~getStartBB();
		newPieceLocations[friendlySide.index() + 4] |= getTargetBB(); // Add a queen

		long newHash = updateGeneralHashFeatures(state, state.getCastleRights(), (byte) -1);
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getStart(), ChessPiece.get(friendlySide.index()));
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getTarget(), ChessPiece.get(friendlySide.index() + 4));

		if (removedPiece != null)
		{
			newPieceLocations[removedPiece.getIndex()] &= ~getTargetBB();
			newHash ^= BoardState.HASHER.getSquarePieceFeature(getTarget(), ChessPiece.get(removedPiece.getIndex()));
		}

		return new BoardStateImplV2(
				state.getNewRecentHashings(newHash),
				1 - state.getFriendlySideValue(),
				state.getCastleRights(),
				state.getCastleStatus(),
				BoardState.NO_ENPASSANT,
				0,
				state.getDevelopmentStatus(),
				newPieceLocations);
	}
	
	public String toString()
	{
		return "P" + "[" + Sq.getSq(getStart()).name() + ", " + Sq.getSq(getTarget()).name() + "]";
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