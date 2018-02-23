package jenjinn.testingengine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.CastlingRights;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Side;
import jenjinn.testingengine.boardstate.TBoardState;
import jenjinn.testingengine.enums.CastleArea;

/**
 * @author ThomasB
 * @since 20 Sep 2017
 */
public class TCastleMove extends TAbstractChessMove
{
	// INSTANCES
	public static final TCastleMove WHITE_KINGSIDE = new TCastleMove(3, 1, new int[] { 3, 1, 0, 2 });

	public static final TCastleMove WHITE_QUEENSIDE = new TCastleMove(3, 5, new int[] { 3, 5, 7, 4 });

	public static final TCastleMove BLACK_KINGSIDE = new TCastleMove(59, 57, new int[] { 59, 57, 56, 58 });

	public static final TCastleMove BLACK_QUEENSIDE = new TCastleMove(59, 61, new int[] { 59, 61, 63, 60 });

	public static TCastleMove get(final String name)
	{
		switch (name) {
		case "WHITE_KINGSIDE":
			return WHITE_KINGSIDE;
		case "WHITE_QUEENSIDE":
			return WHITE_QUEENSIDE;
		case "BLACK_KINGSIDE":
			return BLACK_KINGSIDE;
		case "BLACK_QUEENSIDE":
			return BLACK_QUEENSIDE;
		default:
			throw new IllegalArgumentException();
		}
	}

	public static TCastleMove get(final CastleArea area)
	{
		switch (area) {
		case W_K:
			return WHITE_KINGSIDE;
		case W_Q:
			return WHITE_QUEENSIDE;
		case B_K:
			return BLACK_KINGSIDE;
		case B_Q:
			return BLACK_QUEENSIDE;
		default:
			throw new RuntimeException("Not yet impl");
		}
	}

	/**
	 * The bit indices representing the 'from' and 'to' squares for the king in this
	 * castle move.
	 */
	private final byte kingRemovalSquare, kingAdditionSquare;

	/**
	 * The bit indices representing the 'from' and 'to' squares for the rook in this
	 * castle move.
	 */
	private final byte rookRemovalSquare, rookAdditionSquare;

	/**
	 * @param type
	 * @param start
	 * @param target
	 */
	public TCastleMove(final int start, final int target, final int[] requiredSquares)
	{
		super(MoveType.CASTLE, start, target);

		assert requiredSquares.length == 4;

		kingRemovalSquare = (byte) requiredSquares[0];
		kingAdditionSquare = (byte) requiredSquares[1];
		rookRemovalSquare = (byte) requiredSquares[2];
		rookAdditionSquare = (byte) requiredSquares[3];
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		final Side moveSide = getMoveSide();
		// Update piece locations-----------------------------------------
		final long[] newPiecePositions = state.getPieceLocationsCopy();

		newPiecePositions[5 + moveSide.index()] &= ~(1L << kingRemovalSquare);
		newPiecePositions[5 + moveSide.index()] |= (1L << kingAdditionSquare);

		newPiecePositions[3 + moveSide.index()] &= ~(1L << rookRemovalSquare);
		newPiecePositions[3 + moveSide.index()] |= (1L << rookAdditionSquare);
		// ----------------------------------------------------------------

		// Update metadata------------------------------------------------
		final byte newCastleRights = updateCastleRights(state.getCastleRights(), moveSide);
		final byte newCastleStatus = updateCastleStatus(state.getCastleStatus(), moveSide);

		return new TBoardState(
				moveSide.otherSide(),
				newPiecePositions,
				newCastleRights,
				newCastleStatus,
				state.getDevelopmentStatus(),
				BoardState.NO_ENPASSANT,
				(byte) (state.getClockValue() + 1),
				state.getHashes());
	}

	public final Side getMoveSide()
	{
		return kingRemovalSquare / 8 == 0 ? Side.W : Side.B;
	}

	public final boolean isKingside()
	{
		return rookRemovalSquare % 8 == 0;
	}

	public final byte updateCastleRights(final byte oldRights, final Side moveSide)
	{
		final byte sideShift = (byte) (moveSide.isWhite() ? 0 : 2);
		final int toMovesRights = CastlingRights.VALUES[sideShift] | CastlingRights.VALUES[sideShift + 1];
		return (byte) (oldRights & ~toMovesRights);
	}

	public byte updateCastleStatus(final byte oldStatus, final Side moveSide)
	{
		final byte sideShift = (byte) (moveSide.isWhite() ? 0 : 2);
		final int kingsideShift = (byte) (isKingside() ? 0 : 1);
		return (byte) (oldStatus | CastlingRights.VALUES[sideShift + kingsideShift]);
	}

	@Override
	public String toString()
	{
		return getMoveSide().name() + "_" + (isKingside() ? "KINGSIDE" : "QUEENSIDE");
	}
}
