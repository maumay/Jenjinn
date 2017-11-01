/**
 *
 */
package jenjinn.ui.model;

import java.io.IOException;
import java.math.BigDecimal;
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
import jenjinn.engine.enums.TerminationType;
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
	private static final int DEFAULT_SIG_FIG = 5;

	private static final double MIN_MOVE_TIME = 0.5;
	private static final double MAX_MOVE_TIME = 30;


	private static final String REPORT_FOLDER = "matchreports/";


	private ChessGameController gameController;

	private List<BoardState> gameStates;
	private List<ChessMove> movesPlayed;
	private Jenjinn jenjinn;
	private StoredUserSelection userSelection;

	private double moveTime = 40;

	private JenjinnHumanGameModel()
	{
	}

	public static JenjinnHumanGameModel createNewModel(final Side humanSide, final ChessGameController controller)
	{
		final JenjinnHumanGameModel newModel = new JenjinnHumanGameModel();
		newModel.gameStates = new ArrayList<>(Arrays.asList(BoardStateImplV2.getStartBoard()));
		newModel.movesPlayed = new ArrayList<>();
		newModel.userSelection = new StoredUserSelection();
		newModel.jenjinn = new Jenjinn(humanSide.otherSide(), BoardEvaluator.getDefault());
		newModel.gameController = controller;
		controller.setModel(newModel);

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
		userSelection.reset();
		fireDisplayUpdate();

		if (getPresentGameState().getTerminationState().isTerminal())
		{
			disableUserInteraction();
			triggerEndOfGame();
		}
		else
		{
			performAiMove();
		}
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


		final List<ChessMove> possibleMoves = getLegalMoves();//getPresentGameState().getMoves();

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

	private List<ChessMove> getLegalMoves()
	{
		final BoardState present = getPresentGameState();
		final List<ChessMove> moves = present.getMoves();

		for (int i = moves.size() - 1; i >= 0; i--)
		{
			final BoardState nextState = moves.get(i).evolve(present);
			final TerminationType termStatus = nextState.getTerminationState();

			if (termStatus.isWin())
			{
				moves.remove(i);
			}
		}

		return moves;
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
		/* Can interrupt in this method maybe? So the thread we got now,
		 * another timer thread which upon finishing interrupts this other
		 * thread which should now terminate because of work done in
		 * TTAlphaBeta class. */


		final Thread jenjinnJob = new Thread(() ->
		{
			synchronized (gameStates)
			{
				final BoardState presentState = getPresentGameState();
				final ChessMove jenjinnMove = jenjinn.calculateBestMove(presentState);
				gameStates.add(jenjinnMove.evolve(presentState));
				movesPlayed.add(jenjinnMove);

				final boolean terminal = getPresentGameState().getTerminationState().isTerminal();

				// Tell the FX thread to do the updates.
				Platform.runLater(() ->
				{
					fireDisplayUpdate();

					if (!terminal)
					{
						enableUserInteraction();
					}
					else
					{
						triggerEndOfGame();
					}
				});
			}
		});

		jenjinnJob.start();

		final Thread timer = new Thread(() ->
		{
			try
			{
				final long t = System.nanoTime();
				Thread.sleep((long) MAX_MOVE_TIME * 1000);

				if (jenjinnJob.isAlive())
				{
					System.out.println("Interrupted after: " + toSeconds(System.nanoTime() - t) + " s");
					jenjinnJob.interrupt();
				}

			} catch (final InterruptedException e)
			{
				throw new AssertionError();
			}
		});

		timer.start();
	}

	private void triggerEndOfGame()
	{

	}

	private String toSeconds(final long nstime)
	{
		final BigDecimal bill = BigDecimal.valueOf(1_000_000_000);
		final String result = String.format("%." + DEFAULT_SIG_FIG + "G", BigDecimal.valueOf(nstime).divide(bill));
		return result;
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

	public double getMoveTime()
	{
		return moveTime;
	}

	@Override
	public void setMoveTimeLimit(final double scaleFactor)
	{
		final double scaled = MIN_MOVE_TIME + scaleFactor*(MAX_MOVE_TIME - MIN_MOVE_TIME);
		this.moveTime = scaled < MIN_MOVE_TIME ? MIN_MOVE_TIME : (scaled > MAX_MOVE_TIME ? MAX_MOVE_TIME : scaled);
	}

	public void setGameController(final ChessGameController gameController)
	{
		this.gameController = gameController;
	}
}
