//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/adapters/InputAdapter.java,v 1.4 2006/01/23 17:59:50 chostetter_cvs Exp $
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

package gov.nasa.gsfc.irc.devices.ports.adapters;

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferListener;

/**
 * An InputAdapter transforms raw input data into an IRC internal form.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:50 $
 * @author	 Troy Ames
 */

public interface InputAdapter extends PortAdapter, InputBufferListener
{
	/**
	 * Causes this InputAdapter to process the specified buffer.
	 * If the result of processing the buffer is a Message then the
	 * implementing class should return it. Otherwise this method should
	 * return null.
	 *
	 * @param handle BufferHandle to a buffer to be processed
	 * @return a result Object or null.
	 * 
	 * @throws InputException
	 */
	
	public Object process(BufferHandle handle) throws InputException;
}

//--- Development History  ---------------------------------------------------
//
//  $Log: InputAdapter.java,v $
//  Revision 1.4  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2005/02/04 21:45:08  tames_cvs
//  Changed signature of the process method to allow a return result.
//
//  Revision 1.2  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/09/28 21:53:29  tames_cvs
//  InputAdapters now do not have to be sources of InputMessageEvents.
//
//  Revision 1.1  2004/09/27 20:32:45  tames
//  Reflects a refactoring of Port architecture and renaming port parsers
//  to InputAdapters and port formatters to OutputAdapters.
//
//  Revision 1.3  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/04/30 20:31:53  tames_cvs
//  Initial Version
//
