/**
 * Copyright © 2017 Lhasa Limited
 * File created: 20 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.testingengine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.Pawn;
import jenjinn.testingengine.boardstate.TBoardState;

/**
 * @author ThomasB
 * @since 20 Sep 2017
 */
public class TEnPassantMove extends TAbstractChessMove
{
	/**
	 * @param type
	 * @param start
	 * @param target
	 */
	public TEnPassantMove(final int start, final int target)
	{
		super(MoveType.ENPASSANT, start, target);
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		assert state.getPieceAt(getEnPassantSquare(), state.getEnemySide()) instanceof Pawn;

		final Side friendlySide = state.getFriendlySide();

		// Update piece locations ---------------------------------------
		final long enPassantSquareBB = 1L << getEnPassantSquare();

		final long[] newPieceLocations = state.getPieceLocationsCopy();

		newPieceLocations[friendlySide.index()] &= ~getStartBB();
		newPieceLocations[friendlySide.index()] |= getTargetBB();
		newPieceLocations[friendlySide.otherSide().index()] &= ~enPassantSquareBB;
		// ---------------------------------------------------------------

		return new TBoardState(friendlySide.otherSide(),
				newPieceLocations,
				state.getCastleRights(),
				state.getCastleStatus(),
				state.getDevelopmentStatus(),
				BoardState.NO_ENPASSANT,
				(byte) 0,
				state.getHashes());
	}

	public final byte getEnPassantSquare()
	{
		return (byte) (getTarget() - Math.signum(getTarget() - getStart()) * 8);
	}

	@Override
	public String toString()
	{
		return "E" + "[" + Sq.get(getStart()).name() + ", " + Sq.get(getTarget()).name() + "]";
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