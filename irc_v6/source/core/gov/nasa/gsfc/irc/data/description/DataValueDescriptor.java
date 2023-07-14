//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataValueDescriptor.java,v $
//  Revision 1.4  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.3  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.2  2005/02/14 22:06:01  chostetter_cvs
//  Revised data formatting, includes binary formatting with mask
//
//  Revision 1.1  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.irc.data.description;

import org.jdom.Element;

import gov.nasa.gsfc.commons.types.arrays.BitArray;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;


/**
 * A DataValueDescriptor describes a data value.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2005/09/30 20:55:47 $ 
 * @author Carl F. Hostetter 
**/

public class DataValueDescriptor extends AbstractIrcElementDescriptor
{
	private Object fValue;
	
	private String fPropertyName;
	
	
	/**
	 * Constructs a new DataValueDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element within the indicated namespace.
	 *
	 * @param parent The parent Descriptor of the new DataValueDescriptor
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataValueDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataValueDescriptor		
	 * @param namespace The namespace to which the new DataValueDescriptor 
	 * 		should belong		
	**/
	
	public DataValueDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element, String namespace)
	{
		super(parent, directory, element, namespace);
		
		xmlUnmarshall();
	}
	
	
	/**
	 * Constructs a new DataValueDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataValueDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataValueDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataValueDescriptor		
	**/
	
	public DataValueDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		this(parent, directory, element, Dataml.N_DATA);
	}
	

	/**
	 * Constructs a new DataValueDescriptor having the given Object value.
	 *
	 * @param value The Object value of the new DataValueDescriptor
	**/
	
	public DataValueDescriptor(Object value)
	{
		fValue = value;
	}
	
	
	/**
	 * Sets the Object value of this DataValueDescriptor to the given Object.
	 *
	 * @param value The new Object value of this DataValueDescriptor
	**/
	
	public void setValue(Object value)
	{
		fValue = value;
	}
	
	
	/**
	 * Returns the Object value of this DataValueDescriptor (if any).
	 *
	 * @return The Object value of this DataValueDescriptor (if any)
	**/
	
	public Object getValue()
	{
		return (fValue);
	}
	
	
	/**
	 * Returns the String value of this DataValueDescriptor (if any).
	 *
	 * @return The String value of this DataValueDescriptor (if any)
	**/
	
	public String getValueAsString()
	{
		String result = null;
		
		if (fValue != null)
		{
			result = fValue.toString();
		}
		
		return (result);
	}
	
	
	/**
	 * Returns the value of this StringValueDescriptor (if any) as a Long.
	 *
	 * @return The value of this StringValueDescriptor (if any) as a Long
	**/
	
	public Long getValueAsInteger()
	{
		return (new Long(getValueAsString()));
	}
	
	
	/**
	 * Returns the value of this StringValueDescriptor (if any) as a Double.
	 *
	 * @return The value of this StringValueDescriptor (if any) as a Double
	**/
	
	public Double getValueAsReal()
	{
		return (new Double(getValueAsString()));
	}
	
	
	/**
	 * Returns the value of this StringValueDescriptor (if any) as a BitArray.
	 *
	 * @return The value of this StringValueDescriptor (if any) as a BitArray
	**/
	
	public BitArray getValueAsBinary()
	{
		return (new BitArray(getValueAsString()));
	}
	
	
	/**
	 * Returns a String representation of this DataValueDescriptor.
	 *
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer(super.toString());
		
		result.append("\nValue: " + fValue);
		
		return (result.toString());
	}
	
	
	/**
	 * Converts any escape sequences (e.g., "\t", "\r", etc.) found in the 
	 * given String with the corresponding ASCII character and returns the 
	 * resulting String.
	 *
	 * @param string The String to convert
	**/
	
	public static String replaceEscapeSequences(String string)
	{
		String result = string;
		
		if (string.indexOf('\\') >= 0)
		{
			StringBuffer resultsBuffer = new StringBuffer();

			char[] characters = string.toCharArray();
			
			for (int i = 0; i < characters.length; i++)
			{
				char character = characters[i];
				
				if (character == '\\')
				{
					character = characters[++i];
					
					if (character == 'r')
					{
						character = '\r';
					}
					else if (character == 'n')
					{
						character = '\n';
					}
					else if (character == 't')
					{
						character = '\t';
					}
					else if (character == '\"')
					{
						character = '\"';
					}
					else if (character == '\'')
					{
						character = '\'';
					}
					else if (character == 'b')
					{
						character = '\b';
					}
					else if (character == 'f')
					{
						character = '\f';
					}
					else if (character == 'n')
					{
						character = '\n';
					}
					else
					{
						character = '\\';
						i--;
					}
				}
				
				resultsBuffer.append(character);
			}
			
			result = resultsBuffer.toString();
		}
		
		return (result);
	}
	
	
	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		String value;
		
		// Unmarshall the propertyName attribute (if any).
		fPropertyName = fSerializer.loadStringAttribute
			(Dataml.A_PROPERTY_NAME, null, fElement);
		
		if (fPropertyName != null)
		{
			value = Irc.getPreference(fPropertyName);
			
			value = replaceEscapeSequences(value);
			
			setValue(value);
		}
		else
		{
			// Unmarshall the value attribute (if any).
			value = fSerializer.loadStringAttribute(Dataml.A_VALUE, null, fElement);
			
			if (value != null)
			{
				value = replaceEscapeSequences(value);
				
				setValue(value);
			}
		}
		
		setValue(value);
	}
}
