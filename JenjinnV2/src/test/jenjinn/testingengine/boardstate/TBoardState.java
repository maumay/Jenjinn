/**
 * Copyright � 2017 Lhasa Limited
 * File created: 19 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.testingengine.boardstate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.enums.TerminationType;
import jenjinn.engine.exceptions.AmbiguousPgnException;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.openingdatabase.AlgebraicCommand;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.zobristhashing.ZobristHasher;
import jenjinn.testingengine.enums.CastleArea;
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

	private CastleArea[] castleStatus;
	
	private List<CastleArea> castleRights;
	
	private Sq enPassantSq;
	
	private long devStatus;
	
	private byte clockValue;

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
		if (getClockValue() == 50)
		{
			return TerminationType.DRAW;
		}
		
		int kId = 5 + friendlySide.otherSide().index();
		Sq kLoc = null;
		for (int i = 0; i < 64; i++)
		{
			if (board[i] != null && board[i].getIndex() == kId)
			{
				kLoc = Sq.getSq((byte) i);
				break;
			}
		}
		assert kLoc != null;
		
		long friendlyAttacks = getSquaresAttackedBy(friendlySide);
		if ((friendlyAttacks & kLoc.getAsBB()) != 0)
		{
			return friendlySide.isWhite() ? TerminationType.WHITE_WIN : TerminationType.BLACK_WIN;
		}
		
		// Check for repetition draw // TODO - Remove stream to increase performance?
		final int uniqueHashings = (int) Arrays.stream(recentHashes).distinct().count();

		assert uniqueHashings >= 2;

		if (uniqueHashings == 2 && Arrays.stream(recentHashes).filter(x -> x == recentHashes[0]).count() != 2)
		{
			return TerminationType.DRAW;
		}

		return TerminationType.NOT_TERMINAL;
	}

	@Override
	public List<ChessMove> getMoves()
	{
		// TODO Auto-generated method stub
//		List<ChessMove>
		
		return null;
	}
	
	private List<ChessMove> getCastleMoves()
	{
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
		ZobristHasher hasher = BoardState.HASHER;
		long hash = EngineUtils.multipleXor(
				IntStream.range(0, 64)
					.filter(i -> board[i] != null)
					.mapToLong(i -> hasher.getSquarePieceFeature((byte) i, board[i]))
					.toArray());
		
		if (enPassantSq != null)
		{
			hash ^= hasher.getEnpassantFeature(enPassantSq.ordinal() % 8);
		}
		
		for (CastleArea area : castleRights)
		{
			hash ^= hasher.getCastleFeature(area.hashingIndex);
		}
		
		if (!friendlySide.isWhite())
		{
			hash ^= hasher.getBlackToMove();
		}
		
		return hash;
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
		return (byte) EngineUtils.multipleOr(
				Arrays.stream(castleStatus)
				.mapToLong(x -> x.byteRep)
				.toArray());
	}

	@Override
	public byte getCastleRights()
	{
		return (byte) EngineUtils.multipleOr(
				castleRights.stream()
				.mapToLong(x -> x.byteRep)
				.toArray());
	}

	@Override
	public byte getClockValue()
	{
		return clockValue;
	}

	@Override
	public byte getPiecePhase()
	{
		int totalPhase = 4*1 + 4*1 + 4*2 + 2*4;// From chessprogramming
		final int[] pieceCounts = new int[6];
		IntStream.range(0, 64).forEach(i -> {
			if (board[i] != null)
			{
				pieceCounts[board[i].getIndex() % 6]++;
			}
		});
		return (byte) (totalPhase 
				- (pieceCounts[1] + pieceCounts[2] + pieceCounts[3]*2 + pieceCounts[4]*4));
	}

	@Override
	public long getDevelopmentStatus()
	{
		return devStatus;
	}

	@Override
	public long getHashing()
	{
		ZobristHasher hasher = BoardState.HASHER;
		long hash = EngineUtils.multipleXor(
				IntStream.range(0, 64)
					.filter(i -> board[i] != null)
					.mapToLong(i -> hasher.getSquarePieceFeature((byte) i, board[i]))
					.toArray());
		
		if (enPassantSq != null)
		{
			hash ^= hasher.getEnpassantFeature(enPassantSq.ordinal() % 8);
		}
		
		for (CastleArea area : castleRights)
		{
			hash ^= hasher.getCastleFeature(area.hashingIndex);
		}
		
		if (!friendlySide.isWhite())
		{
			hash ^= hasher.getBlackToMove();
		}
		
		return hash;
	}

	@Override
	public byte getEnPassantSq()
	{
		return (byte) enPassantSq.ordinal();
	}

	@Override
	public long[] getNewRecentHashings(final long newHash)
	{
		long[] newHashings = new long[4];
		for (int i = 0; i < 3; i++)
		{
			newHashings[i + 1] = recentHashes[i];
		}
		newHashings[0] = newHash;
		return newHashings;
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
		return (short) IntStream.range(0, 64)
				.filter(i -> board[i] != null).map(i -> 
				{
					TChessPiece p = board[i];
					return BoardState.MID_TABLE.getPieceSquareValue(p.getIndex(), (byte) i);
				}).sum();
	}

	@Override
	public short getEndgamePositionalEval()
	{
		return (short) IntStream.range(0, 64)
				.filter(i -> board[i] != null).map(i -> 
				{
					TChessPiece p = board[i];
					return BoardState.END_TABLE.getPieceSquareValue(p.getIndex(), (byte) i);
				}).sum();
	}
	
	public static void main(String[] args)
	{
		CastleArea[] cRights = {};
		
		System.out.println( (byte) EngineUtils.multipleOr(
				Arrays.stream(cRights)
				.mapToLong(x -> x.byteRep)
				.toArray()));
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