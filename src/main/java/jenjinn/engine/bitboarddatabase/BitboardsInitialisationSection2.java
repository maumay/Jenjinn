package jenjinn.engine.bitboarddatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.misc.PieceMovementDirectionArrays;

/**
 * Second of three utility classes containing only static methods to initialise
 * the constants in the BBDB class. This class initialises everything we need to
 * generate the magic move databases for sliding pieces (and for the pawn first
 * moves).
 * 
 * @author TB
 * @date 23 Jan 2017
 */
public class BitboardsInitialisationSection2
{
	public static long[][] generateAllBishopOccupancyVariations()
	{
		return iterateOverSquaresAndCalculateOccupancyVariations(false);
	}

	public static long[][] generateAllRookOccupancyVariations()
	{
		return iterateOverSquaresAndCalculateOccupancyVariations(true);
	}

	private static long[][] iterateOverSquaresAndCalculateOccupancyVariations(final boolean isRook)
	{
		final long[][] ans = new long[64][];
		for (Sq sq : Sq.values()) {
			ans[sq.ordinal()] = calcSingleOccVar(sq, isRook);
		}
		return ans;
	}

	public static long[] calcSingleOccVar(final Sq startSq, final boolean isRook)
	{
		final List<Sq> relevantSquares = new ArrayList<>();
		final Direction[] movementDirections = isRook ? PieceMovementDirectionArrays.RD : PieceMovementDirectionArrays.BD;

		for (Direction dir : movementDirections) {
			final byte numOfSqsLeft = startSq.numberOfSquaresLeftInDirection(dir);
			relevantSquares.addAll(Arrays.asList(startSq.getAllSqInDirection(dir, false, (byte) (numOfSqsLeft - 1))));
		}
		final long[] relevantSqAsBb = new long[relevantSquares.size()];
		for (int i = 0; i < relevantSquares.size(); i++) {
			relevantSqAsBb[i] = relevantSquares.get(i).getAsBB();
		}

		return EngineUtils.findAllPossibleOrCombos(relevantSqAsBb);
	}

	public static long[] generateRookOccupancyMasks()
	{
		return generateOccupancyMasks(true);
	}

	public static long[] generateBishopOccupancyMasks()
	{
		return generateOccupancyMasks(false);
	}

	private static long[] generateOccupancyMasks(final boolean isRook)
	{
		final long[] ans = new long[64];
		final long[][] allOccVars = isRook ? Bitboards.ROV : Bitboards.BOV;

		for (byte i = 0; i < 64; i++) {
			long[] occVars = allOccVars[i];
			ans[i] = occVars[occVars.length - 1];
		}

		return ans;
	}

	public static byte[] generateRookMagicBitshifts()
	{
		return generateMagicBitshifts(true);
	}

	public static byte[] generateBishopMagicBitshifts()
	{
		return generateMagicBitshifts(false);
	}

	private static byte[] generateMagicBitshifts(final boolean isRook)
	{
		byte[] ans = new byte[64];
		long[] occMasks = isRook ? Bitboards.ROM : Bitboards.BOM;

		for (int i = 0; i < 64; i++) {
			ans[i] = (byte) (64 - Long.bitCount(occMasks[i]));
		}

		return ans;
	}

	// Now we do pawn first move stuff

	public static long[] generateWhitePawnFirstMoveOccupancyMasks()
	{
		return generatePawnFirstMoveOccMasks(true);
	}

	public static long[] generateBlackPawnFirstMoveOccupancyMasks()
	{
		return generatePawnFirstMoveOccMasks(false);
	}

	private static long[] generatePawnFirstMoveOccMasks(final boolean isWhite)
	{
		final long[] ans = new long[8];
		final long[] moves = isWhite ? Bitboards.EBM[0] : Bitboards.EBM[1];

		final byte shiftFactor = (byte) (isWhite ? 8 : 48);

		for (byte i = 0; i < 8; i++) {
			ans[i] = moves[shiftFactor + i];
		}

		return ans;
	}

	public static long[][] generateWhitePawnFirstMoveOccupancyVariations()
	{
		return generatePawnFirstMoveOccVars(true);
	}

	public static long[][] generateBlackPawnFirstMoveOccupancyVariations()
	{
		return generatePawnFirstMoveOccVars(false);
	}

	private static long[][] generatePawnFirstMoveOccVars(final boolean isWhite)
	{
		final long[][] ans = new long[8][];
		final Direction movementDirection = isWhite ? Direction.N : Direction.S;

		final byte shiftFactor = (byte) (isWhite ? 8 : 48);

		for (byte i = 0; i < 8; i++) {
			final Sq[] relevantSquares = Sq.values()[shiftFactor + i].getAllSqInDirection(movementDirection, false,
					(byte) 2);
			final long[] relevantSquaresAsBB = { relevantSquares[0].getAsBB(), relevantSquares[1].getAsBB() };
			ans[i] = EngineUtils.findAllPossibleOrCombos(relevantSquaresAsBB);
		}

		return ans;
	}
}
