package jenjinn.ui.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * @author ThomasB
 * @since 20 Nov 2017
 */
public final class StandardisedBacking
{
	public static final Paint DARK_BACK = Color.web("#646670");
	public static final Paint TRANSLUCENT_DARK_FILTER = Color.color(0, 0, 0, 0.5);

	private static final ColorAdjust BACK_ADJUST = new ColorAdjust(-0.1, 0.2, -0.1, 0.1);

	private StandardisedBacking()
	{
	}

	public static final Rectangle getShadowedBackground(final double w, final double h)
	{
		final Color c = Color.web("#0d2c5e");// #7a7a7a
		final Rectangle back = new Rectangle(w, h, c);
		final InnerShadow shadeEffect = new InnerShadow();
		shadeEffect.setWidth(w / 2);
		shadeEffect.setHeight(h / 2);
		shadeEffect.setInput(BACK_ADJUST);
		back.setEffect(shadeEffect);
		return back;
	}

	public static final Rectangle getOverlayFilter(final double w, final double h)
	{
		return new Rectangle(w, h, Color.color(0, 0, 0, 0.5));
	}

	public static final ColorAdjust getBackAdjust()
	{
		return BACK_ADJUST;
	}

	public static void setEffect(final GraphicsContext gc)
	{
		final double w = gc.getCanvas().getWidth(), h = gc.getCanvas().getHeight();
		final InnerShadow shadeEffect = new InnerShadow();
		shadeEffect.setWidth(w / 2);
		shadeEffect.setHeight(h / 2);
		shadeEffect.setInput(BACK_ADJUST);
		gc.setEffect(shadeEffect);
	}
}
