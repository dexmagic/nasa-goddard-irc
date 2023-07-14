//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataContainerSelectionDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
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

package gov.nasa.gsfc.irc.data.selection.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataContainerSelectionDescriptor describes a means of selecting a 
 * container of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class DataContainerSelectionDescriptor 
	extends AbstractDataSelectionDescriptor
{
	/**
	 * Default constructor of a new DataContainerSelectionDescriptor.
	 *
	**/
	
	public DataContainerSelectionDescriptor()
	{

	}
	

	/**
	 * Constructs a new DataContainerSelectionDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataContainerSelectionDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataContainerSelectionDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataContainerSelectionDescriptor		
	**/
	
	public DataContainerSelectionDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataContainerSelectionDescriptor.
	 *
	 * @param descriptor The DataContainerSelectionDescriptor to be cloned
	**/
	
	protected DataContainerSelectionDescriptor(DataContainerSelectionDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DataContainerSelectionDescriptor.
	 *
	 * @return A (deep) copy of this DataContainerSelectionDescriptor
	**/
	
	public Object clone()
	{
		DataContainerSelectionDescriptor result = (DataContainerSelectionDescriptor) super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this DataContainerSelectionDescriptor.
	 *
	 *  @return A String representation of this DataContainerSelectionDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("DataContainerSelectionDescriptor:\n" + 
			super.toString());
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this DataContainerSelectionDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		if (fDataSelector == null)
		{
			// Unmarshall the DataNameSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_FIELD_VALUE, 
					Dataselml.C_DATA_VALUE_SELECTOR, fElement, this, fDirectory);
		}
		
		if (fDataSelector == null)
		{
			// Unmarshall the NamedDataSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_BY_NAME, 
					Dataselml.C_NAMED_DATA_SELECTOR, fElement, this, fDirectory);
		}
		
		if (fDataSelector == null)
		{
			// Unmarshall the KeyedDataSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_BY_FIELD_NAME, 
					Dataselml.C_KEYED_DATA_SELECTOR, fElement, this, fDirectory);
		}
		
		if (fDataSelector == null)
		{
			// Unmarshall the KeyedDataSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_BY_KEY, 
					Dataselml.C_KEYED_DATA_SELECTOR, fElement, this, fDirectory);
		}
	}
}
