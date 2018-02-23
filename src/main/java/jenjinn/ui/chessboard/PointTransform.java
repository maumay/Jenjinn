package jenjinn.ui.chessboard;

import javafx.geometry.Point2D;

/**
 * @author ThomasB
 * @since 11 Jul 2017
 */
public interface PointTransform
{
	Point2D transform(Point2D p);
}
