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
//     any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//     explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.devices.description;

import gov.nasa.gsfc.irc.description.xml.xsd.Xsd;


/**
 * The interface serves as a central location for defining the constants associated 
 * with the IML XML schema. <P>
 *
 * Developers should be certain to refer to the IRC schemas to gain a better understanding
 * of the structure and content of the data used to build the descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2005/05/23 15:28:48 $
 * @author John Higinbotham
 * @author Troy Ames
**/

public interface Iml extends Xsd
{
	static final String PKG = "gov.nasa.gsfc.irc.devices.description";
	
	static final String V_SCHEMA	= "iml.xsd";
	
//	static final String IML_URI = "http://aaa.gsfc.nasa.gov/iml";
//	static final Namespace NAMESPACE = Namespace.getNamespace(null, IML_URI);
	
	//InstrumentPeer:
	static final String A_PEER				= "peer";
	static final String A_PEER_GROUP			= "group";
	static final String A_PEER_DESCRIPTION		= "description";
	static final String A_PEER_RESPONSE_LIMIT	= "responseLimit";

	//Constraint Level:
	public final static String OPERATIONAL_LEVEL	= "operational";
	public final static String INSTRUMENT_LEVEL	= "instrument";

	//CommandResponseMap:
	static final String A_MESSAGE_REFERENCE	= "messageReference";
	static final String A_RESPONSE_REFERENCE	= "responseReference";

	//Response:
	static final String A_IS_FINAL	= "isFinal";
	static final String A_IS_ERROR	= "isError";

	//Message:
	static final String A_TIMEOUT		= "timeout";
	static final String A_SYNCHRONOUS	= "synchronous";

	//Script:
	static final String A_SCRIPT_FILENAME	= "file";
	static final String A_SCRIPT_LANGUAGE	= "language";

	//--- Elements 
	static final String E_DEVICE				= "Device";
	static final String E_DEVICE_SET			= "DeviceSet";
	static final String E_SCRIPT_INTERFACE		= "ScriptInterface";
	static final String E_MESSAGE_INTERFACE	= "MessageInterface";
	static final String E_PORT				= "Port";
	static final String E_MESSAGE_RESPONSE_MAP	= "MessageResponseMap";
	static final String E_RESPONSE			= "Response";
	static final String E_MESSAGE				= "Message";
	static final String E_FIELD				= "Field";
	static final String E_SCRIPT				= "Script";
	static final String E_ARGUMENT			= "Argument";
	static final String E_STATE_MODEL			= "StateModel";
	static final String E_DEVICE_PEER			= "InstrumentPeer";
	static final String E_CONNECTION			= "Connection";
	static final String E_OUTPUT_ADAPTER		= "OutputAdapter";
	static final String E_INPUT_ADAPTER		= "InputAdapter";
	static final String E_INT_REF				= "InterfaceReference";
	static final String E_MSG_REF				= "MessageReference";
	static final String E_RESP_REF			= "ResponseReference";
	static final String E_MSG_INTERFACE_REF	= "MessageInterfaceReference";
	static final String E_DATA_INTERFACE		= "DataInterface";
	static final String E_DATA_INTERFACE_REF	= "DataInterfaceReference";
	static final String E_DATA_REF			= "DataReference";

	//--- Classes
	static final String C_DEVICE =  
		"gov.nasa.gsfc.irc.devices.description.DeviceDescriptor";
	static final String C_DEVICE_PEER = 
		"gov.nasa.gsfc.irc.devices.description.DevicePeerDescriptor";
	static final String C_STATE_MODEL = 
		"gov.nasa.gsfc.irc.devices.description.StateModelDescriptor";
	
	static final String C_DATA_INTERFACE = 
		"gov.nasa.gsfc.irc.devices.description.DataInterfaceDescriptor";
	
	static final String C_MESSAGE_INTERFACE = 
		"gov.nasa.gsfc.irc.devices.description.MessageInterfaceDescriptor";
	static final String C_MESSAGE = 
		"gov.nasa.gsfc.irc.messages.description.MessageDescriptor";
	static final String C_FIELD = 
		"gov.nasa.gsfc.irc.messages.description.FieldDescriptor";
	static final String C_RESPONSE = 
		"gov.nasa.gsfc.irc.messages.description.ResponseDescriptor";
	
	static final String C_SCRIPT_INTERFACE = 
		"gov.nasa.gsfc.irc.devices.description.ScriptInterfaceDescriptor";
	static final String C_SCRIPT = 
		"gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor";
	static final String C_ARGUMENT = 
		"gov.nasa.gsfc.irc.scripts.description.ArgumentDescriptor";
	
	static final String C_PORT = 
		"gov.nasa.gsfc.irc.devices.ports.PortDescriptor";
	static final String C_CONNECTION = 
		"gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor";
	static final String C_OUTPUT_ADAPTER = 
		"gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapterDescriptor";
	static final String C_INPUT_ADAPTER = 
		"gov.nasa.gsfc.irc.devices.ports.adapters.InputAdapterDescriptor";

	//--- IML MML Namespaces
	static final String N_DEVICES			= "DeviceType";
	static final String N_PORTS			= "PortType";
	static final String N_ADAPTERS		= "AdapterType";
	static final String N_INPUT_ADAPTERS	= "InputAdapterType";
	static final String N_OUTPUT_ADAPTERS	= "OutputAdapterType";
	static final String N_CONNECTIONS		= "ConnectionType";
	static final String N_STATE_MODELS	= "StateModelType";
}

//--- Development History  ---------------------------------------------------
//
//  $Log: Iml.java,v $
//  Revision 1.7  2005/05/23 15:28:48  tames_cvs
//  Changed Instrument and InstrumentSet elements to
//  Device and DeviceSet to be consistent with schema.
//
//  Revision 1.6  2004/10/14 23:09:22  chostetter_cvs
//  Message, Adapter descriptor-related changes
//
//  Revision 1.5  2004/10/14 18:42:33  tames_cvs
//  Updated input and output adapter related constants and package names.
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
//  Revision 1.16  2004/09/27 21:24:53  tames
//  Reflects a refactoring of Port architecture and renaming port parsers
//  to InputAdapters and port formatters to OutputAdapters.
//
//  Revision 1.15  2004/09/12 20:45:01  tames
//  Added NAMESPACE definition
//
//  Revision 1.14  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.13  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.12  2004/09/04 13:29:00  tames
//  *** empty log message ***
//
//  Revision 1.11  2004/08/26 14:33:39  tames
//  Added Property element
//
//  Revision 1.10  2004/08/12 03:17:40  tames
//  Scripting support
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
//  Revision 1.6  2004/07/19 21:06:47  tames_cvs
//  Package name changes
//
//  Revision 1.4  2004/06/30 03:30:43  tames_cvs
//  Modified some attribute and element names
//
//  Revision 1.3  2004/06/07 14:23:11  tames_cvs
//  Minor changes
//
//  Revision 1.2  2004/06/04 14:36:08  tames_cvs
//  added proxy type definition
//
//  Revision 1.1  2004/05/12 22:09:03  chostetter_cvs
//  Initial version
// 
