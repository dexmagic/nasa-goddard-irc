//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/description/DataBufferBundleDescriptor.java,v 1.6 2006/01/23 17:59:50 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: 
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

import java.util.Iterator;
import java.util.Set;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataBufferBundleDescriptor is a DataEntryBundleDescriptor that contains and 
 * manages a Set of potentially heterogenous DataBufferDescriptors, the whole 
 * describing a linear Set of potentionally heterogeneous DataBuffers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:50 $
 * @author Carl F. Hostetter
 */

public abstract class DataBufferBundleDescriptor extends DataEntryBundleDescriptor
{
	/**
	 * Constructs a new DataBufferBundleDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataBufferBundleDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataBufferBundleDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataBufferBundleDescriptor		
	**/
	
	public DataBufferBundleDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 *	Constructs a DataBufferBundleDescriptor having the given name and the 
	 *  given Set of DataBufferDescriptors.
	 *
	 *  @param name The name of the new DataBundleDescriptor
	 *  @param dataBufferDescriptors A Set of DataBufferDescriptors
	 */
	
	public DataBufferBundleDescriptor(String name, Set dataBufferDescriptors)
	{
		super(name);
		
		setDataEntryDescriptors(dataBufferDescriptors);
	}
	
	
	/**
	 *	Constructs a DataBufferBundleDescriptor having the given name.
	 *
	 *  @param name The name of the new DataBundleDescriptor
	 */
	
	protected DataBufferBundleDescriptor(String name)
	{
		super(name);
	}
	
	
	/**
	 *  Constructs a new DataBufferBundleDescriptor via XML unmarshalling.
	 *
	 *  @param parent Manager descriptor
	 *  @param directory Descriptor directory
	 *  @param element JDOM subtree root element
	**/
	
	public DataBufferBundleDescriptor(IrcElementDescriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 *	Returns a modifiable copy of this DataBufferBundleDescriptor.
	 *
	 *  @return A modifiable copy of this DataBufferBundleDescriptor
	 */
	
	public DataElementDescriptor getModifiableCopy()
	{
		return (new ModifiableDataBufferBundleDescriptor(this));
	}	
	

	/**
	 *	Sets the Set of DataBufferDescriptors of this DataBufferBundleDescriptor 
	 *  to the given Set of DataBufferDescriptors.
	 *
	 *  @param dataBufferDescriptors A Set of DataBufferDescriptors
	 */
	
	protected void setDataBufferDescriptors(Set dataBufferDescriptors)
	{
		Iterator descriptors = dataBufferDescriptors.iterator();
		
		while (descriptors.hasNext())
		{
			DataElementDescriptor descriptor = 
				(DataElementDescriptor) descriptors.next();
			
			addDataEntryDescriptor(descriptor);
		}
	}


	/**
	 *	Adds the given DataBufferDescriptor to the Set of DataBufferDescriptors 
	 *  of this DataBufferBundleDescriptor.
	 *
	 *  @param descriptor A DataBufferDescriptor
	 */
	
	protected void addDataBufferDescriptor(DataBufferDescriptor descriptor)
	{
		super.addDataEntryDescriptor(descriptor);
	}
	
	
	/**
	 *	Adds the given DataBufferDescriptor to the Set of DataBufferDescriptors 
	 *  of this DataBufferBundleDescriptor.
	 *
	 *  @param descriptor A DataBufferDescriptor
	 */
	
	protected void addDataEntryDescriptor(DataElementDescriptor descriptor)
	{
		if (descriptor instanceof DataBufferDescriptor)
		{
			super.addDataEntryDescriptor(descriptor);
		}
		else
		{
			String message = "Attempted to add a non-DataBufferDescriptor to " + 
				"a DataBufferBundleDescriptor";
			
			throw (new IllegalArgumentException(message));
		}
	}
	
	
	/**
	 *	Removes the given DataBufferDescriptor from the Set of 
	 *  DataBufferDescriptors of this DataBufferBundleDescriptor.
	 *
	 *  @param descriptor A DataBufferDescriptor
	 */
	
	protected void removeDataBufferDescriptor(DataBufferDescriptor descriptor)
	{
		super.removeDataEntryDescriptor(descriptor);
	}
	
	
	/**
	 *	Returns a String representation of this DataBufferBundleDescriptor.
	 *
	 *  @return A String representation of this DataBufferBundleDescriptor
	 */
	
	public String toString()
	{
		StringBuffer stringRep = 
			new StringBuffer("DataBufferBundleDescriptor Name: " + 
				getName() + "\n");
		
		stringRep.append("DataBufferDescriptors: ");
		
		Iterator dataDescriptorIterator = getDataEntryDescriptors().iterator();
			
		if (dataDescriptorIterator.hasNext())
		{
			for (int i = 1; dataDescriptorIterator.hasNext(); i++)
			{
				DataBufferDescriptor descriptor = (DataBufferDescriptor) 
					dataDescriptorIterator.next();
				
				stringRep.append("\n" + i + ": " + descriptor);
			}
		}
		else
		{
			stringRep.append("none");
		}
		
		return (stringRep.toString());
	}
}
