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
 * JUnit test for the
 * {@link gov.nasa.gsfc.commons.system.memory.CompositeAllocation CompositeAllocation}
 * class.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/09/22 18:52:06 $
 * @author Troy Ames
 */
public class CompositeAllocationTest extends TestCase
{
	private MemoryModel fModel = null;
	
	/**
	 * Default constructor for the test
	 */
	public CompositeAllocationTest()
	{
		super();
	}

	/**
	 * Constructs the specified test.
	 * 
	 * @param name the test method name
	 */
	public CompositeAllocationTest(String name)
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
	 * Test allocation construction.
	 */
	public void testAllocationConstruction()
	{
		// Test simple construction
		Allocation rootAllocation = new CompositeAllocation(fModel, 0, 100);
		assertEquals(1, rootAllocation.getReferenceCount());
		assertEquals(100, rootAllocation.getSize());
		assertEquals(0, rootAllocation.getStart());
		
		rootAllocation.release();
		assertEquals(0, rootAllocation.getReferenceCount());
		
		// Test wrap around construction
		rootAllocation = new CompositeAllocation(fModel, 450, 100);
		assertEquals(1, rootAllocation.getReferenceCount());
		assertEquals(100, rootAllocation.getSize());
		assertEquals(450, rootAllocation.getStart());
		
		rootAllocation.release();
		assertEquals(0, rootAllocation.getReferenceCount());
	}
	
	/**
	 * Test slicing an allocation.
	 */
	public void testAllocationSlice()
	{
		Allocation rootAllocation = new CompositeAllocation(fModel, 0, 100);
		
		// make one slice
		Allocation firstAllocation = rootAllocation.slice(0, 10);	
		assertEquals(2, rootAllocation.getReferenceCount());

		// make second slice
		Allocation secondAllocation = rootAllocation.slice(10, 10);;	
		assertEquals(3, rootAllocation.getReferenceCount());

		// Free sub slices and check reference count
		secondAllocation.release();
		assertEquals(2, rootAllocation.getReferenceCount());	
		firstAllocation.release();
		assertEquals(1, rootAllocation.getReferenceCount());	
	}
	
	/**
	 * Test slicing a composite allocation consisting of several parent
	 * allocations. The parent allocations should be released 
	 * or held appropriately.
	 */
	public void testMultiAllocationSlice()
	{
		// Make Allocations
		Allocation firstAllocation = new CompositeAllocation(fModel, 0, 100);
		AbstractAllocation secondAllocation = new CompositeAllocation(fModel, 100, 10);	
		AbstractAllocation thirdAllocation = new CompositeAllocation(fModel, 110, 10);	

		// Combine Allocations
		Allocation combinedAllocation = firstAllocation.append(secondAllocation);
		assertEquals(1, combinedAllocation.getReferenceCount());	
		assertEquals(2, secondAllocation.getReferenceCount());	
		assertEquals(2, firstAllocation.getReferenceCount());	

		// Combine Allocations
		Allocation multipleAllocation = combinedAllocation.append(thirdAllocation);
		combinedAllocation.release();
		assertEquals(1, multipleAllocation.getReferenceCount());	
		assertEquals(0, combinedAllocation.getReferenceCount());	
		assertEquals(2, thirdAllocation.getReferenceCount());	
		assertEquals(2, secondAllocation.getReferenceCount());	
		assertEquals(2, firstAllocation.getReferenceCount());
		
		// Slice multipleAllocation and verify parent allocations are 
		// incremented.
		Allocation firstSlicedAllocation = multipleAllocation.slice(90, 30);
		assertEquals(30, firstSlicedAllocation.getSize());
		assertEquals(90, firstSlicedAllocation.getStart());
		
		// These should not have changed
		assertEquals(1, multipleAllocation.getReferenceCount());	
		
		// These should be incremented
		assertEquals(3, thirdAllocation.getReferenceCount());	
		assertEquals(3, secondAllocation.getReferenceCount());	
		assertEquals(3, firstAllocation.getReferenceCount());

		// Slice allocation and verify parent allocations are 
		// incremented or removed. This slice should be contained in the
		// second allocation only.
		Allocation secondSlicedAllocation = firstSlicedAllocation.slice(11, 9);
		assertEquals(9, secondSlicedAllocation.getSize());
		assertEquals(101, secondSlicedAllocation.getStart());
		
		// This should be incremented
		assertEquals(4, secondAllocation.getReferenceCount());	

		// These should not have been incremented
		assertEquals(3, thirdAllocation.getReferenceCount());	
		assertEquals(3, firstAllocation.getReferenceCount());
	}

	/**
	 * Test slicing a composite allocation consisting of several parent
	 * allocations that wrap around the end of a memory model. The parent
	 * allocations should be released or held appropriately.
	 */
	public void testWrappingMultiAllocationSlice()
	{
		// Make Allocations
		Allocation firstAllocation = new CompositeAllocation(fModel, 450, 100);
		AbstractAllocation secondAllocation = new CompositeAllocation(fModel, 50, 10);	
		AbstractAllocation thirdAllocation = new CompositeAllocation(fModel, 60, 10);	

		// Combine Allocations
		Allocation combinedAllocation = firstAllocation.append(secondAllocation);
		assertEquals(1, combinedAllocation.getReferenceCount());	
		assertEquals(2, secondAllocation.getReferenceCount());	
		assertEquals(2, firstAllocation.getReferenceCount());	

		// Combine Allocations
		Allocation multipleAllocation = combinedAllocation.append(thirdAllocation);
		combinedAllocation.release();
		assertEquals(1, multipleAllocation.getReferenceCount());	
		assertEquals(0, combinedAllocation.getReferenceCount());	
		assertEquals(2, thirdAllocation.getReferenceCount());	
		assertEquals(2, secondAllocation.getReferenceCount());	
		assertEquals(2, firstAllocation.getReferenceCount());
		
		// Slice multipleAllocation and verify parent allocations are 
		// incremented.
		Allocation firstSlicedAllocation = multipleAllocation.slice(90, 30);
		assertEquals(30, firstSlicedAllocation.getSize());
		assertEquals(40, firstSlicedAllocation.getStart());
		
		// These should not have changed
		assertEquals(1, multipleAllocation.getReferenceCount());	
		
		// These should be incremented
		assertEquals(3, thirdAllocation.getReferenceCount());	
		assertEquals(3, secondAllocation.getReferenceCount());	
		assertEquals(3, firstAllocation.getReferenceCount());

		// Slice allocation and verify parent allocations are 
		// incremented or removed. This slice should be contained in the
		// second allocation only.
		Allocation secondSlicedAllocation = firstSlicedAllocation.slice(11, 9);
		assertEquals(9, secondSlicedAllocation.getSize());
		assertEquals(51, secondSlicedAllocation.getStart());
		
		// This should be incremented
		assertEquals(4, secondAllocation.getReferenceCount());	

		// These should not have been incremented
		assertEquals(3, thirdAllocation.getReferenceCount());	
		assertEquals(3, firstAllocation.getReferenceCount());

		// Slice allocation and verify parent allocations are 
		// incremented or removed. This slice should be contained in the
		// first allocation only.
		Allocation thirdSlicedAllocation = multipleAllocation.slice(10, 30);
		assertEquals(30, thirdSlicedAllocation.getSize());
		assertEquals(460, thirdSlicedAllocation.getStart());
		
		// This should be incremented
		assertEquals(4, firstAllocation.getReferenceCount());

		// These should not have been incremented
		assertEquals(4, secondAllocation.getReferenceCount());	
		assertEquals(3, thirdAllocation.getReferenceCount());	
	}
	
	/**
	 * Test appending an allocation.
	 */
	public void testMultipleAllocation()
	{
		// Make Allocations
		Allocation firstAllocation = new CompositeAllocation(fModel, 0, 100);		
		AbstractAllocation secondAllocation = new CompositeAllocation(fModel, 100, 10);	
		AbstractAllocation thirdAllocation = new CompositeAllocation(fModel, 110, 10);	

		// Combine Allocations
		Allocation combinedAllocation = firstAllocation.append(secondAllocation);
		assertEquals(1, combinedAllocation.getReferenceCount());	
		assertEquals(2, secondAllocation.getReferenceCount());	
		assertEquals(2, firstAllocation.getReferenceCount());	
		assertEquals(110, combinedAllocation.getSize());
		assertEquals(0, combinedAllocation.getStart());

		// Combine Allocations
		Allocation multipleAllocation = combinedAllocation.append(thirdAllocation);
		assertEquals(1, multipleAllocation.getReferenceCount());	
		assertEquals(1, combinedAllocation.getReferenceCount());	
		assertEquals(2, thirdAllocation.getReferenceCount());	
		assertEquals(3, secondAllocation.getReferenceCount());	
		assertEquals(3, firstAllocation.getReferenceCount());	

		assertEquals(120, multipleAllocation.getSize());
		assertEquals(0, multipleAllocation.getStart());

		// Test dereference
		combinedAllocation.release();
		assertEquals(0, combinedAllocation.getReferenceCount());	
		assertEquals(2, thirdAllocation.getReferenceCount());	
		assertEquals(2, secondAllocation.getReferenceCount());	
		assertEquals(2, firstAllocation.getReferenceCount());	

		// Test dereference
		multipleAllocation.release();
		assertEquals(0, combinedAllocation.getReferenceCount());	
		assertEquals(1, thirdAllocation.getReferenceCount());	
		assertEquals(1, secondAllocation.getReferenceCount());	
		assertEquals(1, firstAllocation.getReferenceCount());	
	}
	
	/**
	 * Test appending an allocation.
	 */
	public void testAppend()
	{
		// Make Allocations
		Allocation firstAllocation = new CompositeAllocation(fModel, 0, 100);		
		AbstractAllocation secondAllocation = new CompositeAllocation(fModel, 100, 10);	

		// Combine Allocations
		Allocation combinedAllocation = firstAllocation.append(secondAllocation);
		assertEquals(1, combinedAllocation.getReferenceCount());	
		assertEquals(2, secondAllocation.getReferenceCount());	
		assertEquals(2, firstAllocation.getReferenceCount());	
		assertEquals(110, combinedAllocation.getSize());
		assertEquals(0, combinedAllocation.getStart());

		// Test dereference
		combinedAllocation.release();
		assertEquals(0, combinedAllocation.getReferenceCount());	
		assertEquals(1, secondAllocation.getReferenceCount());	
		assertEquals(1, firstAllocation.getReferenceCount());	
	}
	
	/**
	 * Test append and slice.
	 */
	public void testAppendAndSlice()
	{
		// Make Allocations
		Allocation firstAllocation = new CompositeAllocation(fModel, 0, 100);		
		AbstractAllocation secondAllocation = new CompositeAllocation(fModel, 100, 10);	

		// Combine Allocations and slice so that the first is no longer needed.
		Allocation combinedAllocation = 
			((CompositeAllocation) firstAllocation).appendAndSlice(
				secondAllocation, 100, 10);
		assertEquals(1, combinedAllocation.getReferenceCount());	
		assertEquals(2, secondAllocation.getReferenceCount());	
		assertEquals(1, firstAllocation.getReferenceCount());	
		assertEquals(10, combinedAllocation.getSize());
		assertEquals(100, combinedAllocation.getStart());

		// Test dereference
		combinedAllocation.release();
		assertEquals(0, combinedAllocation.getReferenceCount());	
		assertEquals(1, secondAllocation.getReferenceCount());	
		assertEquals(1, firstAllocation.getReferenceCount());	
	}
	
	/**
	 * Test that two allocations are contiguous.
	 */
	public void testIsContiguousWith()
	{
		Allocation firstAllocation = new CompositeAllocation(fModel, 0, 100);

		// Make second contiguous Allocation
		Allocation contiguousAllocation = new CompositeAllocation(fModel, 100, 10);	
		assertTrue(firstAllocation.isContiguousWith(contiguousAllocation));

		// Make noncontigous Allocation
		Allocation noncontigousAllocation = new CompositeAllocation(fModel, 200, 10);	
		assertFalse(firstAllocation.isContiguousWith(noncontigousAllocation));
		
		// Test contiguous slices
		Allocation firstSlice = firstAllocation.slice(0, 50);
		Allocation secondSlice = firstAllocation.slice(50, 50);
		assertTrue(firstSlice.isContiguousWith(secondSlice));
		assertFalse(firstSlice.isContiguousWith(noncontigousAllocation));
		
		// Test contiguous Allocations after append
		Allocation appendedSlices = firstSlice.append(secondSlice);
		assertTrue(appendedSlices.isContiguousWith(contiguousAllocation));		
		assertFalse(appendedSlices.isContiguousWith(noncontigousAllocation));
	}
	
	public static Test suite()
	{
		TestSuite suite = new TestSuite(CompositeAllocationTest.class);
		return suite;
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: CompositeAllocationTest.java,v $
//  Revision 1.2  2005/09/22 18:52:06  tames
//  Added several additional tests.
//
//  Revision 1.1  2005/09/09 21:44:26  tames
//  Initial implementation.
//
//  Revision 1.1  2005/07/14 21:46:01  tames
//  Initial version
//
//