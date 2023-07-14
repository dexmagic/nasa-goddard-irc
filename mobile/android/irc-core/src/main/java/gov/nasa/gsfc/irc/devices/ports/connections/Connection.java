//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/connections/Connection.java,v 1.6 2006/01/23 17:59:55 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.devices.ports.connections;

import java.io.IOException;
import java.nio.ByteBuffer;

import gov.nasa.gsfc.irc.components.ManagedComponent;
import gov.nasa.gsfc.irc.components.IrcComponent;


/**
 *	A Connection is a link between two processes or devices. It can
 *  be written to, started, and stopped.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/01/23 17:59:55 $
 *  @author	Troy Ames
 */
public interface Connection 
	extends ManagedComponent, IrcComponent, OutputBufferListener, 
		InputBufferSource, ConnectSource
{
	/**
	 * Process the contents of the Buffer with respect to the connection(s).
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @throws IOException if the process fails.
	 */
	public void process(ByteBuffer buffer) throws IOException;
}

//--- Development History  ---------------------------------------------------
//
//  $Log: Connection.java,v $
//  Revision 1.6  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2005/05/04 17:14:38  tames_cvs
//  Added ConnectSource to this interface.
//
//  Revision 1.4  2005/02/07 05:06:33  tames
//  Moved InputBufferEvent related methods to new InputBufferSource
//  Interface.
//
//  Revision 1.3  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.2  2004/11/28 16:55:21  tames
//  Updated to reflect change in the ManagedComponent interface.
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.3  2004/09/27 20:33:45  tames
//  Reflects a refactoring of Connection input and output events.
//
//  Revision 1.2  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
//  Revision 1.1  2004/04/30 20:30:43  tames_cvs
//  Initial Version
//
