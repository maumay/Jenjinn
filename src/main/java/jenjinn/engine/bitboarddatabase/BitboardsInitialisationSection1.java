package jenjinn.engine.bitboarddatabase;

import static jenjinn.engine.misc.EngineUtils.multipleOr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.misc.PieceMovementDirectionArrays;

/**
 * First of three utility classes containing only static methods to initialise
 * the constants in the BBDB class. We generate the basic building blocks and
 * then the move and attack sets of all piece types on an empty board.
 *
 * @author TB
 * @date 21 Jan 2017
 */
public class BitboardsInitialisationSection1
{

	public static long[] generateSingleOccupancyBitboards()
	{
		final long[] ans = new long[64];
		for (int i = 0; i < 64; i++) {
			ans[i] = (1L << i);
		}
		return ans;
	}

	public static long[] generateRankBitboards()
	{
		final long[] ans = new long[8];
		final Direction west = Direction.W;
		for (byte i = 0; i < 8; i++) {
			final Sq start = Sq.get( (8 * i));
			final Sq[] allConstituents = start.getAllSqInDirection(west, true);
			ans[i] = multipleOr(allConstituents);
		}
		return ans;
	}

	public static long[] generateFileBitboards()
	{
		final long[] ans = new long[8];
		final Direction north = Direction.N;
		for (byte i = 0; i < 8; i++) {
			final Sq start = Sq.get( (7 - i));
			final Sq[] allConstituents = start.getAllSqInDirection(north, true);
			ans[i] = multipleOr(allConstituents);
		}
		return ans;
	}

	public static long[] generateDiagonalBitboards()
	{
		final long[] ans = new long[15];
		final Direction nEast = Direction.NE;
		for (byte i = 0; i < 15; i++) {
			final Sq start = (i < 8) ? Sq.get(i) : Sq.getSq( 0,  (i - 7));
			final Sq[] allConstituents = start.getAllSqInDirection(nEast, true);
			ans[i] = multipleOr(allConstituents);
		}
		return ans;
	}

	public static long[] generateAntidiagonalBitboards()
	{
		final long[] ans = new long[15];
		final Direction nWest = Direction.NW;
		for (byte i = 0; i < 15; i++) {
			final Sq start = (i < 8) ? Sq.get( (7 - i)) : Sq.getSq( 7,  (i - 7));
			final Sq[] allConstituents = start.getAllSqInDirection(nWest, true);
			ans[i] = multipleOr(allConstituents);
		}
		return ans;
	}

	public static long[][] generateAllEmptyBoardPieceMovementBitboards()
	{
		final long[][] ans = new long[7][];
		for (int i = 0; i < 7; i++) {
			ans[i] = generateMoves(i, false);
		}
		return ans;
	}

	public static long[][] generateAllEmptyBoardPieceAttackBitboards()
	{
		final long[][] ans = new long[7][];
		for (int i = 0; i < 7; i++) {
			ans[i] = generateMoves(i, true);
		}
		return ans;
	}

	private static long[] generateMoves(final int i, final boolean isAttackset)
	{
		long[] ans = new long[64];

		if (i == 0) {
			ans = generateEmptyBoardPawnBitboards(true, isAttackset);
		}
		if (i == 1) {
			ans = generateEmptyBoardPawnBitboards(false, isAttackset);
		}
		if (i == 2) {
			ans = generateEmptyBoardMinorPieceBitboards(true);
		}
		if (i == 3) {
			ans = generateEmptyBoardMinorPieceBitboards(false);
		}
		if (i == 4) {
			ans = generateEmptyBoardMajorPieceBitboards(true);
		}
		if (i == 5) {
			ans = generateEmptyBoardMajorPieceBitboards(false);
		}
		if (i == 6) {
			ans = generateEmptyBoardKingBitboards();
		}
		return ans;
	}

	private static long[] generateEmptyBoardKingBitboards()
	{
		final long[] ans = new long[64];
		final Direction[] movementDirections = PieceMovementDirectionArrays.KD;
		for (final Sq startSq : Sq.values()) {
			final List<Sq> possMoveSqs = new ArrayList<>();
			for (final Direction dir : movementDirections) {
				final Sq[] nextSqs = startSq.getAllSqInDirection(dir, false,  1);
				possMoveSqs.addAll(Arrays.asList(nextSqs));
			}
			ans[startSq.ordinal()] = multipleOr(possMoveSqs.toArray(new Sq[0]));
		}
		return ans;
	}

	private static long[] generateEmptyBoardMajorPieceBitboards(final boolean isRook)
	{
		final long[] ans = new long[64];
		final Direction[] movementDirections = isRook ? PieceMovementDirectionArrays.RD : PieceMovementDirectionArrays.QD;
		for (final Sq startSq : Sq.values()) {
			final List<Sq> possMoveSqs = new ArrayList<>();
			for (final Direction dir : movementDirections) {
				final Sq[] nextSqs = startSq.getAllSqInDirection(dir, false);
				possMoveSqs.addAll(Arrays.asList(nextSqs));
			}
			ans[startSq.ordinal()] = multipleOr(possMoveSqs.toArray(new Sq[0]));
		}
		return ans;
	}

	private static long[] generateEmptyBoardMinorPieceBitboards(final boolean isBishop)
	{
		final long[] ans = new long[64];
		final Direction[] movementDirections = isBishop ? PieceMovementDirectionArrays.BD : PieceMovementDirectionArrays.ND;
		for (final Sq startSq : Sq.values()) {
			final List<Sq> possMoveSqs = new ArrayList<>();
			for (final Direction dir : movementDirections) {
				if (isBishop) {
					final Sq[] nextSqs = startSq.getAllSqInDirection(dir, false);
					possMoveSqs.addAll(Arrays.asList(nextSqs));
				}
				else {
					final Sq[] nextSqs = startSq.getAllSqInDirection(dir, false,  1);
					possMoveSqs.addAll(Arrays.asList(nextSqs));
				}
			}
			ans[startSq.ordinal()] = multipleOr(possMoveSqs.toArray(new Sq[0]));
		}
		return ans;
	}

	private static long[] generateEmptyBoardPawnBitboards(final boolean isWhite, final boolean isAttackset)
	{
		final long[] ans = new long[64];
		final long startRank = isWhite ? Bitboards.RNK[1] : Bitboards.RNK[6];

		Direction[] movementDirections = null;
		if (isWhite) {
			movementDirections = isAttackset ? PieceMovementDirectionArrays.WPA : PieceMovementDirectionArrays.WPM;
		}
		else {
			movementDirections = isAttackset ? PieceMovementDirectionArrays.BPA : PieceMovementDirectionArrays.BPM;
		}

		for (final Sq startSq : Sq.values()) {
			final List<Sq> possMoveSqs = new ArrayList<>();
			for (final Direction dir : movementDirections) {
				// This is the case of the pawns first move
				if (!isAttackset && (startSq.getAsBB() & startRank) != 0) {
					final Sq[] nextSqs = startSq.getAllSqInDirection(dir, false,  2);
					possMoveSqs.addAll(Arrays.asList(nextSqs));
				}
				else {
					final Sq[] nextSqs = startSq.getAllSqInDirection(dir, false,  1);
					possMoveSqs.addAll(Arrays.asList(nextSqs));
				}
			}
			ans[startSq.ordinal()] = multipleOr(possMoveSqs.toArray(new Sq[0]));
		}
		return ans;
	}
}
