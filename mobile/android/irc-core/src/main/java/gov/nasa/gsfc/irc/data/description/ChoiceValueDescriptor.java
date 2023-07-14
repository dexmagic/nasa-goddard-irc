//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.description;

import org.jdom.Element;

import gov.nasa.gsfc.commons.types.namespaces.AbstractNamedObject;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A StringValueDescriptor describes a String value.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/01/23 17:59:50 $ 
 * @author Carl F. Hostetter 
**/

public class ChoiceValueDescriptor extends AbstractIrcElementDescriptor
{
	private String fValue;
	
	
	/**
	 * Constructs a new StringValueDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new StringValueDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		StringValueDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		StringValueDescriptor		
	**/
	
	public ChoiceValueDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Dataml.N_CONSTRAINTS);
		
		xmlUnmarshall();
	}
	

	//TODO should the replaceEscapeSequences method be moved to a utility package?
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
	 * Returns the String value associated with this StringValueDescriptor.
	 *
	**/
	public String toString()
	{
		return (super.toString() + "\nValue: " + fValue);
	}
	
	
	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/	
	private void xmlUnmarshall()
	{
		// Unmarshall the value attribute (if any).
		fValue = fSerializer.loadStringAttribute(Dataml.A_VALUE, null, fElement);
		
		String name = getName();
		
		if (fValue != null)
		{
			fValue = replaceEscapeSequences(fValue);
			
			if ((name == null) || (name == "") || (name == AbstractNamedObject.DEFAULT_NAME))
			{
				setName(fValue);
			}
		}
		else
		{
			if (name != null) 
			{
				fValue = name;
			}
		}
	}
	
	/**
	 * Gets the value associated with this choice element.
	 * 
	 * @return Returns the value.
	 */
	public String getValue()
	{
		return fValue;
	}
	
	/**
	 * Sets the value associated with this choice element. Typically this is
	 * already set during construction.
	 * 
	 * @param value The value to set.
	 */
	public void setValue(String value)
	{
		fValue = value;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ChoiceValueDescriptor.java,v $
//  Revision 1.5  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.4  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.3  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.1  2005/01/20 08:10:50  tames
//  Changes to support choice descriptors
//
//
