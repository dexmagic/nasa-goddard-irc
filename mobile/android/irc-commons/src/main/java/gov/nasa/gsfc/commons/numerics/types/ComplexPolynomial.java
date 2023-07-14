//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/types/ComplexPolynomial.java,v 1.3 2005/11/14 22:01:12 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: ComplexPolynomial.java,v $
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


/**
 *  A ComplexPolynomial is a Polynomial (q.v.) whose coefficients are complex;
 *  that is, they have a real component and an imaginary component.
 *
 *  <P>This code is based on that found in Scott Robert Ladd's
 *  <i>Java Algoriths</i> (McGraw Hill, 1997), pp. 53-58.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2005/11/14 22:01:12 $
 *	@author Carl Hostetter
**/

public class ComplexPolynomial extends Polynomial
{
	/**
	 *  A coefficient representation of a polynomial of degree-bound n:
	 *  <P>
	 *  A(x) = SUM[j=0->n-1] a[j]x^[j]
	 *
	 * <P> is a set of coefficients a = (a[o], a[1], ..., a[n-1])
	**/

	protected ComplexArray fCoefficients;   // The set of Complex coefficients
											// of this ComplexPolynomial.


	/**
	 *  Constructs a ComplexPolynomial having the given degree-bound (i.e.,
	 *  number of coefficients).
	 *
	 *  @param degreeBound The number of coefficients of the new Polynomial
	**/

	public ComplexPolynomial(int degreeBound)
	{
		super(degreeBound);
	}


	/**
	 *  Constructs a ComplexPolynomial having the given set of coefficients.
	 *
	 *  @param coefficients The coefficients of the new ComplexPolynomial
	**/

	public ComplexPolynomial(ComplexArray coefficients)
	{
		super(coefficients.length);

		if (fDegreeBound > 0)
		{
			fCoefficients = new ComplexArray(coefficients);
		}
		else
		{
			String detail = "ComplexPolynomial constructor ComplexPolynomial(ComplexArray) " +
				"was passed an illegal argument: the number of coefficients " +
				"must be greater than or equal to 1.";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Constructs a ComplexPolynomial having the same set of coefficients as
	 *  the given ComplexPolynomial.
	 *
	 *  @param polynomial A ComplexPolynomial
	**/

	public ComplexPolynomial(ComplexPolynomial polynomial)
	{
		super(polynomial.fCoefficients.length);

		fCoefficients = new ComplexArray(polynomial.fCoefficients);
	}


	/**
	 *  Causes this ComplexPolynomial to initialize its coefficients to a set
	 *  of Complex coefficients having the given degree-bound(i.e., number of
	 *  coefficients).
	 *
	 *  @param degreeBound The size of the new set of coefficients
	**/

	protected void initializeCoefficients(int degreeBound)
	{
		fCoefficients = new ComplexArray(degreeBound);
	}


	/**
	 *  Causes this ComplexPolynomial to initialize its coefficients to a set
	 *  of appropriately-typed coefficients having the given degree-bound
	 *  (i.e., number of coefficients) and the same values as the given set of
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

				if (coefficient instanceof Complex)
				{
					fCoefficients.set(i, (Complex) coefficient);
				}
				else
				{
					String detail = "ComplexPolynomial.initializeCoefficients" +
						"(int, Object[]) was passed an illegal argument: the " +
						"coefficients must all be of type Complex.";

					throw (new IllegalArgumentException(detail));
				}
			}
		}
	}


	/**
	 *  Causes this ComplexPolynomial to initialize its coefficients to a set
	 *  of appropriately-typed coefficients having the given degree-bound
	 *  (i.e., number of coefficients) and the same values as the given set of
	 *  coefficients.
	 *
	 *  @param coefficients A set of coefficients
	**/

	protected void initializeCoefficients(int degreeBound,
		Complex[] coefficients)
	{
		initializeCoefficients(degreeBound);

		for (int i = coefficients.length; i >= 0; --i)
		{
			fCoefficients.set(i, coefficients[i]);
		}
	}


	/**
	 *  Returns the set of coefficients of this Polynomial as a ComplexArray.
	 *
	 *  @return The set of coefficients of this Polynomial
	**/

	public ComplexArray getCoefficients()
	{
		return (new ComplexArray(fCoefficients));
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

	public Complex getCoefficient(int index)
	{
		return (fCoefficients.get(index));
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
			if (value instanceof Complex)
			{
				fCoefficients.set(index, (Complex) value);
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

	public void setCoefficient(int index, Complex value)
	{
		fCoefficients.set(index, value);
	}


	/**
	 *  Evaluates this ComplexPolynomial for the given value.
	 *
	 *  <P>A Polynomial having n coefficients a[0] ... a[n-1] evaluates for a
	 *  value x as:
	 *  <P>a[0] + a[1]x + a[2]x^[2] ... a[n-1]x^[n-1],
	 *
	 *  @return The evaluation of this ComplexPolynomial for the given value
	**/

	public Complex evaluate(Object value)
	{
		Complex result = null;

		if ((value != null) && (value instanceof Complex))
		{
			result = evaluate((Complex) value);
		}
		else
		{
			String detail = "ComplexPolynomial.evaluate(Object) was passed " +
				"an illegal argument: the value must be of type Complex.";

			throw (new IllegalArgumentException(detail));
		}

		return (result);
	}


	/**
	 *  Evaluates this ComplexPolynomial for the given value.
	 *
	 *  <P>A Polynomial having n coefficients a[0] ... a[n-1] evaluates for a
	 *  value x as:
	 *  <P>a[0] + a[1]x + a[2]x^[2] ... a[n-1]x^[n-1],
	 *
	 *  @return The evaluation of this ComplexPolynomial for the given value
	**/

	public Complex evaluate(Complex value)
	{
		// We evaluate using Horner's Rule:
		// A(x) = a[0] + x(a[1] + x(a[2] + ... + x(a[n-2] + x(a[n-1])) ... ))
		
		double value_real = value.getReal();
		double value_imaginary = value.getImaginary();

		Complex result = fCoefficients.get(fDegreeBound - 1);
		
		double result_real = result.getReal();
		double result_imaginary = result.getImaginary();

		for (int i = fDegreeBound - 2; i >= 0; i--)
		{
			result_real = (result_real * value_real) - 
				(result_imaginary * value_imaginary);
			result_imaginary = (result_imaginary * value_real) + 
				(result_real * value_imaginary);

			result_real += fCoefficients.realParts[i];
			result_imaginary += fCoefficients.imaginaryParts[i];
		}
		
		result = Complex.valueOf(result_real, result_imaginary);

		return (result);
	}


	/**
	 *  Returns a RealPolynomial whose coefficients are the real parts of the
	 *  Complex coefficients of this Complex polynomial.
	 *
	 *  @return A RealPolynomial whose coefficients are the real parts of the
	 *	  Complex coefficients of this Complex polynomial
	**/

	public RealPolynomial toRealPolynomial()
	{
		RealPolynomial result = new RealPolynomial(fDegreeBound);

		System.arraycopy(fCoefficients.realParts, 0, result.fCoefficients, 0,
			fCoefficients.length);

		return (result);
	}


	/**
	 *  Negates this ComplexPolynomial by negating each of its coefficients.
	 *
	**/

	public void negate()
	{
		for (int i = fDegreeBound - 1; i >= 0; i--)
		{
			fCoefficients.realParts[i] *= -1;
			fCoefficients.imaginaryParts[i] *= -1;
		}
	}


	/**
	 *  Subtracts the given target Polynomial to this ComplexPolynomial. This
	 *  ComplexPolynomial will grow if necessary to accomodate the subtraction.
	 *
	 *  @param target A Polynomial
	 *  @throws IllegalArgumentException if the given target Polynomial is not
	 *	  a ComplexPolynomial
	**/

	public void add(Polynomial target)
	{
		if ((target != null) && (target instanceof ComplexPolynomial))
		{
			add((ComplexPolynomial) target);
		}
		else
		{
			String detail = "Target Polynomial must be a ComplexPolynomial";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Adds the given target ComplexPolynomial to this ComplexPolynomial. This
	 *  ComplexPolynomial will grow if necessary to accomodate the addition.
	 *
	 *  @param target A ComplexPolynomial
	**/

	public void add(ComplexPolynomial target)
	{
		if (fDegreeBound < target.fDegreeBound)
		{
			grow(target.fDegreeBound);
		}

		for (int i = fDegreeBound - 1; i >= 0; i--)
		{
			fCoefficients.realParts[i] += target.fCoefficients.realParts[i];
			fCoefficients.imaginaryParts[i] +=
				target.fCoefficients.imaginaryParts[i];
		}
	}


	/**
	 *  Subtracts the given target Polynomial from this ComplexPolynomial. This
	 *  ComplexPolynomial will grow if necessary to accomodate the subtraction.
	 *
	 *  @param target A Polynomial
	 *  @throws IllegalArgumentException if the given target Polynomial is not
	 *	  a ComplexPolynomial
	**/

	public void subtract(Polynomial target)
	{
		if ((target != null) && (target instanceof ComplexPolynomial))
		{
			subtract((ComplexPolynomial) target);
		}
		else
		{
			String detail = "Target Polynomial must be a ComplexPolynomial";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Subtracts the given target Polynomial from this ComplexPolynomial. This
	 *  ComplexPolynomial will grow if necessary to accomodate the subtraction.
	 *
	 *  @param target A ComplexPolynomial
	**/

	public void subtract(ComplexPolynomial target)
	{
		if (fDegreeBound < target.fDegreeBound)
		{
			grow(target.fDegreeBound);
		}

		for (int i = fDegreeBound - 1; i >= 0; i--)
		{
			fCoefficients.realParts[i] -= target.fCoefficients.realParts[i];
			fCoefficients.imaginaryParts[i] -=
				target.fCoefficients.imaginaryParts[i];
		}
	}


	/**
	 *  Multiplies this ComplexPolynomial by the given target Polynomial. This
	 *  ComplexPolynomial will "grow" to have 2n - 1 coefficients to accomodate
	 *  the multiplication.
	 *
	 *  @param target A Polynomial
	 *  @throws IllegalArgumentException if the given target Polynomial is not
	 *	  a ComplexPolynomial
	 *  @throws IllegalArgumentException if this ComplexPolynomial and the given
	 *	  target Polynomial do not have the same DegreeBound (i.e., number of
	 *	  coefficients)
	**/

	public void multiply(Polynomial target)
	{
		if ((target != null) && (target instanceof ComplexPolynomial))
		{
			multiply((ComplexPolynomial) target);
		}
		else
		{
			String detail = "Target Polynomial must be a ComplexPolynomial";

			throw (new IllegalArgumentException(detail));
		}
	}


	/**
	 *  Multiplies this ComplexPolynomial by the given target ComplexPolynomial.
	 *  This ComplexPolynomial will "grow" to have 2n - 1 coefficients to
	 *  accomodate the multiplication.
	 *
	 *  @param target A ComplexPolynomial
	 *  @throws IllegalArgumentException if this ComplexPolynomial and the given
	 *	  target ComplexPolynomial do not have the same DegreeBound (i.e.,
	 *	  number of coefficients)
	**/

	public void multiply(ComplexPolynomial target)
	{
		if (fDegreeBound == target.fDegreeBound)
		{
			int originalDegreeBound = fDegreeBound;

			grow((originalDegreeBound * 2) - 1);

			for (int i = originalDegreeBound - 1; i >= 0; i--)
			{
				for (int j = originalDegreeBound - 1; j >= 0; j--)
				{
					double this_real = fCoefficients.realParts[i];
					double this_imaginary = fCoefficients.imaginaryParts[i];

					double target_real = target.fCoefficients.realParts[j];
					double target_imaginary = target.fCoefficients.
						imaginaryParts[j];

					fCoefficients.realParts[i + j] +=
						(this_real * target_real) -
							(this_imaginary * target_imaginary);
					fCoefficients.imaginaryParts[i + j] +=
						(this_imaginary * target_real) +
							(this_real * target_imaginary);
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
