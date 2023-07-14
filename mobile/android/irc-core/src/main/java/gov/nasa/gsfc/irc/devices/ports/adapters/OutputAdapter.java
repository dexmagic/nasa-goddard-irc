//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/adapters/OutputAdapter.java,v 1.2 2004/10/16 22:34:23 chostetter_cvs Exp $
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

import gov.nasa.gsfc.irc.devices.ports.connections.OutputBufferSource;


/**
 *  An OutputAdapter transforms output data into ByteBuffers.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/10/16 22:34:23 $
 *  @author	Troy Ames
 */
public interface OutputAdapter extends PortAdapter, OutputBufferSource
{
	
}

//--- Development History  ---------------------------------------------------
//
//  $Log: OutputAdapter.java,v $
//  Revision 1.2  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/05 13:50:16  tames_cvs
//  Reflects changes made to the OutputAdapter interface and abstract
//  implementations.
//
//  Revision 1.1  2004/09/27 20:32:45  tames
//  Reflects a refactoring of Port architecture and renaming port parsers
//  to InputAdapters and port formatters to OutputAdapters.
//
//  Revision 1.9  2004/08/03 20:34:19  tames_cvs
//  Many configuration and descriptor changes
//
//  Revision 1.8  2004/07/28 18:24:16  tames_cvs
//  Changed interactions between ports and formatters.
//
//  Revision 1.7  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
//  Revision 1.6  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.5  2004/07/11 21:24:54  chostetter_cvs
//  Organized imports
//
//  Revision 1.4  2004/07/06 14:53:20  tames_cvs
//  Changed to pass ByteBuffers
//
//  Revision 1.3  2004/07/06 14:44:54  tames_cvs
//  Updated to reflect message changes
//
//  Revision 1.2  2004/05/27 16:10:34  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.1  2004/04/30 20:31:37  tames_cvs
//  Initial Version
//
