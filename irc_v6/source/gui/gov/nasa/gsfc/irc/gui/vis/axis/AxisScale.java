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

/**
 * An AxisScale provides the means to convert a value to a given scale and 
 * back. This is independent from screen resolution or axis size because 
 * the scaled result is in terms of a ratio which the client then translates
 * into screen coordinates.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/11 02:56:59 $
 * @author 	Troy Ames
 */
public interface AxisScale
{
	/**
	 * Applies the scale to the value and returns a ratio of where the
	 * given value is relative to the view minimum and in units the size of
	 * the view extent. For example, with a linear scale 
	 * and the minimum = 100 and the extent = 100, this method will return
	 * 0.5 for a value of 150, 1.0 for a value of 200, and -0.5 for a value of 
	 * 50.
	 * 
	 * @param value the value to scale
	 * @param viewMin the view visible value on the axis
	 * @param extent the extent of the visible view on the axis
	 * @return a ratio relative to the view value
	 */
	public double getScaleRatio(double value, double viewMin, double extent);

	/**
	 * Determines for the scale what value would result in the given ratio
	 * relative to the view minimum and in units the size of
	 * the view extent. 
	 * This is the inverse of <code>getScaleRatio</code> and is necessary when 
	 * translating from a screen relative coordinate to an axis domain value.
	 * 
	 * @param ratio a ratio relative to the axis minimum and axis range
	 * @param viewMin the minimum visible value on the axis
	 * @param extent the extent of the visible view on the axis
	 * @return a value when scaled results in the given ratio
	 */
	public double getScaleValue(double ratio, double viewMin, double extent);
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AxisScale.java,v $
//  Revision 1.2  2005/10/11 02:56:59  tames
//  Javadoc updates.
//
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//