//=== File Prolog ============================================================
//
//	ServerSocketManager.java
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

package gov.nasa.gsfc.commons.system.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * The ServerSocketManager class listens for client connections and
 * calls the <code>acceptConnection</code> method on the registered handler.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2006/03/24 20:31:31 $
 * @author	Troy Ames
**/
public class ServerSocketManager implements Runnable
{
	private ServerSocket fServerSocket = null;
	private SocketConnectionHandler fHandler = null;
	private int fServerPort = 9000;
	private Thread fThread = null;

	/**
	 * Creates a new ServerSocketManager for the given port.
	 *
	 * @param handler   the client connection handler for this server socket.
	 * @param port      the port to listen for connections on.
	 */
	public ServerSocketManager(SocketConnectionHandler handler, int port)
	{
		fHandler = handler;
		fServerPort = port;
	}

	/**
	 * Causes this manager to start waiting for client connections.
	 */
	public void start()
	{
		//---If the Thread is already running skip it
		if(fThread == null || fThread.isInterrupted())
		{
			try
			{
				fServerSocket = new ServerSocket(fServerPort);
				fThread = new Thread(this);
				fThread.start();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Causes this manager to stop waiting for client connections. Interrupts 
	 * the server thread and closses the socket.
	 */
	public void stop()
	{
		//---Interrupt server thread, if it's running
		if(fThread != null && fThread.isAlive())
		{
			fThread.interrupt();
		}
	}

	/**
	 * Listens for client connections and calls the <code>acceptConnection</code>
	 * method of the registered handler.
	 *
	 * @see SocketConnectionHandler
	 */
	public void run()
	{
		try
		{
			while (!fThread.isInterrupted())
			{
				//System.out.println("Waiting for a connection ...");
				Socket socket = fServerSocket.accept();
				if (socket != null)
				{
					Date date = new Date();
					
					System.out.println(date.toString()+
						" Connection made from " + socket.getInetAddress());

					// Give connection to handler for this client
					fHandler.acceptConnection(socket);
				}
			}

			fServerSocket.close();
		}
		catch (IOException e)
		{
			System.out.println("IOException in accept(): " + e);
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: ServerSocketManager.java,v $
//	Revision 1.3  2006/03/24 20:31:31  rfl
//	Added Date.toString for connection println.
//	
//	Revision 1.2  2005/11/30 19:15:38  tames
//	Changed stop method to interrupt thread instead of just setting a flag.
//	
//	Revision 1.1  2005/11/28 23:16:16  tames
//	Added from previous version of IRC. Renamed to be more descriptive.
//	
//
//	 1    IRC       1.0         7/3/2001 11:51:41 AM Troy Ames
//	
//
