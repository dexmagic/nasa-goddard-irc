//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/math/types/FftPolynomial.java,v 1.3 2005/11/14 22:01:12 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: FftPolynomial.java,v $
//	Revision 1.3  2005/11/14 22:01:12  chostetter_cvs
//	Removed dependency on ibm array lib
//	
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

package gov.nasa.gsfc.commons.numerics.math.types;

import org.jscience.mathematics.numbers.Complex;

import gov.nasa.gsfc.commons.numerics.math.calculations.Fft;
import gov.nasa.gsfc.commons.numerics.types.ComplexArray;
import gov.nasa.gsfc.commons.numerics.types.ComplexPolynomial;
import gov.nasa.gsfc.commons.numerics.types.RealPolynomial;


/**
 *  An FftPolynomial is a ComplexPolynomial (q.v.) that represents the results
 *  of applying the Fast Fourier Transform to the RealPolynomial (q.v.) based
 *  upon which it was constructed.
 *
 *  <P>This code is based on that found in Scott Robert Ladd's
 *  <i>Java Algorithms</i> (McGraw Hill, 1997), pp. 62-68, with modifications
 *  (esp. in terminology) due to Thomas H. Cormen (et al.)'s
 *  <i>Introduction to Algorithms</i> (McGraw Hill, 1991), pp. 776-95.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2005/11/14 22:01:12 $
 *	@author Carl Hostetter
**/

public class FftPolynomial extends ComplexPolynomial
{
	protected double[] fOriginalRealCoefficients;


	/**
	 *  Constructs an FftPolynomial by applying the FFT to the given set of
	 *  real coefficients.
	 *
	 *  @param coefficients A set of real coefficients
	**/

	public FftPolynomial(double[] coefficients)
	{
		super(coefficients.length);

		fOriginalRealCoefficients = coefficients;

		ComplexArray fftOfCoefficients = Fft.applyCoefficientFft(coefficients);

		for (int i = coefficients.length - 1; i >= 0; i--)
		{
			Complex fftValue = fftOfCoefficients.get(i);
			setCoefficient(i, fftValue);
		}
	}


	/**
	 *  Constructs an FftPolynomial by applying the FFT to the coefficients of
	 *  the given RealPolynomial.
	 *
	 *  @param polynomial RealPolynomial
	**/

	public FftPolynomial(RealPolynomial polynomial)
	{
		this(polynomial.getCoefficients());
	}


	/**
	 *  Returns the set of real coefficients with which this FftPolynomial was
	 *  originally constructed.
	 *
	 *  @return The Set of real coefficients with which this FftPolynomial was
	 *	  originally constructed
	**/

	public double[] getOriginalRealCoefficients()
	{
		return ((double[]) fOriginalRealCoefficients.clone());
	}


	/**
	 *  Multiplies this FftPolynomial by the given target FftPolynomial, and
	 *  returns the results as a new RealPolynomial.
	 *
	 *  <P>See Scott Robert Ladd's <i>Java Algorithms</i> (McGraw Hill, 1997),
	 *  pp. 67-68, and Thomas H. Cormen (et al.)'s
	 *  <i>Introduction to Algorithms</i> (McGraw Hill, 1991), pp. 781-82, 790.
	 *
	 *  @param target An FftPolynomial
	 *  @return A new RealPolynomial representing the results of the
	 *	  multiplication
	**/

	public RealPolynomial multiply(FftPolynomial target)
	{
		RealPolynomial result;

		// First, make sure the set of coefficients of the two FftPolynomials
		// are of the same size, expanding one or both as needed to the next
		// highest power of two to ensure this.

		ComplexArray myFftValues = this.getCoefficients();
		ComplexArray targetFftValues = target.getCoefficients();

		int myDegreeBound = this.getDegreeBound();
		int targetDegreeBound = target.getDegreeBound();
		int sharedDegreeBound = 0;

		if (myDegreeBound > targetDegreeBound)
		{
			myFftValues = myFftValues.expandToNextPowerOfTwo();
			sharedDegreeBound = myFftValues.length;
			targetFftValues = targetFftValues.expand(sharedDegreeBound);
		}

		// Because the two Polynomials concerned are in FFT form and of the
		// same (power-of-two) size, we can, by the Convolution theorem for
		// polynomials, mulitply them simply by multiplying their coefficients
		// and then applying the inverse FFT to the results.

		ComplexArray multipliedValues = myFftValues.multiply(targetFftValues);

		// Now create the polynomial coefficient representation of the result
		// of the multiplication by applying the inverse FFT to it.

		ComplexArray polynomialCoefficients =
			Fft.applyInverseCoefficientFft(multipliedValues);

		result = new RealPolynomial(polynomialCoefficients.realParts);

		return (result);
	}
}
