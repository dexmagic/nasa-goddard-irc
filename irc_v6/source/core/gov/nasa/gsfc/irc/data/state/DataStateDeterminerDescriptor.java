//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/state/DataStateDeterminerDescriptor.java,v 1.4 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataStateDeterminerDescriptor.java,v $
//  Revision 1.4  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

package gov.nasa.gsfc.irc.data.state;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.description.DataValueType;
import gov.nasa.gsfc.irc.data.description.Dataml;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataStateDeterminerDescriptor describes a determiner of the state of data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public class DataStateDeterminerDescriptor extends AbstractIrcElementDescriptor
{
	private DataStateDeterminantDescriptor fDeterminant;
	private DataValueType fDeterminantType;
	private Map fComparatorsByName = new LinkedHashMap();
	
	
	/**
	 * Constructs a new DataStateDeterminerDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataStateDeterminerDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataStateDeterminerDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataStateDeterminerDescriptor		
	**/
	
	public DataStateDeterminerDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Datastateml.N_DETERMINERS);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataStateDeterminerDescriptor.
	 *
	 * @param descriptor The DataStateDeterminerDescriptor to be copied
	**/
	
	protected DataStateDeterminerDescriptor
		(DataStateDeterminerDescriptor descriptor)
	{
		super(descriptor);
		
		fDeterminant = descriptor.fDeterminant;
		fDeterminantType = descriptor.fDeterminantType;
		fComparatorsByName = descriptor.fComparatorsByName;
	}
	

	/**
	 * Returns a (deep) copy of this DataStateDeterminerDescriptor.
	 *
	 * @return A (deep) copy of this DataStateDeterminerDescriptor 
	**/
	
	public Object clone()
	{
		DataStateDeterminerDescriptor result = 
			(DataStateDeterminerDescriptor) super.clone();
		
		result.fDeterminant = (DataStateDeterminantDescriptor) 
			fDeterminant.clone();
		result.fDeterminantType = fDeterminantType;
		result.fComparatorsByName = new LinkedHashMap(fComparatorsByName);
		
		return (result);
	}
	

	/** 
	 *  Returns the DataStateDeterminantDescriptor of this 
	 *  DataStateDeterminerDescriptor (if any).
	 *
	 *  @return The DataStateDeterminantDescriptor of this 
	 *  	DataStateDeterminerDescriptor (if any)
	**/
	
	public DataStateDeterminantDescriptor getDeterminant()
	{
		return (fDeterminant);
	}
	
	
	/** 
	 *  Returns the determinant (source) DataValueType of this 
	 *  DataStateDeterminerDescriptor (if any).
	 *
	 *  @return The determinant (source) DataValueType of this 
	 *  	DataStateDeterminerDescriptor (if any)
	**/
	
	public DataValueType getDeterminantType()
	{
		return (fDeterminantType);
	}
	
	
	/** 
	 *  Returns the set of DataComparatorDescriptors associated with this 
	 *  DataStateDeterminerDescriptor.
	 *
	 *  @return The set of DataComparatorDescriptors associated with this 
	 *  	DataStateDeterminerDescriptor
	**/
	
	public Collection getComparators()
	{
		return (Collections.unmodifiableCollection(fComparatorsByName.values()));
	}
	
	
	/** 
	 *  Returns a String representation of this DataStateDeterminerDescriptor.
	 *
	 *  @return A String representation of this DataStateDeterminerDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataStateDeterminerDescriptor:");
		result.append("\n" + super.toString());
		
		if (fDeterminant != null)
		{
			result.append("\nDeterminant: " + fDeterminant);
		}
		
		if (fDeterminantType != null)
		{
			result.append("\nDeterminant Type: " + fDeterminantType);
		}
		
		if (fComparatorsByName.size() > 0)
		{
			result.append("\nComparators: ");
			
			Iterator comparators = fComparatorsByName.values().iterator();
			
			while (comparators.hasNext())
			{
				DataComparatorDescriptor comparator = 
					(DataComparatorDescriptor) comparators.next();
				
				result.append("\n" + comparator);
			}
		}
		
		return (result.toString());
	}


	/**
	  * Unmarshalls this DataStateDeterminerDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the determinant AbstractDataSelectorDescriptor (if any).
		fDeterminant = (DataStateDeterminantDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Datastateml.E_DETERMINANT, 
				Datastateml.C_DETERMINANT, fElement, this, fDirectory);
		
		// Unmarshall the determinant (source) DataValueType attribute (if any).
		String typeString = fSerializer.loadStringAttribute
			(Datastateml.A_SOURCE_TYPE, Dataml.STRING, fElement);
		
		if (typeString != null)
		{
			fDeterminantType = DataValueType.forName(typeString);
		}

		// Unmarshall the DataComparisonDescriptors (if any).
		fComparatorsByName.clear();
		
		fSerializer.loadChildDescriptorElements(Datastateml.E_COMPARISON, 
			fComparatorsByName, Datastateml.C_COMPARATOR, 
				fElement, this, fDirectory);
	}
}
