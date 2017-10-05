/**
 *
 */
package jenjinn.ui.view;

import javafx.scene.paint.Color;

/**
 * @author ThomasB
 *
 */
public final class BoardColors
{
	private BoardColors()
	{
	};

	public static final BoardColorScheme BLUE_THEME = new BoardColorScheme(
			new Color[] { Color.web("308CAB"), Color.web("D3ECF5"), Color.web("5260FF"), Color.web("DE4A00"), Color.INDIANRED, Color.BLACK });

	// public static final Color[] GREY_THEME = { Color.web("949494"), Color.web("E8E8E8") };
	//
	// public static final Color[] BROWN_THEME = { Color.web("8F6400"), Color.web("E3C98D") };
}
