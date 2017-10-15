/**
 * Copyright � 2017 Lhasa Limited
 * File created: 20 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.testingengine.moves;

import java.util.EnumSet;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.PieceType;
import jenjinn.testingengine.boardstate.TBoardState;

/**
 * @author ThomasB
 * @since 20 Sep 2017
 */
public class TPromotionMove extends TAbstractChessMove
{
	private PieceType toPromoteTo;
	
	/**
	 * @param type
	 * @param start
	 * @param target
	 */
	public TPromotionMove(final int start, final int target, PieceType toPromoteTo)
	{
		super(MoveType.PROMOTION, start, target);
		assert !EnumSet.of(PieceType.K, PieceType.P).contains(toPromoteTo);
		this.toPromoteTo = toPromoteTo;
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		final Side friendlySide = state.getFriendlySide();
		int newPieceIndex = friendlySide.index() + toPromoteTo.getId();

		final ChessPiece removedPiece = state.getPieceAt(getTarget(), friendlySide.otherSide());

		// Update piece locations ---------------------------------------------
		final long[] newPieceLocations = state.getPieceLocationsCopy();

		newPieceLocations[friendlySide.index()] &= ~getStartBB();
		newPieceLocations[newPieceIndex] |= getTargetBB(); 
		if (removedPiece != null)
		{
			newPieceLocations[removedPiece.index()] &= ~getTargetBB();
		}
		// ---------------------------------------------------------------------
		return new TBoardState(friendlySide.otherSide(),
				newPieceLocations,
				state.getCastleRights(),
				state.getCastleStatus(),
				state.getDevelopmentStatus(),
				BoardState.NO_ENPASSANT,
				(byte) 0,
				state.getHashes());
	}

	@Override
	public String toString()
	{
		return "P" + "[" + Sq.get(getStart()).name() + ", " + Sq.get(getTarget()).name() + "]";
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