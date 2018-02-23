package jenjinn.testingengine.pieces;

import java.util.Arrays;

import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;
import jenjinn.engine.pieces.PieceType;

/**
 * @author ThomasB
 * @since 19 Sep 2017
 */
public class TBishop extends TChessPiece
{

	/**
	 * @param type
	 * @param side
	 * @param moveDirs
	 */
	public TBishop(final Side side)
	{
		super(PieceType.B, side, Arrays.asList(Direction.NE, Direction.NW, Direction.SE, Direction.SW));
	}

	@Override
	public long getStartBitboard()
	{
		return 0b100100L << 56 * (getSide().isWhite() ? 0 : 1);
	}
}
