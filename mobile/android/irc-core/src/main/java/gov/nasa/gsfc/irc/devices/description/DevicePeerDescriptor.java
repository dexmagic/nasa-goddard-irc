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
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.devices.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;

/**
 * The class provides access to information describing an instrument peer. 
 * Instrument peers are realated instrument that are distrubted across a
 * network rather than being located locally. IRC currently uses the
 * information contained in this class along with JXTA to discover that
 * remote instrument and determine how to communicate with it.<P><BR>
 *
 * The object is built based on information contained in an IML XML file
 * which describes the insturment being interfaced with by IRC.<P>
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			 $Date: 2005/09/13 20:30:12 $
 * @author			  John Higinbotham
**/
public class DevicePeerDescriptor extends AbstractIrcElementDescriptor
{
	private String fPeer;  // Peer  
	private String fGroup;  // Group 
	private String fDescription;  // Description 
	private int fTimeout		= -1;	// Timeout 
	private int fResponseLimit  = 1;	 // Repsonse Limit 


	/**
	 * Constructs a new DevicePeerDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DevicePeerDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DevicePeerDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DevicePeerDescriptor		
	**/
	
	public DevicePeerDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Iml.N_DEVICES);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new DevicePeerDescriptor having the given name.
	 * 
	 * @param name The name of the new DevicePeerDescriptor
	**/
	
	public DevicePeerDescriptor(String name)
	{
		super(name);
	}

	
	/**
	 * Get the peer.
	 *
	 * @return The peer. 
	 *			
	**/
	public String getPeer()
	{
		return fPeer;
	}

	/**
	 * Set the peer. 
	 *
	 * @param peer  
	**/
	public void setPeer(String peer)
	{
		fPeer = peer;
	}

	/**
	 * Get the group.
	 *
	 * @return The group. 
	 *			
	**/
	public String getGroup()
	{
		return fGroup;
	}

	/**
	 * Set the group. 
	 *
	 * @param group 
	**/
	public void setGroup(String group)
	{
		fGroup = group;
	}

	/**
	 * Get the description.
	 *
	 * @return The description. 
	 *			
	**/
	public String getDescription()
	{
		return fDescription;
	}

	/**
	 * Set the description. 
	 *
	 * @param description 
	**/
	public void setDescription(String description)
	{
		fDescription = description;
	}

	/**
	 * Get the timeout.
	 *
	 * @return The timeout. 
	 *			
	**/
	public int getTimeout()
	{
		return fTimeout;
	}

	/**
	 * Set the timeout. 
	 *
	 * @param timeout 
	**/
	public void setTimeout(int timeout)
	{
		fTimeout = timeout;
	}

	/**
	 * Get the response limit.
	 *
	 * @return The response limit. 
	 *			
	**/
	public int getResponseLimit()
	{
		return fResponseLimit;
	}

	/**
	 * Set the response limit. 
	 *
	 * @param responseLimit 
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setResponseLimit(int responseLimit)
	{
		fResponseLimit = responseLimit;
	}

	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	private void xmlUnmarshall()
	{
		fPeer		  = fSerializer.loadStringAttribute(Iml.A_PEER, null, fElement);
		fGroup		 = fSerializer.loadStringAttribute(Iml.A_PEER_GROUP, null, fElement);
		fDescription   = fSerializer.loadStringAttribute(Iml.A_PEER_DESCRIPTION, null, fElement);
		fTimeout	   = fSerializer.loadIntAttribute(Iml.A_TIMEOUT, fTimeout, fElement);
		fResponseLimit = fSerializer.loadIntAttribute(Iml.A_PEER_RESPONSE_LIMIT, fResponseLimit, fElement);
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

		//---Store instrument peer 
		fSerializer.storeAttribute(Iml.A_PEER, fPeer, element);
		fSerializer.storeAttribute(Iml.A_PEER_GROUP, fGroup, element);
		fSerializer.storeAttribute(Iml.A_PEER_DESCRIPTION, fDescription, element);
		fSerializer.storeAttribute(Iml.A_TIMEOUT, fTimeout, element);
		fSerializer.storeAttribute(Iml.A_PEER_RESPONSE_LIMIT, fResponseLimit, element);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DevicePeerDescriptor.java,v $
//  Revision 1.4  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.3  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.1  2004/09/28 19:26:32  tames_cvs
//  Reflects changing the name of Instrument related classes and methods
//  to Device since a device can include sensors, software, simulators etc.
//  Instrument maybe used in the future for a specific device type.
//
//  Revision 1.1  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//
//   2	IRC	   1.1		 1/16/2002 11:48:56 AMJohn Higinbotham Update for
//		Troy's jxta support.
//   1	IRC	   1.0		 1/9/2002 1:55:27 PM  John Higinbotham 
//  
