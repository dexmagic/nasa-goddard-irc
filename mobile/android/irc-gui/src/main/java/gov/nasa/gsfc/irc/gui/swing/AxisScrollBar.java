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

package gov.nasa.gsfc.irc.gui.swing;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.Scrollable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gov.nasa.gsfc.irc.gui.swing.plaf.AxisScrollBarUI;
import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;

/**
 * An implementation of a scrollbar for an axis. The user positions the knob in the
 * scrollbar to determine the contents of the viewing area. The
 * program typically adjusts the axis model so that the end of the
 * scrollbar represents the end of the displayable contents, or 100%
 * of the contents. The start of the scrollbar is the beginning of the 
 * displayable contents, or 0%. The position of the knob within
 * those bounds then translates to the corresponding percentage of
 * the displayable contents.
 * <p>
 * Typically, as the position of the knob in the scrollbar changes
 * a corresponding change is made to the position of the view on
 * the underlying graphic, changing the contents.
 * 
 * <p>This code was adapted from <code>javax.swing.JScrollBar</code>.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 02:54:24 $
 * @author 	Troy Ames
 */
public class AxisScrollBar extends JComponent implements ChangeListener
{
	/**
	 * @see #getUIClassID
	 * @see #readObject
	 */
	private static final String uiClassID = "AxisScrollBarUI";

	/**
	 * Indicates that the <code>Adjustable</code> has horizontal orientation.  
	 */
	public static final int HORIZONTAL = 0;

	/**
	 * Indicates that the <code>Adjustable</code> has vertical orientation.  
	 */
	public static final int VERTICAL = 1;

	/**
	 * Indicates that the <code>Adjustable</code> has no orientation.  
	 */
	public static final int NO_ORIENTATION = 2;

	/**
	 * @see #setOrientation
	 */
	protected int fOrientation;

	/**
	 * @see #setUnitIncrement
	 */
	protected double fUnitIncrement;

	/**
	 * @see #setBlockIncrement
	 */
	protected double fBlockIncrement;

	protected boolean fValueIsAdjusting = false;
	private AxisModel fAxisModel = null;

	/**
	 * 
	 */
	public AxisScrollBar(AxisModel model)
	{
		super();
		fAxisModel = model;
		this.fUnitIncrement = 1;
		this.fBlockIncrement = 10.0d;
		this.fOrientation = HORIZONTAL;
		setRequestFocusEnabled(false);
		fAxisModel.addChangeListener(this);
		updateUI();
	}

	/**
	 * Returns the delegate that implements the look and feel for 
	 * this component.
	 *
	 * @see JComponent#setUI
	 */
	public AxisScrollBarUI getUI()
	{
		return (AxisScrollBarUI) ui;
	}

	/**
	 * Overrides <code>JComponent.updateUI</code>.
	 * 
	 * @see JComponent#updateUI
	 */
	public void updateUI()
	{
		setUI((AxisScrollBarUI) UIManager.getUI(this));
	}

	/**
	 * Returns the name of the LookAndFeel class for this component.
	 * 
	 * @return "AxisScrollBarUI"
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
	public String getUIClassID()
	{
		return uiClassID;
	}

	/**
	 * Returns data model that handles the scrollbar's four
	 * fundamental properties: minimum, maximum, value, extent.
	 * 
	 * @see #setModel
	 */
	public AxisModel getModel()
	{
		return fAxisModel;
	}

	/**
	 * Sets the model that handles the components's four
	 * fundamental properties: minimum, maximum, viewMinimum, viewMaximum.
	 * 
	 * @see #getModel
	 * @beaninfo
	 *       bound: true
	 * description: The scrollbar's BoundedRangeModel.
	 */
	public void setModel(AxisModel newModel)
	{
		Integer oldValue = null;
		AxisModel oldModel = fAxisModel;
		fAxisModel = newModel;

		firePropertyChange("model", oldModel, fAxisModel);

		//        if (accessibleContext != null) {
		//            accessibleContext.firePropertyChange(
		//                    AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
		//                    oldValue, new Integer(model.getValue()));        
		//        }
	}

	/**
	 * Returns the component's orientation (horizontal or vertical). 
	 *                     
	 * @return VERTICAL or HORIZONTAL
	 * @see #setOrientation
	 * @see java.awt.Adjustable#getOrientation
	 */
	public int getOrientation()
	{
		return fOrientation;
	}

	/**
	 * Set the scrollbar's orientation to either VERTICAL or
	 * HORIZONTAL.
	 * 
	 * @exception IllegalArgumentException if orientation is not one of VERTICAL, HORIZONTAL
	 * @see #getOrientation
	 * @beaninfo
	 *    preferred: true
	 *        bound: true
	 *    attribute: visualUpdate true
	 *  description: The scrollbar's orientation.
	 *         enum: VERTICAL JScrollBar.VERTICAL 
	 *               HORIZONTAL JScrollBar.HORIZONTAL
	 */
	public void setOrientation(int orientation)
	{
		checkOrientation(orientation);
		int oldValue = this.fOrientation;
		this.fOrientation = orientation;
		firePropertyChange("orientation", oldValue, orientation);

		if ((oldValue != orientation) && (accessibleContext != null))
		{
			accessibleContext.firePropertyChange(
				AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
				((oldValue == VERTICAL) ? AccessibleState.VERTICAL
						: AccessibleState.HORIZONTAL),
				((orientation == VERTICAL) ? AccessibleState.VERTICAL
						: AccessibleState.HORIZONTAL));
		}
		if (orientation != oldValue)
		{
			revalidate();
		}
	}

	private void checkOrientation(int orientation)
	{
		switch (orientation)
		{
			case VERTICAL:
			case HORIZONTAL:
				break;
			default:
				throw new IllegalArgumentException(
						"orientation must be one of: VERTICAL, HORIZONTAL");
		}
	}

	/**
	 * Returns the amount to change the scrollbar's value by,
	 * given a unit up/down request.  A ScrollBarUI implementation
	 * typically calls this method when the user clicks on a scrollbar 
	 * up/down arrow and uses the result to update the scrollbar's
	 * value.   Subclasses my override this method to compute
	 * a value, e.g. the change required to scroll up or down one
	 * (variable height) line text or one row in a table.
	 * <p>
	 * The JScrollPane component creates scrollbars (by default) 
	 * that override this method and delegate to the viewports
	 * Scrollable view, if it has one.  The Scrollable interface
	 * provides a more specialized version of this method.
	 * <p>
	 * This implementation uses 10% of the models extent for the unit increment.
	 * 
	 * @param direction is -1 or 1 for up/down respectively
	 * @return the value of the unitIncrement property
	 * @see #setUnitIncrement
	 * @see #setValue
	 * @see Scrollable#getScrollableUnitIncrement
	 */
	public double getUnitIncrement(int direction)
	{
		// TODO direction is ignored since the caller (BasicAxisScrollBarUI) is 
		// inconsistent with respect to the returned value. Callers
		// should be rewritten to be consistent. Need to also check if and
		// how the scroll wheel uses this method.
		//double unitIncrement = fUnitIncrement;
		double unitIncrement = fAxisModel.getViewExtent() * 0.1;
		
		if (getOrientation() == JScrollBar.VERTICAL)
		{
			unitIncrement = -unitIncrement;
		}

		return unitIncrement;
	}

	/**
	 * Sets the unitIncrement property. Currently the unit increment is 
	 * dynamically determined from the model so this method's implementation
	 * does nothing.
	 * @see #getUnitIncrement
	 * @beaninfo
	 *   preferred: true
	 *       bound: true
	 * description: The scrollbar's unit increment.
	 */
	public void setUnitIncrement(int unitIncrement)
	{
		//int oldValue = this.fUnitIncrement;
		//this.fUnitIncrement = unitIncrement;
		//firePropertyChange("unitIncrement", oldValue, unitIncrement);
	}

	/**
	 * Returns the amount to change the scrollbar's value by,
	 * given a block (usually "page") up/down request.  A ScrollBarUI 
	 * implementation typically calls this method when the user clicks 
	 * above or below the scrollbar "knob" to change the value
	 * up or down by large amount.  Subclasses my override this 
	 * method to compute a value, e.g. the change required to scroll 
	 * up or down one paragraph in a text document.
	 * <p>
	 * The JScrollPane component creates scrollbars (by default) 
	 * that override this method and delegate to the viewports
	 * Scrollable view, if it has one.  The Scrollable interface
	 * provides a more specialized version of this method.
	 * <p>
	 * This implementation uses the models extent for the block increment.
	 * 
	 * @param direction is -1 or 1 for up/down respectively
	 * @return the value of the blockIncrement property
	 * @see #setBlockIncrement
	 * @see #setValue
	 * @see Scrollable#getScrollableBlockIncrement
	 */
	public double getBlockIncrement(int direction)
	{
		// TODO direction is ignored since the caller (BasicAxisScrollBarUI) is 
		// inconsistent with respect to the returned value. Callers
		// should be rewritten to be consistent. Need to also check if and
		// how the scroll wheel uses this method.
		//double blockIncrement = fBlockIncrement;
		double blockIncrement = fAxisModel.getViewExtent();
		
		if (getOrientation() == JScrollBar.VERTICAL)
		{
			// The direction of the vertical axis is inverse of the scrollbar
			blockIncrement = -blockIncrement;
		}

		return blockIncrement;
	}

	/**
	 * Sets the blockIncrement property. Currently the block increment is 
	 * dynamically determined from the model so this method's implementation
	 * does nothing.
	 * @see #getBlockIncrement()
	 * @beaninfo
	 *   preferred: true
	 *       bound: true
	 * description: The scrollbar's block increment.
	 */
	public void setBlockIncrement(double blockIncrement)
	{
		//double oldValue = this.fBlockIncrement;
		//this.fBlockIncrement = blockIncrement;
		//firePropertyChange("blockIncrement", oldValue, blockIncrement);
	}

	/**
	 * Returns the scrollbar's value.
	 * @return the model's value property
	 * @see #setValue
	 */
	public double getValue()
	{
		return getModel().getViewMinimum();
	}

	/**
	 * Sets the scrollbar's value.  This method just forwards the value
	 * to the model.
	 *
	 * @see #getValue
	 * @see BoundedRangeModel#setValue
	 * @beaninfo
	 *   preferred: true
	 * description: The scrollbar's current value.
	 */
	public void setValue(double value)
	{
		AxisModel m = getModel();
		//int oldValue = m.getValue();
		m.setViewMinimum(value);

		//        if (accessibleContext != null) {
		//            accessibleContext.firePropertyChange(
		//                    AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
		//                    new Integer(oldValue),
		//                    new Integer(m.getValue()));
		//        }
	}

	/**
	 * Returns the scrollbar's extent, aka its "visibleAmount".  In many 
	 * scrollbar look and feel implementations the size of the 
	 * scrollbar "knob" or "thumb" is proportional to the extent.
	 * 
	 * @return the value of the model's extent property
	 * @see #setVisibleAmount
	 */
	public double getVisibleAmount()
	{
		AxisModel m = getModel();
		return m.getViewExtent();
	}

	/**
	 * Set the model's extent property.
	 * 
	 * @see #getVisibleAmount
	 * @see BoundedRangeModel#setExtent
	 * @beaninfo
	 *   preferred: true
	 * description: The amount of the view that is currently visible.
	 */
	public void setVisibleAmount(double extent)
	{
		AxisModel m = getModel();
		m.setViewExtent(extent);
		
		// Update the block increment amount
		//setBlockIncrement(m.getViewExtent());
	}

	/**
	 * Returns the minimum value supported by the scrollbar 
	 * (usually zero).
	 *
	 * @return the value of the model's minimum property
	 * @see #setMinimum
	 */
	public double getMinimum()
	{
		return getModel().getMinimum();
	}

	/**
	 * Sets the model's minimum property.
	 *
	 * @see #getMinimum
	 * @see BoundedRangeModel#setMinimum
	 * @beaninfo
	 *   preferred: true
	 * description: The scrollbar's minimum value.
	 */
	public void setMinimum(double minimum)
	{
		getModel().setMinimum(minimum);
	}

	/**
	 * The maximum value of the scrollbar is maximum - extent.
	 *
	 * @return the value of the model's maximum property
	 * @see #setMaximum
	 */
	public double getMaximum()
	{
		return getModel().getMaximum();
	}

	/**
	 * Sets the model's maximum property.  Note that the scrollbar's value
	 * can only be set to maximum - extent.
	 * 
	 * @see #getMaximum
	 * @see BoundedRangeModel#setMaximum
	 * @beaninfo
	 *   preferred: true
	 * description: The scrollbar's maximum value. 
	 */
	public void setMaximum(double maximum)
	{
		getModel().setMaximum(maximum);
	}

	/**
	 * True if the scrollbar knob is being dragged.
	 * 
	 * @return the value of the model's valueIsAdjusting property
	 * @see #setValueIsAdjusting
	 */
	public boolean getValueIsAdjusting()
	{
		return getModel().getModelIsAdjusting();
	}

	/**
	 * Sets the model's valueIsAdjusting property.  Scrollbar look and
	 * feel implementations should set this property to true when 
	 * a knob drag begins, and to false when the drag ends.  The
	 * scrollbar model will not generate ChangeEvents while
	 * valueIsAdjusting is true.
	 * 
	 * @see #getValueIsAdjusting
	 * @see BoundedRangeModel#setValueIsAdjusting
	 * @beaninfo
	 *      expert: true
	 * description: True if the scrollbar thumb is being dragged.
	 */
	public void setValueIsAdjusting(boolean b)
	{
		AxisModel m = getModel();
		//boolean oldValue = m.getValueIsAdjusting();
		m.setModelIsAdjusting(b);

		//        if ((oldValue != b) && (accessibleContext != null)) {
		//            accessibleContext.firePropertyChange(
		//                    AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
		//                    ((oldValue) ? AccessibleState.BUSY : null),
		//                    ((b) ? AccessibleState.BUSY : null));
		//        }
	}

	/**
	 * Sets the four BoundedRangeModel properties after forcing
	 * the arguments to obey the usual constraints:
	 * <pre>
	 * minimum <= value <= value+extent <= maximum
	 * </pre>
	 * <p>
	 * 
	 * @see BoundedRangeModel#setRangeProperties
	 * @see #setValue
	 * @see #setVisibleAmount
	 * @see #setMinimum
	 * @see #setMaximum
	 */
	public void setValues(double viewMin, double extent, double axisMin,
			double axisMax)
	{
		AxisModel m = getModel();
		double oldValue = m.getViewMinimum();
		
		m.setRangeProperties(
			viewMin, extent, axisMin, axisMax, m.getModelIsAdjusting());
		
		//setBlockIncrement(m.getViewExtent());

        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
                    new Double(oldValue),
                    new Double(m.getViewMinimum()));
        }
	}

	/**
	 * Invoked when a model of interest has changed its state.
	 * 
	 * @param event a ChangeEvent object
	 */
	public void stateChanged(ChangeEvent event)
	{
		// We don't really care what changed, but assume we need to redraw to
		// reflect the change.
		repaint();
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AxisScrollBar.java,v $
//  Revision 1.1  2005/10/11 02:54:24  tames
//  Initial version.
//
//