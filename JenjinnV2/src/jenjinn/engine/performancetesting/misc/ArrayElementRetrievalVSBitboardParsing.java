package jenjinn.engine.performancetesting.misc;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import jenjinn.engine.misc.EngineUtils;

public class ArrayElementRetrievalVSBitboardParsing
{
	static Random R = new Random();

	public static void main(final String[] args)
	{
		final byte[] arr = new byte[10];

		final List<TLongList> allTimes = Arrays.asList(new TLongArrayList());
		Arrays.fill(arr, (byte) 62);

		for (int i = 0; i < 400000; i++)
		{
			final int rIndex = R.nextInt(10);

			final long start = System.nanoTime();
			final byte retrieved = arr[rIndex];
			allTimes.get(0).add(System.nanoTime() - start);
		}

		final List<BigInteger> averageTimes = EngineUtils.average(allTimes);

		System.out.println(averageTimes.get(0).toString());
	}

}

/* ---------------------------------------------------------------------*
 * This software is the confidential and proprietary
 * information of Lhasa Limited
 * Granary Wharf House, 2 Canal Wharf, Leeds, LS11 5PS
 * ---
 * No part of this confidential information shall be disclosed
 * and it shall be used only in accordance with the terms of a
 * written license agreement entered into by holder of the information
 * with LHASA Ltd.
 * --------------------------------------------------------------------- */