//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/models/DeviceStateModel.java,v 1.1 2006/04/25 19:52:03 tames_cvs Exp $
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

package gov.nasa.gsfc.irc.devices.models;

import gov.nasa.gsfc.irc.components.ManagedComponent;


/**
 * The DeviceStateModel interface maintains instrument specific state
 * information about the instrument or subsystem.
 *
 * @version	$Date: 2006/04/25 19:52:03 $
 * @author	Troy Ames
**/
public interface DeviceStateModel extends ManagedComponent
{
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DeviceStateModel.java,v $
//  Revision 1.1  2006/04/25 19:52:03  tames_cvs
//  Relocated.
//
//  Revision 1.2  2006/04/25 19:06:13  tames_cvs
//  Changed extends to ManagedComponent to be compatible with previous
//  namespace changes.
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
//  Revision 1.3  2004/06/07 14:21:57  tames_cvs
//  Now extends initial commponent
//
