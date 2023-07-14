//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/scripts/description/ScriptContainer.java,v 1.2 2004/07/12 14:26:23 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ScriptContainer.java,v $
//  Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/06/30 20:45:14  tames_cvs
//  Initial Version
//
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

package gov.nasa.gsfc.irc.scripts.description;

/**
 * This interface defines those methods that should be implemented
 * by any descriptor that stores scripts.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/07/12 14:26:23 $
 * @author	Troy Ames
 */
public interface ScriptContainer
{
	/**
	 *	Add a script to this container.
	 *
	 *  @param script  script to add
	 */
	public void addScript(ScriptDescriptor script);

	/**
	 *	Remove a script from this container.
	 *
	 *  @param script  script to remove
	 */
	public void removeScript(ScriptDescriptor script);
}
