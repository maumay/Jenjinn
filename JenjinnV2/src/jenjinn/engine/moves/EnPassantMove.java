/**
 * Copyright � 2017 Lhasa Limited
 * File created: 20 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.moves;

import static jenjinn.engine.boardstate.BoardState.END_TABLE;
import static jenjinn.engine.boardstate.BoardState.MID_TABLE;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.Pawn;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public class EnPassantMove extends AbstractChessMoveImplV2
{

	/**
	 * EnPassant moves are so rare that I don't think we really need to cache them.
	 *
	 * @param start
	 * @param target
	 * @return
	 */
	public static EnPassantMove get(final int start, final int target)
	{
		return new EnPassantMove(start, target);
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
		assert state.getPieceAt(getEnPassantSquare(), state.getEnemySide()) instanceof Pawn;

		final Side friendlySide = state.getFriendlySide();

		// Update piece locations ---------------------------------------
		final long enPassantSquareBB = 1L << getEnPassantSquare();

		final long[] newPieceLocations = state.getPieceLocationsCopy();

		newPieceLocations[friendlySide.index()] &= ~getStartBB();
		newPieceLocations[friendlySide.index()] |= getTargetBB();
		newPieceLocations[friendlySide.otherSide().index()] &= ~enPassantSquareBB;
		//---------------------------------------------------------------
		
		// Update metadata ----------------------------------------------
		long newHash = updateGeneralHashFeatures(state, state.getCastleRights(), BoardState.NO_ENPASSANT);
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getStart(), ChessPiece.get(friendlySide.index()));
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getTarget(), ChessPiece.get(friendlySide.index()));
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getEnPassantSquare(), ChessPiece.get(friendlySide.otherSide().index()));
		//---------------------------------------------------------------
		
		// Update positional evaluations --------------------------------
		short midPosEval = state.getMidgamePositionalEval(), endPosEval = state.getEndgamePositionalEval();
		
		midPosEval += MID_TABLE.getPieceSquareValue((byte) (friendlySide.index()), getTarget());
		midPosEval -= MID_TABLE.getPieceSquareValue((byte) (friendlySide.index()), getStart());
		
		endPosEval += END_TABLE.getPieceSquareValue((byte) (friendlySide.index()), getTarget());
		endPosEval -= END_TABLE.getPieceSquareValue((byte) (friendlySide.index()), getStart());
		
		midPosEval -= MID_TABLE.getPieceSquareValue((byte) (friendlySide.otherSide().index()), getEnPassantSquare());
		endPosEval -= END_TABLE.getPieceSquareValue((byte) (friendlySide.otherSide().index()), getEnPassantSquare());
		//---------------------------------------------------------------
		
		return new BoardStateImplV2(
				state.getNewRecentHashings(newHash),
				1 - state.getFriendlySideValue(),
				state.getCastleRights(),
				state.getCastleStatus(),
				BoardState.NO_ENPASSANT,
				0,
				state.getPiecePhase(),
				midPosEval,
				endPosEval,
				state.getDevelopmentStatus(),
				newPieceLocations);
	}

	public static EnPassantMove get(final Sq start, final Sq targSq)
	{
		return get(start.ordinal(), targSq.ordinal());
	}

	@Override
	public String toString()
	{
		return "E" + "[" + Sq.getSq(getStart()).name() + ", " + Sq.getSq(getTarget()).name() + "]";
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