//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: Coadd.java,v $
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

package gov.nasa.gsfc.commons.numerics.math.calculations;

/**
 * The Coadd Calculation calculates three outputs: a sum / mean, the root
 * mean square, and a sample count.  It is given an array of data to sum
 * with the indices to include in the sum.  Then when all the arrays have
 * been summed, it returns all three calculations in a CoaddData structure.
 *  
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:24 $
 *  @author Lynne Case, et al (prior IRC team members)
**/
public class Coadd 
{
	private double [] fSum;
	private double [] fSumOfSquares;
	private long [] fNumberOfSamples;
	private boolean   fProduceMeans = true;

	/**
	 * Create a new CoaddCalculation. 
	 * <P>
	 * @param   factor Coadd factor.
	 *
	**/
	public Coadd(int numberOfChannels)
	{
		this(numberOfChannels, true);
	}

	/**
	 * Create a new CoaddCalculation. 
	 * <P>
	 * @param   factor Coadd factor.
	 *
	**/
	public Coadd(int numberOfChannels, boolean produceMeans)
	{
		fSum = new double[numberOfChannels];
		fSumOfSquares = new double[numberOfChannels];
		fNumberOfSamples = new long[numberOfChannels];
		fProduceMeans = produceMeans;
	}

	/**
	 * initializes the variables to zero.
	 * <P>
	 * @param   factor Coadd factor.
	 *
	**/
	public void initialize()
	{
		for (int c = 0; c < fSum.length; c++)
		{
			fSum[c] = 0.0;
			fSumOfSquares[c] = 0.0;
			fNumberOfSamples[c] = 0;
		}
	}

	/**
	 * Sum the data.  This takes the source array and adds the data to
	 * the sums, the RMS, and counts the samples included. It only sums
	 * the portion of the array specified in the start and end indexes.
	 * 
	 * <P>
	 * @param   srcArray	Source array.
	 * @param   start		First index of the array to include.
	 * @param   end			Last index of the array to include.
	 * @param   channelID	Which channel are we summing
	**/
	public void sumData(double[] srcArray, int startIndex, int endIndex,
							int channelId)
	{
		for (int frame = startIndex; frame <= endIndex; frame++)
		{
			double value = srcArray[frame];
			fSum[channelId] += value;

			fSumOfSquares[channelId] += value*value;
		}

		fNumberOfSamples[channelId] += (long)(endIndex - startIndex + 1);
	}

	/**
	 * Sum the data.  This takes the source array and adds the data to
	 * the sums, the RMS, and counts the samples included. It only sums
	 * the portion of the array specified in the start and end indexes.
	 * 
	 * <P>
	 * @param   srcArray	Source array.
	 * @param   start		First index of the array to include.
	 * @param   end			Last index of the array to include.
	 * @param   channelID	Which channel are we summing
	**/
	public void sumData(float[] srcArray, int startIndex, int endIndex,
							int channelId)
	{
		for (int frame = startIndex; frame <= endIndex; frame++)
		{
			double value = srcArray[frame];
			fSum[channelId] += value;

			fSumOfSquares[channelId] += value*value;
		}

		fNumberOfSamples[channelId] += (long)(endIndex - startIndex + 1);
	}

	/**
	 * Calculate the sum / average and then return a CoaddData structure.
	 * 
	 * <P>
	 * @param   srcArray	Source array.
	 * @param   start		First index of the array to include.
	 * @param   end			Last index of the array to include.
	 * @param   channelID	Which channel are we summing
	**/
	public strictfp CoaddInfo calculate(int channelId)
	{
		double coadditionValue;

		if (fProduceMeans)
		{
			coadditionValue = fSum[channelId] / fNumberOfSamples[channelId];
		}
		else
		{
			coadditionValue = fSum[channelId];
		}

		double rmsValue = calculateRmsValue(
					fSum[channelId], fSumOfSquares[channelId], 
					fNumberOfSamples[channelId]);

		return new CoaddInfo(coadditionValue, 
							rmsValue, fNumberOfSamples[channelId]);
	}

	/**
	 *  Causes this Coadder to calculate the Root Mean Square (RMS)
	 *  for the given sum of samples, sum of the squares of the samples, and
	 *  number of samples.
	 *
	 *  @param sumOfSamples 			A sum of some set of samples of data
	 *  @param sumOfSquaresOfSamples 	The sum of the squares of the same set
	 *	  							of samples
	 *  @param numSample 				number of samples that were in the set
	**/
	protected strictfp double calculateRmsValue ( double sumOfSamples,
										double sumOfSquaresOfSamples,
										long numSamples)
	{
		double rmsValue = 0;
		if ( numSamples > 1 )
		{
			double squareOfSumOfSamples = sumOfSamples * sumOfSamples;

			rmsValue = Math.sqrt(((numSamples * sumOfSquaresOfSamples) -
				squareOfSumOfSamples) / (numSamples * (numSamples-1)));
		}
		else
		{
			rmsValue = Double.NaN;
		}

		return rmsValue;
	}

	/**
	 * Get the number of samples associated with the coadd of the 
	 * specified channel.
	 * 
	 * @param channel Channel
	 * @return long Number of sampels
	**/
	public long getSampleCount(int channel)
	{
		return fNumberOfSamples[channel];
	}
}
