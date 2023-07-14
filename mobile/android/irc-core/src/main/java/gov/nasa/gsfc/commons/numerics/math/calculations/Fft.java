//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/math/calculations/Fft.java,v 1.6 2006/01/23 17:59:55 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: Fft.java,v $
//	Revision 1.6  2006/01/23 17:59:55  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.5  2005/11/14 22:01:12  chostetter_cvs
//	Removed dependency on ibm array lib
//	
//	Revision 1.4  2005/11/14 21:15:11  chostetter_cvs
//	Merged Matt's FFT optimizations into single FFT class
//	
//	Revision 1.3  2005/03/16 17:45:43  mn2g
//	fixed a factor of 2
//	
//	Revision 1.2  2005/03/15 22:50:04  mn2g
//	an fft algorithm that caches the twiddle factors
//	should provide a speedup for multiple fft's of the same size
//	
//	Revision 1.1  2005/03/15 17:30:25  mn2g
//	an fft that cache's the twiddle factors
//	
//	Revision 1.3  2004/07/21 14:20:39  chostetter_cvs
//	Returns output size for given number of input samples
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

import java.util.HashMap;
import java.util.Map;

import org.jscience.mathematics.numbers.Complex;

import gov.nasa.gsfc.commons.numerics.Constants;
import gov.nasa.gsfc.commons.numerics.math.Utilities;
import gov.nasa.gsfc.commons.numerics.types.ComplexArray;


/**
 *  The Fft class contains static methods for applying the Fft and inverse Fft
 *  algorithms to real and Complex arrays.
 *
 *  <P>This code is based on that found in Scott Robert Ladd's
 *  <i>Java Algorithms</i> (McGraw Hill, 1997), pp. 62-68, with modifications
 *  (esp. in terminology) due to Thomas H. Cormen (et al.)'s
 *  <i>Introduction to Algorithms</i> (McGraw Hill, 1991), pp. 776-95.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version 	$Date: 2006/01/23 17:59:55 $
 *	@author Carl Hostetter
**/

public class Fft implements Constants
{
	protected static Map fRealFactorsByNumSamples = new HashMap();
	protected static Map fImaginaryFactorsByNumSamples = new HashMap();

	
   /**
	 *  Applies the Fast Fourier Transform to the given set of n real-valued
	 *  samples. The resulting set of n Complex values represents the Discrete
	 *  Fourier Transform (DFT) of the values.
	 *
	 *  <P>See Scott Robert Ladd's <i>Java Algorithms</i> (McGraw Hill, 1997),
	 *  pp. 62-68, and Thomas H. Cormen (et al.)'s
	 *  <i>Introduction to Algorithms</i> (McGraw Hill, 1991), pp. 791-95.
	 *
	 *  @param sampleValues A set of real-valued samples
	 *  @return A ComplexArray representing the result of the Fft
	**/

	public static ComplexArray applyCoefficientFft(double[] coefficients)
	{
		int numCoefficients = coefficients.length;
		int numBitsOfCoefficientIndices = Utilities.logBase2(numCoefficients);

		// First, put the coefficients into Complex form and in the proper
		// (bit-reversal) order to apply an efficient, iterative form of the
		// Fft algorithm.

		ComplexArray results = new ComplexArray(coefficients);
		results = results.expandToNextPowerOfTwo();
		results = results.bitReversalReorder();

		int m = 2;  // The index of the first sample value to be evaluated.
		int mDiv2 = 1;  // Half that index.

		// Now evaluate the given n coefficients at the n complex nth roots of
		// unity, i.e., at each omega[n] = e^[TWO_PI_I / n]

		double omega_real = 1.0D;
		double omega_imaginary = 0.0D;

		int kLimit = numCoefficients - 1;

		// Because we have reordered the coefficients in bit-reversal order,
		// we can halve the number of evaluations by decomposing them into
		// levels based on the order of the sample indices.

		for (int s = 0; s < numBitsOfCoefficientIndices; s++)
		{
			Complex expInput = Complex.valueOf(0, TWO_PI / m);
			Complex omegaSubM = expInput.exp();
			
			double omegaSubM_real = omegaSubM.getReal();
			double omegaSubM_imaginary = omegaSubM.getImaginary();
			
			int jLimit = mDiv2 - 1;

			for (int j = 0; j <= jLimit; j++)
			{
				for (int k = j; k <= kLimit; k += m)
				{
					int kOffset = k + mDiv2;

					double sample_real = results.realParts[kOffset];
					double sample_imaginary = results.imaginaryParts[kOffset];

					double t_real = (omega_real * sample_real) -
						(omega_imaginary * sample_imaginary);
					double t_imaginary = (omega_real * sample_imaginary) +
						(omega_imaginary * sample_real);

					double u_real = results.realParts[k];
					double u_imaginary = results.imaginaryParts[k];

					results.realParts[k] = u_real + t_real;
					results.imaginaryParts[k] = u_imaginary + t_imaginary;

					results.realParts[kOffset] = u_real - t_real;
					results.imaginaryParts[kOffset] =
						u_imaginary - t_imaginary;
				}
				
				omega_real = (omega_real * omegaSubM_real) -
					(omega_imaginary * omegaSubM_imaginary);
				omega_imaginary = (omega_real * omegaSubM_imaginary) +
					(omega_imaginary * omegaSubM_real);
			}

			m <<= 1;
			mDiv2 <<= 1;
		}

		return (results);
	}

	
   /**
	 *  Applies the Fast Fourier Transform to the given set of n real-valued
	 *  samples. The resulting set of n Complex values represents the Discrete
	 *  Fourier Transform (DFT) of the values.
	 *
	 *  @param sampleValues A set of real-valued samples
	 *  @return A ComplexArray representing the result of the Fft
	**/

	public static ComplexArray applySampledFft(double[] sampleValues)
	{
		int numValues = sampleValues.length;
		int numUsefulValues = Utilities.roundDownToPriorPowerOfTwo(numValues);
		int halfNumUsefulValues = numUsefulValues / 2;
		
		Integer numSamples = new Integer(numUsefulValues);
		
		double[] realFactors = null;
		double[] imaginaryFactors = (double[]) fImaginaryFactorsByNumSamples.
			get(numSamples);
		
		if (imaginaryFactors == null)
		{
			final int limit = numSamples.intValue() / 2;

			realFactors = new double[limit];
			imaginaryFactors = new double[limit];

			final double delta = PI/limit;
			
			for (int index=0; index<limit; index++) 
			{
				final double w = (double) delta*index;
				final double w_real = (double) Math.cos(w);
				final double w_imaginary = (double) Math.sin(w);
				
				realFactors[index]=w_real;
				imaginaryFactors[index]=w_imaginary;
			}
			
			fRealFactorsByNumSamples.put(numSamples, realFactors);
			fImaginaryFactorsByNumSamples.put(numSamples, imaginaryFactors);
		}
		else
		{
			realFactors = (double[]) fRealFactorsByNumSamples.get(numSamples);
		}

		// Resize, reorder, and scale the input sample values in preparation
		// for applying the Fft proper.

		double[] inputValues_real = sampleValues;
		double[] inputValues_imaginary = new double[numValues];

		double scale = Math.sqrt(1.0D / numUsefulValues);
		int i, j;

		for (i = j = 0; i < numUsefulValues; i++)
		{
			if (j >= i)
			{
				double temp_real = inputValues_real[j] * scale;
				double temp_imaginary = inputValues_imaginary[j] * scale;

				inputValues_real[j] = inputValues_real[i] * scale;
				inputValues_imaginary[j] = inputValues_imaginary[i] * scale;

				inputValues_real[i] = temp_real;
				inputValues_imaginary[i] = temp_imaginary;
			}

			int m = halfNumUsefulValues;

			while ((m >= 1) && (j >= m))
			{
				j -= m;
				m /= 2;
			}

			j += m;
		}

		// Now apply the sampled Fft proper.

		int mMax, iStep;
		for (mMax = 1, iStep = 2 * mMax; mMax < numUsefulValues;
			mMax = iStep, iStep = 2 * mMax)
		{

			final int factorOffset = halfNumUsefulValues/mMax;
			
			for (int m = 0; m < mMax; m++)
			{
				int factorIndex = (m*factorOffset)%halfNumUsefulValues;
				double w_real = realFactors[factorIndex];
				double w_imaginary = imaginaryFactors[factorIndex];
								
				for (i = m; i < numUsefulValues; i += iStep)
				{
					j = i + mMax;

					double t_real =
						(w_real * inputValues_real[j]) -
						(w_imaginary * inputValues_imaginary[j]);
					double t_imaginary =
						(w_real * inputValues_imaginary[j]) +
						(w_imaginary * inputValues_real[j]);

					inputValues_real[j] =
						inputValues_real[i] - t_real;
					inputValues_imaginary[j] =
						inputValues_imaginary[i] - t_imaginary;

					inputValues_real[i] += t_real;
					inputValues_imaginary[i] += t_imaginary;
				}
			}

			mMax = iStep;
		}

		int numValidResults = (numUsefulValues / 2) + 1;

		ComplexArray results = new ComplexArray
			(inputValues_real, inputValues_imaginary, numValidResults);

		return (results);
	}


	/**
	 *  Applies the inverse of the Fast Fourier Transform to the given set of
	 *  n Complex values. The resulting set of n real values represents the
	 *  inverse Discrete Fourier Transform (DFT) of the n Complex values.
	 *
	 *  <P>See Scott Robert Ladd's <i>Java Algorithms</i> (McGraw Hill, 1997),
	 *  pp. 62-68, and Thomas H. Cormen (et al.)'s
	 *  <i>Introduction to Algorithms</i> (McGraw Hill, 1991), pp. 791-95.
	 *
	 *  @param values A set of n Complex values
	 *  @return A set of n real value representing the result of the inverse
	 *	  Fft
	**/

	public static ComplexArray applyInverseCoefficientFft
		(ComplexArray inputValues)
	{
		int numValues = inputValues.length;
		int numBitsOfValueIndices = Utilities.logBase2(numValues);

		// First, put the values into the proper bit-reversal) order to apply
		// an efficient, iterative form of the inverse Fft algorithm.

		ComplexArray results = inputValues.bitReversalReorder();

		int m = 2;  // The index of the first sample value to be evaluated.
		int mDiv2 = 1;  // Half that index.

		// Now interpolate the given values at the n complex nth roots of
		// unity, i.e., at each omega[n] = e^[-TWO_PI_I / n]

		double omega_real = 1.0D;
		double omega_imaginary = 0.0D;

		int kLimit = numValues - 1;

		// Because we have reordered the values in bit-reversal order, we can
		// halve the number of interpolations by decomposing them into levels
		// based on the order of the value indices.

		// The inverse Fft is almost exactly the same as the Fft, but each
		// omega[n] is first inverted, and each resulting value is divided by
		// the number of input values.

		for (int s = 0; s < numBitsOfValueIndices; s++)
		{
			Complex expInput = Complex.valueOf(0, MINUS_TWO_PI / m);
			Complex omegaSubM = expInput.exp();
			
			double omegaSubM_real = omegaSubM.getReal();
			double omegaSubM_imaginary = omegaSubM.getImaginary();

			int jLimit = mDiv2 - 1;

			for (int j = 0; j <= jLimit; j++)
			{
				for (int k = j; k <= kLimit; k += m)
				{
					int kOffset = k + mDiv2;

					double value_real = results.realParts[kOffset];
					double value_imaginary = results.imaginaryParts[kOffset];

					double t_real = (omega_real * value_real) -
						(omega_imaginary * value_imaginary);
					double t_imaginary = (omega_real * value_imaginary) +
						(omega_imaginary * value_real);

					double u_real = results.realParts[k];
					double u_imaginary = results.imaginaryParts[k];

					results.realParts[k] = u_real + t_real;
					results.imaginaryParts[k] = u_imaginary + t_imaginary;

					results.realParts[kOffset] = u_real - t_real;
					results.imaginaryParts[kOffset] =
						u_imaginary - t_imaginary;
				}

				omega_real = (omega_real * omegaSubM_real) -
					(omega_imaginary * omegaSubM_imaginary);
				omega_imaginary = (omega_real * omegaSubM_imaginary) +
					(omega_imaginary * omegaSubM_real);
			}

			m <<= 1;
			mDiv2 <<= 1;
		}

		for (int i = 0; i < results.length; i++)
		{
			results.realParts[i] /= numValues;
			results.imaginaryParts[i] /= numValues;
		}

		return (results);
	}


	/**
	 *  Applies the inverse of the Fast Fourier Transform to the given set of
	 *  n Complex values. The resulting set of n real values represents the
	 *  inverse Discrete Fourier Transform (DFT) of the n Complex values.
	 *
	 *  @param inputValues A set of n Complex values
	 *  @return A set of n real value representing the result of the inverse
	 *	  Fft
	**/

	public static ComplexArray applyInverseSampledFft(ComplexArray inputValues)
	{
		int numValues = inputValues.length;
		int numUsefulValues = Utilities.roundDownToPriorPowerOfTwo(numValues);
		int halfNumUsefulValues = numUsefulValues / 2;

		// Resize, reorder, and scale the input values in preparation for
		// applying the Fft proper.

		double scale = Math.sqrt(1.0D / numUsefulValues);
		int i, j;

		for (i = j = 0; i < numUsefulValues; i++)
		{
			if (j >= i)
			{
				double temp_real = inputValues.realParts[j] * scale;
				double temp_imaginary = inputValues.imaginaryParts[j] * scale;

				inputValues.realParts[j] =
					inputValues.realParts[i] * scale;
				inputValues.imaginaryParts[j] =
					inputValues.imaginaryParts[i] * scale;

				inputValues.realParts[i] = temp_real;
				inputValues.imaginaryParts[i] = temp_imaginary;
			}

			int m = halfNumUsefulValues;

			while ((m >= 1) && (j >= m))
			{
				j -= m;
				m /= 2;
			}

			j += m;
		}

		// Now apply the sampled Fft proper.

		int mMax, iStep;

		for (mMax = 1, iStep = 2 * mMax; mMax < numUsefulValues;
			mMax = iStep, iStep = 2 * mMax)
		{
			final double delta = MINUS_PI / mMax;

			for (int m = 0; m < mMax; m++)
			{
				double w = (double) m * delta;
				double w_real = (double) Math.cos(w);
				double w_imaginary = (double) Math.sin(w);

				for (i = m; i < numUsefulValues; i += iStep)
				{
					j = i + mMax;

					double t_real =
						(w_real * inputValues.realParts[j]) -
						(w_imaginary * inputValues.imaginaryParts[j]);
					double t_imaginary =
						(w_real * inputValues.imaginaryParts[j]) +
						(w_imaginary * inputValues.realParts[j]);

					inputValues.realParts[j] =
						inputValues.realParts[i] - t_real;
					inputValues.imaginaryParts[j] =
						inputValues.imaginaryParts[i] - t_imaginary;

					inputValues.realParts[i] += t_real;
					inputValues.imaginaryParts[i] += t_imaginary;
				}
			}

			mMax = iStep;
		}

		int numValidResults = (numUsefulValues / 2) + 1;

		ComplexArray results = new ComplexArray(inputValues, numValidResults);

		return (results);
	}
	
	
	/**
	 *  Returns the predicted size of the result of applying this Fft to the 
	 *  given number of input samples.
	 * 
	 *  @return The predicted size of the result of applying this Fft to the 
	 *  		given number of input samples
	**/

	public static int getResultSize(int numInputSamples)
	{
		return ((Utilities.roundDownToPriorPowerOfTwo(numInputSamples) / 2) + 1);
	}
}
