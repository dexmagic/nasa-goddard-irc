//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/RegExPatternDataSelectorDescriptor.java,v 1.3 2005/09/30 20:55:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: RegExPatternDataSelectorDescriptor.java,v $
//  Revision 1.3  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.2  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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

import java.util.regex.Pattern;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A RegExPatternDataSelectorDescriptor describes the means to select named data via 
 * regular-expression pattern matching.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/30 20:55:48 $
 * @author Carl F. Hostetter
 */

public class RegExPatternDataSelectorDescriptor 
	extends AbstractCharDataSelectorDescriptor
{
	private Pattern fPattern;
	private int fFlags = 0;
	private int fGroupNumber = 0;
	
	
	/**
	 * Constructs a new RegExPatternDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		RegExPatternDataSelectorDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		RegExPatternDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		RegExPatternDataSelectorDescriptor		
	**/
	
	public RegExPatternDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given RegExPatternDataSelectorDescriptor.
	 *
	 * @param descriptor The RegExPatternDataSelectorDescriptor to be copied
	**/
	
	protected RegExPatternDataSelectorDescriptor
		(RegExPatternDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fPattern = descriptor.fPattern;
		fFlags = descriptor.fFlags;
		fGroupNumber = descriptor.fGroupNumber;
	}
	

	/**
	 * Returns a (deep) copy of this RegExPatternDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this RegExPatternDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		RegExPatternDataSelectorDescriptor result = 
			(RegExPatternDataSelectorDescriptor) super.clone();
		
		result.fPattern = fPattern;
		result.fFlags = fFlags;
		result.fGroupNumber = fGroupNumber;
		
		return (result);
	}
	
	
	/**
	 *  Returns the Pattern which this RegExPatternDataSelectorDescriptor is 
	 *  configured to match.
	 *  
	 *  @return The Pattern which this RegExPatternDataSelectorDescriptor is 
	 *  		configured to match
	 */
	
	public Pattern getPattern()
	{
		return (fPattern);
	}
	
	
	/**
	 *  Returns the group number which this RegExPatternDataSelectorDescriptor is 
	 *  configured to match.
	 *  
	 *  @return The group number which this RegExPatternDataSelectorDescriptor is 
	 *  		configured to match
	 */
	
	public int getGroupNumber()
	{
		return (fGroupNumber);
	}
	
	
	/** 
	 *  Returns a String representation of this RegExPatternDataSelectorDescriptor.
	 *
	 *  @return A String representation of this RegExPatternDataSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("RegExPatternDataSelectorDescriptor: ");
		result.append("\n" + super.toString());
		result.append("\nPattern: " + fPattern);
		result.append("\nFlags: " + fFlags);
		result.append("\nGroupNumber: " + fGroupNumber);
		
		return (result.toString());
	}
	
	
	/**
	 * Unmarshalls a RegExPatternDataSelectorDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Unmarshall the regex attribute (if any).
		String regex = fSerializer.loadStringAttribute
			(Dataselml.A_REGEX, null, fElement);
		
		// Unmarshall the flags attribute (if any).
		fFlags = fSerializer.loadIntAttribute(Dataselml.A_FLAGS, 0, fElement);
		
		if (regex != null)
		{
			fPattern = Pattern.compile(regex, fFlags);
		}
		
		// Unmarshall the group number attribute (if any).
		fGroupNumber = fSerializer.loadIntAttribute
			(Dataselml.A_GROUP_NUMBER, 0, fElement);
	}
}
