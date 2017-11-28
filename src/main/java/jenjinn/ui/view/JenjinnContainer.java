/**
 * Copyright © 2017 Lhasa Limited
 * File created: 28 Nov 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.ui.view;

import javafx.scene.layout.Region;
import jenjinn.ui.chessboard.ChessBoard;
import jenjinn.ui.model.ChessGameModel;

/**
 * @author ThomasB
 * @since 28 Nov 2017
 */
public class JenjinnContainer extends Region
{
	private ChessBoard board;
	private ChessGameModel gameModel;
	private StartScreen start = new StartScreen();

	public JenjinnContainer()
	{
		start.relocate(0, 0);
		getChildren().add(start);
	}

	@Override
	public void resize(final double w, final double h)
	{
		super.resize(w, h);
		start.setPrefSize(w, h);
		start.autosize();
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