//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/RangeDataSelectorDescriptor.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: RangeDataSelectorDescriptor.java,v $
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

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A RangeDataSelectorDescriptor is an abstract selector of some range of data 
 * elements.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public abstract class RangeDataSelectorDescriptor 
	extends AbstractDataSelectorDescriptor
{
	private int fStart = -1;
	private int fLength = 1;
	
	
	/**
	 * Constructs a new RangeDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		RangeDataSelectorDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		RangeDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		RangeDataSelectorDescriptor		
	**/
	
	public RangeDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given RangeDataSelectorDescriptor.
	 *
	 * @param descriptor The RangeDataSelectorDescriptor to be copied
	**/
	
	protected RangeDataSelectorDescriptor(RangeDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fStart = descriptor.fStart;
		fLength = descriptor.fLength;
	}
	

	/**
	 * Returns a (deep) copy of this RangeDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this RangeDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		RangeDataSelectorDescriptor result = 
			(RangeDataSelectorDescriptor) super.clone();
		
		result.fStart = fStart;
		result.fLength = fLength;
		
		return (result);
	}
	
	
	/**
	 *  Returns the element start index specified by this 
	 *  RangeDataSelectorDescriptor.
	 *  
	 *  @return The element start index specified by this 
	 *  	RangeDataSelectorDescriptor
	 */
	
	public int getStart()
	{
		return (fStart);
	}
	
	
	/**
	 *  Returns the elements length specified by this 
	 *  RangeDataSelectorDescriptor.
	 *  
	 *  @return The elements length specified by this 
	 *  	RangeDataSelectorDescriptor
	 */
	
	public int getLength()
	{
		return (fLength);
	}
	
	
	/** 
	 *  Returns a String representation of this RangeDataSelectorDescriptor.
	 *
	 *  @return A String representation of this RangeDataSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("RangeDataSelectorDescriptor:");
		result.append("\n" + super.toString());
		result.append("\nStart: " + fStart);
		result.append("\nLength: " + fLength);
		
		return (result.toString());
	}
	
				
	/**
	 * Unmarshalls a RangeDataSelectorDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Unmarshall the start attribute (if any).
		fStart = fSerializer.loadIntAttribute(Dataselml.A_INDEX, -1, fElement);
		
		// Unmarshall the length attribute (if any).
		fLength = fSerializer.loadIntAttribute(Dataselml.A_LENGTH, 1, fElement);
	}
}
