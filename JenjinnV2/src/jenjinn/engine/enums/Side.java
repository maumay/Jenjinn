/**
 * Written by Tom Ball 2017.
 *
 * This code is unlicensed but please don't plagiarize.
 */

package jenjinn.engine.enums;

/**
 * @author TB
 * @date 1 Dec 2016
 *
 *       Enumeration of the two sides in a chessgame.
 */
public enum Side
{
	W(0, 1), B(6, -1);

	private final byte id;
	private final byte orientation;

	private Side(final int id, final int orientation)
	{
		this.id = (byte) id;
		this.orientation = (byte) orientation;
	}

	public int index()
	{
		return this == W ? 0 : 1;
	}

	public boolean isWhite()
	{
		return this == Side.W;
	}

	public Side otherSide()
	{
		if (this == B)
		{
			return W;
		}
		else
		{
			return B;
		}
	}

	public static Side getOtherSide(final Side s)
	{
		if (s == B)
		{
			return W;
		}
		else
		{
			return B;
		}
	}

	public String getFilename()
	{
		return name().toLowerCase();
	}

	public int orientation()
	{
		return orientation;
	}

	public boolean isMaximising()
	{
		if (this == W)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * @return the id
	 */
	public byte getId()
	{
		return id;
	}
}
