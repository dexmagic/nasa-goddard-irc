//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 580 
//  for the Instrument Remote Control (IRC) project.
//--- Notes ------------------------------------------------------------------
//
//--- Development History:
//
//	$Log: InvalidMessageException.java,v $
//	Revision 1.3  2006/04/18 04:16:01  tames
//	Relocated and refactored Message related classes.
//	
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/07/06 14:44:25  tames_cvs
//	General message refactoring
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
package gov.nasa.gsfc.irc.messages;

import gov.nasa.gsfc.commons.publishing.messages.Message;


/**
 * An InvalidMessageException indicates a validation problem with a
 * command.  TBD: This may eventually need to return more info about
 * the kind of validation problem.
 *
 * @version		$Date: 2006/04/18 04:16:01 $
 * @author		Troy Ames
**/
public class InvalidMessageException extends MessageException
{
	/**
	 * Create a new InvalidMessageException.
	 *
	 * @param message	The message which triggered the exception.
	 * @param error		The error message
	 */
	public InvalidMessageException(Message messageObject, String error)
	{
		super(messageObject, error);
	}
}
