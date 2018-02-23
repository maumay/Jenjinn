package jenjinn.testingengine.pieces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.PieceType;

/**
 * @author ThomasB
 * @since 19 Sep 2017
 */
public class TPawn extends TChessPiece
{
	final List<Direction> attackDirections;

	/**
	 * @param type
	 * @param side
	 * @param value
	 * @param moveDirs
	 */
	public TPawn(final Side side)
	{
		super(PieceType.P, side, Arrays.asList(side.isWhite() ? Direction.N : Direction.S));
		attackDirections = Collections.unmodifiableList(
				side.isWhite() ? Arrays.asList(Direction.NE, Direction.NW) : Arrays.asList(Direction.SE, Direction.SW));
	}

	@Override
	public long getAttackset(final byte loc, final long occupiedSquares)
	{
		final List<Sq> attcks = new ArrayList<>();
		final Sq start = Sq.get(loc);
		attackDirections.stream().forEach(x -> {
			final Sq next = start.getNextSqInDirection(x);
			if (next != null) {
				attcks.add(next);
			}
		});
		return bbFromSqs(attcks);
	}

	/**
	 * Just the same as usual pawn impl since we don't need magic bb
	 */
	@Override
	public long getMoveset(final byte loc, final long friendlyPieces, final long enemyPieces)
	{
		final long attck = getAttackset(loc, enemyPieces | friendlyPieces) & enemyPieces;
		long push = (1L << (loc + getSide().orientation() * 8)) & ~(friendlyPieces | enemyPieces);

		if (inFirstMoveZone(loc) && push != 0) {
			push |= ((1L << (loc + getSide().orientation() * 16)) & ~(friendlyPieces | enemyPieces));
		}
		return attck | push;
	}

	private boolean inFirstMoveZone(final byte loc)
	{
		return isWhite() ? loc < 16 : 47 < loc;
	}

	@Override
	public long getStartBitboard()
	{
		return 0b1111111100000000L << 40 * (getSide().isWhite() ? 0 : 1);
	}

}
