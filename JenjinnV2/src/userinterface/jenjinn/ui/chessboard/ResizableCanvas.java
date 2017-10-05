/**
 * Copyright © 2017 Lhasa Limited
 * File created: 10 May 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.ui.chessboard;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.canvas.Canvas;

/**
 * A simple extension of the {@link Canvas} class provided by JavaFX to add resizing
 * functionality.
 *
 * @author ThomasB
 * @since 10 May 2017
 */
public class ResizableCanvas extends Canvas
{
	public ResizableCanvas()
	{
	}

	public ResizableCanvas(final double width, final double height)
	{
		super(width, height);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see javafx.scene.Node#minHeight(double)
	 */
	@Override
	public double minHeight(final double width)
	{
		return 64;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see javafx.scene.Node#maxHeight(double)
	 */
	@Override
	public double maxHeight(final double width)
	{
		return 10000;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see javafx.scene.Node#prefHeight(double)
	 */
	@Override
	public double prefHeight(final double width)
	{
		return minHeight(width);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see javafx.scene.Node#minWidth(double)
	 */
	@Override
	public double minWidth(final double height)
	{
		return 64;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see javafx.scene.Node#maxWidth(double)
	 */
	@Override
	public double maxWidth(final double height)
	{
		return 10000;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see javafx.scene.Node#isResizable()
	 */
	@Override
	public boolean isResizable()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see javafx.scene.Node#resize(double, double)
	 */
	@Override
	public void resize(final double width, final double height)
	{
		super.resize(width, height);
		super.setWidth(width);
		super.setHeight(height);
	}

	/**
	 * This method is called when it is time to update the drawing on the
	 * canvas. It is to be implemented by subclasses.
	 */
	protected void updateDrawing()
	{
		// do nothing
	}

	public final void addDimensionChangeListeners()
	{
		// Add the action listeners for width and height changes
		widthProperty().addListener(e -> widthChangeAction((ObservableDoubleValue) e));
		heightProperty().addListener(e -> heightChangeAction((ObservableDoubleValue) e));
	}

	/**
	 * This method is called upon a change in the height property of this
	 * canvas. By default we just update the canvas drawing.
	 */
	protected void heightChangeAction(final ObservableDoubleValue e)
	{
		updateDrawing();
	}

	/**
	 * This method is called upon a change in the width property of this
	 * canvas. By default we just update the canvas drawing.
	 */
	protected void widthChangeAction(final ObservableDoubleValue e)
	{
		updateDrawing();
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