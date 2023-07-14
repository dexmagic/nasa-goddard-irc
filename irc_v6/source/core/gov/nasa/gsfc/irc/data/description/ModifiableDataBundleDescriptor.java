//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/description/ModifiableDataBundleDescriptor.java,v 1.3 2006/01/23 17:59:50 chostetter_cvs Exp $
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


/**
 * A ModifiableDataBundleDescriptor is a modifiable copy of a 
 * DataBundleDescriptor.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:50 $
 * @author Carl F. Hostetter
 */

public class ModifiableDataBundleDescriptor extends DataBundleDescriptor
{
	/**
	 *	Constructs a modifiable copy of the given DataBundleDescriptor.
	 *
	 *  @param descriptor A DataBundleDescriptor
	 */
	
	public ModifiableDataBundleDescriptor(DataBundleDescriptor descriptor)
	{
		super(descriptor.getName(), descriptor.getDataEntryDescriptors());
		
		setDataEntryDescriptors(descriptor.getDataEntryDescriptors());
	}


	/**
	 *	Sets the name of this ModifiableDataBundleDescriptor to the given 
	 *	name.
	 *
	 *  @param name The new name of this ModifiableDataBundleDescriptor
	 */
	
	public void setName(String name)
	{
		super.setName(name);
	}


	/**
	 *	Sets the Set of DataEntryDescriptors of this DataBundleDescriptor to the 
	 *  given Set of DataEntryDescriptors. Note that each DataElementDescriptor in the 
	 *  resulting Set will also be modifiable.
	 *
	 *  @param dataEntryDescriptors A Set of DataEntryDescriptors
	 */
	
	public void setDataEntryDescriptors(Collection dataEntryDescriptors)
	{
		if (dataEntryDescriptors != null)
		{
			Set modifiableDescriptors = 
				new LinkedHashSet(dataEntryDescriptors.size());
			
			Iterator descriptors = dataEntryDescriptors.iterator();
			
			while (descriptors.hasNext())
			{
				DataElementDescriptor descriptor = (DataElementDescriptor)
					descriptors.next();
				
				DataElementDescriptor modifiableDescriptor = 
					descriptor.getModifiableCopy();
				
				modifiableDescriptors.add(modifiableDescriptor);
			}
			
			super.setDataEntryDescriptors(modifiableDescriptors);
		}
	}


	/**
	 *	Adds the given DataElementDescriptor to the Set of DataEntryDescriptors of 
	 *  this DataBundleDescriptor.
	 *
	 *  @param descriptor A DataElementDescriptor
	 */
	
	public void addDataEntryDescriptor(DataElementDescriptor descriptor)
	{
		super.addDataEntryDescriptor(descriptor);
	}
	
	
	/**
	 *	Removes the given DataElementDescriptor from the Set of DataEntryDescriptors 
	 *  of this DataBundleDescriptor.
	 *
	 *  @param descriptor A DataElementDescriptor
	 *  @return True if the given DataBufferDescriptor was actually removed
	 */
	
	public boolean removeDataEntryDescriptor(DataElementDescriptor descriptor)
	{
		return (super.removeDataEntryDescriptor(descriptor));
	}
	
	
	/**
	 *	Removes the Set of DataEntryDescriptors of this DataBundleDescriptor.
	 *
	 */
	
	public void removeDataEntryDescriptors()
	{
		super.removeDataEntryDescriptors();
	}
}
