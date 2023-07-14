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

import javax.swing.event.ChangeListener;

import gov.nasa.gsfc.irc.gui.swing.event.ChangeSource;


/**
 * An AxisModel holds the current state of an Axis and notifies all registered
 * listeners when the state changes via a set method. An axis model contains
 * four interrelated double properties: axis minimum, axis maximum, view position
 * and view extent. These four doubles define two nested ranges like this:
 * 
 * <pre>
 * axisMinimum &lt;= view &lt;= view+extent &lt;= axisMaximum
 * </pre>
 * 
 * The outer range is <code>axisMinimum, axisMaximum</code> and the inner
 * range is <code>view, view+extent</code>. The inner range must lie
 * within the outer one, i.e. <code>view</code> must be less than or
 * equal to <code>AxisMaximum</code> and <code>viewMaximum</code> must
 * greater than or equal to <code>axisMinimum</code>, and
 * <code>axisMaximum</code> must be greater than or equal to
 * <code>axisMinimum</code>. Implementing classes must enforce these
 * constraints.
 * <p>
 * The four range values are defined as Java Beans properties however Swing
 * ChangeEvents are used to notify clients of changes rather than
 * PropertyChangeEvents. This was done to keep the overhead of monitoring a
 * model low. Changes are often reported at MouseDragged rates.
 * <p>
 * Implementing classes should only send change events when a meaningful change
 * has occurred. For example a class should avoid sending a change event when
 * the <code>setMaximum</code> method is called with a new value equal to the
 * current value.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/10/11 02:56:16 $
 * @author Troy Ames
 */
public interface AxisModel extends ChangeSource
{
	/**
	 * Set the minimum visible view value on the axis.
	 *
	 * @param min  the minimum view value on the axis
	 */
	public void setViewMinimum(double min);

	/**
	 * Get the minimum visible view value on the axis.
	 *
	 * @return  the minimum view value of the axis
	 */
	public double getViewMinimum();

	/**
	 * Set the visible view extent on the axis.
	 *
	 * @param newExtent  the view extent on the axis
	 */
	public void setViewExtent(double newExtent);

	/**
	 * Get the visible view extent on the axis.
	 *
	 * @return  the view extent of the axis
	 */
	public double getViewExtent();

    /**
	 * Sets the axis minimum to <I>min</I> after ensuring that <I>min</I> 
	 * obeys the model's constraints:
	 * 
	 * <pre>
	 * axisMinimum &lt;= viewMinimum
	 * </pre>
	 * @param min the axis minimum
	 * @see #getMinimum
	 */
	public void setMinimum(double min);

	/**
	 * Get the minimum value on the axis.
	 *
	 * @return  the minimum value of the axis
	 */
	public double getMinimum();

	/**
	 * Sets the axis maximum to <I>max</I> after ensuring that <I>max</I> 
	 * obeys the model's constraints:
	 * 
	 * <pre>
	 * view+extent &lt;= axisMaximum
	 * </pre>
	 * @param max  the maximum value on the axis
	 */
	public void setMaximum(double max);

	/**
	 * Get the maximum value on the axis.
	 *
	 * @return  the maximum value on the axis
	 */
	public double getMaximum();

	/**
	 * Set the reference value for the axis. A reference value is a baseline 
	 * or context specific value that has special meaning for this axis.
	 *
	 * @param value  the reference value for the axis
	 */
	public void setReference(double value);

	/**
	 * Get the reference value for the axis.
	 *
	 * @return  the reference value for the axis
	 */
	public double getReference();

	/**
	 * Set the visible view values on the axis. The axis minimum and maximum may
	 * be adjusted to obey the model's constraints:
	 * 
	 * <pre>
	 * axisMinimum &lt;= view &lt;= view + extent &lt;= axisMaximum
	 * </pre>
	 * 
	 * Should result in only one change event.
	 * 
	 * @param view the view value on the axis
	 * @param extent the view extent on the axis
	 */
	public void setViewRange(double view, double extent);

	/**
	 * This method sets all the range properties with a single call. Forces the
	 * values to obey the model's constraints:
	 * 
	 * <pre>
	 * axisMinimum &lt;= view &lt;= view + extent &lt;= axisMaximum
	 * </pre>
	 * 
	 * Should result in only one change event.
	 * 
	 * @param view the view value on the axis
	 * @param viewExtent the view extent on the axis
	 * @param axisMinimum the minimum value on the axis
	 * @param axisMaximum the maximum value on the axis
	 * @param adjusting a boolean, true if a series of changes are in progress
	 * @see #setViewMinimum(double)
	 * @see #setViewMaximum(double)
	 * @see #setMinimum(double)
	 * @see #setMaximum(double)
	 * @see #setModelIsAdjusting(boolean)
	 */
	public void setRangeProperties(double view, double viewExtent,
			double axisMinimum, double axisMaximum, boolean adjusting);

	/**
	 * Sets the preferred number of ticks on this axis. Clients can use the
	 * returned value to seed TickFactory methods in order to get consistent
	 * axis tick spacing.
	 * 
	 * @param n the preferred number of ticks for this axis.
	 */
	public void setNumberOfTicks(int n);

	/**
	 * Gets the preferred number of ticks on this axis. Clients can use the
	 * returned value to seed TickFactory methods in order to get consistent
	 * axis tick spacing.
	 * 
	 * @return the preferred number of ticks for this axis.
	 */
	public int getNumberOfTicks();

    /**
	 * Set the scale that visualizations should use for this axis.
	 * 
	 * @param scale the scale for this axis
	 */
	public void setAxisScale(AxisScale scale);

    /**
	 * Get the scale that visualizations should use for this axis.
	 * 
	 * @return the scale for this axis
	 */
	public AxisScale getAxisScale();

	/**
	 * Applies the axis scale to the value and returns a ratio of where the
	 * given value is relative to the view and in units the size of
	 * the view extent. 
	 * <p>For example, with a linear scale 
	 * and the minimum = 100 and the extent = 100, this method will return
	 * 0.5 for a value of 150, 1.0 for a value of 200, and -0.5 for a value of 
	 * 50.
	 * 
	 * @param value the value to scale
	 * @return a ratio relative to the view and extent.
	 */
	public double getScaleRatio(double value);

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
	 * @param ratio a ratio relative to the view minimum and extent
	 * @return a value when scaled results in the given ratio
	 */
	public double getScaleValue(double ratio);

	/**
	 * Set whether or not the axis should be auto scaled.
	 *
	 * @param autoScale  whether or not to auto scale the axis
	 */
	public void setAutoScale(boolean autoScale);

	/**
	 * Get whether or not the axis should be auto scaled.
	 *
	 * @return  whether or not the axis is auto scaled.
	 */
	public boolean isAutoScale();

    /**
     * This attribute indicates that any upcoming changes to the 
     * model should be considered a single event. This attribute
     * will be set to true at the start of a series of changes,
     * and will be set to false when the model has finished changing.  Normally
     * this allows a listener to only take action when the final change is
     * committed, instead of having to do updates for all intermediate changes.
     * 
     * @param b true if the upcoming changes to the model property are part of a series
     */
    public void setModelIsAdjusting(boolean b);


    /**
     * Returns true if the current changes to the model are part 
     * of a series of changes.
     * 
     * @return the modelIsAdjustingProperty.  
     * @see #setModelIsAdjusting
     */
    public boolean getModelIsAdjusting();

    /**
     * Adds a ChangeListener to the model's listener list.
     *
     * @param listener the ChangeListener to add
     * @see #removeChangeListener
     */
    public void addChangeListener(ChangeListener listener);

    /**
     * Removes a ChangeListener from the model's listener list.
     *
     * @param listener the ChangeListener to remove
     * @see #addChangeListener
     */
    public void removeChangeListener(ChangeListener listener);
    
	/**
	 * Returns an array of all the <code>ChangeListener</code> objects added to 
	 * this model with addChangeListener().
	 * 
	 * @return array of <code>ChangeListener</code> objects or an empty
	 *         array if no listeners have been added
	 */
	public ChangeListener[] getChangeListeners();
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AxisModel.java,v $
//  Revision 1.3  2005/10/11 02:56:16  tames
//  Reflects changes to the AxisModel interface to support AxisScrollBars.
//
//  Revision 1.2  2005/09/23 20:01:24  tames
//  Updated JavaDoc only.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.3  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.2  2004/11/09 16:29:18  tames
//  Added a reference value.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//