//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

import java.net.URL;
import java.util.Map;

import gov.nasa.gsfc.irc.scripts.DefaultScriptEvaluator.NonNumericResultException;

/**
 * The ScriptEvaluator is the interface used by IRC to execute a
 * script or expression. 
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/02/09 21:30:04 $
 * @author	    Melissa Hess
 * @author	    Troy Ames
 */
public interface ScriptEvaluator
{
	public final String PYTHON_LANGUAGE = "Jython";
	public final String JAVASCRIPT_LANGUAGE = "JavaScript";

	/**
	 * Evaluate a String as a Python expression.
	 *
	 * @param expr		The expression to evaluate
	 * @param namespace	Set of variable definitions to use to resolve
	 *					identifiers in expression
	 *
	 * @return Resulting value from expression evaluation
	 *
	 * @exception NonNumericResultException	The expression produced a
	 *		result which could not be coerced to a Number
	 */
	public double evalExpression(String expr, Map namespace)
			throws NonNumericResultException;

	/**
	 * Execute the specified script. This method does not attempt to validate
	 * the specified script.
	 * 
	 * @param fileName	the script file name
	 * @param language	the language of the script
	 * @param arguments key/value argument pairs accessible from the script
	 * 
	 * @throws ScriptException 
	 */
	public void execute(URL fileName, String language, Map arguments)
			throws ScriptException;

	/**
	 * Execute the specified script. The file extension is used to determine 
	 * the script language. This method does not attempt to validate
	 * the specified script.
	 * 
	 * @param fileName	the script file name
	 * @param arguments key/value argument pairs accessible from the script
	 * 
	 * @throws ScriptException 
	 */
	public void execute(URL fileName, Map arguments)
		throws ScriptException;

	/**
	 * Execute a script, copying argument values in from
	 * the specified Map.
	 *
	 * @param	script	The script to be executed.
	 * @param	args	Set of argument values to copy into 
	 * 					script before execution.
	 * @throws ScriptException
	 * @throws UnknownScriptException
	 * @throws InvalidScriptException
	 *
	 * @see #execute(Script)
	 */
	public void execute(Script script, Map args) throws ScriptException,
			InvalidScriptException, UnknownScriptException;

	/**
	 * Execute the specified Script, dispatching the
	 * primitive scripts in its expansion. The script arguments are 
	 * validated against the script descriptor. If
	 * script.isSynchronous() returns false, the script is
	 * executed in a separate thread, and this call returns
	 * immediately.  Otherwise, this method does not return until the
	 * script has completed.
	 *
	 * @param	script	The script invocation to execute
	 * @throws ScriptException	
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	 */
	public void execute(Script script) throws ScriptException,
			InvalidScriptException, UnknownScriptException;
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ScriptEvaluator.java,v $
//  Revision 1.4  2005/02/09 21:30:04  tames_cvs
//  Added method and capability to determine script language by
//  file extension.
//
//  
