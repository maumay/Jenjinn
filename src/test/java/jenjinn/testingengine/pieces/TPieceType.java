/**
 * Copyright © 2017 Lhasa Limited
 * File created: 22 Nov 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.testingengine.pieces;

import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 * @since 22 Nov 2017
 */
public enum TPieceType
{
	P(0, TPawn.class), B(1, TBishop.class), N(2, TKnight.class), R(3, TRook.class), Q(4, TQueen.class), K(5, TKing.class);

	private final byte id;

	private final Class<?> classRep;

	private TPieceType(final int id, final Class<?> classRep)
	{
		this.id = (byte) id;
		this.classRep = classRep;
	}

	public TChessPiece generatePiece(final Side s)
	{
		TChessPiece p = null;
		switch (this)
		{
			case P:
				p = new TPawn(s);
				break;
			case B:
				p = new TBishop(s);
				break;
			case N:
				p = new TKnight(s);
				break;
			case R:
				p = new TRook(s);
				break;
			case Q:
				p = new TQueen(s);
				break;
			case K:
				p = new TKing(s);
				break;
			default:
				throw new AssertionError("Not yet impl");
		}
		return p;
	}

	/**
	 * @return the id
	 */
	public byte getId()
	{
		return id;
	}

	/**
	 * @return the classRep
	 */
	public Class<?> getClassRep()
	{
		return classRep;
	}

	public static TPieceType fromId(final byte id)
	{
		return TPieceType.values()[id % 6];
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