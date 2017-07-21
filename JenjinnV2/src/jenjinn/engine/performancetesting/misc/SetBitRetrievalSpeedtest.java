/**
 * Copyright © 2017 Lhasa Limited
 * File created: 19 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.performancetesting.misc;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import jenjinn.engine.misc.EngineUtils;

/**
 * @author ThomasB
 * @since 19 Jul 2017
 */
public class SetBitRetrievalSpeedtest
{
	static final int NUM_TEST_CASES = 4000000;

	static final Random R = new Random();

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final long[] random = generateRandom64bits(10);
		final List<TLongList> allTimes = Arrays.asList(new TLongArrayList());// , new TLongArrayList());

		for (int i = 0; i < NUM_TEST_CASES; i++)
		{
			final int rIndex = R.nextInt(10);

			final long start1 = System.nanoTime();
			final byte[] firstResult = EngineUtils.getSetBits(random[rIndex]);
			allTimes.get(0).add(System.nanoTime() - start1);

			// final long start2 = System.nanoTime();
			// final byte[] secondResult = EngineUtils.getSetBits(bit64);
			// allTimes.get(1).add(System.nanoTime() - start2);
			//
			// if (!Arrays.equals(firstResult, secondResult))
			// {
			// throw new AssertionError("" + bit64);
			// }
		}

		final List<BigInteger> averageTimes = EngineUtils.average(allTimes);

		System.out.println(averageTimes.get(0).toString());
		// System.out.println(averageTimes.get(1).toString());

	}

	private static long[] generateRandom64bits(final int testCases)
	{
		final long[] nums = new long[testCases];

		for (int i = 0; i < testCases; i++)
		{
			nums[i] = R.nextLong();
		}
		return nums;
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