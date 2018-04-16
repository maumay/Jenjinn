package jenjinn.engine.moves;

import static jenjinn.engine.boardstate.BoardStateConstants.getEndGamePST;
import static jenjinn.engine.boardstate.BoardStateConstants.getMiddleGamePST;
import static jenjinn.engine.boardstate.BoardStateConstants.getStateHasher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImpl;
import jenjinn.engine.boardstate.CastlingRights;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Side;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public final class CastleMove extends AbstractChessMove
{
	// INSTANCES
	public static final CastleMove WHITE_KINGSIDE = new CastleMove(3, 1, new int[] { 3, 1, 0, 2 });

	public static final CastleMove WHITE_QUEENSIDE = new CastleMove(3, 5, new int[] { 3, 5, 7, 4 });

	public static final CastleMove BLACK_KINGSIDE = new CastleMove(59, 57, new int[] { 59, 57, 56, 58 });

	public static final CastleMove BLACK_QUEENSIDE = new CastleMove(59, 61, new int[] { 59, 61, 63, 60 });

	private static final List<CastleMove> ALL = Arrays.asList(WHITE_KINGSIDE, WHITE_QUEENSIDE, BLACK_KINGSIDE,
			BLACK_QUEENSIDE);

	public static final List<CastleMove> ALL_INSTANCES = Collections.unmodifiableList(
			Arrays.asList(WHITE_KINGSIDE, WHITE_QUEENSIDE, BLACK_KINGSIDE, BLACK_QUEENSIDE));

	public static CastleMove get(final String name)
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

	public static CastleMove get(final int i)
	{
		assert 0 <= i && i < 5;
		return ALL.get(i);
	}

	// ------------------------------------------------------------------------------------------------
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

	private CastleMove(final int start, final int target, final int[] requiredSquares)
	{
		super(MoveType.CASTLE, start, target);

		assert requiredSquares.length == 4;

		kingRemovalSquare = (byte) requiredSquares[0];
		kingAdditionSquare = (byte) requiredSquares[1];
		rookRemovalSquare = (byte) requiredSquares[2];
		rookAdditionSquare = (byte) requiredSquares[3];
	}

	public final Side getMoveSide()
	{
		return kingRemovalSquare / 8 == 0 ? Side.W : Side.B;
	}

	public final boolean isKingside()
	{
		return rookRemovalSquare % 8 == 0;
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		final Side moveSide = getMoveSide();

		// Update piece locations-----------------------------------------
		final long[] newPiecePositions = state.getPieceLocationsCopy();

		newPiecePositions[5 + moveSide.index()] ^= (1L << kingRemovalSquare);
		newPiecePositions[5 + moveSide.index()] |= (1L << kingAdditionSquare);

		newPiecePositions[3 + moveSide.index()] ^= (1L << rookRemovalSquare);
		newPiecePositions[3 + moveSide.index()] |= (1L << rookAdditionSquare);
		// ----------------------------------------------------------------

		// Update metadata------------------------------------------------
		final byte newCastleRights = updateCastleRights(state.getCastleRights(), moveSide);
		final byte newCastleStatus = updateCastleStatus(state.getCastleStatus(), moveSide);

		long newHash = updateGeneralHashFeatures(state, newCastleRights, BoardState.NO_ENPASSANT);
		newHash ^= getStateHasher().getSquarePieceFeature(kingAdditionSquare, ChessPiece.get(5 + moveSide.index()));
		newHash ^= getStateHasher().getSquarePieceFeature(kingRemovalSquare, ChessPiece.get(5 + moveSide.index()));
		newHash ^= getStateHasher().getSquarePieceFeature(rookAdditionSquare, ChessPiece.get(3 + moveSide.index()));
		newHash ^= getStateHasher().getSquarePieceFeature(rookRemovalSquare, ChessPiece.get(3 + moveSide.index()));
		// -----------------------------------------------------------------

		// Update positional evaluation------------------------------------

		short midPosEval = state.getMidgamePositionalEval(), endPosEval = state.getEndgamePositionalEval();

		midPosEval += getMiddleGamePST().getPieceSquareValue((byte) (5 + moveSide.index()), kingAdditionSquare);
		midPosEval -= getMiddleGamePST().getPieceSquareValue((byte) (5 + moveSide.index()), kingRemovalSquare);

		endPosEval += getEndGamePST().getPieceSquareValue((byte) (5 + moveSide.index()), kingAdditionSquare);
		endPosEval -= getEndGamePST().getPieceSquareValue((byte) (5 + moveSide.index()), kingRemovalSquare);

		midPosEval += getMiddleGamePST().getPieceSquareValue((byte) (3 + moveSide.index()), rookAdditionSquare);
		midPosEval -= getMiddleGamePST().getPieceSquareValue((byte) (3 + moveSide.index()), rookRemovalSquare);

		endPosEval += getEndGamePST().getPieceSquareValue((byte) (3 + moveSide.index()), rookAdditionSquare);
		endPosEval -= getEndGamePST().getPieceSquareValue((byte) (3 + moveSide.index()), rookRemovalSquare);

		// -----------------------------------------------------------------

		return new BoardStateImpl(
				state.getNewRecentHashings(newHash),
				1 - state.getFriendlySideValue(),
				newCastleRights,
				newCastleStatus,
				BoardState.NO_ENPASSANT,
				state.getClockValue() + 1,
				state.getPiecePhase(),
				midPosEval,
				endPosEval,
				state.getDevelopmentStatus(),
				newPiecePositions);
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

	@Override
	public String toCompactString()
	{
		return "" + getType().id + ChessMove.SEPARATOR + ALL.indexOf(this);
	}

}