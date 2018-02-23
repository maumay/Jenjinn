package jenjinn.engine.evaluation;

import jenjinn.engine.boardstate.BoardState;

/**
 * Simple interface representing a piece square table which integrates PIECE
 * VALUES into the table as well as bonuses/penalties for positions. This means
 * we don't have to calculate material values separately. The scores are
 * designed to be stored in {@link BoardState} instances and updated
 * incrementally during evolution.
 *
 * @author ThomasB
 * @since 28 Jul 2017
 */
public interface PieceSquareTable
{
	short getPieceSquareValue(byte pieceIndex, byte squareIndex);
}
