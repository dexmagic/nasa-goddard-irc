//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/axis/AbstractAxisRenderer.java,v 1.1 2004/12/16 23:01:15 tames Exp $
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

package gov.nasa.gsfc.irc.gui.vis.axis;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gov.nasa.gsfc.commons.numerics.formats.SciDecimalFormat;
import gov.nasa.gsfc.commons.numerics.formats.ValueFormat;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.gui.vis.VisRenderer;


/**
 * This abstract class provides the basic functionality for an axis
 * renderer.  A axis renderer is typically responsible for drawing 
 * the axis itself, tick marks, and labels for the tick marks.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/12/16 23:01:15 $
 * @author	Troy Ames
 */
public abstract class AbstractAxisRenderer implements VisRenderer, ChangeListener
{
	/** 
	 * The Axis model for the implementing class. For performance reasons this 
	 * field is protected only.
	 */
	protected AxisModel fAxisModel = null;

	/** 
	 * The parent JComponent for this renderer. For performance reasons this 
	 * field is protected only.
	 */
	protected JComponent fParent = null;

	//  Tick properties
	private Tick[] fTicks = null;
	private TickFactory fTickFactory = null;

	// Label formatter
	private ValueFormat fFormatter = null;

	/**
	 * Create a new axis renderer with default model, tick factory, and 
	 * label formatters.
	 */
	public AbstractAxisRenderer()
	{
		this(null, null, null);
	}

	/**
	 * Create a new axis using the designated model. If the model is null one
	 * will be created.
	 *
	 * @param model  the model of the axis.
	 */
	public AbstractAxisRenderer(AxisModel model)
	{
		this(model, null, null);
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
	public AbstractAxisRenderer(
			AxisModel model, TickFactory factory, ValueFormat formatter)
	{
		fAxisModel = model;
		fTickFactory = factory;
		fFormatter = formatter;
		
		if (fAxisModel == null)
		{
			fAxisModel = new DefaultAxisModel();
		}
		
		if (fTickFactory == null)
		{
			fTickFactory = new EvenTickFactory();
		}
		
		if (fFormatter == null)
		{
			fFormatter = new SciDecimalFormat();
		}
		
		fTicks = fTickFactory.createTicks(fAxisModel);
	}

	/**
	 * Get the amount of horizontal space needed to draw the axis 
	 * given the current axis properties.  This should
	 * take into account the room needed to print the tick marks and the
	 * corresponding text.
	 *
	 * @param g2d  the graphics context
	 * @return the preferred width
	 */
	public abstract double getPreferredWidth(Graphics2D g2d);

	/**
	 * Get the amount of vertical space needed to draw the axis 
	 * given the current axis properties.  This should
	 * take into account the room needed to print the tick marks and the
	 * corresponding text.
	 *
	 * @param g2d  the graphics context
	 * @return the preferred width
	 */
	public abstract double getPreferredHeight(Graphics2D g2d);

    /**
     * Get the amount of space needed to draw the axis 
	 * given the current axis properties. Calls the abstract methods
	 * <code>getPreferredWidth</code> and <code>getPreferredHeight</code>.
     *
     * @return the preferred size of this renderer.
     */
    public Dimension getPreferredSize() {
        Dimension size = new Dimension();
        Graphics2D g2d = (Graphics2D) fParent.getGraphics();
        
        size.setSize(getPreferredWidth(g2d), getPreferredHeight(g2d));
        
        return size;
    }

	/**
	 * Set the tick marks for this axis.
	 *
	 * @param ticks  an array of tick marks to display on the axis
	 */
	public void setTicks(Tick[] ticks)
	{
		if (ticks != null)
		{
			fTicks = ticks;
		}
	}

	/**
	 * Get the tick marks for this axis. To force and update of the ticks use
	 * the <code>updateTicks</code> method instead.
	 *
	 * @return an array of tick marks to display on the axis
	 * @see #updateTicks()
	 */
	public Tick[] getTicks()
	{
		if (fTicks == null)
		{
			fTicks = updateTicks();
		}
		
		return fTicks;
	}

	/**
	 * Update and return the tick marks for this axis.
	 * 
	 * @return an array of updated ticks.
	 */
	protected Tick[] updateTicks()
	{
		fTicks = fTickFactory.createTicks(fAxisModel);
		return fTicks;
	}

	/**
	 * Draw the axis, tick marks, and tick mark labels for this axis.
	 *
	 * @param g2d	the graphics context on which to draw
	 * @param rect	the unclipped drawing area bounds for rendering. This 
	 * 				rectangle is used for sizing the axis.
	 * @param dataSet the data to render if applicable.
	 *
	 * @return a rectangle that can be passed to other renderers. Typically this
	 * 		is identical to the rectangle passed in.
	 */
	public abstract Rectangle2D draw(
			Graphics2D g2d, Rectangle2D rect, DataSet dataSet);

	/**
	 * Set the axis model. Forces and update to the ticks.
	 * 
	 * @param model the axis model
	 */
	public void setAxisModel(AxisModel model)
	{
		fAxisModel = model;
		updateTicks();
	}

    /**
     * Get the axis model.
     * 
	 * @param model the axis model
	 */
	public AxisModel getAxisModel()
	{
		return fAxisModel;
	}

	/**
	 * Set the tick factory for this axis. Forces an update to the ticks.
	 * 
	 * @param factory the tick factory
	 */
	public void setTickFactory(TickFactory factory)
	{
		fTickFactory = factory;
		updateTicks();
	}
	
	/**
	 * Get the tick factory.
	 * 
	 * @param factory the tick factory
	 */
	public TickFactory getTickFactory()
	{
		return fTickFactory;
	}
	
	/**
	 * Set the parent component that will use this renderer to draw its image.
	 *
	 * @param parent  the parent component
	 */
	public void setParent(JComponent component)
	{
		fParent = component;
	}
	
	/**
	 * Get the parent component to this axis renderer
	 * 
	 * @return the parent component
	 */
	public JComponent getParent()
	{
		return fParent;
	}
	
	/**
	 * Get the label formatter.
	 * 
	 * @return Returns the formatter.
	 */
	public ValueFormat getLabelFormatter()
	{
		return fFormatter;
	}
	
	/**
	 * Set the label formatter.
	 * 
	 * @param formatter The formatter to set.
	 */
	public void setLabelFormatter(ValueFormat formatter)
	{
		fFormatter = formatter;
	}

	/**
	 * Invoked when a model of interest has changed its state. Calls 
	 * <code>repaint</code> on the parent component.
	 *
	 * @param event  a ChangeEvent object
	 */
	public void stateChanged(ChangeEvent event)
	{
		// We don't really care what changed, but assume we need to redraw to
		// reflect the change.
		if (fParent != null)
		{
			fParent.repaint();
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractAxisRenderer.java,v $
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.4  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.3  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2004/11/16 18:49:41  tames
//  Java doc updates
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//
