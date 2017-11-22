/**
 * Written by Tom Ball 2017.
 *
 * This code is unlicensed but please don't plagiarize.
 */

package jenjinn.engine.pieces;

import jenjinn.engine.enums.Side;

/**
 * @author TB
 * @date 1 Dec 2016
 *
 *       An enumeration of piece types.
 */
public enum PieceType
{
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
		switch (this)
		{
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

	//	public TChessPiece generateTPiece(final Side s)
	//	{
	//		TChessPiece p = null;
	//		switch (this)
	//		{
	//			case P:
	//				p = new TPawn(s);
	//				break;
	//			case B:
	//				p = new TBishop(s);
	//				break;
	//			case N:
	//				p = new TKnight(s);
	//				break;
	//			case R:
	//				p = new TRook(s);
	//				break;
	//			case Q:
	//				p = new TQueen(s);
	//				break;
	//			case K:
	//				p = new TKing(s);
	//				break;
	//			default:
	//				throw new AssertionError("Not yet impl");
	//		}
	//		return p;
	//	}

	public static PieceType fromId(final byte id)
	{
		return PieceType.values()[id % 6];
	}
	//
	// public TestingChessPiece generateTestPiece(final Side s)
	// {
	// TestingChessPiece p = null;
	// switch (this)
	// {
	// case P:
	// p = new TestingPawn(s);
	// break;
	// case B:
	// p = new TestingBishop(s);
	// break;
	// case N:
	// p = new TestingKnight(s);
	// break;
	// case R:
	// p = new TestingRook(s);
	// break;
	// case Q:
	// p = new TestingQueen(s);
	// break;
	// case K:
	// p = new TestingKing(s);
	// break;
	// }
	// return p;
	// }

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
