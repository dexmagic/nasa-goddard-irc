//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, 
//  Code 588 for the Instrument Remote Control (IRC)project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//     any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//     explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.messages.description;

import java.util.logging.Logger;

import org.jdom.Element;

import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.DescriptorException;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.devices.description.Iml;
import gov.nasa.gsfc.irc.devices.description.MessageInterfaceDescriptor;

/**
 * The class provides access to information describing a message. <P>
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
 * @version             $Date: 2006/04/18 14:02:49 $
 * @author              John Higinbotham
**/
public class MessageDescriptor extends AbstractMessageDescriptor
{
    /**
     * Logger for this class
     */
    private static final Logger sLogger = Logger
            .getLogger(MessageDescriptor.class.getName());

    private int fTimeout = 0;     // Timeout (units???)
    private Path fPath;


	/**
	 * Constructs a new MessageDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new MessageDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		MessageDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		MessageDescriptor		
	**/
	
	public MessageDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	
	/**
	 * Constructs a new MessageDescriptor having the given name.
	 * 
	 * @param name The name of the new MessageDescriptor
	**/
	
	public MessageDescriptor(String name)
	{
		super(name);
	}
	
    /**
     * Construct a new message descriptor manually with the specified properties.
     *
     * @param  name 	Name of message descriptor.
     * @param  timeout	message timeout 
     * @param  synchronous	True if synchronous
    **/
    public MessageDescriptor(String name, int timeout, boolean synchronous) 
		throws DescriptorException
    {
		super(name, synchronous);
		
		fTimeout = timeout;
    }

    /**
     * Get the parent message interface descriptor for this message.
     *
     * @return Manager message interface descriptor.
    **/
    public MessageInterfaceDescriptor getMessageInterface()
    {
        MessageInterfaceDescriptor rval = null;
        if (fParent instanceof MessageInterfaceDescriptor)
        {
            rval = (MessageInterfaceDescriptor) fParent;
        }
        return rval;
    }

	/**
	 * Get the path of this message.
	 * 
	 * @return a the full path
	 */
	public Path getPath()
	{
	    if (fPath == null)
        {
            if (fParent != null && fParent instanceof MessageInterfaceDescriptor)
            {
                fPath = ((MessageInterfaceDescriptor) fParent).getPath();
            }            
        }
        
        return fPath;
	}
	
	/**
     * Get timeout property.
     *
     * @return Message timeout property.
     *
    **/
    public int getTimeout()
    {
        return fTimeout;
    }

    /**
     * Set timeout property.
     *
     * @param value Message timeout property.
     * @throws IllegalStateException if descriptor is finalized.
    **/
    public void setTimeout(int value)
    {
        fTimeout = value;
        
    }


  
    /**
     * Unmarshall descriptor from XML.
     *
    **/
    private void xmlUnmarshall()
    {
		//--- Unmarshall the FieldDescriptors
		
        fTimeout = fSerializer.loadIntAttribute(Iml.A_TIMEOUT, fTimeout, fElement);
    }

    /**
     * Marshall descriptor to XML. 
     *
     * @param element Element to marshall into.
     *
    **/
    public void xmlMarshall(Element element)
    {
		super.xmlMarshall(element);
		fSerializer.storeAttribute(Iml.A_TIMEOUT, fTimeout, element);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MessageDescriptor.java,v $
//  Revision 1.20  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.19  2006/04/18 04:16:41  tames
//  Changed to reflect relocated Path related classes.
//
//  Revision 1.18  2006/03/29 21:31:12  chostetter_cvs
//  First stage of IRC schema cleanup
//
//  Revision 1.17  2005/09/27 14:44:09  pjain_cvs
//  Changed the initialization of the path to be delayed until getPath is called the first time.
//
//  Revision 1.16  2005/06/08 22:03:19  chostetter_cvs
//  Organized imports
//
//  Revision 1.15  2005/05/23 15:30:23  tames_cvs
//  Moved field element to super class.
//
//  Revision 1.14  2005/02/04 21:47:44  tames_cvs
//  Import changes to reflect the relocation of the Path interface.
//
//  Revision 1.13  2005/01/21 20:16:51  tames
//  Reflects a refactoring of Message paths to support a new Path interface
//  and implementation.
//
//  Revision 1.12  2005/01/11 21:35:47  chostetter_cvs
//  Initial version
//
//  Revision 1.11  2004/11/19 21:42:20  smaher_cvs
//  Added getFieldByAbbreviation
//
//  Revision 1.10  2004/10/14 23:09:22  chostetter_cvs
//  Message, Adapter descriptor-related changes
//
//  Revision 1.9  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.8  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.7  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.6  2004/09/21 15:10:28  tames
//  Added functionality to support a destination path for messages.
//
//  Revision 1.5  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.4  2004/08/06 14:32:24  tames_cvs
//  *** empty log message ***
//
//  Revision 1.3  2004/07/27 21:10:31  tames_cvs
//  Schema changes
//
