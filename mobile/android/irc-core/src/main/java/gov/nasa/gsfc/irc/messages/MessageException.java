//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/messages/MessageException.java,v 1.4 2006/04/18 04:16:01 tames Exp $
//
//	This code was developed by NASA Goddard Space Flight Center, Code 580 
//  for the Instrument Remote Control (IRC) project.
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


package gov.nasa.gsfc.irc.messages;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.irc.app.IrcException;



/**
 * This is the abstract superclass of all exceptions having to do with
 * problems with IrcMessages in the message pipeline.
 *
 * @version		$Date: 2006/04/18 04:16:01 $
 * @author		Troy Ames
**/
public abstract class MessageException extends IrcException
{
	private Message fMessage;


	/**
	 * Create a new MessageException.
	 *
	 * @param message	The message which triggered the exception.
	 * @param error		The error message
	 */
	public MessageException(Message message, String error)
	{
		super(error);
		fMessage = message;
	}

	/**
	 * Create a new MessageException.
	 *
	 * @param message	The message which triggered the exception.
	 */
	public MessageException(Message message)
	{
		fMessage = message;
	}


	/**
	 * Set the message which triggered this exception.
	 *
	 * @param	message	The triggering message
	 */
	public void setMessageObject(Message message)
	{
		fMessage = message;
	}

	/**
	 * Retrieve the message which triggered this exception.
	 *
	 * @return	Message - The triggering message
	 */
	public Message getMessageObject()
	{
		return fMessage;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MessageException.java,v $
//  Revision 1.4  2006/04/18 04:16:01  tames
//  Relocated and refactored Message related classes.
//
//  Revision 1.3  2005/02/02 19:26:52  tames_cvs
//  Comment changes only.
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/07/06 14:44:25  tames_cvs
//  General message refactoring
//
