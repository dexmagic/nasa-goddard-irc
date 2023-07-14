//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//	$Log: Sexagesimal.java,v $
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
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

package gov.nasa.gsfc.commons.numerics.types;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.StringTokenizer;

/**
 *   The Sexagesimal class stores a representation of a time in hours, minutes,
 *   seconds, and tenths of seconds.  It accepts a time string in the format
 *   HH:MM:SS.S and converts it internally to a numeric representation.  It 
 *   allows the time to be stored, updated, and compared to.  The time can 
 *   also be converted to a string in the above format.
 *
 *  TODO:  This file needs to be cleaned up.  There are a lot of methods
 *		(e.g. any compare methods not called compareTo which seem pretty
 *		useless.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *   @version $Date: 2004/07/12 14:26:24 $
 *   @author	 M. Grossman
 **/
public class Sexagesimal extends Number implements Comparable
{
	/**
	 *	Represents a positive value of the sign.
	 */
	public static final int POS_SIGN = 1;

	/**
	 *	Represents a negative value of the sign.
	 */
	public static final int NEG_SIGN = -1;

	/**
	 *  This constant represents the Maximum number of minutes
	 *  allowed in a Sexagesimal object.
	 */
	public final static int MAX_MINUTES = 60;
 
	/**
	 *  This constant represents the Maximum number of seconds
	 *  allowed in a Sexagesimal object.
	 */
	public final static double MAX_SECONDS = 60;

	/**
	 *  This constant represents the Minimum number of minutes
	 *  and seconds allowed in a Sexagesimal object.
	 */
	public final static double MIN = 0.0;

	//  String representation
	public static final String POS_SIGN_STR = "";
	public static final String NEG_SIGN_STR = "-";

	//  String formatting.
	private static DecimalFormat sSecondsForm = null;
	private static DecimalFormat sMinutesForm = null;

	//  Text parsing help
	private static final String COLON_SEP_STR = ":";
	private static final int INDEX_SEARCH_FAILED = -1;

	//  Pieces of the value.
	private int fSign = 0;
	private int fHours = 0;
	private int fMinutes = 0;
	private double fSeconds = 0;

	//  static initialization block for two formats.
	static
	{
		sSecondsForm = (DecimalFormat)NumberFormat.getInstance();
		sSecondsForm.applyPattern("#.00");
		sSecondsForm.setMaximumFractionDigits(2);
		sSecondsForm.setMinimumFractionDigits(2);
		sSecondsForm.setMinimumIntegerDigits(2);
		sMinutesForm = (DecimalFormat)NumberFormat.getInstance();
		sMinutesForm.applyPattern("#.00");
		sMinutesForm.setMaximumFractionDigits(0);
		sMinutesForm.setMinimumFractionDigits(0);
		sMinutesForm.setMinimumIntegerDigits(2);
	}

	/**
	 *   Default class constructor. Sets hours, minutes, and seconds to 0.
	 */
	public Sexagesimal()
	{
		fSign = POS_SIGN;
		fHours = 0;
		fMinutes = 0;
		fSeconds = 0.0;

		System.out.println("Test");
	}

	/**
	 *   Class constructor
	 *
	 *   @param time   Initial time value in the format HH:MM:SS.S
	 *
	 *  @exception NumberFormatException - if the String argument is not in the
	 *  correct format or, if the minutes or seconds contained
	 *  in the argument are larger than their MAX  or less than MIN
	 *  then a NumberFormatException
	 *  is thrown.
	 */
	public Sexagesimal(String time) throws NumberFormatException
	{
		setValue(time);
	}

	/**
	 *   Class constructor
	 *
	 *   @param sign		Sign (positive or negative) of the value,
	 *					  normally set as POS_SIGN or NEG_SIGN
	 *
	 *   @param hours	   Initial hours value
	 *
	 *   @param minutes		Initial minutes value
	 *
	 *   @param seconds	 Initial seconds value
	 *
	 *  @exception IllegalArgumentException   if the minutes or seconds 
	 *										contained in this Sexagesimal 
	 *										object are larger than their 
	 *										MAX  or less than MIN then an 
	 *										exception is thrown.
	 */
	public Sexagesimal(int sign, int hours, int minutes, double seconds) 
		throws IllegalArgumentException
	{
		//  Set the value.
		setSign(sign);
		setHours(hours);
		setMinutes(minutes);
		setSeconds(seconds);
		
		verifyValidMinutesSeconds();
	}
	
	/**
	 *   Set the time held in the Sexagesimal object.
	 *
	 *   @param newTime   New time value in the format HH:MM:SS.S
	 *
	 *  @exception NumberFormatException  if the minutes or seconds 
	 *	contained in this Sexagesimal object are larger than their MAX  
	 *	or less than MIN then an exception is thrown.
	 */
	public void setValue(String newTime) throws NumberFormatException
	{
		//  Break the value up into its three pieces.
		StringTokenizer tokens = new StringTokenizer(newTime, COLON_SEP_STR);
		fHours = Math.abs(new Integer(tokens.nextToken()).intValue());
		fMinutes = Math.abs(new Integer(tokens.nextToken()).intValue());
		fSeconds = Math.abs(new Double(tokens.nextToken()).doubleValue());
		
		//  Just check the whole string for the sign.
		if (newTime.indexOf(NEG_SIGN_STR) == INDEX_SEARCH_FAILED)
		{
			fSign = POS_SIGN;
		}
		else
		{
			fSign = NEG_SIGN;
		}

		//  Check validation.
		try
		{
			verifyValidMinutesSeconds();
		}
		catch(IllegalArgumentException ex)
		{
			throw new NumberFormatException();
		}
	}

	/**
	 *   Convert the time held in a Sexagesimal object to a string in the
	 *   format HH:MM:SS.S.
	 *
	 *   @return   String holding the time in HH:MM:SS.S format
	 */
	public String toString()
	{
		String signStr = fSign >= 0 ? POS_SIGN_STR : NEG_SIGN_STR;

		return signStr + fHours +  
			COLON_SEP_STR + 
			(sMinutesForm.format(fMinutes).toString()) + 
			COLON_SEP_STR + 
			(sSecondsForm.format(fSeconds).toString());
	}

	/**
	 *	Set the sign of the value.
	 *
	 *  @param sign  the new sign of the value, normally POS_SIGN or NEG_SIGN
	 */
	public void setSign(int sign)
	{
		if (sign >= 0)
		{
			fSign = POS_SIGN;
		}
		else
		{
			fSign = NEG_SIGN;
		}
	}
	
	/**
	 *   Set the number of hours held in this Sexagesimal object.
	 *
	 *   @param   hours   New hours value
	 */
	public void setHours(int hours)
	{
		fHours = Math.abs(hours);
	}

	/**
	 *   Set the number of minutes held in this Sexagesimal object.
	 *
	 *   @param   minutes   New minutes value
	 *  @exception IllegalArgumentException - if the minutes passed in
	 *  are larger than their MAX  or less than MIN
	 *  then an exception
	 *  is thrown.
	 */
	public void setMinutes(int minutes) throws IllegalArgumentException
	{
		fMinutes = minutes;
		verifyValidMinutesSeconds();
	}

	/**
	 *   Set the number of seconds held in this Sexagesimal object.
	 *
	 *   @param   seconds   New seconds value
	 *  @exception IllegalArgumentException - if the seconds passed in
	 *  are larger than their MAX  or less than MIN
	 *  then an exception
	 *  is thrown.
	 */
	public void setSeconds(double seconds)   throws IllegalArgumentException
	{
		fSeconds = seconds;
		verifyValidMinutesSeconds();
	}

 	/**
	 *   Get the sign of this Sexagesimal object.
	 *
	 *   @return  sign of the sexagesimal object, returned as POS_SIGN or 
	 *			NEG_SIGN (sign >=0 or sign < 0, respectively)
	 */
	public int getSign()
	{ 
		return fSign;
	}

 	/**
	 *   Get the number of hours held in this Sexagesimal object.
	 *
	 *   @return   Hours value held in this object
	 */
	public int getHours()
	{
		return fHours;
	}

	/**
	 *   Get the number of minutes held in this Sexagesimal object.
	 *
	 *   @return   Minutes value held in this object
	 */
	public int getMinutes()
	{
		return fMinutes;
	}

	/**
	 *   Get the number of seconds held in this Sexagesimal object.
	 *
	 *   @return   Seconds value held in this object
	 */
	public double getSeconds()
	{
		return fSeconds;
	}

	/**
	 *   Test if time value held in the Sexagesimal object passed to
	 *   this method is equal to the time value held in this object.
	 *
	 *   @param	otherTime  The Sexagesimal to be tested
	 *
	 *   @return   True if the time values are equal, false otherwise
	 */
	public boolean equals(Sexagesimal otherTime)
	{
		return (fSign == otherTime.getSign() &&
				fHours == otherTime.getHours() && 
				fMinutes == otherTime.getMinutes() &&
				fSeconds == otherTime.getSeconds());
	}

	/**
	 *   Test if time value held in the Sexagesimal object passed to
	 *   this method is less than the time value held in this object.
	 *
	 *   @param	otherTime  The Sexagesimal to be tested
	 *
	 *   @return   True if the time value in this object is less than
	 *			 that which is held in the other object, false otherwise
	 */
	public boolean lessThan(Sexagesimal time)
	{
		double mySeconds = convertToSeconds();
		double otherSeconds = convertToSeconds(time);

		return (mySeconds < otherSeconds);
	}

	/**
	 *   Test if time value held in the Sexagesimal object passed to
	 *   this method is greater than the time value held in this object.
	 *
	 *   @param	otherTime  The Sexagesimal to be tested
	 *
	 *   @return   True if the time value in this object is greater than
	 *			 that which is held in the other object, false otherwise
	 */
	public boolean greaterThan(Sexagesimal time)
	{
		double mySeconds = convertToSeconds();
		double otherSeconds = convertToSeconds(time);

		return (mySeconds > otherSeconds);
	}

	/**
	 *   Test if time value held in the Sexagesimal object passed to
	 *   this method is less than or equal to the time value held in 
	 *   this object.
	 *
	 *   @param	otherTime  The Sexagesimal to be tested
	 *
	 *   @return   True if the time value in this object is less than
	 *			 or equal to that which is held in the other object, 
	 *			 false otherwise
	 */
	public boolean lessThanOrEqual(Sexagesimal time)
	{
		double mySeconds = convertToSeconds();
		double otherSeconds = convertToSeconds(time);

		return (mySeconds <= otherSeconds);
	}

	/**
	 *   Test if time value held in the Sexagesimal object passed to
	 *   this method is equal to the time value held in this object.
	 *
	 *   @param	otherTime  The Sexagesimal to be tested
	 *
	 *   @return   True if the time value in this object is greater than
	 *			 or equal to that which is held in the other object, 
	 *			 false otherwise
	 */
	public boolean greaterThanOrEqual(Sexagesimal time)
	{
		double mySeconds = convertToSeconds();
		double otherSeconds = convertToSeconds(time);

		return (mySeconds >= otherSeconds);
	}

	/**
	 *  converts a double value to a Sexagesimal
	 *
	 *  @return Sexagesimal - the Sexagesimal representation of the argument
	 */
	public static Sexagesimal convertDoubleToSexagesimal(double value)
	{
		double tempValue = (double) Math.abs(value);
		int hours = (int) (tempValue / 3600);
		int minutes = (int) ((tempValue - (hours * 3600))/60);
		double seconds = (double) (tempValue - (hours * 3600) - 
								   (minutes * 60));

		//  Note that the value itself is used to get its sign.
		return  new Sexagesimal((int) value, hours, minutes, seconds);
	}
	
	/**
	 *   Convert the time held in this Sexagesimal object to seconds.
	 *   This is a double representation cast to a int
	 *   Needed to subclass Number
	 *
	 *   @return   The time value in seconds
	 */
	public int intValue()
	{
		return (int) convertToSeconds();
	}

	/**
	 *   Convert the time held in this Sexagesimal object to seconds.
	 *   Needed to subclass Number
	 *
	 *   @return   The time value in seconds
	 */
	public float floatValue()
	{
		return (float) convertToSeconds();
	}

	/**
	 *   Convert the time held in this Sexagesimal object to seconds.
	 *   This is a double representation cast to a long
	 *   Needed to subclass Number
	 *
	 *   @return   The time value in seconds
	 */
	public long longValue()
	{
		return (long) convertToSeconds();
	}

	/**
	 *   Convert the time held in this Sexagesimal object to seconds.
	 *   This is a double representation cast to a short
	 *   Needed to subclass Number
	 *
	 *   @return   The time value in seconds
	 */
	public short shortValue()
	{
		return (short) convertToSeconds();
	}

	/**
	 *   Convert the time held in this Sexagesimal object to seconds.
	 *   This is a double representation cast to a byte
	 *   Needed to subclass Number
	 *
	 *   @return   The time value in seconds
	 */
	public byte byteValue()
	{
		return (byte) convertToSeconds();
	}

	/**
	 *   Convert the time held in this Sexagesimal object to seconds.
	 *   This is a double representation cast to a double
	 *   Needed to subclass Number
	 *
	 *   @return   The time value in seconds
	 */
	public double doubleValue()
	{
		return convertToSeconds();
	}

	/**
	 *	Compare this object with the given object.
	 *
	 *  @param o  the object to be compared
	 *
	 *  @return  a negative integer, zero, or a positive integer as this
	 *		   object is less than, equal to, or greater than the specified
	 *		   object
	 */
	 public int compareTo(Object o)
	 {
		 int compareResult = 0;
		 double thisSeconds = convertToSeconds();
		 double oSeconds = convertToSeconds((Sexagesimal) o);

		 //  Compare the value in seconds.
		 if (thisSeconds < oSeconds)
		 {
			 compareResult = -1;
		 }
		 else if (thisSeconds > oSeconds)
		 {
			 compareResult = 1;
		 }

		 return compareResult;
	 }

	/**
	 *	 This method verifies that the minutes and seconds contained in this
	 *  Sexagesimal object are valid. The minutes cannot be larger than
	 *  MAX_MINUTES and the seconds cannot be larger than MAX_SECONDS.
	 *
	 *  @exception IllegalArgumentException  if the minutes or seconds 
	 *	contained in this Sexagesimal object are larger than their MAX 
	 *	or less than MIN then an exception is thrown.
	 */
	private void verifyValidMinutesSeconds() throws IllegalArgumentException
	{
		if(fMinutes >= MAX_MINUTES || fSeconds >= MAX_SECONDS || 
		   fMinutes < MIN || fSeconds < MIN)
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 *   Convert the time held in this Sexagesimal object to seconds.
	 *
	 *   @return   The time value in seconds
	 */
	private double convertToSeconds()
	{
		return convertToSeconds(this);
	}

	/**
	 *   Convert the time held in the Sexagesimal object passed in to
	 *   seconds.
	 *
	 *   @param	timeVal  The Sexagesimal object holding the time to
	 *					  be converted
	 *
	 *   @return   The time value in seconds
	 */
	private double convertToSeconds(Sexagesimal timeVal)
	{
		return convertToSeconds(timeVal.getSign(), timeVal.getHours(),
								timeVal.getMinutes(), timeVal.getSeconds());
	}

	/**
	 *	Convert the time passed in as hours, minutes, and seconds to
	 *  seconds.
	 *
	 *  @param sign  sign of the value, normally set as POS_SIGN or NEG_SIGN
	 *  @param hours  The number of hours
	 *  @param minutes  The number of minutes
	 *  @param seconds  The number of seconds
	 *
	 *   @return   The time value in seconds
	 */
	private double convertToSeconds(int sign, int hours, int minutes,
									double seconds)
	{
		double tempHours = (double) hours;
		double tempMinutes = (double) minutes;
		double tempSeconds = seconds;
		double returnValue = (tempHours * 3600.0D) + (tempMinutes * 60.0D) +
			tempSeconds;

		//  Fix the sign.
		if (sign < 0)
		{
			returnValue = -returnValue;
		}

		return returnValue;
	}
}
