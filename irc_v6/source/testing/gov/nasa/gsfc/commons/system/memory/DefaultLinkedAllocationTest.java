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

package gov.nasa.gsfc.commons.system.memory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit test for methods implemented by the
 * {@link gov.nasa.gsfc.commons.system.memory.DefaultLinkedAllocation DefaultLinkedAllocation}
 * class.
 * <P>
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/09/09 21:45:50 $
 * @author 	Troy Ames
 */
public class DefaultLinkedAllocationTest extends TestCase
{
	private Allocation fRootAllocation;
	private MemoryModel fModel = null;
	
	/**
	 * Default constructor for the test
	 */
	public DefaultLinkedAllocationTest()
	{
		super();
	}

	/**
	 * Constructs the specified test.
	 * 
	 * @param name the test method name
	 */
	public DefaultLinkedAllocationTest(String name)
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
		fModel = new ContiguousMemoryModel(500);
		fRootAllocation = new DefaultLinkedAllocation(fModel, 0, 100);
	}

	/**
	 * Tear down for test cases defined in this class.
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test slicing an allocation.
	 */
	public void testSlice()
	{
		assertEquals(1, fRootAllocation.getReferenceCount());
		
		// make one slice
		Allocation firstAllocation = fRootAllocation.slice(0, 10);	
		assertEquals(10, firstAllocation.getSize());
		assertEquals(2, fRootAllocation.getReferenceCount());

		// make second slice
		Allocation secondAllocation = fRootAllocation.slice(10, 10);;	
		assertEquals(10, secondAllocation.getSize());
		assertEquals(10, secondAllocation.getStart());
		assertEquals(3, fRootAllocation.getReferenceCount());

		// Free sub slices and check reference count
		secondAllocation.release();
		assertEquals(2, fRootAllocation.getReferenceCount());	
		firstAllocation.release();
		assertEquals(1, fRootAllocation.getReferenceCount());	
	}
	
	/**
	 * Test appending an allocation.
	 */
	public void testAppend()
	{
		assertEquals(1, fRootAllocation.getReferenceCount());
		
		// Make second Allocation
		AbstractAllocation secondAllocation = new DefaultLinkedAllocation(fModel, 100, 10);	
		assertEquals(10, secondAllocation.getSize());
		assertEquals(100, secondAllocation.getStart());
		assertEquals(1, secondAllocation.getReferenceCount());

		// Combine Allocations
		Allocation combinedAllocation = fRootAllocation.append(secondAllocation);
		assertEquals(1, combinedAllocation.getReferenceCount());	
		assertEquals(2, secondAllocation.getReferenceCount());	
		assertEquals(2, fRootAllocation.getReferenceCount());	

		// Test dereference
		combinedAllocation.release();
		assertEquals(0, combinedAllocation.getReferenceCount());	
		assertEquals(1, secondAllocation.getReferenceCount());	
		assertEquals(1, fRootAllocation.getReferenceCount());	
	}
	
	/**
	 * Test that two allocations are contiguous.
	 */
	public void testIsContiguousWith()
	{
		// Make second contiguous Allocation
		Allocation contiguousAllocation = new DefaultLinkedAllocation(fModel, 100, 10);	
		assertTrue(fRootAllocation.isContiguousWith(contiguousAllocation));

		// Make noncontigous Allocation
		Allocation noncontigousAllocation = new DefaultLinkedAllocation(fModel, 200, 10);	
		assertFalse(fRootAllocation.isContiguousWith(noncontigousAllocation));
		
		// Test contiguous slices
		Allocation firstSlice = fRootAllocation.slice(0, 50);
		Allocation secondSlice = fRootAllocation.slice(50, 50);
		assertTrue(firstSlice.isContiguousWith(secondSlice));
		assertFalse(firstSlice.isContiguousWith(noncontigousAllocation));
		
		// Test contiguous Allocations after append
		Allocation appendedSlices = firstSlice.append(secondSlice);
		assertTrue(appendedSlices.isContiguousWith(contiguousAllocation));		
		assertFalse(appendedSlices.isContiguousWith(noncontigousAllocation));
	}
	
	public static Test suite()
	{
		TestSuite suite = new TestSuite(DefaultLinkedAllocationTest.class);
		return suite;
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultLinkedAllocationTest.java,v $
//  Revision 1.2  2005/09/09 21:45:50  tames
//  Updated JavaDoc.
//
//  Revision 1.1  2005/07/14 21:46:01  tames
//  Initial version
//
//