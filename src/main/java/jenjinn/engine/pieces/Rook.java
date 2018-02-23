package jenjinn.engine.pieces;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Side;

/**
 * Rook representation to be used by the engine.
 *
 * @author TB
 * @date 24 Jan 2017
 */
public class Rook extends ChessPiece
{
	Rook(final Side side)
	{
		super(PieceType.R, side);
	}

	@Override
	public long getAttackset(final byte loc, final long occupiedSquares)
	{
		return staticGetAttackset(loc, occupiedSquares);
	}

	static long staticGetAttackset(final byte loc, final long occupiedSquares)
	{
		final int magicIndex = generateMagicIndex(loc, occupiedSquares);
		return BBDB.RMM[loc][magicIndex];
	}

	private static int generateMagicIndex(final byte loc, final long allPieces)
	{
		final long occupancyVariation = allPieces & BBDB.ROM[loc];
		final long magicNumber = BBDB.RMN[loc];
		final byte bitShift = BBDB.RMB[loc];
		return (int) ((occupancyVariation * magicNumber) >>> bitShift);
	}

	@Override
	public long getStartBitboard()
	{
		return 0b10000001L << 56 * (getSide().isWhite() ? 0 : 1);
	}
}
