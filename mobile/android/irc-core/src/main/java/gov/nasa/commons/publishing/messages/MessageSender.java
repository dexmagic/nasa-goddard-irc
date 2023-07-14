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

package gov.nasa.gsfc.commons.publishing.messages;



/**
 * A simple interface that can be used by implementers to indicate the 
 * capability to send a message. Potentially useful for components that 
 * delegate the actual sending of a Message to a helper class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/27 18:54:32 $
 * @author	tjames
 **/
public interface MessageSender
{
	public static final String MESSAGE_SENDER_KEY = "MessageSender";
	
	/**
	 * Causes this sender to send the given Message.
	 *
	 * @param message A Message to send
	**/
	public void sendMessage(Message message);
}


//--- Development History  ---------------------------------------------------
//
//  $Log: MessageSender.java,v $
//  Revision 1.3  2006/04/27 18:54:32  chostetter_cvs
//  Moved to commons.publishing.messages package
//
//  Revision 1.1  2006/04/18 14:00:50  tames
//  Reflects relocated MessageSender interface.
//
//  Revision 1.1  2006/04/18 03:57:06  tames
//  Relocated implementation.
//
//  Revision 1.2  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.1  2005/09/14 18:04:37  tames_cvs
//  Initial implementation.
//
//