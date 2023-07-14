//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/gui/vis/axis/XAxisRenderer.java,v 1.2 2005/10/11 03:02:11 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.library.gui.vis.axis;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import gov.nasa.gsfc.commons.numerics.formats.ValueFormat;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.vis.VisRenderer;
import gov.nasa.gsfc.irc.gui.vis.axis.AbstractAxisRenderer;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisScale;
import gov.nasa.gsfc.irc.gui.vis.axis.Tick;
import gov.nasa.gsfc.irc.gui.vis.axis.TickFactory;


/**
 * This class renderers a horizontal X axis.  When drawn to a given graphics
 * context this axis will draw the axis itself, tick marks, and labels for the 
 * tick marks. This renderer can be set to draw based on a location relative
 * to a graphic it is associated with. The location determines which side the 
 * ticks should be drawn on relative to the labels. 
 * If the axis is located above the graphic
 * then location should be set to <code>XAxisRenderer.ABOVE</code>. If the
 * axis is located below the graphic then <code>XAxisRenderer.BELOW</code> 
 * should be used.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 03:02:11 $
 * @author	Troy Ames
 */
public class XAxisRenderer extends AbstractAxisRenderer implements VisRenderer
{
	/**  
	 * Axis located above the graphic. 
	 * @see #setLocation(String)
	 */
	public static final String ABOVE = "above";

	/**  
	 * Axis located below the graphic. 
	 * @see #setLocation(String)
	 */
	public static final String BELOW = "below";

	private static final double LABEL_MARGIN = 2.0;

	//  Simple properties
	private String fLocation = BELOW;

	//  Tick properties
	private int fTickLength = 4;
	
	/**
	 * Create a new axis renderer using the designated model. If the model is 
	 * null one will be created.
	 *
	 * @param model  the model of the axis.
	 */
	public XAxisRenderer(AxisModel model)
	{
		super(model);
	}

	/**
	 * Create a new axis using the designated model and label 
	 * formatter. If either the model or formatter is null one will be 
	 * created. 
	 *
	 * @param model  the model of the axis.
	 * @param formatter the label formatter to use
	 */
	public XAxisRenderer(AxisModel model, ValueFormat formatter)
	{
		super(model, null, formatter);		
	}
	
	/**
	 * Create a new axis using the designated model, TickFactory, and label 
	 * formatter. If either the model, factory, or formatter is null one will be 
	 * created. 
	 *
	 * @param model  the model of the axis.
	 * @param factory the tick factory to use
	 * @param formatter the label formatter to use
	 */
	public XAxisRenderer(
			AxisModel model, TickFactory factory, ValueFormat formatter)
	{
		super(model, factory, formatter);		
	}
	
	/**
	 * Set the location of the axis with respect to the graphic it is associated
	 * with.
	 *
	 * @param location  the location of the axis, either ABOVE, or BELOW
	 * @see #ABOVE
	 * @see #BELOW
	 */
	public void setLocation(String location)
	{
		if (BELOW.equals(location.toLowerCase()))
		{
			fLocation = BELOW;
		}
		else
		{
			fLocation = ABOVE;
		}
	}

	/**
	 * Get the location of the axis with respect to the graphic it is associated
	 * with.
	 *
	 * @return  the location of the axis, either ABOVE, or BELOW
	 * @see #ABOVE
	 * @see #BELOW
	 */
	public String getLocation()
	{
		return fLocation;
	}

	/**
	 * Get the amount of horizontal space needed to draw the axis 
	 * given the current axis properties.  This
	 * takes into account the room needed to print the tick marks and the
	 * corresponding labels.
	 *
	 * @param g2d  the graphics context
	 * @return the preferred width
	 */
	public double getPreferredWidth(Graphics2D g2d)
	{
		FontMetrics labelMetrics = g2d.getFontMetrics();
		// TODO find a better way to determine the default size below
		int biggestStr = Math.abs(labelMetrics.charWidth('0') * 8);
		
		//  Add in the tick length and margin as well as the margin between
		//  the text and the end of the canvas.
		return biggestStr + fTickLength + (LABEL_MARGIN * 2);
	}

	/**
	 * Get the amount of vertical space needed to draw the axis 
	 * given the current axis properties.  This
	 * takes into account the room needed to print the tick marks and the
	 * corresponding labels.
	 *
	 * @param g2d  the graphics context
	 * @return the preferred width
	 */
	public double getPreferredHeight(Graphics2D g2d)
	{
		FontMetrics labelMetrics = g2d.getFontMetrics();
		int height = (int) (labelMetrics.getHeight() + fTickLength + LABEL_MARGIN);

		return height;
	}

	/**
	 * Draw the axis, tick marks, and tick mark labels for this axis.
	 * 
	 * @param g2d the graphics context on which to draw
	 * @param rect the unclipped drawing area bounds for rendering. This
	 *            rectangle is used for sizing the axis.
	 * @param dataSet not used by this renderer.
	 * @return the same rectangle instance received.
	 */
	public Rectangle2D draw(Graphics2D g2d, Rectangle2D rect, DataSet dataSet)
	{
		// Axis information
		double min = fAxisModel.getViewMinimum();
		double max = min + fAxisModel.getViewExtent();
		AxisScale axisScale = fAxisModel.getAxisScale();
		
		// Drawing canvas bounds information
		double canvasLeft = rect.getMinX();		
		double canvasRight = rect.getMaxX() - 1;		
		double canvasTop = rect.getMinY();
		double canvasBottom = rect.getMaxY() - 1;

		//  Canvas width and height
		double canvasHeight = rect.getHeight() - 1;
		double canvasWidth = rect.getWidth() - 1;
		
		// Update the maximum number of labels that fit in the axis.
		FontMetrics labelMetrics = g2d.getFontMetrics();
		int labelHeight = labelMetrics.getHeight();
		int labelWidth = Math.abs(labelMetrics.charWidth('0') * 8);
		
		// Set the maximum number of ticks that will fit given the canvas
		// width.
		fAxisModel.setNumberOfTicks((int)(canvasWidth / labelWidth) + 1);
		Tick[] ticks = updateTicks();

		//  Axis information
		Point2D.Double axisEnd = new Point2D.Double();
		Point2D.Double axisOrigin = new Point2D.Double();
		Line2D.Double axisLine = new Line2D.Double();
		
		//  Tick information
		double tickValue = 0;
		Line2D.Double tickLine = new Line2D.Double();
		
		// Determine location of horizontal axis base line
		if (fLocation == BELOW)
		{
			//  Line at top of canvas.
			axisEnd.setLocation(canvasRight, canvasTop);
			axisOrigin.setLocation(canvasLeft, canvasTop);
		}
		else
		{
			//  Line at bottom of canvas.
			axisEnd.setLocation(canvasRight, canvasBottom);
			axisOrigin.setLocation(canvasLeft, canvasBottom);
		}

        ValueFormat formatter = getLabelFormatter();
		String tickLabel = null;
		double tickRatio = 0;
		double tickPosition = 0;
		labelHeight = labelMetrics.getAscent();
		double labelYPosition = 0;
		
		//  Set the y position of the label based on orientation.
		if (fLocation == BELOW)
		{
			labelYPosition = axisOrigin.y + fTickLength + labelHeight + LABEL_MARGIN;
		}
		else // fLocation == ABOVE
		{
			labelYPosition = axisOrigin.y - fTickLength - LABEL_MARGIN;
		}
		
        //  Figure out the position of each tick and label on the axis.
		for (int i = 0; i < ticks.length; i++)
		{
			tickValue = ticks[i].getValue();
			tickRatio = axisScale.getScaleRatio(tickValue, min, max-min);
			tickLabel = formatter.format(ticks[i].getLabelValue());
			tickPosition = canvasLeft + (tickRatio * canvasWidth);
			
			//  Set the position of the tick mark.
			if (fLocation == BELOW)
			{
				tickLine.setLine(tickPosition, axisOrigin.y, 
					tickPosition, axisOrigin.y  + fTickLength);
			}
			else // fLocation == ABOVE
			{
				tickLine.setLine(tickPosition, axisOrigin.y, 
					tickPosition, axisOrigin.y  - fTickLength);
			}
			
			// Center the label horizontal with respect to the tick
			labelWidth = labelMetrics.stringWidth(tickLabel);
			double labelXPosition = tickPosition - (labelWidth / 2);

			// Adjust x position if the label is clipped by a side
			if (labelXPosition < canvasLeft)
			{
				labelXPosition = canvasLeft;
			}
			else if ((labelXPosition + labelWidth) > canvasRight)
			{
				labelXPosition = canvasRight - labelWidth;
			}

			//  Draw the tick mark and its label.
			g2d.draw(tickLine);
			g2d.drawString(
				tickLabel, (float) labelXPosition, (float) labelYPosition);
			//System.out.println("1 " + tickValue);
		}
		
		//  Set the location of the axis line and draw it.
		//axisLine.setLine(axisOrigin.x, axisOrigin.y, axisEnd.x, axisEnd.y);
		//g2d.draw(axisLine);
		
		return rect;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: XAxisRenderer.java,v $
//  Revision 1.2  2005/10/11 03:02:11  tames
//  Reflects changes to the AxisModel interface to support AxisScrollBars.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.3  2004/12/14 21:45:30  tames
//  Updates to reflect using an external axis scale along with
//  some refactoring of interfaces.
//
//  Revision 1.2  2004/11/16 18:52:06  tames
//  Added constructor and modified the calculation of the preferred height.
//
//  Revision 1.1  2004/11/08 23:14:35  tames
//  Initial Version
//
//
