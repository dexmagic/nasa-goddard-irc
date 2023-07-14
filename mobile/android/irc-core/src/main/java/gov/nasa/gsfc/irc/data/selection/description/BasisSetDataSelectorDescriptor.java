//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/BasisSetDataSelectorDescriptor.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetDataSelectorDescriptor.java,v $
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
 * A BasisSetDataSelectorDescriptor describes the means to select a BasisSet from 
 * a BasisBundle in the Dataspace.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public class BasisSetDataSelectorDescriptor extends NamedDataSelectorDescriptor 
{
	/**
	 * Constructs a new BasisSetDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new BasisSetDataSelectorDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		BasisSetDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		BasisSetDataSelectorDescriptor		
	**/
	
	public BasisSetDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 * Constructs a new BasisSetDataSelectorDescriptor for the BasisBundle in the 
	 * Dataspace that has the given name.
	 *
	 * @param name The name that the new BasisSetDataSelectorDescriptor will use to 
	 * 		select data
	**/
	
	public BasisSetDataSelectorDescriptor(String basisBundleName)
	{
		super(basisBundleName);
	}
	

	/**
	 * Constructs a (shallow) copy of the given BasisSetDataSelectorDescriptor.
	 *
	 * @param descriptor The BasisSetDataSelectorDescriptor to be copied
	**/
	
	protected BasisSetDataSelectorDescriptor
		(BasisSetDataSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this BasisSetDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this BasisSetDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		BasisSetDataSelectorDescriptor result = 
			(BasisSetDataSelectorDescriptor) super.clone();
		
		return (result);
	}
	
	
	/**
	 *  Returns the BasisBundle name associated with this 
	 *  BasisSetDataSelectorDescriptor.
	 *  
	 *  @return The BasisBundle name associated with this 
	 *  		BasisSetDataSelectorDescriptor
	 */
	
	public String getBasisBundleName()
	{
		return (getName());
	}
	
	
	/** 
	 *  Returns a String representation of this BasisSetDataSelectorDescriptor.
	 *
	 *  @return A String representation of this BasisSetDataSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("BasisSetDataSelectorDescriptor:");
		result.append("\n" + super.toString());
		
		return (result.toString());
	}
}
