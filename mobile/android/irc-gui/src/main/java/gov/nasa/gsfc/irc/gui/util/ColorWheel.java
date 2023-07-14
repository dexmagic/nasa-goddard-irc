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

/**
 * This interface describes a set of, usually standard, colors. It is called a
 * color "wheel" because its normal use involves iterating over this set during
 * repeated uses.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2004/11/08 23:12:15 $
 * @author Ken Wootton
 */
public interface ColorWheel
{
	/**
	 * Get the colors within the color wheel.
	 * 
	 * @return the current colors of the color wheel
	 */
	public Color[] getColors();

	/**
	 * Get the number of colors within the color wheel.
	 * 
	 * @return the number of colors within the color wheel
	 */
	public int getNumColors();
} 

//--- Development History  ---------------------------------------------------
//
//  $Log: ColorWheel.java,v $
//  Revision 1.1  2004/11/08 23:12:15  tames
//  Initial version
//
//

