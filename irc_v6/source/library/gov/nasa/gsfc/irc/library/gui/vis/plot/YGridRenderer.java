//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/gui/vis/plot/YGridRenderer.java,v 1.2 2005/10/11 03:13:05 tames Exp $
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.vis.AbstractVisRenderer;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisScale;
import gov.nasa.gsfc.irc.gui.vis.axis.DefaultAxisModel;
import gov.nasa.gsfc.irc.gui.vis.axis.EvenTickFactory;
import gov.nasa.gsfc.irc.gui.vis.axis.Tick;
import gov.nasa.gsfc.irc.gui.vis.axis.TickFactory;


/**
 * This class renders horizontal grid lines on a graphics context. The placement 
 * of the lines of the grid are a function of the axis model and a tick factory. 
 * In order for the grid to match the ticks of an axis component this renderer
 * must use the same axis model and the same type of tick factory as the axis.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 03:13:05 $
 * @author	Troy Ames
 */
public class YGridRenderer extends AbstractVisRenderer
{
	private static final String CLASS_NAME = YGridRenderer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private boolean fLinesVisible = true;
     
    private AxisModel fAxisModel = null;
	private TickFactory fTickFactory = null;
	private Color fGridColor = new Color(224, 224, 224);

	/**
	 * Create a new grid renderer with default axis model and tick factory.
	 */
	public YGridRenderer()
	{
		this(null, null);
	}

	/**
	 * Create a new grid renderer with the given axis model and 
	 * a default tick factory.
	 * 
	 * @param model the axis model
	 */
	public YGridRenderer(AxisModel model)
	{
		this(model, null);
	}

	/**
	 * Create a new grid renderer with the given axis model and tick factory.
	 * 
	 * @param model the axis model
	 * @param factory the tick factory
	 */
	public YGridRenderer(AxisModel model, TickFactory factory)
	{
		super();
		
		fAxisModel = model;		
		fTickFactory = factory;
		
		if (fAxisModel == null)
		{
			fAxisModel = new DefaultAxisModel();
		}

		if (fTickFactory == null)
		{
			fTickFactory = new EvenTickFactory();
		}
	}

	/**
	 * Draw the grid lines on the given graphics context.
	 *  
	 * @param g2d  the graphics context on which to draw.
	 * @param rect the unclipped drawing area bounds for rendering.
	 * @param dataSet the data to render.
	 *
	 * @return the same rectangle instance received.
	 */
	public Rectangle2D draw(Graphics2D g2d, Rectangle2D rect, DataSet dataSet)
	{
		Color savedColor = g2d.getColor();
		Composite savedComposite = g2d.getComposite();
		
		g2d.setColor(fGridColor);
		g2d.setComposite(AlphaComposite.SrcOver);
		
		if (fLinesVisible)
		{
			drawGrid(g2d, rect);
		}
		
		g2d.setColor(savedColor);
		g2d.setComposite(savedComposite);
		
		return rect;
	}
	
	/**
	 * Actually draw the grid lines on the given graphics context.
	 *  
	 * @param g2d  the graphics context on which to draw.
	 * @param rect the unclipped drawing area bounds for rendering.
	 */
	protected void drawGrid(Graphics2D g2d, Rectangle2D rect)
	{
		// Axis information
		double min = fAxisModel.getViewMinimum();
		double max = min + fAxisModel.getViewExtent();
		AxisScale axisScale = fAxisModel.getAxisScale();
		
		// Drawing canvas bounds information
		double canvasLeft = rect.getMinX();		
		double canvasRight = rect.getMaxX() - 1;		
		double canvasBottom = rect.getMaxY() - 1;

		//  Canvas width and height
		double canvasHeight = rect.getHeight() - 1;
		double canvasWidth = rect.getWidth() - 1;
		
		Tick[] ticks = fTickFactory.createTicks(fAxisModel);
		
		//  Tick information
		double tickValue = 0;
		double tickRatio = 0;
		double tickPosition = 0;
		Line2D.Double gridLine = new Line2D.Double();
		
        //  Figure out the position of each tick and label on the axis.
		for (int i = 0; i < ticks.length; i++)
		{
			tickValue = ticks[i].getValue();
			tickRatio = axisScale.getScaleRatio(tickValue, min, max-min);
			tickPosition = canvasBottom - (tickRatio * canvasHeight);
			
			//  Set the position of the tick mark.
			gridLine.setLine(
				canvasLeft, tickPosition, canvasRight, tickPosition);
			
			//  Draw the tick mark and its label.
			g2d.draw(gridLine);
		}
	}

	/**
	 * Get the color of the grid lines.
	 * 
	 * @return Returns the grid Color.
	 */
	public Color getGridColor()
	{
		return fGridColor;
	}
	
	/**
	 * Set the color to draw the grid line.
	 * 
	 * @param color The gridColor to set.
	 */
	public void setGridColor(Color color)
	{
		fGridColor = color;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: YGridRenderer.java,v $
//  Revision 1.2  2005/10/11 03:13:05  tames
//  Reflects changes to the AxisModel interface.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
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