//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.InvalidValueException;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;


/**
 * The class provides access to information describing a field constraint. 
 * It serves as a base for more specific kinds of constraints including
 * those that deal with bit fields, ranges, and lists.
 *
 * The object is built based on information contained in an IML XML file
 * which describes the insturment being interfaced with by IRC.<P>
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2005/09/13 20:30:12 $ 
 * @author John Higinbotham   
**/

public abstract class ConstraintDescriptor extends AbstractIrcElementDescriptor
{
	//Constraint Level:
	public final static String DEFAULT_LEVEL = "default";
	
	//Constraint:
	public static final String A_LEVEL = "level";

	private Class fConstraintClass;  //Class of constrained value (Integer, Float, etc.)
	private String fLevel	;  //Constraint level 


	/**
	 * Constructs a new ConstraintDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ConstraintDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ConstraintDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ConstraintDescriptor		
	**/
	
	public ConstraintDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Dataml.N_CONSTRAINTS);
		
		fConstraintClass = ((ValueDescriptor) fParent).getTypeClass();
		
		xmlUnmarshall();
	}


	/**
	 * Default constructor for manual creation.
	**/
	public ConstraintDescriptor()
	{
		//---DEV: we may want to take the parent as a arg here to supporting getting 
		// the value class
		fConstraintClass = String.class;
		fLevel = DEFAULT_LEVEL;
	}
	

	/**
	 * Get the constraint's class.
	 *
	 * @return Constraint's class.
	 *			
	**/
	public Class getConstraintClass()
	{
		return fConstraintClass;
	}

	/**
	 * Set the constraint class.
	 *
	 * @param constraintClass Constraint class
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setConstraintClass(Class constraintClass)
	{
		fConstraintClass = constraintClass;
	}

	/**
	 * Get the constraint level property. 
	 *
	 * @return Constraint level property. 
	 *			
	**/
	public String getLevel()
	{
		return fLevel;
	}

	/**
	 * Set the constraint level.
	 *
	 * @param level Constraint level.
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setLevel(String level)
	{
		fLevel = level;
	}

	/**
	 * Validate the given value against this Constraint.
	 *
	 * @param Value to validate.
	 * @throws InvalidValueException if the given value fails to validate against 
	 * 		this Constraint
	**/
	abstract public void validateValue(Object value) 
		throws InvalidValueException;

	/**
	 * Unmarshall descriptor from XML. 
	 *
	 * @param  element   JDOM subtree root element.
	 *			
	**/
	private void xmlUnmarshall()
	{ 
		fLevel = fSerializer.loadStringAttribute(A_LEVEL, null, fElement);
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
		fSerializer.storeAttribute(A_LEVEL, fLevel, element);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ConstraintDescriptor.java,v $
//  Revision 1.4  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.3  2005/01/21 21:53:19  tames
//  Updated to reflect removal of redundant getElementClass method in
//  AbstractIrcElementDescriptor.
//
//  Revision 1.2  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.3  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//  Initial version
//
