//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: TextUtil.java,v $
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
// 
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

import javax.swing.JTextField;

/**
 *    This class contains utility methods for dealing with text and text
 *  field components.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/16 21:12:51 $
 *  @author	    Ken Wootton
 */
public class TextUtil
{
	/**
	 *    Determine whether or not the given text field contain any 
	 *  non-whitespace text.
	 *
	 *  @param field  the text field to check
	 *
	 *  @return  whether or not the field contains any text
	 */
	public static boolean fieldContainsText(JTextField field)
	{
		return field.getText().trim().length() > 0 ;
	}

	/**
	 *    Get an integer from the given text field.
	 *
	 *  @param field  the text field in which to retrieve the integer
	 *
	 *  @return  the retrieved integer
	 * 
	 *  @throws NumberFormatException  if the text in the text field
	 *                                 is not an integer
	 */
	public static int getIntFromField(JTextField field)
		throws NumberFormatException
	{
		return (new Integer(field.getText().trim())).intValue();
	}

	/**
	 *    Get a long from the given text field.
	 *
	 *  @param field  the text field in which to retrieve the long value
	 *
	 *  @return  the retrieved long value
	 * 
	 *  @throws NumberFormatException  if the text in the text field
	 *                                 is not a long
	 */
	public static long getLongFromField(JTextField field)
		throws NumberFormatException
	{
		return (new Long(field.getText().trim())).longValue();
	}

	/**
	 *    Get an integer from the given text field.
	 *
	 *  @param field  the text field in which to retrieve the integer
	 *
	 *  @return  the retrieved integer
	 * 
	 *  @throws NumberFormatException  if the text in the text field
	 *                                 is not an integer
	 */
	public static double getDoubleFromField(JTextField field)
		throws NumberFormatException
	{
		return (new Double(field.getText().trim())).doubleValue();
	}
}

