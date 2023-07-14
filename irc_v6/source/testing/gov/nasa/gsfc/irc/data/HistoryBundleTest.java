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
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jscience.physics.units.SI;

import gov.nasa.gsfc.commons.system.memory.MemoryModel;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;

/**
 * JUnit test for methods implemented by the
 * {@link gov.nasa.gsfc.irc.data.HistoryBundle HistoryBundle} class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:55 $
 * @author 	Troy Ames
 */
public class HistoryBundleTest extends TestCase
{
	private final String BASIS_BUFFER_NAME = "time";
	private final String DATA_BUFFER_NAME = "data";
	private final DataBufferDescriptor BASIS_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(BASIS_BUFFER_NAME, int.class, 
			SI.MILLI(SI.SECOND));
	
	private int fTestCapacity = 10;
	private int fHistorySize = fTestCapacity * 2;
	private int fHistoryCapacity = fHistorySize * 2;
	private int fDataBuffersPerBasisBundle = 1;
	private BasisBundle fBasisBundle;
	private HistoryBundle fHistoryBundle;
	
	/**
	 * Default constructor for the test
	 */
	public HistoryBundleTest()
	{
		super();
	}
	
	/**
	 * Constructs the specified test.
	 * 
	 * @param name the test method name
	 */
	public HistoryBundleTest(String name)
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
		Set dataBufferDescriptors = new HashSet();
		
		// Create descriptor
		for (int i = 0; i < fDataBuffersPerBasisBundle; i++)
		{
			dataBufferDescriptors.add(
					new DataBufferDescriptor(
							DATA_BUFFER_NAME + i, int.class));
		}
		
		BasisBundleDescriptor basisBundleDescriptor = 
			new BasisBundleDescriptor
				("Signals", BASIS_BUFFER_DESCRIPTOR, dataBufferDescriptors);
		
		// Create BasisBundle
		fBasisBundle = new DefaultBasisBundle(basisBundleDescriptor, 
			new BundleSource(), fTestCapacity);
		
		((DefaultBasisBundle) fBasisBundle).createNewBackingBuffers();
		
		Irc.getDataSpace().addBasisBundle(fBasisBundle);
		
		// Create BasisSet
		BasisSet basisSet = fBasisBundle.allocateBasisSet(fTestCapacity);
		DataBuffer basisBuffer = basisSet.getBasisBuffer();
		
		// Initialize Basis Buffer
		for (int i = 0; i < basisBuffer.getSize(); i++)
		{
			basisBuffer.put(i, i);
		}
		
		// Release for test
		basisSet.release();
		
		fHistoryBundle = new HistoryBundle(
			fBasisBundle.getBasisBundleSourceId(), 
			fBasisBundle.getBasisBundleId(),
			basisBundleDescriptor, fHistoryCapacity); 
		
		fHistoryBundle.setHistorySize(fHistorySize);
	}

	/**
	 * Tear down for test cases defined in this class.
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
		Irc.getDataSpace().clear();
		fBasisBundle = null;
	}

	/**
	 * Test appending a BasisSet to history.
	 * 
	 * @see HistoryBundle#appendBasisSet(BasisSet)
	 */
	public void testHistoryAppend()
	{
		BasisSet historySet;
		int basisSetSize = 3;
		
		for (int i = 1; i < 1000; i++)
		{
			BasisSet basisSet = fBasisBundle.allocateBasisSet(basisSetSize);
			
			// Append the basis set to history
			historySet = fHistoryBundle.appendBasisSet(basisSet);

			if (i * basisSetSize < fHistorySize)
			{
				assertEquals(i * basisSetSize, historySet.getSize());
			}
			else
			{
				assertEquals(fHistorySize, historySet.getSize());
			}
			
			assertEquals(
				basisSet.getLastBasisValue(), 
				historySet.getLastBasisValue(), 
				0.1);
			
			int historyIndex = historySet.getSize() - basisSet.getSize();
			
			for (int index = 0; index < basisSet.getSize(); index++)
			{
				assertEquals(
					basisSet.getBasisBuffer().getAsInt(index), 
					historySet.getBasisBuffer().getAsInt(historyIndex + index));
			}

			basisSet.release();
			historySet.release();
		}
	}
	
	/**
	 * Test simple allocation and deallocation from the BasisBundle.
	 * 
	 * @see HistoryBundle#appendBasisSet(BasisSet)
	 */
	public void testAllocations()
	{
		int basisSetSize = 3;
		BasisSet basisSet = fBasisBundle.allocateBasisSet(basisSetSize);
		MemoryModel memoryModel = fHistoryBundle.getMemoryModel();
		int expectedAvailable = 0;
		
		// Calculate the smallest available size expected
		for (int i = 0; i * basisSetSize < fHistorySize; i++)	
		{
			expectedAvailable = fHistoryCapacity - ((i + 1) * basisSetSize);
		}
		
		// Check initial condition
		assertEquals(fHistoryCapacity, memoryModel.getAmountAvailable());
		
		for (int i = 1; i < 1000; i++)
		{
			// Append the basis set to history
			BasisSet historySet = fHistoryBundle.appendBasisSet(basisSet);

			if (i * basisSetSize < fHistorySize)
			{
				assertEquals(i * basisSetSize, historySet.getSize());
				assertEquals(fHistoryCapacity - historySet.getSize(), 
					memoryModel.getAmountAvailable());
			}
			else
			{
				assertEquals(fHistorySize, historySet.getSize());
				assertEquals(expectedAvailable, memoryModel.getAmountAvailable());
			}
			
			historySet.release();
		}

		basisSet.release();
	}
	
	/**
	 * Test allocation and deallocation from the BasisBundle simulating a user
	 * that may hold onto the data for several iterations or release it 
	 * immediately.
	 * 
	 * @see HistoryBundle#appendBasisSet(BasisSet)
	 */
	public void testNoncontigousDeallocations()
	{
		BasisSet historySet = null;
		int basisSetSize = 3;
		BasisSet basisSet = fBasisBundle.allocateBasisSet(basisSetSize);
		MemoryModel memoryModel = fHistoryBundle.getMemoryModel();
		
		// Check initial condition
		assertEquals(fHistoryCapacity, memoryModel.getAmountAvailable());
		
		for (int i = 1; i < 1000; i++)
		{
			// Append the basis set to history
			BasisSet newHistorySet = fHistoryBundle.appendBasisSet(basisSet);

			if (i * basisSetSize < fHistorySize)
			{
				assertEquals(i * basisSetSize, newHistorySet.getSize());
				assertEquals(fHistoryCapacity - newHistorySet.getSize(), 
					memoryModel.getAmountAvailable());
			}
			else
			{
				assertEquals(fHistorySize, newHistorySet.getSize());
				assertTrue(fHistoryCapacity - newHistorySet.getSize() 
					>= memoryModel.getAmountAvailable());
			}
			
			// Selectively hold onto or release BasisSets
			if ((i - 1) % 5 == 0)
			{
				if (historySet != null)
				{
					historySet.release();
				}
				
				historySet = newHistorySet;
			}
			else
			{
				newHistorySet.release();
			}
		}

		basisSet.release();
	}
	
	/**
	 * Runs the suite of tests using the <code>TestRunner</code> class.
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
	
	/**
	 * Returns the suite of tests for this class.
	 * @return a suite of Test.
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite(HistoryBundleTest.class);
		return suite;
	}

	//--- Utility classes ----------------------------------------------------

	private static class BundleSource extends AbstractBasisBundleSource 
	{
		/**
		 * Default constructor.
		 */
		public BundleSource()
		{
			super("testBuncle");
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: HistoryBundleTest.java,v $
//  Revision 1.3  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/09/22 18:53:43  tames
//  Added additional tests.
//
//  Revision 1.1  2005/09/09 21:50:50  tames
//  Initial implementation.
//
//