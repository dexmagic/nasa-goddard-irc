//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: ValueFormatDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

package gov.nasa.gsfc.irc.data.transformation.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A ValueFormatDescriptor describes a means of formatting a value.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class ValueFormatDescriptor extends AbstractDataFormatDescriptor
{
	/**
	 * Constructs a new ValueFormatDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ValueFormatDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ValueFormatDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ValueFormatDescriptor		
	**/
	
	public ValueFormatDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given ValueFormatDescriptor.
	 *
	 * @param descriptor The ValueFormatDescriptor to be copied
	**/
	
	protected ValueFormatDescriptor(ValueFormatDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this ValueFormatDescriptor.
	 *
	 * @return A (deep) copy of this ValueFormatDescriptor
	**/
	
	public Object clone()
	{
		ValueFormatDescriptor result = (ValueFormatDescriptor) super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this ValueFormatDescriptor.
	 *
	 *  @return A String representation of this ValueFormatDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("ValueFormatDescriptor: ");
		result.append("\n" + super.toString());
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this ValueFormatDescriptor from its associated JDOM Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Currently, a value format can be either switched, as a list, or 
		// as a simple value.
		
		if (fDataFormatter == null)
		{
			// Unmarshall the SwitchedValueFormatterDescriptor (if any).
			fDataFormatter = (SwitchedValueFormatterDescriptor) 
				fSerializer.loadSingleChildDescriptorElement(Datatransml.E_SWITCH, 
					Datatransml.C_SWITCHED_VALUE_FORMATTER, fElement, this, fDirectory);
		}
		
		if (fDataFormatter == null)
		{
			// Unmarshall the ListValueFormatterDescriptor (if any).
			fDataFormatter = (ListValueFormatterDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Datatransml.E_LIST, 
					Datatransml.C_LIST_VALUE_FORMATTER, fElement, this, fDirectory);
		}
		
		if (fDataFormatter == null)
		{
			// Unmarshall the SimpleValueFormatterDescriptor.
			fDataFormatter = new SimpleValueFormatterDescriptor
				(fParent, fDirectory, fElement);
		}
	}
}
