//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataTargetSelectionDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.transformation.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.selection.description.DataContainerSelectionDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataTargetSelectionDescriptor describes the means of selecting a 
 * data transformation target.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class DataTargetSelectionDescriptor extends DataContainerSelectionDescriptor
{
	/**
	 * Default constructor of a new DataTargetSelectionDescriptor.
	 *
	**/
	
	public DataTargetSelectionDescriptor()
	{

	}
	

	/**
	 * Constructs a new DataTargetSelectionDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataTargetSelectionDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataTargetSelectionDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataTargetSelectionDescriptor		
	**/
	
	public DataTargetSelectionDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataTargetSelectionDescriptor.
	 *
	 * @param descriptor The DataTargetSelectionDescriptor to be cloned
	**/
	
	protected DataTargetSelectionDescriptor(DataTargetSelectionDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DataTargetSelectionDescriptor.
	 *
	 * @return A (deep) copy of this DataTargetSelectionDescriptor
	**/
	
	public Object clone()
	{
		DataTargetSelectionDescriptor result = (DataTargetSelectionDescriptor) super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this DataTargetSelectionDescriptor.
	 *
	 *  @return A String representation of this DataTargetSelectionDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("DataTargetSelectionDescriptor:\n" + 
			super.toString());
		
		return (result.toString());
	}
}
