package jenjinn.engine.pieces;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Side;

/**
 * A class representing a Bishop, to be used by the engine
 * 
 * @author TB
 * @date 24 Jan 2017
 */
public class Bishop extends ChessPiece
{
	Bishop(final Side side)
	{
		super(PieceType.B, side);
	}

	@Override
	public long getAttackset(final byte loc, final long occupiedSquares)
	{
		return staticGetAttackset(loc, occupiedSquares);
	}

	static long staticGetAttackset(final byte loc, final long occupiedSquares)
	{
		final int magicIndex = generateMagicIndex(loc, occupiedSquares);
		return BBDB.BMM[loc][magicIndex];
	}

	private static int generateMagicIndex(final byte loc, final long allPieces)
	{
		final long occupancyVariation = allPieces & BBDB.BOM[loc];
		final long magicNumber = BBDB.BMN[loc];
		final byte bitShift = BBDB.BMB[loc];
		return (int) ((occupancyVariation * magicNumber) >>> bitShift);
	}

	@Override
	public long getStartBitboard()
	{
		return 0b100100L << 56 * (getSide().isWhite() ? 0 : 1);
	}
}
