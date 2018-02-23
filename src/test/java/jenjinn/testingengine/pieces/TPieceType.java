package jenjinn.testingengine.pieces;

import jenjinn.engine.enums.Side;

/**
 * @author ThomasB
 * @since 22 Nov 2017
 */
public enum TPieceType {
	P(0, TPawn.class), B(1, TBishop.class), N(2, TKnight.class), R(3, TRook.class), Q(4, TQueen.class), K(5, TKing.class);

	private final byte id;

	private final Class<?> classRep;

	private TPieceType(final int id, final Class<?> classRep)
	{
		this.id = (byte) id;
		this.classRep = classRep;
	}

	public TChessPiece generatePiece(final Side s)
	{
		TChessPiece p = null;
		switch (this) {
		case P:
			p = new TPawn(s);
			break;
		case B:
			p = new TBishop(s);
			break;
		case N:
			p = new TKnight(s);
			break;
		case R:
			p = new TRook(s);
			break;
		case Q:
			p = new TQueen(s);
			break;
		case K:
			p = new TKing(s);
			break;
		default:
			throw new AssertionError("Not yet impl");
		}
		return p;
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

	public static TPieceType fromId(final byte id)
	{
		return TPieceType.values()[id % 6];
	}
}
