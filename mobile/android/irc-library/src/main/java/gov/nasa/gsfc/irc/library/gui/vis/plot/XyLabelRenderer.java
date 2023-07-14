//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/gui/vis/plot/XyLabelRenderer.java,v 1.6 2005/10/11 03:12:35 tames Exp $
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

package gov.nasa.gsfc.irc.library.gui.vis.plot;

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
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.JComponent;

import gov.nasa.gsfc.commons.numerics.formats.SciDecimalFormat;
import gov.nasa.gsfc.commons.numerics.formats.TimeFormat;
import gov.nasa.gsfc.commons.numerics.formats.ValueFormat;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.vis.AbstractVisRenderer;
import gov.nasa.gsfc.irc.gui.vis.VisUtil;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisScale;
import gov.nasa.gsfc.irc.gui.vis.axis.DefaultAxisModel;
import gov.nasa.gsfc.irc.gui.vis.channel.ChannelModel;


/**
 * This class optionally renders a label at the current mouse location when the 
 * mouse enters the parent component. There are two types of labels that can
 * be enabled, a position label and a data label. 
 * A position label displays the mouses current position in the 
 * same scale and units as the axis. A data label will display a channel's name 
 * and value when the mouse is positioned close to a data point. When enabled a 
 * data label will supersede a position label. This renderer should be at the
 * end of a render chain to prevent the label from being painted over by 
 * another renderer.
 * 
 * <p>This class can be used in a Decorator pattern for a parent 
 * component that delegates the actual drawing to this renderer. 
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/10/11 03:12:35 $
 * @author Troy Ames
 */
public class XyLabelRenderer extends AbstractVisRenderer
	implements MouseMotionListener, MouseListener
{
	private static final String CLASS_NAME = XyLabelRenderer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	// Distance from a data point, in pixels, that the mouse pointer 
	// must be within for a label to be displayed.
	private double fTolerance = 4.0;

	//  Property values
	private boolean fLabelEnabled = true;
	private boolean fDataLabelEnabled = true;
	private boolean fPositionLabelEnabled = true;

	//  Label info.
	private Point fMousePosition = null;
	private ValueFormat fXFormatter;
	private ValueFormat fYFormatter;

	// Axis info.
	private AxisModel fXAxisModel = null;
	private AxisModel fYAxisModel = null;

	// Data selection fields
	private ChannelModel fChannelModel;
	private String fXAxisChannel = null;
	private LinkedHashSet fYAxisChannels = new LinkedHashSet(5);

	/**
	 * Create a new label renderer with default axis models.
	 */
	public XyLabelRenderer()
	{
		this(null, null, null);
	}

	/**
	 * Create a new label renderer with the given axis models.
	 * 
	 * @param xAxis the X axis model
	 * @param yAxis the Y axis model
	 */
	public XyLabelRenderer(AxisModel xAxis, AxisModel yAxis)
	{
		this(xAxis, yAxis, null);
	}
	
	/**
	 * Create a new label renderer with the given axis models.
	 * 
	 * @param xAxis the X axis model
	 * @param yAxis the Y axis model
	 * @param channels the channel model for this renderer, currently not used
	 */
	public XyLabelRenderer(AxisModel xAxis, AxisModel yAxis, ChannelModel channels)
	{
		super();

		fXAxisModel = xAxis;
		fYAxisModel = yAxis;
		fChannelModel = channels;
		
		if (fChannelModel == null)
		{
			//fChannelModel = new DefaultChannelModel();
		}

		if (fXAxisModel == null)
		{
			fXAxisModel = new DefaultAxisModel();
		}
		
		if (fYAxisModel == null)
		{
			fYAxisModel = new DefaultAxisModel();
		}
		
		if (fXFormatter == null)
		{
			fXFormatter = new TimeFormat();
		}

		if (fYFormatter == null)
		{
			fYFormatter = new SciDecimalFormat();
		}

		fXAxisModel.addChangeListener(this);
		fYAxisModel.addChangeListener(this);
		
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
	 * Set whether or not the drawing of a data label is enabled.
	 * 
	 * @param enabled true to enable data labels
	 */
	public final void setDataLabelEnabled(boolean enable)
	{
		fDataLabelEnabled = enable;
		
		setLabelEnabled(fDataLabelEnabled | fPositionLabelEnabled);
	}

	/**
	 * Get whether or not the drawing of a data label is enabled.
	 * 
	 * @return whether or not the drawing of a data label is enabled.
	 */
	public boolean getDataLabelEnabled()
	{
		return fDataLabelEnabled;
	}

	/**
	 * Set whether or not the drawing of a position label is enabled.
	 * 
	 * @param enabled true to enable position labels
	 */
	public final void setPositionLabelEnabled(boolean enable)
	{
		fPositionLabelEnabled = enable;
		
		setLabelEnabled(fDataLabelEnabled | fPositionLabelEnabled);
	}

	/**
	 * Get whether or not the drawing of a position label is enabled.
	 * 
	 * @return whether or not position labels are enabled.
	 */
	public boolean getPositionLabelEnabled()
	{
		return fPositionLabelEnabled;
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
	 * Draw the label on the given graphics context.
	 * 
	 * @param g2d the graphics context on which to draw.
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param dataSet the data to render.
	 * 
	 * @return the same rectangle instance received.
	 */
	public synchronized Rectangle2D draw(
			Graphics2D g2d, Rectangle2D rect, DataSet dataSet)
	{
		
		if (fLabelEnabled && fMousePosition != null)
		{			
			Point2D screenPoint = VisUtil.getRelativePoint(fMousePosition, rect);
			
			// Update the text of the label
			String labelText = null;
			
			if (fDataLabelEnabled && dataSet != null)
			{
				// Try to create a data based label first
				labelText = getDataLabel(rect, screenPoint, dataSet);
			}

			if (fPositionLabelEnabled && labelText == null)
			{
				// Translate mouse position into domain units
				Point2D valuePoint = VisUtil.screenPointToDomain(
					screenPoint, rect, fXAxisModel, fYAxisModel);
				
				// Get a position label if we did not find a data label
				labelText = getPositionLabel(valuePoint);
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

		return rect;
	}

	/**
	 * Get the mouse position label.
	 * 
	 * @param valuePoint the point to convert into a label.
	 * @return a label
	 */
	protected String getPositionLabel(Point2D valuePoint)
	{
		String label = 
			" [" + fXFormatter.format(valuePoint.getX()) + ", " 
			+ fYFormatter.format(valuePoint.getY()) + "]";
		
		return label;
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
		
		double minDistance = Double.MAX_VALUE;
		double minXValue = 0.0;
		double minYValue = 0.0;
		DataBuffer minBuffer = null;
		boolean dataPointFound = false;
		
		//  Axis model info
		double minX = fXAxisModel.getViewMinimum();
		double maxX = minX + fXAxisModel.getViewExtent();
		double minY = fYAxisModel.getViewMinimum();
		double maxY = minY + fYAxisModel.getViewExtent();

		AxisScale xScale = fXAxisModel.getAxisScale();
		AxisScale yScale = fYAxisModel.getAxisScale();

		// Drawing canvas bounds information
		double canvasLeft = rect.getMinX();
		double canvasBottom = rect.getMaxY();

		//  Canvas width and height
		double canvasHeight = rect.getHeight();
		double canvasWidth = rect.getWidth();

		Object[] dataChannels = fYAxisChannels.toArray();
		int numChannels = dataChannels.length;
		Iterator basisSets = dataSet.getBasisSets().iterator();

		while (basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			DataBuffer xBuffer = basisSet.getDataBuffer(fXAxisChannel);
			
			if (xBuffer == null)
			{
				xBuffer = basisSet.getBasisBuffer();
				
				if (!xBuffer.getName().equals(fXAxisChannel))
				{
					// Basis buffer is not the x axis
					xBuffer = null;
				}
				
				// TODO if the x axis is the basis buffer then you can take
				// advantage of this and implement a more efficient search
				// below.
			}

			if (xBuffer != null)
			{
				for (int i = 0; i < numChannels; i++)
				{
					DataBuffer dataBuffer = basisSet
							.getDataBuffer((String) dataChannels[i]);

					if (dataBuffer != null)
					{
						// Temp variables
						int samples = xBuffer.getSize();
						double screenX = 0.0;
						double screenY = 0.0;
						double xValue = 0.0;
						double yValue = 0.0;
						double xRatio = 0.0;
						double yRatio = 0.0;
						double distance = 0.0;

						// We have x and y buffers, look for closest point.
						for (int j = 0; j < samples; j++)
						{
							// Translate and scale the x to the view
							// coordinates.
							xValue = xBuffer.getAsDouble(j);
							xRatio = xScale.getScaleRatio(xValue, minX, maxX-minX);
							screenX = (canvasWidth * xRatio) + canvasLeft;

							// Check if the x coord is even close enough
							// to continue calculating the y coord an distance
							if (Math.abs(screenX - mouseX) < fTolerance)
							{
								// Translate and scale the y to the view
								// coordinates.
								yValue = dataBuffer.getAsDouble(j);
								yRatio = yScale.getScaleRatio(yValue, minY, maxY-minY);
								screenY = canvasBottom - (canvasHeight * yRatio);
	
								distance = VisUtil.distance(
									mouseX, mouseY, screenX, screenY);
	
								// Check if this is the closest point so far
								if (minDistance > distance)
								{
									minDistance = distance;
									minXValue = xValue;
									minYValue = yValue;
									minBuffer = dataBuffer;
	
									if (minDistance < fTolerance)
									{
										// We have found a point that matches
										dataPointFound = true;
									}
								}
							}
						}
					}
				}
			}
			
			if (dataPointFound)
			{
				StringBuffer buffer = new StringBuffer();
				buffer.append(minBuffer.getName()
					+ " [" + fXFormatter.format(minXValue) + ", " 
					+ fYFormatter.format(minYValue) + "]");
				resultString = buffer.toString();
			}
		}
		
		return resultString;
	}
	
	/**
	 * Get the formatter for the Y component of the mouse label.
	 * 
	 * @return Returns the formatter.
	 */
	public ValueFormat getYLabelFormatter()
	{
		return fYFormatter;
	}
	
	/**
	 * Set the formatter for the Y component of the mouse label.
	 * 
	 * @param formatter The formatter to set.
	 */
	public void setYLabelFormatter(ValueFormat formatter)
	{
		fYFormatter = formatter;
	}

	/**
	 * Get the formatter for the X component of the mouse label.
	 * 
	 * @return Returns the formatter.
	 */
	public ValueFormat getXLabelFormatter()
	{
		return fXFormatter;
	}
	
	/**
	 * Set the formatter for the X component of the mouse label.
	 * 
	 * @param formatter The formatter to set.
	 */
	public void setXLabelFormatter(ValueFormat formatter)
	{
		fXFormatter = formatter;
	}

	/**
	 * Set the pattern for the X component of the mouse label.
	 * 
	 * @param pattern The format pattern.
	 */
	public void setXLabelFormatPattern(String pattern)
	{
		fXFormatter.setPattern(pattern);
	}

	/**
	 * Get the pattern for the X component of the mouse label.
	 * 
	 * @return the pattern
	 */
	public String getXLabelFormatPattern()
	{
		return fXFormatter.getPattern();
	}
	
	/**
	 * Set the pattern for the Y component of the mouse label.
	 * 
	 * @param pattern The format pattern.
	 */
	public void setYLabelFormatPattern(String pattern)
	{
		fYFormatter.setPattern(pattern);
	}

	/**
	 * Get the pattern for the Y component of the mouse label.
	 * 
	 * @return the pattern
	 */
	public String getYLabelFormatPattern()
	{
		return fYFormatter.getPattern();
	}

	/**
	 * Get the X axis channel to use as the basis. 
	 * This method may be replaced 
	 * in the future when channel selection is formalized.
	 * 
	 * @return the X axis channel.
	 */
	public String getXAxisChannel()
	{
		return fXAxisChannel;
	}
	
	/**
	 * Set the X axis channel that this renderer should use.
	 * This method may be replaced 
	 * in the future when channel selection is formalized.
	 * 
	 * @param channelName The X Axis selection to set.
	 */
	public void setXAxisChannel(String channelName)
	{
		fXAxisChannel = channelName;
	}
	
	/**
	 * Get the Y axis channels that this renderer should use.
	 * This method may be replaced 
	 * in the future when channel selection is formalized.
	 * 
	 * @return Returns the Y axis channels.
	 */
	public LinkedHashSet getYAxisChannels()
	{
		return fYAxisChannels;
	}
	
	/**
	 * Set the Y axis channels that this renderer should use.
	 * This method may be replaced 
	 * in the future when channel selection is formalized.
	 * 
	 * @param channelNames The Y axis channels.
	 */
	public void setYAxisChannels(String channelNames)
	{
		fYAxisChannels.clear();
		
		if (channelNames != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(channelNames, "|");
			
			int numNames = tokenizer.countTokens();
			
			for (int i = 0; i < numNames; i++)
			{
				String bufferName = tokenizer.nextToken().trim();
				
				fYAxisChannels.add(bufferName);
			}
		}
	}

	/**
	 * Get the distance from a data point, in pixels, that the mouse pointer 
	 * must be within for a label to be displayed.
	 * 
	 * @return Returns the tolerance.
	 */
	public double getTolerance()
	{
		return fTolerance;
	}
	
	/**
	 * Set the distance from a data point, in pixels, that the mouse pointer 
	 * must be within for a label to be displayed.
	 * 
	 * @param tolerance The tolerance to set.
	 */
	public void setTolerance(double tolerance)
	{
		fTolerance = tolerance;
	}
}

//--- Development History ---------------------------------------------------
//
//  $Log: XyLabelRenderer.java,v $
//  Revision 1.6  2005/10/11 03:12:35  tames
//  Reflects changes to the AxisModel interface.
//
//  Revision 1.5  2005/08/12 21:19:46  tames_cvs
//  Modified the addChannels method to clear any existing channels.
//
//  Revision 1.4  2005/07/15 19:40:46  tames
//  Updated to reflect DataBuffer changes.
//
//  Revision 1.3  2005/03/17 00:23:38  chostetter_cvs
//  Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
//  Revision 1.2  2004/12/18 06:41:22  tames
//  Fixed null pointer exception before data is received.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.3  2004/12/14 21:45:30  tames
//  Updates to reflect using an external axis scale along with
//  some refactoring of interfaces.
//
//  Revision 1.2  2004/11/19 19:17:17  tames
//  Added a slightly transparent  background to the label making it easier
//  to read.
//
//  Revision 1.1  2004/11/19 04:20:55  tames
//  Initial Version
//
//