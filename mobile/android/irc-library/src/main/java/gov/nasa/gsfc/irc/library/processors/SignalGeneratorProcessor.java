//=== File Prolog ============================================================
//
// $Header: /cvs/IRC/Dev/source/library/gov/nasa/gsfc/irc/library/processors/SignalGeneratorProcessor.java,v 1.22 2006/06/02 19:21:07 smaher_cvs Exp $
//
// This code was developed by Commerce One and NASA Goddard Space
// Flight Center, Code 588 for the Instrument Remote Control (IRC)
// project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jscience.physics.units.SI;

import gov.nasa.gsfc.commons.numerics.Constants;
import gov.nasa.gsfc.irc.algorithms.DefaultProcessor;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;


/**
 * A SignalGeneratorProcessor is a programmable generator of various types 
 * of uniformly sampled, time-varying signals.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/02 19:21:07 $
 * @author carlwork
 */

public class SignalGeneratorProcessor extends DefaultProcessor
{
	private static final String CLASS_NAME = 
		SignalGeneratorProcessor.class.getName();
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Signal Generator Processor";
	
	public static final String BASIS_BUFFER_NAME = "Time";
	public static final String SIN_BUFFER_NAME = "Sin(omega*t)";
	public static final String COS_BUFFER_NAME = "Cos(omega*t)";
	public static final String RANDOM_BUFFER_NAME = "Random()";
	public static final String SQUARE_BUFFER_NAME = "Square(t)";
	public static final String RAMPUP_BUFFER_NAME = "RampUp(t)";
	public static final String RAMPDOWN_BUFFER_NAME = "RampDown(t)";
	
	public static final DataBufferDescriptor BASIS_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(BASIS_BUFFER_NAME, double.class, 
			SI.MILLI(SI.SECOND));
	
	public static final DataBufferDescriptor SIN_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(SIN_BUFFER_NAME, double.class);
	
	public static final DataBufferDescriptor COS_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(COS_BUFFER_NAME, double.class);
	
	public static final DataBufferDescriptor RANDOM_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(RANDOM_BUFFER_NAME, double.class);
	
	public static final DataBufferDescriptor SQUARE_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(SQUARE_BUFFER_NAME, double.class);
	
	public static final DataBufferDescriptor RAMPUP_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(RAMPUP_BUFFER_NAME, double.class);
	
	public static final DataBufferDescriptor RAMPDOWN_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(RAMPDOWN_BUFFER_NAME, double.class);
	
	private static final int BASIS_SETS_PER_BASIS_BUNDLE = 5;
	
	private Output fOutput;
	
	private BasisBundleDescriptor fBasisBundleDescriptor;
	private int fBasisBundleCapacity;
	private BasisBundleId fBasisBundleId;
	
	private double fFrequency = 1;
	private double fAmplitude = 1;
	private double fSamplesPerSecond = 100;
	private double fCyclesPerBasisSet = 1;
	private double fOffset = 0;
	
	public static final String FREQUENCY_PROP_NAME = "frequency";
	public static final String AMPLITUDE_PROP_NAME = "amplitude";
	public static final String OFFSET_PROP_NAME = "offset";
	public static final String SAMPLE_RATE_PROP_NAME = "sampleRate";
	public static final String CYCLES_PER_BASIS_SET_PROP_NAME = 
		"cyclesPerBasisSet";
	
	private int fSamplesPerBasisSet;
	private double fSamplePeriodInMilliseconds;
	private double fMillisecondsPerBasisSet;
	
	private double fOmega;
	
	private double fDataClockInMilliseconds = 0;
	
	private BasisSet fOutputBasisSet;
	
	private SignalGenerator fSignalGenerator = new SignalGenerator();
	private Thread fSignalGeneratorThread;
		
	
	private class SignalGenerator implements Runnable
	{
		/**
		 * Pauses this SignalGeneratorThread, other than advancing its 
		 * data clock, until there is at least one listener for its data. 
		 * This avoids wasting clock cycles generating signals no one is 
		 * listening for.
		 * 
		 */
		
		private void waitForListeners()
		{
			if (fOutput.getNumListeners(fBasisBundleId) == 0)
			{
				long startOfWait = System.currentTimeMillis();
				
				declareWaiting();
				
				if (sLogger.isLoggable(Level.FINE))
				{
					String message = "Waiting for listeners to attach to data...";
					
					sLogger.logp(Level.FINE, CLASS_NAME, 
						"waitForListeners", message);
				}
				
				fOutput.waitForListeners(fBasisBundleId);
				
				if (isStarted())
				{
					long endOfWait = System.currentTimeMillis();
					
					fDataClockInMilliseconds += (endOfWait - startOfWait);
				}
			}
		}
		
		
		/**
		 * Causes this SignalGeneratorThread to generate signal data.
		 * 
		 */
		
		public void run()
		{
			fOutput = getOutput();
			
			if (fOutput != null)
			{
				if (fBasisBundleId == null)
				{
					createOutputBasisBundle();
				}
				
				fDataClockInMilliseconds = 0;
				
				waitForListeners();		
				
				fOutput.startNewBasisSequence(fBasisBundleId);
				
				long sleepTime = (int) fMillisecondsPerBasisSet;
				
				while (isStarted() && (fSamplesPerBasisSet > 0))
				{
					declareActive();
					long processingStartTime = System.currentTimeMillis();
					
					synchronized (getConfigurationChangeLock())
					{						
						fOutputBasisSet = fOutput.allocateBasisSet
							(fBasisBundleId, fSamplesPerBasisSet);
						
						// Calculate and write the appropriate basis and data 
						// values to the output BasisSet.
						
						DataBuffer basisBuffer = fOutputBasisSet.getBasisBuffer();
						
						DataBuffer sinBuffer = fOutputBasisSet.getDataBuffer
							(SIN_BUFFER_NAME);
						DataBuffer cosBuffer = fOutputBasisSet.getDataBuffer
							(COS_BUFFER_NAME);
						DataBuffer randomBuffer = fOutputBasisSet.getDataBuffer
							(RANDOM_BUFFER_NAME);
						DataBuffer squareBuffer = fOutputBasisSet.getDataBuffer
							(SQUARE_BUFFER_NAME);
						DataBuffer rampUpBuffer = fOutputBasisSet.getDataBuffer
							(RAMPUP_BUFFER_NAME);
						DataBuffer rampDownBuffer = fOutputBasisSet.getDataBuffer
							(RAMPDOWN_BUFFER_NAME);
						
						int numSamples = basisBuffer.getSize();
						
						double t = fDataClockInMilliseconds;
						double waveSize = 1000d / fFrequency;
						
						for (int i = 0; i < fSamplesPerBasisSet; i++)
						{
							basisBuffer.put(i, t);
							double omega_t = fOmega * t;
							sinBuffer.put(
								i, fAmplitude * Math.sin(omega_t) + fOffset);
							cosBuffer.put(
								i, fAmplitude * Math.cos(omega_t) + fOffset);
							randomBuffer.put(i, 
								(fAmplitude * Math.random() * 2) 
								- fAmplitude + fOffset);
							
							// Calculate square signal
							double signal = (t % waveSize) / waveSize;

							signal = (signal > 0.5) ? -1.0D : 1.0D;							
							squareBuffer.put(i, signal * fAmplitude + fOffset);

							// Calculate ramp up signal
							signal = (t % waveSize) / waveSize;
							rampUpBuffer.put(
								i, (signal * 2 - 1) * fAmplitude + fOffset);
							
							// Calculate ramp down signal
							signal = (t % waveSize) / waveSize;
							rampDownBuffer.put(
								i, (-signal * 2 + 1) * fAmplitude + fOffset);
							
							t += fSamplePeriodInMilliseconds;
						}
						
						fDataClockInMilliseconds = t;
					}
						
					long processingEndTime = System.currentTimeMillis();
					
					sleepTime = (int) fMillisecondsPerBasisSet - 
						(processingEndTime - processingStartTime);
					
					try
					{
						if (sleepTime >= 0)
						{
							Thread.sleep(sleepTime);
						}
						
						synchronized (fOutput)
						{
							fOutput.makeAvailable(fOutputBasisSet);
							
							fOutputBasisSet = null;
						}
						
						waitForListeners();
					}
					catch (InterruptedException ex)
					{
						stop();
					}	
				}
				
				if (fSamplesPerBasisSet < 1)
				{
					if (sLogger.isLoggable(Level.WARNING))
					{
						String message = "Current settings yield less than " + 
							"one sample per BasisSet:\n" + this;
						
						sLogger.logp(Level.WARNING, CLASS_NAME, 
							"start", message);
					}
				}
		
				stop();
			}
			else
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "No Output has been set for this Processor";
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"start", message);
				}
			}
		}
	}
	
	
	/**
	 * Constructs a new SignalGeneratorProcessor having a default name.
	 * 
	 */
	
	public SignalGeneratorProcessor()
	{
		this(DEFAULT_NAME, 1, 1, 100, 1);
	}
	
	
	/**
	 *	Constructs a new SignalGeneratorProcessor having the given name.
	 *
	 *	@param name The name of the new Processor
	 */
	
	public SignalGeneratorProcessor(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new SignalGeneratorProcessor configured according to the 
	 *  given ComponentDescriptor.
	 *
	 *  @param descriptor A ComponentDescriptor describing the desired 
	 * 		configuration of the new Processor
	 */
	
	public SignalGeneratorProcessor(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Constructs a new SignalGeneratorProcessor having a default name and 
	 * configured with the given set of signal parameters.
	 * 
	 * @param frequency The desired signal frequency
	 * @param amplitude The desired signal amplitude
	 * @param samplesPerSecond The desired sample rate in samples per 
	 * 		second
	 * @param cyclesPerBasisSet The number of signal cycles to include in 
	 * 		each output BasisSet
	 */
	
	public SignalGeneratorProcessor(double frequency, 
		double amplitude, double samplesPerSecond, double cyclesPerBasisSet)
	{
		this(DEFAULT_NAME, frequency, amplitude, samplesPerSecond, 
			cyclesPerBasisSet);
	}
	
	
	/**
	 * Constructs a new SignalGeneratorProcessor having the given name and 
	 * configured with the given set of signal parameters.
	 * 
	 * @param name The name of the new SignalGeneratorProcessor
	 * @param frequency The desired signal frequency
	 * @param amplitude The desired signal amplitude
	 * @param samplesPerSecond The desired sample rate in samples per 
	 * 		second
	 * @param cyclesPerBasisSet The number of signal cycles to include in 
	 * 		each output BasisSet
	 */
	
	public SignalGeneratorProcessor(String name,  
		double frequency, double amplitude, double samplesPerSecond, 
		double cyclesPerBasisSet)
	{
		this(name);
		
		fBasisBundleDescriptor = buildBasisBundleDescriptor();
		
		setFrequency(frequency);
		setAmplitude(amplitude);
		setSampleRate(samplesPerSecond);
		setCyclesPerBasisSet(cyclesPerBasisSet);
	}
	
	
	/**
	 *  Causes this Processor to process the given Dataset.
	 * 
	 *  @param dataSet A DataSet
	 */

	protected void processDataSet(DataSet dataSet)
	{
		// We don't receive input, so we don't process it.
	}
	
	
	/**
	 * Returns the BasisBundleDescriptor used by this SignalGeneratorProcessor
	 * to create its Output BasisBundle.
	 * 
	 * @return The BasisBundleDescriptor used by this SignalGeneratorProcessor
	 * 		to create its Output BasisBundle
	 */
	
	protected BasisBundleDescriptor buildBasisBundleDescriptor()
	{
		Set dataBufferDescriptors = new HashSet();
		
		dataBufferDescriptors.add(SIN_BUFFER_DESCRIPTOR);
		dataBufferDescriptors.add(COS_BUFFER_DESCRIPTOR);
		dataBufferDescriptors.add(RANDOM_BUFFER_DESCRIPTOR);
		dataBufferDescriptors.add(SQUARE_BUFFER_DESCRIPTOR);
		dataBufferDescriptors.add(RAMPUP_BUFFER_DESCRIPTOR);
		dataBufferDescriptors.add(RAMPDOWN_BUFFER_DESCRIPTOR);
		
		BasisBundleDescriptor basisBundleDescriptor = 
			new BasisBundleDescriptor
				("Signals", BASIS_BUFFER_DESCRIPTOR, dataBufferDescriptors);
		
		return (basisBundleDescriptor);
	}
	
	
	/**
	 * Creates the Output BasisBundle for this SignalGenerator's data.
	 * 
	 */
	
	protected void createOutputBasisBundle()
	{
		if (fBasisBundleId == null)
		{
			fBasisBundleId = fOutput.addBasisBundle
				(fBasisBundleDescriptor, fBasisBundleCapacity);
		
			fOutput.setUniformSampleInterval
				(fBasisBundleId, fSamplePeriodInMilliseconds);
		}
	}
	
	
	/**
	 * Updates the current set of calculated signal parameters.
	 * 
	 */
	
	private void updateParameters()
	{
		fSamplePeriodInMilliseconds = 1000 / (double) fSamplesPerSecond;
		
		fSamplesPerBasisSet = 
			(int) ((fCyclesPerBasisSet / fFrequency) * fSamplesPerSecond);
		fMillisecondsPerBasisSet = 
			fSamplesPerBasisSet * fSamplePeriodInMilliseconds;
		
		fOmega = Constants.TWO_PI * (fFrequency / 1000);
		
		int oldBasisBundleCapacity = fBasisBundleCapacity;
		
		fBasisBundleCapacity = fSamplesPerBasisSet * BASIS_SETS_PER_BASIS_BUNDLE;
		
		if (fBasisBundleId != null)
		{
			if (fBasisBundleCapacity > oldBasisBundleCapacity)
			{
				fOutput.resizeBasisBundle(fBasisBundleId, 
					fBasisBundleCapacity);
			}
			
			fOutput.setUniformSampleInterval
				(fBasisBundleId, fSamplePeriodInMilliseconds);
		}
	}
	
	
	/**
	 * Sets the frequency of the signals produced by this 
	 * SignalGeneratorProcessor to the given value.
	 * 
	 * @param frequency The desired signal frequency
	 */
	
	public void setFrequency(double frequency)
	{
		if (frequency > 0)
		{
			double oldFrequency = fFrequency;

			synchronized (getConfigurationChangeLock())
			{
				fFrequency = frequency;
					
				updateParameters();
			}
			
			firePropertyChange(FREQUENCY_PROP_NAME, 
				oldFrequency, fFrequency);
		}
		else
		{
			String message = "Frequency must be greater than 0";
			
			sLogger.logp(Level.SEVERE, CLASS_NAME, 
				"setFrequency", message);
			
			throw (new IllegalArgumentException(message));
		}
	}
	
	
	/**
	 * Returns the current frequency of the signals produced by this 
	 * SignalGeneratorProcessor.
	 * 
	 * @return The current frequency of the signals produced by this 
	 * 		SignalGeneratorProcessor
	 */
	
	public double getFrequency()
	{
		return (fFrequency);
	}
	
	
	/**
	 * Sets the amplitude of the signals produced by this 
	 * SignalGeneratorProcessor to the given value.
	 * 
	 * @param amplitude The desired signal amplitude
	 */
	
	public void setAmplitude(double amplitude)
	{
		if (amplitude > 0)
		{
			double oldAmplitude = fAmplitude;

			synchronized (getConfigurationChangeLock())
			{
				fAmplitude = amplitude;
				
				updateParameters();
			}

			firePropertyChange(AMPLITUDE_PROP_NAME, 
				oldAmplitude, fAmplitude);				
		}
		else
		{
			String message = "Amplitude must be greater than 0";
			
			sLogger.logp(Level.SEVERE, CLASS_NAME, 
				"setAmplitude", message);
			
			throw (new IllegalArgumentException(message));
		}
	}
	
	
	/**
	 * Returns the current amplitude of the signals produced by this 
	 * SignalGeneratorProcessor.
	 * 
	 * @return The current amplitude of the signals produced by this 
	 * 		SignalGeneratorProcessor
	 */
	
	public double getAmplitude()
	{
		return (fAmplitude);
	}
	
	/**
	 * Sets the offset of the signals produced by this 
	 * SignalGeneratorProcessor to the given value.
	 * 
	 * @param offset The desired signal offset
	 */
	
	public void setOffset(double offset)
	{
		double oldOffset = fOffset;

		synchronized (getConfigurationChangeLock())
		{
			fOffset = offset;
			
			updateParameters();
		}

		firePropertyChange(OFFSET_PROP_NAME, 
			oldOffset, fOffset);				
	}
	
	
	/**
	 * Returns the current offset of the signals produced by this 
	 * SignalGeneratorProcessor.
	 * 
	 * @return The current offset of the signals produced by this 
	 * 		SignalGeneratorProcessor
	 */
	
	public double getOffset()
	{
		return (fOffset);
	}
		
	/**
	 * Sets the sample rate (in samples per second) of the signals produced 
	 * by this SignalGeneratorProcessor to the given value.
	 * 
	 * @param samplesPerSecond The desired sample rate (in samples per 
	 * 		second)
	 */
	
	public void setSampleRate(double samplesPerSecond)
	{
		if (samplesPerSecond > 0)
		{
			double oldSamplesPerSecond = fSamplesPerSecond;

			synchronized (getConfigurationChangeLock())
			{				
				fSamplesPerSecond = samplesPerSecond;
				
				updateParameters();
			}
			
			firePropertyChange(SAMPLE_RATE_PROP_NAME, 
				oldSamplesPerSecond, fSamplesPerSecond);
		}
		else
		{
			String message = "Sample rate must be greater than 0";
			
			sLogger.logp(Level.SEVERE, CLASS_NAME, 
				"setSampleRate", message);
			
			throw (new IllegalArgumentException(message));
		}
	}
	
	
	/**
	 * Returns the current sample rate (in samples per second) of the 
	 * signals produced by this SignalGeneratorProcessor.
	 * 
	 * @return The current sample rate (in samples per second) of the 
	 * 		signals produced by this SignalGeneratorProcessor
	 */
	
	public double getSampleRate()
	{
		return (fSamplesPerSecond);
	}
	
	
	/**
	 * Sets the number of signal cycles to include in each BasisSet 
	 * produced by this SignalGeneratorProcessor to the given number.
	 * 
	 * @param cyclesPerBasisSet The number of signal cycles to include in 
	 * 		each BasisSet produced by this SignalGeneratorProcessor
	 */
	
	public void setCyclesPerBasisSet(double cyclesPerBasisSet)
	{
		if (cyclesPerBasisSet > 0)
		{
			double oldCyclesPerBasisSet = fCyclesPerBasisSet;

			synchronized (getConfigurationChangeLock())
			{
				fCyclesPerBasisSet = cyclesPerBasisSet;
					
				updateParameters();
			}
			
			firePropertyChange(CYCLES_PER_BASIS_SET_PROP_NAME, 
				oldCyclesPerBasisSet, fSamplesPerSecond);
		}
		else
		{
			String message = "Cycles per BasisSet must be greater than 0";
			
			sLogger.logp(Level.SEVERE, CLASS_NAME, 
				"setCyclesPerBasisSet", message);
			
			throw (new IllegalArgumentException(message));
		}
	}
	
	
	/**
	 * Returns the current number of signal cycles included in each BasisSet 
	 * produced by this SignalGeneratorProcessor.
	 * 
	 * @return The current number of signal cycles included in each BasisSet 
	 * 		produced by this SignalGeneratorProcessor
	 */
	
	public double getCyclesPerBasisSet()
	{
		return (fCyclesPerBasisSet);
	}
	
	
	// The following overriding method gives us a chance to create an Output 
	// BasisBundle even before this Processor is started, so that other 
	// Algorithms can attach to the data in the meantime.
	
	/**
	 *  Adds the given Output to the Set of Outputs associated with this 
	 *  Processor.
	 *
	 *  @param output The Output to be added to this Processor
	 */

	public void addOutput(Output output)
	{
		if (fOutput == null)
		{
			fOutput = output;
			
			createOutputBasisBundle();
		}
		
		super.addOutput(output);
	}
	
	
	/**
	 * Causes this SignalGeneratorProcessor to start.
	 * 
	 */
	
	public void start()
	{
		if (! isStarted())
		{
			super.start();
		
			if (fSignalGeneratorThread == null)
			{
				fSignalGeneratorThread = new Thread(fSignalGenerator);
			}
			
			fSignalGeneratorThread.start();
		}
	}
	
	
	/**
	 * Causes this SignalGeneratorProcessor to stop.
	 * 
	 */
	
	public void stop()
	{
		if (isStarted())
		{
			super.stop();
			
			if (! fSignalGeneratorThread.isInterrupted())
			{
				fSignalGeneratorThread.interrupt();
			}
			
			synchronized (fOutput)
			{
				if (fOutputBasisSet != null)
				{
					fOutputBasisSet.release();
				}
			}
			
			// TODO investigate how to restart an interrupted thread instead
			// of discarding the old one.
			fSignalGeneratorThread = null;
		}
	}
	
	
	/**
	 * Kills this SignalGeneratorProcessor.
	 * 
	 */
	
	public void kill()
	{
		if (! isKilled())
		{
			super.kill();
			
			fSignalGeneratorThread.interrupt();
			fSignalGeneratorThread = null;
		}
	}
	
	
	/**
	 * Returns a String representation of this SignalGeneratorProcessor.
	 * 
	 * @return A String representation of this SignalGeneratorProcessor
	 */
	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer(super.toString());
		
		stringRep.append("\nFrequency: " + fFrequency + " Hz");
		stringRep.append("\nAmplitude: " + fAmplitude);
		stringRep.append("\nSample rate: " + fSamplesPerSecond + 
			" samples/sec.");
		stringRep.append("\nOmega: " + fOmega );
		stringRep.append("\nCycles per BasisSet: " + 
				fCyclesPerBasisSet);
		stringRep.append("\nSamples per BasisSet: " + 
			fSamplesPerBasisSet);
		stringRep.append("\nSample period: " + 
				fSamplePeriodInMilliseconds + " msec.");
		stringRep.append("\nBasisSet period: " + 
			fMillisecondsPerBasisSet + " msec.");
		stringRep.append("\nBasisBundle capacity: " + 
			fBasisBundleCapacity + " samples");
		stringRep.append("\nBasisBundleId: " + fBasisBundleId);
		
		return (stringRep.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
// $Log: SignalGeneratorProcessor.java,v $
// Revision 1.22  2006/06/02 19:21:07  smaher_cvs
// Name change: UniformSampleRate->UniformSampleInterval
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
// Revision 1.18  2005/11/07 22:22:59  tames
// Added four more signal types and modified the name of all signals to be
// consistent (Cap first letter). Added an offset property.
//
// Revision 1.17  2005/07/19 18:01:33  tames_cvs
// Removed all reference to obsolete DataBufferType class.
//
// Revision 1.16  2005/07/15 19:21:35  chostetter_cvs
// Adjusted for changes in DataBuffer
//
// Revision 1.15  2005/04/06 14:59:46  chostetter_cvs
// Adjusted logging levels
//
// Revision 1.14  2005/03/17 00:23:38  chostetter_cvs
// Further DataBuffer refactoring. Any remaining calls to getDataAs_TYPE_Buffer should be changed to as_TYPE_Buffer
//
// Revision 1.13  2005/03/15 00:36:03  chostetter_cvs
// Implemented covertible Units compliments of jscience.org packages
//
// Revision 1.12  2005/02/25 04:02:06  chostetter_cvs
// See previous comment. Grrrrr.
//
// Revision 1.11  2005/02/25 01:05:46  chostetter_cvs
// REALLY really fixed start/stop behavior problem vis a vis unreleased BasisSets due to Thread interruption
//
// Revision 1.10  2004/11/15 22:02:54  tames
// Moved sleep and firing property change events out of sync blocks to minimize
// risk of thread deadlocks.
//
// Revision 1.9  2004/09/14 20:12:13  chostetter_cvs
// Units now specified as Strings in lieu of better scheme later
//
// Revision 1.8  2004/09/02 19:39:57  chostetter_cvs
// Initial data-description redesign work
//
// Revision 1.7  2004/07/22 22:15:11  chostetter_cvs
// Created Algorithm versions of library Processors
//
// Revision 1.6  2004/07/22 16:28:03  chostetter_cvs
// Various tweaks
//
// Revision 1.5  2004/07/21 14:26:15  chostetter_cvs
// Various architectural and event-passing revisions
//
// Revision 1.4  2004/07/19 23:47:50  chostetter_cvs
// Real-valued boundary conidition work
//
// Revision 1.3  2004/07/19 14:16:14  chostetter_cvs
// Added ability to subsample data in requests
//
// Revision 1.2  2004/07/18 05:14:02  chostetter_cvs
// Refactoring of data classes
//
// Revision 1.1  2004/07/17 01:25:58  chostetter_cvs
// Refactored test algorithms
//
// Revision 1.20  2004/07/16 21:35:24  chostetter_cvs
// Work on declaring uniform sample rate of data
//
// Revision 1.19  2004/07/16 15:18:31  chostetter_cvs
// Revised, refactored Component activity state
//
// Revision 1.18  2004/07/16 04:48:50  chostetter_cvs
// Fixed BasisSet period calculation error
//
// Revision 1.17  2004/07/16 00:23:20  chostetter_cvs
// Refactoring of DataSpace, Output wrt BasisBundle collections
//
// Revision 1.16  2004/07/15 19:39:42  chostetter_cvs
// Further improvements to processing timing
//
// Revision 1.15  2004/07/15 19:07:47  chostetter_cvs
// Added ability to block while waiting for a BasisBundle to have listeners
//
// Revision 1.14  2004/07/15 18:27:11  chostetter_cvs
// Add rich set of firePropertyChange event methods  to Components
//
// Revision 1.13  2004/07/15 17:48:55  chostetter_cvs
// ComponentManager, property change work
//
// Revision 1.12  2004/07/14 22:24:53  chostetter_cvs
// More Algorithm, data work. Fixed bug with slices on filtered BasisSets.
//
// Revision 1.11  2004/07/14 03:45:15  chostetter_cvs
// Added ability to detect number of data listeners, stop when goes to 0
//
// Revision 1.10  2004/07/14 03:29:21  chostetter_cvs
// Smarter Thread sleep time
//
// Revision 1.9  2004/07/14 03:09:55  chostetter_cvs
// More data, Algorithm work
//
// Revision 1.8  2004/07/14 00:33:49  chostetter_cvs
// More Algorithm, data testing. Fixed slice bug.
//
// Revision 1.7  2004/07/13 18:52:50  chostetter_cvs
// More data, Algorithm work
//
// Revision 1.6  2004/07/12 19:04:45  chostetter_cvs
// Added ability to find BasisBundleId, Components by their fully-qualified name
//
// Revision 1.5  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.4  2004/07/12 13:49:16  chostetter_cvs
// More Algorithm testing
//
// Revision 1.3  2004/07/11 21:39:03  chostetter_cvs
// AlgorithmTest tweaks
//
// Revision 1.2  2004/07/11 21:24:54  chostetter_cvs
// Organized imports
//
// Revision 1.1  2004/07/11 21:19:44  chostetter_cvs
// More Algorithm work
//
