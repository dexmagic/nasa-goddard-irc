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

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisSetEvent;
import gov.nasa.gsfc.irc.data.events.BasisSetListener;

/**
 * JUnit test for methods implemented by the
 * {@link gov.nasa.gsfc.irc.data.BasisBundle BasisBundle} class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:55 $
 * @author 	Troy Ames
 */
public class BasisBundleTest extends TestCase
{
	private final String BASIS_BUFFER_NAME = "time";
	private final String DATA_BUFFER_NAME = "data";
	private final DataBufferDescriptor BASIS_BUFFER_DESCRIPTOR = 
		new DataBufferDescriptor(BASIS_BUFFER_NAME, int.class, 
			SI.MILLI(SI.SECOND));
	
	private int fTestCapacity = 10;
	private int fDataBuffersPerBasisBundle = 1;
	private BasisBundle fBasisBundle;
	
	/**
	 * Default constructor for the test
	 */
	public BasisBundleTest()
	{
		super();
	}
	
	/**
	 * Constructs the specified test.
	 * 
	 * @param name the test method name
	 */
	public BasisBundleTest(String name)
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
		
		fBasisBundle.addBasisSetListener(
			new BasisSetListener()
			{
				public void receiveBasisSetEvent(BasisSetEvent event)
				{
					event.getBasisSet().release();
				}			
			});
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
	 * Test makeAvailable using 0 of the allocated size.
	 * 
	 * @see BasisBundle#makeAvailable(BasisSet, int)
	 */
	public void testMakeAvailable0()
	{
		int basisSetSize = 3;
		
		for (int i = 1; i < 1000; i++)
		{
			BasisSet basisSet = fBasisBundle.allocateBasisSet(basisSetSize);
			basisSet.makeAvailable(0);
		}
	}
	
	/**
	 * Test makeAvailable using all of the allocated size.
	 * 
	 * @see BasisBundle#makeAvailable(BasisSet, int)
	 */
	public void testMakeAvailableAll()
	{
		int basisSetSize = 3;
		
		for (int i = 1; i < 1000; i++)
		{
			BasisSet basisSet = fBasisBundle.allocateBasisSet(basisSetSize);
			basisSet.makeAvailable(basisSetSize);
		}
	}
	
	/**
	 * Test makeAvailable using a varible number of the allocated size.
	 * 
	 * @see BasisBundle#makeAvailable(BasisSet, int)
	 */
	public void testMakeAvailableVariable()
	{
		int basisSetSize = 3;
		
		for (int i = 1; i < 1000; i++)
		{
			BasisSet basisSet = fBasisBundle.allocateBasisSet(basisSetSize);
			basisSet.makeAvailable(i % basisSetSize);
		}
	}
	
	/**
	 * Test simple allocation and deallocation from the BasisBundle.
	 * 
	 * @see BasisBundle#makeAvailable(BasisSet)
	 */
	public void testAllocations()
	{
		int basisSetSize = 3;
		BasisSet basisSet = fBasisBundle.allocateBasisSet(basisSetSize);
		basisSet.makeAvailable();
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
		TestSuite suite = new TestSuite(BasisBundleTest.class);
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
//  $Log: BasisBundleTest.java,v $
//  Revision 1.3  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/11/14 19:52:59  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2005/10/28 18:45:03  tames
//  Initial version.
//
//