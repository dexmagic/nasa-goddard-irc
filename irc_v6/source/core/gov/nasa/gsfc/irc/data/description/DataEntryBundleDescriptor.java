//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/description/DataEntryBundleDescriptor.java,v 1.6 2006/01/23 17:59:50 chostetter_cvs Exp $
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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jdom.Element;

import gov.nasa.gsfc.commons.types.namespaces.DefaultMemberSet;
import gov.nasa.gsfc.commons.types.namespaces.HasFullyQualifiedName;
import gov.nasa.gsfc.commons.types.namespaces.Member;
import gov.nasa.gsfc.commons.types.namespaces.MemberSet;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataEntryBundleDescriptor is a DataElementDescriptor that contains and 
 * manages a Set of other, potentially heterogenous DataEntryDescriptors, the 
 * whole describing a structured Set of potentionally heterogeneous 
 * DataBuffers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:50 $
 * @author Carl F. Hostetter
 */

public abstract class DataEntryBundleDescriptor extends DataElementDescriptor
{
	protected MemberSet fDataEntryDescriptors;

	
	/**
	 * Constructs a new DataEntryBundleDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataEntryBundleDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataEntryBundleDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataEntryBundleDescriptor		
	**/
	
	public DataEntryBundleDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		fDataEntryDescriptors = new DefaultMemberSet();
	}
	

	/**
	 * Constructs a new DataEntryBundleDescriptor having the given name.
	 * 
	 * @param name The name of the new DataEntryBundleDescriptor
	**/
	
	public DataEntryBundleDescriptor(String name)
	{
		super(name);
		
		fDataEntryDescriptors = new DefaultMemberSet();
	}

	
	/**
	 *	Constructs a DataEntryBundleDescriptor having the given name and the 
	 *  given Set of DataEntryDescriptors.
	 *
	 *  @param name The name of the new DataEntryBundleDescriptor
	 *  @param dataEntryDescriptors A Set of DataEntryDescriptors
	 */
	
	public DataEntryBundleDescriptor(String name, 
		Collection dataEntryDescriptors)
	{
		super(name);
		
		setDataEntryDescriptors(dataEntryDescriptors);
	}
	
	
	/**
	 *  Constructs a new DataEntryBundleDescriptor via XML unmarshalling.
	 *
	 *  @param parent Manager descriptor
	 *  @param directory Descriptor directory
	 *  @param element JDOM subtree root element
	**/
	
	public DataEntryBundleDescriptor(IrcElementDescriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 *	Sets the Set of DataEntryDescriptors of this 
	 *  DataEntryBundleDescriptor to the given Set of DataEntryDescriptors.
	 *
	 *  @param dataEntryDescriptors A Set of DataEntryDescriptors
	 */
	
	protected void setDataEntryDescriptors(Collection dataEntryDescriptors)
	{
		fDataEntryDescriptors.clear();
		
		if (dataEntryDescriptors != null)
		{
			Iterator descriptors = dataEntryDescriptors.iterator();
				
			while (descriptors.hasNext())
			{
				DataElementDescriptor descriptor = 
					(DataElementDescriptor) descriptors.next();
				
				addDataEntryDescriptor(descriptor);
			}
		}
	}


	/**
	 *	Adds the given DataElementDescriptor to the Set of DataEntryDescriptors 
	 *  of this DataEntryBundleDescriptor.
	 *
	 *  @param descriptor A DataElementDescriptor
	 */
	
	protected void addDataEntryDescriptor(DataElementDescriptor descriptor)
	{
		descriptor.setNameQualifier(this);
		
		fDataEntryDescriptors.add((Member) descriptor);
	}
	
	
	/**
	 *	Removes the given DataElementDescriptor from the Set of 
	 *  DataEntryDescriptors of this DataEntryBundleDescriptor.
	 *
	 *  @param descriptor A DataElementDescriptor
	 *  @return True if the given DataElementDescriptor was actually removed
	 */
	
	protected boolean removeDataEntryDescriptor(DataElementDescriptor descriptor)
	{
		boolean result = fDataEntryDescriptors.remove((Member) descriptor);
		
		if (result == true)
		{
			descriptor.setNameQualifier((HasFullyQualifiedName) null);
		}
		
		return (result);
	}
	
	
	/**
	 *	Removes the DataElementDescriptor having the given fully-qualified name 
	 *  from the Set of DataEntryDescriptors of this DataEntryBundleDescriptor.
	 *
	 *  @param name The name of the DataElementDescriptor to be removed
	 *  @return The DataElementDescriptor that was removed
	 */
	
	protected DataElementDescriptor removeDataEntryDescriptor(String name)
	{
		DataElementDescriptor descriptor = (DataElementDescriptor) 
			fDataEntryDescriptors.remove(name);
		
		if (descriptor != null)
		{
			descriptor.setNameQualifier((HasFullyQualifiedName) null);
		}
		
		return (descriptor);
	}
	
	
	/**
	 *	Removes the Set of DataEntryDescriptors of this DataEntryBundleDescriptor.
	 *
	 */
	
	protected void removeDataEntryDescriptors()
	{
		fDataEntryDescriptors.clear();
	}
	
	
	/**
	 *  Returns the Set of fully-qualified names of the DataEntryDescriptors of this 
	 *  DataEntryBundleDescriptor.
	 *  
	 *  @return The Set of fully-qualified names of the DataEntryDescriptors of this 
	 * 		DataEntryBundleDescriptor
	 */
	
	public Set getDataEntryNames()
	{
		return (fDataEntryDescriptors.getFullyQualifiedNames());
	}
	
	
	/**
	 *	Returns the (potentially structured) Set of DataEntryDescriptors of this 
	 *  DataEntryBundleDescriptor.
	 *
	 *  @return The Set of DataEntryDescriptors of this DataEntryBundleDescriptor
	 */
	
	public Set getDataEntryDescriptors()
	{
		return (fDataEntryDescriptors.getMembers());
	}


	/**
	 *	Returns the ordered (but unstructured) Set of DataBufferDescriptors of 
	 *  this DataEntryBundleDescriptor.
	 *
	 *  @return The Set of DataBufferDescriptors of this 
	 *  	DataEntryBundleDescriptor
	 */
	
	public Set getDataBufferDescriptors()
	{
		Set dataBufferDescriptors = new LinkedHashSet();
		
		Iterator dataEntryDescriptors = fDataEntryDescriptors.iterator();
		
		while (dataEntryDescriptors.hasNext())
		{
			DataElementDescriptor descriptor = 
				(DataElementDescriptor) dataEntryDescriptors.next();
			
			if (descriptor instanceof DataBufferDescriptor)
			{
				dataBufferDescriptors.add(descriptor);
			}
			else
			{
				dataBufferDescriptors.addAll
					(((DataEntryBundleDescriptor) descriptor).
						getDataBufferDescriptors());
			}
		}
		
		return (dataBufferDescriptors);
	}


	/**
	 *  Returns the ordered Set of DataBufferDescriptors of this 
	 *  DataEntryBundleDescriptor whose fully-qualified names match the given 
	 *  regular expression pattern.
	 *
	 *  @param regExPattern A regular expression pattern 
	 *  @return The ordered Set of DataBufferDescriptors of this 
	 *  		DataEntryBundleDescriptor whose fully-qualified names match the given 
	 *  		regular expression pattern
	 **/

	public Set getDataBufferDescriptors(String regExPattern)
	{
		return (fDataEntryDescriptors.
			getByFullyQualifiedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *	Returns a String representation of this DataEntryBundleDescriptor.
	 *
	 *  @return A String representation of this DataEntryBundleDescriptor
	 */
	
	public String toString()
	{
		StringBuffer stringRep = 
			new StringBuffer("DataEntryBundleDescriptor Name: " + 
				getName() + "\n");
		
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
}
