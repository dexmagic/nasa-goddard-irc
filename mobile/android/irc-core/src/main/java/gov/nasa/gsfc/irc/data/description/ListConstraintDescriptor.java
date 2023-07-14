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
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.description;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.InvalidValueException;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * The class provides access to information describing a list 
 * of constraint choices that can be applied to a field. <P>
 *
 * The object is built based on information contained in an IML XML file
 * which describes the insturment being interfaced with by IRC.
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2005/01/20 08:10:50 $
 * @author John Higinbotham   
**/

public class ListConstraintDescriptor extends ConstraintDescriptor
{
	private static final String CLASS_NAME = 
		ListConstraintDescriptor.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	private Map fChoicesByName;
	private ArrayList fChoiceValues;
	private ArrayList fChoiceNames;
	
	/**
	 * Constructs a new ListConstraintDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ListConstraintDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ListConstraintDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ListConstraintDescriptor		
	**/
	
	public ListConstraintDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		fChoicesByName = new LinkedHashMap();
		fChoiceNames = new ArrayList();
		fChoiceValues = new ArrayList();
		
		xmlUnmarshall();
	}
	
	/**
	 * Gets the choice descriptor mapped to the specified name.
	 * 
	 * @param key name of choice descriptor
	 * @return the desccriptor
	 */
	public ChoiceValueDescriptor getChoiceDescriptor(Object key)
	{
		return (ChoiceValueDescriptor) fChoicesByName.get(key);
	}
	
	/**
	 * Convenience method to get all the constraint choice values.
	 *
	 * @return All of the constraint choice values. 
	**/
	public Iterator getChoiceValues()
	{
		return fChoiceValues.iterator();
	}

	/**
	 * Convenience method to get all the constraint choice display names.
	 *
	 * @return All of the constraint choice display names. 
	**/
	public Iterator getChoiceDisplayNames()
	{
		return fChoiceNames.iterator();
	}

	/**
	 * Get the collection of constraint choice descriptors.
	 *
	 * @return All of the constraint choices. 
	**/
	public Iterator getChoiceDescriptors()
	{
		return fChoicesByName.values().iterator();
	}

	/**
	 * Get the collection of constraint choice keys that can be used to 
	 * get a specific descriptor using the <code>getChoiceDescriptor</code>
	 * method.
	 *
	 * @return All of the constraint choice keys. 
	**/
	public Iterator getChoiceKeys()
	{
		return fChoicesByName.keySet().iterator();
	}

	/**
	 * Validate a value against the constraint. Checks to see if the
	 * object is contained in a list of values.
	 *
	 * @param Value to validate.
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

		if (!fChoiceValues.contains(value))
		{
			InvalidValueException fieldEx = new InvalidValueException(value, this,
				"Value " + value.toString() + " not contained in list.");
			throw fieldEx;
		}
	}

	/**
	 * Unmarshall descriptor from XML.
	 *  
	 */
	private void xmlUnmarshall()
	{
		fSerializer.loadChildDescriptorElements(Dataml.E_CHOICE,
			fChoicesByName, Dataml.C_CHOICE, fElement, this, fDirectory);
		
		Class constraintClass = getConstraintClass();

		// Initialize convenience caches
		Iterator choices = fChoicesByName.values().iterator();
		
		while (choices.hasNext())
		{
			ChoiceValueDescriptor descriptor = 
				(ChoiceValueDescriptor) choices.next();

			fChoiceNames.add(descriptor.getDisplayName());

			Object value  = 
				fSerializer.getValueObject(descriptor.getValue(), constraintClass); 
			fChoiceValues.add(value);
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
		
//		fSerializer.storeListElement
//			(Dataml.E_CHOICE, Dataml.A_VALUE, fChoices, element);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ListConstraintDescriptor.java,v $
//  Revision 1.6  2005/01/20 08:10:50  tames
//  Changes to support choice descriptors
//
//  Revision 1.5  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
//
//  Revision 1.4  2004/10/15 17:24:46  jhiginbotham_cvs
//  Fixed list constraint so that choices would be correctly unmarshalled.
//
//  Revision 1.3  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
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
