/**
 *
 */
package jenjinn.ui.view;

import javafx.scene.paint.Paint;

/**
 * @author ThomasB
 *
 */
public final class BoardColorScheme
{
	public final Paint darkSquares;
	public final Paint lightSquares;
	public final Paint locationMarker;
	public final Paint movementMarker;
	public final Paint attackMarker;
	public final Paint backingColor;

	public BoardColorScheme(final Paint[] colors)
	{
		darkSquares = colors[0];
		lightSquares = colors[1];
		locationMarker = colors[2];
		movementMarker = colors[3];
		attackMarker = colors[4];
		backingColor = colors[5];
	}
}
