/**
 * Copyright © 2017 Lhasa Limited
 * File created: 28 Nov 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.ui.view;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import jenjinn.engine.enums.Side;
import jenjinn.ui.chessboard.ChessBoard;
import jenjinn.ui.controller.ChessGameController;
import jenjinn.ui.model.ChessGameModel;
import jenjinn.ui.model.JenjinnHumanGameModel;

/**
 * @author ThomasB
 * @since 28 Nov 2017
 */
public class JenjinnContainer extends Region
{
	private ChessBoard board = new ChessBoard(BoardColors.BLUE_THEME);
	private ChessGameModel gameModel;
	private StartScreen start = new StartScreen();

	public JenjinnContainer()
	{
		setSnapToPixel(true);
		start.relocate(0, 0);
		getChildren().addAll(start, board);
		board.setVisible(false);
		start.addListener((a, b, c) ->
		{
			final Side selected = start.getChosen();
			Platform.runLater(() ->
			{
				board.setVisible(true);
				board.rotateToSuitSide(selected, false);
				final ChessGameController controller = new ChessGameController(board);
				gameModel = JenjinnHumanGameModel.createNewModel(selected, controller);
				gameModel.fireDisplayUpdate();
			});
		});

		start.setOnMouseClicked(this::reset);
	}

	public void setMoveTimeLimit(final double d)
	{
		if (gameModel != null)
		{
			gameModel.setMoveTimeLimit(d);
		}
	}

	private void reset(final MouseEvent evt)
	{
		if (evt.isControlDown())
		{
			getChildren().remove(board);
			board = new ChessBoard(BoardColors.BLUE_THEME);
			gameModel = null;
			getChildren().add(board);
			board.setVisible(false);
		}
	}

	@Override
	public void resize(final double w, final double h)
	{
		super.resize(w, h);
		start.setPrefSize(w, h);
		start.autosize();

		final double min = Math.min(w,  h);
		board.resize(0.8*min, 0.8*min);
	}

	@Override
	protected void layoutChildren()
	{
		final double w = getWidth(), h = getHeight();
		board.relocate(snapSize((w - board.getWidth())/2), snapSize((h - board.getHeight())/2));
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