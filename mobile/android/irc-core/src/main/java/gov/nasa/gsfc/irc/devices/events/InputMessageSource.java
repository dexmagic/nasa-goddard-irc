//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/events/InputMessageSource.java,v 1.1 2006/04/18 04:05:56 tames Exp $
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

package gov.nasa.gsfc.irc.devices.events;


/**
 * Interface for classes that are sources of <code>InputMessageEvent</code>
 * objects.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 04:05:56 $
 * @author	Troy Ames
 */
public interface InputMessageSource
{
	/**
	 * Registers a listener for a {@link InputMessageEvent} from this source.
	 * 
	 * @param listener the InputMessageEvent listener to register
	 */
	public void addInputMessageListener(InputMessageListener listener);

	/**
	 * Unregisters a listener for InputMessageEvent objects from this source.
	 * 
	 * @param listener the InputMessageEvent listener to unregister
	 */
	public void removeInputMessageListener(InputMessageListener listener);
	
	/**
	 * Returns all the registered listeners to this source
	 * 
	 * @return an array of InputListeners
	 */
	public InputMessageListener[] getInputMessageListeners();
}

//--- Development History  ---------------------------------------------------
//
//  $Log: InputMessageSource.java,v $
//  Revision 1.1  2006/04/18 04:05:56  tames
//  Relocated and refactored Input and Output messages.
//
//  Revision 1.1  2004/09/27 20:13:24  tames
//  Reflects changes to message event types and message handling.
//
//
