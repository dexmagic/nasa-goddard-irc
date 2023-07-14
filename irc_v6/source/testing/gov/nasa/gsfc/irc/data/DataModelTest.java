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

package gov.nasa.gsfc.irc.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jscience.physics.units.SI;

import gov.nasa.gsfc.irc.algorithms.BasisSetProcessor;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.DefaultOutput;
import gov.nasa.gsfc.irc.algorithms.DefaultProcessor;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.Output;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;

/**
 * JUnit test for the Data Model framework that test data integrity end to end
 * from a source writing data to a BasisBundle to a consumer of the BasisBundle.
 * This test case compares the data received with the data actually written.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/01/23 17:59:55 $
 * @author Troy Ames
 */
public class DataModelTest extends TestCase
{	
	// Components
	private Input fReaderInput = null;
	private Output fWriterOutput = null;
	private DataWriter fDataWriter = null;
	private DataReader fDataReader = null;
	private int fTestDuration = 10000;
	
	/**
	 * Default constructor for the test
	 */
	public DataModelTest()
	{
		super();
	}
	
	/**
	 * Constructs the specified test.
	 * 
	 * @param name the test method name
	 */
	public DataModelTest(String name)
	{
		super(name);
	}
	
	/**
	 * Set up for test cases defined in this class.
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		//System.out.println("DataModelTest setUp");
		// Create writer
		fDataWriter = new DataWriter();	
		fWriterOutput = new DefaultOutput("Data Writer Output");			
		fDataWriter.addOutput(fWriterOutput);

		// Create reader
		fDataReader = new DataReader();				
		fReaderInput = new DefaultInput();
		fReaderInput.addInputListener(fDataReader);
	}

	/**
	 * Tear down for test cases defined in this class.
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
		//System.out.println("DataModelTest tearDown");
		
		// Stop components
		fDataWriter.stop();
		fWriterOutput.stop();
		fReaderInput.stop();
		fDataReader.stop();

		Irc.getDataSpace().clear();
	}

	/**
	 * This test case compares the data received with the data written for the
	 * simplest case when BasisSet size is a even multiple of both the bundle
	 * size and the consumer request size.
	 */
	public void testDataIntegrityUniformSizes()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				5, 		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				10, 	// Data consumer request amount in samples
				false,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when the producer BasisSet size is equal to the consumer request
	 * size.
	 */
	public void testDataIntegrityEqualSizes()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				5,		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				5,		// Data consumer request amount in samples
				false,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when producer BasisSet size is not an even multiple of the bundle
	 * size.
	 */
	public void testDataIntegrityOddProducerSizes()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				3, 		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				6, 		// Data consumer request amount in samples
				false,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when producer BasisSet size is not an even multiple of the consumer
	 * request size.
	 */
	public void testDataIntegrityOddConsumerSizes()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				5, 		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				7, 		// Data consumer request amount in samples
				false,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when producer BasisSet size is larger, but can be evenly divisible
	 * by the consumer request size.
	 */
	public void testDataIntegritySmallUniformConsumerSizes()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				4, 		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				2, 		// Data consumer request amount in samples
				false,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when producer BasisSet size is larger and not evenly divisible by
	 * the consumer request size.
	 */
	public void testDataIntegritySmallOddConsumerSizes()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				4, 		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				3, 		// Data consumer request amount in samples
				false,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when BasisSet size is a even multiple of both the bundle
	 * size and the consumer request size while the Basis Bundle is being resized.
	 */
	public void testDataIntegrityUniformSizesWithResize()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				5, 		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				10, 	// Data consumer request amount in samples
				true,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when the producer BasisSet size is equal to the consumer request
	 * size while the Basis Bundle is being resized.
	 */
	public void testDataIntegrityEqualSizesWithResize()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				5,		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				5,		// Data consumer request amount in samples
				true,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when producer BasisSet size is not an even multiple of the bundle
	 * size while the Basis Bundle is being resized.
	 */
	public void testDataIntegrityOddProducerSizesWithResize()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				3, 		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				6, 		// Data consumer request amount in samples
				true,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when producer BasisSet size is not an even multiple of the consumer
	 * request size while the Basis Bundle is being resized.
	 */
	public void testDataIntegrityOddConsumerSizesWithResize()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				5, 		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				7, 		// Data consumer request amount in samples
				true,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when producer BasisSet size is larger, but can be evenly divisible
	 * by the consumer request size while the Basis Bundle is being resized.
	 */
	public void testDataIntegritySmallUniformConsumerSizesWithResize()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				4, 		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				2, 		// Data consumer request amount in samples
				true,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * This test case compares the data received with the data written for the
	 * case when producer BasisSet size is larger and not evenly divisible by
	 * the consumer request size while the Basis Bundle is being resized.
	 */
	public void testDataIntegritySmallOddConsumerSizesWithResize()
	{
		// Setup and run test		
		int samplesTested = 
			dataIntegrityUseCase(
				4, 		// Samples per basis set written by data producer
				20, 	// Basis bundle capacity
				3, 		// Data consumer request amount in samples
				true,	// Test resizing BasisBundle
				fTestDuration);	// Test duration in samples
		
		// Check if test processed the expected number of samples. 
		assertTrue("Test duration reached", samplesTested >= fTestDuration);		
	}

	/**
	 * Test data integrity end to end from a source writing data to a
	 * BasisBundle to a consumer of the BasisBundle. This use case compares
	 * the data received with the data written using the specified configuration.
	 * 
	 * @param samplesPerBasisSet samples per BasisSet written by data producer
	 * @param basisBundleCapacity capacity of BasisBundle
	 * @param consumerRequestAmount consumer data request amount in samples
	 * @param samplesToTest test duration in samples
	 * @return the actual number of samples tested
	 */
	protected int dataIntegrityUseCase(
			int samplesPerBasisSet,
			int basisBundleCapacity, 
			int consumerRequestAmount,
			boolean resizeBasisBundle,
			int samplesToTest)
	{
		//Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		DataSpace dataSpace = Irc.getDataSpace();
		int dataBuffersPerBasisBundle = 1;

		// Setup test conditions
		
		// Setup Writer
		fDataWriter.setSamplesPerBasisSet(samplesPerBasisSet);
		fDataWriter.setBasisBundleCapacity(basisBundleCapacity);
		fDataWriter.setDataBuffersPerBasisBundle(dataBuffersPerBasisBundle);
		fDataWriter.setResizeBasisBundleFlag(resizeBasisBundle);
		fDataWriter.createOutputBasisBundle();
		
		// Setup Reader input
		fDataReader.setTestDuration(samplesToTest);

		BasisBundleId basisBundleId = 
			dataSpace.getBasisBundleId("Signals.Data Writer Output.IRC");
	
		BasisRequestAmount basisRequestAmount = new BasisRequestAmount();
		basisRequestAmount.setAmount(consumerRequestAmount);
		BasisRequest basisRequest = 
			new BasisRequest(basisBundleId, basisRequestAmount);
	
		fReaderInput.addBasisRequest(basisRequest);

		// Start components
		fDataReader.start();
		fReaderInput.start();
		fWriterOutput.start();
		fDataWriter.start();
		
		// Wait for test to complete.
		synchronized (fDataReader)
		{
			try
			{
				//fDataReader.wait(10000);
				fDataReader.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		return fDataReader.getSamplesReceived();
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite()
	{
		TestSuite suite = new TestSuite(DataModelTest.class);
		//TestSuite suite = new TestSuite();
		//suite.addTest(new DataModelTest("testDataIntegrityEqualSizes"));
		//suite.addTest(new DataModelTest("testDataIntegrityUniformSizes"));
		//suite.addTest(new DataModelTest("testDataIntegrityOddProducerSizes"));
		//suite.addTest(new DataModelTest("testDataIntegrityOddConsumerSizes"));
		return suite;
	}

	//--- Utility classes ----------------------------------------------------

	private static class DataReader extends BasisSetProcessor
	{
		protected int fExpectedStartValue = 0;
		protected boolean fExpectedValueInitialized = false;
		protected int fSamplesReceived = 0;
		private int fTestDuration = 1000;
		
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
			DataBuffer basisBuffer = basisSet.getBasisBuffer();
			int samples = basisBuffer.getSize();
			fSamplesReceived += samples;
			//System.out.println("DMTest Reader processBasisSet Start:" + basisSet.getFirstBasisValue() + " size:" + basisSet.getSize() + " totalSamples:" + fSamplesReceived);
			//System.out.println("DMTest Allocation:" + basisSet.getAllocation());

			if (!fExpectedValueInitialized)
			{
				fExpectedValueInitialized = true;
				fExpectedStartValue = basisBuffer.getAsInt(0);
			}
			
			int expectedValue = fExpectedStartValue;
			
			// Test basis buffer
			for (int j = 0; j < samples; j++)
			{
				//System.out.println("Expected:" + expectedValue + " actual:" + basisBuffer.getAsInt(j) + " at:" + j);
				assertEquals("Basis buffer value", expectedValue, basisBuffer.getAsInt(j));
				expectedValue++;
			}
			
			testEachDataBuffer(basisSet, fExpectedStartValue);

			// Update expected value for next BasisSet
			fExpectedStartValue += samples;

			if (fSamplesReceived >= fTestDuration)
			{
				synchronized(this)
				{
					this.notifyAll();
				}
			}				
		}

		protected void testEachDataBuffer(BasisSet basisSet, int expectedStartValue)
		{
			DataBuffer aBuffer = null;
			
			for (Iterator buffers = basisSet.getDataBuffers(); buffers.hasNext();)
			{
				int expectedValue = expectedStartValue;
				aBuffer = (DataBuffer) buffers.next();
				int size = aBuffer.getSize();
				String message = aBuffer.getName();
				
				for (int j = 0; j < size; j++)
				{
					assertEquals(message, expectedValue, aBuffer.getAsInt(j));
					expectedValue++;
				}
			}
		}	
		
		/**
		 * @return Returns the samplesReceived.
		 */
		public int getSamplesReceived()
		{
			return fSamplesReceived;
		}
		
		/**
		 * @param testDuration The testDuration to set.
		 */
		public void setTestDuration(int testDuration)
		{
			fTestDuration = testDuration;
		}
	}
	
	private static class DataWriter extends DefaultProcessor
	{
		public final String BASIS_BUFFER_NAME = "time";
		public final String DATA_BUFFER_NAME = "data";
		public final DataBufferDescriptor BASIS_BUFFER_DESCRIPTOR = 
			new DataBufferDescriptor(BASIS_BUFFER_NAME, int.class, 
				SI.MILLI(SI.SECOND));
		
		public final DataBufferDescriptor DATA_BUFFER_DESCRIPTOR = 
			new DataBufferDescriptor(DATA_BUFFER_NAME, int.class);

		private int fSamplesPerBasisSet;
		private int fBasisBundleCapacity;
		private int fDataBuffersPerBasisBundle;
		private DataGenerator fDataGenerator = new DataGenerator();
		private Thread fDataGeneratorThread;
		private BasisSet fOutputBasisSet;
		
		private BasisBundleDescriptor fBasisBundleDescriptor;
		private BasisBundleId fBasisBundleId;
		boolean fResizeBasisBundleFlag = false;	
			
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
				synchronized (getConfigurationChangeLock())
				{	
					super.stop();
					
					if (! fDataGeneratorThread.isInterrupted())
					{
						fDataGeneratorThread.interrupt();
					}
					
					synchronized (getOutput())
					{
						if (fOutputBasisSet != null)
						{
							fOutputBasisSet.release();
						}
					}
					
					fDataGeneratorThread = null;
				}
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

		/**
		 * Creates the Output BasisBundle for this generator's data.
		 */		
		protected void createOutputBasisBundle()
		{
			if (fBasisBundleId == null)
			{
				fBasisBundleId = getOutput().addBasisBundle
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
								DATA_BUFFER_NAME + i, int.class));
			}
			
			BasisBundleDescriptor basisBundleDescriptor = 
				new BasisBundleDescriptor
					("Signals", BASIS_BUFFER_DESCRIPTOR, dataBufferDescriptors);
			
			return (basisBundleDescriptor);
		}

		/**
		 * @param basisBundleCapacity The basisBundleCapacity to set.
		 */
		public void setBasisBundleCapacity(int basisBundleCapacity)
		{
			fBasisBundleCapacity = basisBundleCapacity;
		}
		
		/**
		 * @param dataBuffersPerBasisBundle The dataBuffersPerBasisBundle to set.
		 */
		public void setDataBuffersPerBasisBundle(int dataBuffersPerBasisBundle)
		{
			fDataBuffersPerBasisBundle = dataBuffersPerBasisBundle;
		}
		
		/**
		 * @param samplesPerBasisSet The samplesPerBasisSet to set.
		 */
		public void setSamplesPerBasisSet(int samplesPerBasisSet)
		{
			fSamplesPerBasisSet = samplesPerBasisSet;
		}

		private class DataGenerator implements Runnable
		{
			private int fDataValue = 0;
			
			/**
			 * Causes this DataGeneratorThread to generate data.
			 */		
			public void run()
			{
				Output output = getOutput();
				
				if (output != null)
				{
					if (fBasisBundleId == null)
					{
						createOutputBasisBundle();
					}
					
					output.resizeBasisBundle(fBasisBundleId, 
							fBasisBundleCapacity);
					
					output.startNewBasisSequence(fBasisBundleId);
					
					declareActive();
					
					while (isStarted() && (fSamplesPerBasisSet > 0))
					{
						synchronized (getConfigurationChangeLock())
						{	
							if (isStarted())
							{
								fOutputBasisSet = output.allocateBasisSet
									(fBasisBundleId, fSamplesPerBasisSet);
								
								// Calculate and write the appropriate basis and data 
								// values to the output BasisSet.
								
								DataBuffer basisBuffer = fOutputBasisSet.getBasisBuffer();
								
								for (int i = 0; i < fSamplesPerBasisSet; i++)
								{
									basisBuffer.put(i, fDataValue + i);
								}
	
								for (Iterator buffers = 
									fOutputBasisSet.getDataBuffers(); 
									buffers.hasNext();)
								{
									DataBuffer dataBuffer = 
										(DataBuffer) buffers.next();

									for (int j = 0; j < fSamplesPerBasisSet; j++)
									{
										dataBuffer.put(j, fDataValue + j);
									}
								}
							}
						}
							
						synchronized (output)
						{
							if (fOutputBasisSet != null)
							{
								output.makeAvailable(fOutputBasisSet);
							}
							
							fOutputBasisSet = null;
							
							if (fResizeBasisBundleFlag 
									&& (fDataValue % (2 * fBasisBundleCapacity) == 0))
							{
								// Force a resize of the BasisBundle
								output.resizeBasisBundle(fBasisBundleId, 
									fBasisBundleCapacity);
							}

						}

						fDataValue += fSamplesPerBasisSet;
					}					
				}
			}
		}

		/**
		 * @param resizeBasisBundleFlag The resizeBasisBundleFlag to set.
		 */
		public void setResizeBasisBundleFlag(boolean resizeBasisBundleFlag)
		{
			fResizeBasisBundleFlag = resizeBasisBundleFlag;
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: DataModelTest.java,v $
//  Revision 1.3  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/07/19 18:01:55  tames_cvs
//  Removed all reference to obsolete DataBufferType class.
//
//  Revision 1.1  2005/07/14 21:46:20  tames
//  Initial version
//
//