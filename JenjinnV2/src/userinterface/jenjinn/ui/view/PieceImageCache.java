/**
 *
 */
package jenjinn.ui.view;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 *
 */
public final class PieceImageCache
{
	public static final String IMAGE_FILE_EXTENSION = ".png";
	public static final String FOLDER_PATH = "/";

	private Map<String, Image> cache;

	public PieceImageCache(final int... sizes)
	{
		cache = new HashMap<>();
		for (final int size : sizes)
		{
			try
			{
				for (final ChessPiece wp : ChessPiece.WPIECES)
				{
					final String key = wp.getImageString(size);
					cache.put(key, new Image(getClass().getResourceAsStream(FOLDER_PATH + key + IMAGE_FILE_EXTENSION)));
				}
				for (final ChessPiece bp : ChessPiece.BPIECES)
				{
					final String key = bp.getImageString(size);
					cache.put(key, new Image(getClass().getResourceAsStream(FOLDER_PATH + key + IMAGE_FILE_EXTENSION)));
				}
			}
			catch (final Exception e)
			{
				System.err.println("Problem with creating images of size: " + size);
				e.printStackTrace();
			}
		}
	}

	public Image get(final String key)
	{
		return cache.get(key);
	}
}
