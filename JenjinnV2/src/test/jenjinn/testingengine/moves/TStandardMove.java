/**
 *
 */
package jenjinn.testingengine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.CastlingRights;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.King;
import jenjinn.engine.pieces.Pawn;
import jenjinn.testingengine.boardstate.TBoardState;

/**
 * @author t
 *
 */
public class TStandardMove extends TAbstractChessMove
{
	public static TStandardMove get(final int start, final int target)
	{
		return new TStandardMove(start, target);
	}

	/**
	 * @param type
	 * @param start
	 * @param target
	 */
	private TStandardMove(final int start, final int target)
	{
		super(MoveType.STANDARD, start, target);
	}

	/* (non-Javadoc)
	 *
	 * @see jenjinn.engine.moves.ChessMove#evolve(jenjinn.engine.boardstate.BoardState) */
	@Override
	public BoardState evolve(final BoardState state)
	{
		final ChessPiece movingPiece = state.getPieceAt(getStart(), state.getFriendlySide());
		final ChessPiece removedPiece = state.getPieceAt(getTarget(), state.getEnemySide());

		assert !(removedPiece instanceof King);

		// Update metadata -----------------------------------------
		final byte newCastleRights = updateCastleRights(state.getCastleRights());
		final byte newEnPassantSquare = getNewEnPassantSquare(movingPiece);
		final byte newClockValue = getNewClockValue(movingPiece, removedPiece, state.getClockValue());

		// -----------------------------------------------------------

		// Update locations -----------------------------------------
		final long start = getStartBB(), target = getTargetBB();
		final long[] newPieceLocations = state.getPieceLocationsCopy();
		newPieceLocations[movingPiece.getIndex()] &= ~start;
		newPieceLocations[movingPiece.getIndex()] |= target;
		if (removedPiece != null)
		{
			newPieceLocations[removedPiece.getIndex()] &= ~target;
		}
		// -----------------------------------------------------------
		final long newDevStatus = state.getDevelopmentStatus() & ~start;
		return new TBoardState(
				state.getFriendlySide().otherSide(),
				newPieceLocations,
				newCastleRights,
				state.getCastleStatus(),
				newDevStatus,
				newEnPassantSquare,
				newClockValue);
	}

	/* These three methods will be tested separately! */
	public final byte getNewClockValue(final ChessPiece movingPiece, final ChessPiece removedPiece, final byte oldClockValue)
	{
		if (removedPiece != null || movingPiece instanceof Pawn)
		{
			return 0;
		}
		return (byte) (oldClockValue + 1);
	}

	public final byte getNewEnPassantSquare(final ChessPiece movingPiece)
	{
		if (movingPiece instanceof Pawn && Math.abs(getTarget() - getStart()) == 16)
		{
			return (byte) (getStart() + Math.signum(getTarget() - getStart()) * 8);
		}
		return BoardState.NO_ENPASSANT;
	}

	public final byte updateCastleRights(byte oldRights)
	{
		if (oldRights > 0)
		{
			if (CastlingRights.STANDARD_MOVE_ERASURES.containsKey(getStart()))
			{
				oldRights &= ~CastlingRights.STANDARD_MOVE_ERASURES.get(getStart());
			}
			if (CastlingRights.STANDARD_MOVE_ERASURES.containsKey(getTarget()))
			{
				oldRights &= ~CastlingRights.STANDARD_MOVE_ERASURES.get(getTarget());
			}
		}
		return oldRights;
	}

	@Override
	public String toString()
	{
		return "S" + "[" + Sq.get(getStart()).name() + ", " + Sq.get(getTarget()).name() + "]";
	}
}
