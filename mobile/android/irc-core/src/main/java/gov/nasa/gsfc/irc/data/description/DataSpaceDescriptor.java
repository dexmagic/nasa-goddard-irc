//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/description/DataSpaceDescriptor.java,v 1.3 2006/01/23 17:59:50 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataSpaceDescriptor.java,v $
//  Revision 1.3  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.6  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.5  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.4  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.3  2004/09/10 23:15:00  chostetter_cvs
//  Checking in for weekend, still needs some PixelBundle debugging
//
//  Revision 1.2  2004/09/10 14:49:03  chostetter_cvs
//  More data description work
//
//  Revision 1.1  2004/09/07 18:02:39  chostetter_cvs
//  Defines DataSpaceDescriptor as top-most level of data description
//
//  Revision 1.2  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.1  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
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
 * A DataSpaceDescriptor describes a Set of one or more BasisBundles.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:50 $
 * @author Carl F. Hostetter
 */

public class DataSpaceDescriptor extends AbstractIrcElementDescriptor
{
	private MemberSet fBasisBundleDescriptors;
	
	
	/**
	 * Constructs a new DataSpaceDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataSpaceDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataSpaceDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataSpaceDescriptor		
	**/
	
	public DataSpaceDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Dataml.N_DATA);
		
		fBasisBundleDescriptors = new DefaultMemberSet();
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new DataSpaceDescriptor having the given name.
	 * 
	 * @param name The name of the new DataSpaceDescriptor
	**/
	
	public DataSpaceDescriptor(String name)
	{
		super(name);
		
		fBasisBundleDescriptors = new DefaultMemberSet();
	}

	
	/**
	 *  Returns the Set of names of the BasisBundleDescriptors of this 
	 *  DataSpaceDescriptor.
	 *  
	 *  @return The Set of names of the BasisBundleDescriptors of this 
	 * 		DataSpaceDescriptor
	 */
	
	public Set getBasisBundleNames()
	{
		return (fBasisBundleDescriptors.getFullyQualifiedNames());
	}
	
	
	/**
	 *	Returns the Set of BasisBundleDescriptors of this DataSpaceDescriptor.
	 *
	 *  @return The Set of BasisBundleDescriptors of this DataSpaceDescriptor
	 */
	
	public Set getBasisBundleDescriptors()
	{
		return (fBasisBundleDescriptors.getMembers());
	}


	/**
	 *	Returns a String representation of this DataSpaceDescriptor.
	 *
	 *  @return A String representation of this DataSpaceDescriptor
	 */
	
	public String toString()
	{
		StringBuffer stringRep = 
			new StringBuffer("DataSpaceDescriptor Name: " + getName() + "\n");
		
		stringRep.append("BasisBundleDescriptors: ");
		
		Iterator basisBundleDescriptors = 
			fBasisBundleDescriptors.iterator();
			
		if (basisBundleDescriptors.hasNext())
		{
			for (int i = 1; basisBundleDescriptors.hasNext(); i++)
			{
				BasisBundleDescriptor descriptor = (BasisBundleDescriptor) 
					basisBundleDescriptors.next();
				
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
	 * Unmarshalls a DataSpaceDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		Map basisBundleDescriptorsByName = new LinkedHashMap();
		
		// Load the Set of BasisBundles (if any)
		fSerializer.loadChildDescriptorElements(Dataml.E_BASIS_BUNDLE,
			basisBundleDescriptorsByName, Dataml.C_BASIS_BUNDLE, fElement, null,
			fDirectory);
		
		fBasisBundleDescriptors.addAll(basisBundleDescriptorsByName.values());
	}
}
