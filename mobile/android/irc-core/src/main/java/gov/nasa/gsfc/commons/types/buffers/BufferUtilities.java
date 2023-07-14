//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
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

package gov.nasa.gsfc.commons.types.buffers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Some utilities for dealing with ByteBuffers
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/10/06 16:55:40 $
 * @author	smaher
 */

abstract public class BufferUtilities
{

	/**
	 * Makes an independent copy of the provided ByteBuffer.
	 * @param in ByteBuffer to be copied
	 * @param whether the copy should be a direct buffer
	 * @return new ByteBuffer copy
	 */
	public static ByteBuffer makeCopyOfByteBuffer(ByteBuffer in, boolean directBuffer)
	{
	    ByteBuffer buf;
	    if (directBuffer == true)
	    {
	        buf = ByteBuffer.allocateDirect(in.limit());
	    }
	    else
	    {
	        buf = ByteBuffer.allocate(in.limit());
	    }
	    buf.order(in.order());
	    buf.put(in);
	    buf.flip();
	    return buf;
	}
	
	/**
	 * Make copy of byte buffer using it's default "direct" disposition.
	 * @param in
	 * @return
	 */
	public static ByteBuffer makeCopyOfByteBuffer(ByteBuffer in)
	{
	    return makeCopyOfByteBuffer(in, in.isDirect());
	}
	
	/**
	 * Merge remaining contents of buffers into a new ByteBuffer.  
	 * @param in1 ByteBuffer to be merged, may be null
	 * @param in2 ByteBuffer to be merged, may be null
	 * @param whether the new buffer should be a direct buffer
	 * @return new ByteBuffer merge
	 */
	public static ByteBuffer mergeByteBuffers(ByteBuffer in1, ByteBuffer in2, boolean directBuffer)
	{
	    ByteBuffer newBuf = null;
	    if (in1 == null && in2 == null)
	    {
	    	throw new IllegalArgumentException("Buffer arguments are both null");
	    }
	    if (in1 != null && in2 != null && in1.order() != in2.order())
	    {
	    	throw new IllegalArgumentException("Buffers must use the same byte ordering");
	    }
	    ByteOrder order = (in1 == null ? in2.order() : in1.order());
	    int size = (in1 == null ? 0 : in1.remaining()) + (in2 == null ? 0 : in2.remaining()) ;
	    
	    if (directBuffer == true)
	    {
	        newBuf = ByteBuffer.allocateDirect(size);
	    }
	    else
	    {
	        newBuf = ByteBuffer.allocate(size);
	    }
	    
	    newBuf.order(order);
	    
	    if (in1 != null)
		{
			newBuf.put(in1);
		}
	    if (in2 != null)
		{
			newBuf.put(in2);
		}
	    newBuf.flip();
	    return newBuf;
	}	
}



//--- Development History  ---------------------------------------------------
//
//$Log: BufferUtilities.java,v $
//Revision 1.5  2005/10/06 16:55:40  smaher_cvs
//Added merge buffers.
//
//Revision 1.4  2004/12/17 21:06:57  smaher_cvs
//Comments
//
//Revision 1.3  2004/11/05 18:51:32  smaher_cvs
//Added flexibility with direct buffers.  Also set order() of buffer to be the same.
//
//Revision 1.2  2004/10/12 20:43:29  smaher_cvs
//Added option for DirectBuffer in makeCopyOfByteBuffer
//
//Revision 1.1  2004/10/06 17:56:41  smaher_cvs
//Initial
//