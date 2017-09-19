/**
 * Written by Tom Ball 2017.
 *
 * This code is unlicensed but please don't plagiarize.
 */

package jenjinn.engine.pieces;

import jenjinn.engine.enums.Side;

/**
 * @author TB
 * @date 24 Jan 2017
 *
 *       The abstract superclass of all chess pieces to be used by
 *       the engine. These are immutable as we want all information
 *       about position on the board etc to be stored by each individual
 *       BoardState instance.
 */
public abstract class ChessPiece
{
	/**
	 * Here we initialise all instances of ChessPiece we will ever need (since the
	 * class is completely immutable).
	 */
	public static final ChessPiece[] WPIECES;
	public static final ChessPiece[] BPIECES;

	public static final ChessPiece[] PIECES;
	static
	{
		WPIECES = new ChessPiece[6];
		BPIECES = new ChessPiece[6];
		PIECES = new ChessPiece[12];

		for (int i = 0; i < PieceType.values().length; i++)
		{
			WPIECES[i] = PieceType.values()[i].generatePiece(Side.W);
			BPIECES[i] = PieceType.values()[i].generatePiece(Side.B);

			PIECES[i] = WPIECES[i];
			PIECES[6 + i] = BPIECES[i];
		}
	}

	public static ChessPiece get(final int pieceIndex)
	{
		return PIECES[pieceIndex];
	}

	/** The {@link Side} this piece belongs to. */
	private final Side side;

	/** The unique identifier of this piece. An interger between 0 - 12 inclusive. */
	private final byte id;

	protected ChessPiece(final PieceType type, final Side side)
	{
		this.side = side;
		this.id = (byte) (type.getId() + side.index());
	}

	/**
	 * Given the location of the piece, the location of all friendly pieces
	 * and the location of all enemy pieces this method returns the attackset
	 * in bitboard form.
	 */
	public abstract long getAttackset(final byte loc, final long occupiedSquares);

	public abstract long getStartBitboard();

	/**
	 * Given the location of the piece, the location of all friendly pieces
	 * and the location of all enemy pieces this method returns the possible
	 * (standard) moves as an array.
	 */
	public long getMoveset(final byte loc, final long friendlyPieces, final long enemyPieces)
	{
		return getAttackset(loc, enemyPieces | friendlyPieces) & ~friendlyPieces;
	}

	@Override
	public String toString()
	{
		return side.name() + PieceType.fromId(id).name();
	}

	public String getImageString(final int size)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(side.name());
		sb.append(PieceType.fromId(id).name());
		sb.append(size);
		return sb.toString();
	}

	public String getImageString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(side.name());
		sb.append(PieceType.fromId(id).name());
		sb.append("64");
		return sb.toString();
	}

	/**
	 * @return the side
	 */
	public Side getSide()
	{
		return side;
	}

	/**
	 * Convenience method.
	 *
	 * @return
	 */
	public boolean isWhite()
	{
		return side == Side.W;
	}

	/**
	 * @return the id
	 */
	public byte getIndex()
	{
		return id;
	}

	public PieceType getPieceType()
	{
		return PieceType.fromId(getIndex());
	}
}
