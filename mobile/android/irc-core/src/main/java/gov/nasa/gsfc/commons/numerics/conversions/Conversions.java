//=== File Prolog ============================================================
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

package gov.nasa.gsfc.commons.numerics.conversions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  This object contains static methods that can be used to perform
 *  various conversions.  It also contains some default conversion
 *  factors for internal and public use.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2006/02/01 22:55:46 $
 *  @author Ken Wootton
 *  @author	Troy Ames
 */

public class Conversions
{
	/**
	 *  This is the default decimal format.  This will format a
	 *  decimal number using 3 decimal places.
	 */
	public static final DecimalFormat sDefaultDecimalFormat =
		new DecimalFormat("###,###,###,###,###,###.000");

	/**
	 * This is the format used for putting microseconds into the default
	 * date format.
	 */
	public static final DecimalFormat sMicrosecFormat =
		new DecimalFormat("000");

	/**
	 *  This is the default date format.  This will format a date
	 *  in the following fashion: Mon May 10 15:09:58.964 EST 1999
	 */ 
	public static final SimpleDateFormat sDefaultDateFormat = 
		new SimpleDateFormat("EEE MMM dd HH:mm:ss.SSS zzz yyyy");

	/**
	 * The necessary conversion factor to convert bytes to
	 * kilobytes.  This is derived from the following formula:
	 * x kB = (x bytes) * (1 kB / 1000 bytes)
	 */
	public static final double BYTES_TO_KILOBYTES = .001;

	/**
	 * The necessary conversion factor to convert words to
	 * kilobytes.  This is derived from the following formula:
	 * x kB = (x words) * (2 bytes / 1 word) * (1 kB / 1000 bytes)
	 */
	public static final double WORDS_TO_KILOBYTES = .002;

	/**
	 *  The necessary conversion factor to convert a clock ticks to
	 *  milliseconds.  This is derived from the following formula:
	 *  x ms = (x clock ticks) * (1 microsec / 3.2 clock ticks) *
	 *			 (1 ms/1000 microsec)
	 */
	public static final double CLOCK_TICKS_TO_MS = .0003125;
	
	/**
	 *	The number of milliseconds per second.
	 */
	public static final double MS_PER_SEC = 1000;

	/**
	 *	The number of milliseconds per second.
	 */
	public static final double MICRO_PER_MS = 1000;

	//  Used to format the time.
	private static final int MICRO_POSITION = 23;

	//  The number of fraction digits in the normal double format.
	private static final int MAX_AXIS_FRACTION_DIGITS = 2;

	//  Constants that define when we should use scientific notation
	//  to represent double values.
	private static final double SCI_NOT_SMALL = .01;
	private static final double SCI_NOT_BIG = 100000;

	//  Number formats for axis labels
	private static NumberFormat sDoubleFormat = NumberFormat.getInstance();
	private static NumberFormat sDoubleSciFormat = new DecimalFormat("0.0##E00");

	//  Set up the double format
	static
	{
		sDoubleFormat.setMaximumFractionDigits(MAX_AXIS_FRACTION_DIGITS);
	}

	/**
	 *  Convert the given number of words into kilobytes.  See
	 *  the final variable 'WORDS_TO_KILOBYTES' for more information.
	 *
	 *  @param numWords  an amount, in words
	 *
	 *  @return  the given amount of words, expressed in kilobytes
	 */
	public static double wordsToKBytes(double numWords)
	{
		return (numWords * WORDS_TO_KILOBYTES);
	}

	/**
	 *  Convert the given number of clock ticks into milliseconds.
	 *
	 *  @param numClockTicks  an amount, in clock ticks
	 *
	 *  @return  the given amount of clock ticks, expressed in milliseconds
	 */
	public static double clockTicksToMs(double numClockTicks)
	{
		return (numClockTicks * CLOCK_TICKS_TO_MS);
	}

	/**
	 *  Given a system time and an offset from that system time,
	 *  return the corresponding absolute time in milliseconds.
	 *
	 *  @param systemTime  the system time, given in milliseconds
	 *					 since the java epoch
	 *  @param offset  the offset from the system time, given in clock
	 *				 ticks
	 */
	public static double systemToAbsTime(long systemTime, long offset)
	{
		double systemTimeDouble = (new Long(systemTime)).doubleValue();

		return ((double) systemTimeDouble + clockTicksToMs((double) offset));
	}

	/**
	 *  Convert the given number of kilobytes to a string
	 *  representation.  See 'sDefaultDecimalFormat' for more
	 *  information. 
	 *
	 *  @param  an amount, in kilobytes
	 *
	 *  @return  the string representation
	 */
	public static String kBytesToString(double numKBytes)
	{
		return sDefaultDecimalFormat.format(numKBytes);
	}

	/**
	 *  Convert the given number of milliseconds to a string
	 *  representation.  See 'sDefaultDecimalFormat' for more
	 *  information. 
	 *
	 *  @param numMs  an amount, in milliseconds
	 *
	 *  @return  the string representation
	 */
	public static String msToString(double numMs)
	{
		return sDefaultDecimalFormat.format(numMs);
	}

	/**
	 *  Convert the given time, given in milliseconds since the Java
	 *  epoch (January 1, 1970, 00:00:00 GMT), to a formally formatted
	 *  date.  See 'sDefaultDateFormat' for more information.
	 *
	 *  @param numMs  a time, in milliseconds since the Java epoch
	 *
	 *  @return  a string representing the date formatted as in this
	 *		   example:  Mon May 10 15:09:58.964 EST 1999 
	 */
	public static String msToDate(double numMs)
	{
		Date date = new Date((long)(numMs));
		StringBuffer dateStr = 
			new StringBuffer(sDefaultDateFormat.format(date));

		// Get the number of microseconds
		double fractionOfMs = numMs - Math.floor(numMs);
		long numOfMicro = Math.round(fractionOfMs * MICRO_PER_MS);

		// Need the following so that we can format 23 to 023 and 3 to 003.
		String numOfMicroString = sMicrosecFormat.format(numOfMicro);
		dateStr.insert(MICRO_POSITION, numOfMicroString);
		
		return dateStr.toString();
	}

	/**
	 *  Convert the given double to a scientific format string.
	 *
	 *  @param value double value.
	 *  @return  a string representing the double in scientific format 
	 */
	public static synchronized String doubleToSciFormat(double value)
	{
		String str = null;
		
		//  We use scientific notation to denote numbers that
		//  are bigger or smaller than some defined constants.
		if ((value < SCI_NOT_SMALL && value > -SCI_NOT_SMALL && value != 0) ||
			value > SCI_NOT_BIG || value < -SCI_NOT_BIG)
		{
			str = sDoubleSciFormat.format(value);
		}
		else
		{
			str = sDoubleFormat.format(value);
		}

		return str;
	}
	
	/**
	 * Converts the given integer to a boolean.
	 * 
	 * @param value an int to convert
	 * @return a boolean true if the value is greater than zero, false otherwise.
	 */
	public static boolean booleanValue(int value)
	{
		boolean result = false;
		
		if (value > 0)
		{
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Converts the given Object to a boolean if possible.
	 * 
	 * @param value an Object to convert
	 * @return a boolean representing the given value or false.
	 */
	public static boolean booleanValue(Object value)
	{
		boolean result = false;
		
		if (value instanceof Boolean)
		{
			result = ((Boolean) value).booleanValue();
		}
		else
		{
			result = Boolean.valueOf(value.toString()).booleanValue();
		}
		
		return result;
	}

	/**
	 * Converts the given Object to a short if possible, otherwise throws
	 * a NumberFormatException.
	 * 
	 * @param value an Object to convert
	 * @return a short representing the given value.
	 * @throws NumberFormatException if value cannot be converted
	 */
	public static short shortValue(Object value)
	{
		short result = 0;
		
		if (value instanceof Number)
		{
			result = ((Number) value).shortValue();
		}
		else
		{
			result = Short.decode(value.toString()).shortValue();
		}
		
		return result;
	}

	/**
	 * Converts the given Object to an int if possible, otherwise throws
	 * a NumberFormatException.
	 * 
	 * @param value an Object to convert
	 * @return an int representing the given value.
	 * @throws NumberFormatException if value cannot be converted
	 */
	public static int intValue(Object value)
	{
		int result = 0;
		
		if (value instanceof Number)
		{
			result = ((Number) value).intValue();
		}
		else
		{
			result = Integer.decode(value.toString()).intValue();
		}
		
		return result;
	}

	/**
	 * Converts the given Object to a float if possible, otherwise throws
	 * a NumberFormatException.
	 * 
	 * @param value an Object to convert
	 * @return a float representing the given value.
	 * @throws NumberFormatException if value cannot be converted
	 */
	public static float floatValue(Object value)
	{
		float result = 0.0f;
		
		if (value instanceof Number)
		{
			result = ((Number) value).floatValue();
		}
		else
		{
			result = Float.parseFloat(value.toString());
		}
		
		return result;
	}

	/**
	 * Converts the given Object to a double if possible, otherwise throws
	 * a NumberFormatException.
	 * 
	 * @param value an Object to convert
	 * @return a double representing the given value.
	 * @throws NumberFormatException if value cannot be converted
	 */
	public static double doubleValue(Object value)
	{
		double result = 0.0d;
		
		if (value instanceof Number)
		{
			result = ((Number) value).doubleValue();
		}
		else
		{
			result = Double.parseDouble(value.toString());
		}
		
		return result;
	}
} 

//--- Development History  ---------------------------------------------------
//
// $Log: Conversions.java,v $
// Revision 1.4  2006/02/01 22:55:46  tames
// Added floatValue, shortValue, and booleanValue(int) methods.
//
// Revision 1.3  2006/01/26 21:48:39  tames
// Added booleanValue, intValue, and doubleValue conversion methods.
//
// Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
// Initial version
//
