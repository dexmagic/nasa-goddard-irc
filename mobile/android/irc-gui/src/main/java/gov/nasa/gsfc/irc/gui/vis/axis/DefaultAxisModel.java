//=== File Prolog ============================================================
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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import gov.nasa.gsfc.commons.numerics.math.Utilities;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.library.gui.vis.scale.LinearScale;

/**
 * Concrete class that implements the AxisModel interface describing the 
 * current state of an axis. ChangeEvents are sent to all registered 
 * listeners if an actual change in the model results from a set method.
 * An axis model contains
 * four interrelated double properties: axis minimum, axis maximum, view position
 * and view extent. These four doubles define two nested ranges like this:
 * 
 * <pre>
 * axisMinimum &lt;= view &lt;= view+extent &lt;= axisMaximum
 * </pre>
 * 
 * The outer range is <code>axisMinimum, axisMaximum</code> and the inner
 * range is <code>view, view+extent</code>. This class places the priority on 
 * the inner view range by adjusting the outer range if necessary to accommodate
 * changes to the inner range.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 02:59:46 $
 * @author 	Troy Ames
 */
public class DefaultAxisModel implements AxisModel
{
	private double fAxisMin = -2.0d;
	private double fAxisMax = 2.0d;
	private double fView = fAxisMin;
	private double fExtent = fAxisMax - fView;
	private boolean fAutoScale = false;
	private boolean fIsAdjusting = false;
	private static final double EQUAL_EPSILON = 1.0E-12;
	private AxisScale fScale = new LinearScale();
	
    /** A list of event listeners for this component. */
    protected EventListenerList fListenerList = new EventListenerList();

    /**
     * Only one <code>ChangeEvent</code> is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this". The event is lazily
     * created the first time that an event notification is fired.
     * 
     * @see #fireStateChanged
     */
    protected transient ChangeEvent fChangeEvent = null;
	private int fMajorTicks = 5;
	private double fReference = 0.0d;

	/**
	 * Create an AxisModel
	 */
	public DefaultAxisModel()
	{
		super();
	}

    /**
	 * Sets the <code>modelIsAdjusting</code> property.
	 * 
	 * @see #getModelIsAdjusting
	 * @see AxisModel#setModelIsAdjusting
	 */
	public void setModelIsAdjusting(boolean b)
	{
		// setRangeProperties(value, extent, min, max, b);
		fIsAdjusting = b;
	}

	/**
	 * Returns true if the model is in the process of changing as a result of
	 * actions being taken by the user.
	 * 
	 * @return the value of the <code>modelIsAdjusting</code> property
	 * @see AxisModel#getModelIsAdjusting
	 */
	public boolean getModelIsAdjusting()
	{
		return fIsAdjusting;
	}

	/**
	 * Set the view minimum value of the axis.
	 * 
	 * @param view the view value of the axis
	 */
	public synchronized void setViewMinimum(double view)
	{
		setRangeProperties(view, fExtent, fAxisMin, fAxisMax, fIsAdjusting);
	}

	/**
	 * Get the minimum view value of the axis.
	 *
	 * @return  the minimum value of the axis
	 */
	public double getViewMinimum()
	{
		return fView;
	}

	/**
	 * Set the extent of the view of the axis.
	 *
	 * @param newExtent  the extent of the view of the axis
	 */
	public synchronized void setViewExtent(double newExtent)
	{
		setRangeProperties(fView, newExtent, fAxisMin, fAxisMax, fIsAdjusting);
	}

	/**
	 * Get the extent of the view of the axis.
	 *
	 * @return  the extent of the view of the axis
	 */
	public double getViewExtent()
	{
		return fExtent;
	}

	/**
	 * Set the visible view value and extent on the axis.
	 * 
	 * @param view the view value on the axis
	 * @param extent the extent of the view on the axis
	 */
	public synchronized void setViewRange(double view, double extent)
	{
		setRangeProperties(view, extent, fAxisMin, fAxisMax, fIsAdjusting);
	}

    /**
	 * Sets the axis minimum to <I>min</I> after ensuring that <I>min</I> 
	 * obeys the model's constraints:
	 * 
	 * <pre>
	 * axisMinimum &lt;= viewMinimum
	 * </pre>
	 * @param min the axis minimum
	 * @see #getMinimum
	 * @see gov.nasa.gsfc.irc.gui.vis.axis.AxisModel#setMinimum(double)
	 */
	public void setMinimum(double min)
	{
		double oldMin = fAxisMin;
		
		if (min > fView)
		{
			min = fView;
		}
		
		fAxisMin = min;
		
		// Notify listeners if this is a change
		if (Utilities.compareDouble(oldMin, fAxisMin, EQUAL_EPSILON) != 0)
		{
			fireStateChanged();
		}
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.gui.vis.axis.AxisModel#getMinimum()
	 */
	public double getMinimum()
	{
		return fAxisMin;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.gui.vis.axis.AxisModel#setMaximum(double)
	 */
	public void setMaximum(double max)
	{
		double oldMax = fAxisMax;
		
		if (max < fView + fExtent)
		{
			max = fView + fExtent;
		}
		
		fAxisMax = max;
		
		// Notify listeners if this is a change
		if (Utilities.compareDouble(fAxisMax, oldMax, EQUAL_EPSILON) != 0)
		{
			fireStateChanged();
		}
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.gui.vis.axis.AxisModel#getMaximum()
	 */
	public double getMaximum()
	{
		return fAxisMax;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.gui.vis.axis.AxisModel#setRangeProperties(double, double, double, double)
	 */
	public void setRangeProperties(
			double view, double extent, 
			double axisMinimum, double axisMaximum, boolean adjusting)
	{
		boolean stateChange = false;
		
		// Check that the parameters fall within the model's constraints:
		// axisMinimum <= view <= view+extent <= axisMaximum
		
		if (axisMinimum > view)
		{
			axisMinimum = view;
		}
		
		if (extent < 0.0)
		{
			extent = 0.0;
		}

		if (view + extent > axisMaximum)
		{
			axisMaximum = view + extent;
		}

		// Check if there are any changes with current state
		if (Utilities.compareDouble(view, fView, EQUAL_EPSILON) != 0
			|| Utilities.compareDouble(extent, fExtent, EQUAL_EPSILON) != 0
			|| Utilities.compareDouble(axisMinimum, fAxisMin, EQUAL_EPSILON) != 0
			|| Utilities.compareDouble(axisMaximum, fAxisMax, EQUAL_EPSILON) != 0
			|| adjusting != fIsAdjusting)
		{
			stateChange = true;
		}
		
		if (stateChange)
		{
			// Update the state
			fAxisMin = axisMinimum;
			fAxisMax = axisMaximum;
			fView = view;
			fExtent = extent;
			fIsAdjusting = adjusting;
			fireStateChanged();
		}
	}

	/**
	 * Sets the preferred number of ticks on this axis. Clients can use the
	 * returned value to seed TickFactory methods in order to get consistent
	 * axis tick spacing.
	 * 
	 * @param n the preferred number of ticks for this access.
	 */
	public void setNumberOfTicks(int n)
	{
		if (n != fMajorTicks)
		{
			fMajorTicks = n;
			fireStateChanged();
		}
	}

	/**
	 * Gets the preferred number of ticks on this axis. Clients can use the
	 * returned value to seed TickFactory methods in order to get consistent
	 * axis tick spacing.
	 * 
	 * @return the preferred number of ticks for this access.
	 */
	public int getNumberOfTicks()
	{
		return fMajorTicks;
	}

	/**
	 * Set whether or not the axis should be auto scaled.
	 *
	 * @param autoScale  whether or not to auto scale the axis
	 */
	public void setAutoScale(boolean autoScale)
	{
		if (autoScale != fAutoScale)
		{
			fAutoScale = autoScale;
			fireStateChanged();
		}
	}

	/**
	 * Get whether or not the axis should be auto scaled.
	 *
	 * @return  whether or not the axis is auto scaled.
	 */
	public boolean isAutoScale()
	{
		return fAutoScale;
	}
	
	/**
	 * Adds a ChangeListener to the model.
	 * 
	 * @param l the ChangeListener to add
	 * @see #fireStateChanged
	 * @see #removeChangeListener
	 */
	public void addChangeListener(ChangeListener l)
	{
		fListenerList.add(ChangeListener.class, l);
	}

	/**
	 * Removes a ChangeListener from the model.
	 * 
	 * @param l the ChangeListener to remove
	 * @see #fireStateChanged
	 * @see #addChangeListener
	 */
	public void removeChangeListener(ChangeListener l)
	{
		fListenerList.remove(ChangeListener.class, l);
	}

	/**
	 * Returns an array of all the <code>ChangeListener</code> objects added to 
	 * this model with addChangeListener().
	 * 
	 * @return array of <code>ChangeListener</code> objects or an empty
	 *         array if no listeners have been added
	 */
	public ChangeListener[] getChangeListeners()
	{
		return (ChangeListener[]) fListenerList
				.getListeners(ChangeListener.class);
	}

	/**
	 * Send a ChangeEvent, whose source is this model, to each listener. This
	 * method is called each time the model changes.
	 * 
	 * @see #addChangeListener
	 * @see EventListenerList
	 */
	protected void fireStateChanged()
	{
		Object[] listeners = fListenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ChangeListener.class)
			{
				if (fChangeEvent == null)
				{
					fChangeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(fChangeEvent);
			}
		}
	}

	/**
	 * Set the reference value for the axis. A reference value is a baseline 
	 * or context specific value that has special meaning for this axis.
	 *
	 * @param value  the reference value for the axis
	 */
	public void setReference(double value)
	{
		fReference = value;
	}

	/**
	 * Get the reference value for the axis. The default value is 0.
	 *
	 * @return  the reference value for the axis
	 */
	public double getReference()
	{
		return fReference;
	}

    /**
	 * Set the scale that visualizations should use for this axis. The 
	 * name must exist in the Type map file.
	 * 
	 * @param name the name of the scale for this axis
	 * @deprecated this is only temp fix for not having a AxisScale property editor.
	 */
	public void setAxisScale(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException(
				"AxisScale name parameter cannot be null");
		}
		
		AxisScale scale = 
			(AxisScale) Irc.instantiateFromTypemap("AxisScale", name);
			
		if (scale != null)
		{
			fScale = scale;
		}
	}

    /**
	 * Set the scale that visualizations should use for this axis.
	 * 
	 * @param scale the scale for this axis
	 */
	public void setAxisScale(AxisScale scale)
	{
		if (scale == null)
		{
			throw new IllegalArgumentException(
				"AxisScale parameter cannot be null");
		}
		
		fScale = scale;
	}

    /**
	 * Get the scale that visualizations should use for this axis.
	 * 
	 * @return the scale for this axis
	 */
	public AxisScale getAxisScale()
	{
		return fScale;
	}

	/**
	 * Applies the scale to the value and returns a ratio of where the
	 * given value is relative to the view minimum and in units the size of
	 * the view extent. For example, with a linear scale 
	 * and the minimum = 100 and the extent = 100, this method will return
	 * 0.5 for a value of 150, 1.0 for a value of 200, and -0.5 for a value of 
	 * 50.
	 * 
	 * @param value the value to scale
	 * @return a ratio relative to the visible view value
	 */
	public double getScaleRatio(double value)
	{
		return fScale.getScaleRatio(value, fView, fExtent);
	}

	/**
	 * Determines for the axis scale what value would result in the given ratio
	 * relative to the view minimum and in units the size of the extent. 
	 * This is the inverse of <code>getScaleRatio</code>
	 * and is necessary when translating from a screen relative coordinate to an
	 * axis domain value.
	 * <p>
	 * For example, with a linear scale and the minimum = 100 and the extent =
	 * 100, this method will return 150.0 for a ratio of 0.5, 200.0 for a ratio
	 * of 1.0, and 50 for a ratio of -0.5.
	 * 
	 * @param ratio a ratio relative to the visible view value and extent
	 * @return a value when scaled results in the given ratio
	 */
	public double getScaleValue(double ratio)
	{
		return fScale.getScaleValue(ratio, fView, fExtent);
	}

}


//--- Development History ---------------------------------------------------
//
//  $Log: DefaultAxisModel.java,v $
//  Revision 1.4  2005/10/11 02:59:46  tames
//  Reflects changes to the AxisModel interface to support AxisScrollBars.
//
//  Revision 1.3  2005/09/23 20:41:17  tames
//  Updated JavaDoc, synchronized some of the set methods, and reduced the
//  size of the EQUAL_EPSILON constant.
//
//  Revision 1.2  2005/08/11 20:06:35  mn2g
//  sets the models 'reference' value to be max+min/2.0.  This makes the axisscaler work right..
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.5  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.4  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.3  2004/11/09 16:29:18  tames
//  Added a reference value.
//
//  Revision 1.2  2004/11/09 15:18:36  tames
//  Made the value of the EQUAL_EPSILON constant smaller.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//