//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: SimpleValueFormatterDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.4  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.3  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
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

import gov.nasa.gsfc.irc.data.selection.description.DataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.Dataselml;
import gov.nasa.gsfc.irc.data.transformation.DataFormatRuleType;
import gov.nasa.gsfc.irc.data.transformation.DataValueFormatType;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.Ircml;


/**
 * A SimpleValueFormatterDescriptor describes a formatter of an atomic data value.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class SimpleValueFormatterDescriptor extends AbstractDataFormatterDescriptor 
	implements ValueFormatterDescriptor
{
	private DataValueFormatType fFormat;
	private String fPattern;
	private DataFormatRuleType fRule;
	private String fValue;
	private DataSelectionDescriptor fSelection;
	
	/**
	 * Constructs a new SimpleValueFormatterDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new SimpleValueFormatterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		SimpleValueFormatterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		SimpleValueFormatterDescriptor		
	**/
	
	public SimpleValueFormatterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given SimpleValueFormatterDescriptor.
	 *
	 * @param descriptor The SimpleValueFormatterDescriptor to be copied
	**/
	
	public SimpleValueFormatterDescriptor(SimpleValueFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fFormat = descriptor.fFormat;
		fPattern = descriptor.fPattern;
		fRule = descriptor.fRule;
		fValue = descriptor.fValue;
	}
	

	/**
	 * Returns a (deep) copy of this SimpleValueFormatterDescriptor.
	 *
	 * @return A (deep) copy of this SimpleValueFormatterDescriptor 
	**/
	
	public Object clone()
	{
		SimpleValueFormatterDescriptor result = (SimpleValueFormatterDescriptor) super.clone();
		
		result.fFormat = fFormat;
		result.fPattern = fPattern;
		result.fRule = fRule;
		result.fValue = fValue;
		
		return (result);
	}
	
	
	/** 
	 *  Returns the DataValueFormatType associated with this SimpleValueFormatterDescriptor.
	 *
	 *  @return The DataValueFormatType associated with this SimpleValueFormatterDescriptor
	**/
	
	public DataValueFormatType getDataFormatType()
	{
		return (fFormat);
	}
	
	
	/** 
	 *  Returns the pattern String associated with this SimpleValueFormatterDescriptor 
	 *  (if any)
	 *
	 *  @return The pattern String associated with this SimpleValueFormatterDescriptor
	 *  		(if any)
	**/
	
	public String getPattern()
	{
		return (fPattern);
	}
	
	
	/** 
	 *  Returns the DataFormatRuleType associated with this SimpleValueFormatterDescriptor
	 *  (if any).
	 *
	 *  @return The DataFormatRuleType associated with this SimpleValueFormatterDescriptor
	 *  		(if any)
	**/
	
	public DataFormatRuleType getDataFormatRuleType()
	{
		return (fRule);
	}
	
	
	/** 
	 *  Returns the constant value String associated with this 
	 *  SimpleValueFormatterDescriptor (if any).
	 *
	 *  @return The constant value String associated with this 
	 *  	SimpleValueFormatterDescriptor(if any)
	**/
	
	public String getConstantValue()
	{
		return (fValue);
	}
	
	
	/**
	 * Returns the DataSelectionDescriptor associated with this 
	 * SimpleValueFormatterDescriptor (if any).
	 *
	 * @return The DataSelectionDescriptor associated with this 
	 * 		SimpleValueFormatterDescriptor (if any) 
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
	 *  Returns a String representation of this SimpleValueFormatterDescriptor.
	 *
	 *  @return A String representation of this SimpleValueFormatterDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("SimpleValueFormatterDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fValue != null)
		{
			result.append("\nValue: " + fValue);
		}
		else if (fSelection != null)
		{
			result.append("\nSelection: " + fSelection);
		}
		
		if (fFormat != null)
		{
			result.append("\nFormat: " + fFormat);
		}
		
		if (fPattern != null)
		{
			result.append("\nPattern: " + fPattern);
		}
		
		if (fRule != null)
		{
			result.append("\nRule: " + fRule);
		}

		return (result.toString());
	}
	
	
	/**
	 * Unmarshall a new SimpleValueFormatterDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Unmarshall the DataValueFormatType attribute (if any).
		String formatName = fSerializer.loadStringAttribute
			(Ircml.A_TYPE, DataValueFormatType.TEXT.getName(), fElement);
		
		if (formatName != null)
		{
			fFormat = DataValueFormatType.forName(formatName);
		}
		
		// Unmarshall the format value String attribute (if any).
		fValue = fSerializer.loadStringAttribute
			(Datatransml.A_VALUE, null, fElement);
		
		// Unmarshall the format pattern String attribute (if any).
		fPattern = fSerializer.loadStringAttribute
			(Datatransml.A_PATTERN, null, fElement);
		
		// Unmarshall the FormatPatternRule attribute (if any).
		String ruleName = fSerializer.loadStringAttribute
			(Datatransml.A_RULE, null, fElement);
		
		if (ruleName != null)
		{
			fRule = DataFormatRuleType.forName(ruleName);
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
