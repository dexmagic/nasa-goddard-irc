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
 * {@link gov.nasa.gsfc.commons.system.memory.AbstractMemoryModel AbstractMemoryModel}
 * class.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/10/28 18:44:43 $
 * @author Troy Ames
 */
public class AbstractMemoryModelTest extends TestCase
{
	private AbstractMemoryModel fModel;

	/**
	 * Default constructor for the test
	 */
	public AbstractMemoryModelTest()
	{
		super();
	}

	/**
	 * Constructs the specified test.
	 * 
	 * @param name the test method name
	 */
	public AbstractMemoryModelTest(String name)
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
		fModel = new TestMemoryModel();
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
	 * Test Get Listener methods to determine if array casting is correct.
	 */
	public void testGetListeners()
	{
		assertNotNull(fModel.getMemoryModelListeners());
		assertNotNull(fModel.getReleaseRequestListeners());
	}
	
	/**
	 * Returns the suite of tests for this class.
	 * @return a suite of Test.
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite(AbstractMemoryModelTest.class);
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
	
	// --- Utility Classes ---------------------------------------------------
	
	private static class TestMemoryModel extends AbstractMemoryModel
	{
		private int fAllocationPosition = 0;
		private int fAmountAvailable = 0;

		/**
		 * Default constructor of a MemoryModel.
		 */
		public TestMemoryModel()
		{
			super(100);
			fAmountAvailable = 100;
		}

		/**
		 *  Attempts to allocate the given amount of memory from within this 
		 *  MemoryModel. If successful, the index of the start of 
		 *  the allocated block is returned. 
		 * 
		 *  @param amount The desired amount to allocate
		 *  @return The index of the start of the allocation, if successful
		 */	
		public synchronized Allocation allocate(int amount)
		{
			AbstractAllocation result = null;
			
			if (this.getAmountAvailable() >= amount)
			{
				int start = fAllocationPosition;
				
				result = new DefaultLinkedAllocation(this, start, amount);				
				fAllocationPosition = resolveIndex(start + amount);
				fAmountAvailable -= amount;
			}
			
			return (result);
		}
		
		/* (non-Javadoc)
		 * @see gov.nasa.gsfc.commons.system.memory.AbstractMemoryModel#getAmountAvailable()
		 */
		public synchronized int getAmountAvailable()
		{
			return fAmountAvailable;
		}

		/* (non-Javadoc)
		 * @see gov.nasa.gsfc.commons.system.memory.AbstractMemoryModel#release(gov.nasa.gsfc.commons.system.memory.Allocation)
		 */
		public synchronized void release(Allocation allocation)
		{
			fAmountAvailable += allocation.getSize();
		}

		/* (non-Javadoc)
		 * @see gov.nasa.gsfc.commons.system.memory.MemoryModel#resolveIndex(int)
		 */
		public int resolveIndex(int index)
		{
			int size = getSize();
			
			if (index >= size)
			{
				index -= size;
			}
			
			return index;
		}
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractMemoryModelTest.java,v $
//  Revision 1.3  2005/10/28 18:44:43  tames
//  Added test for get listeners methods.
//
//  Revision 1.2  2005/09/09 21:43:58  tames
//  Updated JavaDoc
//
//  Revision 1.1  2005/07/14 21:46:01  tames
//  Initial version
//
//