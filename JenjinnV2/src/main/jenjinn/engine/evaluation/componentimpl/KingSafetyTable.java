/**
 * Copyright � 2017 Lhasa Limited
 * File created: 13 Oct 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.evaluation.componentimpl;

/**
 * Based on the one used in StockFish, read about on ChessProgramming
 *
 * @author ThomasB
 * @since 13 Oct 2017
 */
public class KingSafetyTable
{
	private static final double SCALE_FACTOR = 1.5;

	private static final int[] SAFETY_TABLE =
		{
				0,  0,   1,   2,   3,   5,   7,   9,  12,  15,
				18,  22,  26,  30,  35,  39,  44,  50,  56,  62,
				68,  75,  82,  85,  89,  97, 105, 113, 122, 131,
				140, 150, 169, 180, 191, 202, 213, 225, 237, 248,
				260, 272, 283, 295, 307, 319, 330, 342, 354, 366,
				377, 389, 401, 412, 424, 436, 448, 459, 471, 483,
				494, 500, 500, 500, 500, 500, 500, 500, 500, 500,
				500, 500, 500, 500, 500, 500, 500, 500, 500, 500,
				500, 500, 500, 500, 500, 500, 500, 500, 500, 500,
				500, 500, 500, 500, 500, 500, 500, 500, 500, 500
		};

	static
	{
		// Perform scaling
		for (int i = 0; i < SAFETY_TABLE.length; i++)
		{
			SAFETY_TABLE[i] = (short) (SCALE_FACTOR*SAFETY_TABLE[i]);
		}
	}

	private static final int[] ATTACK_UNITS = { 2, 2, 3, 5 };
	
	private static final int[] CHECK_BONUS  = { 0, 0, 2, 6 };


	public static int indexSafetyTable(final int idx)
	{
		return SAFETY_TABLE[idx];
	}
	
	public static int indexBonusTable(final int idx)
	{
		return CHECK_BONUS[idx];
	}

	public static int indexAttackUnits(final int idx)
	{
		return ATTACK_UNITS[idx];
	}

	// Uninstantiable
	private KingSafetyTable() {}
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