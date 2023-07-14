//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: ParameterDescriptor.java,v $
//  Revision 1.7  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.6  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.5  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.3  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//  Initial version
//
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.description.xml;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.description.Dataml;
import gov.nasa.gsfc.irc.description.Descriptor;


/**
 * The class provides access to information describing a parameter.<P>
 * 
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/01/23 17:59:54 $
 * @author John Higinbotham
**/

public class ParameterDescriptor extends AbstractIrcElementDescriptor
{
	private String fValue; 

	
	/**
	 *  Constructs a new ParameterDescriptor having the given base name and no name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new ParameterDescriptor
	 **/

	public ParameterDescriptor(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new ParameterDescriptor having the given base name and name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new ParameterDescriptor
	 *  @param nameQualifier The name qualifier of the new ParameterDescriptor
	 **/

	public ParameterDescriptor(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
	
	
	/**
	 * Constructs a new ParameterDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ParameterDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ParameterDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ParameterDescriptor		
	**/
	
	public ParameterDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Dataml.N_DATA);
		
		xmlUnmarshall();
	}
	

	/**
	 * Returns the value associated with this parameter.
	 *
	 * @return The value associated with this parameter 
	**/
	
	public String getValue()
	{
		return (fValue);
	}


	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	private void xmlUnmarshall()
	{
		//---Load value 
		fValue = fSerializer.loadStringAttribute(Dataml.A_VALUE, fElement);
	}
}
