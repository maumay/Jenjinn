package jenjinn.engine.pieces;

import jenjinn.engine.enums.Side;

/**
 * An enumeration of piece types.
 * 
 * @author TB
 * @date 1 Dec 2016
 */
public enum PieceType {
	P(0, Pawn.class), B(1, Bishop.class), N(2, Knight.class), R(3, Rook.class), Q(4, Queen.class), K(5, King.class);

	private final byte id;

	private final Class<?> classRep;

	private PieceType(final int id, final Class<?> classRep)
	{
		this.id = (byte) id;
		this.classRep = classRep;
	}

	public ChessPiece generatePiece(final Side s)
	{
		ChessPiece p = null;
		switch (this) {
		case P:
			p = new Pawn(s);
			break;
		case B:
			p = new Bishop(s);
			break;
		case N:
			p = new Knight(s);
			break;
		case R:
			p = new Rook(s);
			break;
		case Q:
			p = new Queen(s);
			break;
		case K:
			p = new King(s);
			break;
		default:
			throw new AssertionError("Not yet impl");
		}
		return p;
	}

	public static PieceType fromId(final byte id)
	{
		return PieceType.values()[id % 6];
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
}
