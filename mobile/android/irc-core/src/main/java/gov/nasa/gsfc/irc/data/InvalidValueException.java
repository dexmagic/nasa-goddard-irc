//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/InvalidValueException.java,v 1.2 2005/01/11 21:35:46 chostetter_cvs Exp $
//
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: InvalidValueException.java,v $
//  Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
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

package gov.nasa.gsfc.irc.data;

import gov.nasa.gsfc.irc.app.IrcException;
import gov.nasa.gsfc.irc.data.description.ConstraintDescriptor;
import gov.nasa.gsfc.irc.data.description.DataMapEntryDescriptor;


/**
 * An InvalidValueException indicates a validation problem with a constrained value.
 *
 * @version $Date: 2005/01/11 21:35:46 $
 * @author Carl F. Hostetter
**/

public class InvalidValueException extends IrcException
{
	private ConstraintDescriptor fConstraint;
	private Object fValue;
	

	/**
	 * Create a new InvalidValueException.
	 *
	 * @param value Value that caused exception. 
	 * @param constraint Constraint used to validate field value. 
	 * @param message Text describing the constraint violation.	
	 *
	 */
	public InvalidValueException(Object value, ConstraintDescriptor constraint, 
		String message)
	{
		super(message);
		fConstraint = constraint;
		fValue = value;
	}

	/**
	 * Get the error message string for this InvalidFieldValueException.
	 *
	 * @return  String - The error message string
	 */
	public String getMessage()
	{
		DataMapEntryDescriptor fd = (DataMapEntryDescriptor) fConstraint.getParent();
		StringBuffer message = new StringBuffer(super.getMessage());
		
		if (fd != null)
		{
			message.append("(Field name = " + fd.getName() + "; ");
		}
		if (fConstraint.getName() != null)
		{
			message.append("Constraint name = " + fConstraint.getName() + ")");
		}
		else 
		{
			message.append("Constraint name = <unnamed>)");
		}

		return message.toString();
	}

	/**
	 * Get the constraint descriptor which was used to validate the field value. 
	 *
	 * @return  String - The error message string
	 */
	public ConstraintDescriptor getConstraintDescriptor()
	{
		return fConstraint;
	}

	/**
	 * Get the invalid field value that caused this exception.
	 *
	 * @return value that caused exception. 
	 */
	public Object getValue()
	{
		return fValue;
	}
}
