//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/DataBufferDataSelectorDescriptor.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataBufferDataSelectorDescriptor.java,v $
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
 * A DataBufferDataSelectorDescriptor describes the means to select a 
 * DataBuffer from a BasisSet.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public class DataBufferDataSelectorDescriptor extends NamedDataSelectorDescriptor 
{
	/**
	 * Constructs a new DataBufferDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 *  	DataBufferDataSelectorDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataBufferDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataBufferDataSelectorDescriptor		
	**/
	
	public DataBufferDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 * Constructs a new DataBufferDataSelectorDescriptor for a DataBuffer with 
	 * the given name.
	 *
	 * @param dataBufferName The name that the new 
	 *  	DataBufferDataSelectorDescriptor will use to select a DataBuffer
	**/
	
	public DataBufferDataSelectorDescriptor(String dataBufferName)
	{
		super(dataBufferName);
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataBufferDataSelectorDescriptor.
	 *
	 * @param descriptor The DataBufferDataSelectorDescriptor to be copied
	**/
	
	protected DataBufferDataSelectorDescriptor
		(DataBufferDataSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DataBufferDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this DataBufferDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		DataBufferDataSelectorDescriptor result = 
			(DataBufferDataSelectorDescriptor) super.clone();
		
		return (result);
	}
	
	
	/**
	 *  Returns the DataBuffer name associated with this 
	 *  DataBufferDataSelectorDescriptor.
	 *  
	 *  @return The DataBuffer name associated with this 
	 *  		DataBufferDataSelectorDescriptor
	 */
	
	public String getDataBufferName()
	{
		return (getName());
	}
	
	
	/** 
	 *  Returns a String representation of this DataBufferDataSelectorDescriptor.
	 *
	 *  @return A String representation of this DataBufferDataSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataBufferDataSelectorDescriptor:");
		result.append("\n" + super.toString());
		
		return (result.toString());
	}
}
