//=== File Prolog ============================================================
//
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;


/**
 * An DataFeatureDescriptor is a descriptor that defines attributes
 * similar to the <code>java.beans.FeatureDescriptor</code>.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/13 20:30:12 $ 
 * @author	Troy Ames
**/

public class DataFeatureDescriptor extends AbstractIrcElementDescriptor
{
	private boolean fExpert = false;
	private boolean fHidden = false;
	private boolean fPreferred = true;

	/**
	 * Constructs a new DataFeatureDescriptor having the given parent Descriptor and 
	 * belonging to the given DescriptorDirectory by unmarshalling from the given 
	 * JDOM Element within the indicated namespace.
	 *
	 * @param parent The parent Descriptor of the new DataMapDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataMapDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataMapDescriptor		
	 * @param namespace The namespace to which the new DataMapDescriptor should 
	 * 		belong		
	**/
	
	public DataFeatureDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element, String namespace)
	{
		super(parent, directory, element, namespace);
		
		xmlUnmarshall();
	}
	
	
	/**
	 * Constructs a new DataFeatureDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataMapDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataMapDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataMapDescriptor		
	**/
	
	public DataFeatureDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		this(parent, directory, element, Dataml.N_DATA);
	}
	

	/**
	 * Constructs a new DataFeatureDescriptor having the given name.
	 * 
	 * @param name The name of the new DataMapEntryDescriptor
	**/
	
	public DataFeatureDescriptor(String name)
	{
		super(name);
	}

	/**
	 * The "expert" flag is used to distinguish between those features that are
	 * intended for expert users from those that are intended for normal users.
	 *
	 * @return True if this feature is intended for use by experts only.
	 */
	public boolean isExpert()
	{
		return fExpert;
	}

	/**
	 * The "expert" flag is used to distinguish between features that are
	 * intended for expert users from those that are intended for normal users.
	 *
	 * @param expert True if this feature is intended for use by experts only.
	 */
	public void setExpert(boolean expert)
	{
		fExpert = expert;
	}

	/**
	 * The "hidden" flag is used to identify features that are intended only
	 * for tool use, and which should not be exposed to humans.
	 *
	 * @return True if this feature should be hidden from human users.
	 */
	public boolean isHidden()
	{
		return fHidden;
	}

	/**
	 * The "hidden" flag is used to identify features that are intended only
	 * for tool use, and which should not be exposed to humans.
	 *
	 * @param hidden  True if this feature should be hidden from human users.
	 */
	public void setHidden(boolean hidden)
	{
		fHidden = hidden;
	}

	/**
	 * The "preferred" flag is used to identify features that are particularly
	 * important for presenting to humans.
	 *
	 * @return True if this feature should be preferentially shown to human users.
	 */
	public boolean isPreferred()
	{
		return fPreferred;
	}

	/**
	 * The "preferred" flag is used to identify features that are particularly
	 * important for presenting to humans.
	 *
	 * @param preferred  True if this feature should be preferentially shown
	 *		    	 to human users.
	 */
	public void setPreferred(boolean preferred)
	{
		fPreferred = preferred;
	}

	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		fExpert = fSerializer.loadBooleanAttribute(Dataml.A_EXPERT, false, fElement);
		fHidden = fSerializer.loadBooleanAttribute(Dataml.A_HIDDEN, false, fElement);
		fPreferred = fSerializer.loadBooleanAttribute(Dataml.A_PREFERRED, false, fElement);
	}
	

	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into
	**/
	
	public void xmlMarshall(Element element)
	{
		super.xmlMarshall(element);

		fSerializer.storeAttribute(Dataml.A_EXPERT, fExpert, element);
		fSerializer.storeAttribute(Dataml.A_HIDDEN, fHidden, element);
		fSerializer.storeAttribute(Dataml.A_PREFERRED, fPreferred, element);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataFeatureDescriptor.java,v $
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.1  2005/01/07 20:24:15  tames
//  Initial version.
//
//
