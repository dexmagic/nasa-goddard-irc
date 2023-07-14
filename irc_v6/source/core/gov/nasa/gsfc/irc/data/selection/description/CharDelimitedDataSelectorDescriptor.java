//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/CharDelimitedDataSelectorDescriptor.java,v 1.3 2006/04/27 19:46:21 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: CharDelimitedDataSelectorDescriptor.java,v $
//  Revision 1.3  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.2  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.2  2004/11/15 20:33:08  chostetter_cvs
//  Fixed remaining Message formatting issues
//
//  Revision 1.1  2004/10/18 22:58:15  chostetter_cvs
//  More data transformation work
//
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.selection.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A CharDelimitedSelectorDescriptor describes the means to select a character-
 * delimited data element.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 19:46:21 $
 * @author Carl F. Hostetter
 */

public class CharDelimitedDataSelectorDescriptor 
	extends AbstractCharDataSelectorDescriptor
{
	private char fDelimiter;
	
	
	/**
	 * Constructs a new CharDelimitedDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		CharDelimitedDataSelectorDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		CharDelimitedDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		CharDelimitedDataSelectorDescriptor		
	**/
	
	public CharDelimitedDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given CharDelimitedDataSelectorDescriptor.
	 *
	 * @param descriptor The CharDelimitedDataSelectorDescriptor to be copied
	**/
	
	protected CharDelimitedDataSelectorDescriptor
		(CharDelimitedDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fDelimiter = descriptor.fDelimiter;
	}
	

	/**
	 * Returns a (deep) copy of this CharDelimitedDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this CharDelimitedDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		CharDelimitedDataSelectorDescriptor result = 
			(CharDelimitedDataSelectorDescriptor) super.clone();
			
		result.fDelimiter = fDelimiter;
		
		return (result);
	}
	
	
	/**
	 *  Returns the delimiter char associated with this 
	 *  CharDelimitedSelectorDescriptor.
	 *  
	 *  @return The delimiter char associated with this 
	 *  		CharDelimitedSelectorDescriptor
	 */
	
	public char getDelimiter()
	{
		return (fDelimiter);
	}
	
	
	/** 
	 *  Returns a String representation of this CharDelimitedSelectorDescriptor.
	 *
	 *  @return A String representation of this CharDelimitedSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("CharDelimitedSelectorDescriptor:");
		result.append("\n" + super.toString());
		result.append("\nDelimiter: " + fDelimiter);
		
		return (result.toString());
	}
	
				
	/**
	 * Unmarshalls a CharDelimitedSelectorDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Unmarshall the delimiter attribute (if any).
		String delimiter = 
			fSerializer.loadStringAttribute(Dataselml.A_DELIMITER, " ", fElement);
		
		if (delimiter != null)
		{
			fDelimiter = delimiter.charAt(0);
			
			if ((fDelimiter == '\\') && (delimiter.length() > 1))
			{
				char secondChar = delimiter.charAt(1);
				
				switch (secondChar)
				{
					case('b') : 
					{
						fDelimiter = '\b';
						break;
					}

					case('f') : 
					{
						fDelimiter = '\f';
						break;
					}

					case('n') : 
					{
						fDelimiter = '\n';
						break;
					}

					case('t') : 
					{
						fDelimiter = '\t';
						break;
					}

					case('r') : 
					{
						fDelimiter = '\r';
						break;
					}
					
					case('\'') : 
					{
						fDelimiter = '\'';
						break;
					}
					
					case('\"') : 
					{
						fDelimiter = '\"';
						break;
					}
					
					case('\\') : 
					{
						fDelimiter = '\\';
						break;
					}
				}
			}
		}
	}
}
