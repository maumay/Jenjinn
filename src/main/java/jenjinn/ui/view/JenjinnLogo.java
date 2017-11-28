package jenjinn.ui.view;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.text.TextFlow;
import jenjinn.engine.enums.Side;
import jenjinn.ui.chessboard.ResizableCanvas;

public class JenjinnLogo extends Region
{
	private static final Color WHITE = Color.WHITE, BLACK = Color.BLACK, OVERLAY = Color.GREEN;

	private static final Image WHITE_N = new Image(StartScreen.class.getResourceAsStream("/WN64.png"));
	private static final Image BLACK_N = new Image(StartScreen.class.getResourceAsStream("/BN64.png"));
	private static final double MAX_FONT_HEIGHT = 40;

	private Text txt_jen = new Text("Jen"), txt_jin = new Text("Jin");
	private TextFlow txt_jenjinn = new TextFlow(txt_jen, txt_jin);
	private ResizableCanvas back = new ResizableCanvas();
	private ResizableCanvas overlay = new ResizableCanvas();

	private Side selected;

	public JenjinnLogo()
	{
		txt_jen.setFill(WHITE);
		txt_jen.setBoundsType(TextBoundsType.VISUAL);
		txt_jin.setFill(BLACK);
		txt_jin.setBoundsType(TextBoundsType.VISUAL);
		getChildren().addAll(back, txt_jenjinn, overlay);
		overlay.getGraphicsContext2D().setStroke(OVERLAY);

		setOnMouseClicked(this::mclickedAction);
	}

	private void mclickedAction(final MouseEvent evt)
	{
		final double evtX = evt.getX();
		selected = evtX < getWidth()/2? Side.B : Side.W;
		drawBackground(getWidth(), getHeight());
	}

	@Override
	public void resize(final double w, final double h)
	{
		super.resize(w, h);
		txt_jenjinn.setVisible(false);
		back.resize(w, h);
		overlay.resize(w, h);
		updateFonts(w, h);

		final DropShadow dropShadow2 = new DropShadow();
		dropShadow2.setOffsetX(6.0);
		dropShadow2.setOffsetY(4.0);
		setEffect(dropShadow2);

		overlay.getGraphicsContext2D().setLineWidth(Math.min(7, h/20));
	}

	private void updateFonts(final double w, final double h)
	{
		double fheight = MAX_FONT_HEIGHT;
		Font f = Font.font(fheight);
		txt_jen.setFont(f);
		txt_jin.setFont(f);
		txt_jenjinn.autosize();
		Bounds b = txt_jenjinn.getLayoutBounds();

		while (b.getWidth() >= w)
		{
			fheight -= 0.5;
			f = Font.font(fheight);
			txt_jen.setFont(f);
			txt_jin.setFont(f);
			txt_jenjinn.autosize();
			b = txt_jenjinn.getLayoutBounds();
		}
	}

	@Override
	protected void layoutChildren()
	{
		final double w = getWidth(), h = getHeight();
		final Bounds tBounds = txt_jenjinn.getLayoutBounds();
		txt_jenjinn.relocate((w - tBounds.getWidth())/2, (h - tBounds.getHeight())/2);
		drawBackground(w, h);
	}

	private void drawBackground(final double w, final double h)
	{
		final GraphicsContext backGC = back.getGraphicsContext2D();
		final GraphicsContext overlayGC = overlay.getGraphicsContext2D();

		backGC.clearRect(0, 0, w, h);
		overlayGC.clearRect(0, 0, w, h);

		backGC.setFill(WHITE);
		backGC.fillRect(0, 0, w/ 2, h);
		backGC.drawImage(BLACK_N, 0, 0, w/ 2, h);

		backGC.setFill(BLACK);
		backGC.fillRect(w/2, 0, w/2, h);
		backGC.drawImage(WHITE_N, w/2, 0, w/2, h);

		if (selected != null)
		{
			if (selected.isWhite())
			{
				overlayGC.strokeRect(w/2, 0, w/2, h);
			}
			else
			{
				overlayGC.strokeRect(0, 0, w/ 2, h);
			}
		}
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