/**
 * Copyright © 2017 Lhasa Limited
 * File created: 28 Nov 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.ui.view;

import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jenjinn.engine.enums.Side;
import jenjinn.ui.chessboard.ResizableCanvas;

/**
 * @author ThomasB
 * @since 28 Nov 2017
 */
public class StartScreen extends Region
{
	private static final Image WHITE_BTN = new Image(StartScreen.class.getResourceAsStream("/WQ64.png"));
	private static final Image BLACK_BTN = new Image(StartScreen.class.getResourceAsStream("/BQ64.png"));
	private static final Color BTN_OUTLINE = null;
	private static final String PICK_COLOR = "Pick your color";

	private JenjinnLogo logo = new JenjinnLogo();
	private Rectangle back = new Rectangle();
	//	private Text txt_pickColor = new Text(PICK_COLOR);
	private ResizableCanvas btn_white = new ResizableCanvas(), btn_black = new ResizableCanvas();

	private Side chosenSide;

	public StartScreen()
	{
		getChildren().addAll(back, btn_white, btn_black, logo);
	}

	@Override
	protected void layoutChildren()
	{
		final double w = getWidth(), h = getHeight();
		logo.relocate((w - logo.getPrefWidth())/2, (h - logo.getPrefHeight())/2);

		btn_white.relocate((w/2 - btn_white.getWidth())/2, h/3 + (h/3 - btn_white.getHeight())/2);
		btn_black.relocate((3*w/2 - btn_white.getWidth())/2, h/3 + (h/3 - btn_white.getHeight())/2);
	}

	@Override
	public void resize(final double w, final double h)
	{
		super.resize(w, h);
		updateBacking(w, h);

		final double h3 = h/2;

		final double logoHeight = Math.min(0.7*Math.min(h3, w/2), 150);
		final double btnHeight = 0.75*Math.min(h3, w/2);

		logo.setPrefSize(2*logoHeight, logoHeight);
		logo.autosize();
		btn_white.resize(btnHeight, btnHeight);
		btn_black.resize(btnHeight, btnHeight);
	}

	private void updateBacking(final double w, final double h)
	{
		getChildren().remove(back);
		back = StandardisedBacking.getShadowedBackground(w, h);
		getChildren().add(0, back);
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