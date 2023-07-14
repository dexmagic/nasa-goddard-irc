//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

import java.nio.Buffer;
import java.nio.ByteBuffer;



/**
 *  A simple default implementation of the BufferHandle interface.
 * 
 *  <p>Users should mark the buffer as in use with the <code>setInUse</code>
 *  method before getting access to the buffer region using the
 *  <code>getBuffer</code> method.
 *  Users should notify the implementing class when the buffer is no longer
 *  needed by releasing the buffer handle with the <code>release</code> method.
 *  Multiple calls of <code>setInUse</code> should be paired with
 *  an equal number of <code>release</code> method calls in order for the
 *  buffer region to be reclaimed.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/02/28 23:58:10 $
 *  @author		T. Ames/588
**/

public class DefaultBufferHandle extends AbstractBufferHandle
{
	private ByteBuffer fBuffer;
	
	
	/**
	 * Constructs a new BufferHandle with the given buffer.
	 * 
	 * @param buffer the buffer to wrap by this handle.
	 */
	public DefaultBufferHandle(ByteBuffer buffer)
	{
		fBuffer = buffer;
	}
	
	/**
	 * Gets the buffer that this BufferHandle refers to. The method
	 * <code>setInUse</code> should be called before getting the buffer.
	 *
	 * @return a read only version of the Buffer
	 * @see #setInUse
	**/
	public Buffer getBuffer()
	{
		return fBuffer.asReadOnlyBuffer();
	}

	/**
	 * Gets the offset of the containing buffer's position with respect
	 * to it's parent buffer. This implementation always returns 0 since
	 * there is not a parent buffer.
	 *
	 * @return the offset
	**/
	public int getParentOffset()
	{
		return 0;
	}

	/**
	 *  Releases buffer from use. This is only called if there are no current
	 *  users of the buffer. This implementation does nothing.
	**/
	protected void releaseBuffer()
	{
		// Do nothing here;
	}
}

//--- Development History ----------------------------------------------------
//
// $Log: DefaultBufferHandle.java,v $
// Revision 1.1  2005/02/28 23:58:10  tames
// Initial version
//
//
