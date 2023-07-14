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

package gov.nasa.gsfc.commons.numerics.formats;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Creates String representations of values that are time based. The 
 * values to be formatted are assumed to be since January 1, 1970, 00:00:00 GMT
 * in units set by the <code>setUnit</code> method.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2004/11/16 19:44:57 $
 * @author Ken Wootton
 * @author Troy Ames
 */
public class TimeFormat implements ValueFormat
{
	private static final double MSEC_PER_SEC = 1000d;
	private static final String MSEC_UNITS = "msec";
	private static final String MILLISECOND_UNITS = "milliseconds";
	private static final String SEC_UNITS = "sec";
	private static final String SECOND_UNITS = "seconds";

	private SimpleDateFormat fExplicitFormat = null;

	//private String fPattern = "ss.S";

	private String fUnit = SEC_UNITS;

	/**
	 * Create a default time formatter with a default localized
	 * <code>SimpleDateFormat</code>.
	 */
	public TimeFormat()
	{
		fExplicitFormat = new SimpleDateFormat();
	}

	/**
	 * Create a format using the given pattern. Note that the pattern should
	 * follow that of the <code>SimpleDateFormat</code> class.
	 * 
	 * @param pattern pattern to use
	 * 
	 * @see SimpleDateFormat
	 */
	public TimeFormat(String pattern)
	{
		fExplicitFormat = new SimpleDateFormat(pattern);
	}

	/**
	 * Format the given float value into a string.
	 * 
	 * @param value time value
	 * 
	 * @return the string representation of the given value
	 */
	public synchronized String format(float value)
	{
		return format((double) value);
	}

	/**
	 * Format the given double value into a string.
	 * 
	 * @param value time value
	 * 
	 * @return the string representation of the given value
	 */
	public synchronized String format(double value)
	{
		double msecValue = value;

		if (fUnit == SEC_UNITS)
		{
			//  Convert seconds to milliseconds to get a date
			msecValue = value * MSEC_PER_SEC;
		}

		Date date = new Date(Math.round(msecValue));

		return fExplicitFormat.format(date);
	}

	/**
	 * Set the unit that this formatter will use to create labels from. The
	 * values passed to the <code>format</code> method must be in the given 
	 * units to create a correct label.
	 * 
	 * @param units the units String
	 */
	public void setUnit(String unit)
	{
		String unitString = unit.toLowerCase();
		
		if (unitString.equals(MSEC_UNITS) 
				|| unitString.equals(MILLISECOND_UNITS))
		{
			fUnit = MSEC_UNITS;
		}
		else if (unitString.equals(SEC_UNITS)
				|| unitString.equals(SECOND_UNITS))
		{
			fUnit = SEC_UNITS;
		}
	}
	
	/**
	 * Get the unit that this formatter will expect the values to be in.
	 * 
	 * @return the value units for this formatter.
	 */
	public String getUnit()
	{
		return fUnit;
	}
	
	/**
	 * Set the format pattern to apply. Note that the pattern should follow that
	 * of the <code>SimpleDateFormat</code> class.
	 * 
	 * @param pattern the pattern
	 */
	public void setPattern(String pattern)
	{
		//fPattern = pattern;
		fExplicitFormat = new SimpleDateFormat(pattern);
	}

	/**
	 * Get the pattern.
	 * 
	 * @return the pattern
	 */
	public String getPattern()
	{
		return fExplicitFormat.toPattern();
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: TimeFormat.java,v $
//  Revision 1.5  2004/11/16 19:44:57  tames
//  Changed default format and updated java docs.
//
//  Revision 1.4  2004/11/16 18:48:40  tames
//  Simplified class and allowed a user defined format pattern.
//
//  Revision 1.3  2004/11/08 23:01:35  tames
//  Updated format architecture and javadoc comments
//
//

