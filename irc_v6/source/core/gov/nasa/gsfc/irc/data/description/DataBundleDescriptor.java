//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/description/DataBundleDescriptor.java,v 1.6 2006/01/23 17:59:50 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataBundleDescriptor.java,v $
//  Revision 1.6  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.4  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.3  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
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

package gov.nasa.gsfc.irc.data.description;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataBundleDescriptor is a DataEntryBundleDescriptor that contains and manages a 
 * Set of other, potentially heterogenous DataEntryDescriptors, the whole describing 
 * a structured Set of potentionally heterogeneous DataBuffers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:50 $
 * @author Carl F. Hostetter
 */

public class DataBundleDescriptor extends DataEntryBundleDescriptor
{
	/**
	 * Constructs a new DataBundleDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataBundleDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataBundleDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataBundleDescriptor		
	**/
	
	public DataBundleDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new DataBundleDescriptor having the given name.
	 * 
	 * @param name The name of the new DataBundleDescriptor
	**/
	
	public DataBundleDescriptor(String name)
	{
		super(name);
	}

	
	/**
	 *	Constructs a DataBundleDescriptor having the given name and the 
	 *  given Set of DataEntryDescriptors.
	 *
	 *  @param name The name of the new DataBundleDescriptor
	 *  @param dataEntryDescriptors A Set of DataEntryDescriptors
	 */
	
	public DataBundleDescriptor(String name, Collection dataEntryDescriptors)
	{
		super(name);
		
		setDataEntryDescriptors(dataEntryDescriptors);
	}
	
	
	/**
	 *	Returns a modifiable copy of this DataBundleDescriptor.
	 *
	 *  @return A modifiable copy of this DataBundleDescriptor
	 */
	
	public DataElementDescriptor getModifiableCopy()
	{
		return (new ModifiableDataBundleDescriptor(this));
	}	
	

	/**
	 *	Returns a String representation of this DataEntryBundleDescriptor.
	 *
	 *  @return A String representation of this DataEntryBundleDescriptor
	 */
	
	public String toString()
	{
		StringBuffer stringRep = 
			new StringBuffer("DataBundleDescriptor Name: " + getName() + "\n");
		
		stringRep.append("DataEntryDescriptors: ");
		
		Iterator dataEntryDescriptors = fDataEntryDescriptors.iterator();
			
		if (dataEntryDescriptors.hasNext())
		{
			for (int i = 1; dataEntryDescriptors.hasNext(); i++)
			{
				DataElementDescriptor descriptor = (DataElementDescriptor) 
					dataEntryDescriptors.next();
				
				stringRep.append("\n" + i + ": " + descriptor);
			}
		}
		else
		{
			stringRep.append("none");
		}
		
		return (stringRep.toString());
	}
	
	
	/**
	 * Unmarshalls a DataBundleDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// We iterate through and load each child Element separately in order 
		// to preserve the specification order of the DataBuffers.
		
		Map dataEntryDescriptorsByName = new LinkedHashMap();
		
		Iterator children = fElement.getChildren().iterator();
		
		while (children.hasNext())
		{
			Element child = (Element) children.next();
			String name = child.getName();
			
			if (name.equals(Dataml.E_DATA_BUFFER))
			{
				// Load a DataBuffer
				fSerializer.loadDescriptorElement(Dataml.E_DATA_BUFFER,
					dataEntryDescriptorsByName, Dataml.C_DATA_BUFFER, child, this,
					fDirectory);
			}
			else if (name.equals(Dataml.E_PIXEL_BUNDLE))
			{
				fSerializer.loadDescriptorElement(Dataml.E_PIXEL_BUNDLE,
					dataEntryDescriptorsByName, Dataml.C_PIXEL_BUNDLE, child, this,
					fDirectory);
			}
			else if (name.equals(Dataml.E_DATA_BUNDLE))
			{
				// Load any sub-bundles
				fSerializer.loadDescriptorElement(Dataml.E_DATA_BUNDLE,
					dataEntryDescriptorsByName, Dataml.C_DATA_BUNDLE, child, this,
					fDirectory);
			}
		}
		
		fDataEntryDescriptors.addAll(dataEntryDescriptorsByName.values());
	}
}
