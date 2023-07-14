//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: DmaBufferState.java,v $
//	Revision 1.2  2005/02/11 23:16:49  mn2g
//	Read data in multiples of the min read size
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

import java.nio.ByteBuffer;

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;

/**
 * Maintains the current state of a DMA buffer.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/02/11 23:16:49 $
 * @author	Troy Ames
**/
public class DmaBufferState
{
	private int fAvailPosition = 0;
	private int fAvailLimit = 0;
	private int fCapacity = 0;
	private int fFilledSize = 0;
	private ByteBuffer fBuffer = null;

	/**
	 *  Default constructor of a DmaState.
	 **/
	public DmaBufferState(ByteBuffer buffer)
	{
		fBuffer = buffer;
		fAvailPosition = fBuffer.position();
		fAvailLimit = fBuffer.limit();
		fCapacity = fBuffer.capacity();
	}

	/**
	 * Get the available space in the DMA buffer.
	 *
	 * @return the available buffer size as int
	 **/
	public synchronized int getAvailableSize()
	{
		//System.out.println("getAvailableBufferSize:" + (fCapacity - fFilledSize)
		//	+ " position:" + fAvailPosition + " limit:" + fAvailLimit);
		//System.out.print("DMA: "
		//	+ (100 - (int)(((float)(fCapacity - fFilledSize) / fCapacity) * 100.0)) + "%"
		//	+ " Available:" + (fCapacity - fFilledSize) + " Bytes "
		//	+ " position:" + fAvailPosition + " limit:" + fAvailLimit);
		//	+ "			                     \r");

		return fCapacity - fFilledSize;
	}

	/**
	 *  Releases the buffer slice and notifies the Thread waiting for
	 *  available buffer space.
	 *
	 *  @param start the start of the buffer slice.
	 *  @param limit the limit of the buffer slice.
	 **/
	public synchronized void releaseSlice(int start, int limit)
	{
		fAvailLimit = limit;
		fFilledSize -= limit - start;

		//System.out.println("releaseBufferSlice " + " start:" + start
		//	+ " limit:" + limit + " size:" + (limit - start));

		// Notify waiting threads that buffer space was released
		notifyAll();
	}

	/**
	 *  Hold the buffer slice.
	 *
	 *  @param start the start of the buffer slice.
	 *  @param limit the limit of the buffer slice.
	 **/
	public synchronized void holdSlice(int start, int limit)
	{
		// Mark DMA buffer region as in use
		fAvailPosition = limit;
		fFilledSize += limit - start;

		//System.out.println("holdBufferSlice " + " start:" + start
	 	//	+ " limit:" + limit + " size:" + (limit - start));
	}

	/**
	 * Sets the buffer position and limit constraints for the next read.
	 *
	 * @param buffer the Buffer to transfer data from.
	 * @throws InterruptedException
	 **/
	public synchronized void updateBufferConstraints(int minSize)
			throws InterruptedException
	{
		// Use temp variables to avoid race conditions
		int position = fAvailPosition;
		int limit = fAvailLimit;

		// Adjust DMA buffer constraints for next transfer.
		if (limit <= position)
		{
			// Available DMA wraps around to start of buffer
			if ((fBuffer.capacity() - position) >= minSize)
			{
				// Use end of buffer
				limit = fBuffer.capacity();
			}
			else
			{
				// Use beginning of buffer
				position = 0;
			}
		}

		// Set DMA constraints to use remainder of the buffer
		fBuffer.limit(limit- ((limit-position)%minSize));
		fBuffer.position(position);

		/*
		System.out.println("updateBufferAvailableConstraints:"
			+ " buffer.position:" + buffer.position()
			+ " fAvailPosition:" + fAvailPosition
			+ " buffer.limit:" + buffer.limit()
			+ " fAvailLimit" + fAvailLimit
			);
		*/
	}

	/**
	 *  Returns a BufferHandle with a view on a Buffer with the given
	 *  position and limit.
	 *
	 *  @param buffer the parent Buffer
	 *  @param position the start position
	 *  @param limit the end limit
	 *  @returns a BufferHandle
	**/
	public BufferHandle getBufferHandle(int position, int limit)
	{
		// Update DMA available limits
		holdSlice(position, limit);

		// TBD: It might be more efficient to pool BufferHandles instead of
		// creating new ones
		fBuffer.limit(limit);
		fBuffer.position(position);

		// Create a view on the parent buffer
		ByteBuffer bufferSlice = ((ByteBuffer) fBuffer).slice();
		bufferSlice.order(((ByteBuffer) fBuffer).order());

		return new DmaBufferHandle(this, bufferSlice, position, limit);
	}

	protected ByteBuffer getByteBuffer()
	{
		return fBuffer;
	}
} // end DmaState Class
