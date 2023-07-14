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

import gov.nasa.gsfc.irc.gui.vis.AbstractVisComponent;


/**
 * A concrete axis component that delegates the actual drawing of the 
 * axis to one or more plugin renderers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 02:59:12 $
 * @author 	Troy Ames
 */
public class DefaultAxisComponent extends AbstractVisComponent
{
	private AxisModel fAxisModel;

	/**
	 * Create an axis component with the given AxisModel. This component is 
	 * added as a registered ChangeListener of the model.
	 * 
	 * @param model the axis model for this component
	 */
	public DefaultAxisComponent(AxisModel model)
	{
		super(model);
	}
//
//	/**
//	 * Set the minimum value of the axis.
//	 *
//	 * @param min  the minimum value of the axis
//	 */
//	public void setViewMinimum(double min)
//	{
//		fAxisModel.setViewMinimum(min);
//	}
//
//	/**
//	 * Get the minimum value of the axis.
//	 *
//	 * @return  the minimum value of the axis
//	 */
//	public double getMinimum()
//	{
//		return fAxisModel.getViewMinimum();
//	}
//
//	/**
//	 * Set the maximum value of the axis.
//	 *
//	 * @param min  the maximum value of the axis
//	 */
//	public void setViewExtent(double max)
//	{
//		fAxisModel.setViewExtent(max);
//	}
//
//	/**
//	 * Get the maximum value of the axis.
//	 *
//	 * @return  the maximum value of the axis
//	 */
//	public double getMaximum()
//	{
//		return getMinimum() + fAxisModel.getViewExtent();
//	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultAxisComponent.java,v $
//  Revision 1.2  2005/10/11 02:59:12  tames
//  Commented out for now set and get methods. May remove in the future.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.2  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//