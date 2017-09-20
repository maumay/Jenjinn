/**
 * Copyright � 2017 Lhasa Limited
 * File created: 19 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.testingengine.boardstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jenjinn.engine.bitboarddatabase.BBDB;
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
import jenjinn.testingengine.moves.TCastleMove;
import jenjinn.testingengine.moves.TEnPassantMove;
import jenjinn.testingengine.moves.TPromotionMove;
import jenjinn.testingengine.moves.TStandardMove;
import jenjinn.testingengine.pieces.TChessPiece;
import jenjinn.testingengine.pieces.TKing;
import jenjinn.testingengine.pieces.TPawn;

/**
 * @author ThomasB
 * @since 19 Sep 2017
 */
public final class TBoardState implements BoardState
{
	private long[] recentHashes;

	private Side friendlySide;

	private TChessPiece[] board = new TChessPiece[64];

	private CastleArea[] castleStatus = new CastleArea[2];

	private Set<CastleArea> castleRights = EnumSet.noneOf(CastleArea.class);

	private Sq enPassantSq;

	private long devStatus;

	private byte clockValue;

	/**
	 *
	 */
	public TBoardState(final Side friendlySide,
			final long[] pieceLocations,
			final byte castleRights,
			final byte castleStatus,
			final long devStatus,
			final byte enPassantSq,
			final byte clockValue,
			final long[] oldHashings)
	{
		this.friendlySide = friendlySide;
		IntStream.range(0, 12).forEach(i ->
		{
			final long locs = pieceLocations[i];
			for (final byte loc : EngineUtils.getSetBits(locs))
			{
				board[loc] = TChessPiece.get(i);
			}
		});

		int idxCtr = 0;
		for (final CastleArea area : CastleArea.values())
		{
			if ((area.byteRep & castleRights) != 0)
			{
				this.castleRights.add(area);
			}
			if ((area.byteRep & castleStatus) != 0)
			{
				this.castleStatus[idxCtr++] = area;
			}
		}

		this.clockValue = clockValue;
		this.devStatus = devStatus;
		this.enPassantSq = Sq.get(enPassantSq);

		recentHashes = oldHashings;
		recentHashes = getNewRecentHashings(getHashing());
	}

	public static TBoardState getStartBoard()
	{
		return new TBoardState(Side.W,
				EngineUtils.getStartingPieceLocs(),
				(byte) 0b1111,
				(byte) 0,
				EngineUtils.getStartingDevStatus(),
				BoardState.NO_ENPASSANT,
				(byte) 0,
				new long[] { 1L, 2L, 3L, 0L });
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
	public final TerminationType getTerminationState()
	{
		if (getClockValue() == 50)
		{
			return TerminationType.DRAW;
		}

		final int kId = 5 + friendlySide.otherSide().index();
		Sq kLoc = null;
		for (int i = 0; i < 64; i++)
		{
			if (board[i] != null && board[i].getIndex() == kId)
			{
				kLoc = Sq.get((byte) i);
				break;
			}
		}
		assert kLoc != null;

		final long friendlyAttacks = getSquaresAttackedBy(friendlySide);
		if ((friendlyAttacks & kLoc.getAsBB()) != 0)
		{
			return friendlySide.isWhite() ? TerminationType.WHITE_WIN : TerminationType.BLACK_WIN;
		}

		final int uniqueHashings = (int) Arrays.stream(recentHashes).distinct().count();

		assert uniqueHashings >= 2;

		if (uniqueHashings == 2 && Arrays.stream(recentHashes).filter(x -> x == recentHashes[0]).count() != 2)
		{
			return TerminationType.DRAW;
		}

		return TerminationType.NOT_TERMINAL;
	}

	@Override
	public final List<ChessMove> getMoves()
	{
		final List<ChessMove> mvs = new ArrayList<>();
		mvs.addAll(getCastleMoves());

		final long friendly = getSideLocations(friendlySide), enemy = getSideLocations(getEnemySide());

		IntStream.range(0, 64).forEach(i ->
		{
			final TChessPiece p = board[i];
			if (p instanceof TPawn && p.getSide() == friendlySide)
			{
				long mvSquares = p.getMoveset((byte) i, friendly, enemy);
				final long attackSqs = p.getAttackset((byte) i, friendly | enemy);
				final long backRankMvSqs = mvSquares & (friendlySide.isWhite() ? BBDB.RNK[7] : BBDB.RNK[0]);
				mvSquares &= ~backRankMvSqs;

				for (final byte targ : EngineUtils.getSetBits(mvSquares))
				{
					mvs.add(TStandardMove.get(i, targ));
				}
				for (final byte targ : EngineUtils.getSetBits(backRankMvSqs))
				{
					mvs.add(new TPromotionMove(i, targ));
				}

				if (enPassantSq != null && (attackSqs & enPassantSq.getAsBB()) != 0)
				{
					mvs.add(new TEnPassantMove(i, enPassantSq.ordinal()));
				}
			}
			else if (p != null && p.getSide() == friendlySide)
			{
				final long mvSquares = p.getMoveset((byte) i, friendly, enemy);
				for (final byte targ : EngineUtils.getSetBits(mvSquares))
				{
					mvs.add(TStandardMove.get(i, targ));
				}
			}
		});

		return mvs;
	}

	public final List<ChessMove> getCastleMoves()
	{
		final List<ChessMove> cmvs = new ArrayList<>();

		final byte sideShift = (byte) (friendlySide.isWhite() ? 0 : 56);
		final long enemyAttacks = getSquaresAttackedBy(getEnemySide()), occupied = getOccupiedSquares();

		final CastleArea kSideArea = CastleArea.getKingside(friendlySide), qSideArea = CastleArea.getQueenside(friendlySide);

		if (castleRights.contains(kSideArea))
		{
			final List<Sq> kSide = Arrays.asList(Sq.get(1 + sideShift), Sq.get(2 + sideShift), Sq.get(3 + sideShift));
			boolean allowed = true;
			for (int i = 0; i < 3; i++)
			{
				final long sqBB = kSide.get(i).getAsBB();

				if ((i < 2 && ((sqBB & occupied) != 0)) || (sqBB & enemyAttacks) != 0)
				{
					allowed = false;
					break;
				}
			}

			if (allowed)
			{
				cmvs.add(TCastleMove.get(kSideArea));
			}
		}
		if (castleRights.contains(qSideArea))
		{
			final List<Sq> qSide = Arrays.asList(Sq.get(6 + sideShift), Sq.get(5 + sideShift), Sq.get(4 + sideShift), Sq.get(3 + sideShift));
			boolean allowed = true;
			for (int i = 0; i < 3; i++)
			{
				final long sqBB = qSide.get(i).getAsBB();

				if ((i < 3 && ((sqBB & occupied) != 0)) || (sqBB & enemyAttacks) != 0)
				{
					allowed = false;
					break;
				}
			}

			if (allowed)
			{
				cmvs.add(TCastleMove.get(qSideArea));
			}
		}
		return cmvs;
	}

	@Override
	public final List<ChessMove> getAttackMoves()
	{
		return getMoves().stream().filter(x -> board[x.getTarget()] != null).collect(Collectors.toList());
	}

	@Override
	public ChessPiece getPieceAt(final byte loc)
	{
		return board[loc];
	}

	@Override
	public ChessPiece getPieceAt(final byte loc, final Side s)
	{
		TChessPiece p = board[loc];
		return (p != null && p.getSide() == s) ? p : null;
	}

	@Override
	public long getPieceLocations(final int pieceIndex)
	{
		return EngineUtils.multipleOr(
				IntStream.range(0, 64)
						.filter(i -> board[i] != null && board[i].getIndex() == pieceIndex)
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
						.filter(i -> board[i] != null && board[i].getSide() == side)
						.mapToLong(i -> board[i].getAttackset((byte) i, occupied))
						.toArray());
	}

	@Override
	public byte getCastleStatus()
	{
		return (byte) EngineUtils.multipleOr(
				Arrays.stream(castleStatus)
						.filter(x -> x != null)
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
		final int totalPhase = 24;// From chessprogramming
		final int[] pieceCounts = new int[6];
		IntStream.range(0, 64).forEach(i ->
		{
			if (board[i] != null)
			{
				pieceCounts[board[i].getIndex() % 6]++;
			}
		});
		return (byte) (totalPhase
				- (pieceCounts[1] + pieceCounts[2] + pieceCounts[3] * 2 + pieceCounts[4] * 4));
	}

	@Override
	public long getDevelopmentStatus()
	{
		return devStatus;
	}

	@Override
	public long getHashing()
	{
		final ZobristHasher hasher = BoardState.HASHER;
		long hash = EngineUtils.multipleXor(
				IntStream.range(0, 64)
						.filter(i -> board[i] != null)
						.mapToLong(i -> hasher.getSquarePieceFeature((byte) i, board[i]))
						.toArray());

		if (enPassantSq != null)
		{
			hash ^= hasher.getEnpassantFeature(enPassantSq.ordinal() % 8);
		}

		for (final CastleArea area : castleRights)
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
		return enPassantSq == null ? BoardState.NO_ENPASSANT : (byte) enPassantSq.ordinal();
	}

	@Override
	public long[] getNewRecentHashings(final long newHash)
	{
		final long[] newHashings = new long[4];
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
					final TChessPiece p = board[i];
					return BoardState.MID_TABLE.getPieceSquareValue(p.getIndex(), (byte) i);
				}).sum();
	}

	@Override
	public short getEndgamePositionalEval()
	{
		return (short) IntStream.range(0, 64)
				.filter(i -> board[i] != null).map(i ->
				{
					final TChessPiece p = board[i];
					return BoardState.END_TABLE.getPieceSquareValue(p.getIndex(), (byte) i);
				}).sum();
	}

	@Override
	public ChessMove generateMove(final AlgebraicCommand com) throws AmbiguousPgnException
	{
		if (com.isPromotionOrder())
		{
			throw new RuntimeException("Not yet implemented.");
		}

		final String castleOrder = com.getCastleOrder();

		if (castleOrder != null)
		{
			return TCastleMove.get(getFriendlySide().isWhite() ? "WHITE" + castleOrder : "BLACK" + castleOrder);
		}

		final Sq targSq = com.getTargetSq();
		final byte targ = (byte) com.getTargetSq().ordinal();

		if (com.isAttackOrder() && getPieceAt(targ, getEnemySide()) == null)
		{
			final int startFile = com.getStartFile();
			final Sq start = Sq.getSq(startFile, targ / 8 - getFriendlySide().orientation());
			return new TEnPassantMove(start.ordinal(), targSq.ordinal());
		}

		final int startRnk = com.getStartRow();
		final int startFle = com.getStartFile();

		final List<TStandardMove> possibleStandardMoves = getMoves().stream()
				.filter(x -> x instanceof TStandardMove)
				.map(x -> (TStandardMove) x)
				.collect(Collectors.toList());

		final List<TStandardMove> possibleMoves = new ArrayList<>();

		for (final TStandardMove mv : possibleStandardMoves)
		{
			final ChessPiece p = getPieceAt(mv.getStart(), getFriendlySide());
			if (p == null)
			{
				System.out.println();
				System.out.println(getFriendlySide().name());
				System.out.println(mv.toString());
				throw new RuntimeException();
			}
			if (mv.getTarget() == targ && p.getPieceType() == com.getPieceToMove())
			{
				if (startRnk < 0 && startFle < 0)
				{
					possibleMoves.add(mv);
				}
				else if (startFle >= 0 && startRnk < 0)
				{
					if (startFle == (7 - mv.getStart() % 8))
					{
						possibleMoves.add(mv);
					}
				}
				else if (startRnk >= 0 && startFle < 0)
				{
					if (startRnk == mv.getStart() / 8)
					{
						possibleMoves.add(mv);
					}
				}
				else if (startFle == (7 - mv.getStart() % 8) && startRnk == mv.getStart() / 8)
				{
					possibleMoves.add(mv);
				}
			}
		}
		if (possibleMoves.isEmpty() || possibleMoves.size() > 2)
		{
			System.out.println(com.getAsString());
			System.out.println(getFriendlySide().name());
			throw new AssertionError("Not found a move correctly.");
		}
		else if (possibleMoves.size() == 2)
		{
			final BitSet pinned = new BitSet();
			for (final int i : new int[] { 1, 2 })
			{
				final byte startLoc = possibleMoves.get(i).getStart();
				final TChessPiece p = (TChessPiece) getPieceAt(startLoc, getFriendlySide());
				assert p != null;

				board[startLoc] = null;
				if ((getSquaresAttackedBy(getEnemySide()) & getKingLoc(friendlySide)) != 0)
				{
					pinned.set(i);
				}
				board[startLoc] = p;
			}
			final int pCard = pinned.cardinality();
			if (pCard == 0 || pCard == 2)
			{
				throw new AmbiguousPgnException();
			}
			else
			{
				return possibleMoves.get(pinned.nextClearBit(0));
			}
		}
		else
		{
			return possibleMoves.get(0);
		}
	}

	private long getKingLoc(final Side s)
	{
		for (int i = 0; i < 64; i++)
		{
			if (board[i] instanceof TKing && board[i].getSide() == s)
			{
				return (1L << i);
			}
		}
		throw new AssertionError("No king");
	}

	public static void main(final String[] args)
	{
		final TBoardState s = getStartBoard();
		EngineUtils.printNbitBoards(s.getPieceLocationsCopy());
		System.out.println();

		// s = TStandardMove.get(Sq.d2.ordinal(), Sq.d4.ordinal()).evolve(s);
		//
		// EngineUtils.printNbitBoards(s.getSideLocations(Side.B), s.getSideLocations(Side.W));
		// System.out.println(s.getPieceAt((byte) 57).getPieceType());
		// final long kat = s.getPieceAt((byte) 57).getAttackset((byte) 57, s.getSideLocations(Side.B) | s.getSideLocations(Side.W));
		// final long kMoves = s.getPieceAt((byte) 57).getMoveset((byte) 57, s.getSideLocations(Side.B), s.getSideLocations(Side.W));
		// EngineUtils.printNbitBoards(kMoves, kat);
		// System.out.println();
		EngineUtils.printNbitBoards(EngineUtils.getStartingPieceLocs());
		// for (final ChessMove mv : s.getMoves())
		// {
		// System.out.println(mv.toString());
		// }
		System.out.println();
		System.out.println(s.board[1]);
		
		System.out.println(s.getHashing() == BoardState.HASHER.generateStartHash());
	}

	@Override
	public long[] getHashes()
	{
		return Arrays.copyOf(recentHashes, recentHashes.length);
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