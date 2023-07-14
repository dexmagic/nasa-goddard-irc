//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataSelectionDescriptor.java,v $
//  Revision 1.2  2006/05/31 21:15:11  chostetter_cvs
//  Aovid potential NullPointerException in clone()
//
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

package gov.nasa.gsfc.irc.data.selection.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataSelectionDescriptor describes a means of selecting data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/31 21:15:11 $ 
 * @author Carl F. Hostetter   
**/

public abstract class AbstractDataSelectionDescriptor 
	extends AbstractIrcElementDescriptor implements DataSelectionDescriptor
{
	protected DataSelectorDescriptor fDataSelector;
	
	
	/**
	 * Default constructor of a new AbstractDataSelectionDescriptor.
	 *
	**/
	
	public AbstractDataSelectionDescriptor()
	{

	}
	

	/**
	 * Constructs a new AbstractDataSelectionDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by 
	 * unmarshalling from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		AbstractDataSelectionDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataSelectionDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataSelectionDescriptor		
	**/
	
	public AbstractDataSelectionDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Dataselml.N_SELECTORS);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataSelectionDescriptor.
	 *
	 * @param descriptor The DataSelectionDescriptor to be cloned
	**/
	
	protected AbstractDataSelectionDescriptor
		(AbstractDataSelectionDescriptor descriptor)
	{
		super(descriptor);
		
		fDataSelector = descriptor.fDataSelector;
	}
	

	/**
	 * Returns a (deep) copy of this DataSelectionDescriptor.
	 *
	 * @return A (deep) copy of this DataSelectionDescriptor
	**/
	
	public Object clone()
	{
		AbstractDataSelectionDescriptor result = 
			(AbstractDataSelectionDescriptor) super.clone();
		
		if (fDataSelector != null)
		{
			result.fDataSelector = (DataSelectorDescriptor) 
				fDataSelector.clone();
		}
		
		return (result);
	}
	
	
	/**
	 * Sets the DataSelectorDescriptor associated with this 
	 * DataSelectionDescriptor to the given DataSelectorDescriptor.
	 *
	 * @param descriptor The DataSelectorDescriptor associated with this 
	 * 		DataSelectionDescriptor 
	**/
	
	public void setDataSelector(DataSelectorDescriptor descriptor)
	{
		fDataSelector = descriptor;
	}
	

	/**
	 * Returns the DataSelectorDescriptor associated with this 
	 * 		DataSelectionDescriptor (if any).
	 *
	 * @return The DataSelectorDescriptor associated with this 
	 * 		DataSelectionDescriptor (if any) 
	**/
	
	public DataSelectorDescriptor getDataSelector()
	{
		return (fDataSelector);
	}
	

	/** 
	 *  Returns a String representation of this DataSelectionDescriptor.
	 *
	 *  @return A String representation of this DataSelectionDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("DataSelectionDescriptor: ");

		result.append("\nDataSelector: " + fDataSelector);
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this DataSelectionDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{

	}
}
