//=== File Prolog ============================================================
//
// $Header: /cvs/IRC/Dev/source/library/gov/nasa/gsfc/irc/library/processors/FrequencySpectrumProcessor.java,v 1.24 2006/05/31 13:05:22 smaher_cvs Exp $
//
// This code was developed by Commerce One and NASA Goddard Space
// Flight Center, Code 588 for the Instrument Remote Control (IRC)
// project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
// $Log: FrequencySpectrumProcessor.java,v $
// Revision 1.24  2006/05/31 13:05:22  smaher_cvs
// Name change: UniformSampleRate->UniformSampleInterval
//
// Revision 1.23  2006/05/31 12:49:50  smaher_cvs
// Removed divide by 1000 (apparently milliseconds was assumed in the basis buffer)
//
// Revision 1.22  2006/05/12 15:09:15  smaher_cvs
// Changed log message so that it doesn't output the entire basis bundle, which could be thousands of lines (instead just the name is outputted).
//
// Revision 1.21  2006/03/14 14:57:15  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.20  2006/01/23 17:59:53  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.19  2005/11/15 23:14:20  chostetter_cvs
// Fixed issue with correct mapping of input DataBuffers to output DataBuffers when input is filtered
//
// Revision 1.18  2005/11/15 18:37:39  chostetter_cvs
// Further FFT-related clean-up
//
// Revision 1.17  2005/11/14 21:15:11  chostetter_cvs
// Merged Matt's FFT optimizations into single FFT class
//
// Revision 1.16  2005/10/18 18:43:26  mn2g
// removed excess debugging messages
//
// Revision 1.15  2005/07/19 18:24:27  chostetter_cvs
// Renamed certain processors, algorithms
//
// Revision 1.7  2005/07/19 18:01:33  tames_cvs
// Removed all reference to obsolete DataBufferType class.
//
// Revision 1.6  2005/07/15 19:21:35  chostetter_cvs
// Adjusted for changes in DataBuffer
//
// Revision 1.5  2005/03/18 20:53:05  tames_cvs
// Updated to reflect DataBuffer interface changes.
//
// Revision 1.4  2005/03/17 00:24:58  chostetter_cvs
// Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
// Revision 1.3  2005/03/16 17:45:43  mn2g
// fixed a factor of 2
//
// Revision 1.2  2005/03/15 22:51:19  mn2g
// fft processor that uses code that cache's the twiddle factors for the fft
// should provide a speedup for multiple fft's of the same size
//
// Revision 1.1  2005/03/15 17:37:07  mn2g
// uses an fft package that cache's it's twiddle factors
//
// Revision 1.9  2005/03/15 00:36:02  chostetter_cvs
// Implemented covertible Units compliments of jscience.org packages
//
// Revision 1.8  2005/02/02 20:47:01  chostetter_cvs
// Revised, refined input/output BasisBundle mapping, access to management methods, and documentation of same
//
// Revision 1.7  2004/09/14 20:12:13  chostetter_cvs
// Units now specified as Strings in lieu of better scheme later
//
// Revision 1.6  2004/09/02 19:39:57  chostetter_cvs
// Initial data-description redesign work
//
// Revision 1.5  2004/07/22 22:15:11  chostetter_cvs
// Created Algorithm versions of library Processors
//
// Revision 1.4  2004/07/22 21:29:47  chostetter_cvs
// BasisBundle name, access by name changes
//
// Revision 1.3  2004/07/22 16:28:03  chostetter_cvs
// Various tweaks
//
// Revision 1.2  2004/07/21 14:26:41  chostetter_cvs
// Working version
//
// Revision 1.1  2004/07/17 01:25:58  chostetter_cvs
// Refactored test algorithms
//
// Revision 1.2  2004/07/16 21:35:24  chostetter_cvs
// Work on declaring uniform sample rate of data
//
// Revision 1.1  2004/07/14 03:09:17  chostetter_cvs
// Beginning work...
//
//
//--- Warning ----------------------------------------------------------------
//
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// *  Neither the author, their corporation, nor NASA is responsible for
//	any consequence of the use of this software.
// *  The origin of this software must not be misrepresented either by
//	explicit claim or by omission.
// *  Altered versions of this software must be plainly marked as such.
// *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.library.processors;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jscience.physics.units.SI;
import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.commons.numerics.math.types.FrequencySpectrum;
import gov.nasa.gsfc.irc.algorithms.BasisSetProcessor;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;
import gov.nasa.gsfc.irc.data.description.ModifiableBasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.ModifiableDataBufferDescriptor;


/**
 * A FrequencySpectrum produces a frequency spectrum for each 
 * uniformly-sampled BasisSet of each DataBuffer of each DataSet it 
 * receives.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/31 13:05:22 $
 * @author Carl F. Hostetter
 */

public class FrequencySpectrumProcessor extends BasisSetProcessor
{
	private static final String CLASS_NAME = 
		FrequencySpectrumProcessor.class.getName();
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Frequency Spectrum Processor";
	
	public static final DataBufferDescriptor BASIS_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor("Frequency", double.class, 
			SI.HERTZ);
	
	private FrequencySpectrum fSpectrum;
	
	
	/**
	 *	Constructs a new FrequencySpectrumProcessor having a default name.
	 *
	 *	@param name The name of the new Processor
	 */
	
	public FrequencySpectrumProcessor()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 *	Constructs a new FrequencySpectrumProcessor having the given name.
	 *
	 *	@param name The name of the new Processor
	 */
	
	public FrequencySpectrumProcessor(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new FrequencySpectrumProcessor configured according to the 
	 *  given ComponentDescriptor.
	 *
	 *  @param descriptor A ComponentDescriptor describing the desired 
	 * 		configuration of the new Processor
	 */
	
	public FrequencySpectrumProcessor(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 *  Returns the predicted size of the output BasisSets formed by 
	 *  this Processor for the given number of input samples.
	 * 
	 *  @return The predicted size of the output BasisSets formed by 
	 * 		this Processor for the given number of input samples
	**/

	protected int getOutputSize(int numInputSamples)
	{
		return (FrequencySpectrum.getResultSize(numInputSamples));
	}
	
	
	/**
	 *  Causes this Processor to build a new output BasisBundleDescriptor 
	 *  to describe the output BasisSets that it will create to hold the 
	 *  results of processing input BasisSets of the structure described 
	 *  by the given input BasisBundleDescriptor.
	 * 
	 *  <p>Here, simply returns the given input BasisBundleDescriptor.
	 * 
	 *  @param buildOutputDescriptor A BasisBundleDescriptor describing the 
	 * 		structure of a (potential) input BasisSet to this Processor
	 *  @return A BasisBundleDescriptor describing the structure of the 
	 * 		corresponding ouput BasisSet
	**/

	protected BasisBundleDescriptor buildOutputDescriptor
		(BasisBundleDescriptor inputDescriptor)
	{
		ModifiableBasisBundleDescriptor result = (ModifiableBasisBundleDescriptor)
			inputDescriptor.getModifiableCopy();
		
		result.setName("Frequency Spectra of " + inputDescriptor.getName());
		
		result.setBasisBufferDescriptor(BASIS_BUFFER_DESCRIPTOR);
		
		Iterator dataBufferDescriptors = 
			result.getDataBufferDescriptors().iterator();
		
		while (dataBufferDescriptors.hasNext())
		{
			ModifiableDataBufferDescriptor descriptor = 
				(ModifiableDataBufferDescriptor) 
					dataBufferDescriptors.next();
			
			descriptor.setDataType(double.class);
			descriptor.setUnit(Unit.ONE);
		}
		
		return (result);
	}
	
	
	/**
	 * Causes this FrequencySpectrumProcessor to process the given 
	 * uniformly-sampled BasisSet.
	 * 
	 * @param BasisSet A uniformly-sampled BasisSet
	 */
	
	protected void processBasisSet(BasisSet basisSet)
	{
		if (basisSet.isUniformlySampled())
		{
			Output output = getOutput();
			
			int numInputSamples = basisSet.getSize();
			
			// Old assumption of mS in basis buffer ..
//			double inputSampleRate = 
//				basisSet.getUniformSampleRate() / 1000;
			
			double inputSampleRate = 
				basisSet.getUniformSampleInterval();			
			
			BasisBundleId inputId = basisSet.getBasisBundleId();
			
			BasisBundleId outputId = getAssociatedOutputBasisBundleId(inputId);
			
			int outputSize = FrequencySpectrum.getResultSize(numInputSamples);
			
			BasisSet results = output.allocateBasisSet(outputId, outputSize);
			
			Iterator inputBuffers = basisSet.getDataBuffers();
			
			boolean firstResult = true;
			
			while (inputBuffers.hasNext())
			{
				DataBuffer inputBuffer = (DataBuffer) inputBuffers.next();
				String inputBufferName = inputBuffer.getName();
				
				double[] inputSamples = inputBuffer.getAsDoubleArray();
				
				if (fSpectrum==null) 
				{
					fSpectrum = new FrequencySpectrum(inputSamples, inputSampleRate, 
						1.0, 0.0);
				} 
				else 
				{
					fSpectrum.formFrequencySpectrum(inputSamples, inputSampleRate, 
						1.0, 0.0);
				}
				
				double[] amplitudes = fSpectrum.getAmplitudeValues();
				
				if (firstResult)
				{
					double frequency = fSpectrum.getFrequencyRange().getMin();
					double frequencyDelta = fSpectrum.getFrequencyDelta();
					
					results.setUniformSampleInterval(frequencyDelta);
					
					DataBuffer frequencies = results.getBasisBuffer();
					
					for (int i = 0; i < amplitudes.length; i++)
					{
						frequencies.put(i, frequency);
						frequency += frequencyDelta;
					}
					
					firstResult = false;
				}
				
				String outputBufferName = 
					getOutputDataBufferName(inputBufferName, inputId);
				
				DataBuffer outputBuffer = results.getDataBuffer(outputBufferName);
				
				for (int i = 0; i < amplitudes.length; i++)
				{
					outputBuffer.put(i, amplitudes[i]);
				}
			}
			
			output.startNewBasisSequence(outputId);
			output.makeAvailable(results);
		}
		else
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Skipping non-uniformly-sampled BasisSet: " + basisSet.getName();
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"processBasisSet", message);
			}
		}
	}
}

