//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/Values.java,v 1.1 2004/10/01 15:47:40 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Values.java,v $
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

import java.util.Iterator;

import gov.nasa.gsfc.irc.data.description.ConstraintDescriptor;


/**
 *  The Values class provides utility methods for validating and otherwise 
 *  manipulating values.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2004/10/01 15:47:40 $
 *  @author Carl F. Hostetter
 */

public class Values
{
	/**
	 * Validates the given Object value against the set of constraints described by
	 * the given Iterator of ConstraintDescriptors.
	 *
	 * @param value The value Object to be validated
	 * @param descriptors An Iterator of ConstrainDescriptors
	 * @throws InvalidValueException If the given value does not validate against 
	 * 		the given set of constraints
	 */
	
	public static void validate(Object value, Iterator constraints)
		throws InvalidValueException
	{
		// Loop through the constraints and validate 
		while (constraints.hasNext())
		{
			ConstraintDescriptor constraint = 
				(ConstraintDescriptor) constraints.next();

			validate(value, constraint);
		}
	}
	

	/**
	 * Validates the given Object value against the set of constraints described by
	 * the given Iterator of ConstraintDescriptors that are also at the specified 
	 * Constraint level
	 *
	 * @param value The value Object to be validated
	 * @param descriptors An Iterator of ConstrainDescriptors
	 * @param level The level of Constraints to validate against
	 * @throws InvalidValueException If the given value does not validate against 
	 * 		the given set of constraints
	 */
	
	public static void validate(Object value, Iterator constraints, String level)
		throws InvalidValueException
	{
		if (level != null)
		{
			// Loop through the constraints and validate against those that are at 
			// the specified level.
			while (constraints.hasNext())
			{
				ConstraintDescriptor constraint = 
					(ConstraintDescriptor) constraints.next();
				
				if (level.equals(constraint.getLevel()))
				{
					validate(value, constraint);
				}
			}
		}
		else
		{
			validate(value, constraints);
		}
	}
	

	/**
	 * Validates the given Object value against the set of constraints described by
	 * the given Iterator of ConstraintDescriptors.
	 *
	 * @param value The value Object to be validated
	 * @param constraint	The ConstraintDescriptor against which to validate the 
	 * 		given value
	 * @throws InvalidValueException If the given value does not validate against 
	 * 		the given constraint
	 */
	 
	public static void validate(Object value, ConstraintDescriptor constraint)
		throws InvalidValueException
	{
		constraint.validateValue(value);
	}
}
