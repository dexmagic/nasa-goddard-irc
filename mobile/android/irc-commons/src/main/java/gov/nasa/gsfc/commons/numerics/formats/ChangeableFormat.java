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


/**
 * This format allows the user to switch between a time formatter and generic
 * formatter while acting as a single format.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2004/11/08 23:01:35 $
 * @author Ken Wootton
 */
public class ChangeableFormat implements ValueFormat
{
	//  Possible formats
	private ValueFormat fTimeFormat = null;
	private ValueFormat fGenericFormat = null;

	//  Current format
	private ValueFormat fCurFormat = null;

	/**
	 * 	 Create a new format that can switch between the supplied formats.
	 *  The generic format will be used initially.
	 * 
	 *  @param timeFormat  the formatter for time values
	 *  @param genericFormat  the formatter for everything else
	 */
	public ChangeableFormat(ValueFormat timeFormat,
							 ValueFormat genericFormat)
	{
		fTimeFormat = timeFormat;
		fGenericFormat = genericFormat;
		
		fCurFormat = fGenericFormat;	
	}

	/**
	 *	Format the given float value into a string.
	 *
	 *  @param value  double value to format
	 * 
	 *  @return  the string representation of the given value
	 */
	public String format(float value)
	{
		return fCurFormat.format(value);
	}

	/**
	 *	Format the given double value into a string.
	 *
	 *  @param value  double value to format
	 * 
	 *  @return  the string representation of the given value
	 */
	public String format(double value)
	{
		return fCurFormat.format(value);
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.commons.numerics.formats.ValueFormat#setPattern(java.lang.String)
	 */
	public void setPattern(String pattern)
	{
		fCurFormat.setPattern(pattern);
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.commons.numerics.formats.ValueFormat#getPattern()
	 */
	public String getPattern()
	{
		return fCurFormat.getPattern();
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ChangeableFormat.java,v $
//  Revision 1.4  2004/11/08 23:01:35  tames
//  Updated format architecture and javadoc comments
//
//
