//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataFeatureSelectionDescriptor.java,v $
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
 * A DataFeatureSelectionDescriptor describes a means of selecting a 
 * feature of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class DataFeatureSelectionDescriptor 
	extends AbstractDataSelectionDescriptor
{
	/**
	 * Default constructor of a new DataFeatureSelectionDescriptor.
	 *
	**/
	
	public DataFeatureSelectionDescriptor()
	{

	}
	

	/**
	 * Constructs a new DataFeatureSelectionDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataFeatureSelectionDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataFeatureSelectionDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataFeatureSelectionDescriptor		
	**/
	
	public DataFeatureSelectionDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataFeatureSelectionDescriptor.
	 *
	 * @param descriptor The DataFeatureSelectionDescriptor to be cloned
	**/
	
	protected DataFeatureSelectionDescriptor
		(DataFeatureSelectionDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DataFeatureSelectionDescriptor.
	 *
	 * @return A (deep) copy of this DataFeatureSelectionDescriptor
	**/
	
	public Object clone()
	{
		DataFeatureSelectionDescriptor result = 
			(DataFeatureSelectionDescriptor) super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this DataFeatureSelectionDescriptor.
	 *
	 *  @return A String representation of this DataFeatureSelectionDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer
			("DataFeatureSelectionDescriptor:\n" + super.toString());
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this DataFeatureSelectionDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		if (fDataSelector == null)
		{
			// Unmarshall the ClassSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_CLASS, 
					Dataselml.C_CLASS_SELECTOR, fElement, this, fDirectory);
		}
		
		if (fDataSelector == null)
		{
			// Unmarshall the CounterDataSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_COUNTER, 
					Dataselml.C_COUNTER_DATA_SELECTOR, fElement, this, fDirectory);
		}
		
		if (fDataSelector == null)
		{
			// Unmarshall the DataNameSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_DATA_NAME, 
					Dataselml.C_DATA_NAME_SELECTOR, fElement, this, fDirectory);
		}
	}
}
