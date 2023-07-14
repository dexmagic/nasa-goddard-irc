//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//  for the Instrument Remote Control (IRC) project.
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;

/**
 * The class provides access to information describing a set of instruments. 
 * 
 * An insturment may be a piece of hardware or software that an IRC device 
 * communicates with.
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
 * @version		$Date: 2006/03/17 22:02:07 $
 * @author		Troy Ames
**/
public class DeviceSetDescriptor extends AbstractIrcElementDescriptor
	implements RootDescriptor
{
	private Map fInstrument;  // DeviceDescriptor collection
	private Map fInstrumentPeer;  // DevicePeerDescriptor collection


	/**
	 * Constructs a new DeviceSetDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DeviceSetDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DeviceSetDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DeviceSetDescriptor		
	**/
	
	public DeviceSetDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Iml.N_DEVICES);
		
		init();
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new DeviceSetDescriptor having the given name.
	 * 
	 * @param name The name of the new DeviceSetDescriptor
	**/
	
	public DeviceSetDescriptor(String name)
	{
		super(name);
		
		init();
	}

	
	/**
	 * Initialize class. 
	**/
	private void init()
	{
		fInstrument	   = new LinkedHashMap();
		fInstrumentPeer   = new LinkedHashMap();
	}

	/**
	 * Get the instrument's peer instrument descriptors.
	 *
	 * @return Iterator of DevicePeerDescriptor objects 
	**/
	public Iterator getInstrumentPeers()
	{
		return fInstrumentPeer.values().iterator();
	}

	/**
	 * Get the set's instrument descriptor with the specified name. 
	 *
	 * @param  name Name of an instrument.
	 * @return Instrument descriptor with the specified name.
	**/
	public DeviceDescriptor getInstrumentsByName(String name)
	{
		return (DeviceDescriptor)fInstrument.get(name);
	}

	/**
	 * Get the sets's collection of instrument descriptors.
	 *
	 * @return All of the instrument's sub instrument descriptors.
	**/
	public Iterator getInstruments()
	{
		return fInstrument.values().iterator();
	}

	/**
	 * Set the collection of instrument descriptors.
	 *
	 * @param instruments All of the instrument descriptors.
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setInstruments(Map instruments)
	{
		fInstrument = instruments;
	}


	/**
	 * Unmarshall descriptor from XML. 
	**/
	private void xmlUnmarshall()
	{
		//--- Load the Sub Instruments
		fSerializer.loadChildDescriptorElements(
			Iml.E_DEVICE,
			fInstrument,
			Iml.C_DEVICE,
			fElement,
			null,
			fDirectory);

		//--- Load Peer Instruments
		fSerializer.loadChildDescriptorElements(
			Iml.E_DEVICE_PEER,
			fInstrumentPeer,
			Iml.C_DEVICE_PEER,
			fElement,
			null,
			fDirectory);
	}

	/**
	 * Marshall descriptor to XML.  
	 *
	 * @param element Element to marshall into.
	**/
	public void xmlMarshall(Element element)
	{
		//super.xmlMarshall(fElement);

		fSerializer.storeDescriptorElement(
			Iml.E_DEVICE,
			fInstrument,
			Iml.C_DEVICE,
			null,
			fDirectory);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DeviceSetDescriptor.java,v $
//  Revision 1.5  2006/03/17 22:02:07  tames_cvs
//  Updated Javadoc. Changed the unmarshalling so that a DeviceSet does not
//  serve as the manager of the devices listed in the device set.
//
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
//  Revision 1.3  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.2  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.1  2004/09/04 13:29:00  tames
//  *** empty log message ***
//
