//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataBufferFormatterDescriptor.java,v $
//  Revision 1.4  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.3  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataBufferFormatterDescriptor describes the formatting of a Map of data 
 * across one or more channels of a single sample into a single sample of a 
 * BasisSet.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $ 
 * @author Carl F. Hostetter   
**/

public class DataBufferFormatterDescriptor extends AbstractDataFormatterDescriptor
{
	private String fDataBufferName;
	private DataFormatDescriptor fValue;


	/**
	 * Constructs a new DataBufferFormatterDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataBufferFormatterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataBufferFormatterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataBufferFormatterDescriptor		
	**/
	
	public DataBufferFormatterDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataBufferFormatterDescriptor.
	 *
	 * @param descriptor The DataBufferFormatterDescriptor to be copied
	**/
	
	public DataBufferFormatterDescriptor(DataBufferFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fDataBufferName = descriptor.fDataBufferName;
		fValue = descriptor.fValue;
	}
	

	/**
	 * Returns a (deep) copy of this DataBufferFormatterDescriptor.
	 *
	 * @return A (deep) copy of this DataBufferFormatterDescriptor 
	**/
	
	public Object clone()
	{
		DataBufferFormatterDescriptor result = 
			(DataBufferFormatterDescriptor) super.clone();
		
		result.fDataBufferName = fDataBufferName;
		result.fValue = (DataFormatDescriptor) fValue.clone();
		
		return (result);
	}
	
	
	/**
	 * Sets the DataBuffer name associated with this 
	 * DataBufferFormatterDescriptor to the given name.
	 *
	 * @param The DataBuffer name to be associated with this 
	 * 		DataBufferFormatterDescriptor.
	**/
	
	protected void setDataBufferName(String name)
	{
		fDataBufferName = name;
	}
	

	/**
	 * Returns the DataBuffer name associated with this 
	 * DataBufferFormatterDescriptor (if any).
	 *
	 * @return The DataBuffer name associated with this 
	 * 		DataBufferFormatterDescriptor (if any).
	**/
	
	public String getDataBufferName()
	{
		return (fDataBufferName);
	}
	

	/**
	 * Sets the value DataFormatDescriptor associated with this 
	 * DataBufferFormatterDescriptor to the given DataFormatDescriptor.
	 *
	 * @param The value DataFormatDescriptor to be associated with this 
	 * 		DataBufferFormatterDescriptor
	**/
	
	protected void setValue(DataFormatDescriptor descriptor)
	{
		fValue = descriptor;
	}
	

	/**
	 * Returns the value DataFormatDescriptor associated with this 
	 * DataBufferFormatterDescriptor (if any).
	 *
	 * @return The value DataFormatDescriptor associated with this 
	 * 		DataBufferFormatterDescriptor (if any)
	**/
	
	public DataFormatDescriptor getValue()
	{
		return (fValue);
	}
	

	/** 
	 *  Returns a String representation of this DataBufferFormatterDescriptor.
	 *
	 *  @return A String representation of this DataBufferFormatterDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataBufferFormatterDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fValue != null)
		{
			result.append("\nValue: " + fValue);
		}
		
		if (fDataBufferName != null)
		{
			result.append("\nDataBuffer name: " + fDataBufferName);
		}
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this DataBufferFormatterDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the DataBuffer name attribute.
		fDataBufferName = getName();
		
		// Unmarshall the value DataFormatDescriptor (if any).
		fValue = (DataFormatDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datatransml.E_VALUE, 
				Datatransml.C_VALUE_FORMAT, fElement, this, fDirectory);
	}
}
