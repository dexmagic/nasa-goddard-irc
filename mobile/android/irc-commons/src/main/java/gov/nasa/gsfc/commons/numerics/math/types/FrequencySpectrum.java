//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/math/types/FrequencySpectrum.java,v 1.5 2006/01/23 17:59:54 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: FrequencySpectrum.java,v $
//	Revision 1.5  2006/01/23 17:59:54  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.4  2005/11/14 21:15:11  chostetter_cvs
//	Merged Matt's FFT optimizations into single FFT class
//	
//	Revision 1.1  2005/03/15 17:31:09  mn2g
//	uses the fft algorithm that caches the twiddle factors
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

import gov.nasa.gsfc.commons.numerics.Constants;
import gov.nasa.gsfc.commons.numerics.math.Utilities;
import gov.nasa.gsfc.commons.numerics.math.calculations.Fft;
import gov.nasa.gsfc.commons.numerics.types.ComplexArray;
import gov.nasa.gsfc.commons.numerics.types.Range;


/**
 *  The FrequencySpectrum class applies an FFT to a given set of uniformly
 *  sampled input data and then calculates an amplitude for each resulting
 *  frequency value.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2006/01/23 17:59:54 $
 *	@author Carl Hostetter
**/

public class FrequencySpectrum implements Constants
{
	private double fCalibrationScaleValue = 0.0D;

	private ComplexArray fFftResults; // The results of applying an FFT to
										// the set of input sample values

	private double[] fAmplitudeValues;	// The (calculated) set of
											// amplitude values
	private int fNumAmplitudeValues = 0;
	private Range fFrequencyRange;	// The range of the frequency values
	private Range fAmplitudeRange;	// The range of the amplitude values
	private double fFrequencyDelta;   // The (calculated) change in frequency
										// between each amplitude value


	/**
	 *	Constructs a new FrequencySpectrum of the given set of n sample values,
	 *  which were uniformly sampled at the specified period. The resulting set
	 *  of amplitudes will have n/2 + 1 values.
	 *
	 *	@param sampleValues A set of n uniformly sampled values
	 *  @param samplingPeriod The period at which the values were sampled
	**/

	public FrequencySpectrum(double[] sampleValues, double samplingPeriod)
	{
		formFrequencySpectrum(sampleValues, samplingPeriod, 1.0D, 0.0D);
	}


	/**
	 *	Constructs a new FrequencySpectrum of the given set of n sample values,
	 *  which were uniformly sampled at the specified period. The resulting set
	 *  of amplitudes will have at most n/2 + 1 values.
	 *
	 *	@param sampleValues A set of n uniformly sampled values
	 *  @param samplingPeriod The period at which the values were sampled
	 *	@param amplitudeValues Buffer to receive the resulting (n/2 + 1)  
	 * 		amplitude values
	**/

	public FrequencySpectrum(double[] sampleValues, double samplingPeriod, 
		double[] amplitudeValues)
	{
		formFrequencySpectrum(sampleValues, samplingPeriod, amplitudeValues, 
			1.0D, 0.0D);
	}


	/**
	 *	Constructs a new FrequencySpectrum of the given set of n sample values,
	 *  which were uniformly sampled at the specified period, multiplying each
	 *  calculated amplitude value by the given output scale value. The
	 *  resulting set of amplitudes will have at most n/2 + 1 values.
	 *
	 *	@param sampleValues A set of uniformly sampled values
	 *  @param samplingPeriod The period at which the values were sampled
	 *  @param calibrationCoefficient Each output amplitude is multiplied by
	 *	  a scale value calculated based on this value
	 *  @param calibrationOffset This value is added to each output amplitude
	 *	  after the calibrationCoefficient is applied
	**/

	public FrequencySpectrum(double[] sampleValues, double samplingPeriod,
		double calibrationCoefficient, double calibrationOffset)
	{
		formFrequencySpectrum(sampleValues, samplingPeriod,
			calibrationCoefficient, calibrationOffset);
	}


	/**
	 *	Constructs a new FrequencySpectrum of the given set of n sample values,
	 *  which were uniformly sampled at the specified period, multiplying each
	 *  calculated amplitude value by the given output scale value, and 
	 *  writing the resulting amplitude values into the given buffer. The
	 *  resulting set of amplitudes will have at most n/2 + 1 values.
	 *
	 *	@param sampleValues A set of uniformly sampled values
	 *  @param samplingPeriod The period at which the values were sampled
	 *	@param amplitudeValues Buffer to receive the resulting set of 
	 * 		(at most n/2 + 1 values) amplitude values
	 *  @param calibrationCoefficient Each output amplitude is multiplied by
	 *	  a scale value calculated based on this value
	 *  @param calibrationOffset This value is added to each output amplitude
	 *	  after the calibrationCoefficient is applied
	**/

	public FrequencySpectrum(double[] sampleValues, double samplingPeriod, 
		double[] amplitudeValues, double calibrationCoefficient, 
		double calibrationOffset)
	{
		formFrequencySpectrum(sampleValues, samplingPeriod, amplitudeValues, 
			calibrationCoefficient, calibrationOffset);
	}


	/**
	 *  Returns the predicted size of the FrequencySpectrum formed from the 
	 *  given number of input samples.
	 * 
	 *  @return The predicted size of the result of applying this FFT to the 
	 *  		given number of input samples
	**/

	public static int getResultSize(int numInputSamples)
	{
		return (Fft.getResultSize(numInputSamples));
	}
	
	
	/**
	 *	Forms a new FrequencySpectrum of the given set of n sample values,
	 *  which were uniformly sampled at the specified period. The
	 *  resulting set of amplitudes will have at most n/2 + 1 values.
	 *
	 *	@param sampleValues A set of uniformly sampled values
	 *  @param samplingPeriod The period at which the values were sampled
	 *  @param calibrationCoefficient Each output amplitude is multiplied by
	 *	  a scale value calculated based on this value
	 *  @param calibrationOffset This value is added to each output amplitude
	 *	  after the calibrationCoefficient is applied
	 *  @return A set of (at most n/2 + 1) calculated amplitude values
	**/

	public double[] formFrequencySpectrum(double[] sampleValues,
		double samplingPeriod, double calibrationCoefficient,
			double calibrationOffset)
	{
		formFrequencySpectrum(sampleValues, samplingPeriod, null, 
			calibrationCoefficient, calibrationOffset);
			
		return (fAmplitudeValues);
	}


	/**
	 *	Forms a new FrequencySpectrum of the given set of n sample values,
	 *  which were uniformly sampled at the specified period, and writes the 
	 *  resulting set of at most n/2 + 1 calculated amplitude values into 
	 *  the given buffer.
	 *
	 *	@param sampleValues A set of uniformly sampled values
	 *  @param samplingPeriod The period at which the values were sampled
	 *	@param amplitudeValues Buffer to receive the resulting set of 
	 * 		(at most n/2 + 1 values) amplitude values
	 *  @param calibrationCoefficient Each output amplitude is multiplied by
	 *	  a scale value calculated based on this value
	 *  @param calibrationOffset This value is added to each output amplitude
	 *	  after the calibrationCoefficient is applied
	 *  @return The actual number of calculated amplitude values that were 
	 * 		written into the given amplitude values buffer 
	**/

	public int formFrequencySpectrum(double[] sampleValues,
		double samplingPeriod, double[] amplitudeValues, 
		double calibrationCoefficient, double calibrationOffset)
	{
		double minAmplitude = 0.0D;
		double maxAmplitude = 0.0D;

		int numUsefulSamples =
			Utilities.roundDownToPriorPowerOfTwo(sampleValues.length);

		fCalibrationScaleValue = Math.sqrt((2 * samplingPeriod) /
			((calibrationCoefficient * Math.sqrt(numUsefulSamples)) +
				calibrationOffset));

		fFftResults = Fft.applySampledFft(sampleValues);

		fNumAmplitudeValues = fFftResults.length;
		
		if (amplitudeValues == null)
		{
			fAmplitudeValues = new double[fNumAmplitudeValues];
		}
		else
		{
			fAmplitudeValues = amplitudeValues;	
		}

		for (int i = 0; i < fNumAmplitudeValues; i++)
		{
			double realPart = fFftResults.realParts[i];
			double imaginaryPart = fFftResults.imaginaryParts[i];

			double amplitude = SQRT_OF_TWO * Math.sqrt
				((realPart * realPart) + (imaginaryPart * imaginaryPart));

			amplitude *= fCalibrationScaleValue;

			fAmplitudeValues[i] = amplitude;

			if (amplitude > maxAmplitude)
			{
				maxAmplitude = amplitude;
			}
			else if (amplitude < minAmplitude)
			{
				minAmplitude = amplitude;
			}
		}

		fFrequencyDelta = 1.0D / (2 * fNumAmplitudeValues * samplingPeriod);
		fFrequencyRange = new Range(0, (fNumAmplitudeValues - 1) *
			fFrequencyDelta);
		fAmplitudeRange = new Range(minAmplitude, maxAmplitude);
		
		return (fNumAmplitudeValues);
	}
	

	/**
	 *	Returns the calibration scale value used by this FrequencySpectrum.
	 *
	 *  @return The calibration scale value used by this FrequencySpectrum
	**/

	public double getCalibrationScaleValue()
	{
		return (fCalibrationScaleValue);
	}


	/**
	 *	Returns the set of Complex values that resulted from applying the FFT
	 *  to the set of sample values.
	 *
	 *  @return The set of Complex values that resulted from applying the FFT
	 *	  to the set of sample values
	**/

	public ComplexArray getFftResults()
	{
		return (fFftResults);
	}


	/**
	 *	Returns the set of amplitude values calculated for this
	 *  FrequencySpectrum.
	 *
	 *  @return A set of corresponding amplitude values
	**/

	public double[] getAmplitudeValues()
	{
		return (fAmplitudeValues);
	}


	/**
	 *	Returns the number of amplitude values.
	 *
	 *  @return The number of amplitude values
	**/

	public int getNumAmplitudeValues()
	{
		return (fNumAmplitudeValues);
	}


	/**
	 *	Returns the Range of frequencies calculated for this FrequencySpectrum.
	 *
	 *  @return The Range of frequencies calculated for this FrequencySpectrum
	**/

	public Range getFrequencyRange()
	{
		return (fFrequencyRange);
	}


	/**
	 *	Returns the Range of amplitudes calculated for this FrequencySpectrum.
	 *
	 *  @return The Range of amplitudes calculated for this FrequencySpectrum
	**/

	public Range getAmplitudeRange()
	{
		return (fAmplitudeRange);
	}


	/**
	 *	Returns the (constant) change in frequency between each amplitude
	 *  calculated for this FrequencySpectrum.
	 *
	 *  @return The (constant) change in frequency between each amplitude
	 *	  calculated for this FrequencySpectrum
	**/

	public double getFrequencyDelta()
	{
		return (fFrequencyDelta);
	}
}
