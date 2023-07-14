//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/description/BasisBundleGroupDescriptor.java,v 1.3 2006/01/23 17:59:50 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: BasisBundleGroupDescriptor.java,v $
//  Revision 1.3  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
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

package gov.nasa.gsfc.irc.data.description;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;

import gov.nasa.gsfc.commons.types.namespaces.DefaultMemberSet;
import gov.nasa.gsfc.commons.types.namespaces.MemberSet;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A BasisBundleGroupDescriptor describes a group of BasisBundles.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:50 $
 * @author Carl F. Hostetter
 */

public class BasisBundleGroupDescriptor extends AbstractIrcElementDescriptor
{
	private boolean fPrefixGroupNameToBundleNames = false;
	private BasisBundleDescriptor fDefaults;
	
	private MemberSet fBasisBundles = new DefaultMemberSet();
	
	
	/**
	 * Constructs a new BasisBundleGroupDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by 
	 * unmarshalling from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of this BasisBundleGroupDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		BasisBundleGroupDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		BasisBundleGroupDescriptor		
	**/
	
	public BasisBundleGroupDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Dataml.N_DATA);
		
		xmlUnmarshall();
	}
	

	/**
	 *	Returns true if this BasisBundleGroupDescriptor is configured to 
	 *  prefix its name to the name of each BasisBundle in its group, false 
	 *  otherwise.
	 *
	 *  @return True if this BasisBundleGroupDescriptor is configured to 
	 *  	prefix its name to the name of each BasisBundle in its group, false 
	 *  	otherwise
	 */
	
	public boolean prefixesGroupNameToBundleNames()
	{
		return (fPrefixGroupNameToBundleNames);
	}


	/**
	 *	Returns the default BasisBundleDescriptor associated with this 
	 *  BasisBundleGroupDescriptor (if any).
	 *
	 *  @return The default BasisBundleDescriptor associated with this 
	 *  	BasisBundleGroupDescriptor (if any)
	 */
	
	public BasisBundleDescriptor getDefaults()
	{
		return (fDefaults);
	}


	/**
	 *	Returns the Set of BasisBundleDescriptors associated with this 
	 *  BasisBundleGroupDescriptor (if any).
	 *
	 *  @return The set of BasisBundleDescriptors associated with this 
	 *  		BasisBundleGroupDescriptor (if any)
	 */
	
	public Set getBasisBundles()
	{
		return (fBasisBundles.getMembers());
	}


	/**
	 *	Returns a Map of the BasisBundleDescriptors associated with this 
	 *  BasisBundleGroupDescriptor (if any), keyed by fully-qualified name.
	 *
	 *  @return A Map of the BasisBundleDescriptors associated with this 
	 *  		BasisBundleGroupDescriptor (if any), keyed by fully-qualified name
	 */
	
	public Map getBasisBundlesByFullyQualifiedName()
	{
		return (fBasisBundles.getMembersByFullyQualifiedName());
	}


	/**
	 *	Returns a String representation of this BasisBundleDescriptor.
	 *
	 *  @return A String representation of this BasisBundleDescriptor
	 */
	
	public String toString()
	{
		StringBuffer stringRep = 
			new StringBuffer("BasisBundleGroupDescriptor: " + 
				super.toString());
		
		if (fPrefixGroupNameToBundleNames)
		{
			stringRep.append("\nPrefixes name to bundle names");	
		}

		stringRep.append("\nDefaults: " + fDefaults);
		
		if (fBasisBundles != null)
		{
			stringRep.append("\nBasisBundles: ");
			
			Iterator basisBundles = fBasisBundles.iterator();
			
			while (basisBundles.hasNext())
			{
				BasisBundleDescriptor descriptor = (BasisBundleDescriptor) 
					basisBundles.next();
					
				stringRep.append("\n" + descriptor);
			}
		}
		
		return (stringRep.toString());
	}


	/**
	 * Unmarshalls a BasisBundleDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Load the default BasisBundleDescriptor
		fDefaults = (BasisBundleDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Dataml.E_BASIS_BUNDLE_DEFAULTS, Dataml.C_BASIS_BUNDLE, 
					fElement, this, fDirectory);
		
		// Load the prefixGroupNameToBundleNames attribute
		fPrefixGroupNameToBundleNames = fSerializer.loadBooleanAttribute
			(Dataml.A_PREFIX_GROUP_NAME_TO_BUNDLE_NAMES, false, fElement);
		
		Map basisBundlesByName = new LinkedHashMap();
		
		// Load the BasisBundleDescriptors defined in the group
		fSerializer.loadChildDescriptorElements
			(Dataml.E_BASIS_BUNDLE, basisBundlesByName, Dataml.C_BASIS_BUNDLE, 
				fElement, fParent, fDirectory);
		
		// Modify the descriptors in accordance with any defaults and prefixed 
		// group name
		
		if ((fDefaults != null) || (fPrefixGroupNameToBundleNames))
		{
			DataBufferDescriptor defaultBasisBuffer = null;
			Set defaultDataBuffers = null;
			int defaultSize = 0;
			
			if (fDefaults != null)
			{
				defaultBasisBuffer = fDefaults.getBasisBufferDescriptor();
				defaultDataBuffers = fDefaults.getDataBufferDescriptors();
				defaultSize = fDefaults.getSize();
			}
			
			Map modifiedBasisBundles = new LinkedHashMap();
			
			Iterator basisBundles = basisBundlesByName.values().iterator();
			
			while (basisBundles.hasNext())
			{
				BasisBundleDescriptor descriptor = 
					(BasisBundleDescriptor) basisBundles.next();
				
				ModifiableBasisBundleDescriptor modifiableDescriptor = 
					(ModifiableBasisBundleDescriptor) descriptor.getModifiableCopy();
				
				if (fPrefixGroupNameToBundleNames)
				{
					modifiableDescriptor.setName(getName() + " " + 
						descriptor.getName());
				}
				
				if ((modifiableDescriptor.getSize() < 0) && (defaultSize >= 0))
				{
					modifiableDescriptor.setSize(defaultSize);
				}
				
				if ((modifiableDescriptor.getBasisBufferDescriptor() == null) && 
					(defaultBasisBuffer != null))
				{
					modifiableDescriptor.setBasisBufferDescriptor(defaultBasisBuffer);
				}
				
				if (defaultDataBuffers != null)
				{
					Iterator defaultDataBufferDescriptors = 
						defaultDataBuffers.iterator();
					
					while (defaultDataBufferDescriptors.hasNext())
					{
						DataBufferDescriptor defaultDataBuffer = 
							(DataBufferDescriptor) defaultDataBufferDescriptors.next();
						
						modifiableDescriptor.addDataEntryDescriptor(defaultDataBuffer);
					}
				}
				
				modifiedBasisBundles.put
					(modifiableDescriptor.getName(), modifiableDescriptor);
			}
			
			fBasisBundles.addAll(modifiedBasisBundles.values());
		}
		else
		{
			fBasisBundles.addAll(basisBundlesByName.values());
		}
	}
}
