/**
 * Copyright © 2017 Lhasa Limited
 * File created: 11 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
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