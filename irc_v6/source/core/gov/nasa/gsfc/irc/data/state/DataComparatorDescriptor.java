//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/state/DataComparatorDescriptor.java,v 1.5 2006/04/27 19:46:21 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataComparatorDescriptor.java,v $
//  Revision 1.5  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.4  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.3  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
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

package gov.nasa.gsfc.irc.data.state;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.description.DataValueType;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;


/**
 * A DataComparatorDescriptor describes a comparator of two data items.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 19:46:21 $
 * @author Carl F. Hostetter
 */

public class DataComparatorDescriptor extends AbstractIrcElementDescriptor
{
	private DataComparatorType fComparatorType;
	private String fTargetValue;
	private DataValueType fTargetType;
	
	
	/**
	 * Constructs a new DataComparatorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataComparatorDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataComparatorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataComparatorDescriptor		
	**/
	
	public DataComparatorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Datastateml.N_COMPARISONS);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataComparatorDescriptor.
	 *
	 * @param descriptor The DataComparatorDescriptor to be copied
	**/
	
	protected DataComparatorDescriptor(DataComparatorDescriptor descriptor)
	{
		super(descriptor);
		
		fComparatorType = descriptor.fComparatorType;
		fTargetValue = descriptor.fTargetValue;
		fTargetType = descriptor.fTargetType;
	}
	

	/**
	 * Returns a (deep) copy of this DataComparatorDescriptor.
	 *
	 * @return A (deep) copy of this DataComparatorDescriptor
	**/
	
	public Object clone()
	{
		DataComparatorDescriptor result = (DataComparatorDescriptor) super.clone();
		
		result.fComparatorType = fComparatorType;
		result.fTargetValue = fTargetValue;
		result.fTargetType = fTargetType;
		
		return (result);
	}
	
	
	/** 
	 *  Returns the DataComparatorType specified for this DataComparatorDescriptor.
	 *
	 *  @return The DataComparatorType specified for this DataComparatorDescriptor
	**/
	
	public DataComparatorType getComparatorType()
	{
		return (fComparatorType);
	}
	
	
	/** 
	 *  Returns the target DataValueType specified for this DataComparatorDescriptor.
	 *
	 *  @return The target DataValueType specified for this DataComparatorDescriptor
	**/
	
	public DataValueType getTargetType()
	{
		return (fTargetType);
	}
	
	
	/** 
	 *  Returns the target String value specified for this DataComparatorDescriptor.
	 *
	 *  @return The target String value specified for this DataComparatorDescriptor
	**/
	
	public String getTargetValue()
	{
		return (fTargetValue);
	}
	
	
	/** 
	 *  Returns a String representation of this DataComparatorDescriptor.
	 *
	 *  @return A String representation of this DataComparatorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataComparatorDescriptor: ");
		result.append("\n" + super.toString());
		result.append("\nComparator Type: " + fComparatorType);
		result.append("\nTarget Value: " + fTargetValue);
		result.append("\nTarget Type: " + fTargetType);
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this DataComparatorDescriptor from its associated JDOM Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the comparator attribute (if any).
		String comparatorName = fSerializer.loadStringAttribute
			(Datastateml.A_COMPARATOR, DataComparatorType.TRUE.getName(), 
				fElement);
		
		if (comparatorName != null)
		{
			fComparatorType = DataComparatorType.forName(comparatorName);
		}
		
		// Unmarshall the target type attribute (if any).
		String targetTypeName = fSerializer.loadStringAttribute
			(Datastateml.A_TARGET_TYPE, DataValueType.STRING.getName(), fElement);
	
		if (targetTypeName != null)
		{
			fTargetType = DataValueType.forName(targetTypeName);
		}
		
		// Unmarshall the target value String attribute (if any).
		fTargetValue = fSerializer.loadStringAttribute
			(Datastateml.A_TARGET_VALUE, null, fElement);
	}
}
