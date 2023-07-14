//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/scripts/ScriptValidator.java,v 1.1 2004/08/11 05:42:57 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: ScriptValidator.java,v $
//	Revision 1.1  2004/08/11 05:42:57  tames
//	Script support
//	
//	Revision 1.5  2004/08/09 17:26:58  tames_cvs
//	Changed to an interface
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

import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;


/**
 * A ScriptValidator validates Scripts against their associated
 * ScriptDescriptors, ensuring that arguments are present and valid.
 *
 * @version		$Date: 2004/08/11 05:42:57 $
 * @author		Troy Ames
 */
public interface ScriptValidator
{
	/**
	 * Validate a Script against its Descriptor.
	 *
	 * @param	script		The script to be validated
	 * @throws InvalidScriptException	The script is not valid
	 */
	public void validate(Script script) throws InvalidScriptException;

	/**
	 * Validate a Script against its Descriptor but it only validates
	 * the constraints at a specific constraint level such as instrument
	 * versus operational.
	 *
	 * @param	script		The script to be validated
	 * @param	level		The level at which to validate the constraints
	 * @throws InvalidScriptException	The script is not valid
	 */
	public void validate(Script script, String level)
			throws InvalidScriptException;

	/**
	 * Validate a Script against a specified Descriptor at a specific
	 * level of constraints such as instrument or operational.  To test at
	 * all levels, the level can be null.
	 *
	 * @param	script		The script to be validated
	 * @param	descriptor	The Descriptor against which to validate
	 * @param	level		The level of the constraints to check
	 * @throws InvalidScriptException	The script is not valid
	 */
	public void validate(Script script, ScriptDescriptor descriptor,
			String level) throws InvalidScriptException;
}