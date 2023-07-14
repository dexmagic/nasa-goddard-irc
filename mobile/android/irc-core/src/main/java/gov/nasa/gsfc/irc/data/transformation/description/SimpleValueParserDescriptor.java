//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: SimpleValueParserDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.3  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
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

package gov.nasa.gsfc.irc.data.transformation.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.description.Dataml;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.Dataselml;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A SimpleValueParserDescriptor describes a parser of an atomic data value.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class SimpleValueParserDescriptor extends AbstractDataParserDescriptor 
	implements ValueParserDescriptor
{
	private boolean fIsAscii = false;
	private String fValue;
	private DataSelectionDescriptor fSelection;
	
	/**
	 * Constructs a new SimpleValueParserDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new SimpleValueParserDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		SimpleValueParserDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		SimpleValueParserDescriptor		
	**/
	
	public SimpleValueParserDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given SimpleValueParserDescriptor.
	 *
	 * @param descriptor A SimpleValueParserDescriptor
	**/
	
	public SimpleValueParserDescriptor(SimpleValueParserDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this SimpleValueParserDescriptor.
	 *
	 * @return A (deep) copy of this SimpleValueParserDescriptor 
	**/
	
	public Object clone()
	{
		SimpleValueParserDescriptor result = 
				(SimpleValueParserDescriptor) super.clone();
		
		return (result);
	}

	
	/**
	 * Returns the constant value parse String of this SimpleValueParserDescriptor 
	 * (if any).
	 *
	 * @return The constant value parse String of this SimpleValueParserDescriptor 
	 * 		(if any) 
	**/
	
	public String getConstantValue()
	{
		return (fValue);
	}

	
	/**
	 * Returns true if this ValueParseDescriptor is configured to treat the 
	 * char data it parses as ASCII, false otherwise.
	 *
	 * @return True if this ValueParseDescriptor is configured to treat the 
	 * 		char data it parses as ASCII, false otherwise 
	**/
	
	public boolean isAscii()
	{
		return (fIsAscii);
	}

	
	/**
	 * Returns the DataSelectionDescriptor associated with this 
	 * SimpleValueParserDescriptor (if any).
	 *
	 * @return The DataSelectionDescriptor associated with this 
	 * 		SimpleValueParserDescriptor (if any) 
	**/
	
	public DataSelectionDescriptor getSelection()
	{
		DataSelectionDescriptor result = null;
		
		if (fValue == null)
		{
			result = fSelection;
		}
		
		return (fSelection);
	}

	
	/** 
	 *  Returns a String representation of this SimpleValueParserDescriptor.
	 *
	 *  @return A String representation of this SimpleValueParserDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("SimpleValueParserDescriptor: ");
		result.append("\n" + super.toString());

		if (fValue != null)
		{
			result.append("\nValue: " + fValue);
			
			result.append("\nIs ASCII: " + fIsAscii);
		}
		else if (fSelection != null)
		{
			result.append("\nSelection: " + fSelection);
		}
		
		return (result.toString());
	}
	
	
	/**
	 * Unmarshall a new SimpleValueFormatterDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Unmarshall the parse value String attribute (if any).
		fValue = fSerializer.loadStringAttribute
			(Datatransml.A_VALUE, null, fElement);
		
		// Unmarshall the type attribute (if any).
		String type = getType();
		
		if (type != null)
		{
			if (type.equals(Dataml.ASCII))
			{
				fIsAscii = true;
			}
			else
			{
				fIsAscii = false;
			}
		}
		else
		{
			fIsAscii = true;
		}
		
		if (fValue == null)
		{
			// Unmarshall the DataSelectionDescriptor (if any).
			fSelection = (DataSelectionDescriptor) fSerializer.
				loadSingleChildDescriptorElement(Dataselml.E_SELECTION, 
					Dataselml.C_DATA_SELECTION, fElement, this, fDirectory);
		}
	}
}
