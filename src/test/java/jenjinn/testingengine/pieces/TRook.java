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
public class TRook extends TChessPiece
{
	/**
	 * @param type
	 * @param side
	 * @param moveDirs
	 */
	public TRook(final Side side)
	{
		super(PieceType.R, side, Arrays.stream(Direction.values()).filter(x -> x.name().length() == 1).collect(
				Collectors.toList()));
		assert movementDirections.size() == 4;
	}

	@Override
	public long getStartBitboard()
	{
		return 0b10000001L << 56 * (getSide().isWhite() ? 0 : 1);
	}
}
