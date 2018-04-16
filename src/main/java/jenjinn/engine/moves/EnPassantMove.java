package jenjinn.engine.moves;

import static java.lang.Math.signum;
import static jenjinn.engine.boardstate.BoardStateConstants.getEndGamePST;
import static jenjinn.engine.boardstate.BoardStateConstants.getMiddleGamePST;
import static jenjinn.engine.boardstate.BoardStateConstants.getStateHasher;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImpl;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.Pawn;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public class EnPassantMove extends AbstractChessMove
{
	/**
	 * EnPassant moves are so rare that I don't think we really need to cache them.
	 *
	 * @param start
	 * @param target
	 * @return
	 */
	public static EnPassantMove get(final int start, final int target)
	{
		return new EnPassantMove(start, target);
	}

	public static EnPassantMove get(final Sq start, final Sq targ)
	{
		return get(start.ordinal(), targ.ordinal());
	}

	private EnPassantMove(final int start, final int target)
	{
		super(MoveType.ENPASSANT, start, target);
	}

	public final byte getEnPassantSquare()
	{
		return (byte) (getTarget() - signum(getTarget() - getStart()) * 8);
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		assert state.getPieceAt(getEnPassantSquare(), state.getEnemySide()) instanceof Pawn;

		final Side friendlySide = state.getFriendlySide();

		// Update piece locations ---------------------------------------
		final long enPassantSquareBB = 1L << getEnPassantSquare();

		final long[] newPieceLocations = state.getPieceLocationsCopy();

		newPieceLocations[friendlySide.index()] ^= getStartBB();
		newPieceLocations[friendlySide.index()] |= getTargetBB();
		newPieceLocations[friendlySide.otherSide().index()] ^= enPassantSquareBB;
		// ---------------------------------------------------------------

		// Update metadata ----------------------------------------------
		long newHash = updateGeneralHashFeatures(state, state.getCastleRights(), BoardState.NO_ENPASSANT);
		newHash ^= getStateHasher().getSquarePieceFeature(getStart(), ChessPiece.get(friendlySide.index()));
		newHash ^= getStateHasher().getSquarePieceFeature(getTarget(), ChessPiece.get(friendlySide.index()));
		newHash ^= getStateHasher().getSquarePieceFeature(getEnPassantSquare(), ChessPiece.get(friendlySide.otherSide().index()));
		// ---------------------------------------------------------------

		// Update positional evaluations --------------------------------
		short midPosEval = state.getMidgamePositionalEval(), endPosEval = state.getEndgamePositionalEval();

		midPosEval += getMiddleGamePST().getPieceSquareValue((friendlySide.index()), getTarget());
		midPosEval -= getMiddleGamePST().getPieceSquareValue((friendlySide.index()), getStart());

		endPosEval += getEndGamePST().getPieceSquareValue((friendlySide.index()), getTarget());
		endPosEval -= getEndGamePST().getPieceSquareValue((friendlySide.index()), getStart());

		midPosEval -= getMiddleGamePST().getPieceSquareValue((friendlySide.otherSide().index()), getEnPassantSquare());
		endPosEval -= getEndGamePST().getPieceSquareValue((friendlySide.otherSide().index()), getEnPassantSquare());
		// ---------------------------------------------------------------

		return new BoardStateImpl(
				state.getNewRecentHashings(newHash),
				1 - state.getFriendlySideValue(),
				state.getCastleRights(),
				state.getCastleStatus(),
				BoardState.NO_ENPASSANT,
				0,
				state.getPiecePhase(),
				midPosEval,
				endPosEval,
				state.getDevelopmentStatus(),
				newPieceLocations);
	}

	@Override
	public String toString()
	{
		return "E" + "[" + Sq.get(getStart()).name() + ", " + Sq.get(getTarget()).name() + "]";
	}
}
