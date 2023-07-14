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

package gov.nasa.gsfc.irc.library.gui.vis;

import javax.swing.JSlider;

import gov.nasa.gsfc.irc.gui.vis.axis.AxisModel;

/**
 * This class provides a GUI control for changing the scale for a
 * given axis.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 03:00:48 $
 * @author 	Troy Ames
 */
public class AxisScaleSlider extends JSlider
{
	AxisModel fAxisModel = null;
	double fPowerBase = 2.0d;
	
	/**
	 * Constructs a new AxisScaleSlider with the given model.
	 * @param model the model to modify with a modified scale.
	 */
	public AxisScaleSlider(AxisModel model)
	{
		super();
		super.setValue(0);
		fAxisModel = model;
	}
	
	/**
	 * Get the power base for this scalar.
	 * 
	 * @return Returns the powerBase.
	 */
	public double getPowerBase()
	{
		return fPowerBase;
	}
	
	/**
	 * Set the power base for this scalar. The scale of the target model is 
	 * updated proportional to the base set by this method raised to the 
	 * power set by the <code>setValue</code> method.
	 * 
	 * @param powerBase The powerBase to set.
	 */
	public void setPowerBase(double powerBase)
	{
		fPowerBase = powerBase;
	}

	/**
	 * Sets the sliders current value. This method forwards the value to the
	 * super class as well as the AxisModel.
	 * 
	 * @param n the new value
	 */
	public void setValue(int n)
	{
		int oldValue = getValue();
		
		// Verify n is within the range of this scalar
		if (n > getMaximum())
		{
			n = getMaximum();
		}
		else if (n < getMinimum())
		{
			n = getMinimum();
		}

		int valueDelta = n - oldValue;
		
		if (fAxisModel != null && valueDelta != 0)
		{
			// TODO synchronize this on the model
			double scalar = Math.pow(fPowerBase, (double) valueDelta);
			double axisMin = fAxisModel.getViewMinimum();
			double axisMax = axisMin + fAxisModel.getViewExtent();
			
			// Expand or shrink the axis scale around the midvalue in the axis.
			double midValue = axisMin + ((axisMax - axisMin) / 2.0);
			
			double highRange = axisMax - midValue;
			double highDelta = highRange * (1.0d / scalar);

			double lowRange = midValue - axisMin;
			double lowDelta = lowRange * (1.0d / scalar);
			
			fAxisModel.setViewRange(midValue - lowDelta, lowDelta + highDelta);
		}

		super.setValue(n);
	}
}


//--- Development History ---------------------------------------------------
//
//  $Log: AxisScaleSlider.java,v $
//  Revision 1.3  2005/10/11 03:00:48  tames
//  Reflects changes to the AxisModel interface to support AxisScrollBars.
//
//  Revision 1.2  2005/09/23 20:42:08  tames
//  JavaDoc changes only.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.2  2004/11/09 16:29:52  tames
//  Modified scalar to utilize the reference value for an axis.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//