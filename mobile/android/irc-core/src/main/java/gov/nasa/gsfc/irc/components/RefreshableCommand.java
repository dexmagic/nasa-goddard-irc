//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.irc.components;

/**
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 * 
 * @version	$Id: RefreshableCommand.java,v 1.1 2005/05/20 21:03:54 smaher_cvs Exp $
 * @author	$User:$
 */
public interface RefreshableCommand extends Command {
    
    /**
     * Have this command refresh it's own parameters.
     * This is relevant when the parameters should be pulled
     * from telemetry or other external references that won't 
     * or can't call setters on a command.  For example, when the
     * MarkIII IRC starts up, the commands pull the current parameter
     * values from the MarkIII telemetry.
     */
    void refreshParameters();
}

//
//--- Development History  ---------------------------------------------------
//
//	$Log: RefreshableCommand.java,v $
//	Revision 1.1  2005/05/20 21:03:54  smaher_cvs
//	Initial
//	