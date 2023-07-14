//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/library/gov/nasa/gsfc/irc/library/gui/vis/cell/TwinkleLabelRenderer.java,v 1.2 2006/04/27 18:51:52 chostetter_cvs Exp $
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

package gov.nasa.gsfc.irc.library.gui.vis.cell;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.JComponent;

import gov.nasa.gsfc.commons.numerics.formats.SciDecimalFormat;
import gov.nasa.gsfc.commons.numerics.formats.ValueFormat;
import gov.nasa.gsfc.commons.numerics.types.Pixel;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.vis.AbstractVisRenderer;
import gov.nasa.gsfc.irc.gui.vis.VisUtil;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;


/**
 * This class provides basic two dimensional gridded cell mouse label for 
 * the current mouse position.
 * This class can be used in a Decorator pattern for a parent 
 * component that delegates the actual drawing to this renderer. 
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/27 18:51:52 $
 * @author Troy Ames
 */
public class TwinkleLabelRenderer extends AbstractVisRenderer
	implements MouseMotionListener, MouseListener
{
	private static final String CLASS_NAME = TwinkleLabelRenderer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	// Loop constants that define the screen ratios and margins.
	private int fCellRowOffset = 0;
	private int fCellColumnOffset = 0;
	private int fCellRows = 12;
	private int fCellColumns = 32;
	private boolean fXLeftOrigin = true;
	private boolean fYTopOrigin = true;

	//  Label info.
	//  Property values
	private boolean fLabelEnabled = true;
	private AxisModel fAxisModel = null;
	
	//  Label info.
	private Point fMousePosition = null;
	private ValueFormat fFormatter;
	private String fBufferBaseName = null;
	
	/**
	 * Create a new renderer with default axis models.
	 */
	public TwinkleLabelRenderer()
	{
		this(null);
	}

	/**
	 * Create a new renderer with the given axis models.
	 * 
	 * @param xAxis the X axis model
	 * @param yAxis the Y axis model
	 */
	public TwinkleLabelRenderer(AxisModel axis)
	{
		super();

		if (fFormatter == null)
		{
			fFormatter = new SciDecimalFormat();
		}
	}

	/**
	 * Draw the data on the given graphics context.
	 * 
	 * @param g2d the graphics context on which to draw.
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param dataSet the data to render.
	 * 
	 * @return the same rectangle instance received.
	 */
	public Rectangle2D draw(Graphics2D g2d, Rectangle2D rect, DataSet dataSet)
	{
		if (dataSet != null)
		{
			if (fLabelEnabled && fMousePosition != null)
			{			
				Point2D screenPoint = VisUtil.getRelativePoint(fMousePosition, rect);
				
				// Update the text of the label
				String labelText = null;
				
				if (dataSet != null)
				{
					// Try to create a data based label first
					labelText = getDataLabel(rect, screenPoint, dataSet);
				}

				// Draw the label if we created one above
				
				if (labelText != null)
				{
					Color savedColor = g2d.getColor();

					// Drawing canvas bounds information
					double canvasLeft = rect.getMinX();
					double canvasRight = rect.getMaxX();
					double canvasTop = rect.getMinY();
					double canvasBottom = rect.getMaxY();
		
					FontMetrics labelMetrics = g2d.getFontMetrics();
					int labelAscent = labelMetrics.getAscent();
					int labelDescent = labelMetrics.getDescent();
					int labelWidth = labelMetrics.stringWidth(labelText);
					
					// Translate the label position so that it fits inside the rectangle
					// of the graphics context.
					int labelX = fMousePosition.x;
					int labelY = fMousePosition.y;
					
					// Avoid label cropping on the right
					if ((labelX + labelWidth) > canvasRight)
					{
						int offset = (int) (labelX + labelWidth - canvasRight);
						labelX -= offset;
					}
		
					// Avoid label cropping on the top
					if ((labelY - labelAscent) < canvasTop)
					{
						int offset = (int) (canvasTop - (labelY - labelAscent));
						labelY += offset;
					}
					
					// Avoid label cropping on the bottom
					if ((labelY + labelDescent) > canvasBottom)
					{
						int offset = (int) ((labelY + labelDescent) - canvasBottom);
						labelY -= offset;
					}
					
					//  Create and draw the background rectangle.
					Rectangle2D.Double backgroundRect = 
						new Rectangle2D.Double(
							labelX, (labelY - labelAscent), 
							labelWidth, (labelAscent + labelDescent));
					
					// Get a slightly transparent version of the background color
					Color backgroundColor = g2d.getBackground();
					backgroundColor = new Color(
						backgroundColor.getRed(),
						backgroundColor.getGreen(),
						backgroundColor.getBlue(),
						200);
					
					g2d.setColor(backgroundColor);
					g2d.fill(backgroundRect);

					// Restore the color and draw the label
					g2d.setColor(savedColor);
					g2d.drawString(labelText, labelX, labelY);
					//System.out.println(" " + labelText);
		
					// Restore graphics settings
					
				}
			}
		}

		return rect;
	}

	/**
	 * Get the data label if mouse position is close to a data point.
	 * 
	 * @param screenPoint the mouse position
	 * @param dataSet the data to match with the value
	 * @return a label or null if data point not found
	 */
	protected String getDataLabel(
			Rectangle2D rect, Point2D screenPoint, DataSet dataSet)
	{
		String resultString = null;
		
		// target position
		double mouseX = screenPoint.getX();
		double mouseY = screenPoint.getY();
		int cellX = 0;
		int cellY = 0;
		
		// Drawing canvas bounds information
		double canvasLeft = rect.getMinX();
		double canvasRight = rect.getMaxX();
		double canvasTop = rect.getMinY();
		double canvasBottom = rect.getMaxY();

		//  Canvas width and height
		double canvasHeight = rect.getHeight() - 1;
		double canvasWidth = rect.getWidth() - 1;

		// Cell sizes
		double cellWidth = canvasWidth / fCellColumns;
		double cellHeight = canvasHeight / fCellRows;

		// Determine which corner the [0, 0] cell will be located in.
		if (fXLeftOrigin)
		{
			cellX = (int)((mouseX - canvasLeft) / cellWidth);
		}
		else
		{
			cellX = (int)((canvasRight - mouseX) / cellWidth);
		}
		
		if (fYTopOrigin)
		{
			cellY = (int)((mouseY - canvasTop) / cellHeight);
		}
		else
		{
			cellY = (int)((canvasBottom - mouseY) / cellHeight);
		}

		DataBuffer dataBuffer = null;
		Iterator basisSets = dataSet.getBasisSets().iterator();
		Pixel pixel = new Pixel(cellY, cellX);

		while (basisSets.hasNext() && dataBuffer == null)
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			dataBuffer = basisSet.getDataBuffer(fBufferBaseName , pixel);
		}
		
		if (dataBuffer != null)
		{
			resultString = getLabelString(dataBuffer);
		}
		
		return resultString;
	}
	
	/**
	 * Get the formatted label string for the given buffer.
	 * 
	 * @param buffer the buffer representing the current mouse position
	 * @return a label
	 */
	public String getLabelString(DataBuffer buffer)
	{
		String result = buffer.getName()
			+ " ["
			+ fFormatter.format(buffer.getAsDouble(buffer.getSize() - 1)) 
			+ "]";
		
		return result;
	}
	
	/**
	 * Set the parent component that will use this renderer to draw its content.
	 *
	 * @param component  the chart component
	 */
	public synchronized void setParent(JComponent parent)
	{
		super.setParent(parent);
		if (fLabelEnabled)
		{
			parent.addMouseMotionListener(this);
			parent.addMouseListener(this);
		}
	}
	
	/**
	 * Get the formatter for the data component of the mouse label.
	 * 
	 * @return Returns the formatter.
	 */
	public ValueFormat getLabelFormatter()
	{
		return fFormatter;
	}
	
	/**
	 * Set the formatter for the data component of the mouse label.
	 * 
	 * @param formatter The formatter to set.
	 */
	public void setLabelFormatter(ValueFormat formatter)
	{
		fFormatter = formatter;
	}

	/**
	 * Set the pattern for the data component of the mouse label.
	 * 
	 * @param pattern The format pattern.
	 */
	public void setLabelFormatPattern(String pattern)
	{
		fFormatter.setPattern(pattern);
	}

	/**
	 * Get the pattern for the data component of the mouse label.
	 * 
	 * @return the pattern
	 */
	public String getLabelFormatPattern()
	{
		return fFormatter.getPattern();
	}

	/**
	 * Set whether or not the drawing of a label is enabled.
	 * 
	 * @param enabled true to enable labels
	 * @see #setDataLabelEnabled(boolean)
	 * @see #setPositionLabelEnabled(boolean)
	 */
	protected synchronized final void setLabelEnabled(boolean enable)
	{
		if (fLabelEnabled != enable)
		{
			// The state is changing
			fLabelEnabled = enable;

			if (fLabelEnabled)
			{
				getParent().addMouseMotionListener(this);
				getParent().addMouseListener(this);
			}
			else
			{
				getParent().removeMouseMotionListener(this);
				getParent().removeMouseListener(this);
				
				// Force a repaint to remove any existing label
				getParent().repaint();
			}
		}
	}

	/**
	 * Get whether or not the drawing of a label is enabled.
	 * 
	 * @return whether or not a label is enabled
	 */
	protected boolean getLabelEnabled()
	{
		return fLabelEnabled;
	}

	/**
	 * Get the number of displayed columns.
	 * 
	 * @return Returns the cellColumns.
	 */
	public int getColumns()
	{
		return fCellColumns;
	}
	
	/**
	 * Set the number of columns that will be in the display.
	 * 
	 * @param columns The number of columns.
	 */
	public void setColumns(int columns)
	{
		fCellColumns = columns;
	}
	
	/**
	 * Get the number of rows in the display.
	 * 
	 * @return Returns the number of rows.
	 */
	public int getRows()
	{
		return fCellRows;
	}
	
	/**
	 * Set the number of rows that will be in the display.
	 * 
	 * @param rows The number of rows to display.
	 */
	public void setRows(int rows)
	{
		fCellRows = rows;
	}
	
	/**
	 * Get the relative column offset for drawing the twinkle cells. This 
	 * determines where in the drawing area a [0, 0] cell will be drawn.
	 * 
	 * @return Returns the cell column offset.
	 */
	public int getColumnOffset()
	{
		return fCellColumnOffset;
	}
	
	/**
	 * Set the relative column offset for drawing the twinkle cells. This 
	 * determines where in the drawing area a [0, 0] cell will be drawn.
	 * 
	 * @param columnOffset The cell column offset to set.
	 */
	public void setColumnOffset(int columnOffset)
	{
		fCellColumnOffset = columnOffset;
	}
	
	/**
	 * Get the relative row offset for drawing the twinkle cells. This 
	 * determines where in the drawing area a [0, 0] cell will be drawn.
	 * 
	 * @return Returns the cell row offset.
	 */
	public int getRowOffset()
	{
		return fCellRowOffset;
	}
	
	/**
	 * Set the relative row offset for drawing the twinkle cells. This 
	 * determines where in the drawing area a [0, 0] cell will be drawn.
	 * 
	 * @param rowOffset The cell row offset to set.
	 */
	public void setRowOffset(int rowOffset)
	{
		fCellRowOffset = rowOffset;
	}
	
	/**
	 * Invoked when a mouse is moved over the component.
	 * 
	 * @param e the mouse event
	 */
	public void mouseMoved(MouseEvent e)
	{
		if (fLabelEnabled)
		{
			fMousePosition = e.getPoint();
			getParent().repaint();
		}
	}

    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.  Mouse drag events will continue to be delivered to
     * the component where the first originated until the mouse button is
     * released (regardless of whether the mouse position is within the
     * bounds of the component).
     */
    public void mouseDragged(MouseEvent e) 
    {}

    /**
	 * Invoked when a mouse exits the component.
	 * 
	 * @param e the mouse event
	 */
	public synchronized void mouseExited(MouseEvent e)
	{
		fMousePosition = null;

		if (fLabelEnabled)
		{
			getParent().repaint();
		}
	}

	/**
	 * Invoked when the mouse has been clicked on a component.
	 * 
	 * @param e the mouse event
	 */
	public void mouseClicked(MouseEvent e)
	{
	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 * 
	 * @param e the mouse event
	 */
	public void mousePressed(MouseEvent e)
	{
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 * 
	 * @param e the mouse event
	 */
	public void mouseReleased(MouseEvent e)
	{
	}

    /**
	 * Invoked when the mouse enters a component.
	 * 
	 * @param e the mouse event
	 */
	public void mouseEntered(MouseEvent e)
	{
	}

	/**
	 * Get the base name of the buffer for this label renderer.
	 * @return Returns the base name of the buffer.
	 */
	public String getBaseName()
	{
		return fBufferBaseName;
	}

	/**
	 * Set the base name of the buffer to render a label for.
	 * @param baseName The base name of the buffer to display label for.
	 */
	public void setBaseName(String baseName)
	{
		fBufferBaseName = baseName;
	}
}

//--- Development History ---------------------------------------------------
//
//  $Log: TwinkleLabelRenderer.java,v $
//  Revision 1.2  2006/04/27 18:51:52  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2006/02/14 20:08:01  tames
//  Added support for specifying a specific buffer base name to visualize. This is
//  needed if the BasisSet has more than one pixel bundle in it.
//
//