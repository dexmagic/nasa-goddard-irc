//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: UnknownScriptException.java,v $
//	Revision 1.2  2005/04/12 15:35:18  tames_cvs
//	Modified the Scripts and ScriptException classes to make them consistent
//	with the pattern used in the Messages class.
//	
//	Revision 1.1  2004/08/11 05:42:57  tames
//	Script support
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

package gov.nasa.gsfc.irc.scripts;


/**
 * Indicates that no script exists with the specified type name.
 *
 * @version     $Date: 2005/04/12 15:35:18 $
 * @author      Jeremy Jones
**/
public class UnknownScriptException extends ScriptException
{
	/**
	 * Create a new UnknownScriptException.
	 * 
	 * @param scriptName that could not be found
	 */
	public UnknownScriptException(String scriptName)
	{
		super("Unknown script: " + scriptName);
	}
}
