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

/**
 * This interface is used to signal a listener that the contents of the
 * instruments has changed in the descriptor library. 
 *	
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/02/01 16:52:46 $
 *  @author		Bhavana Singh
**/
public interface DeviceChangeListener 
{
	/**
	 *  Handle a device change in the descriptor library. 
	 * 
	**/
	public void handleDeviceChange();
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DeviceChangeListener.java,v $
//  Revision 1.2  2005/02/01 16:52:46  tames
//  Updated to reflect "device" instead of "instrument".
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
//   1	IRC	   1.0		 12/5/2001 4:39:27 PM John Higinbotham 
//  
