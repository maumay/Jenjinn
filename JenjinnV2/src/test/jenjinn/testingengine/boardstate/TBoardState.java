/**
 * Copyright © 2017 Lhasa Limited
 * File created: 19 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.testingengine.boardstate;

import java.util.List;
import java.util.stream.IntStream;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.TerminationType;
import jenjinn.engine.exceptions.AmbiguousPgnException;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.openingdatabase.AlgebraicCommand;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.testingengine.pieces.TChessPiece;

/**
 * @author ThomasB
 * @since 19 Sep 2017
 */
public class TBoardState implements BoardState
{
	private long[] recentHashes;

	private Side friendlySide;

	private TChessPiece[] board;

	// private byte castleStatus

	/**
	 *
	 */
	public TBoardState()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte getFriendlySideValue()
	{
		return (byte) friendlySide.ordinal();
	}

	@Override
	public Side getFriendlySide()
	{
		return friendlySide;
	}

	@Override
	public Side getEnemySide()
	{
		return friendlySide.otherSide();
	}

	@Override
	public TerminationType getTerminationState()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChessMove> getMoves()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChessMove> getAttackMoves()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChessMove generateMove(final AlgebraicCommand com) throws AmbiguousPgnException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long zobristHash()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ChessPiece getPieceAt(final byte loc)
	{
		return board[loc];
	}

	@Override
	public ChessPiece getPieceAt(final byte loc, final Side s)
	{
		return board[loc];
	}

	@Override
	public long getPieceLocations(final int pieceIndex)
	{
		return EngineUtils.multipleOr(
				IntStream.range(0, 64)
						.filter(i -> board[i].getIndex() == pieceIndex)
						.mapToLong(i -> (1L << i))
						.toArray());
	}

	@Override
	public long[] getPieceLocationsCopy()
	{
		final long[] pLocs = new long[12];
		for (int i = 0; i < 12; i++)
		{
			pLocs[i] = getPieceLocations(i);
		}
		return pLocs;
	}

	@Override
	public long getSideLocations(final Side s)
	{
		final long[] pLocs = new long[6];
		for (int i = 0; i < 6; i++)
		{
			pLocs[i] = getPieceLocations(i + s.index());
		}
		return EngineUtils.multipleOr(pLocs);
	}

	@Override
	public long getOccupiedSquares()
	{
		return EngineUtils.multipleOr(
				IntStream.range(0, 64)
						.filter(i -> board[i] != null)
						.mapToLong(i -> (1L << i))
						.toArray());
	}

	@Override
	public long getSquaresAttackedBy(final Side side)
	{
		final long occupied = getOccupiedSquares();
		return EngineUtils.multipleOr(
				IntStream.range(0, 64)
						.filter(i -> board[i].getSide() == side)
						.mapToLong(i -> board[i].getAttackset((byte) i, occupied))
						.toArray());
	}

	@Override
	public byte getCastleStatus()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getCastleRights()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getClockValue()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getPiecePhase()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getDevelopmentStatus()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getHashing()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getEnPassantSq()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long[] getNewRecentHashings(final long newHash)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void print()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void printMoves()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public short getMidgamePositionalEval()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getEndgamePositionalEval()
	{
		// TODO Auto-generated method stub
		return 0;
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