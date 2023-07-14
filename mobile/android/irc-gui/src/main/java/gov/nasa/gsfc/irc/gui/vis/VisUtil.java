//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/VisUtil.java,v 1.2 2004/12/16 23:00:36 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.gui.vis;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;

/**
 * Misc. Visualization utilities. These may be obsolete but for now I just 
 * included it.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2004/12/16 23:00:36 $
 * @author Ken Wootton
 * @author Troy Ames
 */
public abstract class VisUtil
{
	//  Values for points with an infinite coordinate.
	public static final float FAKE_NEG_INFINITY = -10000;
	public static final float FAKE_POS_INFINITY = 10000;

	//  Distance between the mouse and a printed label.
	private static final int LABEL_POS_MARGIN = 3;

	//   Horizontal distance between the label and its background rectangle.
	private static final int LABEL_RECT_MARGIN = 2;

	//  Properties of the background rectange for the label.	
	private static final Color LABEL_BG_COLOR = 
		new Color(1.0f, 1.0f, 1.0f, 0.5f);

	/**
	 * Given a x or y window coordinate, this method will return the correct
	 * position that this x or y coordinate should be drawn, making any changes
	 * necessary because of an "out of range" coordinate. This method is used to
	 * battle the problem of certain Java drawing shapes, such as path and
	 * filled points, not handling values that are way off the clipped graphics
	 * object. Infinite values are the main culprit here. This adjustment will
	 * make sure the drawn coordinate isn't too far off the drawing area to
	 * cause problems.
	 * <p>
	 * 
	 * Note: I believe this problem is fixed in 1.4. We might want to remove
	 * this when we switch.
	 */
	public static float adjustOutOfRangeValue(float value)
	{
		if (value == java.lang.Float.NEGATIVE_INFINITY ||
			value < FAKE_NEG_INFINITY)
		{
			value = FAKE_NEG_INFINITY;
		}

		else if (value == java.lang.Float.POSITIVE_INFINITY ||
				 value > FAKE_POS_INFINITY)
		{
			value = FAKE_POS_INFINITY;
		}

		return value;
	}

	/**
	 * Save the given image to the given file as a JPEG image.
	 * 
	 * @param image the image to save
	 * @param file the file in which to save the image
	 * 
	 * @throws IOException if there are I/O problems with saving the file
	 */
	public static void saveJpegFile(BufferedImage image, File file)
		throws IOException
	{
		FileOutputStream outputStream = new FileOutputStream(file);

		//  Create the JPEG encoder.
		JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(image);
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outputStream,
															   param);

		//  Encode the image to the output stream and we're done.
		encoder.encode(image);
		outputStream.close();
	}

	/**
	 * Translate the given screen point into a domain scaled point and units.
	 * 
	 * @param screenPoint a screen position within the rect
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param xAxisModel the model for the x axis
	 * @param yAxisModel the model for the y axis
	 * 
	 * @return a point translated into the domain scale and units
	 */
	public static Point2D screenPointToDomain(
			Point2D screenPoint, Rectangle2D rect, 
			AxisModel xAxisModel, AxisModel yAxisModel)
	{
		Point2D valuePoint = new Point2D.Double();
		
		//  Canvas width and height
		double canvasHeight = rect.getHeight() - 1;
		double canvasWidth = rect.getWidth() - 1;
		
		double valueX = 0.0;

		//  Calculate the screen scale in the x direction.
		if (canvasWidth != 0)
		{
			valueX = xAxisModel.getScaleValue(screenPoint.getX() / canvasWidth);
		}

		double valueY = 0.0;

		//  Calculate the screen scale in the y direction.
		if (canvasHeight != 0)
		{
			valueY = 
				yAxisModel.getScaleValue(
					1.0 - (screenPoint.getY() / canvasHeight));
		}

		valuePoint.setLocation(valueX, valueY);
		
		return valuePoint;
	}

	/**
	 * Translate the given domain point into a screen point.
	 * 
	 * @param domainPoint a domain position
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param xAxisModel the model for the x axis
	 * @param yAxisModel the model for the y axis
	 * @return a point translated into the screen scale represented by the rect
	 * 		parameter.
	 */
	public static Point2D domainPointToScreen(
			Point2D domainPoint, Rectangle2D rect, 
			AxisModel xAxisModel, AxisModel yAxisModel)
	{
		Point2D screenPoint = new Point2D.Double();
		
		// Drawing canvas bounds information
		double canvasLeft = rect.getMinX();
		double canvasBottom = rect.getMaxY();

		//  Canvas width and height
		double canvasHeight = rect.getHeight() - 1;
		double canvasWidth = rect.getWidth() - 1;

		double xRatio = xAxisModel.getScaleRatio(domainPoint.getX());
		double yRatio = yAxisModel.getScaleRatio(domainPoint.getY());
		
		// Translate and scale the point to the view coordinates.
		double screenX = (canvasWidth * xRatio) + canvasLeft;
		double screenY = canvasBottom - (canvasHeight * yRatio);

		screenPoint.setLocation(screenX, screenY);
		
		return screenPoint;
	}

	/**
	 * Translate the point into a relative visualization screen
	 * coordinate within the given rect. This method is useful if the rect is
	 * not the same size as the parent component. This method will convert a 
	 * point relative to the parent into a point relative to the rect.
	 * 
	 * @param point a screen point
	 * @param rect the graphics context bounds
	 * 
	 * @return a point translated into a relative screen location
	 */
	public static Point2D getRelativePoint(Point point, Rectangle2D rect)
	{
		Point2D relativePoint = new Point2D.Double();
		
		// Drawing canvas bounds information
		double canvasLeft = rect.getMinX();		
		double canvasTop = rect.getMinY();
		
		// Calculate the X coordinate
		double screenX = point.x - canvasLeft;
		
		// Calculate the Y coordinate
		double screenY = point.y - canvasTop;

		relativePoint.setLocation(screenX, screenY);
		
		return relativePoint;
	}
	
	/**
	 * Determine the distance from point 1 to point 2.
	 * 
	 * @param x1 x-coordinate of point 1
	 * @param y1 y-coordinate of point 1
	 * @param x2 x-coordinate of point 2
	 * @param y2 y-coordinate of point 2
	 */
	public static double distance(double x1, double y1, double x2, double y2)
	{
		//  Scale the x and y distances
		double xDis = (x1 - x2);
		double yDis = (y1 - y2);

		return Math.sqrt((xDis * xDis) + (yDis * yDis));
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: VisUtil.java,v $
//  Revision 1.2  2004/12/16 23:00:36  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//
