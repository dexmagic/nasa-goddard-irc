//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/messages/DefaultMessageValidator.java,v 1.4 2006/04/18 04:16:01 tames Exp $
//
//	This code was developed by NASA Goddard Space Flight Center, Code 580 
//  for the Instrument Remote Control (IRC) project.
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultMessageValidator.java,v $
//  Revision 1.4  2006/04/18 04:16:01  tames
//  Relocated and refactored Message related classes.
//
//  Revision 1.3  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.1  2004/08/09 17:26:36  tames_cvs
//  Initial version
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

import java.util.Iterator;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.irc.data.InvalidValueException;
import gov.nasa.gsfc.irc.data.Values;
import gov.nasa.gsfc.irc.messages.description.AbstractMessageDescriptor;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;
import gov.nasa.gsfc.irc.messages.description.MessageDescriptor;


/**
 * A MessageValidator validates Messages against their associated
 * MessageDescriptors, ensuring that arguments are present and valid.
 *
 * @version		$Date: 2006/04/18 04:16:01 $
 * @author		Troy Ames
 */
public class DefaultMessageValidator implements MessageValidator
{
	/**
	 * Constructs a new MessageValidator.
	 */	
	public DefaultMessageValidator()
	{		
	}

	/**
	 * Validate a Message against its Descriptor.
	 *
	 * @param	message		The message to be validated
	 *
	 * @throws InvalidMessageException	The message is not valid
	 */
	public void validate(Message message)
		throws InvalidMessageException
	{
		MessageDescriptor descriptor = 
			(MessageDescriptor) message.getProperty(MessageKeys.DESCRIPTOR);

		validate(message, descriptor, null);
	}

	/**
	 * Validate a Message against its Descriptor but it only validates
	 * the constraints at a specific constraint level such as instrument
	 * versus operational.
	 *
	 * @param	message		The message to be validated
	 * @param	level		The level at which to validate the constraints
	 *
	 * @throws InvalidMessageException	The message is not valid
	 */
	public void validate(Message message, String level)
		throws InvalidMessageException
	{
		MessageDescriptor descriptor = 
			(MessageDescriptor) message.getProperty(MessageKeys.DESCRIPTOR);

		validate(message, descriptor, level);
	}

	/**
	 * Validate a Message against a specified Descriptor at a specific
	 * level of constraints such as instrument or operational.  To test at
	 * all levels, the level can be null.
	 *
	 * @param	message		The message to be validated
	 * @param	descriptor	The Descriptor against which to validate
	 * @param	level		The level of the constraints to check
	 *
	 * @throws InvalidMessageException	The message is not valid
	 */
	public void validate(Message message, AbstractMessageDescriptor descriptor,
						String level)
		throws InvalidMessageException
	{
		// If there's no descriptor, assume the message is valid
		if (descriptor == null)
		{
		}
		else
		{
			boolean isValid = true;
			String errorstring = new String();
			
			Iterator fields = descriptor.getEntries().iterator();

			// loop over each message attribute. Check to see if it is valid.
			// If it is invalid a message is added to the error string
			// contained in the exception that will then be thrown. If
			// all attributes are valid nothing happens. If the message
			// is invalid an exception is created, dispatched to the 
			// ExceptionDispatcher and thrown.
			while(fields.hasNext())
			{
				FieldDescriptor field = (FieldDescriptor) fields.next();
				Object value =  message.get(field.getName());
				if((value == null || value.toString().equals(""))
					&& field.isRequired())
				{
					errorstring = errorstring 
								+ field.getName() + " is required.\n";

					isValid = false;

				}
				else 
				{
					try
					{
						if (level == null)
						{
							Values.validate(value, field.getConstraints());
						}
						else
						{
							Values.validate(value, field.getConstraints(), level);
						}
					}
					catch (InvalidValueException fieldex)
					{
						errorstring = errorstring 
									+ fieldex.getMessage()
									+ "\n";

						isValid = false;
					}
				}
			}
			if(!isValid)
			{
				 InvalidMessageException invalidex =  
				 		new InvalidMessageException(message,errorstring);
				 throw invalidex;
			}
		}
	}
}
