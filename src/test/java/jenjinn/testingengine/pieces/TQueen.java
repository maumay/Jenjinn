package jenjinn.testingengine.pieces;

import java.util.Arrays;
import java.util.stream.Collectors;

import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;
import jenjinn.engine.pieces.PieceType;

/**
 * @author ThomasB
 * @since 19 Sep 2017
 */
public class TQueen extends TChessPiece
{
	/**
	 * @param type
	 * @param side
	 * @param moveDirs
	 */
	public TQueen(final Side side)
	{
		super(PieceType.Q, side, Arrays.stream(Direction.values()).filter(x -> x.name().length() < 3).collect(
				Collectors.toList()));
		assert movementDirections.size() == 8;
	}

	@Override
	public long getStartBitboard()
	{
		return 0b10000L << 56 * (getSide().isWhite() ? 0 : 1);
	}
}
