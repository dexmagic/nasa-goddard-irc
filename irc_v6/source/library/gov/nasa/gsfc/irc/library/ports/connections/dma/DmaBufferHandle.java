//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: DmaBufferHandle.java,v $
//	Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//	Initial version
//	
//	Revision 1.1  2004/08/23 16:04:53  tames
//	Ported from version 5
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
package gov.nasa.gsfc.irc.library.ports.connections.dma;

import java.nio.Buffer;

import gov.nasa.gsfc.commons.types.buffers.AbstractBufferHandle;
import gov.nasa.gsfc.commons.types.buffers.BufferHandle;


/**
 *  A DmaBufferHandle is a class that defines a region of the DMA buffer
 *  that is published for use by other classes.
 *  The DMA region referred to by an instance of this class is reclaimed
 *  when the buffer is no longer in use and the releaseBuffer
 *  method is called.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/11 21:35:46 $
 * @author	Troy Ames
 * @see AbstractBufferHandle
**/
public class DmaBufferHandle extends AbstractBufferHandle
		implements BufferHandle
{
	private DmaBufferState fParent = null;
	private Buffer fBuffer = null;
	private int fParentPosition = 0;
	private int fParentLimit = 0;

	/**
	 * Constructs a BufferHandle with the given buffer, start position,
	 * and limit.
	 *
	 * @param port	The port to associate with this buffer region.
	 * @param buffer	The parent Buffer.
	 * @param position	The index that is the start of the buffer region.
	 * @param limit 	The limit of the region.
	 * @throws IllegalArgumentException if a given argument is invalid.
	 *
	 * @see Buffer
	**/
	public DmaBufferHandle(
		DmaBufferState parent, Buffer buffer, int position, int limit)
		throws IllegalArgumentException
	{
		if (buffer == null)
		{
			throw new IllegalArgumentException("buffer argument is null");
		}
		if (position >= limit)
		{
			throw new IllegalArgumentException("buffer is empty");
		}

		fParent = parent;
		fBuffer = buffer;
		fParentPosition = position;
		fParentLimit = limit;

		/*System.out.println("DmaBufferHandle "
			+ " fParentPosition:" + fParentPosition
			+ " fParentLimit:" + fParentLimit
			+ " buffer.position:" + buffer.position()
			+ " buffer.limit:" + buffer.limit()
			);*/
	}

	/**
	 * Gets the buffer that this BufferHandle refers to. The method
	 * <code>setInUse</code> should be called before getting the buffer.
	 *
	 * @return the Buffer
	 * @see #setInUse
	**/
	public Buffer getBuffer()
	{
		return fBuffer;
	}

	/**
	 * Gets the offset of the containing buffer's position with respect
	 * to it's parent buffer.
	 *
	 * @return the offset
	**/
	public int getParentOffset()
	{
		return fParentPosition;
	}

	/**
	 *  Marks the buffer as in use to prevent it from being overwritten or
	 *  reclaimed. Should be paired with calls to <code>release</code>.
	 *  @see #release
	**/
	public synchronized void setInUse()
	{
		super.setInUse();
		//System.out.println("holdBufferSlice "
		//	+ " start:" + fParentPosition
		//	+ " limit:" + fParentLimit
		//	+ " size:" + (fParentLimit - fParentPosition));
	}

	/**
	 *  Releases buffer from use. This is only called if there are no current
	 *  users of the buffer.
	**/
	protected synchronized void releaseBuffer()
	{
		if (fBuffer != null)
		{
			fParent.releaseSlice(fParentPosition, fParentLimit);
		}
		fBuffer = null;
	}
} // end DmaBufferHandle Class
