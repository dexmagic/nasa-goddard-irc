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

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;

/**
 * JUnit test for methods implemented by the
 * {@link gov.nasa.gsfc.irc.data.DefaultBasisSet DefaultBasisSet} class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:55 $
 * @author 	Troy Ames
 */
public class DefaultBasisSetTest extends TestCase
{
	private final String BASIS_BUFFER_NAME = "time";
	private final String DATA_BUFFER_NAME = "data";
	private final DataBufferDescriptor BASIS_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(BASIS_BUFFER_NAME, int.class, 
			SI.MILLI(SI.SECOND));
	
	private final DataBufferDescriptor DATA_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(DATA_BUFFER_NAME, int.class);
	private DefaultBasisSet fBasisSet;
	private int fTestCapacity = 10;
	private int fDataBuffersPerBasisBundle = 1;
	private BasisBundle fBasisBundle;
	
	/**
	 * Default constructor for the test
	 */
	public DefaultBasisSetTest()
	{
		super();
	}
	
	/**
	 * Constructs the specified test.
	 * 
	 * @param name the test method name
	 */
	public DefaultBasisSetTest(String name)
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
	 * Test downsampling the BasisSet from 1 to the size of the BasisSet.
	 * @see BasisSet#downsample(int)
	 */
	public void testDownsample()
	{
		BasisSet basisSet = fBasisBundle.allocateBasisSet(10);
		DataBuffer basisBuffer = basisSet.getBasisBuffer();
		BasisSet sampledSet;
		DataBuffer sampledBuffer;

		// --- Downsample from 1 to size of Basis set.
		for (int downsample = 1; downsample <= basisSet.getSize(); downsample++)
		{
			sampledSet = basisSet.downsample(downsample);
			sampledBuffer = sampledSet.getBasisBuffer();
			int expectedSize = basisSet.getSize() / downsample;
			
			assertEquals(expectedSize, sampledSet.getSize());
			assertEquals(expectedSize, sampledBuffer.getSize());
			
			// Test the values of the downsampled basis buffer
			for (int i = 0; i < sampledBuffer.getSize(); i++)
			{
				int expectedValue = basisBuffer.getAsInt(i * downsample);
				assertEquals(expectedValue, sampledBuffer.getAsInt(i));
			}
			
			// Check the sizes of all the DataBuffers
			int sampledSize = sampledSet.getSize();
			
			for (Iterator buffers = sampledSet.getDataBuffers();
				buffers.hasNext();)
			{
				assertEquals(sampledSize, ((DataBuffer) buffers.next()).getSize());
			}
		}
	}
	
	/**
	 * Test appending one BasisSet to another when they are contiguous.
	 * @see BasisSet#append(BasisSet)
	 */
	public void testContiguousAppend()
	{
		BasisSet basisSetFirst = fBasisBundle.allocateBasisSet(5);
		BasisSet basisSetLast = fBasisBundle.allocateBasisSet(5);
		
		assertTrue(
			((DefaultBasisSet) basisSetFirst).isContiguousWith(basisSetLast));

		BasisSet combinedBasisSet = basisSetFirst.append(basisSetLast);
		
		assertEquals(
			basisSetFirst.getSize() + basisSetLast.getSize(), 
			combinedBasisSet.getSize());

		assertEquals(
			basisSetFirst.getFirstBasisValue(), 
			combinedBasisSet.getFirstBasisValue(), 
			0.1);
		assertEquals(
			basisSetLast.getLastBasisValue(), 
			combinedBasisSet.getLastBasisValue(), 
			0.1);
		
		for (int i = 0; i < basisSetFirst.getSize(); i++)
		{
			assertEquals(
				basisSetFirst.getBasisBuffer().getAsInt(i), 
				combinedBasisSet.getBasisBuffer().getAsInt(i));
		}
		 
		int offset = basisSetFirst.getSize();
		 
		for (int i = 0; i < basisSetLast.getSize(); i++)
		{
			assertEquals(
				basisSetLast.getBasisBuffer().getAsInt(i), 
				combinedBasisSet.getBasisBuffer().getAsInt(offset + i));
		}
	}
	
	/**
	 * Test appending one BasisSet to another when they are not contiguous.
	 * @see BasisSet#append(BasisSet)
	 */
	public void testNoncontiguousAppend()
	{
		BasisSet basisSetFirst = fBasisBundle.allocateBasisSet(4);
		BasisSet basisSetMiddle = fBasisBundle.allocateBasisSet(2);
		BasisSet basisSetLast = fBasisBundle.allocateBasisSet(4);
		
		assertFalse(
			((DefaultBasisSet) basisSetFirst).isContiguousWith(basisSetLast));

		BasisSet combinedBasisSet = basisSetFirst.append(basisSetLast);

		assertEquals(
			basisSetFirst.getSize() + basisSetLast.getSize(), 
			combinedBasisSet.getSize());
		assertEquals(
			basisSetFirst.getFirstBasisValue(), 
			combinedBasisSet.getFirstBasisValue(), 
			0.1);
		assertEquals(
			basisSetLast.getLastBasisValue(), 
			combinedBasisSet.getLastBasisValue(), 
			0.1);
		
		for (int i = 0; i < basisSetFirst.getSize(); i++)
		{
			assertEquals(
				basisSetFirst.getBasisBuffer().getAsInt(i), 
				combinedBasisSet.getBasisBuffer().getAsInt(i));
		}
		 
		int offset = basisSetFirst.getSize();
		 
		for (int i = 0; i < basisSetLast.getSize(); i++)
		{
			assertEquals(
				basisSetLast.getBasisBuffer().getAsInt(i), 
				combinedBasisSet.getBasisBuffer().getAsInt(offset + i));
		}
	}
	
	/**
	 * Test appending two BasisSets to another when they are all contiguous.
	 * @see BasisSet#append(BasisSet)
	 */
	public void testMultipleAppend()
	{
		BasisSet basisSetFirst = fBasisBundle.allocateBasisSet(4);
		BasisSet basisSetMiddle = fBasisBundle.allocateBasisSet(2);
		BasisSet basisSetLast = fBasisBundle.allocateBasisSet(4);
		
		BasisSet combinedBasisSet = basisSetFirst.append(basisSetMiddle);
		combinedBasisSet = combinedBasisSet.append(basisSetLast);

		assertEquals(
			basisSetFirst.getSize() + basisSetMiddle.getSize() + basisSetLast.getSize(), 
			combinedBasisSet.getSize());

		assertEquals(
			basisSetFirst.getFirstBasisValue(), 
			combinedBasisSet.getFirstBasisValue(), 
			0.1);
		assertEquals(
			basisSetLast.getLastBasisValue(), 
			combinedBasisSet.getLastBasisValue(), 
			0.1);
		
		for (int i = 0; i < basisSetFirst.getSize(); i++)
		{
			assertEquals(
				basisSetFirst.getBasisBuffer().getAsInt(i), 
				combinedBasisSet.getBasisBuffer().getAsInt(i));
		}
		 
		int offset = basisSetFirst.getSize();
		 
		for (int i = 0; i < basisSetMiddle.getSize(); i++)
		{
			assertEquals(
				basisSetMiddle.getBasisBuffer().getAsInt(i), 
				combinedBasisSet.getBasisBuffer().getAsInt(offset + i));
		}

		offset += basisSetMiddle.getSize();
		 
		for (int i = 0; i < basisSetLast.getSize(); i++)
		{
			assertEquals(
				basisSetLast.getBasisBuffer().getAsInt(i), 
				combinedBasisSet.getBasisBuffer().getAsInt(offset + i));
		}
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
		TestSuite suite = new TestSuite(DefaultBasisSetTest.class);
		//TestSuite suite = new TestSuite();
		//suite.addTest(new DefaultBasisSetTest("testNoncontiguousAppend"));
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
//  $Log: DefaultBasisSetTest.java,v $
//  Revision 1.6  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2005/09/09 22:43:43  tames
//  JavaDoc updates
//
//  Revision 1.4  2005/07/20 20:01:45  tames_cvs
//  Added test for downsampling.
//
//  Revision 1.3  2005/07/19 18:01:55  tames_cvs
//  Removed all reference to obsolete DataBufferType class.
//
//  Revision 1.2  2005/07/15 19:21:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2005/07/14 21:46:20  tames
//  Initial version
//
//