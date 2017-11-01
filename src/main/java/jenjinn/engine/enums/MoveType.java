/**
 * Copyright © 2017 Lhasa Limited
 * File created: 20 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.enums;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public enum MoveType
{
	STANDARD(0), CASTLE(1), ENPASSANT(2), PROMOTION(3);

	public final int id;

	private MoveType(final int id)
	{
		this.id = id;
	}

	public static MoveType getFromId(final int id)
	{
		for (final MoveType mt : MoveType.values())
		{
			if (id == mt.id)
			{
				return mt;
			}
		}
		throw new AssertionError();
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