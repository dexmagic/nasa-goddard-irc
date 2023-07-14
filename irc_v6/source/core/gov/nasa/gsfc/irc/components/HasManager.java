//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/HasManager.java,v 1.1 2006/01/23 17:59:51 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//  $Log: HasManager.java,v $
//  Revision 1.1  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2004/06/08 14:21:53  chostetter_cvs
//  Added child/parent relationship to Components
//
//
//--- Warning ----------------------------------------------------------------
//This software is property of the National Aeronautics and Space
//Administration. Unauthorized use or duplication of this software is
//strictly prohibited. Authorized users are subject to the following
//restrictions:
//*	Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//*	The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//*	Altered versions of this software must be plainly marked as such.
//*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.components;


/**
 * Objects that have a Manager can return a reference to that Manager.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:51 $
 * @author Carl F. Hostetter
 */


public interface HasManager
{
	/**
	 *  Returns the Manager of this Object.
	 *  
	**/

	public ComponentManager getManager();
}
