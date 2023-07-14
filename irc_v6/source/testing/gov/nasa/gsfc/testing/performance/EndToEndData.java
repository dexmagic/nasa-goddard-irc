//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.testing.performance;

import gov.nasa.gsfc.commons.numerics.types.Amount;
import gov.nasa.gsfc.irc.algorithms.BasisSetProcessor;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.DefaultOutput;
import gov.nasa.gsfc.irc.algorithms.DefaultProcessor;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.algorithms.Processor;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jscience.physics.units.SI;

/**
 * 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/20 16:01:25 $
 * @author	tames
 **/
public class EndToEndData
{
	private int fSamplesPerBasisSet = 100;  // Size of allocations by the writer
	private int fBasisBundleCapacity = 2000; // Size of BasisBundle written to
	private int fRequestAmount = 100;		// Size of readers basis request
	private int fDataBuffersPerBasisBundle = 1000; // # buffers in every BasisSet
	private int fNumberOfConsumers = 1;		// Number of readers (and Inputs)
	private boolean fArrayCopyMethod = false; // Read by aBuffer[j] vs aBuffer.getAsDouble(j)

	/**
	 * 
	 */
	public EndToEndData()
	{
		super();
	}
	
	public void start()
	{
		DataSpace dataSpace = Irc.getDataSpace();

		// Create writer
		Processor dataWriter = this.new DataWriter();	
		Output output = new DefaultOutput("Data Writer Output");			
		dataWriter.addOutput(output);
		Irc.getComponentManager().addComponent(output);
		Irc.getComponentManager().addComponent(dataWriter);

		Set basisBundleNames = dataSpace.getBasisBundleNames();
		System.out.println("BBNames: " + basisBundleNames);
		
		// Create readers
		for (int i=0; i < fNumberOfConsumers; i++)
		{
			Processor dataReader = this.new DataReader();				
			Input input = new DefaultInput();
			input.addInputListener(dataReader);
			
			BasisBundleId basisBundleId = 
				dataSpace.getBasisBundleId("Signals.Data Writer Output.IRC");
		
			Amount requestAmount = new Amount();
			requestAmount.setAmount(fRequestAmount);
			BasisRequest basisRequest = 
				new BasisRequest(basisBundleId, requestAmount);
		
			input.addBasisRequest(basisRequest);
			
			Irc.getComponentManager().addComponent(input);
			Irc.getComponentManager().addComponent(dataReader);
		}

		System.out.println(Irc.getComponentManager().toString());
		//System.out.println(dataSpace.toString());

		System.out.println("Starting Algorithms...\n");		
		Irc.getComponentManager().startAllComponents();
	}

	public void stop()
	{
		System.out.println("Stopping Algorithms...\n");		
		Irc.getComponentManager().stopAllComponents();
		Irc.getDataSpace().clear();
		
		System.out.println(Irc.getComponentManager().toString());
		System.out.println(Irc.getDataSpace().toString());
	}

	public static void main(String[] args)
	{
		Irc.main(args);
		new EndToEndData().start();
	}
	
	public class DataReader extends BasisSetProcessor
	{
		protected long fStartTime = 0;
		protected long fCurrentTime = 0;
		protected long fLastTime = 0;
		protected boolean fTimeInitialized = false;
		protected long fSamplesReceived = 0;
		protected long fSamplesReceivedInterval = 0;
		
		public DataReader()
		{
			super("Data Reader Processor");
		}
		
		/**
		 * Causes this processor to process the given 
		 * BasisSet.
		 * 
		 * @param basisSet A BasisSet
		 */
		protected void processBasisSet(BasisSet basisSet)
		{
			if (!fTimeInitialized)
			{
				fTimeInitialized = true;
				fStartTime = System.currentTimeMillis();
				fLastTime = fStartTime;
			}
			else
			{		
				DataBuffer basisBuffer = basisSet.getBasisBuffer();
				int samples = basisBuffer.getSize();
				fSamplesReceived += samples;
				fSamplesReceivedInterval += samples;
				visitEachDataElement(basisSet);

				if ((fSamplesReceived % 10000) == 0)
				{
					fCurrentTime = System.currentTimeMillis();
					long deltaLast = fCurrentTime - fLastTime;
					long deltaOverall = fCurrentTime - fStartTime;
					System.out.println("Samples: " + fSamplesReceived 
						+ " Rate: " + ((double) fSamplesReceivedInterval/deltaLast * 1000.0d) 
						+ " Overall: " + ((double) fSamplesReceived/deltaOverall * 1000.0d) 
						+ " samples ps");

					fLastTime = fCurrentTime;
					fSamplesReceivedInterval = 0;
				}				
			}
		}


		protected double fTotal = 0.0d;
		
		protected void visitEachDataElement(BasisSet basisSet)
		{
			fTotal = 0.0d;
			
			for (Iterator buffers = basisSet.getDataBuffers(); buffers.hasNext();)
			{
				if (fArrayCopyMethod)
				{
					double[] aBuffer = ((DataBuffer) buffers.next()).getAsDoubleArray();
					int size = aBuffer.length;
					
					for (int j = 0; j < size; j++)
					{
						fTotal += aBuffer[j];
					}
				}
				else
				{
					DataBuffer aBuffer = (DataBuffer) buffers.next();
					int size = aBuffer.getSize();
					
					for (int j = 0; j < size; j++)
					{
						fTotal += aBuffer.getAsDouble(j);
					}
				}
			}
		}	
	}
	
	public class DataWriter extends DefaultProcessor
	{
		public final String BASIS_BUFFER_NAME = "time";
		public final String DATA_BUFFER_NAME = "data";
		public final DataBufferDescriptor BASIS_BUFFER_DESCRIPTOR = 
			new DataBufferDescriptor(BASIS_BUFFER_NAME, double.class, 
				SI.MILLI(SI.SECOND));
		
		public final DataBufferDescriptor DATA_BUFFER_DESCRIPTOR = 
			new DataBufferDescriptor(DATA_BUFFER_NAME, double.class);

		private DataGenerator fDataGenerator = new DataGenerator();
		private Thread fDataGeneratorThread;
		private Output fOutput;
		private BasisSet fOutputBasisSet;
		
		private BasisBundleDescriptor fBasisBundleDescriptor;
		private BasisBundleId fBasisBundleId;
			
		public DataWriter()
		{
			super("Data Writer Processor");
			
			fBasisBundleDescriptor = buildBasisBundleDescriptor();
		}
		
		/**
		 * Causes this Processor to process the given Dataset.
		 * 
		 * @param dataSet A DataSet
		 */
		protected void processDataSet(DataSet dataSet)
		{
			// We don't receive input, so we don't process it.
		}
				
		/**
		 * Causes this Processor to start.
		 */		
		public void start()
		{
			if (! isStarted())
			{
				super.start();
			
				if (fDataGeneratorThread == null)
				{
					fDataGeneratorThread = new Thread(fDataGenerator);
				}
				
				fDataGeneratorThread.start();
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
				
				if (! fDataGeneratorThread.isInterrupted())
				{
					fDataGeneratorThread.interrupt();
				}
				
				synchronized (fOutput)
				{
					if (fOutputBasisSet != null)
					{
						fOutputBasisSet.release();
					}
				}
				
				fDataGeneratorThread = null;
			}
		}
		
		/**
		 * Kills this Processor.
		 */		
		public void kill()
		{
			if (! isKilled())
			{
				super.kill();
				
				fDataGeneratorThread.interrupt();
				fDataGeneratorThread = null;
			}
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
		 * Creates the Output BasisBundle for this generator's data.
		 */		
		protected void createOutputBasisBundle()
		{
			if (fBasisBundleId == null)
			{
				fBasisBundleId = fOutput.addBasisBundle
					(fBasisBundleDescriptor, fBasisBundleCapacity);
			}
		}

		/**
		 * Returns the BasisBundleDescriptor used by this Processor
		 * to create its Output BasisBundle.
		 * 
		 * @return The BasisBundleDescriptor used by this Processor
		 * 		to create its Output BasisBundle
		 */		
		protected BasisBundleDescriptor buildBasisBundleDescriptor()
		{
			Set dataBufferDescriptors = new HashSet();
			
			for (int i = 0; i < fDataBuffersPerBasisBundle; i++)
			{
				dataBufferDescriptors.add(
						new DataBufferDescriptor(
								DATA_BUFFER_NAME + i, double.class));
			}
			
			BasisBundleDescriptor basisBundleDescriptor = 
				new BasisBundleDescriptor
					("Signals", BASIS_BUFFER_DESCRIPTOR, dataBufferDescriptors);
			
			return (basisBundleDescriptor);
		}

		private class DataGenerator implements Runnable
		{
			/**
			 * Causes this DataGeneratorThread to generate data.
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
					
					fOutput.resizeBasisBundle(fBasisBundleId, 
							fBasisBundleCapacity);
					
					fOutput.startNewBasisSequence(fBasisBundleId);
					
					declareActive();
					
					while (isStarted() && (fSamplesPerBasisSet > 0))
					{
						synchronized (getConfigurationChangeLock())
						{						
							fOutputBasisSet = fOutput.allocateBasisSet
								(fBasisBundleId, fSamplesPerBasisSet);
							
							// Calculate and write the appropriate basis and data 
							// values to the output BasisSet.
							
							DataBuffer basisBuffer = fOutputBasisSet.getBasisBuffer();
							
							for (int i = 0; i < fSamplesPerBasisSet; i++)
							{
								basisBuffer.put(i, i);
							}

							for (Iterator buffers = 
								fOutputBasisSet.getDataBuffers(); 
								buffers.hasNext();)
							{
								DataBuffer dataBuffer = (DataBuffer) buffers.next();
//								double [] array = (double []) dataBuffer.array();
//								int size = dataBuffer.getSize();
//								int sampleIndex = dataBuffer.arrayOffset();
//								
//								for (int j = 0; j < size; j++)
//								{
//									array[sampleIndex] = 1.0d;
//									sampleIndex++;
//									
//									if (sampleIndex >= fBasisBundleCapacity)
//									{
//										sampleIndex = 0;
//									}
//								}
								for (int j = 0; j < fSamplesPerBasisSet; j++)
								{
									dataBuffer.put(j, 1.0d);
								}
							}
						}
							
						synchronized (fOutput)
						{
							fOutput.makeAvailable(fOutputBasisSet);
							
							fOutputBasisSet = null;
						}
					}
				}
			}
		}
		
		/**
		 * @return Returns the basisBundleCapacity.
		 */
		public int getBasisBundleCapacity()
		{
			return fBasisBundleCapacity;
		}
		
		/**
		 * @param basisBundleCapacity The basisBundleCapacity to set.
		 */
		public void setBasisBundleCapacity(int basisBundleCapacity)
		{
			fBasisBundleCapacity = basisBundleCapacity;
		}
		
		/**
		 * @return Returns the samplesPerBasisSet.
		 */
		public int getSamplesPerBasisSet()
		{
			return fSamplesPerBasisSet;
		}
		
		/**
		 * @param samplesPerBasisSet The samplesPerBasisSet to set.
		 */
		public void setSamplesPerBasisSet(int samplesPerBasisSet)
		{
			fSamplesPerBasisSet = samplesPerBasisSet;
		}
	}
	
	/**
	 * @return Returns the basisBundleCapacity.
	 */
	public int getBasisBundleCapacity()
	{
		return fBasisBundleCapacity;
	}
	
	/**
	 * @param basisBundleCapacity The basisBundleCapacity to set.
	 */
	public void setBasisBundleCapacity(int basisBundleCapacity)
	{
		fBasisBundleCapacity = basisBundleCapacity;
	}
	
	/**
	 * @return Returns the dataBuffersPerBasisBundle.
	 */
	public int getDataBuffersPerBasisBundle()
	{
		return fDataBuffersPerBasisBundle;
	}
	
	/**
	 * @param dataBuffersPerBasisBundle The dataBuffersPerBasisBundle to set.
	 */
	public void setDataBuffersPerBasisBundle(int dataBuffersPerBasisBundle)
	{
		fDataBuffersPerBasisBundle = dataBuffersPerBasisBundle;
	}
	
	/**
	 * @return Returns the requestAmount.
	 */
	public int getRequestAmount()
	{
		return fRequestAmount;
	}
	
	/**
	 * @param requestAmount The requestAmount to set.
	 */
	public void setRequestAmount(int requestAmount)
	{
		fRequestAmount = requestAmount;
	}
	
	/**
	 * @return Returns the samplesPerBasisSet.
	 */
	public int getSamplesPerBasisSet()
	{
		return fSamplesPerBasisSet;
	}
	
	/**
	 * @param samplesPerBasisSet The samplesPerBasisSet to set.
	 */
	public void setSamplesPerBasisSet(int samplesPerBasisSet)
	{
		fSamplesPerBasisSet = samplesPerBasisSet;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: EndToEndData.java,v $
//  Revision 1.13  2006/03/20 16:01:25  chostetter_cvs
//  Fixed BasisBundle name in BasisRequest
//
//  Revision 1.12  2006/03/16 14:46:05  smaher_cvs
//  Changed namespace root in basis bundle.
//
//  Revision 1.11  2006/03/14 22:53:08  tames_cvs
//  Removed a reference to a depricated class.
//
//  Revision 1.10  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/10/07 16:03:36  tames_cvs
//  Added some comments.
//
//  Revision 1.8  2005/10/07 15:39:08  tames_cvs
//  Changed default buffers per bundle to 1000
//
//  Revision 1.7  2005/07/20 15:03:37  tames_cvs
//  Added some testing code (now commented out) for comparing
//  DataBuffer put methods agains writing directly to the underlying
//  array.
//
//  Revision 1.6  2005/07/19 18:03:17  tames_cvs
//  Removed all reference to obsolete DataBufferType class.
//
//  Revision 1.5  2005/07/18 21:17:13  tames_cvs
//  Added a flag and code to test performance of getting the received data
//  as an array.
//
//  Revision 1.4  2005/07/14 21:47:23  tames
//  Updated to reflect data model changes.
//
//  Revision 1.3  2005/05/17 14:33:44  tames
//  Added get and set methods.
//
//  Revision 1.2  2005/05/13 21:34:07  tames_cvs
//  *** empty log message ***
//
//  Revision 1.1  2005/05/11 20:14:08  tames_cvs
//  Initial version
//
//