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
 * JUnit test the memory model implemented by the
 * {@link gov.nasa.gsfc.commons.system.memory.ContiguousMemoryModel ContiguousMemoryModel}
 * class.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/28 18:44:22 $
 * @author 	Troy Ames
 */
public class ContiguousMemoryModelTest extends TestCase
{
	private ContiguousMemoryModel fModel;

	/**
	 * Default constructor for the test
	 */
	public ContiguousMemoryModelTest()
	{
		super();
	}

	/**
	 * Constructs the specified test.
	 * 
	 * @param name the test method name
	 */
	public ContiguousMemoryModelTest(String name)
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
		fModel = new ContiguousMemoryModel(100);
	}

	/**
	 * Tear down for test cases defined in this class.
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
		fModel = null;
	}

	/**
	 * Test allocation and deallocation to verify memory availability is 
	 * correct.
	 */
	public void testAllocations()
	{
		assertEquals(100, fModel.getSize());
		Allocation [] allocations = new Allocation[10];
		
		for (int i = 0; i < 10; i++)
		{
			assertEquals(100 - (i * 10), fModel.getAmountAvailable());
			allocations[i] = fModel.allocate(10);
		}

		assertFalse(fModel.isAvailable(1));
		assertEquals(0, fModel.getAmountAvailable());

		for (int i = 0; i < 10; i++)
		{
			assertEquals(i * 10, fModel.getAmountAvailable());
			allocations[i].release();
		}

		assertEquals(100, fModel.getAmountAvailable());
	}
	
	/**
	 * Test that the MemoryModel returns null when the model is full.
	 */
	public void testBlockingAllocations()
	{
		assertEquals(100, fModel.getSize());
		
		// Check if memory is full
		Allocation allocation = fModel.allocate(100);		
		assertEquals(0, fModel.getAmountAvailable());
		assertFalse(fModel.isAvailable(1));
		assertEquals(null, fModel.allocate(1));
		// Check zero allocation condition
		assertEquals(null, fModel.allocate(0));
		allocation.release();

		// Check when model is almost full
		allocation = fModel.allocate(90);		
		assertEquals(10, fModel.getAmountAvailable());
		assertEquals(null, fModel.allocate(11));
	}
	
	/**
	 * Test allocations that wrap around the end of the memory space.
	 */
	public void testWrappingAllocations()
	{
		assertEquals(100, fModel.getSize());
		Allocation firstAllocation = fModel.allocate(25);
		assertEquals(75, fModel.getAmountAvailable());
		assertEquals(0, firstAllocation.getStart());
		assertEquals(25, firstAllocation.getSize());

		Allocation secondAllocation = fModel.allocate(50);
		assertEquals(25, fModel.getAmountAvailable());
		assertEquals(25, secondAllocation.getStart());
		assertEquals(50, secondAllocation.getSize());

		firstAllocation.release();
		Allocation wrappingAllocation = fModel.allocate(50);
		assertEquals(0, fModel.getAmountAvailable());
		assertEquals(75, wrappingAllocation.getStart());
		assertEquals(50, wrappingAllocation.getSize());

		secondAllocation.release();
		wrappingAllocation.release();
		assertEquals(100, fModel.getAmountAvailable());
	}
	
	/**
	 * Test allocations when deallocations are not done in the order they 
	 * were made.
	 */
	public void testNoncontiguousDeallocations()
	{
		assertEquals(100, fModel.getSize());
		Allocation [] allocations = new Allocation[10];
		
		for (int i = 0; i < 10; i++)
		{
			assertEquals(100 - (i * 10), fModel.getAmountAvailable());
			allocations[i] = fModel.allocate(10);
		}

		assertFalse(fModel.isAvailable(1));
		assertEquals(0, fModel.getAmountAvailable());

		// Release all but the first and last
		for (int i = 1; i < 9; i++)
		{
			allocations[i].release();
			assertEquals(0, fModel.getAmountAvailable());
		}
		
		// Release the first which should make prior releases contiguous and
		// visible
		allocations[0].release();
		assertEquals(90, fModel.getAmountAvailable());

		// Release the last
		allocations[9].release();
		assertEquals(100, fModel.getAmountAvailable());
	}
	
	/**
	 * Test allocations when deallocations are min the reverse order they 
	 * were made.
	 */
	public void testReverseContiguousDeallocations()
	{
		assertEquals(100, fModel.getSize());
		Allocation [] allocations = new Allocation[10];
		
		for (int i = 0; i < 10; i++)
		{
			assertEquals(100 - (i * 10), fModel.getAmountAvailable());
			allocations[i] = fModel.allocate(10);
		}

		assertFalse(fModel.isAvailable(1));
		assertEquals(0, fModel.getAmountAvailable());

		for (int i = 9; i >= 0; i--)
		{
			allocations[i].release();
			assertEquals(100 - (i * 10), fModel.getAmountAvailable());
		}

		assertEquals(100, fModel.getAmountAvailable());
	}
	
	/**
	 * Returns the suite of tests for this class.
	 * @return a Test suite.
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite(ContiguousMemoryModelTest.class);
		return suite;
	}

	/**
	 * Runs the suite of tests using the <code>TestRunner</code> class.
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}	
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ContiguousMemoryModelTest.java,v $
//  Revision 1.3  2005/10/28 18:44:22  tames
//  Added tests for releasing the most recent allocation.
//
//  Revision 1.2  2005/09/09 21:45:17  tames
//  Added test for blocking allocations and updated JavaDoc.
//
//  Revision 1.1  2005/07/14 21:46:01  tames
//  Initial version
//
//