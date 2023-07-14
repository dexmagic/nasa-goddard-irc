//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, Code 588 for 
//  the Instrument Remote Control (IRC) project.
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


/**
 * An DataMapEntryDescriptor is a named ValueDescriptor with certain entry attribtues 
 * (such as "required").
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/01/07 20:27:00 $ 
 * @author Carl F. Hostetter   
**/

public class DataMapEntryDescriptor extends ValueDescriptor
{
	private boolean fIsRequired = false;  // True if entry required


	/**
	 * Constructs a new DataMapEntryDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataMapEntryDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataMapEntryDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataMapEntryDescriptor		
	**/
	
	public DataMapEntryDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new DataMapEntryDescriptor having the given name.
	 * 
	 * @param name The name of the new DataMapEntryDescriptor
	**/
	
	public DataMapEntryDescriptor(String name)
	{
		super(name);
	}

	
	/**
	 * Returns true if the DataEntry described by this DataMapEntryDescriptor is 
	 * required, false otherwise.  
	 *
	 * @return True if the DataEntry described by this DataMapEntryDescriptor is 
	 * 		required, false otherwise		
	**/
	
	public boolean isRequired()
	{
		return (fIsRequired);
	}
	

	/**
	 * Sets the required status of the DataEntry described by this 
	 * DataMapEntryDescriptor according to the given flag.
	 *
	 * @param flag True if required, false otherwise. 
	**/
	
	public void setRequired(boolean flag)
	{
		fIsRequired = flag;
	}

	
	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		fIsRequired = fSerializer.loadBooleanAttribute(Dataml.A_REQUIRED, false, 
			fElement);
	}
	

	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into
	**/
	
	public void xmlMarshall(Element element)
	{
		super.xmlMarshall(element);

		fSerializer.storeAttribute(Dataml.A_REQUIRED, fIsRequired, element);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataMapEntryDescriptor.java,v $
//  Revision 1.3  2005/01/07 20:27:00  tames
//  Changed setIsRequired method to setRequired to comply with bean
//  naming standards.
//
//  Revision 1.2  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
