//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/messages/MessageValidator.java,v 1.6 2006/04/18 04:16:01 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: MessageValidator.java,v $
//	Revision 1.6  2006/04/18 04:16:01  tames
//	Relocated and refactored Message related classes.
//	
//	Revision 1.5  2004/08/09 17:26:58  tames_cvs
//	Changed to an interface
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
import gov.nasa.gsfc.irc.messages.description.AbstractMessageDescriptor;

/**
 * A MessageValidator validates Messages against their associated
 * MessageDescriptors, ensuring that arguments are present and valid.
 *
 * @version		$Date: 2006/04/18 04:16:01 $
 * @author		Troy Ames
 */
public interface MessageValidator
{
	/**
	 * Validate a Message against its Descriptor.
	 *
	 * @param	message		The message to be validated
	 * @throws InvalidMessageException	The message is not valid
	 */
	public void validate(Message message) throws InvalidMessageException;

	/**
	 * Validate a Message against its Descriptor but it only validates
	 * the constraints at a specific constraint level such as instrument
	 * versus operational.
	 *
	 * @param	message		The message to be validated
	 * @param	level		The level at which to validate the constraints
	 * @throws InvalidMessageException	The message is not valid
	 */
	public void validate(Message message, String level)
			throws InvalidMessageException;

	/**
	 * Validate a Message against a specified Descriptor at a specific
	 * level of constraints such as instrument or operational.  To test at
	 * all levels, the level can be null.
	 *
	 * @param	message		The message to be validated
	 * @param	descriptor	The Descriptor against which to validate
	 * @param	level		The level of the constraints to check
	 * @throws InvalidMessageException	The message is not valid
	 */
	public void validate(Message message, AbstractMessageDescriptor descriptor,
			String level) throws InvalidMessageException;
}