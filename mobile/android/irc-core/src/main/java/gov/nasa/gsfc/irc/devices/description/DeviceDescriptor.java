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

import gov.nasa.gsfc.commons.publishing.paths.DefaultPath;
import gov.nasa.gsfc.commons.publishing.paths.Path;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.devices.ports.PortDescriptor;

/**
 * The class provides access to information describing an device as well
 * as the following items logicaly contained within an device: <P><BR>
 *
 *  1) sub-instruments <BR>
 *  2) message interfaces <BR>
 *  3) ports <BR><P>
 * 
 * An insturment may be a piece of hardware or software that an IRC device 
 * communicates with. Through the use of subsystem (subinstruments) descriptions
 * can be made more modular and easier to work with.<P>
 *
 * The object is built based on information contained in an IML XML file
 * which describes the insturment being interfaced with by IRC.
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>

 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			 $Date: 2006/04/18 14:02:49 $
 * @author			  John Higinbotham
**/
public class DeviceDescriptor extends ComponentDescriptor
	implements RootDescriptor
{
	private Map fDevice;  		// DeviceDescriptor collection
	private Map fScriptInterface;  	// ScriptInterfaceDescriptor collection
	private Map fMessageInterface;  // MessageInterfaceDescriptor collection
	private Map fPort;  		// PortDescriptor collection
	private Map fDevicePeer;  	// DevicePeerDescriptor collection
	private StateModelDescriptor fStateModel;  // ActivityState Model for this instrument
	private Path fPath;
	

	/**
	 * Constructs a new DeviceDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DeviceDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DeviceDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DeviceDescriptor		
	**/
	
	public DeviceDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Iml.N_DEVICES);
		
		init();

		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new DeviceDescriptor having the given name.
	 * 
	 * @param name The name of the new DeviceDescriptor
	**/
	
	public DeviceDescriptor(String name)
	{
		super(name);
		
		init();
	}

	/**
	 * Initialize class. 
	**/
	private void init()
	{
		fDevice				= new LinkedHashMap();
		fScriptInterface	= new LinkedHashMap();
		fMessageInterface	= new LinkedHashMap();
		fPort				= new LinkedHashMap();
		fDevicePeer			= new LinkedHashMap();
	}

	/**
	 *  Sets the name of the IRC element described by this Descriptor to 
	 *  the given name and updates the Path for this descriptor.
	 *
	 *  @param name The desired new name of the element described by this 
	 * 		Descriptor
	 **/
	public void setName(String name)
	{
		super.setName(name);
		fPath = null;
	}
	
	/**
	 * Get the device's peer instrument descriptors.
	 *
	 * @return Iterator of DevicePeerDescriptor objects 
	**/
	public Iterator getDevicePeers()
	{
		return fDevicePeer.values().iterator();
	}

	/**
	 * Get the device's sub instrument descriptors with the specified name. 
	 *
	 * @param  name Name of sub instrument.
	 * @return Subdevice descriptor with the specified name.
	**/
	public DeviceDescriptor getSubdevice(String name)
	{
		return (DeviceDescriptor)fDevice.get(name);
	}

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
	 * Get the device's collection of subdevice descriptors.
	 *
	 * @return All of the device's subdevice descriptors.
	**/
	public Iterator getSubdevices()
	{
		return fDevice.values().iterator();
	}

	/**
	 * Get the device's script interface descriptors with the specified name. 
	 *
	 * @param  name Name of script interface. 
	 * @return Script interface descriptor with the specified name.
	**/
	public ScriptInterfaceDescriptor getScriptInterface(String name)
	{
		return (ScriptInterfaceDescriptor)fScriptInterface.get(name);
	}

	/**
	 * Get the device's collection of script interface descriptors.
	 *
	 * @return All of the device's script interface descriptors. 
	**/
	public Iterator getScriptInterfaces()
	{
		return fScriptInterface.values().iterator();
	}

	/**
	 * Get the number of ScriptInterfaces this device has.
	 *
	 * @return the number of ScriptInterfaces. 
	**/
	public int getScriptInterfaceSize()
	{
		return fScriptInterface.size();
	}

	/**
	 * Get the device's message interface descriptor with the specified name. 
	 *
	 * @param  name Name of message interface. 
	 * @return Message interface descriptor with the specified name.
	**/
	public MessageInterfaceDescriptor getMessageInterface(String name)
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
	 * Get the device's port descriptor with the specified name. 
	 *
	 * @param  name Name of port. 
	 * @return Port descriptor with the specified name.
	**/
	public PortDescriptor getPort(String name)
	{
		return (PortDescriptor)fPort.get(name);
	}

	/**
	 * Get the device's collection of port descriptors.
	 *
	 * @return All of the instrument's port descriptors. 
	**/
	public Iterator getPorts()
	{
		return fPort.values().iterator();
	}

	/**
	 * Get the number of ports this device has.
	 *
	 * @return the number of ports. 
	**/
	public int getPortSize()
	{
		return fPort.size();
	}

	/**
	 * Get the instrument's state model descriptor. 
	 *
	 * @return ActivityState Model Descriptor. 
	**/
	public StateModelDescriptor getStateModel()
	{
		return fStateModel;
	}

	/**
	 * Set the instrument's state model descriptor. 
	 *
	 * @param descriptor ActivityState Model Descriptor. 
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setStateModel(StateModelDescriptor descriptor)
	{
		fStateModel = descriptor;
	}

	/**
	 * Unmarshall descriptor from XML. 
	**/
	private void xmlUnmarshall()
	{
		//--- Load the ActivityState Model
		Map tmpHA = new LinkedHashMap();
		fSerializer.loadChildDescriptorElements(
			Iml.E_STATE_MODEL,
			tmpHA,
			Iml.C_STATE_MODEL,
			fElement,
			this,
			fDirectory);
		Iterator i = tmpHA.values().iterator();
		if (i.hasNext())
		{
			fStateModel = (StateModelDescriptor) i.next();
		}

		//--- Load the Script Interface
		fSerializer.loadChildDescriptorElements(
			Iml.E_SCRIPT_INTERFACE,
			fScriptInterface,
			Iml.C_SCRIPT_INTERFACE,
			fElement,
			this,
			fDirectory);

		//--- Load the Message Interface
		fSerializer.loadChildDescriptorElements(
			Iml.E_MESSAGE_INTERFACE,
			fMessageInterface,
			Iml.C_MESSAGE_INTERFACE,
			fElement,
			this,
			fDirectory);

		//--- Order matters, ports must be built after the interfaces
		fSerializer.loadChildDescriptorElements(
			Iml.E_PORT,
			fPort,
			Iml.C_PORT,
			fElement,
			this,
			fDirectory);

		//--- Load the SubDevices
		fSerializer.loadChildDescriptorElements(
			Iml.E_DEVICE,
			fDevice,
			Iml.C_DEVICE,
			fElement,
			this,
			fDirectory);

		//--- Load PeerDevices
		fSerializer.loadChildDescriptorElements(
			Iml.E_DEVICE_PEER,
			fDevicePeer,
			Iml.C_DEVICE_PEER,
			fElement,
			this,
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
			fDevice,
			Iml.C_DEVICE,
			null,
			fDirectory);

		fSerializer.storeDescriptorElement(
			Iml.E_MESSAGE_INTERFACE,
			fMessageInterface,
			Iml.C_MESSAGE_INTERFACE,
			null,
			fDirectory);

		//--- Store the SubDevices
		//fSerializer.storeDescriptorElement(Iml.E_DEVICE, fDevice, Iml.C_DEVICE, element, fDirectory);
		//---Store the ActivityState Model 
		//fSerializer.storeDescriptorElement(Iml.E_STATE_MODEL, fStateModel, Iml.C_STATE_MODEL, element, fDirectory);
		//--- Store the Delegate Type 
		//fSerializer.storeAttribute(Iml.A_DELEGATE, fDelegateType, element);
		//--- Store the Message Interface
		//fSerializer.storeDescriptorElement(Iml.E_COMMAND_INTERFACE, fMessageInterface, Iml.C_COMMAND_INTERFACE,
		//	element, fDirectory);
		//--- Store the Data Interface
		//fSerializer.storeDescriptorElement(Iml.E_DATA_INTERFACE, fDataInterface, Iml.C_DATA_INTERFACE,
		//		element, fDirectory);
		//--- Store the Ports 
		//fSerializer.storeDescriptorElement(Iml.E_PORT, fPort, Iml.C_PORT, element, fDirectory);
	}

	/**
	 * Do XML marshalling on this descriptor tree using the hybrid approach. 
	 *
	 * @return Root element of marshalled descriptor tree.
	**/
	public Element doHybridXmlMarshall()
	{
		xmlMarshall(null);	
		return fElement;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DeviceDescriptor.java,v $
//  Revision 1.18  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.17  2006/04/18 04:05:06  tames
//  Import changes to reflect relocation of Path related classes.
//
//  Revision 1.16  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.15  2005/09/27 19:42:37  tames_cvs
//  Delayed setting the path until getPath is called for the first time.
//
//  Revision 1.14  2005/09/26 21:43:30  tames_cvs
//  Updated init method to correctly replace the path.
//
//  Revision 1.13  2005/09/26 21:25:12  tames_cvs
//  Modified setName method to replace the path with a newly constructed one.
//
//  Revision 1.12  2005/09/26 20:04:13  tames_cvs
//  Overloaded the setName method to update the path for the descriptor.
//
//  Revision 1.11  2005/02/04 21:42:23  tames_cvs
//  Import changes to reflect the relocation of the Path interface.
//
//  Revision 1.10  2005/02/03 07:02:29  tames
//  Additional code cleanup and simplication.
//
//  Revision 1.9  2005/02/02 22:56:35  tames_cvs
//  Added methods to get the number of ports and script interfaces contained
//  in device.
//
//  Revision 1.8  2005/02/01 18:10:58  tames
//  Added getReversedPath method.
//
//  Revision 1.7  2005/01/21 20:16:51  tames
//  Reflects a refactoring of Message paths to support a new Path interface
//  and implementation.
//
//  Revision 1.6  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.5  2004/11/19 21:41:22  smaher_cvs
//  Added getMessageByAbbreviation
//
//  Revision 1.4  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
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
//  Revision 1.15  2004/09/21 15:09:19  tames
//  Added functionality to support a destination path for messages.
//
//  Revision 1.14  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.13  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.12  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.11  2004/09/04 13:29:00  tames
//  *** empty log message ***
//
//  Revision 1.10  2004/08/26 14:34:25  tames
//  changed proxyType attribute to type for consistency
//
//  Revision 1.9  2004/08/06 14:25:56  tames_cvs
//  Port and Instrument changes
//
//  Revision 1.8  2004/08/03 20:32:01  tames_cvs
//  Many configuration and descriptor changes
//
//  Revision 1.7  2004/07/27 21:09:48  tames_cvs
//  Schema changes
//
//  Revision 1.6  2004/07/16 15:18:31  chostetter_cvs
//  Revised, refactored Component activity state
//
//  Revision 1.5  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.4  2004/06/30 20:43:56  tames_cvs
//  Changed references of commands to messages
//
//  Revision 1.3  2004/06/30 03:30:43  tames_cvs
//  Modified some attribute and element names
//
