/**
 *
 */
package jenjinn.ui.controller;

import java.util.List;

import jenjinn.engine.enums.Sq;
import jenjinn.ui.chessboard.ChessBoard;
import jenjinn.ui.model.BoardOccupancy;
import jenjinn.ui.model.ChessGameModel;

/**
 * @author ThomasB
 *
 */
public final class ChessGameController
{
	private ChessGameModel model;
	private final ChessBoard board;

	public ChessGameController(final ChessBoard board)
	{
		this.board = board;
		board.setController(this);
	}


	public void processUserClick(final Sq squareClicked)
	{
		try
		{
			model.processUserClick(squareClicked);
		}
		catch (final NullPointerException npe)
		{
			System.err.println("No model is registered to the controller.");
		}
	}

	public void updateBoardVisuals(final List<BoardOccupancy> piecePlacementInfo)
	{
		try
		{
			board.setPieceLocations(piecePlacementInfo);
			board.redrawPieces();
		}
		catch (final NullPointerException npe)
		{
			System.err.println("No chessboard is registered to the controller.");
		}
	}

	public void clearMovementMarkers()
	{
		try
		{
			board.clearMovementMarkers();
		}
		catch (final NullPointerException npe)
		{
			System.err.println("No chessboard is registered to the controller.");
		}
	}

	public void updateBoardMovementMarkerUpdateAlert(final Sq loc, final List<Sq> movementSquares, final List<Sq> attackSquares)
	{
		try
		{
			board.getMarkers().set(loc, movementSquares, attackSquares);
			board.redrawMarkers();
		}
		catch (final NullPointerException npe)
		{
			System.err.println("No chessboard is registered to the controller.");
		}
	}

	public void setModel(final ChessGameModel model)
	{
		this.model = model;
	}
	//
	//	public void setChessboard(final ChessBoard board)
	//	{
	//		this.board = board;
	//	}

	public void setUserInteractionLocked(final boolean locked)
	{
		try
		{
			board.setUserInterationDisabled(locked);
		}
		catch (final NullPointerException npe)
		{
			System.err.println("No chessboard is registered to the controller.");
		}
	}
}
