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

package gov.nasa.gsfc.irc.library.ports.connections;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.commons.types.buffers.SingleUseBufferHandle;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;

/**
 * Connection to read lines of text from an InputStream.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:15 $
 * @author	 smaher
 */

public class TextLineInputConnection extends AbstractConnection implements
		Connection 
{
	private static final String CLASS_NAME = 
		TextLineInputConnection.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	public static final String DEFAULT_NAME = "Text Line Input Connection";

	private InputStream fTextInputStream;
	private LineNumberReader fTextInputStreamReader;

	/**
	 * The thread for reading from ports.
	 */
	private Thread fReaderThread;


	/**
	 *	Constructs a new TextLineInputConnection having a default name.
	 *
	 */
	
	public TextLineInputConnection()
	{
		this(DEFAULT_NAME);
	}
	

	/**
	 *	Constructs a new TextLineInputConnection having the given base name.
	 *  
	 *  @param name The base name of the new TextLineInputConnection
	 */
	
	public TextLineInputConnection(String name)
	{
		super(name);
	}
	

	/**
	 * Constructs a new TextLineInputConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new TextLineInputConnection
	 */
	public TextLineInputConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Sets the InputStream to be read from to the given InputStream.
	 *  
	 * @param stream The InputStream to be read from
	 */

	protected void setInputStream(InputStream stream) 
	{
		if (stream != null) 
		{
			stopReaderThread();
			
			fTextInputStream = stream;
			
			InputStreamReader reader = new InputStreamReader(fTextInputStream);
			
			fTextInputStreamReader = new LineNumberReader(reader);
			
			startReaderThread();
		}
	}

	/**
	 * Start reading lines of text from the InputStream. 
	 * A connection must be started before the 
	 * <code>write</code> method can be called and before data will be received
	 * from this ConnectionManager.
	 * 
	 */

	public void start() {
		super.start();

		startReaderThread();
	}

	/**
	 * Stops the TextLineInputConnection. A Connection that is closed will not 
	 * generate any ConnectionDataEvents.
	 * 
	 */

	public synchronized void stop() {
		super.stop();

		stopReaderThread();
	}

	/**
	 *  Causes this TextLineInputConnection to immediately cease operation and 
	 *  release any allocated resources. A killed Connection cannot subsequently 
	 *  be started or otherwise reused.
	 *  
	 */

	public void kill() {
		stop();

		super.kill();
	}

	/**
	 * Starts the server thread to listen for new Connections.
	 * 
	 */

	private void startReaderThread() {
		// If the Thread is already running skip it
		if (fReaderThread != null && fReaderThread.isAlive()) {
			return;
		}

		// Start a new TextLineReader Thread
		fReaderThread = new Thread(new TextLineReader(),
			(getFullyQualifiedName() + " Reader Thread"));

		fReaderThread.start();
	}

	/**
	 * Stops the server thread from listening for new Connections.
	 */

	private void stopReaderThread() {
		// Interrupt the Server thread, if it's running
		if (fReaderThread != null && fReaderThread.isAlive()) {
			fReaderThread.interrupt(); // Sets a flag and interrupts
			fReaderThread = null;
		}
	}

	// Utility class definition ------------------------------------------------

	/**
	 * The TextLineReader class reads data from a file and
	 * calls the <code>fireConnectionDataEvent</code> method on the Connection.
	 * <P>It will keep watching the InputStream to see if any new data is 
	 * available
	 **/

	protected class TextLineReader implements Runnable 
	{
		/**
		 * Creates a new TextLineReader.
		 *
		 */

		public TextLineReader() 
		{

		}

		public void run() 
		{
			try
			{
				Thread.sleep(1000);
				
				boolean eof = false;
				
				while (! eof) 
				{
					String line = fTextInputStreamReader.readLine();
					
					if (line != null)
					{
						ByteBuffer byteBuffer = 
							ByteBuffer.wrap(line.getBytes());

						// Send InputBufferEvent with buffer
						BufferHandle handle = 
							new SingleUseBufferHandle(byteBuffer);
						
						fireInputBufferEvent(new InputBufferEvent
							(TextLineInputConnection.this, handle));
					}
					else
					{
						eof = true;
					}
				}
			}
			catch (IOException ex) 
			{
				String message = "IOException:" + fTextInputStream;

				sLogger.logp(Level.WARNING, CLASS_NAME, "run", message, ex);
			}
			catch (Exception ex) 
			{
				String message = "Exception:" + fTextInputStream;

				sLogger.logp(Level.WARNING, CLASS_NAME, "run", message, ex);
			}
		}
	}
	

	/* 
	 * ALWAYS THROWS AN EXCEPTION because not implemented
	 * 
	 */
	
	public void process(ByteBuffer buffer) 
	{
		throw new ReadOnlyBufferException();
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: TextLineInputConnection.java,v $
//  Revision 1.3  2006/03/14 14:57:15  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.2  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//
