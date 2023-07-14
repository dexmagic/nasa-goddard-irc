//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataTransformationDescriptor.java,v $
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
 * A DataTransformationDescriptor describes a means of transforming data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class AbstractDataTransformationDescriptor 
	extends AbstractIrcElementDescriptor implements DataTransformationDescriptor
{
	protected DataTransformerDescriptor fDataTransformer;
	
	
	/**
	 * Constructs a new AbstractDataTransformationDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new AbstractDataTransformationDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataTransformationDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataTransformationDescriptor		
	**/
	
	public AbstractDataTransformationDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Datatransml.N_FORMATS);
	}
	

	/**
	 * Constructs a (shallow) copy of the given AbstractDataTransformationDescriptor.
	 *
	 * @param descriptor The AbstractDataTransformationDescriptor to be copied
	**/
	
	protected AbstractDataTransformationDescriptor
		(AbstractDataTransformationDescriptor descriptor)
	{
		super(descriptor);
		
		fDataTransformer = descriptor.fDataTransformer;
	}
	

	/**
	 * Returns a (deep) copy of this AbstractDataTransformationDescriptor.
	 *
	 * @return A (deep) copy of this AbstractDataTransformationDescriptor
	**/
	
	public Object clone()
	{
		AbstractDataTransformationDescriptor result = 
			(AbstractDataTransformationDescriptor) super.clone();
		
		result.fDataTransformer = (DataTransformerDescriptor) 
			fDataTransformer.clone();
		
		return (result);
	}
	
	
	/**
	 * Sets the DataTransformerDescriptor associated with this 
	 * AbstractDataTransformationDescriptor to the given DataTransformerDescriptor.
	 *
	 * @param formatter The new DataTransformerDescriptor associated with this 
	 * 		AbstractDataTransformationDescriptor 
	**/
	
	public void setDataTransformer(DataTransformerDescriptor formatter)
	{
		fDataTransformer = formatter;
	}
	

	/**
	 * Returns the DataTransformerDescriptor associated with this 
	 * 		AbstractDataTransformationDescriptor (if any).
	 *
	 * @return The DataTransformerDescriptor associated with this 
	 * 		AbstractDataTransformationDescriptor (if any) 
	**/
	
	public DataTransformerDescriptor getDataTransformer()
	{
		return (fDataTransformer);
	}
	

	/** 
	 *  Returns a String representation of this AbstractDataTransformationDescriptor.
	 *
	 *  @return A String representation of this AbstractDataTransformationDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataTransformationDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fDataTransformer != null)
		{
			result.append("\nData Transformer: " + fDataTransformer);
		}
		
		return (result.toString());
	}
}
