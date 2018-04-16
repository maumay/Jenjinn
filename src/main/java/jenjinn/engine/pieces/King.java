/**
 * Written by Tom Ball 2017.
 *
 * This code is unlicensed but please don't plagiarize.
 */

package jenjinn.engine.pieces;

import jenjinn.engine.bitboarddatabase.Bitboards;
import jenjinn.engine.enums.Side;

/**
 * A class representing a King, to be used by the engine. Note here that moving
 * into check is not taken into consideration. This will be done later.
 * 
 * @author TB
 * @date 24 Jan 2017
 */
public class King extends ChessPiece
{
	King(final Side side)
	{
		super(PieceType.K, side);
	}

	@Override
	public long getAttackset(final byte loc, final long occupiedSquares)
	{
		return Bitboards.EBA[6][loc];
	}

	@Override
	public long getStartBitboard()
	{
		return 0b1000L << 56 * (getSide().isWhite() ? 0 : 1);
	}
}
