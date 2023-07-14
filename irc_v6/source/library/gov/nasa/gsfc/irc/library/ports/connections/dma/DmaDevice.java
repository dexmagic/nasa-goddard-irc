//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DmaDevice.java,v $
//  Revision 1.1  2004/08/23 16:04:53  tames
//  Ported from version 5
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
import java.nio.ByteBuffer;

/**
 *	An interface to implementations of DMA controllers and devices. Classes
 *  that implement this interface may have additional constraints on when these
 *  methods can be called.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/08/23 16:04:53 $
 *  @author		Troy Ames
 */
public interface DmaDevice
{
	/**
	 * Returns a ByteBuffer that can be read from.
	 *
	 * @param capacity the requested size of the buffer
	 * @returns a ByteBuffer
	**/
	public ByteBuffer getReadBuffer(int capacity);

	/**
	 * Returns a ByteBuffer that can be written to.
	 *
	 * @param capacity the requested size of the buffer
	 * @returns a ByteBuffer
	**/
	public ByteBuffer getWriteBuffer(int capacity);

	/**
	 * Starts a DMA read into the specified buffer. The DMA device will begin
	 * transferring data into the buffer starting at the buffer position up to
	 * the buffer limit.
	 *
	 * @param buffer the ByteBuffer to transfer data into.
	 * @throws IOException if the DMA device generates an exception
	**/
	public void startRead(ByteBuffer buffer) throws IOException;

	/**
	 * Starts a DMA write from the specified buffer. The DMA device will begin
	 * transferring data from the buffer starting at the buffer position up to
	 * the buffer limit.
	 *
	 * @param buffer the ByteBuffer to transfer data from.
	 * @throws IOException if the DMA device generates an exception
	**/
	public void startWrite(ByteBuffer buffer) throws IOException;

	/**
	 * Checks the Read or Write status of the DMA device. The DMA transfer is
	 * completed if the returned status is 0.
	 *
	 * @returns the number of bytes yet to be transferred
	 * @throws IOException if the DMA device generates an exception
	**/
	public int checkStatus() throws IOException;

	/**
	 * Stops the current Read or Write operation of the DMA device.
	 *
	 * @throws IOException if the DMA device generates an exception
	**/
	public void stop() throws IOException;
}
