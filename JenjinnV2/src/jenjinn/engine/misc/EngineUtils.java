/**
 * Written by Tom Ball 2017.
 *
 * This code is unlicensed but please don't plagiarize.
 */

package jenjinn.engine.misc;

import gnu.trove.list.array.TLongArrayList;
import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Sq;

/**
 * @author TB
 * @date 21 Jan 2017
 *
 *       A general utility class containing various useful
 *       static methods.
 */
public class EngineUtils
{
	/** Takes a bitboard and returns a 64 character string representation */
	public static String bitboardToString(final long l)
	{
		final String lString = Long.toBinaryString(l);
		final byte paddingZeros = (byte) (64 - lString.length());
		final StringBuilder b = new StringBuilder(64);
		for (byte i = 0; i < paddingZeros; i++)
		{
			b.append("0");
		}
		for (byte i = 0; i < lString.length(); i++)
		{
			b.append(lString.charAt(i));
		}
		return b.toString();
	}

	/** Prints n tab separated bitboards side by side to the console */
	public static void printNbitBoards(final long... args)
	{
		final byte n = (byte) args.length;
		final String gap = "\t";
		final String[] asStrings = new String[n];
		for (byte i = 0; i < n; i++)
		{
			asStrings[i] = bitboardToString(args[i]);
		}
		for (byte i = 0; i < 8; i++)
		{
			final StringBuilder builder = new StringBuilder();
			for (byte j = 0; j < n; j++)
			{
				builder.append(asStrings[j].substring(8 * i, 8 * (i + 1)));
				if (j < n - 1)
				{
					builder.append(gap);
				}
			}
			System.out.println(builder.toString());
		}
	}

	/** Performs bitwise or operation of all entries in the parameter array */
	public static long multipleOr(final long... args)
	{
		if (args == null)
		{
			return -1;
		}

		long ans = 0L;
		for (final long arg : args)
		{
			ans |= arg;
		}
		return ans;
	}

	/**
	 * Performs bitwise or operation of all single occupancy
	 * bitboards represented by the Sq in parameter array
	 */
	public static long multipleOr(final Sq... args)
	{
		if (args == null)
		{
			return -1;
		}

		long ans = 0L;

		for (final Sq arg : args)
		{
			ans |= arg.getAsBB();
		}
		return ans;
	}

	/** Return the bb representation of the rank the Sq resides on */
	public static long getRankOf(final Sq sq)
	{
		long ans = -1L;
		final long sqAsBb = BBDB.SOB[sq.ordinal()];
		for (byte i = 0; i < 8; i++)
		{
			final long rnk = BBDB.RNK[i];
			if ((sqAsBb & rnk) != 0)
			{
				ans = rnk;
				break;
			}
		}
		return ans;
	}

	/** Return the bb representation of the file the Sq resides on */
	public static long getFileOf(final Sq sq)
	{
		long ans = -1L;
		final long sqAsBb = BBDB.SOB[sq.ordinal()];
		for (byte i = 0; i < 8; i++)
		{
			final long file = BBDB.FILE[i];
			if ((sqAsBb & file) != 0)
			{
				ans = file;
				break;
			}
		}
		return ans;
	}

	/** Return the bb representation of the diag the Sq resides on */
	public static long getDiagonalOf(final Sq sq)
	{
		long ans = -1L;
		final long sqAsBb = BBDB.SOB[sq.ordinal()];
		for (byte i = 0; i < 15; i++)
		{
			final long diag = BBDB.DGNL[i];
			if ((sqAsBb & diag) != 0)
			{
				ans = diag;
				break;
			}
		}
		return ans;
	}

	/** Return the bb representation of the adiagonal the Sq resides on */
	public static long getAntiDiagonalOf(final Sq sq)
	{
		long ans = -1L;
		final long sqAsBb = BBDB.SOB[sq.ordinal()];
		for (int i = 0; i < 15; i++)
		{
			final long aDiag = BBDB.ADGNL[i];
			if ((sqAsBb & aDiag) != 0)
			{
				ans = aDiag;
				break;
			}
		}
		return ans;
	}

	/**
	 * Recursive method to calculate and return all possible bitboards arising
	 * from performing bitwise | operation on each element of each subset of
	 * the powerset of the given array. The size of the returned array is
	 * 2^(array.length).
	 */
	public static long[] findAllPossibleOrCombos(final long[] array)
	{
		final int length = array.length;
		if (length == 1)
		{
			return new long[] { 0L, array[0] };
		}
		else
		{
			final long[] ans = new long[(int) Math.pow(2.0, length)];
			final TLongArrayList arrayAsArrayList = new TLongArrayList(array);
			arrayAsArrayList.removeAt(length - 1);
			final long[] recursiveArg = arrayAsArrayList.toArray(new long[length - 1]);
			final long[] recursiveAns = findAllPossibleOrCombos(recursiveArg);
			int ansIndexCounter = 0;
			int recursiveAnsIndexCounter = 0;
			for (int j = 0; j < recursiveAns.length; j++)
			{
				for (long i = 0; i < 2; i++)
				{
					ans[ansIndexCounter] = recursiveAns[recursiveAnsIndexCounter] | (array[length - 1] * i);
					ansIndexCounter++;
				}
				recursiveAnsIndexCounter++;
			}
			return ans;
		}
	}

	public static <E> void swapElements(final E[] arr, final int idx1, final int idx2)
	{
		final E temp = arr[idx1];
		arr[idx1] = arr[idx2];
		arr[idx2] = temp;
	}

	public static <E> int getIndexInArray(final E[] arr, final E obj)
	{
		for (int i = 0; i < arr.length; i++)
		{
			if (arr[i] == obj)
			{
				return i;
			}
		}
		return -1;
	}

	public static byte[] getSetBits(final long bitboard)
	{
		// long x = bitboard;

		final int cardinality = Long.bitCount(bitboard);
		final byte[] setBits = new byte[cardinality];
		int counter = 0;

		for (byte i = 0; i < 64 && counter < cardinality; i++)
		{
			if ((BBDB.SOB[i] & bitboard) != 0)
			{
				setBits[counter] = i;
				counter++;
			}
		}
		return setBits;
	}
}
