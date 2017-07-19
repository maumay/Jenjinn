/**
 * Copyright © 2017 Lhasa Limited
 * File created: 13 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.enums;

/**
 * Constant values which can be negated without numerical overflow
 *
 * @author ThomasB
 * @since 13 Jul 2017
 */
public final class Infinity
{
	public static final int INT_INFINITY = Integer.MAX_VALUE - 1;

	public static final long LONG_INFINITY = Long.MAX_VALUE - 1;

	private Infinity()
	{
	}

	public static void main(final String[] args)
	{
		System.out.println(INT_INFINITY + " " + -INT_INFINITY);
		System.out.println(LONG_INFINITY + " " + -LONG_INFINITY);
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