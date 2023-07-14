//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/gui/gov/nasa/gsfc/irc/gui/vis/axis/Tick.java,v 1.1 2004/12/16 23:01:15 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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
 * The tick class is a struct that contains a tick's value, label value,
 * and level. These values are specified with respect to a given
 * axis. Levels indicate how deeply nested the tick is on the axis with
 * level = 0 the top major scale and level > 0 minor scales at the given
 * depth.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2004/12/16 23:01:15 $
 * @author Troy Ames
 */
public class Tick
{
	private double fValue = 0;
	private double fLabelValue = 0;
	private int fLevel = 0;

	/**
	 * Create a new tick mark with the given value and level. The label value
	 * is the same as the given value.
	 * 
	 * @param value value represented by the tick
	 * @param level the level depth of this tick with 0 being the top major level.
	 */
	public Tick(double value, int level)
	{
		this(value, value, level);
	}

	/**
	 * Create a new tick mark with the given value, label, and level.
	 * 
	 * @param value value represented by the tick
	 * @param label value to use for tick label
	 * @param level the level depth of this tick with 0 being the top major level.
	 */
	public Tick(double value, double label, int level)
	{
		fValue = value;
		fLabelValue = label;
		fLevel = level;
	}

	/**
	 * Get the value of the tick with respect to the axis. This value is in 
	 * the same units and scale as the axis model so this can be translated
	 * into screen coordinates for positioning the tick mark.
	 * 
	 * @return value of the tick
	 */
	public double getValue()
	{
		return fValue;
	}

	/**
	 * Get the value to use for the tick label. Depending on the label strategy 
	 * this may be different then what is returned by <code>getValue</code>.
	 * 
	 * @return value of the tick
	 */
	public double getLabelValue()
	{
		return fLabelValue;
	}

	/**
	 * Get the tick level. Level=0 is the major scale and levels > 0 are 
	 * minor scales nested to the given level depth.
	 * 
	 * @return level of the tick
	 */
	public double getLevel()
	{
		return fLevel;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: Tick.java,v $
//  Revision 1.1  2004/12/16 23:01:15  tames
//  Updated to reflect final visualization package restructuring.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//

