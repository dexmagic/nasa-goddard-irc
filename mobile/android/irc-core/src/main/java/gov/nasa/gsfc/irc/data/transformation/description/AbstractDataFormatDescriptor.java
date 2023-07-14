//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataFormatDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataFormatDescriptor describes a means of formatting data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class AbstractDataFormatDescriptor extends AbstractIrcElementDescriptor 
	implements DataFormatDescriptor
{
	protected DataFormatterDescriptor fDataFormatter;
	
	
	/**
	 * Constructs a new AbstractDataFormatDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new AbstractDataFormatDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataFormatDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataFormatDescriptor		
	**/
	
	public AbstractDataFormatDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Datatransml.N_FORMATS);
	}
	

	/**
	 * Constructs a (shallow) copy of the given AbstractDataFormatDescriptor.
	 *
	 * @param descriptor The AbstractDataFormatDescriptor to be copied
	**/
	
	protected AbstractDataFormatDescriptor
		(AbstractDataFormatDescriptor descriptor)
	{
		super(descriptor);
		
		fDataFormatter = descriptor.fDataFormatter;
	}
	

	/**
	 * Returns a (deep) copy of this AbstractDataFormatDescriptor.
	 *
	 * @return A (deep) copy of this AbstractDataFormatDescriptor
	**/
	
	public Object clone()
	{
		AbstractDataFormatDescriptor result = 
			(AbstractDataFormatDescriptor) super.clone();
		
		result.fDataFormatter = (DataFormatterDescriptor) 
			fDataFormatter.clone();
		
		return (result);
	}
	
	
	/**
	 * Sets the DataFormatterDescriptor associated with this 
	 * AbstractDataFormatDescriptor to the given DataFormatterDescriptor.
	 *
	 * @param formatter The new DataFormatterDescriptor associated with this 
	 * 		AbstractDataFormatDescriptor 
	**/
	
	public void setDataFormatter(DataFormatterDescriptor formatter)
	{
		fDataFormatter = formatter;
	}
	

	/**
	 * Returns the DataFormatterDescriptor associated with this 
	 * 		AbstractDataFormatDescriptor (if any).
	 *
	 * @return The DataFormatterDescriptor associated with this 
	 * 		AbstractDataFormatDescriptor (if any) 
	**/
	
	public DataFormatterDescriptor getDataFormatter()
	{
		return (fDataFormatter);
	}
	

	/** 
	 *  Returns a String representation of this AbstractDataFormatDescriptor.
	 *
	 *  @return A String representation of this AbstractDataFormatDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataFormatDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fDataFormatter != null)
		{
			result.append("\nData Formatter: " + fDataFormatter);
		}
		
		return (result.toString());
	}
}
