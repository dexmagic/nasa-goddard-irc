//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/gui/vis/axis/TimeLabelRenderer.java,v 1.3 2005/10/11 03:01:29 tames Exp $
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
import java.awt.geom.Rectangle2D;

import gov.nasa.gsfc.commons.numerics.formats.TimeFormat;
import gov.nasa.gsfc.commons.numerics.formats.ValueFormat;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.vis.VisRenderer;
import gov.nasa.gsfc.irc.gui.vis.axis.AbstractAxisRenderer;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;


/**
 * This class renderers a single time label in an axis.  
 * This renderer can be set to draw based on a location relative
 * to a graphic it is associated with. The location determines which area 
 * of the axis the label should be drawn. If the axis is located above the graphic
 * then location should be set to <code>XAxisRenderer.ABOVE</code>. If the
 * axis is located below the graphic then <code>XAxisRenderer.BELOW</code> 
 * should be used.
 * 
 * The label is drawn against the right side of the axis and either near the
 * top if the axis is located above the graphic or near the bottom if the 
 * axis is located below.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 03:01:29 $
 * @author	Troy Ames
 */
public class TimeLabelRenderer extends AbstractAxisRenderer implements VisRenderer
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

	/**
	 * Create a new axis renderer using the designated model. If the model is 
	 * null one will be created.
	 *
	 * @param model  the model of the axis.
	 */
	public TimeLabelRenderer(AxisModel model)
	{
		super(model, null, new TimeFormat());
	}

	/**
	 * Create a new axis using the designated model, TickFactory, and label 
	 * formatter. If either the model, factory, or formatter is null one will be 
	 * created. 
	 *
	 * @param model  the model of the axis.
	 * @param factory the tick factory to use
	 */
	public TimeLabelRenderer(
			AxisModel model, ValueFormat formatter)
	{
		super(model, null, formatter);		
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
	 * Get the amount of horizontal space needed to draw the label 
	 * given the current axis properties.  
	 *
	 * @param g2d  the graphics context
	 * @return the preferred width
	 */
	public double getPreferredWidth(Graphics2D g2d)
	{
		FontMetrics labelMetrics = g2d.getFontMetrics();
		ValueFormat formatter = getLabelFormatter();
		String label = formatter.format(System.currentTimeMillis());
		int labelWidth = labelMetrics.stringWidth(label);

		return labelWidth + (LABEL_MARGIN * 2);
	}

	/**
	 * Get the amount of vertical space needed to draw the label 
	 * given the current axis properties.
	 *
	 * @param g2d  the graphics context
	 * @return the preferred width
	 */
	public double getPreferredHeight(Graphics2D g2d)
	{
		FontMetrics labelMetrics = g2d.getFontMetrics();
		int height = (int) (labelMetrics.getHeight() + (LABEL_MARGIN * 2));

		return height;
	}

	/**
	 * Set the unit that this renderer will use to create the label.
	 * 
	 * @param unit the unit String
	 */
	public void setUnit(String unit)
	{
		((TimeFormat) getLabelFormatter()).setUnit(unit);
	}
	
	/**
	 * Get the unit that this renderer will use to create the label.
	 * 
	 * @return the value units for this formatter.
	 */
	public String getUnit()
	{
		return ((TimeFormat) getLabelFormatter()).getUnit();
	}

	/**
	 *	Set the label format pattern to apply. Note that the pattern
	 *  should follow that of the <code>SimpleDateFormat</code> class.
	 *
	 *  @param pattern  the pattern
	 */
	public void setPattern(String pattern)
	{
		getLabelFormatter().setPattern(pattern);
	}

	/**
	 *	Get the label format pattern.
	 *
	 *  @return  the pattern
	 */
	public String getPattern()
	{
		return getLabelFormatter().getPattern();
	}

	/**
	 * Draw the time label for this axis.
	 * 
	 * @param g2d the graphics context on which to draw
	 * @param rect the unclipped drawing area bounds for rendering. This
	 *            rectangle is used for sizing the axis.
	 * @param dataSet not used by this renderer.
	 * @return the same rectangle instance received.
	 */
	public Rectangle2D draw(Graphics2D g2d, Rectangle2D rect, DataSet dataSet)
	{
		double max = fAxisModel.getViewMinimum() + fAxisModel.getViewExtent();
		
		// Drawing canvas bounds information
		double canvasLeft = rect.getMinX();		
		double canvasRight = rect.getMaxX() - 1;		
		double canvasTop = rect.getMinY();
		double canvasBottom = rect.getMaxY() - 1;

		// Get the font size for the axis.
		FontMetrics labelMetrics = g2d.getFontMetrics();
		int labelHeight = labelMetrics.getHeight();

		ValueFormat formatter = getLabelFormatter();
		double labelYPosition = 0;
		
		//  Set the y position of the label based on orientation.
		if (fLocation == BELOW)
		{
			labelYPosition = canvasBottom - LABEL_MARGIN;
		}
		else // fLocation == ABOVE
		{
			labelYPosition = canvasTop + labelHeight + LABEL_MARGIN;
		}
		
		String label = formatter.format(max);
		int labelWidth = labelMetrics.stringWidth(label);
		double labelXPosition = canvasRight - labelWidth;

		g2d.drawString(label, (float) labelXPosition, (float) labelYPosition);
		
		return rect;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: TimeLabelRenderer.java,v $
//  Revision 1.3  2005/10/11 03:01:29  tames
//  Reflects changes to the AxisModel interface.
//
//  Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.3  2004/12/14 21:45:30  tames
//  Updates to reflect using an external axis scale along with
//  some refactoring of interfaces.
//
//  Revision 1.2  2004/11/16 19:47:30  tames
//  Changed default format
//
//  Revision 1.1  2004/11/16 18:50:05  tames
//  Initial version
//
//  Revision 1.1  2004/11/08 23:14:35  tames
//  Initial Version
//
//
