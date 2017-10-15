/**
 * Copyright © 2017 Lhasa Limited
 * File created: 11 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
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

/* ---------------------------------------------------------------------*
 * This software is the confidential and proprietary
 * information of Lhasa Limited
 * Granary Wharf House, 2 Canal Wharf, Leeds, LS11 5PS
 * ---
 * No part of this confidential information shall be disclosed
 * and it shall be used only in accordance with the terms of a
 * written license agreement entered into by holder of the information
 * with LHASA Ltd.
 * --------------------------------------------------------------------- */