//=== File Prolog ============================================================
//
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   1	Dataset Redesign1.0		 9/20/2002 3:55:36 PM Lynne Case	  
//  $
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

package gov.nasa.gsfc.commons.system.properties;

/**
 *  This class provides some static methods for looking up System properties.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:24 $
 *  @author		Lynne Case
 */

public class PropertyUtil
{
	/**
	 * You cannot construct one of these, it just has static utility methods
	 */
	private PropertyUtil ()
	{
	}
	
	public static int findIntProperty(String propName, int defaultValue)
	{
		int returnValue = defaultValue;
		if (System.getProperty(propName)!=null)
		{
			returnValue = Integer.parseInt(
				System.getProperty(propName));
		}
		return returnValue;
	}
}
