//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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
package gov.nasa.gsfc.irc.components;


/**
* Interface for components that are executable commands.
* This is an experimental design to use commands in the component browser.
* 
* <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
* for the Instrument Remote Control (IRC) project.
*
* @version	$Date: 2005/05/20 21:03:46 $
* @author	smaher
*/

public interface Command
{
     /**
     * Execute the command
     */
    void execute();
}
