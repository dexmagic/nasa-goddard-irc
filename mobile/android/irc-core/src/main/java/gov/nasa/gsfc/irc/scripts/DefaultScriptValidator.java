//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 580 
//  for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.scripts;

import java.util.Iterator;

import gov.nasa.gsfc.irc.data.InvalidValueException;
import gov.nasa.gsfc.irc.data.Values;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;


/**
 * A ScriptValidator validates Scripts against their associated
 * ScriptDescriptors, ensuring that arguments are present and valid.
 *
 * @version		$Date: 2005/01/20 08:10:07 $
 * @author		Troy Ames
 */

public class DefaultScriptValidator implements ScriptValidator
{
	/**
	 * Constructs a new ScriptValidator
	 */
	public DefaultScriptValidator()
	{
	}

	/**
	 * Validate a Script against its Descriptor.
	 *
	 * @param	script		The script to be validated
	 *
	 * @throws InvalidScriptException	The script is not valid
	 */
	public void validate(Script script)
		throws InvalidScriptException
	{
		validate(script, (ScriptDescriptor) script.getDescriptor(), null);
	}

	/**
	 * Validate a Script against its Descriptor but it only validates
	 * the constraints at a specific constraint level such as instrument
	 * versus operational.
	 *
	 * @param script The script to be validated
	 * @param level The level at which to validate the constraints
	 * @throws InvalidScriptException	The script is not valid
	 */
	public void validate(Script script, String level)
		throws InvalidScriptException
	{
		validate(script, (ScriptDescriptor) script.getDescriptor(), level);
	}

	/**
	 * Validate a Script against a specified Descriptor.
	 *
	 * @param script The script to be validated
	 * @param descriptor The Descriptor against which to validate
	 * @param level The level at which to validate the constraints
	 * @throws InvalidScriptException	If the script is not valid
	 */
	public void validate(Script script, ScriptDescriptor descriptor, String level)
		throws InvalidScriptException
	{
		// If there's no descriptor, assume the script is valid
		if (descriptor != null)
		{
			boolean isValid = true;
			String error = new String();
			
			Iterator arguments = descriptor.getArguments().iterator();

			// loop over each script attribute. Check to see if it is valid.
			// If it is invalid a script is added to the error string
			// contained in the exception that will then be thrown. If
			// all attributes are valid nothing happens. If the script
			// is invalid an exception is created, dispatched to the 
			// ExceptionDispatcher and thrown.
			
			while (arguments.hasNext())
			{
				FieldDescriptor argument = (FieldDescriptor) arguments.next();
				
				Object value =  script.get(argument.getName());
				
				if (value == null || value.toString().equals(""))
				{
					error += argument.getName() + " is required.\n";

					isValid = false;
				}
				else 
				{
					try
					{
						Values.validate(value, argument.getConstraints(), level);
					}
					catch (InvalidValueException ex)
					{
						ex.printStackTrace();
						error += ex.getMessage() + "\n";

						isValid = false;
					}
				}
			}
			
			if (!isValid)
			{
				 InvalidScriptException ex =  new InvalidScriptException(error);
				 
				 throw (ex);
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultScriptValidator.java,v $
//  Revision 1.4  2005/01/20 08:10:07  tames
//  Removed stack trace
//
//  Revision 1.3  2005/01/07 20:46:24  tames
//  Removed obsolete references to an argument descriptor since a
//  script uses the field descriptor.
//
//  Revision 1.2  2004/10/01 15:47:41  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.1  2004/08/11 05:42:57  tames
//  Script support
//
//  Revision 1.1  2004/08/09 17:26:36  tames_cvs
//  Initial version
//
