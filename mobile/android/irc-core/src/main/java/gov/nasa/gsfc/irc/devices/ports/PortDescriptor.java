//=== File Prolog ============================================================
//
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//  for the Instrument Remote Control (IRC)project.
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

package gov.nasa.gsfc.irc.devices.ports;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.commons.publishing.paths.DefaultPath;
import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.devices.description.DataInterfaceDescriptor;
import gov.nasa.gsfc.irc.devices.description.DeviceDescriptor;
import gov.nasa.gsfc.irc.devices.description.Iml;
import gov.nasa.gsfc.irc.devices.description.MessageInterfaceDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapterDescriptor;


/**
 * The class provides access to information describing a port as well
 * as the following items logicaly contained within the port: <P><BR>
 *
 *  1) parameters <BR>
 *  2) connections <BR>
 *  3) output adapter <BR>
 *  4) input adapter <BR>
 *
 * The object is built based on information contained in an IML XML file
 * which describes the device being interfaced with by IRC.<P>
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * NOTE: The getData(), getResponses() methods utilize
 * a cache approach. If future development enables the descriptor tree
 * to change on the fly, these methods will need to be updated so they
 * recache the data they return.  <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/04/18 14:02:49 $
 * @author John Higinbotham
**/

public class PortDescriptor extends ComponentDescriptor
{
//	private String fEncodingTypeString;  // Encoding type alias
//	private List fMessageInterfaces;  // Cached of message interfaces
//	private List fResponses;  // Cached responses
//	private List fDataElements;  // Cached data elements

	private Map fMessageInterface;  // MessageInterfaceDescriptor collection
	private Map fDataInterface;  // DataInterfaceDescriptor collection

//	private Map fMessagesByAbbrev;
//	private Map fMessagesByName;

//	private String fOutputClassName = null;  // Class of formatter
//	private String fInputClassName = null;  // Class of parser
	private InputAdapterDescriptor fInputDescriptor;
	private OutputAdapterDescriptor fOutputDescriptor;
	
//	private Map fFormats;  // AbstractDataFormatterDescriptor collection
//	private Map fTypeFormat;  // Name / format pairs
	private Map fConnections;  // Name / format pairs
	private Path fPath;

	/**
	 * Constructs a new PortDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new PortDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		PortDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		PortDescriptor		
	**/
	
	public PortDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Iml.N_PORTS);
		
		fConnections = new LinkedHashMap();
		fMessageInterface	= new LinkedHashMap();
		fDataInterface		= new LinkedHashMap();
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new PortDescriptor having the given name.
	 * 
	 * @param name The name of the new PortDescriptor
	**/
	
	public PortDescriptor(String name)
	{
		super(name);
		
		fConnections = new LinkedHashMap();
	}

	/**
	 * Get all the message descriptors for this port. Note that
	 * this call will return both messages and message procedures.
	 * The caller may use instanceof to distinguish between the
	 * two.
	 *
	 * @return All of the port's message descriptors.
	 *
	**/
//	public Iterator getMessages()
//	{
//		ArrayList list = new ArrayList();
//
//		//--- Loop over the format descriptors
//		for (Iterator i = fFormat.values().iterator(); i.hasNext(); )
//		{
//			AbstractDataFormatterDescriptor fd = (AbstractDataFormatterDescriptor) i.next();
//			if (fd.isMessage())
//			{
//				//--- Loop over the formatted interface descriptors
//				for (Iterator j = fd.getFormattedInterfaces(); j.hasNext(); )
//				{
//					FormattedInterfaceDescriptor fid =
//							(FormattedInterfaceDescriptor) j.next();
//					Iterator cidIterator = fid.getInterfaces();
//					while (cidIterator.hasNext())
//					{
//						Descriptor d = (Descriptor)cidIterator.next();
//
//						//--- Only process message interfaces
//						if (d instanceof MessageInterfaceDescriptor)
//						{
//							MessageInterfaceDescriptor cid =
//								(MessageInterfaceDescriptor) d;
//
//							//--- Loop over messages in the interface
//							for (Iterator k =
//									cid.getMessagesAndScripts();
//									k.hasNext();)
//							{
//								list.add(k.next());
//							}
//						}
//					}
//				}
//			}
//		}
//		return list.iterator();
//	}
//
	/**
	 * Get all the message descriptors for this port. Note that
	 * this call will return both messages and message procedures.
	 * The caller may use instanceof to distinguish between the
	 * two.
	 *
	 * @return All of the port's message descriptors.
	 *
	**/
//	public Iterator getMessageInterfaces()
//	{
//		if (fMessageInterfaces == null)
//		{
//			//--- Build and cache message interfaces available on this port
//			ArrayList list = new ArrayList();
//
//			//--- Loop over the format descriptors
//			for (Iterator i = fFormat.values().iterator(); i.hasNext(); )
//			{
//				AbstractDataFormatterDescriptor fd = (AbstractDataFormatterDescriptor) i.next();
//				if (fd.isMessage())
//				{
//					//--- Loop over the formatted interface descriptors
//					for (Iterator j = fd.getFormattedInterfaces(); j.hasNext(); )
//					{
//						FormattedInterfaceDescriptor fid =
//								(FormattedInterfaceDescriptor) j.next();
//						Iterator cidIterator = fid.getInterfaces();
//						while (cidIterator.hasNext())
//						{
//							Descriptor d = (Descriptor)cidIterator.next();
//
//							//--- Only process message interfaces
//							if (d instanceof MessageInterfaceDescriptor)
//							{
//								list.add(d);
//							}
//						}
//					}
//				}
//			}
//			fMessageInterfaces = list;
//		}
//		return fMessageInterfaces.iterator();
//	}
//
//
//
//	/**
//	 * Get message descriptor with specified name on this port.
//	 * Because message names are to be unique within an instrument
//	 * this method returns as soon as it finds a message with
//	 * the specified name on any of the interfaces. Note that this
//	 * call searches through both messages and message procedures.
//	 * The caller can use instanceof to determine if they have
//	 * a message procedure or just a plain message.
//	 *
//	 * @param  name Name of message to look for.
//	 * @return Message descriptor with specified name on this port.
//	 *
//	**/
//	public MessageDescriptor getMessageByName(String name)
//	{
//		fMessagesByName = DescriptorSerializer.toMapByName(getMessages());
//		if(fMessagesByName == null)
//		{
//			return null;
//		}
//		return (MessageDescriptor) fMessagesByName.get(name);
//	}
//
//	/**
//	 * Get message descriptor with specified abbreviation on this port.
//	 * Because message abbreviations are to be unique within an instrument
//	 * this method returns as soon as it finds a message with
//	 * the specified abbreviation on any of the interfaces. Note that this
//	 * call searches through both messages and message procedures.
//	 * The caller can use instanceof to determine if they have
//	 * a message procedure or just a plain message.
//	 *
//	 * @param  abbreviation Abbreviation of message to look for.
//	 * @return Message descriptor with specified abbreviation on this port.
//	 *
//	**/
//	public MessageDescriptor getMessageByAbbreviation(String abbreviation)
//	{
//		fMessagesByAbbrev = DescriptorSerializer.toMapByAbbreviation(getMessages());
//		return (MessageDescriptor) fMessagesByAbbrev.get(abbreviation);
//	}
//
//	/**
//	 * Find the message descriptor with the given name or abbreviation.
//	 * If the key does not exist in the name namespace, the abbreviations
//	 * are searched. If the key does not exist in the abbreviations either
//	 * then null is returned.
//	 *
//	 * @param  key Name/Abbreviation of message.
//	 * @return Message procedure descriptor with the specified key.
//	 *
//	**/
//	public MessageDescriptor findMessage(String key)
//	{
//		MessageDescriptor rval = null;
//		rval = getMessageByName(key);
//		if (rval == null)
//		{
//			rval = getMessageByAbbreviation(key);
//		}
//		return rval;
//	}
//
//	/**
//	 * Get all the response descriptors for this port.
//	 *
//	 * @return All of the port's response descriptors.
//	 *
//	**/
//	public Iterator getResponses()
//	{
//		if (fResponses == null)
//		{
//			//---Build and cache the responses on this port
//			ArrayList list = new ArrayList();
//
//			//--- Loop over the format descriptors
//			for (Iterator i = fFormat.values().iterator(); i.hasNext(); )
//			{
//				AbstractDataFormatterDescriptor fd = (AbstractDataFormatterDescriptor) i.next();
//				//--- Loop over the formatted interface descriptors
//				for (Iterator j = fd.getFormattedInterfaces(); j.hasNext(); )
//				{
//					FormattedInterfaceDescriptor fid = (FormattedInterfaceDescriptor) j.next();
//					Iterator cidIterator = fid.getInterfaces();
//					while (cidIterator.hasNext())
//					{
//						Descriptor d = (Descriptor)cidIterator.next();
//
//						//--- Only process message interfaces
//						if (d instanceof MessageInterfaceDescriptor)
//						{
//							MessageInterfaceDescriptor cid =
//									(MessageInterfaceDescriptor) d;
//
//							//--- Loop over responses in the interface
//							for (Iterator k = cid.getResponses(); k.hasNext();)
//							{
//								list.add(k.next());
//							}
//						}
//					}
//				}
//			}
//			fResponses = list;
//		}
//		return fResponses.iterator();
//	}
//
//// It does not appear that anyone is using this method.
//// We may want to remove it!
//
////	/**
////	  * Get response descriptor with specified name on this port.
////	  * Because response names are to be unique within an instrument
////	  * this method returns as soon as it finds a response with
////	  * the specified name on any of the interfaces.
////	  *
////	  * @param  name Name of response to look for.
////	  * @return Response descriptor with specified name on this port.
////	  *
////	 **/
///*
//	public ResponseDescriptor getResponseByName(String name)
//	{
//		ResponseDescriptor rval = null;
//		for (Iterator i = getResponses(); i.hasNext(); )
//		{
//			ResponseDescriptor rd = (ResponseDescriptor) i.next();
//			if (rd.getName().equals(name))
//			{
//				rval = rd;
//				break;
//			}
//		}
//		return rval;
//	}
//*/
//
//	/**
//	 * Get all the data element descriptors for this port.
//	 *
//	 * @return All of the port's data element descriptors.
//	 *
//	**/
//	public Iterator getDataElements()
//	{
//		if (fDataElements == null)
//		{
//			//---Build and cache the responses on this port
//			ArrayList list = new ArrayList();
//
//			//--- Loop over the format descriptors
//			for (Iterator i = fFormat.values().iterator(); i.hasNext(); )
//			{
//				AbstractDataFormatterDescriptor fd = (AbstractDataFormatterDescriptor) i.next();
//				//--- Loop over the formatted interface descriptors
//				for (Iterator j = fd.getFormattedInterfaces(); j.hasNext(); )
//				{
//					FormattedInterfaceDescriptor fid =
//						(FormattedInterfaceDescriptor) j.next();
//					Iterator didIterator = fid.getInterfaces();
//					while (didIterator.hasNext())
//					{
//						Descriptor d = (Descriptor)didIterator.next();
//
//						//--- Only process data interfaces
//						if (d instanceof DataInterfaceDescriptor)
//						{
//							DataInterfaceDescriptor did =
//										(DataInterfaceDescriptor) d;
//
//							//--- Loop over data elements in the interface
//							for (Iterator k = did.getDataElements();
//										k.hasNext();)
//							{
//								list.add(k.next());
//							}
//						}
//					}
//				}
//			}
//			fDataElements = list;
//		}
//		return fDataElements.iterator();
//	}
//
//// It does not appear that anyone is using this method.
//// We may want to remove it!
//
////	/**
////	  * Get data element descriptor with specified name on this port.
////	  * Because data element names are to be unique within an instrument
////	  * this method returns as soon as it finds a data element with
////	  * the specified name on any of the interfaces.
////	  *
////	  * @param  name Name of data element to look for.
////	  * @return Data element descriptor with specified name on this port.
////	  *
////	 **/
///*
//	public DataElementDescriptor getDataElementByName(String name)
//	{
//		DataElementDescriptor rval = null;
//		for (Iterator i = getDataElements(); i.hasNext(); )
//		{
//			DataElementDescriptor ded = (DataElementDescriptor) i.next();
//			if (ded.getName().equals(name))
//			{
//				rval = ded;
//				break;
//			}
//		}
//		return rval;
//	}
//*/
//
	/**
	 * Get the path of this instrument. 
	 * 
	 * @return the full path
	 */
	public Path getPath()
	{
        if (fPath == null)
        {
            Path parentPath = null;
            
            if (fParent != null && fParent instanceof DeviceDescriptor)
            {
                parentPath = ((DeviceDescriptor) fParent).getPath();
            }
    
            // Add this device to parent path
            fPath = new DefaultPath(parentPath, getName());
        }
        
        return fPath;
	}
	
	/**
	 * Get the instrument's message interface descriptors with the specified name. 
	 *
	 * @param  name Name of message interface. 
	 * @return Message interface descriptor with the specified name.
	**/
	public MessageInterfaceDescriptor getMessageInterfaceByName(String name)
	{
		return (MessageInterfaceDescriptor)fMessageInterface.get(name);
	}

	/**
	 * Get the port's collection of message interface descriptors.
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

	/**
	 * Set the instrument's collection of message interface descriptors.
	 *
	 * @param descriptors All of the instrument's message interface descriptors. 
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setMessageInterfaces(Map descriptors)
	{
		fMessageInterface = descriptors;
	}

	/**
	 * Get the instrument's collection of data interface descriptors.
	 *
	 * @return All of the instrument's data interface descriptors. 
	**/
	public Iterator getDataInterfaces()
	{
		return fDataInterface.values().iterator();
	}

	/**
	 * Get the instrument's data interface descriptor with the specified name. 
	 *
	 * @param  name Name of data interface. 
	 * @return Data interface descriptor with the specified name.
	**/
	public DataInterfaceDescriptor getDataInterfaceByName(String name)
	{
		return (DataInterfaceDescriptor)fDataInterface.get(name);
	}

	/**
	 * Returns the (unmodifiable) Collection of Connections of the Port described by 
	 * this PortDescriptor.
	 *
	 * @return The (unmodifiable) Collection of Connections of the Port described by 
	 * 		this PortDescriptor
	**/
	
	public Collection getConnections()
	{
		return (Collections.unmodifiableCollection(fConnections.values()));
	}

	/**
	 * Get the port's OutputAdapter descriptor.
	 *
	 * @return the port's OutputAdapter descriptor.
	 *
	**/
	public OutputAdapterDescriptor getOutputAdapter()
	{
		return fOutputDescriptor;
	}

	/**
	 * Get the port's InputAdapter descriptor.
	 *
	 * @return the port's InputAdapter descriptor.
	 *
	**/
	public InputAdapterDescriptor getInputAdapter()
	{
		return fInputDescriptor;
	}

	/**
	 * Unmarshall descriptor from XML.
	 *
	**/
	private void xmlUnmarshall()
	{
		//--- Load the Message Interface
		fSerializer.loadChildDescriptorElements(
			Iml.E_MESSAGE_INTERFACE,
			fMessageInterface,
			Iml.C_MESSAGE_INTERFACE,
			fElement,
			this,
			fDirectory);

		//--- Load the Data Interface
		fSerializer.loadChildDescriptorElements(
			Iml.E_DATA_INTERFACE,
			fDataInterface,
			Iml.C_DATA_INTERFACE,
			fElement,
			this,
			fDirectory);

		fSerializer.loadChildDescriptorElements
			(Iml.E_CONNECTION, fConnections, Iml.C_CONNECTION, fElement, this, 
				fDirectory);
		
		fOutputDescriptor = (OutputAdapterDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Iml.E_OUTPUT_ADAPTER, Iml.C_OUTPUT_ADAPTER, fElement, this, 
					fDirectory);
		
		fInputDescriptor = (InputAdapterDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Iml.E_INPUT_ADAPTER, Iml.C_INPUT_ADAPTER, fElement, this, 
					fDirectory);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: PortDescriptor.java,v $
//  Revision 1.12  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.11  2006/04/18 04:08:02  tames
//  Changed to reflect refactored Input and Output messages.
//
//  Revision 1.10  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/09/27 14:44:09  pjain_cvs
//  Changed the initialization of the path to be delayed until getPath is called the first time.
//
//  Revision 1.8  2005/09/27 14:13:16  tames_cvs
//  Modified init not to recursively use the fPath field.
//
//  Revision 1.7  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.6  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.5  2005/02/04 21:43:54  tames_cvs
//  Import changes to reflect the relocation of the Path interface.
//
//  Revision 1.4  2005/02/03 07:03:10  tames
//  Updated to reflect changes in DeviceDescriptor.
//
//  Revision 1.3  2005/02/02 22:55:54  tames_cvs
//  Added method to get the number of message interfaces contained in
//  Port.
//
//  Revision 1.2  2005/02/01 18:23:22  tames
//  Changes to support relocation of message description to ports.
//
//  Revision 1.1  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.14  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.13  2004/10/06 15:02:36  tames_cvs
//  Updated element name for Input and OutputAdapters to reflect
//  change in the IML scheme.
//
//  Revision 1.12  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.11  2004/09/27 21:47:34  tames
//  Reflects a refactoring of port descriptors.
//
//  Revision 1.10  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.9  2004/09/07 05:30:16  tames
//  Descriptor cleanup
//
//  Revision 1.8  2004/08/06 14:34:21  tames_cvs
//  Moved format descriptors
//
//  Revision 1.7  2004/08/03 20:34:05  tames_cvs
//  Many configuration and descriptor changes
//
//  Revision 1.6  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
//  Revision 1.5  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.4  2004/06/30 20:44:50  tames_cvs
//  Changed references of commands to messages
//
//  Revision 1.3  2004/06/04 14:35:13  tames_cvs
//  Returns class names instead of Class objects
//
//  Revision 1.2  2004/05/12 21:58:23  chostetter_cvs
//  Revisions for Descriptor changes (primarily).
//
//  Revision 1.1  2004/04/30 20:31:16  tames_cvs
//  Relocated
//
