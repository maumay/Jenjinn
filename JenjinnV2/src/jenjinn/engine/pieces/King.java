/**
 * Written by Tom Ball 2017.
 *
 * This code is unlicensed but please don't plagiarize.
 */

package jenjinn.engine.pieces;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Side;

/**
 * @author TB
 * @date 24 Jan 2017
 *
 *       A class representing a King, to be used by the engine. Note here that
 *       moving into check is not taken into consideration. This will be done later.
 */
public class King extends ChessPiece
{
	King(final Side side)
	{
		super(PieceType.K, side, side == Side.W ? 500_000_000 : -500_000_000);
	}

	/* (non-Javadoc)
	 * 
	 * @see jenjinn.engine.pieces.ChessPiece#getAttackset(byte, long, long) */
	@Override
	public long getAttackset(final byte loc, final long occupiedSquares)
	{
		return BBDB.EBA[6][loc];
	}
}
