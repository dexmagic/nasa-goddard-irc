//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataSpanSelectionDescriptor.java,v $
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
 * A DataSpanSelectionDescriptor describes a means of selecting a span of 
 * data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class DataSpanSelectionDescriptor extends AbstractDataSelectionDescriptor
{
	/**
	 * Default constructor of a new DataSpanSelectionDescriptor.
	 *
	**/
	
	public DataSpanSelectionDescriptor()
	{

	}
	

	/**
	 * Constructs a new DataSpanSelectionDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataSpanSelectionDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataSpanSelectionDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataSpanSelectionDescriptor		
	**/
	
	public DataSpanSelectionDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataSpanSelectionDescriptor.
	 *
	 * @param descriptor The DataSpanSelectionDescriptor to be cloned
	**/
	
	protected DataSpanSelectionDescriptor(DataSpanSelectionDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DataSpanSelectionDescriptor.
	 *
	 * @return A (deep) copy of this DataSpanSelectionDescriptor
	**/
	
	public Object clone()
	{
		DataSpanSelectionDescriptor result = 
			(DataSpanSelectionDescriptor) super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this DataSpanSelectionDescriptor.
	 *
	 *  @return A String representation of this DataSpanSelectionDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new 
			StringBuffer("DataSpanSelectionDescriptor:\n" + super.toString());
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this DataSpanSelectionDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		if (fDataSelector == null)
		{
			// Unmarshall the ByteDelimitedDataSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_BYTE_DELIMITED, 
					Dataselml.C_BYTE_DELIMITED_DATA_SELECTOR, 
						fElement, this, fDirectory);
		}
		
		if (fDataSelector == null)
		{
			// Unmarshall the CharDelimitedDataSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_CHAR_DELIMITED, 
					Dataselml.C_CHAR_DELIMITED_DATA_SELECTOR, 
						fElement, this, fDirectory);
		}
		
		if (fDataSelector == null)
		{
			// Unmarshall the ByteSpanDataSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_BYTE_RANGE, 
					Dataselml.C_BYTE_RANGE_DATA_SELECTOR, 
						fElement, this, fDirectory);
		}
		
		if (fDataSelector == null)
		{
			// Unmarshall the CharSpanDataSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_CHAR_RANGE, 
					Dataselml.C_CHAR_RANGE_DATA_SELECTOR, 
						fElement, this, fDirectory);
		}
		
		if (fDataSelector == null)
		{
			// Unmarshall the RegExPatternDataSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_BY_REG_EX_PATTERN, 
					Dataselml.C_REG_EX_PATTERN_DATA_SELECTOR, 
						fElement, this, fDirectory);
		}
	}
}
