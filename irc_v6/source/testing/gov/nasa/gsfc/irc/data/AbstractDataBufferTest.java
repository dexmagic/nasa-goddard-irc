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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;

/**
 * JUnit test for all methods implemented by the
 * {@link gov.nasa.gsfc.irc.data.AbstractDataBuffer AbstractDataBuffer} class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/07/19 18:01:55 $
 * @author 	Troy Ames
 */
public class AbstractDataBufferTest extends TestCase
{
	private AbstractDataBuffer fDataBuffer;
	private int [] fTestArray = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private int fTestCapacity = fTestArray.length;
	
	/**
	 * Set up for test cases defined in this class.
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		DataBufferDescriptor descriptor = 
			new DataBufferDescriptor("TestBuffer", int.class);
			
		fDataBuffer = 
			new IntegerDataBuffer(
				descriptor, fTestArray, 0, fTestCapacity);
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
	 * Test initial property values after construction.
	 */
	public void testInitialCondition()
	{
		assertEquals(fDataBuffer.getSize(), fTestCapacity);
	}

	
	/**
	 * Test the <code>resolveIndex(int)</code> method.
	 * @see AbstractDataBuffer#resolveIndex(int)
	 */
	public void testResolveIndex()
	{
		assertEquals("arrayOffset()", fDataBuffer.arrayOffset(), 0);
		
		for (int i = 0; i < fTestCapacity; i++)
		{
			assertEquals("resolveIndex(i)", 
				fDataBuffer.resolveIndex(i), 
				(fDataBuffer.fArrayOffset + i) % fDataBuffer.fArrayLength);
		}

		// Test wrap around
		fDataBuffer.fArrayOffset = 5;
		assertEquals("arrayOffset()", fDataBuffer.arrayOffset(), 5);
		
		for (int i = 0; i < fTestCapacity; i++)
		{
			assertEquals("resolveIndex(i)", 
				fDataBuffer.resolveIndex(i), 
				(fDataBuffer.fArrayOffset + i) % fDataBuffer.fArrayLength);
		}
	}

	/**
	 * Test slicing a DataBuffer.
	 * @see AbstractDataBuffer#slice(int, int)
	 */
	public void testSlice()
	{
		DataBuffer fSlicedBuffer = fDataBuffer.slice(0, fDataBuffer.getSize());
		
		// check simple case when slice is the same region.
		assertEquals(fDataBuffer.getSize(), fSlicedBuffer.getSize());
		
		// check case when slice is a subregion.
		fSlicedBuffer = fDataBuffer.slice(1, fDataBuffer.getSize() - 1);

		assertEquals(fDataBuffer.getSize()-1, fSlicedBuffer.getSize());
		assertEquals(
			fDataBuffer.arrayOffset() + 1, 
			fSlicedBuffer.arrayOffset());
	}
	
	/**
	 * Test finding Min and Max of the buffer.
	 * @see AbstractDataBuffer#slice(int, int)
	 */
	public void testMinMax()
	{
		assertEquals(9, (int) fDataBuffer.getMaxValue());
		assertEquals(9, (int) fDataBuffer.getMaxValueIndex());
		assertEquals(0, (int) fDataBuffer.getMinValue());
		assertEquals(0, (int) fDataBuffer.getMinValueIndex());
	}
	
	/**
	 * Test duplicating a DataBuffer.
	 * @see AbstractDataBuffer#duplicate()
	 */
	public void testDuplicate()
	{		
		AbstractDataBuffer fDuplicateBuffer = 
			(AbstractDataBuffer) fDataBuffer.duplicate();
		
		assertEquals(fDuplicateBuffer.isReadOnly(), fDataBuffer.isReadOnly());
		assertEquals(fDuplicateBuffer.getSize(), fDataBuffer.getSize());
		assertEquals(fDuplicateBuffer.resolveIndex(0), fDataBuffer.resolveIndex(0));
		assertEquals(fDuplicateBuffer.arrayOffset(), fDataBuffer.arrayOffset());

		for (int i = 0; i < fDataBuffer.getSize(); i++)
		{
			assertEquals(fDuplicateBuffer.getAsInt(i), fDataBuffer.getAsInt(i));
		}
		
		// Check that they share array
		assertSame(fDuplicateBuffer.array(), fDataBuffer.array());
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite()
	{
		return new TestSuite(AbstractDataBufferTest.class);
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataBufferTest.java,v $
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