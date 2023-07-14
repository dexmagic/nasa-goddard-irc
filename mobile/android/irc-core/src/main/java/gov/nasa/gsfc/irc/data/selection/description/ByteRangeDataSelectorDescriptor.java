//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/ByteRangeDataSelectorDescriptor.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ByteRangeDataSelectorDescriptor.java,v $
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
 * A ByteRangeSelectorDescriptor describes the means to select a range of 
 * bytes from a data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public class ByteRangeDataSelectorDescriptor extends RangeDataSelectorDescriptor	
	implements ByteDataSelectorDescriptor
{
	/**
	 * Constructs a new ByteRangeDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		ByteRangeDataSelectorDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ByteRangeDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ByteRangeDataSelectorDescriptor		
	**/
	
	public ByteRangeDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
	}
	
	
	/**
	 * Constructs a (shallow) copy of the given ByteRangeDataSelectorDescriptor.
	 *
	 * @param descriptor The ByteRangeDataSelectorDescriptor to be copied
	**/
	
	protected ByteRangeDataSelectorDescriptor
		(ByteRangeDataSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this ByteRangeDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this ByteRangeDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		ByteRangeDataSelectorDescriptor result = 
			(ByteRangeDataSelectorDescriptor) super.clone();
		
		return (result);
	}
}
