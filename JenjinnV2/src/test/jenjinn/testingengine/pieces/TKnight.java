/**
 * Copyright © 2017 Lhasa Limited
 * File created: 19 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.testingengine.pieces;

import java.util.Arrays;
import java.util.stream.Collectors;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;
import jenjinn.engine.pieces.PieceType;

/**
 * @author ThomasB
 * @since 19 Sep 2017
 */
public class TKnight extends TChessPiece
{

	/**
	 * @param type
	 * @param side
	 * @param moveDirs
	 */
	public TKnight(final Side side)
	{
		super(PieceType.K, side, Arrays.stream(Direction.values())
				.filter(x -> x.name().length() == 3)
				.collect(Collectors.toList()));

		assert movementDirections.size() == 8;
	}

	@Override
	public long getAttackset(final byte loc, final long occupiedSquares)
	{
		long sup = super.getAttackset(loc, occupiedSquares);

		for (int i = 0; i < 8; i++)
		{
			if (Math.abs((loc / 8) - i) > 2)
			{
				sup &= ~BBDB.RNK[i];
			}
			if (Math.abs((loc % 8) - i) > 2)
			{
				sup &= ~BBDB.FILE[i];
			}
		}

		return sup;
	}

	@Override
	public long getStartBitboard()
	{
		return 0b1000010L << 56 * (getSide().isWhite() ? 0 : 1);
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