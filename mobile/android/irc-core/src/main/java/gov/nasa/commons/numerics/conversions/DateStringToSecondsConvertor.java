//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/conversions/DateStringToSecondsConvertor.java,v 1.2 2004/07/12 14:26:24 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DateStringToSecondsConvertor.java,v $
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.commons.numerics.conversions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 *	A DateStringToSecondsConvertor converts a String representation of a date
 *  into a corresponding number of seconds.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:24 $
 *  @author Carl Hostetter
 */


public class DateStringToSecondsConvertor
{
	private static HashMap fDateFormats = new HashMap();


	/**
	 *  Causes this DateStringToSecondsConvertor to interpret the given
	 *  String as a date according to the given date format String, and
	 *  return the corresponding number of seconds.
	 *
	 *  @param dateString A String representation of a date
	 *  @param formatString A representation of the format of the date
	 * 		String
	 *  @throws ParseException if the given date String cannot be parsed
	 * 		in accordance with the given date format String
	**/

	public static double convert(String dateString, String formatString)
		throws ParseException
	{
		double timeInSeconds = -1;

		DateFormat dateFormat =
			(DateFormat) fDateFormats.get(formatString);

		if (dateFormat == null)
		{
			dateFormat = new SimpleDateFormat(formatString);
			fDateFormats.put(formatString, dateFormat);
		}

		Date dateObject = dateFormat.parse(dateString);

		timeInSeconds = dateObject.getTime() / 1000;

		return (timeInSeconds);
	}
}
