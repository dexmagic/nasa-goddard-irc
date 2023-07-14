//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: ParameterSetDescriptor.java,v $
//	Revision 1.8  2006/01/23 17:59:54  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.7  2005/09/13 20:30:12  chostetter_cvs
//	Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//	
//	Revision 1.6  2004/10/14 15:16:51  chostetter_cvs
//	Extensive descriptor-oriented refactoring
//	
//	Revision 1.1  2004/10/07 21:38:26  chostetter_cvs
//	More descriptor refactoring, initial version of data transformation description
//	
//	Revision 1.4  2004/09/14 16:02:22  chostetter_cvs
//	Fixed DataBundleDescriptor ordering problem
//	
//	Revision 1.3  2004/09/07 05:22:19  tames
//	Descriptor cleanup
//	
//	Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//	Initial version
//	
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*  Neither the author, their corporation, nor NASA is responsible for
//	   any consequence of the use of this software.
//	*  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//	*  Altered versions of this software must be plainly marked as such.
//	*  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.description.xml;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.description.Dataml;
import gov.nasa.gsfc.irc.description.Descriptor;


/**
 * The class provides access to information describing a parameter set. Parameter
 * sets are defined in xml and give users an easy way to associate expressions with 
 * parameter name for use by scripts and pipeline algorithms.<P>
 * 
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			$Date: 2006/01/23 17:59:54 $
 * @author				John Higinbotham
**/

public class ParameterSetDescriptor extends AbstractIrcElementDescriptor
{
	private String fTypeString; // One of Generic, Channel, Row, Column, others?
	private Class fParameterSetClass;	// Class of ParameterSet
	private Map fParameters;  // Collection of multi-valued parameters 


	/**
	 *  Constructs a new ParameterSetDescriptor having the given base name and 
	 *  no name qualifier.
	 * 
	 *  @param name The base name of the new ParameterSetDescriptor
	 **/

	public ParameterSetDescriptor(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new ParameterSetDescriptor having the given base name and name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new ParameterSetDescriptor
	 *  @param nameQualifier The name qualifier of the new ParameterSetDescriptor
	 **/

	public ParameterSetDescriptor(String name, String nameQualifier)
	{
		super(name, nameQualifier);
		
		fParameters = new LinkedHashMap();
	}
	
	
	/**
	 * Constructs a new ParameterSetDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ParameterSetDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ParameterSetDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ParameterSetDescriptor		
	**/
	
	public ParameterSetDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Dataml.N_DATA);
		
		fParameters = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	

	/**
	 * Get the parameterset's class.
	 *
	 * @return parameterset's class.
	 *						
	**/
	public Class getParameterSetClass()
	{
		 return fParameterSetClass;
	}

	/**
	 * Get the type of the parameter set. 
	 *
	**/
	public String getType()
	{
		return fTypeString; 
	}

	/**
	 * Get a parameter by name.
	 *
	 * @param	name Name of requested item.
	 * @return Parameter with specified name. 
	 *		   
	**/
	public ParameterDescriptor getParameterByName(String name)
	{
		return (ParameterDescriptor) fParameters.get(name);
	}

	/**
	 * Get the collection of parameters. 
	 *
	 * @return All of the parameters. 
	 *		   
	**/
	public Iterator getParameters()
	{
		return fParameters.values().iterator();
	}

	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	private void xmlUnmarshall()
	{
		LookupTable lt = fDirectory.getMapTable();

		//---Load type 
		// Melissa listed the type attribute in her design, but gave no indication about
		// a need to map it to a specific class. 
		fTypeString = fSerializer.loadStringAttribute
			(Ircml.A_TYPE, fTypeString, fElement);

		if (fTypeString != null) 
		{
			fParameterSetClass = DescriptorSerializer.getValueClass
				(lt.lookup(Psml.N_PARAMETERSET, fTypeString));
		}

		//---Load Parameters 
		fSerializer.loadChildDescriptorElements(Psml.E_PARAMETER, fParameters, 
			Psml.C_PARAMETER, fElement, this, fDirectory);
	}
}
