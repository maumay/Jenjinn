package jenjinn.engine.moves;

import static jenjinn.engine.boardstate.BoardState.END_TABLE;
import static jenjinn.engine.boardstate.BoardState.MID_TABLE;

import java.util.EnumSet;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImpl;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.PieceType;

/**
 * @author ThomasB
 * @since 21 Jul 2017
 */
public class PromotionMove extends AbstractChessMove
{
	private PieceType toPromoteTo;

	public static PromotionMove get(final int start, final int target, final PieceType toPromoteTo)
	{
		return new PromotionMove(start, target, toPromoteTo);
	}

	public static PromotionMove get(final Sq start, final Sq target, final PieceType toPromoteTo)
	{
		return get(start.ordinal(), target.ordinal(), toPromoteTo);
	}

	/**
	 * @param type
	 * @param start
	 * @param target
	 */
	private PromotionMove(final int start, final int target, final PieceType toPromoteTo)
	{
		super(MoveType.PROMOTION, start, target);
		assert !EnumSet.of(PieceType.K, PieceType.P).contains(toPromoteTo);
		this.toPromoteTo = toPromoteTo;
	}

	@Override
	public BoardState evolve(final BoardState state)
	{
		final Side friendlySide = state.getFriendlySide();
		final int newPieceIndex = friendlySide.index() + toPromoteTo.getId();

		final ChessPiece removedPiece = state.getPieceAt(getTarget(), friendlySide.otherSide());

		// Update piece locations ---------------------------------------------
		final long[] newPieceLocations = state.getPieceLocationsCopy();

		newPieceLocations[friendlySide.index()] ^= getStartBB();
		newPieceLocations[newPieceIndex] |= getTargetBB();
		// ---------------------------------------------------------------------

		// Update metadata ----------------------------------------------------
		long newHash = updateGeneralHashFeatures(state, state.getCastleRights(), BoardState.NO_ENPASSANT);
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getStart(), ChessPiece.get(friendlySide.index()));
		newHash ^= BoardState.HASHER.getSquarePieceFeature(getTarget(), ChessPiece.get(newPieceIndex));
		// ---------------------------------------------------------------------

		// Update positional eval ---------------------------------------------
		short midPosEval = state.getMidgamePositionalEval(), endPosEval = state.getEndgamePositionalEval();

		midPosEval += MID_TABLE.getPieceSquareValue((byte) (newPieceIndex), getTarget());
		midPosEval -= MID_TABLE.getPieceSquareValue((friendlySide.index()), getStart());

		endPosEval += END_TABLE.getPieceSquareValue((byte) (newPieceIndex), getTarget());
		endPosEval -= END_TABLE.getPieceSquareValue((friendlySide.index()), getStart());

		// ---------------------------------------------------------------------

		byte oldPiecePhase = state.getPiecePhase();

		if (removedPiece != null) {
			newPieceLocations[removedPiece.index()] ^= getTargetBB();
			newHash ^= BoardState.HASHER.getSquarePieceFeature(getTarget(), ChessPiece.get(removedPiece.index()));
			oldPiecePhase = updatePiecePhase(oldPiecePhase, removedPiece);

			midPosEval -= MID_TABLE.getPieceSquareValue(removedPiece.index(), getTarget());
			endPosEval -= END_TABLE.getPieceSquareValue(removedPiece.index(), getTarget());
		}

		return new BoardStateImpl(
				state.getNewRecentHashings(newHash),
				1 - state.getFriendlySideValue(),
				state.getCastleRights(),
				state.getCastleStatus(),
				BoardState.NO_ENPASSANT,
				0,
				Math.max(0, oldPiecePhase - PIECE_PHASES[newPieceIndex % 6]),
				midPosEval,
				endPosEval,
				state.getDevelopmentStatus(),
				newPieceLocations);
	}

	@Override
	public String toString()
	{
		return "P" + "[" + Sq.get(getStart()).name() + ", " + Sq.get(getTarget()).name() + "]";
	}

	@Override
	public String toCompactString()
	{
		final StringBuilder sb = new StringBuilder(super.toCompactString());
		sb.append(ChessMove.SEPARATOR);
		sb.append(toPromoteTo.name());
		return sb.toString();
	}
}
