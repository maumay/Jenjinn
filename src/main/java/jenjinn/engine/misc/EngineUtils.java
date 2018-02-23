package jenjinn.engine.misc;

import static io.xyz.chains.utilities.CollectionUtil.len;
import static io.xyz.chains.utilities.CollectionUtil.take;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.moves.StandardMove;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author TB
 * @date 21 Jan 2017
 *
 *       A general utility class containing various useful static methods.
 */
public class EngineUtils
{
	/** Takes a bitboard and returns a 64 character string representation */
	public static String bitboardToString(final long l)
	{
		final String lString = Long.toBinaryString(l);
		final byte paddingZeros = (byte) (64 - lString.length());
		final StringBuilder b = new StringBuilder(64);
		for (byte i = 0; i < paddingZeros; i++) {
			b.append("0");
		}
		for (byte i = 0; i < lString.length(); i++) {
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
		for (byte i = 0; i < n; i++) {
			asStrings[i] = bitboardToString(args[i]);
		}
		for (byte i = 0; i < 8; i++) {
			final StringBuilder builder = new StringBuilder();
			for (byte j = 0; j < n; j++) {
				builder.append(asStrings[j].substring(8 * i, 8 * (i + 1)));
				if (j < n - 1) {
					builder.append(gap);
				}
			}
			System.out.println(builder.toString());
		}
	}

	/** Performs bitwise or operation of all entries in the parameter array */
	public static long multipleOr(final long... args)
	{
		long ans = 0L;
		for (final long arg : args) {
			ans |= arg;
		}
		return ans;
	}

	/** Performs bitwise xor operation of all entries in the parameter array */
	public static long multipleXor(final long... args)
	{
		long ans = 0L;
		for (final long arg : args) {
			ans ^= arg;
		}
		return ans;
	}

	/**
	 * Performs bitwise or operation of all single occupancy bitboards represented
	 * by the Sq in parameter array
	 */
	public static long multipleOr(final Sq... args)
	{
		if (args == null) {
			return -1;
		}

		long ans = 0L;

		for (final Sq arg : args) {
			ans |= arg.getAsBB();
		}
		return ans;
	}

	/** Return the bb representation of the rank the Sq resides on */
	public static long getRankOf(final Sq sq)
	{
		long ans = -1L;
		final long sqAsBb = BBDB.SOB[sq.ordinal()];
		for (byte i = 0; i < 8; i++) {
			final long rnk = BBDB.RNK[i];
			if ((sqAsBb & rnk) != 0) {
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
		for (byte i = 0; i < 8; i++) {
			final long file = BBDB.FILE[i];
			if ((sqAsBb & file) != 0) {
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
		for (byte i = 0; i < 15; i++) {
			final long diag = BBDB.DGNL[i];
			if ((sqAsBb & diag) != 0) {
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
		for (int i = 0; i < 15; i++) {
			final long aDiag = BBDB.ADGNL[i];
			if ((sqAsBb & aDiag) != 0) {
				ans = aDiag;
				break;
			}
		}
		return ans;
	}

	/**
	 * Recursive method to calculate and return all possible bitboards arising from
	 * performing bitwise | operation on each element of each subset of the powerset
	 * of the given array. The size of the returned array is 2^(array.length).
	 */
	public static long[] findAllPossibleOrCombos(final long[] array)
	{
		final int length = array.length;
		if (length == 1) {
			return new long[] { 0L, array[0] };
		}
		else {
			final long[] ans = new long[(int) Math.pow(2.0, length)];
			final long[] recursiveAns = findAllPossibleOrCombos(take(length - 1, array));
			int ansIndexCounter = 0;
			int recursiveAnsIndexCounter = 0;
			for (int j = 0; j < recursiveAns.length; j++) {
				for (long i = 0; i < 2; i++) {
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
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == obj) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param bitboard
	 * @return
	 */
	public static byte[] getSetBits(long bitboard)
	{
		final int cardinality = Long.bitCount(bitboard);
		final byte[] setBits = new byte[cardinality];
		byte arrCounter = 0, loopCounter = 0;

		while (bitboard != 0) {
			if ((1 & bitboard) != 0) {
				setBits[arrCounter++] = loopCounter;
			}
			loopCounter++;
			bitboard >>>= 1;
		}
		return setBits;
	}

	public static List<BigInteger> average(final List<long[]> values)
	{
		final List<BigInteger> averages = new ArrayList<>(values.size());

		values.stream().forEach(x -> {
			BigInteger total = BigInteger.ZERO;
			for (long val : x) {
				total = total.add(BigInteger.valueOf(val));
			}
			averages.add(total.divide(BigInteger.valueOf(len(x))));
		});

		return averages;
	}

	public static StandardMove[] bitboardToMoves(final byte loc, final long bitboard)
	{
		final int bitboardCard = Long.bitCount(bitboard);

		final StandardMove[] mvs = new StandardMove[bitboardCard];

		int ctr = 0;
		final byte[] setBits = getSetBits(bitboard);
		for (final byte b : setBits) {
			mvs[ctr++] = StandardMove.get(loc, b);
		}

		return mvs;
	}

	public static long[] getStartingPieceLocs()
	{
		final long[] start = new long[12];

		for (int i = 0; i < 12; i++) {
			start[i] = ChessPiece.get(i).getStartBitboard();
		}

		return start;
	}

	public static long getStartingDevStatus()
	{
		final long[] startLocs = EngineUtils.getStartingPieceLocs();

		return startLocs[1] | startLocs[2] | startLocs[7] | startLocs[8] | ((startLocs[0] | startLocs[6]) & (BBDB.FILE[3] | BBDB.FILE[4]));
	}

	public static long getBB(final Sq... sqs)
	{
		return multipleOr(Arrays.stream(sqs).mapToLong(sq -> sq.getAsBB()).toArray());
	}

	public static void writeMoves(final List<ChessMove> toWrite, final Path path) throws IOException
	{
		final List<String> asStrings = new ArrayList<>();

		for (final ChessMove mv : toWrite) {
			asStrings.add(mv.toCompactString());
		}

		Files.write(path, asStrings);
	}

	public static List<ChessMove> readMoves(final Path path) throws IOException
	{
		final List<String> lines = Files.readAllLines(path);
		final List<ChessMove> mvs = new ArrayList<>();

		for (final String line : lines) {
			mvs.add(ChessMove.fromCompactString(line));
		}

		return mvs;
	}

	public static String formatPieceTable(short[] ptable)
	{
		assert ptable.length == 64;
		int maxlen = 0;
		for (short val : ptable) {
			maxlen = Math.max(Integer.toString(val).length(), maxlen);
		}

		StringBuilder sb = new StringBuilder();
		int ctr = 63;
		for (int i = 63; i >= 0; i--) {
			int val = ptable[i];
			sb.append(getPaddedString(val, maxlen));
			sb.append(" ");
			if ((--ctr) % 8 == 7) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private static String getPaddedString(int i, int len)
	{
		StringBuilder sb = new StringBuilder(Integer.toString(i));
		while (sb.length() < len) {
			sb.append(" ");
		}
		return sb.toString();
	}

	public static void main(final String[] args)
	{
		// System.out.println(Arrays.toString(getSetBits(33746390L)));
		// System.out.println(BBDB.SOB[0]);

		short[] testP = new short[64];
		testP[0] = 1;
		testP[Sq.c4.ordinal()] = -3;
		System.out.println(formatPieceTable(testP));
	}
}
