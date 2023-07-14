//=== File Prolog ============================================================
//
//	SocketConnectionHandler.java
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

import java.net.Socket;

/**
 * The SocketConnectionHandler interface accepts socket connections.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/11/28 23:16:16 $
 * @author	Troy Ames
**/
public interface SocketConnectionHandler
{
	/**
	 * Accepts a socket connection.
	 *
	 * @param socket    the socket to manage.
	 */
    void acceptConnection(Socket connection);
}

//--- Development History  ---------------------------------------------------
//
//	$Log: SocketConnectionHandler.java,v $
//	Revision 1.1  2005/11/28 23:16:16  tames
//	Added from previous version of IRC. Renamed to be more descriptive.
//	
//
//	 1    IRC       1.0         7/3/2001 11:51:31 AM Troy Ames
//	
