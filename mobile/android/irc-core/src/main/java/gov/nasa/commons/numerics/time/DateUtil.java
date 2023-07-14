//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log:
//   1	IRC	   1.0		 4/29/2002 2:54:03 PM Uri Kerbel
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

package gov.nasa.gsfc.commons.numerics.time;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *  A utility class which contains a number of helper methods which provide
 *  generic functionality for prefered Date Formats within IRC.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/11/22 18:39:47 $
 *  @author		Uri Kerbel
 */
public class DateUtil
{
	// the constant list of recognized date formats
	public static final String[] RECOGNIZED_DATE_FORMATS =
	{
		"yyyy.MM.dd",
		"MMMM dd, yyyy",
  		"MM/dd/yyyy",
  		"MMM-dd-yyyy",
  		"yyyy.D",
  		"yyyy-D"
	};

	// the constant list of recognized time formats
	public static final String[] RECOGNIZED_TIME_FORMATS =
	{
		"H:mm:ss",
		"h:mm a",
	};

	// the default date format used for parsing
	public static final String DEFAULT_DATE_FORMAT =
			"yyyy.MM.dd";

	// the default time format used for parsing
	public static final String DEFAULT_TIME_FORMAT =
			"H:mm:ss";

	// the default date format used for parsing an instrument's epoch
	public static final String EPOCH_DATE_FORMAT =
			"yyyy.MM.dd hh:mm:ss";

	/**
	 * constructor
	**/
	protected DateUtil()
	{
	}

	/**
	 * attempts to parse a given String and return a date
	 * based on the recognized date formats
	 *
	 * @param dateString a string representing a date
	 *
	 * @return a date object if parsed successfully, null otherwise
	**/
	public static Date getDate(String dateString)
	{
		// try and loop through each simpleDateFormat and parse the given date
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat();

		for (int i = 0; i < RECOGNIZED_DATE_FORMATS.length; i++)
		{
			try
			{
				format.applyPattern(RECOGNIZED_DATE_FORMATS[i]);
				date = format.parse(dateString.trim());
				break;
			}
			catch (ParseException pe)
			{
				// nothing to do here as we may continue parsing
			}
		}

		return date;
	}

	/**
	 * attempts to parse a given String and return a date
	 * based on the recognized date formats
	 *
	 * @param dateString a string representing a time
	 *
	 * @return a date object if parsed successfully, null otherwise
	**/
	public static Date getTime(String timeString)
	{
		// try and loop through each simpleDateFormat and parse the given time
		Date time = null;
		SimpleDateFormat format = new SimpleDateFormat();
		for (int i = 0; i < RECOGNIZED_TIME_FORMATS.length; i++)
		{
			try
			{
				format.applyPattern(RECOGNIZED_TIME_FORMATS[i]);
				time = format.parse(timeString);
				break;
			}
			catch (ParseException pe)
			{
				// nothing to do here as we may continue parsing
			}
		}

		return time;
	}

	/**
	 * A helper method which formats the given date based on the default
	 * or null if the date is invalid
	 * 
	 * @param date Date to be formatted
	 * @return a formatted date string
	**/
	public static String getFormattedDate(Date date)
	{
		return getFormattedDate(date, DEFAULT_DATE_FORMAT);
	}

	/**
	 * A helper method which formats the given date based on a pattern
	 * or null if the date is invalid
	 * 
	 * @param date Date to be formatted
	 * @param pattern String specifying the format of the result
	 * @return a formatted date string
	**/
	public static String getFormattedDate(Date date, String pattern)
	{
		String formattedDate;
		SimpleDateFormat format = new SimpleDateFormat();
		try
		{
			format.applyPattern(pattern);
			formattedDate = format.format(date);
		}
		catch(Exception e)
		{
			formattedDate = null;
		}

		return formattedDate;
	}

	/**
	 * A helper method which formats the given time based on the default
	 * pattern or null if the time is invalid
	 * 
	 * @param time time to be formatted
	 * @return a formatted time string
	**/
	public static String getFormattedTime(Date time)
	{
		return getFormattedTime(time, DEFAULT_TIME_FORMAT);
	}

	/**
	 * A helper method which formats the given time based on the pattern
	 * or null if the time is invalid
	 * 
	 * @param time time to be formatted
	 * @param pattern String specifying the format of the result
	 * @return a formatted time string
	**/
	public static String getFormattedTime(Date time, String pattern)
	{
		String formattedTime;
		SimpleDateFormat format = new SimpleDateFormat();

		try
		{
			format.applyPattern(pattern);
			formattedTime = format.format(time);
		}
		catch(Exception e)
		{
			formattedTime = null;
		}

		return formattedTime;
	}

	/** 
	 * Create FITS format date string.
	 * Note that the date is not rounded.
	 * 
	 * @param date		   The date to be converted to FITS format.
	 * @param timeOfDay	Should time of day information be included?
	 * @return a formatted date and time string
	 */
	public static String getFitsDateString(Date date, boolean timeOfDay)
	{
		StringBuffer fitsDate = new StringBuffer();

		try {
			GregorianCalendar cal = new GregorianCalendar(
							TimeZone.getTimeZone("GMT"));


			cal.setTime(date);

			DecimalFormat df = new DecimalFormat("0000");
			fitsDate.append(df.format(cal.get(Calendar.YEAR)));
			fitsDate.append("-");
			df = new DecimalFormat("00");

			fitsDate.append(df.format(cal.get(Calendar.MONTH)+1));
			fitsDate.append("-");
			fitsDate.append(df.format(cal.get(Calendar.DAY_OF_MONTH)));

			if (timeOfDay)
			{
				fitsDate.append("T");
				fitsDate.append(df.format(cal.get(Calendar.HOUR_OF_DAY)));
				fitsDate.append(":");
				fitsDate.append(df.format(cal.get(Calendar.MINUTE)));
				fitsDate.append(":");
				fitsDate.append(df.format(cal.get(Calendar.SECOND)));
				fitsDate.append(".");
				df = new DecimalFormat("000");
				fitsDate.append(df.format(cal.get(Calendar.MILLISECOND)));
			}
		}
		catch (Exception e)
		{
			return new String("");
		}

		return new String(fitsDate);
	}
	
	
	/**
	 * Get the equivalent UTC Date for the given local Date.
	 * 
	 * @param localDate the local date to convert
	 * @return a UTC Date
	 */
	public static Date getUtcDate(Date localDate)
	{
		TimeZone localZone = TimeZone.getDefault();

		Calendar localCal = Calendar.getInstance();
		localCal.setTimeZone(localZone);
		localCal.setTime(localDate);

		long zoneOffsetMs = (localCal.get(Calendar.ZONE_OFFSET) 
				+ localCal.get(Calendar.DST_OFFSET));
		return new Date(localDate.getTime() - zoneOffsetMs);
	}
}