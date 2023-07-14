//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.scripts;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;

/**
 * A collection of static functions that provide a simple facade on
 * the IRC scripting functionality. This class contains functions for executing
 * scripts. While this class is primarily intended for
 * use by external scripts, it may be used by internal Java code as well.
 *
 * @version     $Date: 2006/01/25 17:02:43 $
 * @author      Jeremy Jones
 * @author      Troy Ames
**/
public class Scripts
{
	/**
	 * Create a Script object with the given fully qualified name.
	 * 
	 * @param	scriptName	name of script
	 * @throws UnknownScriptException
	**/
	public static Script createScript(String scriptName)
			throws UnknownScriptException
	{
		ScriptDescriptor descriptor = getScriptDescriptor(scriptName);
		
		return Irc.getScriptFactory().createScript(descriptor);
	}

	/**
	 * Create a Script object with the given descriptor.
	 * 
	 * @param descriptor the descriptor of the script to create
	**/
	public static Script createScript(ScriptDescriptor descriptor)
	{
		return Irc.getScriptFactory().createScript(descriptor);
	}

	/**
	 * Executes the Script with the given fully qualified name.
	 * 
	 * @param scriptName	name of script to call
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(String scriptName) 
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{		
		callScript(getScriptDescriptor(scriptName), new Object[0]);
	}

	/**
	 * Executes the Script with the given descriptor.
	 * 
	 * @param descriptor	descriptor of script to call
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(ScriptDescriptor descriptor) 
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		callScript(descriptor, new Object[0]);
	}

	/**
	 * Executes the Script with the given descriptor.
	 * 
	 * @param descriptor	descriptor of script to call
	 * @param arg1			script argument value
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(ScriptDescriptor descriptor, Object arg1) 
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		callScript(descriptor, new Object[] { arg1 });
	}

	/**
	 * Executes the Script with the given descriptor.
	 * 
	 * @param descriptor	descriptor of script to call
	 * @param arg1			script argument value
	 * @param arg2			script argument value
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(ScriptDescriptor descriptor, Object arg1, Object arg2) 
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		callScript(descriptor, new Object[] { arg1, arg2 });
	}

	/**
	 * Executes the Script with the given descriptor.
	 * 
	 * @param descriptor	descriptor of script to call
	 * @param arg1			script argument value
	 * @param arg2			script argument value
	 * @param arg3			script argument value
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(
			ScriptDescriptor descriptor, 
			Object arg1, 
			Object arg2, 
			Object arg3) 
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		callScript(descriptor, new Object[] { arg1, arg2, arg3 });
	}

	/**
	 * Executes the Script with the given descriptor.
	 * 
	 * @param descriptor	descriptor of script to call
	 * @param arg1			script argument value
	 * @param arg2			script argument value
	 * @param arg3			script argument value
	 * @param arg4			script argument value
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(
			ScriptDescriptor descriptor, 
			Object arg1, 
			Object arg2,
			Object arg3, 
			Object arg4) 
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		callScript(descriptor, new Object[] { arg1, arg2, arg3, arg4 });
	}
		
	/**
	 * Executes the Script with the given descriptor and arguments.
	 * 
	 * @param descriptor	descriptor of script to call
	 * @param	arg1			script argument value
	 * @param	arg2			script argument value
	 * @param	arg3			script argument value
	 * @param	arg4			script argument value
	 * @param	arg5			script argument value
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(
		ScriptDescriptor descriptor,
		Object arg1,
		Object arg2,
		Object arg3,
		Object arg4,
		Object arg5)
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		callScript(
			descriptor,
			new Object[] { arg1, arg2, arg3, arg4, arg5 });
	}
		
	/**
	 * Executes the Script with the given descriptor.
	 * 
	 * @param descriptor	descriptor of script to call
	 * @param	arg1			script argument value
	 * @param	arg2			script argument value
	 * @param	arg3			script argument value
	 * @param	arg4			script argument value
	 * @param	arg5			script argument value
	 * @param	arg6			script argument value
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(
			ScriptDescriptor descriptor,
		Object arg1,
		Object arg2,
		Object arg3,
		Object arg4,
		Object arg5,
		Object arg6)
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		callScript(
			descriptor,
			new Object[] { arg1, arg2, arg3, arg4, arg5, arg6 });
	}
		
	/**
	 * Executes the Script with the given descriptor.
	 * 
	 * @param descriptor	descriptor of script to call
	 * @param	arg1			script argument value
	 * @param	arg2			script argument value
	 * @param	arg3			script argument value
	 * @param	arg4			script argument value
	 * @param	arg5			script argument value
	 * @param	arg6			script argument value
	 * @param	arg7			script argument value
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(
			ScriptDescriptor descriptor,
		Object arg1,
		Object arg2,
		Object arg3,
		Object arg4,
		Object arg5,
		Object arg6,
		Object arg7)
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		callScript(
			descriptor,
			new Object[] { arg1, arg2, arg3, arg4, arg5, arg6, arg7 });
	}
		
	/**
	 * Executes the Script with the given descriptor.
	 * 
	 * @param descriptor	descriptor of script to call
	 * @param	arg1			script argument value
	 * @param	arg2			script argument value
	 * @param	arg3			script argument value
	 * @param	arg4			script argument value
	 * @param	arg5			script argument value
	 * @param	arg6			script argument value
	 * @param	arg7			script argument value
	 * @param	arg8			script argument value
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(
			ScriptDescriptor descriptor,
		Object arg1,
		Object arg2,
		Object arg3,
		Object arg4,
		Object arg5,
		Object arg6,
		Object arg7,
		Object arg8)
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		callScript(
			descriptor,
			new Object[] { arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8 });
	}
		
	/**
	 * Executes the Script with given descriptor using the specified
	 * argument set.
	 * 
	 * @param descriptor	descriptor of script to call
	 * @param argSet		set of script argument values
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	**/
	public static void callScript(ScriptDescriptor descriptor, Set argSet) 
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		callScript(descriptor, argSet.toArray());
	}
	
	/**
	 * Executes the Script with the given descriptor using the specified 
	 * arguments. Values in array must be in the same order as
	 * fields in the descriptor.
	 * 
	 * @param	descriptor	the descriptor of the script
	 * @param	args		script argument values
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	 */
	public static void callScript(ScriptDescriptor descriptor, Object[] args) 
		throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		if (descriptor == null)
		{
			throw new IllegalArgumentException("descriptor cannot be null");
		}
		
		Script script = Irc.getScriptFactory().createScript(descriptor);
		fillScriptArgs(script, args);		
		
		validateScript(script);
		Irc.getScriptEvaluator().execute(script);
	}

	/**
	 * Executes the Script with the given descriptor using the specified 
	 * arguments. Keys in arugument map should match field names
	 * in descriptor.
	 * 
	 * @param	descriptor	the descriptor of the script
	 * @param	argMap	map of script argument values
	 * @throws ScriptException
	 * @throws InvalidScriptException
	 * @throws UnknownScriptException
	 */
	public static void callScript(ScriptDescriptor descriptor, Map map) 
		throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		if (descriptor == null)
		{
			throw new IllegalArgumentException("descriptor cannot be null");
		}
		
		Script script = Irc.getScriptFactory().createScript(descriptor);
		fillScriptArgs(script, map);		
		
		validateScript(script);
		Irc.getScriptEvaluator().execute(script);
	}

	/**
	 * Calls or executes the given Script.
	 * 
	 * @param	script	the script to execute
	 * @throws InvalidScriptException
	 * @throws ScriptException
	 * @throws UnknownScriptException
	 * 
	 * @see #validateScript(Script)
	 */
	public static void callScript(Script script) 
		throws ScriptException, InvalidScriptException, UnknownScriptException 
	{
		if (script == null)
		{
			throw new IllegalArgumentException("script cannot be null");
		}

		validateScript(script);
		Irc.getScriptEvaluator().execute(script);
	}

	/**
	 * Retrieves the Script Descriptor with the given fully qualified name.
	 * 
	 * @param	scriptName	name of script
	 * @return	descriptor for specified script
	 * @throws UnknownScriptException
	**/
	public static ScriptDescriptor getScriptDescriptor(String scriptName) 
		throws UnknownScriptException
	{
		ScriptDescriptor descriptor = 
			Irc.getDescriptorLibrary().getScriptDescriptor(scriptName);		
		
		if (descriptor == null)
		{
			throw new UnknownScriptException(
					"Descriptor not found for script: " + scriptName);
		}
		
		return descriptor;
	}
	
	

	/**
	 * Validate script against descriptor constraints.
	 * 
	 * @param	script	Script to validate
	 * @throws 	InvalidScriptException	
	 * 			if script does not match descriptor constraints
	**/
	public static void validateScript(Script script)
			throws InvalidScriptException
	{
		// Validate script
		Irc.getScriptValidator().validate(script);
	}
	
	/**
	 * Fills up a Script with values for its attributes according to the
	 * specified arguments. Values in array must be in the same order as
	 * fields in the descriptor.
	 * 
	 * @param	script	field arguments set in this script object
	 * @param	args	ordered set of script argument values
	 * @throws IllegalArgumentException
	**/
	public static void fillScriptArgs(Script script, Object[] args)
	{
		if (args == null)
		{
			throw new IllegalArgumentException
				("Internal Error: Messages.fillMessageArgs() args not allowed to be null");
		}
		
		ScriptDescriptor descriptor = 
			(ScriptDescriptor) script.getDescriptor();

		// Assume the arguments match the order specified for the attributes
		// in the descriptor
		int i = 0;
		Iterator arguments = descriptor.getArguments().iterator();
		
		while (arguments.hasNext())
		{
			FieldDescriptor argument = (FieldDescriptor) arguments.next();
			
			if (i < args.length)
			{
				putScriptField(script, argument, args[i]);
				++i;				
			}
			else
			{
				putScriptField(script, argument, argument.getDefaultValue());
			}
		}
		if (i < args.length)
		{
			throw new IllegalArgumentException(
					"Number of arguments (" + args.length 
					+ ") exceeds number of fields in " + 
					descriptor.getFullyQualifiedName());
		}
	}
	
	/**
	 * Fills up a Script with values for its attributes according to the
	 * specified arguments. Keys in arugument map should match field names
	 * in descriptor.
	 * 
	 * @param	script	field arguments set in this script object
	 * @param	argMap	map of script argument values
	 * @throws IllegalArgumentException
	**/
	public static void fillScriptArgs(Script script, Map argMap)
	{
		if (argMap == null)
		{
			throw new IllegalArgumentException(
					"Internal Error: Scripts.fillScriptArgs() " 
					+ "argMap not allowed to be null");
		}
		
		ScriptDescriptor descriptor = 
			(ScriptDescriptor) script.getDescriptor();

		// Fill in default values first
		Iterator arguments = descriptor.getArguments().iterator();
		
		while (arguments.hasNext())
		{
			FieldDescriptor argument = (FieldDescriptor) arguments.next();
			
			putScriptField(script, argument, argument.getDefaultValue());
		}
		
		// Fill in arguments
		for (Iterator argIter = argMap.keySet().iterator(); argIter.hasNext();)
		{
			String argName = (String) argIter.next();
			FieldDescriptor argument = descriptor.getArgument(argName);
			
			if (argument != null)
			{
				putScriptField(script, argument, argMap.get(argName));
			}
			else
			{
				throw new IllegalArgumentException(
						"Argument (" + argName 
						+ ") is not defined in descriptor " 
						+ descriptor.getFullyQualifiedName());
			}
		}
	}
			
	/**
	 * Stores an attribute value in the specified script object.
	 * 
	 * @param	script		attribute value stored in this script object
	 * @param	attribute	attribute in which to store the value
	 * @param	value		the new value to store
	**/
	private static void putScriptField(
			Script script, 
			FieldDescriptor argument, 
			Object value)
	{
		Object convertedValue = value;

		Class expectedType = argument.getValueClass();

		// If field is a type of Number, value must be too
		if (Number.class.isAssignableFrom(expectedType) 
				&& !(value instanceof Number))
		{
			try
			{
				value = new Double(value.toString());
			}
			catch (NumberFormatException ex)
			{
				throw new IllegalArgumentException(
						"Number type expected for argument \"" 
						+ argument.getName() + "\", got " 
						+ value.getClass().getName());	
			}
		}
		
		// Convert value to the expected type
		if (expectedType.equals(Byte.class))
		{
			convertedValue = new Byte(((Number) value).byteValue());
		}
		else if (expectedType.equals(Double.class))
		{	
			convertedValue = new Double(((Number) value).doubleValue());
		}
		else if (expectedType.equals(Float.class))
		{	
			convertedValue = new Float(((Number) value).floatValue());
		}
		else if (expectedType.equals(Integer.class))
		{	
			convertedValue = new Integer(((Number) value).intValue());
		}
		else if (expectedType.equals(Long.class))
		{	
			convertedValue = new Long(((Number) value).longValue());
		}
		else if (expectedType.equals(Short.class))
		{	
			convertedValue = new Short(((Number) value).shortValue());
		}
		
		// Store the value in the Script
		script.put(argument.getName(), convertedValue);
	}
	
	/**
	 * Defined as private to prevent instantiation.
	**/
	private Scripts()
	{
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: Scripts.java,v $
//	Revision 1.12  2006/01/25 17:02:43  chostetter_cvs
//	Support for arbitrary-length Message parsing
//	
//	Revision 1.11  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.10  2005/04/12 15:35:18  tames_cvs
//	Modified the Scripts and ScriptException classes to make them consistent
//	with the pattern used in the Messages class.
//	
//	Revision 1.9  2005/02/09 19:09:06  tames_cvs
//	Removed references to ArgumentDescriptor
//	
//	Revision 1.8  2005/02/01 18:42:57  tames
//	Changes to reflect DescriptorLibrary changes.
//	
//	Revision 1.7  2005/01/10 23:07:14  tames_cvs
//	Change callScript(Script) method not to call validateScript.
//	
//	Revision 1.6  2005/01/07 20:49:34  tames
//	Added methods to call a script given a ScriptDescriptor or a Script.
//	
//	Revision 1.5  2004/10/01 15:47:41  chostetter_cvs
//	Extensive refactoring of field/property/argument descriptors
//	
//	Revision 1.4  2004/08/19 13:41:04  jhiginbotham_cvs
//	Replace asserts with runtime exceptions.
//	
//	Revision 1.3  2004/08/12 03:18:14  tames
//	Scripting support
//	
//	Revision 1.2  2004/08/12 02:22:06  tames
//	*** empty log message ***
//	
//	Revision 1.1  2004/08/11 23:01:17  tames
//	Scripting support
//	
