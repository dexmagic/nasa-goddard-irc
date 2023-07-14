//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: ValueParseDescriptor.java,v $
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
 * A ValueParseDescriptor describes a means of parsing a value.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class ValueParseDescriptor extends AbstractDataParseDescriptor
{
	/**
	 * Constructs a new ValueParseDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ValueParseDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ValueParseDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ValueParseDescriptor		
	**/
	
	public ValueParseDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given ValueParseDescriptor.
	 *
	 * @param descriptor The ValueParseDescriptor to be copied
	**/
	
	protected ValueParseDescriptor(ValueParseDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this ValueParseDescriptor.
	 *
	 * @return A (deep) copy of this ValueParseDescriptor
	**/
	
	public Object clone()
	{
		ValueParseDescriptor result = (ValueParseDescriptor) super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this ValueParseDescriptor.
	 *
	 *  @return A String representation of this ValueParseDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("ValueParseDescriptor: ");
		result.append("\n" + super.toString());
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this ValueParseDescriptor from its associated JDOM Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Currently, a value parse can be either switched, as a list, or 
		// as a simple value.
		
		if (fDataParser == null)
		{
			// Unmarshall the SwitchedValueParserDescriptor (if any).
			fDataParser = (SwitchedValueParserDescriptor) 
				fSerializer.loadSingleChildDescriptorElement(Datatransml.E_SWITCH, 
					Datatransml.C_SWITCHED_VALUE_PARSER, fElement, this, fDirectory);
		}
		
		if (fDataParser == null)
		{
			// Unmarshall the ListValueParserDescriptor (if any).
			fDataParser = (ListValueParserDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Datatransml.E_LIST, 
					Datatransml.C_LIST_VALUE_PARSER, fElement, this, fDirectory);
		}
		
		if (fDataParser == null)
		{
			// Unmarshall the SimpleValueFormatterDescriptor.
			fDataParser = new SimpleValueParserDescriptor
				(fParent, fDirectory, fElement);
		}
	}
}
