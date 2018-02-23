package jenjinn.testingengine.pieces;

import java.util.Arrays;
import java.util.stream.Collectors;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.pieces.PieceType;

/**
 * @author ThomasB
 * @since 19 Sep 2017
 */
public class TKnight extends TChessPiece
{

	/**
	 * @param type
	 * @param side
	 * @param moveDirs
	 */
	public TKnight(final Side side)
	{
		super(PieceType.N, side, Arrays.stream(Direction.values()).filter(x -> x.name().length() == 3).collect(
				Collectors.toList()));

		assert movementDirections.size() == 8;
	}

	@Override
	public long getAttackset(final byte loc, final long occupiedSquares)
	{
		long sup = super.getAttackset(loc, occupiedSquares);
		for (int i = 0; i < 8; i++) {
			if (Math.abs((loc / 8) - i) > 2) {
				sup &= ~BBDB.RNK[i];
			}
			if (Math.abs((7 - (loc % 8)) - i) > 2) {
				sup &= ~BBDB.FILE[i];
			}
		}
		return sup;
	}

	@Override
	public long getStartBitboard()
	{
		return 0b1000010L << 56 * (getSide().isWhite() ? 0 : 1);
	}

	public static void main(final String[] args)
	{
		EngineUtils.printNbitBoards((new TKnight(Side.W).getMoveset((byte) 56, 0L, 0L)));
	}
}
