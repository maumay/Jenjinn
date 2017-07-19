/**
 * Copyright © 2017 Lhasa Limited
 * File created: 19 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.performancetesting.misc;

import java.util.Random;

/**
 * @author ThomasB
 * @since 19 Jul 2017
 */
public class SetBitRetrievalSpeedtest
{
	static final int TEST_CASES = 4000;

	static final Random R = new Random();

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final long[] random = generateRandom64bits(TEST_CASES);

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