/**
 *
 */
package jenjinn.engine.boardstate;

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
	public static final byte[] VALUES = new byte[] { W_KINGSIDE, W_QUEENSIDE, B_KINGSIDE, B_QUEENSIDE };

	/**
	 * A convenience map for when we want to update the castle rights feature of the
	 * board hashing for standard moves. It allows us to quickly map corresponding
	 * start and target squares to the rights they would lose.
	 */
	public static final byte[] STANDARD_MOVE_ERASURES = getStandardMoveErasures();

	private static byte[] getStandardMoveErasures()
	{
		final byte[] erasures = new byte[64];

		erasures[0] = W_KINGSIDE;
		erasures[3] = W_KINGSIDE | W_QUEENSIDE;
		erasures[7] = W_QUEENSIDE;
		erasures[56] = B_KINGSIDE;
		erasures[59] = B_KINGSIDE | B_QUEENSIDE;
		erasures[63] = B_QUEENSIDE;

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
