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
 *       A class representing a Bishop, to be used by the engine
 */
public class Bishop extends ChessPiece
{
	Bishop(final Side side)
	{
		super(PieceType.B, side, side == Side.W ? 3_000_100 : -3_000_100);
	}

	/* (non-Javadoc)
	 *
	 * @see jenjinn.engine.pieces.ChessPiece#getAttackset(byte, long, long) */
	@Override
	public long getAttackset(final byte loc, final long friendlyPieces, final long enemyPieces)
	{
		return staticGetAttackset(loc, friendlyPieces, enemyPieces);
	}

	static long staticGetAttackset(final byte loc, final long friendlyPieces, final long enemyPieces)
	{
		final int magicIndex = generateMagicIndex(loc, friendlyPieces | enemyPieces);
		return BBDB.BMM[loc][magicIndex];
	}

	private static int generateMagicIndex(final byte loc, final long allPieces)
	{
		final long occupancyVariation = allPieces & BBDB.BOM[loc];
		final long magicNumber = BBDB.BMN[loc];
		final byte bitShift = BBDB.BMB[loc];
		return (int) ((occupancyVariation * magicNumber) >>> bitShift);
	}
}
