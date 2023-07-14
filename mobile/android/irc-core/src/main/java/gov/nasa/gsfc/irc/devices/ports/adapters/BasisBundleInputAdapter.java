//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/adapters/BasisBundleInputAdapter.java,v 1.1 2006/01/23 17:59:50 chostetter_cvs Exp $
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

package gov.nasa.gsfc.irc.devices.ports.adapters;

import gov.nasa.gsfc.irc.data.BasisBundleSource;


/**
 * A BasisBundleInputAdapter transforms raw input data into a BasisBundle.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:50 $
 * @author Carl F. Hostetter
 */

public interface BasisBundleInputAdapter extends InputAdapter, BasisBundleSource
{

}

//--- Development History  ---------------------------------------------------
//
//  $Log: BasisBundleInputAdapter.java,v $
//  Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//
