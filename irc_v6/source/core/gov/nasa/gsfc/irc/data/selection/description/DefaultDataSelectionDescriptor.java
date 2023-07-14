//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataSelectionDescriptor.java,v $
//  Revision 1.2  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.8  2006/04/27 23:31:09  chostetter_cvs
//  Added support for field value selection in determinants
//
//  Revision 1.7  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.6  2006/04/11 16:31:58  chostetter_cvs
//  Fixed dual-level selection syntax handling
//
//  Revision 1.5  2006/03/31 21:57:38  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.4  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
//
//  Revision 1.3  2005/09/15 15:34:08  chostetter_cvs
//  Added support for command counter value selection
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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
 * A DefaultDataSelectionDescriptor describes the means of selecting data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $ 
 * @author Carl F. Hostetter   
**/

public class DefaultDataSelectionDescriptor extends AbstractDataSelectionDescriptor
{
	/**
	 * Default constructor of a new DefaultDataSelectionDescriptor.
	 *
	**/
	
	public DefaultDataSelectionDescriptor()
	{

	}
	

	/**
	 * Constructs a new DefaultDataSelectionDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by 
	 * unmarshalling from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DefaultDataSelectionDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DefaultDataSelectionDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DefaultDataSelectionDescriptor		
	**/
	
	public DefaultDataSelectionDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DefaultDataSelectionDescriptor.
	 *
	 * @param descriptor The DefaultDataSelectionDescriptor to be cloned
	**/
	
	protected DefaultDataSelectionDescriptor
		(DefaultDataSelectionDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DefaultDataSelectionDescriptor.
	 *
	 * @return A (deep) copy of this DefaultDataSelectionDescriptor
	**/
	
	public Object clone()
	{
		DefaultDataSelectionDescriptor result = 
			(DefaultDataSelectionDescriptor) super.clone();
		
		return (result);
	}
	
	
	/**
	  * Unmarshalls this DefaultDataSelectionDescriptor from its associated JDOM 
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
			// Unmarshall the DataNameSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_FIELD_VALUE, 
					Dataselml.C_DATA_VALUE_SELECTOR, fElement, this, fDirectory);
		}
		
		if (fDataSelector == null)
		{
			// Unmarshall the KeyedDataSelectorDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_BY_KEY, 
					Dataselml.C_KEYED_DATA_SELECTOR, fElement, this, fDirectory);
		}

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
		
		if (fDataSelector == null)
		{
			// Unmarshall the DataConcatenationDescriptor (if any).
			fDataSelector = (DataSelectorDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_CONCATENATION, 
					Dataselml.C_DATA_CONCATENATION, 
						fElement, this, fDirectory);
		}
	}
}
