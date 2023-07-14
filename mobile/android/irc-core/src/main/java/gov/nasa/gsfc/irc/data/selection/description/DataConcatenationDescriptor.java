//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/DataConcatenationDescriptor.java,v 1.5 2006/06/01 22:22:43 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataConcatenationDescriptor.java,v $
//  Revision 1.5  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.4  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.3  2006/03/31 21:57:38  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.2  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataConcatenationDescriptor describes the concatenation of a set of 
 * data selections.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $
 * @author Carl F. Hostetter
 */

public class DataConcatenationDescriptor extends AbstractDataSelectorDescriptor
{
	private Set fDataSelectors = new LinkedHashSet();
	private String fSeparator;
	
	
	/**
	 * Constructs a new DataConcatenationDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataConcatenationDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataConcatenationDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataConcatenationDescriptor		
	**/
	
	public DataConcatenationDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataConcatenationDescriptor.
	 *
	 * @param descriptor The DataConcatenationDescriptor to be copied
	**/
	
	protected DataConcatenationDescriptor
		(DataConcatenationDescriptor descriptor)
	{
		super(descriptor);
		
		fDataSelectors = descriptor.fDataSelectors;
		fSeparator = descriptor.fSeparator;
	}
	

	/**
	 * Returns a (deep) copy of this DataConcatenationDescriptor.
	 *
	 * @return A (deep) copy of this DataConcatenationDescriptor 
	**/
	
	public Object clone()
	{
		DataConcatenationDescriptor result = 
			(DataConcatenationDescriptor) super.clone();
		
		result.fSeparator = fSeparator;
		result.fDataSelectors = new LinkedHashSet(fDataSelectors);
		
		return (result);
	}
	
	
	/** 
	 *  Returns the ordered set of DataSelectorDescriptors defined on this 
	 *  DataConcatenationDescriptor.
	 *
	 *  @return The ordered set of DataSelectorDescriptors defined on this 
	 *  		DataConcatenationDescriptor
	**/
	
	public Set getDataSelectors()
	{
		return (Collections.unmodifiableSet(fDataSelectors));
	}
	
	
	/** 
	 *  Returns the separator defined on this DataConcatenationDescriptor.
	 *
	 *  @return The separator defined on this DataConcatenationDescriptor
	**/
	
	public String getSeparator()
	{
		return (fSeparator);
	}
	
	
	/** 
	 *  Returns a String representation of this DataConcatenationDescriptor.
	 *
	 *  @return A String representation of this DataConcatenationDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("DataConcatenationDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fSeparator != null)
		{
			result.append("\nSeparator: " + fSeparator);
		}
		
		Iterator selections = fDataSelectors.iterator();
		
		for (int i = 1; selections.hasNext(); i++)
		{
			DataSelectorDescriptor selection = (DataSelectorDescriptor) 
				selections.next();
			
			result.append("\nSelection " + i + ": " + selection);
		}
		
		return (result.toString());
	}


	/**
	  * Unmarshalls this DataConcatenationDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the set of DataSelectorDescriptors (if any).
		Map selectionMap = new LinkedHashMap();
		
		fDataSelectors.clear();
		
		fSerializer.loadChildDescriptorElements(Dataselml.E_SELECTION, 
			selectionMap, Dataselml.C_DATA_SELECTION, fElement, this, fDirectory);
		
		Iterator selections = selectionMap.values().iterator();
		
		while (selections.hasNext())
		{
			DataSelectionDescriptor selection = (DataSelectionDescriptor) 
				selections.next();
			
			DataSelectorDescriptor selector = selection.getDataSelector();
			
			fDataSelectors.add(selector);
		}
		
		// Unmarshall the separator attribute (if any).
		fSeparator = fSerializer.loadStringAttribute
			(Dataselml.A_SEPARATOR, " ", fElement);
	}
}
