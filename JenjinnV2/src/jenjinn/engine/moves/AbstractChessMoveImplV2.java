/**
 * Copyright © 2017 Lhasa Limited
 * File created: 20 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.moves;

import jenjinn.engine.enums.MoveType;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public abstract class AbstractChessMoveImplV2 implements ChessMove
{
	private final byte start, target;

	private final MoveType type;

	AbstractChessMoveImplV2(final MoveType type, final int start, final int target)
	{
		this.type = type;
		this.start = (byte) start;
		this.target = (byte) target;
	}

	public byte getStart()
	{
		return start;
	}

	public long getStartBB()
	{
		return (1L << start);
	}

	public byte getTarget()
	{
		return target;
	}

	public long getTargetBB()
	{
		return (1L << target);
	}

	public MoveType getType()
	{
		return type;
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