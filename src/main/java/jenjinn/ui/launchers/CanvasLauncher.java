package jenjinn.ui.launchers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jenjinn.ui.chessboard.ChessBoard;
import jenjinn.ui.view.BoardColors;

/**
 * @author ThomasB
 * @since 11 Jul 2017
 */
public class CanvasLauncher extends Application
{
	@Override
	public void start(final Stage primaryStage) throws Exception
	{
		final ChessBoard board = new ChessBoard(BoardColors.BLUE_THEME);
		final double sideLength = Screen.getPrimary().getBounds().getHeight() / 4;
		final Scene s = new Scene(board, sideLength, sideLength);
		primaryStage.setScene(s);
		primaryStage.show();
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		launch(args);
	}
}
