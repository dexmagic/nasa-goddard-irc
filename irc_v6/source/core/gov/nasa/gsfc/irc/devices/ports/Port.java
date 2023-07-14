//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.devices.ports;

import gov.nasa.gsfc.irc.components.IrcComponent;
import gov.nasa.gsfc.irc.devices.events.InputMessageSource;
import gov.nasa.gsfc.irc.devices.events.OutputMessageListener;

/**
 * Port defines the minimal interface that all ports should 
 * implement. A port is an abstract connection to hardware or software
 * located outside of the enclosing application.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 04:08:02 $
 * @author		T. Ames
 */
public interface Port 
	extends IrcComponent, InputMessageSource, OutputMessageListener
{
}

//--- Development History  ---------------------------------------------------
//
//  $Log: Port.java,v $
//  Revision 1.2  2006/04/18 04:08:02  tames
//  Changed to reflect refactored Input and Output messages.
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/09/27 20:31:18  tames
//  Reflects a refactoring of port architecture.
//
//  Revision 1.9  2004/08/03 20:12:38  tames_cvs
//  changed port interface
//
//  Revision 1.7  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
//  Revision 1.6  2004/06/07 14:35:02  tames_cvs
//  Changed to extend MinimalComponent
//
//  Revision 1.5  2004/06/04 14:33:36  tames_cvs
//  Changed to extend MessageSource interface
//
//  Revision 1.4  2004/05/12 21:58:23  chostetter_cvs
//  Revisions for Descriptor changes (primarily).
//
//  Revision 1.3  2004/05/04 15:14:49  tames_cvs
//  Javadoc comments updated
//
//  Revision 1.2  2004/04/30 20:33:33  tames_cvs
//  Updated Interface to use MessageEvents
//
//  Revision 1.1  2004/04/23 20:40:18  tames_cvs
//  initial version
//
