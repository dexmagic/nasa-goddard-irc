//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/math/Utilities.java,v 1.4 2005/11/14 22:01:12 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: Utilities.java,v $
//	Revision 1.4  2005/11/14 22:01:12  chostetter_cvs
//	Removed dependency on ibm array lib
//	
//	Revision 1.3  2004/11/08 23:01:56  tames
//	Added compareDouble method
//	
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

package gov.nasa.gsfc.commons.numerics.math;

import org.jscience.mathematics.numbers.Complex;

import gov.nasa.gsfc.commons.numerics.Constants;
import gov.nasa.gsfc.commons.numerics.types.Range;


/**
 *  The Utilities class implements a number of general-purpose static
 *  mathematical methods.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2005/11/14 22:01:12 $
 *	@author Carl Hostetter
**/
public class Utilities implements Constants
{
	public static final double BASE_10 = 10;
	public static final double POW_OF_10 = 1e10;
	public static final double NEGATIVE_MAX = -1e5;

	private static final double NAT_LOG_BASE_10 = Math.log(BASE_10);

	/**
	 * Compares two double values to determine if the first is less than, equal to,
	 * or greater than the second. Equality is determined by the result of
	 * <code>Math.abs((x / y) - 1.0D) < epsilon</code> being true. If either
	 * x or y is 0 then equality is the result of 
	 * <code>Math.abs(x - y) < epsilon</code> being true.
	 * 
	 * @param x	the first double for comparison
	 * @param y the second double for comparison
	 * @param epsilon the maximum difference to consider as equal
	 * @return	a negative integer, zero, or a positive integer as x is less 
	 * 			than, equal to, or greater than y.
	 */
	public static int compareDouble(double x, double y, double epsilon)
	{
		// Initialize to x is less than y
		int result = -1;
		
		// Guard against divide by zero
		if (y == 0.0 || x == 0.0)
		{
			if (Math.abs(x - y) < epsilon)
			{
				// They are equal or close enough to be considered equal
				result = 0;
			}
			else if (x > y)
			{
				result = 1;
			}				
		}
		else if (Math.abs((x / y) - 1.0D) < epsilon)
		{
			// They are equal or close enough to be considered equal
			result = 0;
		}
		else if (x > y)
		{
			// x is greater than y
			result = 1;
		}
		
		return result;
	}
	
	/**
	 *  Returns the reversed-bit value corresponding to the given value of the
	 *  given number of bits
	 *
	 *  @param value A value
	 *  @param numBits The number of bits to reverse
	 *  @returns The corresponding reversed-bit value
	**/
	public static int reverseBits(int value, int numBits)
	{
		int result = 0;

		int leftMask = 1 << (numBits -1);
		int rightMask = 1;

		while (leftMask != 0)
		{
			if ((value & rightMask) != 0)
			{
				result |= leftMask;
			}

			leftMask >>= 1;
			rightMask <<= 1;
		}

		return (result);
	}


	/**
	 *  Normalizes the given set of real values by calculating the mean of all
	 *  the values and then subtracting this mean from all the values.
	 *
	 *  @param values A set of real values
	**/
	public static void normalize(double[] values)
	{
		if (values != null)
		{
			int numValues = values.length;
			double accumulation = 0;
			int count = 0;
			double mean = 0;

			for (int i = 0; i < numValues; i++)
			{
				accumulation += values[i];
				count++;
			}

			mean = accumulation / count;

			for (int i = 0; i < numValues; i++)
			{
				values[i] -= mean;
			}
		}
		else
		{
			String detail = "Utilities.normalize(double[]) was passed " +
				"a null set of values";
			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Normalizes the given set of real values by calculating the mean of
	 *  all the values and then subtracting this mean from all the values while
	 *  skipping the values at the given list of skip index Ranges (if any).
	 *
	 *  @param values A set of real values
	 *  @param skipIndexRanges A set of index ranges to exclude from the
	 *	  normalization
	**/
	public static void normalize(double[] values, Range[] skipIndexRanges)
	{
		if ((skipIndexRanges == null) || (skipIndexRanges.length == 0))
		{
			normalize(values);
		}
		else if (values != null)
		{
			int numValues = values.length;
			double accumulation = 0;
			int count = 0;
			double mean = 0;

			int numSkipRanges = skipIndexRanges.length;

			int nextSkipRangeIndex = 0;
			Range nextSkipRange = skipIndexRanges[nextSkipRangeIndex];
			nextSkipRangeIndex++;

			boolean skipRangeAhead = true;
			int nextSkipRangeStart = (int) nextSkipRange.getMin();
			int nextSkipRangeEnd = (int) nextSkipRange.getMax();

			for (int i = 0; i < numValues; i++)
			{
				while (skipRangeAhead &&
					(i >= nextSkipRangeStart) && (i <= nextSkipRangeEnd))
				{
					// We're in a skip range, so we can just advance to the
					// end of the current skip range.

					i = nextSkipRangeEnd + 1;

					if (nextSkipRangeIndex < numSkipRanges)
					{
						nextSkipRange = skipIndexRanges[nextSkipRangeIndex];
						nextSkipRangeIndex++;

						skipRangeAhead = true;
						nextSkipRangeStart = (int) nextSkipRange.getMin();
						nextSkipRangeEnd = (int) nextSkipRange.getMax();
					}
					else
					{
						skipRangeAhead = false;
					}
				}

				if (i < numValues)
				{
					accumulation += values[i];
					count++;
				}
			}

			mean = accumulation / count;

			nextSkipRange = skipIndexRanges[0];
			nextSkipRangeIndex = 1;

			skipRangeAhead = true;
			nextSkipRangeStart = (int) nextSkipRange.getMin();
			nextSkipRangeEnd = (int) nextSkipRange.getMax();

			for (int i = 0; i < numValues; i++)
			{
				while (skipRangeAhead &&
					(i >= nextSkipRangeStart) && (i <= nextSkipRangeEnd))
				{
					// We're in a skip range, so we can just advance to the
					// end of the current skip range.

					i = nextSkipRangeEnd + 1;

					if (nextSkipRangeIndex < numSkipRanges)
					{
						nextSkipRange = skipIndexRanges[nextSkipRangeIndex];
						nextSkipRangeIndex++;

						skipRangeAhead = true;
						nextSkipRangeStart = (int) nextSkipRange.getMin();
						nextSkipRangeEnd = (int) nextSkipRange.getMax();
					}
					else
					{
						skipRangeAhead = false;
					}
				}

				if (i < numValues)
				{
					values[i] -= mean;
				}
			}
		}
		else
		{
			String detail = "Utilities.normalize(double[], Range[]) was passed " +
				"a null set of values";
			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Returns the log base 2 of the given value.
	 *
	 *  @param value A value
	 *  @return The log base 2 of the given value
	**/
	public static int logBase2(int value)
	{
		int result = 0;

		for (int mask = 1; (mask < value) && (mask != 0); mask <<= 1)
		{
			++result;
		}

		return (result);
	}


	public static double exp2(double x)
	{
		return (Math.exp(x * LOG_OF_TWO));
	}

	public static double log2(double x)
	{
		return (Math.log(x) * INVERSE_LOG_OF_TWO);
	}

	public static int exp2int (int x)
	{
		return (1 << x);
	}

	public static int ceil_log2int (int x)
	{
		// For input values that are exactly 2^N, we have the problem that
		// the double result might be slightly more than N due to finite
		// precision.  This will cause the result to be rounded up incorrectly.
		// A slight downwards error will not cause a problem.
		//
		// We solve this problem by reducing the INTEGER input value by 0.1
		// which will overwhelm any finite precision problem.  Since the
		// input is INTEGER, this will not generate erroneous outputs for
		// any (legal) input value.
		//
		return ((int)(Math.ceil(log2(x-0.1))));
	}

	public static int floor_log2int (int x)
	{
		// For input values that are exactly 2^N, we have the problem that
		// the double result might be slightly less than N due to finite
		// precision.  This will cause the result to be rounded down
		// incorrectly.
		// A slight upwards error will not cause a problem.
		//
		// We solve this problem by increasing the INTEGER input value by 0.1
		// which will overwhelm any finite precision problem.  Since the
		// input is INTEGER, this will not generate erroneous outputs for
		// any (legal) input value.
		//
		return ((int)(Math.floor(log2(x+0.1))));
	}

	/**
	 *	Rounds the given value up to the next power of 2.
	 *
	 *	@param value An interger value
	 *  @return The value rounded up to the next power of 2
	 */
	public static int roundUpToNextPowerOfTwo(int value)
	{
		return (exp2int(ceil_log2int(value)));
	}


	/**
	 *	Rounds the given value down to the prior power of 2.
	 *
	 *	@param value An interger value
	 *  @return The value rounded down to the prior power of 2
	 */
	public static int roundDownToPriorPowerOfTwo(int value)
	{
		int result = 0;

		if (value != 0)
		{
			result = exp2int(floor_log2int(value));
		}

		return (result);
	}


	/**
	 *  Converts the given polar coordinates to a Complex representation.
	 *
	 *  @param radius The radius of the coordinate
	 *  @param theta The angle of the coordinate
	 *  @param A Complex holding the results
	**/
	public static Complex polarToComplex(double radius, double theta)
	{
		return(Complex.valueOf(radius * Math.cos(theta), radius * Math.sin(theta)));
	}


	/**
	 *  Returns the natural logarithm of the given Complex value as a Complex.
	 *
	 *  @param x A Complex value
	 *  @param imaginaryPart The imaginary part of a complex input value
	 *  @param A Complex holding the natural logarithm of the given complex
	 *	  input value
	**/
	public static Complex exp(Complex x)
	{
		return (polarToComplex(Math.exp(Math.abs(x.getReal())), x.getImaginary()));
	}

	/**
	 *	Converts the given number of seconds to the corresponding number of
	 *  cycles, based on the given cycle period.
	 *
	 *	@param seconds Some number of seconds
	 *	@param cyclePeriod The cycle period in seconds (i.e., the number of
	 *	  seconds required complete one cycle)
	 *	@return The number of cycles that will occur in the given number of
	 *	  seconds
	**/
	public static double convertSecondsToCycles(double seconds,
		double cyclePeriod)
	{
		double result = 0.0D;

		double cyclesPerSecond = 0.0D;

		if (cyclePeriod > 0.0D)
		{
			cyclesPerSecond = 1.0D / cyclePeriod;
		}

		result = cyclesPerSecond * seconds;

		return (result);
	}
	
	/**
	 *  Replace each value in the given array with the log (base 10)
	 *  of that value.
	 *
	 *  @param values  the values to take the log of
	 *  @param numValues  the number of values to process, starting at
	 *                    the beginning
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
	 *  Take the log (base 10) of the given value.
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
	 *  Take the log (base 10) of the given value.
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
	 *  Return the inverse log of the given value.
	 *
	 *  @return  the inverse log
	 */
	public static double inverseLogBase10(double value)
	{
		return Math.pow(BASE_10, value);
	}
}
