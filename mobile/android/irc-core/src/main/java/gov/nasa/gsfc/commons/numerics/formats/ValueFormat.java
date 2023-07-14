//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ValueFormat.java,v $
//  Revision 1.3  2004/11/08 23:01:35  tames
//  Updated format architecture and javadoc comments
//
//  Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
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

package gov.nasa.gsfc.commons.numerics.formats;

/**
 *	This interface defines the methods of a generic formatter for numbers.  
 *  Nothing is implied about the returned format other than it probably
 *  contains a decimal point.
 *
 *  @see SciDecimalFormat
 *  @see TimeFormat
 *  
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2004/11/08 23:01:35 $
 *  @author Ken Wootton
 */
public interface ValueFormat
{
	/**
	 *	Format the given float value into a string.
	 *
	 *  @param value  float value to format
	 *
	 *  @return  the string representation of the given value
	 */
	public String format(float value);

	/**
	 *	Format the given double value into a string.
	 *
	 *  @param value  double value to format
	 * 
	 *  @return  the string representation of the given value
	 */
	public String format(double value);

	/**
	 *	Set the format pattern to apply.
	 *
	 *  @param pattern  the pattern
	 */
	public void setPattern(String pattern);

	/**
	 *	Get the pattern.
	 *
	 *  @return  the pattern
	 */
	public String getPattern();
}


