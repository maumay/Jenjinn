/**
 * Copyright � 2017 Lhasa Limited
 * File created: 11 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.ui.chessboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.ui.controller.ChessGameController;
import jenjinn.ui.model.BoardOccupancy;
import jenjinn.ui.view.BoardColorScheme;
import jenjinn.ui.view.PieceImageCache;
import jenjinn.ui.view.RenderUtils;

/**
 * @author ThomasB
 * @since 11 Jul 2017
 */
public class ChessBoard extends Region
{
	/**
	 * Make sure < 1
	 */
	private static final double BOARD_BACKING_SIDE_RATIO = 19 / 20.0;

	/**
	 * Our cache of images of each piece
	 */
	private static final PieceImageCache PIECE_IMAGES = new PieceImageCache(64);

	private BoardColorScheme colorScheme;

	private Canvas backingCanvas = new ResizableCanvas();

	private Canvas boardCanvas = new ResizableCanvas();

	private Canvas markerCanvas = new ResizableCanvas();

	private Canvas pieceCanvas = new ResizableCanvas();

	private Canvas interactionLayer = new ResizableCanvas();

	private double squareLength = 0.0;

	/** Bijective mapping between points and squares. */
	private BiMap<Point2D, Sq> squareLocations = HashBiMap.create(64);

	private Map<Sq, ChessPiece> pieceLocationMap = new HashMap<>();

	private MarkerLocations markers = new MarkerLocations();

	// private Sq locationMarkerPosition;
	//
	// private List<Sq> movementMarkerPositions = new ArrayList<>();

	private Side currentPerspective = Side.B;

	private ChessGameController controller;

	public ChessBoard(final BoardColorScheme colorScheme)
	{
		this.colorScheme = colorScheme;
		getChildren().addAll(Arrays.asList(backingCanvas, boardCanvas, markerCanvas, pieceCanvas, interactionLayer));
		interactionLayer.toFront();
		pieceLocationMap.put(Sq.c5, ChessPiece.BPIECES[1]);
		interactionLayer.setOnMouseClicked(evt -> processClick(evt));
		// setUserInterationDisabled(true);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javafx.scene.layout.Region#computePrefWidth(double)
	 */
	@Override
	protected double computePrefWidth(final double height)
	{
		return Screen.getPrimary().getBounds().getWidth() / 2;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javafx.scene.layout.Region#computePrefHeight(double)
	 */
	@Override
	protected double computePrefHeight(final double width)
	{
		return Screen.getPrimary().getBounds().getHeight() / 2;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javafx.scene.Parent#layoutChildren()
	 */
	@Override
	protected void layoutChildren()
	{
		final double width = getWidth(), height = getHeight();
		final boolean widthSmaller = width < height;
		final double backingSideLength = Math.min(width, height);
		final double boardSideLength = BOARD_BACKING_SIDE_RATIO * backingSideLength;
		final double canvasLengthDifference = backingSideLength - boardSideLength;

		final Point2D backingUpperLeft = widthSmaller ? new Point2D(0.0, (height - backingSideLength) / 2) : new Point2D((width - backingSideLength) / 2, 0.0);
		final Point2D boardUpperLeft = backingUpperLeft.add(canvasLengthDifference / 2, canvasLengthDifference / 2);

		backingCanvas.resizeRelocate(backingUpperLeft.getX(), backingUpperLeft.getY(), backingSideLength, backingSideLength);
		boardCanvas.resizeRelocate(boardUpperLeft.getX(), boardUpperLeft.getY(), boardSideLength, boardSideLength);
		pieceCanvas.resizeRelocate(boardUpperLeft.getX(), boardUpperLeft.getY(), boardSideLength, boardSideLength);
		markerCanvas.resizeRelocate(boardUpperLeft.getX(), boardUpperLeft.getY(), boardSideLength, boardSideLength);
		interactionLayer.resizeRelocate(boardUpperLeft.getX(), boardUpperLeft.getY(), boardSideLength, boardSideLength);

		if (!(width == 0 || height == 0))
		{
			updateBoardPoints(boardCanvas.getWidth());
			redraw();
		}
	}

	public void redraw()
	{
		redrawBackground();
		redrawSquares();
		redrawMarkers();
		redrawPieces();
	}

	public void redrawBackground()
	{
		getBackingGC().clearRect(0, 0, backingCanvas.getWidth(), backingCanvas.getHeight());
		getBackingGC().setFill(colorScheme.backingColor);
		getBackingGC().fillRect(0, 0, backingCanvas.getWidth(), backingCanvas.getHeight());
	}

	public void redrawSquares()
	{
		getBoardGC().clearRect(0, 0, boardCanvas.getWidth(), boardCanvas.getHeight());
		squareLocations.keySet().stream().forEach(p -> fillSquare(p));
	}

	public void redrawMarkers()
	{
		getMarkerGC().clearRect(0, 0, markerCanvas.getWidth(), markerCanvas.getHeight());

		if (markers.getLocationMarker() != null)
		{
			drawLocationMarker(markers.getLocationMarker());
		}
		markers.getMovementMarkers().stream().forEach(x -> drawMovementMarker(x));
		markers.getAttackMarkers().stream().forEach(x -> drawAttackMarker(x));
	}

	private void drawAttackMarker(final Sq x)
	{
		final GraphicsContext gc = getMarkerGC();
		final Point2D loc = squareLocations.inverse().get(x);
		final Bounds renderBounds = RenderUtils.getSquareBounds(loc, squareLength, 1);
		RenderUtils.strokeTarget(gc, renderBounds, colorScheme.attackMarker);
	}

	public void redrawPieces()
	{
		getPieceGC().clearRect(0, 0, backingCanvas.getWidth(), backingCanvas.getHeight());
		pieceLocationMap.keySet().stream().forEach(x -> drawPiece(x));
	}

	private void drawPiece(final Sq x)
	{
		final Point2D loc = squareLocations.inverse().get(x);
		final Image pieceImage = PIECE_IMAGES.get(pieceLocationMap.get(x).getImageString());
		final Bounds b = RenderUtils.getSquareBounds(loc, squareLength, 1);
		getPieceGC().drawImage(pieceImage, b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
	}

	private void drawMovementMarker(final Sq sq)
	{
		final Point2D p = squareLocations.inverse().get(sq);
		RenderUtils.strokeOval(getMarkerGC(), RenderUtils.getSquareBounds(p, squareLength, 0.8), squareLength / 20, colorScheme.locationMarker);
	}

	private void drawLocationMarker(final Sq sq)
	{
		final Point2D p = squareLocations.inverse().get(sq);
		getMarkerGC().setFill(colorScheme.locationMarker);
		final Bounds b = RenderUtils.getSquareBounds(p, squareLength, 1);
		getMarkerGC().fillOval(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
	}

	private void updateBoardPoints(final double boardSideLength)
	{
		squareLocations.clear();
		squareLength = boardSideLength / 8;

		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				final Point2D p = new Point2D((i + 1.0 / 2.0) * squareLength, boardSideLength - (j + 1.0 / 2.0) * squareLength);
				squareLocations.put(p, Sq.getSq(i, j));
			}
		}
		if (!currentPerspective.isWhite())
		{
			rotatePoints();
		}
	}

	private GraphicsContext getBackingGC()
	{
		return backingCanvas.getGraphicsContext2D();
	}

	private GraphicsContext getBoardGC()
	{
		return boardCanvas.getGraphicsContext2D();
	}

	private GraphicsContext getMarkerGC()
	{
		return markerCanvas.getGraphicsContext2D();
	}

	private GraphicsContext getPieceGC()
	{
		return pieceCanvas.getGraphicsContext2D();
	}

	private void fillSquare(final Point2D p)
	{
		final Sq correspondingSq = squareLocations.get(p);
		assert correspondingSq != null;
		getBoardGC().setFill(correspondingSq.isLightSquare() ? colorScheme.lightSquares : colorScheme.darkSquares);
		final Bounds b = RenderUtils.getSquareBounds(p, squareLength, 1);
		getBoardGC().fillRect(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
	}

	public void setPieceLocations(final List<BoardOccupancy> piecePlacementInfo)
	{
		pieceLocationMap.clear();
		piecePlacementInfo.stream().forEach(x -> pieceLocationMap.put(Sq.get(x.occupiedSquare), x.occupyingPiece));
	}

	public void clearMovementMarkers()
	{
		markers.clear();
		getMarkerGC().clearRect(0, 0, markerCanvas.getWidth(), markerCanvas.getHeight());
	}

	private void processClick(final MouseEvent evt)
	{
		if (evt.getButton() == MouseButton.PRIMARY)
		{
			for (final Point2D p : squareLocations.keySet())
			{
				if (Math.abs(p.getX() - evt.getX()) < squareLength / 2 && Math.abs(p.getY() - evt.getY()) < squareLength / 2)
				{
					controller.processUserClick(squareLocations.get(p));
					break;
				}
			}
		}
		else if (evt.getButton() == MouseButton.SECONDARY)
		{
			rotateToSuitSide(currentPerspective.otherSide(), true);
		}
	}

	public void setUserInterationDisabled(final boolean locked)
	{
		if (locked)
		{
			interactionLayer.toBack();
		}
		else
		{
			interactionLayer.toFront();
		}
	}

	private void rotatePoints()
	{
		final BiMap<Point2D, Sq> newMap = HashBiMap.create(64);
		final PointTransform rotate = getRotationTransform();

		for (final Point2D p : squareLocations.keySet())
		{

			newMap.put(rotate.transform(p), squareLocations.get(p));
		}

		squareLocations = newMap;
	}

	public void rotateToSuitSide(final Side side, final boolean redraw)
	{
		if (side != currentPerspective)
		{
			rotatePoints();
			currentPerspective = side;
		}
		if (redraw)
		{
			redraw();
		}
	}

	private PointTransform getRotationTransform()
	{
		final double translation = boardCanvas.getWidth();
		final Point2D translate = new Point2D(translation, translation);
		return p -> (new Point2D(-p.getX(), -p.getY())).add(translate);
	}

	/**
	 * @return the markers
	 */
	public MarkerLocations getMarkers()
	{
		return markers;
	}

	public void setController(final ChessGameController controller)
	{
		this.controller = controller;
	}

	// public static void main(String[] args)
	// {
	//
	// }

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