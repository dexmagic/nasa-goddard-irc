//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/KeyedDataSelectorDescriptor.java,v 1.3 2006/06/01 22:22:43 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: KeyedDataSelectorDescriptor.java,v $
//  Revision 1.3  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.2  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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

package gov.nasa.gsfc.irc.data.selection.description;

import org.jdom.Element;

import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A KeyedDataSelectorDescriptor describes the means to select keyed data 
 * elements.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $
 * @author Carl F. Hostetter
 */

public class KeyedDataSelectorDescriptor extends AbstractDataSelectorDescriptor
{
	private Object fKey;
	

	/**
	 * Constructs a new KeyedDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new KeyedDataSelectorDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		KeyedDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		KeyedDataSelectorDescriptor		
	**/
	
	public KeyedDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new KeyedDataSelectorDescriptor for the given key Object.
	 *
	 * @param key The key that the new KeyedDataSelectorDescriptor will use to 
	 * 		select data
	**/
	
	public KeyedDataSelectorDescriptor(Object key)
	{
		fKey = key;
	}
	

	/**
	 * Constructs a (shallow) copy of the given KeyedDataSelectorDescriptor.
	 *
	 * @param descriptor The KeyedDataSelectorDescriptor to be copied
	**/
	
	protected KeyedDataSelectorDescriptor
		(KeyedDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fKey = descriptor.fKey;
	}
	

	/**
	 * Returns a (deep) copy of this KeyedDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this KeyedDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		KeyedDataSelectorDescriptor result = 
			(KeyedDataSelectorDescriptor) super.clone();
		
		result.fKey = fKey;
		
		return (result);
	}
	
	
	/**
	 *  Returns the key of the element this KeyedDataSelectorDescriptor is 
	 *  configured to select.
	 *  
	 *  @return The name of the element this KeyedDataSelectorDescriptor is 
	 *  		configured to select
	 */
	
	public Object getKey()
	{
		return (fKey);
	}
	
	
	/** 
	 *  Returns a String representation of this KeyedDataSelectorDescriptor.
	 *
	 *  @return A String representation of this KeyedDataSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("KeyedDataSelectorDescriptor:");
		result.append("\n" + super.toString());
		result.append("\nKey:  " + fKey);
		
		return (result.toString());
	}
	
				
	/**
	 * Unmarshalls a KeyedDataSelectorDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Unmarshall the key attribute (if any).
		fKey = fSerializer.loadStringAttribute(Dataselml.A_KEY, null, fElement);
		
		// If no key is specified, use the name attribute as the key
		if (fKey == null)
		{
			fKey = getName();
		}
		else
		{
			String name = getName();
			
			if ((name == null) || name.equals(HasName.DEFAULT_NAME))
			{
				setName(fKey.toString());
			}
		}
	}
}
