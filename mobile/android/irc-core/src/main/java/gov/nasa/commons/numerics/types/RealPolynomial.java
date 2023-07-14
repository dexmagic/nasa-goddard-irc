//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/types/RealPolynomial.java,v 1.2 2004/07/12 14:26:24 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: RealPolynomial.java,v $
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


/**
 *  A RealPolynomial is a Polynomial in which each of its set of coefficients
 *  a[i] is real valued (here, double).
 *
 *  <P>This code is based on that found in Scott Robert Ladd's
 *  <i>Java Algoriths</i> (McGraw Hill, 1997), pp. 43-47, with modifications
 *  and adaptations (esp. in terminology) after Thomas H. Cormen, et al.,
 *  <i>Introduction to Algorithms</i> (McGraw Hill, 1991), pp. 776-82.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2004/07/12 14:26:24 $
 *	@author Carl Hostetter
**/

public class RealPolynomial extends Polynomial
{
	/**
	 *  A coefficient representation of a polynomial of degree-bound n:
	 *  <P>
	 *  A(x) = SUM[j=0->n-1] a[j]x^[j]
	 *
	 * <P> is a set of coefficients a = (a[o], a[1], ..., a[n-1])
	**/

	protected double[] fCoefficients;   // The set of double coefficients of
										// this RealPolynomial.


	/**
	 *  Constructs a RealPolynomial having the given degree-bound (i.e., number
	 *  of coefficients).
	 *
	 *  @param degreeBound The number of coefficients of the new Polynomial
	**/

	public RealPolynomial(int degreeBound)
	{
		super(degreeBound);
	}


	/**
	 *  Constructs a RealPolynomial having the given set of coefficients.
	 *
	 *  @param coefficients The coefficients of the new RealPolynomial
	**/

	public RealPolynomial(double[] coefficients)
	{
		super(coefficients.length);

		if (fDegreeBound > 0)
		{
			fCoefficients = (double[]) coefficients.clone();
		}
		else
		{
			String detail = "RealPolynomial constructor RealPolynomial(double[]) " +
				"was passed an illegal argument: the number of coefficients " +
				"must be greater than or equal to 1.";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Constructs a RealPolynomial having the same set of coefficients as the
	 *  given RealPolynomial.
	 *
	 *  @param polynomial A RealPolynomial
	**/

	public RealPolynomial(RealPolynomial polynomial)
	{
		super(polynomial.fCoefficients.length);
		fCoefficients = (double[]) polynomial.fCoefficients.clone();
	}


	/**
	 *  Causes this RealPolynomial to initialize its coefficients to a set of
	 *  real-typed (here, double) coefficients having the given degree-bound
	 *  (i.e., number of coefficients).
	 *
	 *  @param degreeBound The size of the new set of coefficients
	**/

	protected void initializeCoefficients(int degreeBound)
	{
		fCoefficients = new double[degreeBound];
	}


	/**
	 *  Causes this RealPolynomial to initialize its coefficients to a set of
	 *  appropriately-typed coefficients having the given degree-bound (i.e.,
	 *  number of coefficients) and the same values as the given set of
	 *  coefficients.
	 *
	 *  @param Object[] A set of coefficients
	**/

	protected void initializeCoefficients(int degreeBound,
		Object[] coefficients)
	{
		if (coefficients != null)
		{
			initializeCoefficients(degreeBound);

			for (int i = coefficients.length; i >= 0; --i)
			{
				Object coefficient = coefficients[i];

				if (coefficient instanceof Number)
				{
					fCoefficients[i] = ((Number) coefficient).doubleValue();
				}
				else
				{
					String detail = "RealPolynomial.initializeCoefficients" +
						"(int, Object[]) was passed an illegal argument: the " +
						"coefficients must all be of type Number.";

					throw (new IllegalArgumentException(detail));
				}
			}
		}
	}


	/**
	 *  Returns a new array representing the given array plus enough additional
	 *  0-valued elements at the end to expand its size to the given size.
	 *
	 *  @param array An array of double
	 *  @param newSize The size to which to expand the given array
	 *  @return A new array representing the given array plus enough additional
	 *	  0-valued elements at the end to expand its size to the given size
	**/

	public static double[] expand(double[] array, int newSize)
	{
		double[] result = array;

		if (newSize > array.length)
		{
			result = new double[newSize];
			System.arraycopy(array, 0, result, 0, array.length);
		}

		return (result);
	}


	/**
	 *  Returns a new array representing the given array plus enough additional
	 *  0-valued elements at the end to expand its size to the given size.
	 *
	 *  @param array An array of float
	 *  @param newSize The size to which to expand the given array
	 *  @return A new array representing the given array plus enough additional
	 *	  0-valued elements at the end to expand its size to the given size
	**/

	public static float[] expand(float[] array, int newSize)
	{
		float[] result = array;

		if (newSize > array.length)
		{
			result = new float[newSize];
			System.arraycopy(array, 0, result, 0, array.length);
		}

		return (result);
	}


	/**
	 *  Returns a new array representing the given array plus enough additional
	 *  0-valued elements at the end to expand its size to the next highest
	 *  power of two.
	 *
	 *  @param array An array of double
	 *  @return A new array representing the given array plus enough additional
	 *	  0-valued elements at the end to expand its size to the next highest
	 *	  power of two
	**/

	public static double[] expandToNextPowerOfTwo(double[] array)
	{
		double[] result = array;

		int newSize = 1;

		while (newSize < array.length)
		{
			newSize <<= 1;

			if (newSize == 0)
			{
				String detail = "Array expansion failed";
				throw (new IllegalArgumentException(detail));
			}
		}

		newSize <<= 1;

		result = expand(array, newSize);

		return (result);
	}


	/**
	 *  Returns a new array representing the given array plus enough additional
	 *  0-valued elements at the end to expand its size to the next highest
	 *  power of two.
	 *
	 *  @param array An array of double
	 *  @return A new array representing the given array plus enough additional
	 *	  0-valued elements at the end to expand its size to the next highest
	 *	  power of two
	**/

	public static float[] expandToNextPowerOfTwo(float[] array)
	{
		float[] result = array;

		int newSize = 1;

		while (newSize < array.length)
		{
			newSize <<= 1;

			if (newSize == 0)
			{
				String detail = "Array expansion failed";
				throw (new IllegalArgumentException(detail));
			}
		}

		newSize <<= 1;

		result = expand(array, newSize);

		return (result);
	}


	/**
	 *  Causes this RealPolynomial to initialize its coefficients to a set of
	 *  appropriately-typed coefficients having the given degree-bound (i.e.,
	 *  number of coefficients) and the same values as the given set of
	 *  coefficients.
	 *
	 *  @param coefficients A set of coefficients
	**/

	protected void initializeCoefficients(int degreeBound,
		double[] coefficients)
	{
		initializeCoefficients(degreeBound);

		for (int i = coefficients.length; i >= 0; --i)
		{
			fCoefficients[i] = coefficients[i];
		}
	}


	/**
	 *  Returns the set of coefficients of this Polynomial as an array of
	 *  doubles.
	 *
	 *  @return The set of coefficients of this Polynomial
	**/

	public double[] getCoefficients()
	{
		return ((double[]) fCoefficients.clone());
	}


	/**
	 *  Returns the coefficient of this Polynomial at the given index.
	 *
	 *  <P>Note that coefficient indices start at 0, and that the highest
	 *  order coefficient of a Polynomial having n coefficients is at index
	 *  n - 1.
	 *
	 *  @return The coefficient of this Polynomial at the given index
	**/

	public double getCoefficient(int index)
	{
		return (fCoefficients[index]);
	}


	/**
	 *  Sets the coefficient of this Polynomial at the given index to the given
	 *  value.
	 *
	 *  <P>Note that coefficient indices start at 0, and that the highest
	 *  order coefficient of a Polynomial having n coefficients is at index
	 *  n - 1.
	 *
	 *  @param index The index of a target coefficient of this Polynomial
	 *  @param value The new value for the target coefficient
	**/

	public void setCoefficient(int index, Object value)
	{
		if (value != null)
		{
			if (value instanceof Number)
			{
				fCoefficients[index] = ((Number) value).doubleValue();
			}
		}
	}


	/**
	 *  Sets the coefficient of this Polynomial at the given index to the given
	 *  value.
	 *
	 *  <P>Note that coefficient indices start at 0, and that the highest
	 *  order coefficient of a Polynomial having n coefficients is at index
	 *  n - 1.
	 *
	 *  @param index The index of a target coefficient of this Polynomial
	 *  @param value The new value for the target coefficient
	**/

	public void setCoefficient(int index, double value)
	{
		fCoefficients[index] = value;
	}


	/**
	 *  Evaluates this RealPolynomial for the given value.
	 *
	 *  <P>A Polynomial having n coefficients a[0] ... a[n-1] evaluates for a
	 *  value x as:
	 *  <P>a[0] + a[1]x + a[2]x^[2] ... a[n-1]x^[n-1],
	 *
	 *  @return The evaluation of this RealPolynomial for the given value
	**/

	public double evaluate(Object value)
	{
		double result = 0;

		if ((value != null) && (value instanceof Number))
		{
			result = evaluate(((Number) value).doubleValue());
		}
		else
		{
			String detail = "RealPolynomial.evaluate(Object) was passed an " +
				"illegal argument: the value must be a Number.";

			throw (new IllegalArgumentException(detail));
		}

		return (result);
	}


	/**
	 *  Evaluates this RealPolynomial for the given value.
	 *
	 *  <P>A Polynomial having n coefficients a[0] ... a[n-1] evaluates for a
	 *  value x as:
	 *  <P>a[0] + a[1]x + a[2]x^[2] ... a[n-1]x^[n-1],
	 *
	 *  @return The evaluation of this RealPolynomial for the given value
	**/

	public double evaluate(double value)
	{
		// We evaluate using Horner's Rule:
		// A(x) = a[0] + x(a[1] + x(a[2] + ... + x(a[n-2] + x(a[n-1])) ... ))

		double result = fCoefficients[fDegreeBound - 1];

		for (int i = fDegreeBound - 2; i >= 0; i--)
		{
			result = fCoefficients[i] + (value * result);
		}

		return (result);
	}


	/**
	 *  Negates this RealPolynomial by negating each of its coefficients.
	 *
	**/

	public void negate()
	{
		for (int i = fDegreeBound - 1; i >= 0; i--)
		{
			fCoefficients[i] *= -1;
		}
	}


	/**
	 *  Subtracts the given target Polynomial to this RealPolynomial. This
	 *  RealPolynomial will grow if necessary to accomodate the subtraction.
	 *
	 *  @param target A Polynomial
	 *  @throws IllegalArgumentException if the given target Polynomial is not
	 *	  a RealPolynomial
	**/

	public void add(Polynomial target)
	{
		if ((target != null) && (target instanceof RealPolynomial))
		{
			add((RealPolynomial) target);
		}
		else
		{
			String detail = "Target Polynomial must be a RealPolynomial";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Adds the given target RealPolynomial to this RealPolynomial. This
	 *  RealPolynomial will grow if necessary to accomodate the addition.
	 *
	 *  @param target A RealPolynomial
	**/

	public void add(RealPolynomial target)
	{
		if (fDegreeBound < target.fDegreeBound)
		{
			grow(target.fDegreeBound);
		}

		for (int i = fDegreeBound - 1; i >= 0; i--)
		{
			fCoefficients[i] += target.fCoefficients[i];
		}
	}


	/**
	 *  Subtracts the given target Polynomial from this RealPolynomial. This
	 *  RealPolynomial will grow if necessary to accomodate the subtraction.
	 *
	 *  @param target A Polynomial
	 *  @throws IllegalArgumentException if the given target Polynomial is not
	 *	  a RealPolynomial
	**/

	public void subtract(Polynomial target)
	{
		if ((target != null) && (target instanceof RealPolynomial))
		{
			subtract((RealPolynomial) target);
		}
		else
		{
			String detail = "Target Polynomial must be a RealPolynomial";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Subtracts the given target Polynomial from this RealPolynomial. This
	 *  RealPolynomial will grow if necessary to accomodate the subtraction.
	 *
	 *  @param target A RealPolynomial
	**/

	public void subtract(RealPolynomial target)
	{
		if (fDegreeBound < target.fDegreeBound)
		{
			grow(target.fDegreeBound);
		}

		for (int i = fDegreeBound - 1; i >= 0; i--)
		{
			fCoefficients[i] -= target.fCoefficients[i];
		}
	}


	/**
	 *  Multiplies this RealPolynomial by the given target Polynomial. This
	 *  RealPolynomial will "grow" to have 2n - 1 coefficients to accomodate
	 *  the multiplication.
	 *
	 *  @param target A Polynomial
	 *  @throws IllegalArgumentException if the given target Polynomial is not
	 *	  a RealPolynomial
	 *  @throws IllegalArgumentException if this RealPolynomial and the given
	 *	  target Polynomial do not have the same DegreeBound (i.e., number of
	 *	  coefficients)
	**/

	public void multiply(Polynomial target)
	{
		if ((target != null) && (target instanceof RealPolynomial))
		{
			multiply((RealPolynomial) target);
		}
		else
		{
			String detail = "Target Polynomial must be a RealPolynomial";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Multiplies this RealPolynomial by the given target RealPolynomial. This
	 *  RealPolynomial will "grow" to have 2n - 1 coefficients to accomodate
	 *  the multiplication.
	 *
	 *  @param target A RealPolynomial
	 *  @throws IllegalArgumentException if this RealPolynomial and the given
	 *	  target RealPolynomial do not have the same DegreeBound (i.e.,
	 *	  number of coefficients)
	**/

	public void multiply(RealPolynomial target)
	{
		if (fDegreeBound == target.fDegreeBound)
		{
			int originalDegreeBound = fDegreeBound;

			grow((originalDegreeBound * 2) - 1);

			for (int i = originalDegreeBound - 1; i >= 0; i--)
			{
				for (int j = originalDegreeBound - 1; j >= 0; j--)
				{
					fCoefficients[i + j] += fCoefficients[i] *
						target.fCoefficients[j];
				}
			}
		}
		else
		{
			String detail = "Polynomials must have the same number of " +
				"coefficients to be muliplied together.";

			throw (new IllegalArgumentException(detail));
		}
	}
}
