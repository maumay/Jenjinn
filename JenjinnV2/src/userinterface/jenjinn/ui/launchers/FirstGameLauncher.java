/**
 *
 */
package jenjinn.ui.launchers;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jenjinn.engine.enums.Side;
import jenjinn.ui.chessboard.ChessBoard;
import jenjinn.ui.controller.ChessGameController;
import jenjinn.ui.model.JenjinnHumanGameModel;
import jenjinn.ui.view.BoardColorScheme;
import jenjinn.ui.view.BoardColors;

/**
 * @author TB
 * @date 16 Feb 2017
 *
 */
public class FirstGameLauncher extends Application
{

	/* (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage) */
	@Override
	public void start(final Stage stage) throws Exception
	{
		final BoardColorScheme colors = BoardColors.BLUE_THEME;
		final ChessBoard root = getInitialisedGame(colors);
		stage.setScene(new Scene(root));
		stage.show();
	}

	public ChessBoard getInitialisedGame(final BoardColorScheme colors)
	{
		final Side humanSide = Side.B;
		final JenjinnHumanGameModel model = JenjinnHumanGameModel.createNewModel(humanSide);
		final ChessBoard board = new ChessBoard(colors);
		board.rotateToSuitSide(humanSide, false);
		ChessGameController.getInstance().setModel(model);
		ChessGameController.getInstance().setChessboard(board);
		model.fireDisplayUpdate();
		return board;
	}

	public HBox getInitialisedRootWithReportWriter(final int squareLength, final BoardColorScheme colors)
	{
		final HBox root = new HBox();
		root.setAlignment(Pos.CENTER);

		final Side humanSide = Side.W;
		final JenjinnHumanGameModel model = JenjinnHumanGameModel.createNewModel(humanSide);
		final ChessBoard board = new ChessBoard(colors);
		board.rotateToSuitSide(humanSide, false);
		ChessGameController.getInstance().setModel(model);
		ChessGameController.getInstance().setChessboard(board);
		model.fireDisplayUpdate();
		root.getChildren().add(board);

		final Button reportWriter = new Button("Write moves played");
		reportWriter.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
		{

			@Override
			public void handle(final MouseEvent event)
			{
				model.writeGameRecord();
			}
		});
		root.getChildren().add(reportWriter);

		return root;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		launch(args);
	}

}
