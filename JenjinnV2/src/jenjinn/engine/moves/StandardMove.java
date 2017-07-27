/**
 * Copyright � 2017 Lhasa Limited
 * File created: 20 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.moves;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.boardstate.CastlingRights;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.King;
import jenjinn.engine.pieces.Pawn;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public class StandardMove extends AbstractChessMoveImplV2
{
	// STATIC CACHE STUFF
	/**
	 * StandardMove database, all StandardMove objects we will need. Ordered in
	 * natural way; start is first entry and target is second. Many entries will
	 * be null but these correspond to impossible moves.
	 */
	private static final StandardMove[][] SM_CACHE = generateStandardMoveDB();

	/**
	 * This static factory is the way one should retrieve instances of this class.
	 * It accesses the stored cache.
	 *
	 * @param start
	 * @param target
	 * @return
	 */
	public static StandardMove get(final int start, final int target)
	{
		return SM_CACHE[start][target];
	}

	public static StandardMove get(final Sq start, final Sq target)
	{
		return get(start.ordinal(), target.ordinal());
	}

	private static StandardMove[][] generateStandardMoveDB()
	{
		final StandardMove[][] database = new StandardMove[64][64];

		final long[] bishopEmptyBoardMoves = BBDB.EBM[2];
		final long[] knightEmptyBoardMoves = BBDB.EBA[3];
		final long[] rookEmptyBoardMoves = BBDB.EBM[4];

		convertAndAddBitboardsToStandardMoveDB(database, bishopEmptyBoardMoves);
		convertAndAddBitboardsToStandardMoveDB(database, knightEmptyBoardMoves);
		convertAndAddBitboardsToStandardMoveDB(database, rookEmptyBoardMoves);

		return database;
	}

	private static void convertAndAddBitboardsToStandardMoveDB(final StandardMove[][] db, final long[] bitboardsToAdd)
	{
		for (byte i = 0; i < bitboardsToAdd.length; i++)
		{
			final StandardMove[] movesetAsStandardMoves = bitboardToMoves(i, bitboardsToAdd[i]);
			for (final StandardMove sm : movesetAsStandardMoves)
			{
				db[sm.getStart()][sm.getTarget()] = sm;
			}
		}
	}

	private static StandardMove[] bitboardToMoves(final byte loc, final long bitboard)
	{
		final int bitboardCard = Long.bitCount(bitboard);

		final StandardMove[] mvs = new StandardMove[bitboardCard];

		int ctr = 0;
		for (final byte b : EngineUtils.getSetBits(bitboard))
		{
			mvs[ctr++] = new StandardMove(loc, b);
		}

		return mvs;
	}

	// INSTANCE STUFF
	private StandardMove(final int start, final int target)
	{
		super(MoveType.STANDARD, start, target);
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		final ChessPiece movingPiece = state.getPieceAt(getStart(), state.getFriendlySide());
		final ChessPiece removedPiece = state.getPieceAt(getTarget(), state.getEnemySide());

		assert !(removedPiece instanceof King);

		final byte newFriendlySide = (byte) (1 - state.getFriendlySideValue());
		final byte newCastleRights = updateCastleRights(state.getCastleRights());
		final byte newEnPassantSquare = getNewEnPassantSquare(movingPiece);
		final byte newClockValue = getNewClockValue(movingPiece, removedPiece, state.getClockValue());

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
				newClockValue,
				newDevStatus,
				newPieceLocations);
	}

	private byte getNewClockValue(final ChessPiece movingPiece, final ChessPiece removedPiece, final byte oldClockValue)
	{
		if (removedPiece != null || movingPiece instanceof Pawn)
		{
			return 0;
		}
		return (byte) (oldClockValue + 1);
	}

	private byte getNewEnPassantSquare(final ChessPiece movingPiece)
	{
		if (movingPiece instanceof Pawn && Math.abs(getTarget() - getStart()) == 16)
		{
			return (byte) (getStart() + Math.signum(getTarget() - getStart()) * 8);
		}
		return BoardState.NO_ENPASSANT;
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

	@Override
	public String toString()
	{
		return "S" + "[" + Sq.getSq(getStart()).name() + ", " + Sq.getSq(getTarget()).name() + "]";
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