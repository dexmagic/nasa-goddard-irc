//=== File Prolog ============================================================
//
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

package gov.nasa.gsfc.irc.library.ports.connections.ni;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.library.ports.connections.dma.DmaDevice;
import gov.nasa.gsfc.irc.library.ports.connections.ni.daq.NiDaq;
import gov.nasa.gsfc.irc.library.ports.connections.ni.daq.NiDaqConstants;
import gov.nasa.gsfc.irc.library.ports.connections.ni.daq.NiDaqException;



/**
 *	A DMA device for the asynchronous transfer of data using the
 *  National Instruments (NI) PC-DIOHS board.
 *  <P>The configuration of this device is specified by a ConnectionDescriptor.
 *  The table below gives the configuration parameters that this device uses.
 *  If the parameter is missing then the default value will be used. If there
 *  is not a default value specified then the parameter is required to be
 *  in the PortDescriptor.
 *  <p>
 *  <center><table border="1">
 *  <tr align="center">
 *      <th>Key</th>
 *      <th>Default</th>
 *      <th>Description</th>
 *  </tr>
 *  <tr align="center">
 *      <td>Device Number</td><td>1</td>
 *      <td align="left">The device number assigned to the board</td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML port description with a NiDio32hs:
 *  <BR>
 *  <pre>
 *     &lt;Connection name="Mark2" type="DMA"&gt;
 *         &lt;Parameter name="DMA Device"
 *             value="gov.nasa.gsfc.irc.library.ports.connections.ni.NiDio32hs" /&gt;
 *         &lt;Parameter name="Buffer Size" value="10000000" /&gt;
 *         ...
 *         &lt;Parameter name="Device Number" value="1" /&gt;
 *         ...
 *     &lt;/Connection&gt;
 *  </pre>
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/01/28 21:58:25 $
 *  @author	    Troy Ames
 */
public class NiDio32hs implements NiDaqConstants, DmaDevice
{
	private static final String CLASS_NAME = NiDio32hs.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	//----------- Descriptor keys ---------------
	public static final String DEVICE_NUMBER_KEY = "Device Number";

	//----------- NiDaq Board fields ------------
	public static final short DIGITAL_LOGIC_LOW = 0;
	public static final short DIGITAL_LOGIC_HIGH = 1;
	public static final short ACTIVE_HIGH_POLARITY = 0;
	public static final short ACTIVE_LOW_POLARITY = 1;
	public static final short LEADING_EDGE_PULSED = 0;
	public static final short TRAILING_EDGE_PULSED = 1;
	public static final short BURST_PROTOCOL = 3;

	public static final short GROUP_SIZE_8 = 1;	    // 8 bits = 1 bytes
	public static final short GROUP_SIZE_16 = 2;	// 16 bits = 2 bytes
	public static final short GROUP_SIZE_32 = 4;	// 32 bits = 4 bytes
	public static final short OUTPUT_PORT = 1;
	public static final short INPUT_PORT = 0;
	public static final short PORT_A = 0;
	public static final short PORT_B = 1;
	public static final short PORT_C = 2;
	public static final short PORT_D = 3;
	public static final short NO_HANDSHAKING = 0;
	public static final short HANDSHAKING = 1;
	public static final short ACK1 = 3;
	public static final short REQ1 = 2;

	//----------- NiDaq State fields ------------
	private short fDeviceNumber = 0;
	private short fMode = UNDEFINED_MODE;
	private short fDmaPort = PORT_A;		// port used for DMA input and command output
	private short fDioPort = 4;		// port used for DIO_IF communication
	private short fInputLine = 1;	// toggeling this line sets the status to input
	private short fOutputLine = 3;	// toggeling this line sets the status to output
	private short fGroup = 1;
	private short fGroupSize = GROUP_SIZE_32;
	private short fProtocol = BURST_PROTOCOL;
	private short fEdge = LEADING_EDGE_PULSED;
	private short fRequestPolarity = ACTIVE_HIGH_POLARITY;
	private short fAckPolarity = ACTIVE_HIGH_POLARITY;
	private short fDelayTime = 0;

	private static final short UNDEFINED_MODE = 0;
	private static final short READ_MODE = 1;
	private static final short WRITE_MODE = 2;

	//----------- NiDaq Buffer Constraints --------
	private static final int MIN_BUFFER_SIZE = 1024;


	/**
	 * Creates a NiDio32hs device with the default configuration.
	**/
	public NiDio32hs()
	{
		this(null);
	}

	/**
	 * Creates a NiDio32hs device with the configuration contained in the 
	 * descriptor.
	 *
	 * @param descriptor The descriptor to use for configuration information
	**/
	public NiDio32hs(ConnectionDescriptor descriptor)
	{
		NiDaq.loadLibrary();

		if (descriptor != null)
		{
			String key = null;

			try
			{
				key = DEVICE_NUMBER_KEY;
				fDeviceNumber =
					Short.parseShort(descriptor.getParameter(key));
			}
			catch (NumberFormatException ex)
			{
				String message = 
					"NiDaqDma was given wrong or missing " + key + " parameter";

				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, ex);
			}
		}

		System.out.println("NiDaqDma created");
	}

	/**
	 * Returns a ByteBuffer that can be read from. The actual size of the
	 * buffer will be an even multiple of the group size.
	 *
	 * @param size the requested size of the buffer
	 * @returns a direct ByteBuffer
	**/
	public ByteBuffer getReadBuffer(int size)
	{
		if (size < MIN_BUFFER_SIZE)
		{
			size = MIN_BUFFER_SIZE;
		}

		// Buffer needs to be a multiple of the maximum group size
		size = size - (size % GROUP_SIZE_32);

		ByteBuffer buffer = ByteBuffer.allocateDirect(size);
		//buffer.order(ByteOrder.LITTLE_ENDIAN);

		return buffer;
	}

	/**
	 * Returns a ByteBuffer that can be written to. The actual size of the
	 * buffer will be an even multiple of the group size.
	 *
	 * @param size the requested size of the buffer
	 * @returns a direct ByteBuffer
	**/
	public ByteBuffer getWriteBuffer(int size)
	{
		if (size < MIN_BUFFER_SIZE)
		{
			size = MIN_BUFFER_SIZE;
		}

		// Buffer needs to be a multiple of the maximum group size
		size = size - (size % GROUP_SIZE_32);

		ByteBuffer buffer = ByteBuffer.allocateDirect(size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		return buffer;
	}

	/**
	 * Starts an asynchronous DMA read into the specified buffer. The NI board
	 * will begin transferring data into the buffer starting at the buffer
	 * position up to the buffer limit. The ByteBuffer passed as a parameter
	 * must be a buffer returned by the <code>getReadBuffer</code> method.
	 *
	 * @param buffer the ByteBuffer to transfer data into.
	 * @throws IOException if the NI DAQ driver generates an exception
	 * @see #getReadBuffer
	**/
	public synchronized void startRead(ByteBuffer buffer)
		throws IOException
	{
		if (fMode != READ_MODE)
		{
			configureForRead();
		}

		long start = buffer.position();
		
		// Count must be a multiple of the group size
		long count = (buffer.limit() - start)/fGroupSize;
		
		// Reset limit to match what is actually being used
		buffer.limit((int) (start + (count * fGroupSize)));

		try
		{
			NiDaq.digBlockIn(
				fDeviceNumber, fGroup, buffer, start, count);
		}
		catch(NiDaqException ex)
		{
			throw (IOException) new IOException().initCause(ex);
		}
	}

	/**
	 * Starts an asynchronous DMA write from the specified buffer. The NI board
	 * will begin transferring data from the buffer starting at the buffer
	 * position up to the buffer limit. The ByteBuffer passed as a parameter
	 * must be a buffer returned by the <code>getWriteBuffer</code> method.
	 *
	 * @param buffer the ByteBuffer to transfer data from.
	 * @throws IOException if the NI DAQ driver generates an exception
	 * @see #getWriteBuffer
	**/
	public synchronized void startWrite(ByteBuffer buffer)
		throws IOException
	{
		if (fMode != WRITE_MODE)
		{
			configureForWrite();
		}

		long start = buffer.position();
		long count = (buffer.limit() - start)/fGroupSize;
		//System.out.println("startOutput start:" + start + " count:" + count);

		try
		{
			NiDaq.digBlockOut(
				fDeviceNumber, fGroup, buffer, start, count);
		}
		catch(NiDaqException ex)
		{
			throw (IOException) new IOException().initCause(ex);
		}
	}

	/**
	 * Checks the status of a <code>startWrite</code> or <code>startRead</code>
	 * method call. The DMA transfer is completed if the returned status is 0.
	 *
	 * @returns the number of bytes yet to be transferred
	 * @throws IOException if the NI DAQ driver generates an exception
	**/
	public synchronized int checkStatus() throws IOException
	{
		int result = 0;

		try
		{
			result = (int) NiDaq.digBlockCheck(fDeviceNumber, fGroup);
		}
		catch(NiDaqException ex)
		{
			throw (IOException) new IOException().initCause(ex);
		}

		return result * fGroupSize;
	}

	/**
	 * Stops the current <code>startWrite</code> or <code>startRead</code>
	 * operation of the NI board.
	 *
	 * @throws IOException if the NI DAQ driver generates an exception
	**/
	public synchronized void stop() throws IOException
	{
		try
		{
			// Stop the current dma transfer
			NiDaq.digBlockClear(fDeviceNumber, fGroup);
		}
		catch(NiDaqException ex)
		{
			throw (IOException) new IOException().initCause(ex);
		}
	}

	/**
	 *	Configures the NI board for an asynchronous write.
	 *
     *  @throws IOException if the NI DAQ driver generates an exception
	 *  @see #startWrite
	**/
	protected synchronized void configureForWrite() throws IOException
	{
		fGroupSize = GROUP_SIZE_16;

		try
		{
			resetBoard();

			NiDaq.digOutLine(
				fDeviceNumber, fDioPort, fOutputLine, DIGITAL_LOGIC_HIGH);
			NiDaq.digOutLine(
				fDeviceNumber, fDioPort, fOutputLine, DIGITAL_LOGIC_LOW);
			NiDaq.setDaqDeviceInfo(
				fDeviceNumber, ND_CLOCK_REVERSE_MODE_GR1, ND_OFF);
			NiDaq.setDaqDeviceInfo(
				fDeviceNumber, ND_DATA_XFER_MODE_DIO_GR1,
				ND_UP_TO_1_DMA_CHANNEL);
			NiDaq.digGroupConfig(
				fDeviceNumber, fGroup, fGroupSize,
				fDmaPort, OUTPUT_PORT);
			NiDaq.digGroupMode(
				fDeviceNumber, fGroup, fProtocol, fEdge,
				fRequestPolarity, fAckPolarity, fDelayTime);
			fMode = WRITE_MODE;
		}
		catch(NiDaqException ex)
		{
			throw (IOException) new IOException().initCause(ex);
		}
	}

	/**
	 *	Configures the NI board for an asynchronous read.
	 *
	 *  @throws IOException if the NI DAQ driver generates an exception
	 *	@see #startRead
	**/
	protected synchronized void configureForRead() throws IOException
	{
		fGroupSize = GROUP_SIZE_32;

		try
		{
			resetBoard();

			NiDaq.digOutLine(
				fDeviceNumber, fDioPort, fInputLine, DIGITAL_LOGIC_HIGH);
			NiDaq.digOutLine(
				fDeviceNumber, fDioPort, fInputLine, DIGITAL_LOGIC_LOW);
			NiDaq.setDaqDeviceInfo(
				fDeviceNumber, ND_CLOCK_REVERSE_MODE_GR1, ND_ON);
			NiDaq.digGroupConfig(
				fDeviceNumber, fGroup, fGroupSize,
				fDmaPort, INPUT_PORT);
			NiDaq.digGroupMode(
				fDeviceNumber, fGroup, fProtocol, fEdge,
				fRequestPolarity, fAckPolarity, fDelayTime);
			fMode = READ_MODE;
		}
		catch(NiDaqException ex)
		{
			throw (IOException) new IOException().initCause(ex);
		}
	}

	/**
	 *  Resets the NI board.
	 *
	 *  @throws NiDaqException if the NI DAQ driver generates an exception
	**/
	protected void resetBoard() throws NiDaqException
	{
		short result = NiDaq.initDaBoards(fDeviceNumber);

		System.out.println("InitDaBoards returned: " + result);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: NiDio32hs.java,v $
//  Revision 1.5  2005/01/28 21:58:25  tames_cvs
//  Minor changes with defaults.
//
//  Revision 1.4  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.3  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/08/23 17:51:55  tames
//  Updated for version 6
//
