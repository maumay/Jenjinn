/**
 * Written by Tom Ball 2017.
 *
 * This code is unlicensed but please don't plagiarize.
 */

package jenjinn.engine.pieces;

import jenjinn.engine.enums.Side;

/**
 * A class representing a Queen, to be used by the engine.
 *
 * @author TB
 * @date 24 Jan 2017
 */
public class Queen extends ChessPiece
{
	Queen(final Side side)
	{
		super(PieceType.Q, side);
	}

	/* (non-Javadoc)
	 *
	 * @see jenjinn.engine.pieces.ChessPiece#getAttackset(byte, long, long) */
	@Override
	public long getAttackset(final byte loc, final long occupiedSquares)
	{
		final long bishopContribution = Bishop.staticGetAttackset(loc, occupiedSquares);
		final long rookContribution = Rook.staticGetAttackset(loc, occupiedSquares);
		return bishopContribution | rookContribution;
	}

	@Override
	public long getStartBitboard()
	{
		return 0b10000L << 56 * (getSide().isWhite() ? 0 : 1);
	}
}
