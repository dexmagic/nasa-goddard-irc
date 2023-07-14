//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.devices;

import gov.nasa.gsfc.commons.publishing.BusEventListener;
import gov.nasa.gsfc.commons.publishing.BusEventPublisher;
import gov.nasa.gsfc.irc.components.IrcComponent;
import gov.nasa.gsfc.irc.devices.description.DeviceDescriptor;

/**
 * The DeviceProxy interface represents the minimal interface 
 * for an object representing an external device.  
 *  
 * @version	$Date: 2006/04/18 04:03:35 $
 * @author tames
 */
public interface DeviceProxy
	extends IrcComponent, BusEventListener, BusEventPublisher
{
	/**
	 * Add a subsystem instrument to this proxy.
	 *
	 * @param descriptor DeviceDescriptor for new subsystem
	**/
	public abstract void addSubsystem(DeviceDescriptor descriptor);
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DeviceProxy.java,v $
//  Revision 1.2  2006/04/18 04:03:35  tames
//  Changes to support sending and receiving messages via the EventBus.
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
//  Revision 1.5  2004/09/27 20:10:43  tames
//  Reflect changes to message handling and message events.
//
//  Revision 1.4  2004/08/03 20:31:41  tames_cvs
//  Many configuration and descriptor changes
//
//  Revision 1.3  2004/06/07 14:19:45  tames_cvs
//  Changed to interface
//
