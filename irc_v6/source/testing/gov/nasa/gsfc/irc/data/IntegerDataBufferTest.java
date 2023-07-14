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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gov.nasa.gsfc.irc.data.description.DataBufferDescriptor;

/**
 * JUnit test for methods implemented by the
 * {@link gov.nasa.gsfc.irc.data.IntegerDataBuffer IntegerDataBuffer} class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/07/20 22:08:12 $
 * @author 	Troy Ames
 */
public class IntegerDataBufferTest extends TestCase
{
	private IntegerDataBuffer fDataBuffer;
	private int [] testArray = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private int fTestCapacity = testArray.length;
	private DataBufferDescriptor fDescriptor = 
			new DataBufferDescriptor("TestBuffer", int.class);
	
	/**
	 * Set up for test cases defined in this class.
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
			
		fDataBuffer = 
			new IntegerDataBuffer(fDescriptor, testArray, 0, fTestCapacity);
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
	 * Test copying an IntegerDataBuffer.
	 * @see IntegerDataBuffer#copy()
	 */
	public void testCopy()
	{		
		IntegerDataBuffer copyBuffer = 
			(IntegerDataBuffer) fDataBuffer.copy();
		
		assertEquals(copyBuffer.isReadOnly(), fDataBuffer.isReadOnly());
		assertEquals(copyBuffer.getSize(), fDataBuffer.getSize());

		for (int i = 0; i < copyBuffer.getSize(); i++)
		{
			assertEquals(copyBuffer.getAsInt(i), fDataBuffer.getAsInt(i));
		}
		
		// Check that they do not share array
		assertNotSame(copyBuffer.array(), fDataBuffer.array());
	}
	
	/**
	 * Test Downsampling an IntegerDataBuffer.
	 * @see IntegerDataBuffer#downsample(int)
	 */
	public void testDownsample()
	{		
		IntegerDataBuffer sampledBuffer;
		
		// --- Downsample from 1 to size of data buffer.
		for (int downsample = 1; downsample <= fDataBuffer.getSize(); downsample++)
		{
			int expectedSize = fDataBuffer.getSize() / downsample;
			sampledBuffer = (IntegerDataBuffer) fDataBuffer.downsample(downsample);
			
			assertEquals(sampledBuffer.isReadOnly(), fDataBuffer.isReadOnly());
			assertEquals(expectedSize, sampledBuffer.getSize());
	
			for (int i = 0; i < sampledBuffer.getSize(); i++)
			{
				int expectedValue = fDataBuffer.getAsInt(i * downsample);
				assertEquals(expectedValue, sampledBuffer.getAsInt(i));
			}
		}
	}
	
	/**
	 * Test getting buffer as a ByteBuffer.
	 * @see IntegerDataBuffer#asByteBuffer()
	 */
	public void testAsByteBuffer()
	{		
		ByteBuffer fByteBuffer = fDataBuffer.getAsByteBuffer();
		
		// Check that they have the same number of integer elements
		assertTrue(fByteBuffer.hasRemaining());
		assertEquals(
			fDataBuffer.getSize() * IntegerDataBuffer.BYTES_PER_ELEMENT, 
			fByteBuffer.remaining());

		for (int i = 0; i < fDataBuffer.getSize(); i++)
		{
			assertEquals(fByteBuffer.getInt(), fDataBuffer.getAsInt(i));
		}
		
		// Check that they do not share array
		assertNotSame(fByteBuffer.array(), fDataBuffer.array());
	}
	
	/**
	 * Test bulk put method using an int array.
	 * @see IntegerDataBuffer#put(int, int[], int, int)
	 */
	public void testBulkPut()
	{		
		int [] source = {10, 20, 30};
		int bufferSize = fDataBuffer.getSize();
		int bufferIndex = 0;
		DataBuffer buffer = fDataBuffer.copy();
		int copySize = source.length;
		
		// Test exception when invalid arguments are used
		
		try
		{
			buffer.put(bufferSize - copySize + 1, source, 0, copySize);
			assertTrue("Expected BufferOverflowException", false);
		}
		catch (BufferOverflowException e)
		{
			// This is expected
			assertTrue(true);
		}
		
		// Test put at front of buffer
		
		buffer.put(bufferIndex, source, 0, copySize);
		
		for (int i = 0; i < copySize; i++)
		{
			assertEquals(source[i], buffer.getAsInt(bufferIndex + i));
		}

		// Test put at end of buffer
		
		buffer = fDataBuffer.copy();
		bufferIndex = bufferSize - copySize;
		buffer.put(bufferIndex, source, 0, copySize);
		
		for (int i = 0; i < copySize; i++)
		{
			assertEquals(source[i], buffer.getAsInt(bufferIndex + i));
		}

		// Test put that spans the end of the buffer
		
		buffer = new IntegerDataBuffer(
				fDescriptor, testArray, 5, fTestCapacity);
		bufferIndex = 4;
		buffer.put(bufferIndex, source, 0, copySize);
		
		for (int i = 0; i < copySize; i++)
		{
			assertEquals(source[i], buffer.getAsInt(bufferIndex + i));
		}

		// Test put that wraps around to the front
		
		buffer = new IntegerDataBuffer(
					fDescriptor, testArray, 5, fTestCapacity);

		bufferIndex = 6;
		buffer.put(bufferIndex, source, 0, copySize);
		
		for (int i = 0; i < copySize; i++)
		{
			assertEquals(source[i], buffer.getAsInt(bufferIndex + i));
		}
	}
	
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite()
	{
		return new TestSuite(IntegerDataBufferTest.class);
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: IntegerDataBufferTest.java,v $
//  Revision 1.5  2005/07/20 22:08:12  tames_cvs
//  Added test for type specific bulk put of an int[].
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