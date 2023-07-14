//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/types/ComplexArray.java,v 1.3 2005/11/14 22:01:12 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: ComplexArray.java,v $
//	Revision 1.3  2005/11/14 22:01:12  chostetter_cvs
//	Removed dependency on ibm array lib
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

package gov.nasa.gsfc.commons.numerics.types;

import org.jscience.mathematics.numbers.Complex;

import gov.nasa.gsfc.commons.numerics.Constants;
import gov.nasa.gsfc.commons.numerics.math.Utilities;


/**
 *  The ComplexArray class uses two internal arrays of double to efficiently
 *  represent an array of Complex values.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2005/11/14 22:01:12 $
 *	@author Carl Hostetter
**/

public class ComplexArray implements Constants
{
	public int length = 0;

	public double[] realParts;
	public double[] imaginaryParts;


	/**
	 *  Constructs a ComplexArray having the given number of elements.
	 *
	 *  @param size The number of elements in the new array
	**/

	public ComplexArray(int size)
	{
		this(null, null, size);
	}


	/**
	 *  Constructs a ComplexArray having the given set of real values and a
	 *  corresponding set of zero-valued imaginary parts of the same length.
	 *
	 *  @param size The number of elements in the new array
	**/

	public ComplexArray(double[] realValues)
	{
		this(realValues, null, 0);
	}


	/**
	 *  Constructs a ComplexArray having the same set of real values and
	 *  imaginary values as the two given sets of double values
	 *
	 *  @param size The number of elements in the new array
	**/

	public ComplexArray(double[] realValues, double[] imaginaryValues)
	{
		this(realValues, imaginaryValues, 0);
	}


	/**
	 *  Constructs a ComplexArray having the same set of real parts and
	 *  imaginary parts as the given ComplexArray
	 *
	 *  @param array A ComplexArray
	**/

	public ComplexArray(ComplexArray array)
	{
		this(array.realParts, array.imaginaryParts, 0);
	}


	/**
	 *  Constructs a ComplexArray having the same set of real parts and
	 *  imaginary parts as the given ComplexArray, but limited in length to the
	 *  given size.
	 *
	 *  @param array A ComplexArray
	 *  @param size The size of the new array
	**/

	public ComplexArray(ComplexArray array, int size)
	{
		this(array.realParts, array.imaginaryParts, size);
	}


	/**
	 *  Constructs a ComplexArray having the same set of real values and
	 *  imaginary values as the two given sets of double values, but limited
	 *  in length to the given size.
	 *
	 *  @param realValues An array of real values
	 *  @param imaginaryValues An array of imaginary values
	 *  @param size The size of the new array
	**/

	public ComplexArray(double[] realValues, double[] imaginaryValues,
		int size)
	{
		length = 0;

		if ((realValues != null) && (imaginaryValues != null))
		{
			length = Math.max(realValues.length, imaginaryValues.length);
		}
		else if (realValues != null)
		{
			length = realValues.length;
		}
		else if (imaginaryValues != null)
		{
			length = imaginaryValues.length;
		}

		if ((size > 0) && ((length == 0) || (size < length)))
		{
			length = size;
		}

		realParts = new double[length];
		imaginaryParts = new double[length];

		if (realValues != null)
		{
			System.arraycopy(realValues, 0, realParts, 0, length);
		}

		if (imaginaryValues != null)
		{
			System.arraycopy(imaginaryValues, 0, imaginaryParts, 0, length);
		}
	}


	/**
	 *  Sets the psuedo-Complex element at the given index to the given real
	 *  and imaginary values.
	 *
	 *  @param index The index of the pseudo-Complex element
	 *  @param real The real value of the psuedo-Complex element
	 *  @param imaginary The imaginary value of the psuedo-Complex element
	**/

	public void set(int index, double real, double imaginary)
	{
		realParts[index] = real;
		imaginaryParts[index] = imaginary;
	}


	/**
	 *  Sets the psuedo-Complex element at the given index to that of the given
	 *  Complex.
	 *
	 *  @param index The index of the pseudo-Complex element
	 *  @param value A Complex value
	**/

	public void set(int index, Complex value)
	{
		realParts[index] = value.getReal();
		imaginaryParts[index] = value.getImaginary();
	}


	/**
	 *  Returns the pseudo-Complex element at the given index.
	 *
	 *  @param index The index of the pseudo-Complex element
	 *  @param result A Complex to hold the result
	**/

	public Complex get(int index)
	{
		return (Complex.valueOf(realParts[index], imaginaryParts[index]));
	}


	/**
	 *  Returns a new ComplexArray representing this ComplexArray plus enough
	 *  additional 0-valued elements at the end to expand its size to the given
	 *  size.
	 *
	 *  @param array A ComplexArray
	 *  @param newSize The size to which to expand this ComplexArray
	 *  @return A new ComplexArray representing this array plus enough
	 *	  additional 0-valued elements at the end to expand its size to the
	 *	  given size
	**/

	public ComplexArray expand(int newSize)
	{
		ComplexArray result = this;

		if (newSize > length)
		{
			result = new ComplexArray(newSize);

			System.arraycopy(realParts, 0, result.realParts, 0, length);

			System.arraycopy(imaginaryParts, 0, result.imaginaryParts, 0,
				length);
		}

		return (result);
	}


	/**
	 *  Returns a new ComplexArray representing this ComplexArray plus enough
	 *  additional 0-valued elements at the end to expand its size to the next
	 *  highest power of two.
	 *
	 *  @param array A ComplexArray
	 *  @return A new ComplexArray representing this ComplexArray plus enough
	 *	  additional 0-valued elements at the end to expand its size to the
	 *	  next highest power of two
	**/

	public ComplexArray expandToNextPowerOfTwo()
	{
		int newSize = 1;

		while (newSize < length)
		{
			newSize <<= 1;

			if (newSize == 0)
			{
				String detail = "Array expansion failed";
				throw (new IllegalArgumentException(detail));
			}
		}

//		newSize <<= 1;

		return (expand(newSize));
	}


	/**
	 *  Reorders this ComplexArray based on the bit-reversal value of its array
	 *  indices, and returns the results in a new ComplexArray.
	 *
	 *  <P>This is a standard technique that allows for an iterative
	 *  implementation of the inverse FFT algorithm.
	 *
	 *  @return A new ComplexArray having the bit-reversal ordering of values
	**/

	public ComplexArray bitReversalReorder()
	{
		ComplexArray result = new ComplexArray(length);

		int numBitsInValueIndex = Utilities.logBase2(length);

		for (int i = 1; i < length; i++)
		{
			int targetIndex = Utilities.reverseBits(i, numBitsInValueIndex);

			result.realParts[targetIndex] = realParts[i];
			result.imaginaryParts[targetIndex] = imaginaryParts[i];
		}

		return (result);
	}


	/**
	 *  Adds the given target ComplexArray to this ComplexArray, and returns
	 *  the result in a new ComplexArray.
	 *
	 *  @param target A ComplexArray
	 *  @return A new ComplexArray holding the results
	**/

	public ComplexArray add(ComplexArray target)
	{
		ComplexArray result = new ComplexArray(this);

		if (length < target.length)
		{
			result = result.expand(target.length);
		}

		for (int i = target.length - 1; i >= 0; i--)
		{
			realParts[i] += target.realParts[i];
			imaginaryParts[i] += target.imaginaryParts[i];
		}

		return (result);
	}


	/**
	 *  Subtracts the given target ComplexArray from this ComplexArray, and
	 *  returns the result in a new ComplexArray.
	 *
	 *  @param target A ComplexArray
	 *  @return A new ComplexArray holding the results
	**/

	public ComplexArray subtract(ComplexArray target)
	{
		ComplexArray result = new ComplexArray(this);

		if (length < target.length)
		{
			result = result.expand(target.length);
		}

		for (int i = target.length - 1; i >= 0; i--)
		{
			realParts[i] -= target.realParts[i];
			imaginaryParts[i] -= target.imaginaryParts[i];
		}

		return (result);
	}


	/**
	 *  Multiplies this ComplexArray by the given target ComplexArray, and
	 *  returns the result in a new ComplexArray. The resulting ComplexArray
	 *  will have 2n - 1 elements to accomodate the multiplication.
	 *
	 *  @param target A ComplexArray
	 *  @throws IllegalArgumentException if this ComplexArray and the given
	 *	  target ComplexArray do not have the same length
	**/

	public ComplexArray multiply(ComplexArray target)
	{
		ComplexArray result = null;

		if (length == target.length)
		{
			result = new ComplexArray((length + 2) + 1);

			for (int i = length - 1; i >= 0; i--)
			{
				for (int j = length - 1; j >= 0; j--)
				{
					double this_real = realParts[i];
					double this_imaginary = imaginaryParts[i];

					double target_real = target.realParts[j];
					double target_imaginary = target.imaginaryParts[j];

					realParts[i + j] += (this_real * target_real) -
						(this_imaginary * target_imaginary);
					imaginaryParts[i + j] += (this_imaginary * target_real) +
							(this_real * target_imaginary);
				}
			}
		}
		else
		{
			String detail = "ComplexArrays must have the same number of " +
				"elements to be muliplied together.";

			throw (new IllegalArgumentException(detail));
		}

		return (result);
	}
}
