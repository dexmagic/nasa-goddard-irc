//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/description/ModifiableDataBufferBundleDescriptor.java,v 1.4 2006/01/23 17:59:50 chostetter_cvs Exp $
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
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * A ModifiableDataBufferBundleDescriptor is a modifiable copy of a 
 * DataBundleDescriptor.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:50 $
 * @author Carl F. Hostetter
 */

public class ModifiableDataBufferBundleDescriptor 
	extends DataBufferBundleDescriptor
{
	/**
	 *	Constructs a modifiable copy of the given DataBufferBundleDescriptor.
	 *
	 *  @param descriptor A DataBufferBundleDescriptor
	 */
	
	public ModifiableDataBufferBundleDescriptor
		(DataBufferBundleDescriptor descriptor)
	{
		super(descriptor.getName());
		
		setDataDescriptors(descriptor.getDataBufferDescriptors());
	}
	
	
	/**
	 *	Sets the Set of DataBufferDescriptors of this 
	 *  ModifiableDataBufferBundleDescriptor to the given Set of 
	 *  DataBufferDescriptors. Note that each DataBufferDescriptor in the 
	 *  resulting set will also be modifiable.
	 *
	 *  @param dataBufferDescriptors A Set of DataBufferDescriptors
	 */
	
	public void setDataDescriptors(Set dataBufferDescriptors)
	{
		if (dataBufferDescriptors != null)
		{
			Set modifiableDescriptors = 
				new LinkedHashSet(dataBufferDescriptors.size());
			
			Iterator descriptors = dataBufferDescriptors.iterator();
			
			while (descriptors.hasNext())
			{
				DataBufferDescriptor descriptor = (DataBufferDescriptor)
					descriptors.next();
				
				ModifiableDataBufferDescriptor modifiableDescriptor = 
					(ModifiableDataBufferDescriptor) 
						descriptor.getModifiableCopy();
				
				modifiableDescriptors.add(modifiableDescriptor);
			}
			
			super.setDataEntryDescriptors(modifiableDescriptors);
		}
	}


	/**
	 *	Adds the given DataBufferDescriptor to the Set of DataBufferDescriptors 
	 *  of this ModifiableDataBufferBundleDescriptor.
	 *
	 *  @param descriptor A DataBufferDescriptor
	 */
	
	public void addDataBufferDescriptor(DataBufferDescriptor descriptor)
	{
		super.addDataBufferDescriptor(descriptor);
	}
	
	
	/**
	 *	Removes the given DataBufferDescriptor from the Set of 
	 *  DataBufferDescriptors of this ModifiableDataBufferBundleDescriptor.
	 *
	 *  @param descriptor A DataBufferDescriptor
	 *  @return True if the given DataBufferDescriptor was actually removed
	 */
	
	public boolean removeDataEntryDescriptor(DataElementDescriptor descriptor)
	{
		return (super.removeDataEntryDescriptor(descriptor));
	}
	
	
	/**
	 *	Removes the Set of DataBufferDescriptors of this 
	 *  ModifiableDataBufferBundleDescriptor.
	 *
	 */
	
	public void removeDataEntryDescriptors()
	{
		super.removeDataEntryDescriptors();
	}
}
