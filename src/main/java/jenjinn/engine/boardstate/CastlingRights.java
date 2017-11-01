/**
 *
 */
package jenjinn.engine.boardstate;

import gnu.trove.impl.unmodifiable.TUnmodifiableByteByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteList;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.map.TByteByteMap;
import gnu.trove.map.hash.TByteByteHashMap;

/**
 * @author t
 *
 */
public final class CastlingRights
{
	private CastlingRights()
	{
	}

	public static final byte W_KINGSIDE = 0b1;

	public static final byte W_QUEENSIDE = 0b10;

	public static final byte B_KINGSIDE = 0b100;

	public static final byte B_QUEENSIDE = 0b1000;

	public static final byte ALL_RIGHTS = 0b1111;

	/**
	 * A List of the different values, it is unmodifiable.
	 */
	public static final TByteList VALUES = new TUnmodifiableByteList(getValues());

	/**
	 * A convenience map for when we want to update the castle rights feature of the
	 * board hashing for standard moves. It allows us to quickly map corresponding
	 * start and target squares to the rights they would lose.
	 */
	public static final TByteByteMap STANDARD_MOVE_ERASURES = new TUnmodifiableByteByteMap(getStandardMoveErasures());

	private static final TByteArrayList getValues()
	{
		return new TByteArrayList(new byte[] { W_KINGSIDE, W_QUEENSIDE, B_KINGSIDE, B_QUEENSIDE });
	}

	private static TByteByteMap getStandardMoveErasures()
	{
		final TByteByteMap erasures = new TByteByteHashMap();

		erasures.put((byte) 0, W_KINGSIDE);
		erasures.put((byte) 3, (byte) (W_KINGSIDE | W_QUEENSIDE));
		erasures.put((byte) 7, W_QUEENSIDE);
		erasures.put((byte) 56, B_KINGSIDE);
		erasures.put((byte) 59, (byte) (B_KINGSIDE | B_QUEENSIDE));
		erasures.put((byte) 63, B_QUEENSIDE);

		return erasures;
	}

	// public static final byte representationOf(final CastlingArea area)
	// {
	// return VALUES.get(area.ordinal());
	// }

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		System.out.println(Integer.toBinaryString(W_KINGSIDE | W_QUEENSIDE | B_KINGSIDE | B_QUEENSIDE));
	}
}
