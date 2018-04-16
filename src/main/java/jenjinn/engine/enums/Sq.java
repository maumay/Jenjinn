/**
 * Written by Tom Ball 2017.
 *
 * This code is unlicensed but please don't plagiarize.
 */

package jenjinn.engine.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.bitboarddatabase.Bitboards;
import jenjinn.engine.misc.ChessBoardPoint;

/**
 * Enumeration of the 64 chess squares on a chess board.
 * Ordered the same as the order in which the single
 * occupancy bitboards are generated.
 *
 * @author TB
 * @date 30 Nov 2016
 */
public enum Sq
{
	// DON'T CHANGE ORDER
	h1, g1, f1, e1, d1, c1, b1, a1,
	h2, g2, f2, e2, d2, c2, b2, a2,
	h3, g3, f3, e3, d3, c3, b3, a3,
	h4, g4, f4, e4, d4, c4, b4, a4,
	h5, g5, f5, e5, d5, c5, b5, a5,
	h6, g6, f6, e6, d6, c6, b6, a6,
	h7, g7, f7, e7, d7, c7, b7, a7,
	h8, g8, f8, e8, d8, c8, b8, a8;

	/**
	 * Convenience method for getting the number of squares
	 * between the start sq (this) and the edge of the board.
	 * Not inclusive of the start sq.
	 */
	public byte numberOfSquaresLeftInDirection(final Direction direction)
	{
		byte ans = 0;
		Sq nextSq = getNextSqInDirection(direction);
		while (nextSq != null) {
			ans++;
			nextSq = nextSq.getNextSqInDirection(direction);
		}
		return ans;
	}

	/**
	 * Given a start Sq and direction this method returns
	 * the next Sq on from the start in the specified
	 * direction, or null if there are no more Sq in this
	 * direction.
	 */
	public Sq getNextSqInDirection(final Direction direction)
	{
		final ChessBoardPoint startSq = getAsPoint();
		final int newX = startSq.x + direction.dx;
		final int newY = startSq.y + direction.dy;
		return getSq(newX, newY);
	}

	/**
	 * Given a start Sq and direction this method returns
	 * an array containing all Sq in given direction
	 * including the start Sq.
	 */
	public Sq[] getAllSqInDirection(final Direction direction, final boolean includeStart, final int lengthCap)
	{
		final List<Sq> collectedSqs = includeStart ? new ArrayList<>(Arrays.asList(this)) : new ArrayList<>();
		Sq nextSq = getNextSqInDirection(direction);
		byte lengthCounter = 0;
		while (nextSq != null && lengthCounter < lengthCap)
		{
			collectedSqs.add(nextSq);
			nextSq = nextSq.getNextSqInDirection(direction);
			lengthCounter++;
		}
		return collectedSqs.toArray(new Sq[collectedSqs.size()]);
	}

	/**
	 * Given a start Sq and direction this method returns
	 * an array containing all Sq in given direction
	 * including the start Sq.
	 */
	public Sq[] getAllSqInDirection(final Direction direction, final boolean includeStart)
	{
		return getAllSqInDirection(direction, includeStart, (byte) 10);
	}

	/** Return corresponding Sq for given index parameter */
	public static Sq get(final int index)
	{
		if (index >= 0 && index < 64)
		{
			return values()[index];
		}
		return null;
	}

	/** Cartesian coordinates */
	public static Sq getSq(final int x, final int y)
	{
		if (Sq.coordinatesAreValid(x, y)) {
			return get(8 * y + (7 - x));
		}
		return null;
	}

	/** Returns the coords of a Sq in terms of files and ranks */
	public ChessBoardPoint getAsPoint()
	{
		final byte index = (byte) ordinal();
		return new ChessBoardPoint((byte) (7 - (index % 8)), (byte) (index / 8));
	}

	public long getAsBB()
	{
		return Bitboards.SOB[ordinal()];
	}

	/**
	 * Method to check whether all provided indices correspond to chess squares
	 */
	public static boolean allIndicesCorrespondToChessSquares(final byte... indices)
	{
		boolean answer = true;

		for (final byte index : indices)
		{
			answer = 0 <= index && index < 64;
			if (!answer)
			{
				break;
			}
		}
		return answer;
	}

	/**
	 * Method to check whether (x,y) lies on a chessboard, i.e whether
	 * (x,y) in {0,...,7} X {0,...,7}.
	 */
	public static boolean coordinatesAreValid(final int x, final int y)
	{
		return 0 <= x && x < 8 && 0 <= y && y < 8;
	}

	public boolean isLightSquare()
	{
		final ChessBoardPoint asPoint = getAsPoint();
		final boolean rankStartsWithLightSquare = (asPoint.y % 2) == 0;
		return (asPoint.x % 2) == (rankStartsWithLightSquare ? 0 : 1);
	}

	public static void main(final String[] args)
	{
		System.out.println(a1.isLightSquare());
		System.out.println(e4.isLightSquare());
		System.out.println(g7.isLightSquare());
	}
}
