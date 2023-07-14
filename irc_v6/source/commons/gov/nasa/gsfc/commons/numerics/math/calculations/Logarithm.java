//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//
//--- Development History:
//
//	$Log: Logarithm.java,v $
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/07/06 13:47:34  chostetter_cvs
//	More commons package restructuring
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//	
//	
//  ---------------------------------------------------
//
//	10/19/99	Ken Wootton
//		Initial version.
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

package gov.nasa.gsfc.commons.numerics.math.calculations;

public class Logarithm
{
	public static final double BASE_10 = 10;
	public static final double POW_OF_10 = 1e10;
	public static final double NEGATIVE_MAX = -1e5;

	private static final double NAT_LOG_BASE_10 = Math.log(BASE_10);

	/**
	 *	Replace each value in the given array with the log (base 10)
	 *  of that value.
	 *
	 *  @param values  the values to take the log of
	 *  @param numValues  the number of values to process, starting at
	 *					the beginning
	 */
	public static void doubleToLog(double[] values, int numValues)
	{
		//  Sanity check.
		if (numValues > values.length)
		{
			numValues = values.length;
		}

		for (int i = 0; i < numValues; i++)
		{
			values[i] = Math.log(values[i]) / NAT_LOG_BASE_10;
		}
	}

	/**
	 *	Take the log (base 10) of the given value.
	 *
	 *  @param value  the value to take the log of
	 *
	 *  @return  the log (base 10) of the given value
	 */
	public static double logBase10(double value)
	{
		return Math.log(value) / NAT_LOG_BASE_10;
	}

	/**
	 *	Take the log (base 10) of the given value.
	 *
	 *  @param value  the value to take the log of
	 *
	 *  @return  the log (base 10) of the given value
	 */
	public static float logBase10(float value)
	{
		double doubleValue = (double) value;

		return (float) logBase10(doubleValue);
	}

	/**
	 *	Return the inverse log of the given value.
	 *
	 *  @return  the inverse log
	 */
	public static double inverseLogBase10(double value)
	{
		return Math.pow(Logarithm.BASE_10, value);
	}
}
