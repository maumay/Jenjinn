/**
 * Copyright � 2017 Lhasa Limited
 * File created: 20 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.moves;

import java.util.Arrays;
import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.CastlingRights;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.PieceType;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public interface ChessMove
{
	String SEPARATOR = "_";

	/** Don't modify this obviously */
	byte[] PIECE_PHASES = { 0, 1, 1, 2, 4, 0 };

	BoardState evolve(BoardState state);

	byte getTarget();

	byte getStart();

	MoveType getType();

	long getTargetBB();

	boolean matches(final Sq start, final Sq target);

	boolean matchesStart(final Sq start);

	default String toCompactString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getType().id);
		sb.append(ChessMove.SEPARATOR);
		sb.append(getStart());
		sb.append(ChessMove.SEPARATOR);
		sb.append(getTarget());
		return sb.toString();
	}

	static ChessMove fromCompactString(final String reportString)
	{
		final List<String> components = Arrays.asList(reportString.split(SEPARATOR));
		final MoveType mt = MoveType.getFromId(Integer.parseInt(components.get(0)));

		if (mt == MoveType.CASTLE) {
			assert components.size() == 2;
			return CastleMove.get(Integer.parseInt(components.get(1)));
		}

		final int start = Integer.parseInt(components.get(1)), targ = Integer.parseInt(components.get(2));

		switch (mt) {
		case STANDARD:
			assert components.size() == 3;
			return StandardMove.get(start, targ);
		case ENPASSANT:
			assert components.size() == 3;
			return EnPassantMove.get(start, targ);
		case PROMOTION:
			assert components.size() == 4;
			final PieceType toPromoteTo = PieceType.valueOf(components.get(3));
			return PromotionMove.get(start, targ, toPromoteTo);
		default:
			throw new RuntimeException("Not yet impl");
		}
	}

	static ChessMove fromCompactString2(final String reportString)
	{
		final List<String> components = Arrays.asList(reportString.split(SEPARATOR));
		final MoveType mt = MoveType.getFromId(Integer.parseInt(components.get(0)));

		if (mt == MoveType.CASTLE) {
			assert components.size() == 2;
			return CastleMove.get(Integer.parseInt(components.get(1)));
		}

		final Sq start = Sq.valueOf(components.get(1)), targ = Sq.valueOf(components.get(2));

		switch (mt) {
		case STANDARD:
			assert components.size() == 3;
			return StandardMove.get(start, targ);
		case ENPASSANT:
			assert components.size() == 3;
			return EnPassantMove.get(start, targ);
		case PROMOTION:
			assert components.size() == 4;
			final PieceType toPromoteTo = PieceType.valueOf(components.get(3));
			return PromotionMove.get(start, targ, toPromoteTo);
		default:
			throw new RuntimeException("Not yet impl");
		}
	}

	default byte updatePiecePhase(final byte oldPhase, final ChessPiece removedPiece)
	{
		return (byte) (oldPhase + PIECE_PHASES[removedPiece.index() % 6]);
	}

	default long updateGeneralHashFeatures(final BoardState oldState, final byte newCastleRights, final byte newEnPassantSquare)
	{
		long newHashing = oldState.getHashing() ^ BoardState.HASHER.getBlackToMove();

		// Can't gain castling rights, can only lose them.
		final byte castleRightsChange = (byte) (oldState.getCastleRights() & ~newCastleRights);
		if (castleRightsChange > 0) {
			for (int i = 0; i < 4; i++) {
				if ((CastlingRights.VALUES[i] & castleRightsChange) > 0) {
					newHashing ^= BoardState.HASHER.getCastleFeature(i);
				}
			}
		}

		if (oldState.getEnPassantSq() != BoardState.NO_ENPASSANT) {
			newHashing ^= BoardState.HASHER.getEnpassantFeature(oldState.getEnPassantSq() % 8);
		}
		if (newEnPassantSquare != BoardState.NO_ENPASSANT) {
			newHashing ^= BoardState.HASHER.getEnpassantFeature(newEnPassantSquare % 8);
		}

		return newHashing;
	}
}

/*
 * ---------------------------------------------------------------------* This
 * software is the confidential and proprietary information of Lhasa Limited
 * Granary Wharf House, 2 Canal Wharf, Leeds, LS11 5PS --- No part of this
 * confidential information shall be disclosed and it shall be used only in
 * accordance with the terms of a written license agreement entered into by
 * holder of the information with LHASA Ltd.
 * ---------------------------------------------------------------------
 */