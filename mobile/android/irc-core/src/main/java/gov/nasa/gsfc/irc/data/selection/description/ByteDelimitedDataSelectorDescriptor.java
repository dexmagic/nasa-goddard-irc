//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/ByteDelimitedDataSelectorDescriptor.java,v 1.2 2005/09/30 20:55:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ByteDelimitedDataSelectorDescriptor.java,v $
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
 * A ByteDelimitedSelectorDescriptor describes the means to select a byte-
 * delimited data element.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/30 20:55:48 $
 * @author Carl F. Hostetter
 */

public class ByteDelimitedDataSelectorDescriptor  
	extends AbstractDataSelectorDescriptor implements ByteDataSelectorDescriptor
{
	private byte fDelimiter;
	
	
	/**
	 * Constructs a new ByteDelimitedSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		ByteDelimitedSelectorDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ByteDelimitedSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ByteDelimitedSelectorDescriptor		
	**/
	
	public ByteDelimitedDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given ByteDelimitedDataSelectorDescriptor.
	 *
	 * @param descriptor The ByteDelimitedDataSelectorDescriptor to be copied
	**/
	
	protected ByteDelimitedDataSelectorDescriptor
		(ByteDelimitedDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fDelimiter = descriptor.fDelimiter;
	}
	

	/**
	 * Returns a (deep) copy of this ByteDelimitedDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this ByteDelimitedDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		ByteDelimitedDataSelectorDescriptor result = 
			(ByteDelimitedDataSelectorDescriptor) super.clone();
		
		result.fDelimiter = fDelimiter;
		
		return (result);
	}
	
	
	/**
	 *  Returns the delimiter byte associated with this 
	 *  ByteDelimitedSelectorDescriptor.
	 *  
	 *  @return The delimiter byte associated with this 
	 *  	ByteDelimitedSelectorDescriptor
	 */
	
	public byte getDelimiter()
	{
		return (fDelimiter);
	}
	
	
	/** 
	 *  Returns a String representation of this ByteDelimitedSelectorDescriptor.
	 *
	 *  @return A String representation of this ByteDelimitedSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("ByteDelimitedSelectorDescriptor:");
		result.append("\n" + super.toString());
		result.append("\nDelimiter: " + fDelimiter);
		
		return (result.toString());
	}
	
				
	/**
	 * Unmarshalls a ByteDelimitedSelectorDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Unmarshall the delimiter attribute (if any).
		String delimiter;
		
		delimiter = fSerializer.loadStringAttribute
			(Dataselml.A_DELIMITER, "\0", fElement);
		
		if (delimiter != null)
		{
			byte[] bytes = delimiter.getBytes();
			
			fDelimiter = bytes[0];
		}
	}
}
