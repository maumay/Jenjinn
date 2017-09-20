/**
 * 
 */
package jenjinn.testingengine.moves;

import static jenjinn.engine.boardstate.BoardState.END_TABLE;
import static jenjinn.engine.boardstate.BoardState.MID_TABLE;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.CastlingRights;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.moves.StandardMove;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.King;
import jenjinn.engine.pieces.Pawn;

/**
 * @author t
 *
 */
public class TStandardMove extends TAbstractChessMove 
{
	private final StandardMove matchingMove;

	/**
	 * @param type
	 * @param start
	 * @param target
	 */
	public TStandardMove(int start, int target) 
	{
		super(MoveType.STANDARD, start, target);
		matchingMove = StandardMove.get(start, target);
	}

	/* (non-Javadoc)
	 * @see jenjinn.engine.moves.ChessMove#evolve(jenjinn.engine.boardstate.BoardState)
	 */
	@Override
	public BoardState evolve(BoardState state) 
	{
		final ChessPiece movingPiece = state.getPieceAt(getStart(), state.getFriendlySide());
		final ChessPiece removedPiece = state.getPieceAt(getTarget(), state.getEnemySide());

		assert !(removedPiece instanceof King);

		// Update metadata -----------------------------------------
		final byte newCastleRights = updateCastleRights(state.getCastleRights());
		final byte newEnPassantSquare = getNewEnPassantSquare(movingPiece);
		final byte newClockValue = getNewClockValue(movingPiece, removedPiece, state.getClockValue());

		//-----------------------------------------------------------

		// Update locations -----------------------------------------
		final long start = getStartBB(), target = getTargetBB();
		final long[] newPieceLocations = state.getPieceLocationsCopy();
		newPieceLocations[movingPiece.getIndex()] &= ~start;
		newPieceLocations[movingPiece.getIndex()] |= target;
		if (removedPiece != null)
		{
			newPieceLocations[removedPiece.getIndex()] &= ~target;
		}
		//-----------------------------------------------------------
		final long newDevStatus = state.getDevelopmentStatus() & ~start;
		return null;
	}
	
	/*
	 * These three methods will be tested separately!
	 */
	public final byte getNewClockValue(final ChessPiece movingPiece, final ChessPiece removedPiece, final byte oldClockValue)
	{
		return matchingMove.getNewClockValue(movingPiece, removedPiece, oldClockValue);
	}

	public final byte getNewEnPassantSquare(final ChessPiece movingPiece)
	{
		return matchingMove.getNewEnPassantSquare(movingPiece);
	}

	public final byte updateCastleRights(byte oldRights)
	{
		return matchingMove.updateCastleRights(oldRights);
	}
	
	@Override
	public String toString()
	{
		return "S" + "[" + Sq.getSq(getStart()).name() + ", " + Sq.getSq(getTarget()).name() + "]";
	}

}
