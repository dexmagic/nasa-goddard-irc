//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataSourceSelectionDescriptor.java,v $
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

import gov.nasa.gsfc.irc.data.selection.description.AbstractDataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.Dataselml;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataSourceSelectionDescriptor describes the means of selecting a 
 * source of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class DataSourceSelectionDescriptor extends AbstractDataSelectionDescriptor
{
	/**
	 * Default constructor of a new DataSourceSelectionDescriptor.
	 *
	**/
	
	public DataSourceSelectionDescriptor()
	{

	}
	

	/**
	 * Constructs a new DataSourceSelectionDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by 
	 * unmarshalling from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		DataSourceSelectionDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataSourceSelectionDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataSourceSelectionDescriptor		
	**/
	
	public DataSourceSelectionDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataSourceSelectionDescriptor.
	 *
	 * @param descriptor The DataSourceSelectionDescriptor to be cloned
	**/
	
	protected DataSourceSelectionDescriptor
		(DataSourceSelectionDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DataSourceSelectionDescriptor.
	 *
	 * @return A (deep) copy of this DataSourceSelectionDescriptor
	**/
	
	public Object clone()
	{
		DataSourceSelectionDescriptor result = (DataSourceSelectionDescriptor) 
			super.clone();
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this DataSourceSelectionDescriptor.
	 *
	 *  @return A String representation of this DataSourceSelectionDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer
			("DataSourceSelectionDescriptor:\n" + super.toString());
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this DataSourceSelectionDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the DataSelectionDescriptor (if any). This can be 
		// either in a Selection tag or within a Source tag.
		
		DataSelectionDescriptor selection = (DataSelectionDescriptor)
			fSerializer.loadSingleChildDescriptorElement
				(Dataselml.E_SELECTION, Dataselml.C_DATA_SELECTION, fElement, 
					this, fDirectory);
	}
}
