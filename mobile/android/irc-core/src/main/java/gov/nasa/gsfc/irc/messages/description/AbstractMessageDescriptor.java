//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
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
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.messages.description;

import java.util.Collection;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.description.DataMapDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.DescriptorException;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.devices.description.Iml;


/**
 * The class provides access to information describing a message as well
 * as the following items logically contained within a message: <P>
 *
 *  1) fields <BR>
 *
 * The object is built based on information contained in an IML XML file
 * which describes the instrument being interfaced with by IRC.
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2005/05/23 15:29:28 $ 
 * @author John Higinbotham
 * @author Troy Ames
**/

public abstract class AbstractMessageDescriptor extends DataMapDescriptor
{
	private boolean fSynchronous = false;


	/**
	 * Constructs a new AbstractMessageDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new AbstractMessageDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractMessageDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractMessageDescriptor		
	**/
	
	public AbstractMessageDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new AbstractMessageDescriptor having the given name.
	 * 
	 * @param name The name of the new AbstractMessageDescriptor
	**/
	
	public AbstractMessageDescriptor(String name)
	{
		super(name);
	}

	
    /**
     * Construct a new abstract message descriptor manually with the specified 
     * properties.
     *
     * @param  name 	Name of message descriptor.
     * @param  synchronous True if synchronous
    **/
	
    public AbstractMessageDescriptor(String name, boolean synchronous) 
		throws DescriptorException
    {
		this(name);
		
		fSynchronous = synchronous;
    }


	/**
	 * Returns the field of this Message that has the given name.
	 *
	 * @return The field of this Script that has the given name
	**/
	
	public FieldDescriptor getField(String name)
	{
		return ((FieldDescriptor) getEntry(name));
	}

	/**
	 * Returns the (unmodifiable) Collection of fields of this Message.
	 *
	 * @return The (unmodifiable) Collection of fields of this Message
	**/
	
	public Collection getFields()
	{
		return (getEntries());
	}
	

    /**
	 * Get the synchronous property.
	 *
	 * @return Default synchronous property
	**/
	
	public boolean isSynchronous()
	{
		return (fSynchronous);
	}
	

	/**
	 * Set the synchronous property.
	 *
	 * @param value synchronous property
	**/
	
	public void setSynchronous(boolean value)
	{
		fSynchronous = value;
	}
	
	
	/**
	 * Unmarshall a MessageDescriptor from XML.
	 *
	**/
	
	private void xmlUnmarshall()
	{
		//--- Load the synchronous attribute
		
		fSynchronous = fSerializer.loadBooleanAttribute(Iml.A_SYNCHRONOUS, false, 
			fElement);

		fSerializer.loadChildDescriptorElements(Iml.E_FIELD, 
				fDataMapEntryDescriptorsByName, Iml.C_FIELD, fElement, this, 
				fDirectory);			
	}
	

	/**
	 * Marshall this MessageDescriptor to XML. 
	 *
	 * @param element Element to marshall into.
	**/
	
	public void xmlMarshall(Element element)
	{
		super.xmlMarshall(element);

		//---Store the DataEntryDescriptors
		
		fSerializer.storeAttribute(Iml.A_SYNCHRONOUS, fSynchronous, element);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractMessageDescriptor.java,v $
//  Revision 1.9  2005/05/23 15:29:28  tames_cvs
//  Moved field element to this abstract class.
//
//  Revision 1.8  2005/03/14 20:29:34  tames
//  Removed local A_SYNCHRONOUS field.
//
//  Revision 1.7  2004/10/14 23:09:22  chostetter_cvs
//  Message, Adapter descriptor-related changes
//
//  Revision 1.6  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.5  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.4  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.3  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.2  2004/09/07 05:30:16  tames
//  Descriptor cleanup
//
//  Revision 1.1  2004/07/27 21:10:31  tames_cvs
//  Schema changes
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/06/30 17:36:53  tames_cvs
//  Initial Version
//
//  Revision 1.3  2004/06/03 04:42:26  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2004/06/01 16:05:18  tames_cvs
//  Removed old references to obsolete package names and constants.
//
//  Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//  Initial version
//
