//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/AbstractDataSelectorDescriptor.java,v 1.4 2006/01/23 17:59:51 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataSelectorDescriptor.java,v $
//  Revision 1.4  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
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
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataSelectorDescriptor describes the means to select data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:51 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractDataSelectorDescriptor 
	extends AbstractIrcElementDescriptor implements DataSelectorDescriptor
{
	/**
	 *  Constructs an anonymous DataSelectorDescriptor.
	 * 
	 *  @param name The base name of the new DataSelectorDescriptor
	 **/

	public AbstractDataSelectorDescriptor()
	{
		
	}
	
	
	/**
	 *  Constructs a new DataSelectorDescriptor having the given base name and 
	 *  no name qualifier.
	 * 
	 *  @param name The base name of the new DataSelectorDescriptor
	 **/

	public AbstractDataSelectorDescriptor(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new DataSelectorDescriptor having the given base name and name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new DataSelectorDescriptor
	 *  @param nameQualifier The name qualifier of the new DataSelectorDescriptor
	 **/

	public AbstractDataSelectorDescriptor(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
	
	
	/**
	 * Constructs a new DataSelectorDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element within the indicated namespace.
	 *
	 * @param parent The parent Descriptor of the new DataSelectorDescriptor
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataSelectorDescriptor		
	 * @param namespace The namespace to which the new DataSelectorDescriptor 
	 * 		should belong		
	**/
	
	public AbstractDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element, String namespace)
	{
		super(parent, directory, element, namespace);
	}
	
	
	/**
	 * Constructs a new AbstractDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new AbstractDataSelectorDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataSelectorDescriptor		
	**/
	
	public AbstractDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		this(parent, directory, element, Dataselml.N_SELECTORS);
	}
	

	/**
	 * Constructs a (shallow) copy of the given AbstractDataSelectorDescriptor.
	 *
	 * @param descriptor The AbstractDataSelectorDescriptor to be copied
	**/
	
	protected AbstractDataSelectorDescriptor
		(AbstractDataSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this AbstractDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this AbstractDataSelectorDescriptor
	**/
	
	public Object clone()
	{
		AbstractDataSelectorDescriptor result = 
			(AbstractDataSelectorDescriptor) super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this AbstractDataSelectorDescriptor.
	 *
	 *  @return A String representation of this AbstractDataSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("DataSelectorDescriptor: ");
		result.append("\n" + super.toString());
		
		return (result.toString());
	}
}
