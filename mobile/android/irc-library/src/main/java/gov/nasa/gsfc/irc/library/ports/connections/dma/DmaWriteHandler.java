//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: DmaWriteHandler.java,v $
//	Revision 1.5  2005/03/01 23:25:15  chostetter_cvs
//	Revised Queue design and package for better blocking behavior
//	
//	Revision 1.4  2005/02/27 04:29:34  tames
//	Added logic to recognize thread interrupt in run method.
//	
//	Revision 1.3  2005/02/25 22:35:15  tames_cvs
//	Changed InterruptException handling to reset the interrupt of the Thread.
//	
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

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import gov.nasa.gsfc.commons.types.queues.FifoQueue;
import gov.nasa.gsfc.commons.types.queues.Queue;

/**
 *  The DmaWriteHandler class is a Runnable Object whose
 *  <code>run</code> method performs all data IO for the connection.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/03/01 23:25:15 $
 *  @author		Troy Ames
 **/
public class DmaWriteHandler implements Runnable
{
	//private long fCheckWaitTime = 0;
	private DmaConnection fPort = null;
	private ByteBuffer fWriteBuffer = null;
	private DmaDevice fDmaImpl = null;
	boolean fStopping = false;
	private static final int WRITE_MODE = 2;
	private int fMode = WRITE_MODE;
	// Out buffer fields
	private long fMinWriteDelay = 10;
	private float fMaxDataLag = 100.0F; // in milliseconds
	private Queue fWriteQueue = new FifoQueue();


	/**
	 *  Default constructor of an DmaIoHandler.
	 **/
	public DmaWriteHandler(DmaConnection port, DmaDevice device, ByteBuffer buffer)
	{
		fPort = port;
		fDmaImpl = device;
		fWriteBuffer = buffer;
	}


	/**
	 * Sets the maximum lag between buffer reads.
	 * 
	 * @param maximumLag
	 */
	public void setMaximumDataLag(float maximumLag)
	{
		fMaxDataLag = maximumLag;
	}
	
	/**
	 * Performs all data IO for the connection.
	 **/
	public void run()
	{
		Thread thread = Thread.currentThread();
		
		try
		{
			while (!fStopping && !thread.isInterrupted())
			{
				ByteBuffer data = (ByteBuffer) fWriteQueue.blockingRemove();

				if (data.remaining() > fWriteBuffer.capacity())
				{
					// Get a larger buffer
					fWriteBuffer =
							fDmaImpl.getWriteBuffer(data.remaining());
					fWriteBuffer.limit(fWriteBuffer.position());
				}
				copyToBuffer(fWriteBuffer, data);
				fDmaImpl.startWrite(fWriteBuffer);
				//monitorWriteStatus(fWriteBuffer);
				// Let the DMA device read the data
				Thread.sleep(fMinWriteDelay);
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Adds the data to the write queue for an asychronous DMA write.
	 * @param data Buffer to write
	 */
	public void writeData(Buffer data)
	{
		if (data != null)
		{
			fWriteQueue.add(data);
		}
	}

	/**
	 *  Copies data from the source buffer to the DMA buffer.
	 *
	 *  @param buffer The ByteBuffer to copy data into.
	 *  @param source The source buffer.
	**/
	private void copyToBuffer(ByteBuffer buffer, ByteBuffer source)
	{
		int newPosition = buffer.limit();
		int newLimit = newPosition + source.remaining();

		// Check if there is room remaining in the buffer
		if (newLimit > buffer.capacity())
		{
			// Reached end of buffer so roll over to the start.
			newPosition = 0;
			newLimit = source.remaining();
		}

		buffer.limit(newLimit);
		buffer.position(newPosition);
		buffer.mark();
		buffer.put(source);
		buffer.reset();
	}

	/**
	 *  Monitors the status of a DMA write. Returns when the DMA write is
	 *  complete or the mode of the port is changed.
	 *
	 *  @param buffer the write buffer to monitor.
	 *  @throws IOException if the DMA device generates an exception
	 *  @throws InterruptedException if the status monitor is interrupted
	**/
	private void monitorWriteStatus(Buffer buffer)
		throws IOException, InterruptedException
	{
		long startTime = System.currentTimeMillis();
		long stopTime = 0;
		long intervalTime = 0;
		int startPosition = buffer.position();
		int limit = buffer.limit();
		int transferPosition = startPosition;
		int remaining = 0;
		boolean writeInProgress = true;

		while (writeInProgress && (fMode == WRITE_MODE))
		{
			remaining = fDmaImpl.checkStatus();
			stopTime = System.currentTimeMillis();
			intervalTime = stopTime - startTime;
			startTime = stopTime;

			transferPosition = (int)(limit - remaining);

			if (remaining > 0)
			{
				Thread.sleep(
					getDmaPollInterval(
						intervalTime,
						transferPosition - startPosition,
						limit - transferPosition));
				startPosition = transferPosition;
			}
			else
			{
				writeInProgress = false;
			}
		}
	}

	/**
	 * Calculates the expected poll interval for the remainder of the DMA
	 * buffer or a maximum polling period whichever is less.
	 *
	 * @param timeInterval the time (ms) to transfer data.
	 * @param transferSize the number of elements transferred.
	 * @param remainingSize the number of elements remaining to transfer.
	 *
	 * @return predicted poll interval
	**/
	private long getDmaPollInterval(
		long timeInterval, int transferSize, int remainingSize)
	{
		float newInterval = fMaxDataLag;
		float lastInterval = (float) timeInterval;
		float dataRate;  // Bytes per millisecond

		if (transferSize > 0)
		{
			// some data was transferred
			if (lastInterval <= 0)
			{
				// prevent divide by zero exception
				lastInterval = 0.01F;
			}

			dataRate = (float) transferSize / lastInterval;
			newInterval = ((float) remainingSize / dataRate);

			if (newInterval > fMaxDataLag)
			{
				// Cannot read the remainder before exceeding the lag constraint.

				// Try to determine the most efficient poll intervals with the
				// following assumptions:
				//  1) It is better to transfer smaller similar sized blocks
				//     then several larger blocks and one very small
				//     block at the end.
				//  2) If the remainder can be transferred all at once by relaxing
				//     the lag constraint then do it.
				//  3) If the data rate is slow enough that the lag constraint
				//     needs to be enforced then we can afford this overhead.
				float remainingBlocks = Math.round(newInterval / fMaxDataLag);
				//System.out.println("getDmaPollInterval (newInterval > fMaxDataLag)"
				//	+ " newInterval=" + newInterval
				//	+ " remainingBlocks=" + remainingBlocks);

				newInterval = Math.round(newInterval / remainingBlocks);
			}
		}
		else if (timeInterval <= 0)
		{
			// Perhaps we did not sleep long enough last time to get any data
			// so try the minimum wait period
			newInterval = 1.0F;
		}

		/*
		System.out.println("getDmaReadInterval: "
			+ " timeInterval:" + timeInterval
			+ " transferSize:" + transferSize
			+ " remainingSize:" + remainingSize
			+ " estimatedInterval:" + newInterval
			);
			*/


		return (long) newInterval;
	}
} // end DmaIoHandler class

