//=== File Prolog ============================================================
//
// This code was developed by NASA, Goddard Space Flight Center, Code 580
// for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.library.ports.connections.dma;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 *  The DmaReadHandler class is a Runnable Object whose
 *  <code>run()</code> method performs all data input for the connection.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/02/27 04:29:34 $
 *  @author		Troy Ames
 **/
public class DmaReadHandler implements Runnable
{
	private boolean DEBUG=false;
	//private long fCheckWaitTime = 0;
	private DmaConnection fConnection = null;
	private DmaBufferState fReadBufferState = null;
	private ByteBuffer fReadBuffer = null;
	private int fMinReadSize = 124;
	private float fMaxDataLag = 100.0F; // in milliseconds
	private DmaDevice fDmaImpl = null;
	boolean fStopping = false;


	// The average read size for the DMA Device over time
	private int fAverageSize = 0;
	// The average time the DMA device took to read the DMA
	private long fAverageTime = 0;

	// log the DMA Buffer size, file writer
	private File fDumpFile = null;
	private FileWriter fDumpWriter = null;

	/**
	 *  Default constructor of an DmaIoHandler.
	 **/
	public DmaReadHandler(DmaConnection port, DmaDevice device, DmaBufferState buffer)
	{
		fConnection = port;
		fDmaImpl = device;
		fReadBufferState = buffer;
		fReadBuffer = buffer.getByteBuffer();
	}

	// write more at one time..  performance increase...
	private void dumpBufferSize(int bufferSize)
	{
		try 
		{
			if (fDumpFile == null)
			{
				fDumpFile = File.createTempFile("BufferSizes", ".txt", new File("."));
				fDumpWriter = new FileWriter(fDumpFile);
				System.out.println("#### File Created: " + fDumpFile.getPath());
			}
			
			fDumpWriter.write(Integer.toString(bufferSize)+"\t "+Long.toString(System.currentTimeMillis())+"\n");

			fDumpWriter.flush();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Set the minimum size for buffer reads.
	 * 
	 * @param minimumSize
	 */
	public void setMinimumReadSize(int minimumSize)
	{
		fMinReadSize = minimumSize;
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
	 *  Continuously reads data while in read mode or writes data while in
	 *  write mode.
	 **/
	public void run()
	{
		Thread thread = Thread.currentThread();
		
		try
		{
			while (!fStopping && !thread.isInterrupted())
			{
				if (fReadBufferState.getAvailableSize()
					< (fMinReadSize * 2))
				{
					// DMA is full, wait for a release or mode change
					synchronized (fReadBufferState)
					{
						if (fReadBufferState.getAvailableSize()
							< (fMinReadSize * 2))
						{
							//System.out.println("DMA Buffer is full - waiting...");
							fReadBufferState.wait();
							//System.exit(0);
						}
					}
				}
				else
				{
					fReadBufferState.updateBufferConstraints(fMinReadSize);
					
					if (DEBUG) {
						// write the current DMA buffer size to disk...
						dumpBufferSize(fReadBufferState.getAvailableSize());
					}
					
					// Initiate an asynchronous transfer of data
					fDmaImpl.startRead(fReadBuffer);

					monitorReadStatus(fReadBuffer);
				}
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
	 *  Monitors the status of a DMA read. Returns when the DMA read is
	 *  complete or the mode of the port is changed.
	 *
	 *  @param buffer the read buffer to monitor.
	 *  @throws IOException if the DMA device generates an exception
	 *  @throws InterruptedException if the status monitor is interrupted
	**/
	private void monitorReadStatus(Buffer buffer)
		throws IOException, InterruptedException
	{
		long startTime = System.currentTimeMillis();
		long stopTime;
		int startPosition = buffer.position();
		int limit = buffer.limit();
		int currentPosition = startPosition;
		int remaining;
		boolean readInProgress = true;
		int numReads = 1; // include previous average

		// Assume the rate of the DMA has not changed since the last read
		// and use the previous statistics for the first sleep before polling
		// the DMA.
		long intervalTime = fAverageTime;
		int bytesRead = fAverageSize;

		while (readInProgress && !fStopping)
		{
			// Determine how long it will take to complete the remainder of
			// the DMA and Sleep before polling the DMA again.
			Thread.sleep(getDmaPollInterval(
					intervalTime, bytesRead, limit - currentPosition));

			remaining = fDmaImpl.checkStatus();
			stopTime = System.currentTimeMillis();
			intervalTime = stopTime - startTime;
			startTime = stopTime;
			currentPosition = (int)((limit - remaining) - ( (limit-remaining)%fMinReadSize));

			if (currentPosition > startPosition)
			{
				// Publish available data in chunks
				fConnection.publishBuffer(
					fReadBufferState.getBufferHandle(startPosition, currentPosition));
			} else {
				continue;
			}

			/*
			System.out.println("monitorReadStatus:"
				+ " startPosition:" + startPosition
				+ " currentPosition:" + currentPosition
				+ " limit:" + limit
				+ " bytesRead:" + (currentPosition - startPosition)
				+ " intervalTime:" + intervalTime
				+ " remaining:" + remaining
				);
			*/

			if (remaining > 0)
			{
				bytesRead = currentPosition - startPosition;
				startPosition = currentPosition;

				// Accumulate read statistics
				fAverageSize += bytesRead;
				fAverageTime += intervalTime;
				numReads++;
			}
			else
			{
				//System.out.println("monitorReadStatus: Read complete");

				// This DMA read has completed
				readInProgress = false;

				// Update read statistics
				if (numReads == 1)
				{
					// Since we read the entire buffer at one time we did
					// not get any reliable read statistics.
					// The read time statistics might be too long, so
					// reduce the average by a small percentage. Eventually
					// the statistics will stabilize around the correct averages.
					fAverageTime = (long) (fAverageTime * 0.95);
				}
				else
				{
					fAverageTime /= numReads;
				}

				fAverageSize /= numReads;

				/*System.out.println("monitorReadStatus: "
					+ " fAverageSize:" + fAverageSize
					 + " fAverageTime:" + fAverageTime);
				*/
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

//--- Development History  ---------------------------------------------------
//
// $Log: DmaReadHandler.java,v $
// Revision 1.5  2005/02/27 04:29:34  tames
// Added logic to recognize thread interrupt in run method.
//
// Revision 1.4  2005/02/25 22:34:57  tames_cvs
// Changed InterruptException handling to reset the interrupt of the Thread.
//
// Revision 1.3  2005/02/11 23:16:49  mn2g
// Read data in multiples of the min read size
//
// Revision 1.2  2004/09/05 13:30:18  tames
// *** empty log message ***
//
// Revision 1.1  2004/08/23 16:04:53  tames
// Ported from version 5
//
// Revision 1.8  2004/03/01 22:04:19  chostetter_cvs
// Added veto ability for PropertyManager property changes. Organized imports.
//
// Revision 1.7  2004/03/01 22:01:57  chostetter_cvs
// Added veto ability for PropertyManager property changes.
// Organized imports.
//
// Revision 1.6  2004/03/01 21:59:36  chostetter_cvs
// Added veto ability for PropertyManager property changes.
// Organized imports.
//
// Revision 1.5  2003/12/05 02:15:50  mnewcomb_cvs
// changed file formatting.
//
// Revision 1.4  2003/12/05 01:48:08  mnewcomb_cvs
// Adds some debugging code that lets you log the DMA buffer
// size with a time tag.  Should be useful in debugging the
// data rate
//
// Revision 1.3  2003/09/25 15:09:36  tames_cvs
// Minimum read size parameter is now used.
//
//	1    JDK 1.4 Upgrade1.0         3/19/2002 10:01:37 AMTroy Ames
//