/**
 * Written by Tom Ball 2017.
 *
 * This code is unlicensed but please don't plagiarize.
 */

package jenjinn.engine.pieces;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Side;

/**
 * A class representing a Pawn for use in the engine
 *
 * @author TB
 * @date 24 Jan 2017
 */
public class Pawn extends ChessPiece
{
	Pawn(final Side side)
	{
		super(PieceType.P, side, side == Side.W ? 1_000_000 : -1_000_000);
	}

	/* (non-Javadoc)
	 *
	 * @see jenjinn.engine.pieces.ChessPiece#getAttackset(byte, long, long) */
	@Override
	public long getAttackset(final byte loc, final long friendlyPieces, final long enemyPieces)
	{
		return BBDB.EBA[getSide().ordinal()][loc];
	}

	private int generateMagicIndex(final byte reverseFileNumber, final long allPieces)
	{
		final boolean isWhite = isWhite();
		final long occupancyVariation = allPieces & (isWhite ? BBDB.WPFMOM[reverseFileNumber] : BBDB.BPFMOM[reverseFileNumber]);
		final long magicNumber = isWhite ? BBDB.WPFMMN[reverseFileNumber] : BBDB.BPFMMN[reverseFileNumber];
		final byte bitShift = BBDB.PFMB;
		return (int) ((occupancyVariation * magicNumber) >>> bitShift);
	}

	private boolean inFirstMoveZone(final byte loc)
	{
		return isWhite() ? (7 < loc && loc < 16) : (47 < loc && loc < 56);
	}
}
