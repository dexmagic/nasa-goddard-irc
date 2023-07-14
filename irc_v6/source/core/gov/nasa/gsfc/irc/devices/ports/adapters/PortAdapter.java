//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/adapters/PortAdapter.java,v 1.4 2006/01/23 17:59:50 chostetter_cvs Exp $
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

import gov.nasa.gsfc.irc.components.ManagedComponent;
import gov.nasa.gsfc.irc.components.IrcComponent;


/**
 * PortAdapter is an interface on common port adapter behavior.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:50 $
 * @author	 Troy Ames
 */

public interface PortAdapter extends ManagedComponent, IrcComponent
{
	
}

//--- Development History  ---------------------------------------------------
//
//  $Log: PortAdapter.java,v $
//  Revision 1.4  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2004/11/28 16:55:04  tames
//  Updated to reflect change in the ManagedComponent interface.
//
//  Revision 1.2  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/09/27 20:32:45  tames
//  Reflects a refactoring of Port architecture and renaming port parsers
//  to InputAdapters and port formatters to OutputAdapters.
//
//
