package jenjinn.engine.pieces;

import jenjinn.engine.bitboarddatabase.Bitboards;
import jenjinn.engine.enums.Side;

/**
 * A class representing a Knight to be used by the engine
 *
 * @author TB
 * @date 24 Jan 2017
 */
public class Knight extends ChessPiece
{
	Knight(final Side side)
	{
		super(PieceType.N, side);
	}

	@Override
	public long getAttackset(final byte loc, final long occupiedSquares)
	{
		return Bitboards.EBA[3][loc];
	}

	@Override
	public long getStartBitboard()
	{
		return 0b1000010L << 56 * (getSide().isWhite() ? 0 : 1);
	}
}
