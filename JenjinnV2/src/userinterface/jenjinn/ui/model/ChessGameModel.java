/**
 *
 */
package jenjinn.ui.model;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 *
 *         Interface representing a UI model for a chess game. It needs to be able to process user
 *         clicks and fire update alerts.
 */
public interface ChessGameModel
{
	void processUserClick(final Sq clickedSquare);

	void fireDisplayUpdate(final int stateIndex) throws IndexOutOfBoundsException;

	List<BoardState> getGameStatesCopy();

	default void fireDisplayUpdate()
	{
		try
		{
			final List<BoardState> gameStates = getGameStatesCopy();
			fireDisplayUpdate(gameStates.size() - 1);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	default List<BoardOccupancy> getOccupancyInfo(final BoardState state)
	{
		final List<BoardOccupancy> occ = new ArrayList<>();

		int ctr = 0;
		for (final long locs : state.getPieceLocationsCopy())
		{
			final ChessPiece p = ChessPiece.get(ctr++);
			for (final byte loc : EngineUtils.getSetBits(locs))
			{
				occ.add(new BoardOccupancy(p, loc));
			}
		}
		return occ;
	}
}
