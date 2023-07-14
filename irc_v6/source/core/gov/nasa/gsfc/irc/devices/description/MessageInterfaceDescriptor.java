//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the bottom of the file.
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

package gov.nasa.gsfc.irc.devices.description;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.commons.types.maps.MultivalueMap;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.DescriptorSerializer;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.Ircml;
import gov.nasa.gsfc.irc.devices.ports.PortDescriptor;
import gov.nasa.gsfc.irc.messages.description.AbstractMessageDescriptor;
import gov.nasa.gsfc.irc.messages.description.ResponseDescriptor;

/**
 * The class provides access to information describing an instrument's 
 * message interface as well as the following items logicaly contained 
 * within the message interface: <P>
 *  <BR>
 *  1) messages <BR>
 *  2) responses <BR>
 *
 * <P>
 * It is important to note that a message interface is simply a means 
 * to group a set of similar messages and will be referred to elsewhere for applying
 * formatting rules to those messages. Regardless of the number of message interfaces,
 * all messages on the instrument must have unique names to allow the IRC scripting
 * system to easily access them with out having to dig through instruments to find
 * them.<P>
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
 * @version			 $Date: 2006/04/18 14:02:49 $ 
 * @author			  John Higinbotham 
 * @author			  Troy Ames
**/
public class MessageInterfaceDescriptor extends AbstractIrcElementDescriptor
{
	// These temporary maps contain the string references loaded from the 
	// XML. The strings need to be resolved to object references using the
	// descriptor directory support.
	private MultivalueMap fTmpResponsesByMessage; // Maps responses by message 
	private MultivalueMap fTmpMessagesByResponse; // Maps messages by response 

	// These maps contain resolved object reference. 
	private MultivalueMap fResponsesByMessage; // Maps responses by message 
	private MultivalueMap fMessagesByResponse; // Maps message by response 

	private Map fMessage; // MessageDescriptor collection
	private Map fResponse; // ResponseDescriptor collection
	// Elements of fMessage mapped by local name
	private Map fMessagesByDisplayName; 
	private Map fMessageInterface;  // MessageInterfaceDescriptor collection
	private Path fPath;

	private ArrayList fOrderedMessages; // Messages ordered as in xml

	private final int MODE_MSG_BY_RES = 1;  // Resolve messages by response
	private final int MODE_RES_BY_MSG = 2;  // Resolve responses by message 


	/**
	 * Constructs a new MessageInterfaceDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new MessageInterfaceDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		MessageInterfaceDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		MessageInterfaceDescriptor		
	**/
	
	public MessageInterfaceDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Iml.N_DEVICES);
		
		init();
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new MessageInterfaceDescriptor having the given name.
	 * 
	 * @param name The name of the new MessageInterfaceDescriptor
	**/
	
	public MessageInterfaceDescriptor(String name)
	{
		this(name, null);
		
		init();
	}
	

	/**
	 * Constructs a new MessageInterfaceDescriptor having the given name and name 
	 * qualifier.
	 * 
	 * @param name The name of the new MessageInterfaceDescriptor
	 * @param nameQualifier The name qualifier of the new MessageInterfaceDescriptor
	**/
	
	public MessageInterfaceDescriptor(String name, String nameQualifier)
	{
		super(name, nameQualifier);
		
		init();
	}

	
	/**
	 * Initialize class. 
	**/
	private void init()
	{
		fMessage			  	= new LinkedHashMap();
		fResponse			  	= new LinkedHashMap();
		fTmpResponsesByMessage 	= new MultivalueMap();
		fTmpMessagesByResponse 	= new MultivalueMap();
		fMessageInterface		= new LinkedHashMap();
	}

	/**
	 * This method resolves the reference strings in the maps that 
	 * associate messages and responses. Both the keys and values
	 * in the map will be resolved using the descriptor directory.
	 *
	 * @param  refMap Unresolved MultivalueMap of messages and response.
	 * @param  resMap Resolved MultivalueMap of messages and response.
	**/
	private void resolveReferences(
		MultivalueMap refMap,
		MultivalueMap resMap,
		int mode)
	{
		Iterator i		 = refMap.keys();
		String key		 = null;
		Object resolvedKey = null;

		String fromBase	= null;
		String toBase	  = null;
		String fromKey	 = null;
		String toKey	   = null;

		String refName = getFullyQualifiedName();

		switch (mode)
		{
			case MODE_MSG_BY_RES:
				fromBase = refName + Path.DOT + Iml.E_RESPONSE + Path.DOT; 
				toBase   = refName + Path.DOT + Iml.E_MESSAGE  + Path.DOT; 
				break;

			case MODE_RES_BY_MSG:
				fromBase = refName + Path.DOT + Iml.E_MESSAGE  + Path.DOT; 
				toBase   = refName + Path.DOT + Iml.E_RESPONSE + Path.DOT; 
				break;

			default:
				return;
		}

		while (i.hasNext())
		{
			//--- Extract the key and resolve it (iterate over the froms)
			key		 = (String) i.next();
			fromKey	 = fromBase + key;
			resolvedKey = fDirectory.find(fromKey);
			Iterator j  = refMap.get(key);

			//--- Iterate over the tos 
			while (j.hasNext())
			{
				//---Resolve each value stored under the key in the old
				//   map and store it in the new map under the resolved key.
				toKey = toBase + (String) j.next();
				resMap.put(resolvedKey, fDirectory.find(toKey));
			}
		}
	}

	/**
	 * Get the message interface's message with the specified name. 
	 *
	 * @param  name Name of message.
	 * @return MessageDescriptor with the specified name.
	**/
	public AbstractMessageDescriptor getMessageByName(String name)
	{
		return (AbstractMessageDescriptor)fMessage.get(name);
	}

	/**
	 * Get the Message interface's Message descriptor with the specified 
	 * local name.
	 *
	 * @param  name local name of Message.
	 * @return MessageDescriptor with the specified name.
	**/
	public AbstractMessageDescriptor getMessageByDisplayName(String name)
	{
		return (AbstractMessageDescriptor) fMessagesByDisplayName.get(name);
	}

	/**
	 * Find the Message descriptor with the given name. 
	 * If the key does not exist in the name namespace, the local name 
	 * are searched. If the key does not exist then null is returned.
	 *
	 * @param  key Name of Message. 
	 * @return MessageDescriptor with the specified key.
	**/
	public AbstractMessageDescriptor findMessage(String key)
	{
		AbstractMessageDescriptor rval = null;
		rval = getMessageByName(key);
		if (rval == null)
		{
			rval = getMessageByDisplayName(key);
		}
		return rval; 
	}

	/**
	 * Get the path for the messages defined in this interface. 
	 * This returns the path for the parent instrument.
	 * 
	 * @return the path for the contained messages
	 */
	public Path getPath()
	{
        if (fPath == null && fParent != null)
        {
            if (fParent instanceof DeviceDescriptor)
            {
                fPath = ((DeviceDescriptor) fParent).getPath();
            }
            else if (fParent instanceof PortDescriptor)
            {
                fPath = ((PortDescriptor) fParent).getPath();
            }
            else if (fParent instanceof MessageInterfaceDescriptor)
            {
                fPath = ((MessageInterfaceDescriptor) fParent).getPath();
            }
        }
        
		return fPath;
	}
	
	/**
	 * Get the Message interface's collection of MessageDescriptors.
	 *
	 * @return All of the Message interface's MessageDescriptors.
	**/
	public Iterator getMessages()
	{
		return fMessage.values().iterator();
	}

	/**
	 * Get the Message interface's response descriptor with the specified name. 
	 *
	 * @param  name Name of response.
	 * @return Response descriptor with the specified name.
	**/
	public ResponseDescriptor getResponseByName(String name)
	{
		return (ResponseDescriptor)fResponse.get(name);
	}

	/**
	 * Get the Message interface's collection of response descriptors.
	 *
	 * @return All of the Message interface's response descriptors.
	**/
	public Iterator getResponses()
	{
		return fResponse.values().iterator();
	}

	/**
	 * Get collection of Message descriptors by response descriptor.
	 *
	 * NOTE: This method uses a cached collection of items rather than recreating 
	 * the collection every time. This change arose from the code review. Should 
	 * Should the design change in the future to allow the descriptor tree to
	 * change on the fly, this cache call will need to be updated to recache the
	 * data items.
	 *
	 * @param response Response descriptor.  
	 * @return Collection of associated Message descriptors. 
	**/
	public Iterator getMessagesByResponse(ResponseDescriptor response)
	{
		//---Resolve the references if not already done so.
		if (fMessagesByResponse == null)
		{
			fMessagesByResponse = new MultivalueMap();
			resolveReferences(
				fTmpMessagesByResponse,
				fMessagesByResponse,
				MODE_MSG_BY_RES);
		}
		return fMessagesByResponse.get(response);
	}

	/**
	 * Get collection of response descriptors by Message descriptor.
	 *
	 * NOTE: This method uses a cached collection of items rather than recreating 
	 * the collection every time. This change arose from the code review. Should 
	 * Should the design change in the future to allow the descriptor tree to
	 * change on the fly, this cache call will need to be updated to recache the
	 * data items.
	 *
	 * @param message Message descriptor.  
	 * @return Collection of associated response descriptors. 
	**/
	public Iterator getResponsesByMessage(AbstractMessageDescriptor message)
	{
		//---Resolve the references if not already done so.
		if (fResponsesByMessage == null)
		{
			fResponsesByMessage = new MultivalueMap();
			resolveReferences(
				fTmpResponsesByMessage, fResponsesByMessage, MODE_RES_BY_MSG);
		}
		return fResponsesByMessage.get(message);
	}

	/**
	 * Get the message interface descriptor with the specified name. 
	 *
	 * @param  name Name of message interface. 
	 * @return Message interface descriptor with the specified name.
	**/
	public MessageInterfaceDescriptor getMessageInterface(String name)
	{
		return (MessageInterfaceDescriptor)fMessageInterface.get(name);
	}

	/**
	 * Get the collection of message interface descriptors.
	 *
	 * @return All of the port's message interface descriptors. 
	**/
	public Iterator getMessageInterfaces()
	{
		return fMessageInterface.values().iterator();
	}

	/**
	 * Get the number of message interface descriptors.
	 *
	 * @return the number of message interface descriptors. 
	**/
	public int getMessageInterfaceSize()
	{
		return fMessageInterface.size();
	}

	private void getOrderedMessages()
	{
		ArrayList list = new ArrayList();
		Element element;
		Iterator i = fElement.getChildren().iterator();
		Object o;
		while (i.hasNext())
		{
			element = (Element) i.next();
			o = null;
			o = getMessageByName(
				element.getAttribute(Ircml.A_NAME).getValue());
			if (o != null)
			{
				list.add(o);
			}
		}

		fOrderedMessages = list;
	}

	/**
	  * Unmarshall descriptor from XML. 
	 **/
	private void xmlUnmarshall()
	{
		//---Load Messages
		fSerializer.loadChildDescriptorElements(
			Iml.E_MESSAGE,
			fMessage,
			Iml.C_MESSAGE,
			fElement,
			this,
			fDirectory);

		//---Load responses
		fSerializer.loadChildDescriptorElements(
			Iml.E_RESPONSE,
			fResponse,
			Iml.C_RESPONSE,
			fElement,
			this,
			fDirectory);

		//--- Build the map of responses by messages
		fSerializer.loadMultivalueMapElement(
			Iml.E_MESSAGE_RESPONSE_MAP,
			Iml.A_MESSAGE_REFERENCE,
			Iml.A_RESPONSE_REFERENCE,
			fTmpResponsesByMessage,
			fElement);
		
		//--- Build the map of messages by responses 
		fSerializer.loadMultivalueMapElement(
			Iml.E_MESSAGE_RESPONSE_MAP,
			Iml.A_RESPONSE_REFERENCE,
			Iml.A_MESSAGE_REFERENCE,
			fTmpMessagesByResponse,
			fElement);

		//--- Load the Message Interface
		fSerializer.loadChildDescriptorElements(
			Iml.E_MESSAGE_INTERFACE,
			fMessageInterface,
			Iml.C_MESSAGE_INTERFACE,
			fElement,
			this,
			fDirectory);

		//The actual resolution of references is put off until first use. If we determine the
		//the command response mapping is to only be for the given commands and responses in
		//this interface we can do the resolution here.

		//--- Finally support lookups based on local name
		fMessagesByDisplayName = DescriptorSerializer.toMapByDisplayName(getMessages());

		//--- Build an ordered list of cmds/procs to preserve the xml ordering
		getOrderedMessages();
	}

	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into.
	**/
	public void xmlMarshall(Element element)
	{
		//---Note: This marshall method uses the existing tree and swaps 
		// out the branches for the scripts. When/If the IML tree 
		// is changed to have more items settable above this point, 
		// this method may have to change. 

		//---Prune old command procedures
		List removeElements = new ArrayList();
		List l = fElement.getChildren();
		Iterator i = l.iterator();
		Element e;
		while (i.hasNext())
		{
			e = (Element) i.next(); 
			if (e.getName().equals(Iml.E_SCRIPT) 
					|| e.getName().equals(Iml.E_MESSAGE))
			{
				removeElements.add(e);
			}
		}
		
		i = removeElements.iterator();
		while (i.hasNext())
		{
			((Element) i.next()).detach();
		}

		//---Attach new messages
		i = fOrderedMessages.iterator();
		Descriptor des;
		while (i.hasNext())
		{
			des = (Descriptor) i.next();
			if (des instanceof AbstractMessageDescriptor)
			{
				fSerializer.storeDescriptorElement(
					Iml.E_MESSAGE,
					des,
					Iml.C_MESSAGE,
					fElement,
					fDirectory);
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: MessageInterfaceDescriptor.java,v $
//  Revision 1.14  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.13  2006/04/18 04:05:06  tames
//  Import changes to reflect relocation of Path related classes.
//
//  Revision 1.12  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.11  2005/09/27 14:44:09  pjain_cvs
//  Changed the initialization of the path to be delayed until getPath is called the first time.
//
//  Revision 1.10  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.9  2005/07/14 20:09:05  tames_cvs
//  Fixed path creation to handle MessageInterfaces.
//
//  Revision 1.8  2005/07/12 14:57:55  tames_cvs
//  Added support for nested MessageInterfaces in an XML description.
//
//  Revision 1.7  2005/02/04 21:42:23  tames_cvs
//  Import changes to reflect the relocation of the Path interface.
//
//  Revision 1.6  2005/02/01 18:13:07  tames
//  Changes to support relocation to ports instead of device description.
//
//  Revision 1.5  2005/01/21 20:16:51  tames
//  Reflects a refactoring of Message paths to support a new Path interface
//  and implementation.
//
//  Revision 1.4  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.3  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.2  2004/09/28 19:26:32  tames_cvs
//  Reflects changing the name of Instrument related classes and methods
//  to Device since a device can include sensors, software, simulators etc.
//  Instrument maybe used in the future for a specific device type.
//
//  Revision 1.1  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.11  2004/09/21 15:09:19  tames
//  Added functionality to support a destination path for messages.
//
//  Revision 1.10  2004/09/20 21:07:13  tames
//  Removed references to abbreviations.
//
//  Revision 1.9  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.8  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.7  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.6  2004/09/04 13:29:00  tames
//  *** empty log message ***
//
