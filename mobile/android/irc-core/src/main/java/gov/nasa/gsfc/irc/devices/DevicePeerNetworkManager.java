//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/DevicePeerNetworkManager.java,v 1.1 2004/09/28 19:26:32 tames_cvs Exp $
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

import gov.nasa.gsfc.commons.net.PeerNetworkManager;
import gov.nasa.gsfc.irc.devices.description.DevicePeerDescriptor;


/**
 * The PeerNetworkManager class is a singleton that serves as the bridge to the
 * distributed network discovery and messaging framework between peers.
 * The network implementation uses
 * Jxta for dynamic discovery of other peers and publication of XML
 * descriptions via a Jxta service. The <code>getInstance</code> method returns
 * the instance of the singleton.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2004/09/28 19:26:32 $
 * @author	Troy Ames
**/

public class DevicePeerNetworkManager extends PeerNetworkManager
{
	/**
	 * Find a published instrument description from the specified descriptor.
	 * <p>
	 * Replies to this request will be published as an ImlCommand to all
	 * subscribers.
	 * </p>
	 *
	 * @param  descriptor  a DevicePeerDescriptor
	 *
	 * @see gov.nasa.gsfc.irc.datamodel.ImlCommand
	 * @see gov.nasa.gsfc.irc.description.DevicePeerDescriptor
	 */
	public void findDescriptions(DevicePeerDescriptor descriptor)
	{
		if (descriptor != null)
		{
//			sLog.log("PeerNetworkManager.findDescriptions(DevicePeerDescriptor: "
//				+ " group=" + descriptor.getGroup()
//				+ " peer=" + descriptor.getPeer()
//				+ " description=" + descriptor.getDescription()
//				+ " timeout=" + descriptor.getTimeout()
//				+ " response limit=" + descriptor.getResponseLimit(),
//				Log.NET_DEBUG_CAT);

			findDescriptions(
				descriptor.getGroup(),
				descriptor.getPeer(),
				descriptor.getDescription(),
				descriptor.getTimeout(),
				descriptor.getResponseLimit());
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DevicePeerNetworkManager.java,v $
//	Revision 1.1  2004/09/28 19:26:32  tames_cvs
//	Reflects changing the name of Instrument related classes and methods
//	to Device since a device can include sensors, software, simulators etc.
//	Instrument maybe used in the future for a specific device type.
//	
//	Revision 1.1  2004/09/27 22:19:02  tames
//	Reflects the relocation of Instrument descriptors and
//	implementation classes to the devices package.
//	
//
//	 1	IRC	   1.0		 12/11/2001 5:33:12 PMTroy Ames
//	
