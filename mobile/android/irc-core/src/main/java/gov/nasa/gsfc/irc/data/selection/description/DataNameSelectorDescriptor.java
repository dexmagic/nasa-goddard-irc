//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/DataNameSelectorDescriptor.java,v 1.2 2005/09/15 15:34:08 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataNameSelectorDescriptor.java,v $
//  Revision 1.2  2005/09/15 15:34:08  chostetter_cvs
//  Added support for command counter value selection
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

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataNameSelectorDescriptor describes the means to select the name of  
 * named data Objects.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/15 15:34:08 $
 * @author Carl F. Hostetter
 */

public class DataNameSelectorDescriptor extends AbstractDataSelectorDescriptor
{
	/**
	 * Constructs a new DataNameSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataNameSelectorDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataNameSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataNameSelectorDescriptor		
	**/
	
	public DataNameSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataNameSelectorDescriptor.
	 *
	 * @param descriptor The DataNameSelectorDescriptor to be copied
	**/
	
	protected DataNameSelectorDescriptor
		(DataNameSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DataNameSelectorDescriptor.
	 *
	 * @return A (deep) copy of this DataNameSelectorDescriptor 
	**/
	
	public Object clone()
	{
		DataNameSelectorDescriptor result = 
			(DataNameSelectorDescriptor) super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this DataNameSelectorDescriptor.
	 *
	 *  @return A String representation of this DataNameSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataNameSelectorDescriptor:");
		result.append("\n" + super.toString());
		
		return (result.toString());
	}
}
