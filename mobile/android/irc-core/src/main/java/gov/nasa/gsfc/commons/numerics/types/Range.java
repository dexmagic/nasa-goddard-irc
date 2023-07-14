//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Range.java,v $
//  Revision 1.4  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/05/27 18:21:53  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.2  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
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

package gov.nasa.gsfc.commons.numerics.types;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.strings.FullStringTokenizer;


/**
 *  This class provides a simple way to stores a range as a set of values.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:24 $
 *  @author Ken Wootton
**/

public class Range implements Cloneable, Serializable
{
	private static final String CLASS_NAME = 
		Range.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	/**
	 * The Stream Unique Identifier for this class.
	**/
	private static final long serialVersionUID = 9019418917262081895L;
	
	//  String representation constants
	private static final String RANGE_PREFIX = "[";
	private static final String BOUNDS_SEP = ", ";
	private static final String RANGE_SUFFIX = "]";

	//  Exception messages.
	private static final String BAD_FORMAT_MSG = "Range is not formatted correctly:  ";

	//  Defaults
	private static final double DEF_MIN = 0;
	private static final double DEF_MAX = 0;

	private double fMin = DEF_MIN;
	private double fMax = DEF_MAX;

	/**
	 *  Create a range of size 0.  The min and max of this range will also be 0.
	**/
	public Range()
	{
	}

	/**
	 *  Create a range with the given minimum and maximum.
	 *
	 *  @param min  the minimum of the range
	 *  @param max  the maximum of the range
	**/
	public Range(double min, double max)
	{
		fMin = min;
		fMax = max;
	}

	/**
	 *  Create a range for the given set of values.
	 *
	 *  @param values  the values used to create the range
	**/
	public Range(double[] values)
	{
		//  Initialize the range.
		if (values.length > 0)
		{
			fMin = values[0];
			fMax = values[0];
		}

		//  Go through the list and find the min and max.
		for (int i = 0; i < values.length; i++)
		{
			if (values[i] < fMin)
			{
				fMin = values[i];
			}

			if (values[i] > fMax)
			{
				fMax = values[i];
			}
		}
	}

	/**
	 *  Create a new range using a string representation.
	 *
	 *  @param str  string representation of the range, presumably retrieved
	 *			  from the @see #toString method of this class
	 *  @throws IllegalArgumentException  if the given string cannot be
	 *									parsed
	**/
	public Range(String str)
		throws IllegalArgumentException
	{
		//  Get the indices of the prefix and suffix.
		int prefixIndex = str.indexOf(RANGE_PREFIX);
		int suffixIndex = str.indexOf(RANGE_SUFFIX);

		try
		{
			//  Make sure prefix and suffix exist.
			if (prefixIndex >= 0 && suffixIndex >= 0)
			{
				FullStringTokenizer rangeTok = new FullStringTokenizer(
					str.substring(prefixIndex + 1, suffixIndex), BOUNDS_SEP);

				//  Get the bounds
				fMin = rangeTok.nextDouble();
				fMax = rangeTok.nextDouble();
			}
		}

		//  Report parsing errors.
		catch (NumberFormatException e)
		{
			String message = BAD_FORMAT_MSG + str;
			
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"Range (ctor)", message);
			}

			throw new IllegalArgumentException(message);
		}
		catch (NoSuchElementException e)
		{
			String message = BAD_FORMAT_MSG + str;
			
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"Range (ctor)", message);
			}

			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *  Get the minimum value of the range.
	 *
	 *  @return  minimum value of the range
	**/
	public double getMin()
	{
		return fMin;
	}

	/**
	 *  Get the maximum value of the range.
	 *
	 *  @return  maximum value of the range
	**/
	public double getMax()
	{
		return fMax;
	}

	/**
	 *  Get the size of the range.
	 *
	 *  @return  the size of the range
	**/
	public double getSize()
	{
		return fMax - fMin;
	}

	/**
	 *  Advance the interval represented by the range.  Note that this
	 *  size of the range will remain the same.
	 *
	 *  @param shiftSize  the amount to advance the interval
	**/
	public void advance(double shiftSize)
	{
		fMin += shiftSize;
		fMax += shiftSize;
	}

	/**
	 *  Increase the size of the range by adding the given amount to
	 *  end of this range.
	 *
	 *  @param size  the amount by which the range should be increased
	**/
	public void increase(double size)
	{
		fMax += size;
	}

	 /**
	  *  Create a clone of the object.
	  *
	  *  @return  the clone
	 **/
	 public Object clone()
	 {
		 return new Range(fMin, fMax);
	 }

	 /**
	  *  Test for equality.
	  *
	  *  @param obj  the object to test against
	 **/
	 public boolean equals(Object obj)
	 {
		 //  Get the simple pointer test out of the way.
		 if (obj == this)
		 {
			 return true;
		 }

		 boolean isEqual = false;

		 //  Test the range bounds.
		 Range rangeToTest = (Range) obj;
		 if (rangeToTest != null)
		 {
			 isEqual = rangeToTest.getMin() == fMin &&
			 	rangeToTest.getMax() == fMax;
		 }

		 return isEqual;
	 }

	/**
	 *  Get the string representation of the range.
	 *
	 *  @return  the string representation of the range
	 */
	public String toString()
	{
		return RANGE_PREFIX + fMin + BOUNDS_SEP + fMax + RANGE_SUFFIX;
	}
}
