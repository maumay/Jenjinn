/**
 * Copyright © 2017 Lhasa Limited
 * File created: 19 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.boardstate;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.TerminationType;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.zobristhashing.ZobristHasher;

/**
 * @author ThomasB
 * @since 19 Jul 2017
 */
public class BoardStateImplV2 implements BoardState
{
	public static ZobristHasher HASHER = ZobristHasher.getDefault();

	public static void setHasher(final ZobristHasher hASHER)
	{
		HASHER = hASHER;
	}

	/**
	 * An array of four most recent board hashings (including the
	 * hash of this state) for determining repetition draws.
	 */
	private final long[] recentHashings;

	/**
	 * Representation of the side to move, i.e the friendly side.
	 * 0 for white and 1 for black.
	 */
	private final byte friendlySide;
	private final byte castleRights;
	private final byte castleStatus;
	private final byte enPassantSq;
	private final long devStatus; // For simplicity use a long

	private final long[] pieceLocations;

	public BoardStateImplV2(final long[] recentHashings,
			final byte friendlySide,
			final byte castleRights,
			final byte castleStatus,
			final byte enPassantSq,
			final short devStatus,
			final long[] pieceLocations)
	{
		this.recentHashings = recentHashings;
		this.friendlySide = friendlySide;
		this.castleRights = castleRights;
		this.castleStatus = castleStatus;
		this.enPassantSq = enPassantSq;
		this.devStatus = devStatus;
		this.pieceLocations = pieceLocations;
	}

	// @Override
	// public List<ChessMove> getMoves()
	// {
	// // TODO Auto-generated method stub
	// return null;
	// }

	// @Override
	// public ChessMove generateMove(final AlgebraicCommand com)
	// {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public long zobristHash()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ChessPiece getPieceAt(final byte loc)
	{
		for (byte index = 0; index < 12; index++)
		{
			if ((BBDB.SOB[loc] & pieceLocations[index]) != 0)
			{
				return ChessPiece.PIECES[index];
			}
		}
		return null;
	}

	@Override
	public long getPieceLocations(final int pieceIndex)
	{
		return pieceLocations[pieceIndex];
	}

	@Override
	public long getAttackedSquares(final Side side)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getCastleStatus()
	{
		return castleStatus;
	}

	@Override
	public byte getCastleRights()
	{
		return castleRights;
	}

	@Override
	public short getDevelopmentStatus()
	{
		return devStatus;
	}

	@Override
	public long getHashing()
	{
		return recentHashings[0];
	}

	@Override
	public byte getEnPassantSq()
	{
		return enPassantSq;
	}

	@Override
	public long[] getRecentHashings()
	{
		return recentHashings;
	}

	@Override
	public Side getFriendlySide()
	{
		return friendlySide == 0 ? Side.W : Side.B;
	}

	@Override
	public TerminationType getTerminationState()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSideLocations(final Side s)
	{
		long locs = 0L;
		for (byte index = s.getId(); index < s.getId() + 6; index++)
		{
			locs |= pieceLocations[index];
		}
		return locs;
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