//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/net/DescriptionListener.java,v 1.2 2004/07/12 14:26:23 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log:
//	 1	IRC	   1.0		 12/11/2001 5:33:07 PMTroy Ames
//	$
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

package gov.nasa.gsfc.commons.net;


/**
 * The DescriptionListener will receive notification when a description is
 * received from a remote device.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2004/07/12 14:26:23 $
 * @author	Troy Ames
**/
public interface DescriptionListener
{
	/**
	 * Handle a instrument description that was received by this device.
	 * This method will be called when a requested description is
	 * received.
	 *
	 * @param   evt the Description Event received
	 *
	 * @see DescriptionEvent
	 */
	public void handleDescriptionEvent(DescriptionEvent evt);
}