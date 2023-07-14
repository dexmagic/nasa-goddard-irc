//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: RangeConstraintDescriptor.java,v $
//  Revision 1.2  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.4  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.3  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/05/27 18:24:03  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//  Initial version
// 
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.description;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.InvalidValueException;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;

/**
 * The class provides access to information describing a range of  
 * constraints. A range constraint is simply a set of bounds that
 * are applied to a field.<P>
 *
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2004/10/14 15:16:50 $
 * @author John Higinbotham 	
**/

public class RangeConstraintDescriptor extends ConstraintDescriptor
{
	private static final String CLASS_NAME = 
		RangeConstraintDescriptor.class.getName();
	
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private String fLowStringVal;  // Low string value from IML
	private String fHighStringVal;  // High string value from IML
	private Comparable fLow;  // Low object
	private Comparable fHigh;  // High oject


	/**
	 * Constructs a new RangeConstraintDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new RangeConstraintDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		RangeConstraintDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		RangeConstraintDescriptor		
	**/
	
	public RangeConstraintDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Get the low constraint.
	 *
	 * @return Low constraint. 
	 *						
	**/
	public Comparable getLow()
	{
		return fLow;
	}

	/**
	 * Set the low constraint.
	 *
	 * @param value Low constraint. 
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setLow(Comparable value)
	{
		fLow = value; 
	}

	/**
	 * Get the high constraint. 
	 *
	 * @return High constraint. 
	 *						
	**/
	public Comparable getHigh()
	{
	  return fHigh;
	}

	/**
	 * Set the high constraint.
	 *
	 * @param value High constraint. 
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setHigh(Comparable value)
	{
		fHigh = value; 
	}

	/**
	 * Validate a value against the constraint. Throw and exception if the value 
	 * is not between included in the low/high range.
	 *
	 * @param Value to validate.
	 *			
	**/
	public void validateValue(Object value) throws InvalidValueException
	{

		if (value == null)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Passed null value";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"validateValue", message);
			}

			return;
		}

		if ( !(getLow().compareTo(value) <= 0 && getHigh().compareTo(value) >= 0) )
		{
			InvalidValueException fieldEx = new InvalidValueException(value, this,
				"Value " + value.toString() + " not between " +
				getLow().toString() + " and " + getHigh().toString() + ".");
			throw fieldEx;
		}
	}

	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	private void xmlUnmarshall()
	{
		fLowStringVal = fSerializer.loadStringAttribute
			(Dataml.A_LOW, null, fElement);
		fHighStringVal = fSerializer.loadStringAttribute
			(Dataml.A_HIGH, null, fElement);
		Class constraintClass = getConstraintClass();

		try
		{
			if (fLowStringVal != null) 
			{
				fLow  = (Comparable) fSerializer.getValueObject
					(fLowStringVal, constraintClass); 
			}
			if (fHighStringVal != null) 
			{
				fHigh = (Comparable) fSerializer.getValueObject
					(fHighStringVal, constraintClass); 
			}
		}
		catch (ClassCastException ex) 
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Range Constraint value NOT of type Comparable: " + 
						constraintClass.toString();
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"xmlUnmarshall", message, ex);
			}
		}
	}

	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into.
	 *
	**/
	public void xmlMarshall(Element element)
	{
		super.xmlMarshall(element);
		
		fSerializer.storeAttribute(Dataml.A_LOW,  fLow.toString(), element);
		fSerializer.storeAttribute(Dataml.A_HIGH, fHigh.toString(), element);
	}
}
