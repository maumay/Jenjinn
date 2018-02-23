/**
 * 
 */
package jenjinn.engine.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Sq;

/**
 * A Cord object wraps twos points on a chessboard and the corresponding
 * bitboard connecting them, exclusive of the two end points. We have
 * exclusivity instead of inclusivity because pieces will occupy the end
 * squares. We hold all cords we will need in a cache.
 * 
 * @author TB
 * @date 6 Feb 2017
 */
public class Cord implements Comparable<Cord>
{
	// TODO - Optimise size of cache. Unecessary memory used atm
	public static final long[][] cache = generateCordCache();

	public final byte start;
	public final byte end;
	public final long connectingBitboard;

	private Cord(final byte firstIndex, final byte secondIndex)
	{
		start = (byte) Math.min(firstIndex, secondIndex);
		end = (byte) Math.max(firstIndex, secondIndex);
		connectingBitboard = generateConnection(start, end);
	}

	private long generateConnection(final byte start, final byte end)
	{
		long connectingBitboard = 0L;
		final Sq startSq = Sq.get(start);
		final Sq endSq = Sq.get(end);

		for (Direction dir : PieceMovementDirectionArrays.KD) {
			final List<Sq> allSquaresInGivenDirection = Arrays.asList(startSq.getAllSqInDirection(dir, false));

			if (allSquaresInGivenDirection.contains(endSq)) {
				final int endIndex = allSquaresInGivenDirection.indexOf(endSq);
				final List<Sq> connectingSqs = allSquaresInGivenDirection.subList(0, endIndex);
				connectingBitboard = EngineUtils.multipleOr(connectingSqs.toArray(new Sq[0]));
				break;
			}
		}
		return connectingBitboard;
	}

	@Override
	public boolean equals(final Object obj)
	{
		boolean equal = false;

		if (obj instanceof Cord) {
			final Cord other = (Cord) obj;
			equal = start == other.start && end == other.end;
		}

		return equal;
	}

	@Override
	public int hashCode()
	{
		return 3 * start + 101 * end;
	}

	public static long getCordBetween(final byte firstIndex, final byte secondIndex)
	{
		final byte start = (byte) Math.min(firstIndex, secondIndex);
		final byte end = (byte) Math.max(firstIndex, secondIndex);
		return cache[start][end];
	}

	public static long getInclusiveCordBetween(final byte firstIndex, final byte secondIndex)
	{
		return getCordBetween(firstIndex, secondIndex) | BBDB.SOB[firstIndex] | BBDB.SOB[secondIndex];
	}

	private static long[][] generateCordCache()
	{
		final long[][] cordCache = new long[64][64];

		final List<Cord> possibleCords = generateAllPossibleUniqueCords();

		for (Cord cord : possibleCords) {
			cordCache[cord.start][cord.end] = cord.connectingBitboard;
		}

		return cordCache;
	}

	private static List<Cord> generateAllPossibleUniqueCords()
	{
		final List<Cord> uniqueCords = new ArrayList<>();

		final long[] bishopEmptyBoardMoves = BBDB.EBM[2];
		final long[] rookEmptyBoardMoves = BBDB.EBM[4];

		for (byte i = 0; i < 64; i++) {
			final Cord[] diagonalsToAdd = convertBitboardToCords(i, bishopEmptyBoardMoves[i]);
			final Cord[] nonDiagsToAdd = convertBitboardToCords(i, rookEmptyBoardMoves[i]);

			addNonDuplicateCordsToList(diagonalsToAdd, uniqueCords);
			addNonDuplicateCordsToList(nonDiagsToAdd, uniqueCords);
		}
		Collections.sort(uniqueCords);
		return uniqueCords;
	}

	private static void addNonDuplicateCordsToList(final Cord[] cordsToAdd, final List<Cord> currentCache)
	{
		for (Cord toAdd : cordsToAdd) {
			if (!currentCache.contains(toAdd)) {
				currentCache.add(toAdd);
			}
		}
	}

	private static Cord[] convertBitboardToCords(final byte startLoc, final long bitboard)
	{
		final byte[] setBits = EngineUtils.getSetBits(bitboard);
		final Cord[] converted = new Cord[setBits.length];
		int counter = 0;

		for (byte setBit : setBits) {
			converted[counter] = new Cord(startLoc, setBit);
			counter++;
		}

		return converted;
	}

	@Override
	public int compareTo(final Cord other)
	{
		return Integer.compare(start, other.start);
	}
}
