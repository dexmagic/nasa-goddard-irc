//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/gui/vis/axis/YAxisRenderer.java,v 1.5 2005/11/07 22:20:46 tames Exp $
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
import java.awt.Cursor;
import javax.swing.JComponent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import gov.nasa.gsfc.commons.numerics.formats.ValueFormat;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.vis.VisRenderer;
import gov.nasa.gsfc.irc.gui.vis.axis.AbstractAxisRenderer;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisScale;
import gov.nasa.gsfc.irc.gui.vis.axis.Tick;


/**
 * This class renderers a vertical Y axis.  When drawn to a given graphics
 * context this axis will draw the axis itself, tick marks, and labels for the 
 * tick marks. This renderer can be set to draw based on a location relative
 * to a graphic it is associated with. The location determines which side the 
 * ticks should be drawn on relative to the labels. 
 * If the axis is located left of the graphic
 * then location should be set to <code>YAxisRenderer.LEFT</code>. If the
 * axis is located right of the graphic then <code>YAxisRenderer.RIGHT</code> 
 * should be used.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/11/07 22:20:46 $
 * @author	Troy Ames
 */
public class YAxisRenderer extends AbstractAxisRenderer implements VisRenderer, MouseMotionListener
{
	/**  
	 * Axis located to the left of the graphic. 
	 * @see #setLocation(String)
	 */
	public static final String LEFT = "left";

	/**  
	 * Axis located to the right of the graphic. 
	 * @see #setLocation(String)
	 */
	public static final String RIGHT = "right";

	private static final double LABEL_MARGIN = 2.0;

	//  Simple properties
	private String fLocation = LEFT;

	//  Tick properties
	private int fTickLength = 4;
	
	private boolean fInitialized=false;
	private static final int UPPER_AXIS=0;
	private static final int LOWER_AXIS=1;
	private static final int MIDDLE_AXIS=2;
	private int fMousePos=UPPER_AXIS;
	private int fLastY=0;
	
	/**
	 * Create a new axis using the designated model. If the model is null one
	 * will be created.
	 *
	 * @param model  the model of the axis.
	 */
	public YAxisRenderer(AxisModel model)
	{
		super(model);
	}

	/**
	 * Set the location of the axis with respect to the graphic it is associated
	 * with.
	 *
	 * @param location  the location of the axis, either LEFT, or RIGHT
	 * @see #LEFT
	 * @see #RIGHT
	 */
	public void setLocation(String location)
	{
		if (LEFT.equals(location.toLowerCase()))
		{
			fLocation = LEFT;
		}
		else
		{
			fLocation = RIGHT;
		}
	}

	/**
	 * Get the location of the axis with respect to the graphic it is associated
	 * with.
	 *
	 * @return the location of the axis, either LEFT, or RIGHT
	 * @see #LEFT
	 * @see #RIGHT
	 */
	public String getLocation()
	{
		return fLocation;
	}

	/**
	 * Get the amount of horizontal space needed to draw the axis 
	 * given the current axis properties.  This
	 * takes into account the room needed to print the tick marks and the
	 * corresponding text.
	 *
	 * @param g2d  the graphics context
	 * @return the preferred width
	 */
	public double getPreferredWidth(Graphics2D g2d)
	{
		FontMetrics labelMetrics = g2d.getFontMetrics();
		// TODO find a better way to determine the default size below
		int biggestStr = Math.abs(labelMetrics.charWidth('0') * 5);
		
		//  Add in the tick length and margin as well as the margin between
		//  the text and the end of the canvas.
		return biggestStr + fTickLength + (LABEL_MARGIN * 2);
	}

	/**
	 * Get the amount of vertical space needed to draw the axis 
	 * given the current axis properties.  This
	 * takes into account the room needed to print the tick marks and the
	 * corresponding text.
	 *
	 * @param g2d  the graphics context
	 * @return the preferred width
	 */
	public double getPreferredHeight(Graphics2D g2d)
	{
		FontMetrics labelMetrics = g2d.getFontMetrics();
		int height = labelMetrics.getHeight() * 3;

		return height;
	}
 
	
	public void initialize() {
		if(!fInitialized) {
			JComponent c = fParent;
			if(c!=null) {
				c.addMouseMotionListener(this);
				fInitialized=true;
			}
		}
		
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
		
		// Set the maximum number of ticks that will fit given the canvas
		// height.
		fAxisModel.setNumberOfTicks((int)(canvasHeight / (labelHeight * 1.5)) + 1);
		Tick[] ticks = updateTicks();

		//  Axis information
		Point2D.Double axisEnd = new Point2D.Double();
		Point2D.Double axisOrigin = new Point2D.Double();
		Line2D.Double axisLine = new Line2D.Double();
		
		//  Tick information
		double tickValue = 0;
		Line2D.Double tickLine = new Line2D.Double();
		Point2D.Double tickLabelPos = new Point2D.Double();
		
		initialize();
		
		// Determine location of vertical axis base line
		if (fLocation == LEFT)
		{
			//  Line at right side of canvas.
			axisEnd.setLocation(canvasRight, canvasTop);
			axisOrigin.setLocation(canvasRight, canvasBottom);
		}
		else
		{
			//  Line at left side of canvas.
			axisEnd.setLocation(canvasLeft, canvasTop);
			axisOrigin.setLocation(canvasLeft, canvasBottom);
		}
		
        ValueFormat formatter = getLabelFormatter();
		String tickLabel = null;
		double tickRatio = 0;
		labelHeight = labelMetrics.getAscent();
		double labelYPosition = 0;
		double labelXPosition = 0;
		double tickPosition = 0;
		
        //  Figure out the position of each tick and label on the axis.
		for (int i = 0; i < ticks.length; i++)
		{
			tickValue = ticks[i].getValue();
			tickRatio = axisScale.getScaleRatio(tickValue, min, max-min);
			tickLabel = formatter.format(ticks[i].getLabelValue());
			tickPosition = canvasBottom - (tickRatio * canvasHeight);

			//  Set the position of the tick mark.
			if (fLocation == LEFT)
			{
				tickLine.setLine(axisOrigin.x - fTickLength, tickPosition,
					axisOrigin.x, tickPosition);
			}
			else // fLocation == RIGHT
			{
				tickLine.setLine(axisOrigin.x, tickPosition, 
					axisOrigin.x + fTickLength, tickPosition);
			}
			
			// Center the label with respect to the tick
			labelYPosition = tickPosition + (labelHeight / 2);

			// Adjust y position if the label is clipped by the top or bottom
			if (labelYPosition > canvasBottom)
			{
				labelYPosition = canvasBottom;
			}
			else if ((labelYPosition - labelHeight) < canvasTop)
			{
				labelYPosition = canvasTop + labelHeight;
			}

			//  Position the label to the left or right of the tick mark.
			if (fLocation == LEFT)
			{
				labelXPosition = axisOrigin.x - fTickLength - LABEL_MARGIN
					- labelMetrics.stringWidth(tickLabel);
			}
			else // fLocation == RIGHT
			{
				labelXPosition = axisOrigin.x + fTickLength + LABEL_MARGIN;
			}

			//  Draw the tick mark and its label.
			g2d.draw(tickLine);
			g2d.drawString(
				tickLabel, (float) labelXPosition, (float) labelYPosition);
		}

		//  Set the location of the axis line and draw it.
		//axisLine.setLine(axisOrigin.x, axisOrigin.y, axisEnd.x, axisEnd.y);
		//g2d.draw(axisLine);
		
		return rect;
	}
	
	public void mouseDragged(MouseEvent e) {

		// if we where in the upper half and:
		//		dragged down decrease the upper limit
		//		dragged up increase the upper limit
		// the same, but in reverse for the lower limit
		int y = e.getY();

		double axisMin = fAxisModel.getViewMinimum();
		double axisMax = axisMin + fAxisModel.getViewExtent();
		double range = Math.abs(axisMax - axisMin);
		double ratio = range * 0.1;
		
		//System.out.println("dragged: "+e);
		if(fMousePos==LOWER_AXIS) {
			if(y<fLastY) {
				// increase the lower limit
				axisMin += ratio*(fLastY-y);
			} else {
				// decrease the lower limit
				axisMin -= ratio*(y-fLastY);
			}
		} else if (fMousePos==MIDDLE_AXIS) {
			if(y>fLastY) {
				axisMin += ratio*(fLastY-y);
				axisMax += ratio*(fLastY-y);
			} else {
				axisMin -= ratio*(y-fLastY);
				axisMax -= ratio*(y-fLastY);
			}
		} else {

			if(y<fLastY) {
				// increase the upper limit
				axisMax += ratio*(fLastY-y);
			} else {
				// decrease the upper limit
				axisMax -= ratio*(y-fLastY);
			}
		}
		fLastY=y;
		
		//System.out.println("ratio: "+ratio+" max: "+axisMax+" min: "+axisMin);
		
		if(axisMin<axisMax) {
			fAxisModel.setViewRange(axisMin, axisMax - axisMin);
		} else {
			fAxisModel.setViewRange(axisMax, axisMin - axisMax);
		}
		fAxisModel.setAutoScale(false);
	}
	
	public void mouseMoved(MouseEvent e) {
		int height = e.getComponent().getHeight();

		fLastY = e.getY();
		
		if(fLastY<(height/3)) {
			fMousePos=UPPER_AXIS;
			fParent.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
		} else if (fLastY<( (2*height)/3)){
			fMousePos=MIDDLE_AXIS;
			fParent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			fMousePos=LOWER_AXIS;
			fParent.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: YAxisRenderer.java,v $
//  Revision 1.5  2005/11/07 22:20:46  tames
//  Reduced the default width of this renderer. A better method still needs to be
//  implemented.
//
//  Revision 1.4  2005/10/11 03:02:44  tames
//  Reflects changes to the AxisModel interface to support AxisScrollBars.
//
//  Revision 1.3  2005/08/12 17:51:23  mn2g
//  If the mouse was in the middle of the axis, the movement of the graph was reversed at Bob L.'s request
//
//  Revision 1.2  2005/08/04 00:37:28  mn2g
//  you can use the mouse to alter the y axis
//
//  Revision 1.1  2004/12/16 23:01:14  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.2  2004/12/14 21:45:30  tames
//  Updates to reflect using an external axis scale along with
//  some refactoring of interfaces.
//
//  Revision 1.1  2004/11/08 23:14:35  tames
//  Initial Version
//
//
