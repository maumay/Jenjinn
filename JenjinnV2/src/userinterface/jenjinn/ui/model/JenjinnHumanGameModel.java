/**
 *
 */
package jenjinn.ui.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.entity.Jenjinn;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.moves.ChessMove;
import jenjinn.ui.controller.ChessGameController;

/**
 * @author ThomasB
 *
 */
public class JenjinnHumanGameModel implements ChessGameModel
{
	private static final String REPORT_FOLDER = "matchreports/";

	private static final String DEFAULT_DB = "testdb.txt";

	private ChessGameController gameController;

	private List<BoardState> gameStates;
	private List<ChessMove> movesPlayed;
	private Jenjinn jenjinn;
	private StoredUserSelection userSelection;

	private JenjinnHumanGameModel()
	{
	}

	public static JenjinnHumanGameModel createNewModel(final Side humanSide)
	{
		final JenjinnHumanGameModel newModel = new JenjinnHumanGameModel();
		newModel.gameStates = new ArrayList<>(Arrays.asList(BoardStateImplV2.getStartBoard()));
		newModel.movesPlayed = new ArrayList<>();
		newModel.userSelection = new StoredUserSelection();
		newModel.jenjinn = new Jenjinn(humanSide.otherSide(), 4, BoardEvaluator.getDefault(), DEFAULT_DB);
		newModel.gameController = ChessGameController.getInstance();

		if (humanSide == Side.B)
		{
			newModel.performAiMove();
		}

		return newModel;
	}

	private void processMove(final ChessMove chosenMove)
	{
		final BoardState currentState = getPresentGameState();
		movesPlayed.add(chosenMove);
		gameStates.add(chosenMove.evolve(currentState));
		System.out.println("User: " + getPresentGameState().getHashing());
		userSelection.reset();
		fireDisplayUpdate();
		performAiMove();
	}

	@Override
	public void processUserClick(final Sq userClick)
	{
		if (!userSelection.selectionIsSet())
		{
			processFirstClick(userClick);
		}
		else
		{
			processSecondClick(userClick);
		}
	}

	private void processSecondClick(final Sq userClick)
	{
		disableUserInteraction();
		final ChessMove chosenMove = getIndicatedMove(userClick);
		final boolean moveIsValid = chosenMove != null;

		if (moveIsValid)
		{
			gameController.clearMovementMarkers();
			processMove(chosenMove);
		}
		else
		{
			gameController.clearMovementMarkers();
			userSelection.reset();
			processFirstClick(userClick);
			enableUserInteraction();
		}
	}

	private ChessMove getIndicatedMove(final Sq secondClick)
	{
		ChessMove indicatedMove = null;
		final Sq firstClick = userSelection.getStoredSquare();
		final List<ChessMove> possibleMoves = getPresentGameState().getMoves();

		for (final ChessMove possibleMove : possibleMoves)
		{
			if (possibleMove.matches(firstClick, secondClick))
			{
				indicatedMove = possibleMove;
				break;
			}
		}
		return indicatedMove;
	}

	private void processFirstClick(final Sq userClick)
	{
		if (isValidFirstClick(userClick))
		{
			userSelection.setStoredSquare(userClick);
			fireBoardMovementMarkerUpdateAlert((byte) userClick.ordinal());
		}
	}

	private void fireBoardMovementMarkerUpdateAlert(final byte loc)
	{
		final BoardState presentState = getPresentGameState();
		final Side enemy = presentState.getFriendlySide().otherSide();

		final List<Sq> moveSquares = new ArrayList<>();
		final List<Sq> attackSquares = new ArrayList<>();

		for (final ChessMove mv : presentState.getMoves())
		{
			if (mv.matchesStart(Sq.get(loc)))
			{
				if (presentState.getPieceAt(mv.getTarget(), enemy) != null)
				{
					attackSquares.add(Sq.get(mv.getTarget()));
				}
				else
				{
					moveSquares.add(Sq.get(mv.getTarget()));
				}
			}
		}
		gameController.updateBoardMovementMarkerUpdateAlert(Sq.get(loc), moveSquares, attackSquares);
	}

	/**
	 * Checks whether a given square on a chessboard is valid to initiate the possible generation
	 * of a legal move for the present state in this game model.
	 */
	private boolean isValidFirstClick(final Sq firstClick)
	{
		final BoardState presentState = getPresentGameState();
		final long friendlyPieces = presentState.getSideLocations(presentState.getFriendlySide());
		return (friendlyPieces & firstClick.getAsBB()) != 0;
	}

	@Override
	public List<BoardState> getGameStatesCopy()
	{
		return new ArrayList<>(gameStates);
	}

	public BoardState getPresentGameState()
	{
		return gameStates.get(gameStates.size() - 1);
	}

	public void performAiMove()
	{
		(new Thread(() ->
		{
			synchronized (gameStates)
			{
				final BoardState presentState = getPresentGameState();
				final ChessMove jenjinnMove = jenjinn.calculateBestMove(presentState);
				gameStates.add(jenjinnMove.evolve(presentState));
				movesPlayed.add(jenjinnMove);
//				System.out.println(getPresentGameState().getMidgamePositionalEval());
//				System.out.println(getPresentGameState().getMidgamePositionalEval());
//				System.out.println(evalPiecePositions(getPresentGameState()));
				System.out.println(getPresentGameState().getHashing());

				// Tell the FX thread to do the updates.
				Platform.runLater(() ->
				{
					fireDisplayUpdate();
					enableUserInteraction();
				});
			}
		})).start();
	}
	
	private short evalPiecePositions(final BoardState state)
	{
		final short midGameEval = state.getMidgamePositionalEval(), endGameEval = state.getEndgamePositionalEval();
		final short gamePhase = state.getGamePhase();
		return (short) (((midGameEval * (256 - gamePhase)) + (endGameEval * gamePhase)) / 256);
	}

	@Override
	public void fireDisplayUpdate(final int stateIndex) throws IndexOutOfBoundsException
	{
		final BoardState stateToDisplay = gameStates.get(stateIndex);
		final List<BoardOccupancy> piecePlacementInfo = getOccupancyInfo(stateToDisplay);
		gameController.updateBoardVisuals(piecePlacementInfo);
	}

	private void disableUserInteraction()
	{
		gameController.setUserInteractionLocked(true);
	}

	private void enableUserInteraction()
	{
		gameController.setUserInteractionLocked(false);
	}

	public void writeGameRecord()
	{
		final LocalDateTime date = LocalDateTime.now();
		final Path filePath = Paths.get(REPORT_FOLDER + date.format(DateTimeFormatter.ISO_DATE_TIME));
		try
		{
			EngineUtils.writeMoves(movesPlayed, filePath);
		}
		catch (final IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
