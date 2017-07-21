/**
 * Copyright © 2017 Lhasa Limited
 * File created: 19 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.boardstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.TerminationType;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.moves.CastleMove;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.EnPassantMove;
import jenjinn.engine.moves.PromotionMove;
import jenjinn.engine.moves.StandardMove;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 * @since 19 Jul 2017
 */
public class BoardStateImplV2 implements BoardState
{
	/**
	 * We store each of these properties in the spare 32 bits of space of the
	 * pawn bitboards (since pawns can't be on first and last rank).
	 */
	// Stored in white pawn bb
	private static final long ENPASSANT_SQUARE_GETTER = 0b11111111;

	private static final long CASTLE_RIGHTS_GETTER = 0b11110000L << 56;

	private static final long FRIENDLY_SIDE_GETTER = 1L << 56;

	// Stored in black pawn bb
	private static final long HALFMOVE_CLOCK_GETTER = 0b11111111L << 56;

	private static final long CASTLE_STATUS_GETTER = 0b1111L;

	/**
	 * An array of four most recent board hashings (including the
	 * hash of this state) for determining repetition draws.
	 */
	private final long[] recentHashings;

	/**
	 * Representation of the side to move, i.e the friendly side.
	 * 0 for white and 1 for black.
	 */
	private final long devStatus; // For simplicity use a long

	/**
	 * Think its worth keeping this stored, we need to calculate it
	 * for a states terminal status anyway and it allows us to remove
	 * illegal king moves during move retrieval which could save
	 * significant time during searching. TODO actually for termination status we need all friendly attacks too
	 *
	 * So if we keep all friendly attacks then we basically achieve the same thing because illegal king moves
	 * we induce a terminal boardstate straight away and save on searching, although why store it then? It
	 * will just be used once.
	 */
	// private final long friendlyAttacks;

	private final long[] pieceLocations;

	public BoardStateImplV2(final long[] recentHashings,
			final byte friendlySide,
			final byte castleRights,
			final byte castleStatus,
			final byte enPassantSq,
			final byte halfMoveClock,
			final long devStatus,
			final long[] pieceLocations)
	{
		this.recentHashings = recentHashings;
		this.devStatus = devStatus;
		this.pieceLocations = pieceLocations;

		pieceLocations[0] |= (enPassantSq | ((long) castleRights << 60) | ((long) friendlySide << 56));
		pieceLocations[6] |= (castleStatus | ((long) halfMoveClock << 56));
	}

	@Override
	public List<ChessMove> getMoves()
	{
		final Side friendlySide = getFriendlySide();
		final long friendlyPieces = getSideLocations(friendlySide);
		final long enemyPieces = getSideLocations(friendlySide.otherSide());

		final List<ChessMove> moves = new ArrayList<>(getCastleMoves(enemyPieces | friendlyPieces));

		final byte upperBound = (byte) ((1 + getFriendlySideValue()) * 6 - 1), lowerBound = (byte) (getFriendlySideValue() * 6);

		for (byte i = upperBound; i > lowerBound; i--) // Get the most valuable piece moves first
		{
			final ChessPiece p = ChessPiece.get(i);
			final byte[] locs = EngineUtils.getSetBits(pieceLocations[i]);

			for (final byte loc : locs)
			{
				final long mvset = p.getMoveset(loc, friendlyPieces, enemyPieces);
				addStandardMoves(moves, loc, mvset);
			}
		}

		// Add Pawn moves.
		final ChessPiece p = ChessPiece.get(lowerBound); // Pawn
		final byte[] locs = EngineUtils.getSetBits(pieceLocations[lowerBound]);
		if (getEnPassantSq() != BoardState.NO_ENPASSANT)
		{
			for (final byte loc : locs)
			{
				final long mvset = p.getMoveset(loc, friendlyPieces, enemyPieces);
				addPawnStandardAndPromotionMoves(moves, loc, mvset);
				if (((p.getAttackset(loc, enemyPieces | friendlyPieces) & (1L << getEnPassantSq())) != 0))
				{
					moves.add(EnPassantMove.get(loc, getEnPassantSq()));
				}
			}
		}
		else
		{
			for (final byte loc : locs)
			{
				final long mvset = p.getMoveset(loc, friendlyPieces, enemyPieces);
				addPawnStandardAndPromotionMoves(moves, loc, mvset);
			}
		}

		return moves;
	}

	/**
	 * For non pawns!
	 *
	 * @param moves
	 * @param loc
	 * @param mvset
	 */
	private void addStandardMoves(final List<ChessMove> moves, final byte loc, final long mvset)
	{
		final byte[] targets = EngineUtils.getSetBits(mvset);
		for (final byte target : targets)
		{
			moves.add(StandardMove.get(loc, target));
		}
	}

	/**
	 * For pawns!
	 *
	 * @param moves
	 * @param loc
	 * @param mvset
	 */
	private void addPawnStandardAndPromotionMoves(final List<ChessMove> moves, final byte loc, long mvset)
	{
		final long backRank = 0b11111111L << (getFriendlySideValue() * 56), backRankMvs = mvset & backRank;
		mvset &= ~backRank;

		addStandardMoves(moves, loc, mvset);

		final byte[] backRankTargets = EngineUtils.getSetBits(backRankMvs);
		for (final byte target : backRankTargets)
		{
			moves.add(PromotionMove.get(loc, target));
		}
	}

	private List<CastleMove> getCastleMoves(final long allPieces)
	{
		final List<CastleMove> cmvs = new ArrayList<>(2);

		// for rights and status retrieval
		final byte sideShift = (byte) (getFriendlySideValue() * 2);

		// If we have not already castled
		if ((getCastleStatus() & (0b11 << sideShift)) == 0)
		{
			final boolean hasKsideRights = (getCastleRights() & (0b1 << (sideShift))) != 0;
			final boolean hasQsideRights = (getCastleRights() & (0b10 << (sideShift))) != 0;

			if (hasKsideRights)
			{
				// if squares are clear
				if (((0b110L << (getFriendlySideValue() * 56)) & allPieces) == 0)
				{
					// if squares are not attacked
					if (((0b1110L << (getFriendlySideValue() * 56)) & getAttackedSquares(getEnemySide())) == 0)
					{
						cmvs.add(getFriendlySideValue() == 0 ? CastleMove.WHITE_KINGSIDE : CastleMove.BLACK_KINGSIDE);
					}
				}
			}
			if (hasQsideRights)
			{
				// if squares are clear
				if (((0b1110000L << (getFriendlySideValue() * 56)) & allPieces) == 0)
				{
					// if squares are not attacked
					if (((0b1110000L << (getFriendlySideValue() * 56)) & getAttackedSquares(getEnemySide())) == 0)
					{
						cmvs.add(getFriendlySideValue() == 0 ? CastleMove.WHITE_QUEENSIDE : CastleMove.BLACK_QUEENSIDE);
					}
				}
			}
		}
		return cmvs;
	}

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
	public ChessPiece getPieceAt(final byte loc, final Side s)
	{
		final byte upperBound = (byte) (s.index() + 6);

		for (byte index = s.index(); index < upperBound; index++)
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
		// TODO - Could perform optimisation on pawn attacks
		final long occupiedSquares = getOccupiedSquares();
		long attackedSquares = 0L;

		for (byte i = side.index(); i < side.index() + 6; i++)
		{
			final byte[] locs = EngineUtils.getSetBits(pieceLocations[i]);
			final ChessPiece p = ChessPiece.get(i);

			for (final byte loc : locs)
			{
				attackedSquares |= p.getAttackset(loc, occupiedSquares);
			}
		}
		return attackedSquares;
	}

	@Override
	public byte getCastleStatus()
	{
		return (byte) (pieceLocations[6] & CASTLE_STATUS_GETTER);
	}

	@Override
	public byte getCastleRights()
	{
		return (byte) ((pieceLocations[0] & CASTLE_RIGHTS_GETTER) >>> 60);
	}

	@Override
	public long getDevelopmentStatus()
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
		return (byte) (pieceLocations[0] & ENPASSANT_SQUARE_GETTER);
	}

	@Override
	public byte getClockValue()
	{
		return (byte) ((pieceLocations[6] & HALFMOVE_CLOCK_GETTER) >>> 56);
	}

	@Override
	public long[] getNewRecentHashings(final long newHash)
	{
		final long[] newRecentHashings = { newHash, 0L, 0L, 0L };
		System.arraycopy(recentHashings, 0, newRecentHashings, 1, 3);
		return newRecentHashings;
	}

	@Override
	public Side getFriendlySide()
	{
		return getFriendlySideValue() == 0 ? Side.W : Side.B;
	}

	@Override
	public Side getEnemySide()
	{
		return getFriendlySideValue() == 0 ? Side.B : Side.W;
	}

	@Override
	public TerminationType getTerminationState()
	{
		// First check for taking of king
		final Side friendlySide = getFriendlySide();

		if ((getAttackedSquares(friendlySide) & pieceLocations[friendlySide.otherSide().index() + 6]) != 0)
		{
			return friendlySide == Side.W ? TerminationType.WHITE_WIN : TerminationType.BLACK_WIN;
		}

		// Check for repetition draw // TODO - Remove stream to increase performance?
		final int uniqueHashings = (int) Arrays.stream(recentHashings).distinct().count();

		assert uniqueHashings >= 2;

		if (uniqueHashings == 2 && Arrays.stream(recentHashings).filter(x -> x == recentHashings[0]).count() != 2)
		{
			return TerminationType.DRAW;
		}

		return TerminationType.NOT_TERMINAL;
	}

	@Override
	public long getSideLocations(final Side s)
	{
		long locs = 0L;
		for (byte index = s.index(); index < s.index() + 6; index++)
		{
			locs |= pieceLocations[index];
		}
		return locs;
	}

	@Override
	public long getOccupiedSquares()
	{
		return EngineUtils.multipleOr(pieceLocations);
	}

	@Override
	public byte getFriendlySideValue()
	{
		return (byte) (pieceLocations[0] & FRIENDLY_SIDE_GETTER >>> 56);
	}

	@Override
	public long[] getPieceLocationsCopy()
	{
		final long[] copy = new long[12];
		System.arraycopy(pieceLocations, 0, copy, 0, 12);
		return copy;
	}

	public static BoardState getStartBoard()
	{
		final long startHash = BoardState.HASHER.generateStartHash();

		return new BoardStateImplV2(
				new long[] { startHash, 0L, 0L, 0L },
				(byte) 0,
				(byte) 0b1111,
				(byte) 0,
				BoardState.NO_ENPASSANT,
				(byte) 0,
				getStartingDevStatus(),
				getStartingPieceLocs());
	}

	private static long getStartingDevStatus()
	{
		throw new RuntimeException("Not yet impl");
	}

	private static long[] getStartingPieceLocs()
	{
		final long[] start = new long[12];

		for (int i = 0; i < 12; i++)
		{
			start[i] = ChessPiece.get(i).getStartBitboard();
		}

		return start;
	}

	public static void main(final String[] args)
	{
		EngineUtils.printNbitBoards(getStartBoard().getPieceLocationsCopy());// ((long) 0b10) << 2);
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