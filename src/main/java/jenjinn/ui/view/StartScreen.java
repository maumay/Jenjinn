package jenjinn.ui.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import jenjinn.engine.enums.Side;
import jenjinn.ui.chessboard.ChessBoard;
import jenjinn.ui.chessboard.ResizableCanvas;

/**
 * @author ThomasB
 * @since 28 Nov 2017
 */
public class StartScreen extends Region
{
//	private static final Image WHITE_BTN = new Image(StartScreen.class.getResourceAsStream("/WQ64.png"));
//	private static final Image BLACK_BTN = new Image(StartScreen.class.getResourceAsStream("/BQ64.png"));
//	private static final Color BTN_OUTLINE = null;
//	private static final String PICK_COLOR = "Pick your color";

	private BooleanProperty startTrigger = new SimpleBooleanProperty();

	private JenjinnLogo logo = new JenjinnLogo();
	private Rectangle back = new Rectangle();
	private ChessBoard board = new ChessBoard(BoardColors.BLUE_THEME);
	// private Text txt_pickColor = new Text(PICK_COLOR);
	private ResizableCanvas btn_white = new ResizableCanvas(), btn_black = new ResizableCanvas();

	private Side chosenSide;

	public StartScreen()
	{
		getChildren().addAll(back, board, btn_white, btn_black, logo);
		logo.setOnMouseMoved(this::mMovedHandler);
		logo.setOnMouseExited(this::mExitedHandler);
		logo.setOnMouseClicked(this::mClickedHandler);
	}

	private void mClickedHandler(final MouseEvent evt)
	{
		chosenSide = logo.getSelected() == null ? Side.W : logo.getSelected();
		startTrigger.set(!startTrigger.get());
	}

	private void mExitedHandler(final MouseEvent evt)
	{
		logo.setSelected(null);
		logo.redraw();
	}

	private void mMovedHandler(final MouseEvent evt)
	{
		final double evtX = evt.getX();
		logo.setSelected(evtX < logo.getWidth() / 2 ? Side.B : Side.W);
		logo.redraw();
	}

	@Override
	protected void layoutChildren()
	{
		final double w = getWidth(), h = getHeight();
		logo.relocate((w - logo.getPrefWidth()) / 2, (h - logo.getPrefHeight()) / 2);
		board.relocate((w - board.getWidth()) / 2, (h - board.getHeight()) / 2);

		btn_white.relocate((w / 2 - btn_white.getWidth()) / 2, h / 3 + (h / 3 - btn_white.getHeight()) / 2);
		btn_black.relocate((3 * w / 2 - btn_white.getWidth()) / 2, h / 3 + (h / 3 - btn_white.getHeight()) / 2);
	}

	@Override
	public void resize(final double w, final double h)
	{
		super.resize(w, h);
		updateBacking(w, h);

		final GaussianBlur boardShadow = new GaussianBlur(Math.min(5, 0.05 * Math.min(w, h)));
		board.setEffect(boardShadow);
		final double boardHeight = 0.7 * Math.min(w, h);
		board.resize(boardHeight, boardHeight);

		final double h3 = h / 2;

		final double logoHeight = Math.min(0.7 * Math.min(boardHeight / 2, 1000), 100);
		final double btnHeight = 0.75 * Math.min(h3, w / 2);

		logo.setPrefSize(2 * logoHeight, logoHeight);
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

	public void addListener(final ChangeListener<Boolean> listener)
	{
		startTrigger.addListener(listener);
	}

	public Side getChosen()
	{
		return chosenSide;
	}
}
