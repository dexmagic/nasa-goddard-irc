//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.gui.util;

import java.awt.Color;
import java.util.Arrays;

import gov.nasa.gsfc.commons.types.arrays.ArrayHelpers;


/**
 * Using a given color wheel, this class tracks how much each color within that
 * color wheel is used. Note that this only tracks usage for colors within this
 * color wheel.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2004/12/14 21:42:59 $
 * @author Ken Wootton
 * @author Troy Ames
 */
public class ColorWheelUsage
{
	private ColorWheel fColorWheel = null;

	//  Usage statistics for the color table.
	private int[] fColorWheelUse = null;

	/**
	 * Create a new usage object using the StandardColorWheel.
	 * 
	 * @param colorWheel the color wheel on which to base the usage
	 */
	public ColorWheelUsage()
	{
		this(null);
	}

	/**
	 * Create a new usage object using the specified ColorWheel. If the
	 * parameter is null the StandardColorWheel will be used.
	 * 
	 * @param colorWheel the color wheel on which to base the usage
	 */
	public ColorWheelUsage(ColorWheel colorWheel)
	{
		fColorWheel = colorWheel;
		
		if (fColorWheel == null)
		{
			fColorWheel = StandardColorWheel.getInstance();
		}
		
		fColorWheelUse = new int[fColorWheel.getNumColors()];
	}

	/**
	 * Add a use for the color at the given index.
	 * 
	 * @param the index of the color to add a use to
	 */
	public void addUse(int colorIndex)
	{
		//  Check bounds
		if (colorIndex >= 0 && colorIndex < fColorWheelUse.length)
		{
			fColorWheelUse[colorIndex]++;
		}
	}

	/**
	 * Add a use for the given color.
	 * 
	 * @param the color to add a use, assuming the color exists within the color
	 *            wheel
	 */
	public void addUse(Color color)
	{
		//  If we find the given color, add a use.
		addUse(ArrayHelpers.indexOf(fColorWheel.getColors(), color));
	}

	/**
	 * Remove a use for the color at the given index.
	 * 
	 * @param the index of the color to remove a use from
	 */
	public void removeUse(int colorIndex)
	{
		//  Check bounds and make sure we can't use it less than zero times.
		if (colorIndex >= 0 && colorIndex < fColorWheelUse.length
				&& fColorWheelUse[colorIndex] - 1 >= 0)
		{
			fColorWheelUse[colorIndex]--;
		}
	}

	/**
	 * Remove a use for the given color.
	 * 
	 * @param the color to add a use from
	 */
	public void removeUse(Color color)
	{
		//  If we find the given color, remove a use.
		removeUse(ArrayHelpers.indexOf(fColorWheel.getColors(), color));
	}

	/**
	 * Get the current number of uses for the color at the given index.
	 * 
	 * @param colorIndex the index of color of interest
	 * 
	 * @return the number of uses
	 */
	public int getUsage(int colorIndex)
	{
		int numUses = 0;

		if (colorIndex >= 0 && colorIndex < fColorWheelUse.length)
		{
			numUses = fColorWheelUse[colorIndex];
		}

		return numUses;
	}

	/**
	 * Get the current number of uses for the given color.
	 * 
	 * @param colorIndex the color of interest
	 * 
	 * @return the number of uses, if the color is in the color wheel
	 */
	public int getUsage(Color color)
	{
		//  If we find the given color, return its usage.
		return getUsage(ArrayHelpers.indexOf(fColorWheel.getColors(), color));
	}

	/**
	 * Get the least used color within the color wheel. If more than one color
	 * is tied for the honor, the first one in the color sequence is returned.
	 * 
	 * @return the least used color of the color wheel
	 */
	public Color getLeastUsed()
	{
		int colorWheelIndex = 0;

		//  Return the first color in the color wheel that has been used
		//  the least.
		for (int i = 1; i < fColorWheelUse.length; i++)
		{
			if (fColorWheelUse[i] < fColorWheelUse[colorWheelIndex])
			{
				colorWheelIndex = i;
			}
		}

		return fColorWheel.getColors()[colorWheelIndex];
	}

	/**
	 * Reset the usage of all colors to zero.
	 */
	public void resetUsage()
	{
		Arrays.fill(fColorWheelUse, 0);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ColorWheelUsage.java,v $
//  Revision 1.3  2004/12/14 21:42:59  tames
//  Added a no arg constructor
//
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/11/08 23:12:15  tames
//  Initial version
//
//


