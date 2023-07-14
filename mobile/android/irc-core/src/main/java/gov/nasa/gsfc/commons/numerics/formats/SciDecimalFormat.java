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

import java.text.DecimalFormat;

/**
 *	This class will format given numbers using scientific notation. 
 *  Any numbers within a certain threshold are reported in normal notation
 *  (e.g. 1.234).  Any numbers outside of that threshold (above or below)
 *  are reported in scientific notation (e.g. 1230000 = 1.23 E06).
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/11/16 18:47:45 $
 *  @author Ken Wootton
 *	@author Troy Ames
**/
public class SciDecimalFormat implements ValueFormat
{
	//  Constants that define when we should use scientific notation
	//  to represent double values.
	private static final double SCI_NOT_SMALL = .01;
	private static final double SCI_NOT_BIG = 100000;

	//  Default format pattern strings
	private static final String DECIMAL_PATTERN = "#,##0.0####";
	private static final String SCI_PATTERN = "00.###E0";
	
	//  Number formats
	private DecimalFormat fNormalFormat = null;
	private DecimalFormat fSciFormat = null;

	/**
	 *	Create a default format with the default number of digits.
	 */
	public SciDecimalFormat()
	{
		this (new DecimalFormat(DECIMAL_PATTERN),
			  new DecimalFormat(SCI_PATTERN));
	}

	/**
	 *	Create a format using the given number of decimal digits.
	 *
	 *  @param numDecimalDigits  the number of digits to show to the right
	 *						   of the decimal point
	 */
	public SciDecimalFormat(DecimalFormat decimalFormatter, DecimalFormat sciFormatter)
	{
		if (decimalFormatter == null || sciFormatter == null)
		{
			throw new IllegalArgumentException(
				"Arguments to the constructor cannot be null");
		}
		
		fNormalFormat = decimalFormatter;
		fSciFormat	= sciFormatter;
	}

	/**
	 *	Format the given float value using scientific notation, if necessary.
	 *
	 *  @param value  float value to format
	 *
	 *  @return  the string representation of the given value
	 */
	public synchronized String format(float value)
	{
		return format((double) value);
	}

	/**
	 *	Format the given double value using scientific notation, if necessary.
	 *
	 *  @param value  double value to format
	 * 
	 *  @return  the string representation of the given value
	 */
	public synchronized String format(double value)
	{
		String str = null;

		//  We use scientific notation to denote numbers that
		//  are bigger or smaller than some defined constants.
		if ((value < SCI_NOT_SMALL && value > -SCI_NOT_SMALL && value != 0) ||
			value > SCI_NOT_BIG || value < -SCI_NOT_BIG)
		{
			str = fSciFormat.format(value);
		}
		else
		{
			str = fNormalFormat.format(value);
		}

		return str;
	}

	/**
	 *	Set the format pattern to apply.
	 *
	 *  @param pattern  the pattern
	 */
	public void setPattern(String pattern)
	{
		fNormalFormat.applyPattern(pattern);
	}

	/**
	 *	Get the pattern.
	 *
	 *  @return  the pattern
	 */
	public String getPattern()
	{
		return fNormalFormat.toPattern();
	}

}

//--- Development History  ---------------------------------------------------
//
//  $Log: SciDecimalFormat.java,v $
//  Revision 1.4  2004/11/16 18:47:45  tames
//  Java doc updates
//
//  Revision 1.3  2004/11/08 23:01:35  tames
//  Updated format architecture and javadoc comments
//
//
