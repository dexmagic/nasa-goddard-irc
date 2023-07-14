//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, 
//	Code 580 for the Instrument Remote Control (IRC) project.
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;
import org.python.core.PyObject;

import gov.nasa.gsfc.commons.system.io.FileUtil;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;


/**
 * The DefaultScriptEvaluator is a wrapper around IBM Alphaworks Bean 
 * Scripting Framework (BSF) to execute a script or expression. 
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/04/06 14:59:46 $
 *  @author	    Melissa Hess
 *  @author	    Troy Ames
 */
public class DefaultScriptEvaluator implements ScriptEvaluator
{
	private static final String CLASS_NAME = DefaultScriptEvaluator.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	/**
	 * This array contains a string for each extension supported by IRC.
	 */
	public static final String[] SUPPORTED_LANG_EXTENSIONS = {"py", "js"};
	
	private static LinkedList sAvailableManagers = new LinkedList();
	private static Map sLanguageMapping = null;
	
	/**
	 * Create a ScriptEvaluator.
	 */
	public DefaultScriptEvaluator()
	{
		initializeLanguageMapping();
	}
	
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
	public synchronized double evalExpression(String expr, Map namespace)
		throws NonNumericResultException
	{
		BSFManager manager = this.getBSFManager();
		PyObject returnValue = null;
		try
		{
			if(namespace!=null)
			{
				Iterator i = namespace.keySet().iterator();
				while (i.hasNext())
				{
					String key = (String) i.next();
					Object value = namespace.get(key);
					if(value!=null)
					{
						Class c = value.getClass();
						manager.declareBean(key,value,c);
					}
				}
			}
			
			returnValue = (PyObject)manager.eval(
					ScriptEvaluator.PYTHON_LANGUAGE, null, 0, 0, expr);
			double returnDouble =
				((Number) returnValue.__tojava__(Number.class)).doubleValue();
			
			return returnDouble;
		}
		catch (ClassCastException ex)
		{
			String message = "Encountered exception evaluating expression: " 
				+ expr;			
			sLogger.logp(Level.WARNING, CLASS_NAME, "evalExpression", message, ex);
			
			throw new NonNumericResultException(expr,returnValue);
		}
		catch (BSFException ex)
		{
			String message = "Encountered exception evaluating expression: " 
				+ expr;			
			sLogger.logp(Level.WARNING, CLASS_NAME, "evalExpression", message, ex);
			
			throw new NonNumericResultException(expr, null);
		}
		finally
		{
			this.release(manager);
		}
	}
	
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
		throws ScriptException
	{
		String language = null;
		
		String fileExt = FileUtil.getFilenameExtension(fileName);
		language = (String) sLanguageMapping.get(fileExt);
		
		if (language != null)
		{
			execute(fileName, language, arguments);
		}
		else
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Unknown script language for = " + fileName;
				
				sLogger.logp(Level.WARNING, CLASS_NAME, "execute", message);
			}
		}
	}
	
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
		throws ScriptException
	{
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = "Script Evaluator filename = " + fileName;
			
			sLogger.logp(Level.FINE, CLASS_NAME, "execute", message);
		}
		
		BSFManager manager = getBSFManager();
		
		try
		{
			// handle args - declare the args so that when the script runs
			// the script has access to these object key/value pairs
			// essentially give the script access to the script arguments values
			//
			// bsf provides a means to declare/register objects so they are
			// available for use in your scripts - we are declaring
			// any script arguments here
			
			/* NOTE: CRITICAL!!!!
			 There is currently a bug in the BSF JavascriptEngine
			 it is not allowing me to declare and undeclare objects properly
			 for use within the scripts. To avoid this problem for now if we don't
			 pool BSFManagers and thus don't reuse JavaScript Engines then we
			 don't notice the problem
			 */
			Iterator argumentKeys = null;
			if(arguments!=null)
			{
				argumentKeys = arguments.keySet().iterator();
				while(argumentKeys.hasNext())
				{
					Object key = argumentKeys.next();
					Object value = arguments.get(key);
					if(value!=null)
					{
						manager.declareBean(key.toString(),value,value.getClass());
					}
				}
			}
			
			// if the language is specified look the name up in our static map
			// to determine what that language is registered with bsf as
			
			// if the language is not specified - use the filename extension
			
			// bsf provides a means to determine the scripting language to
			// use based on the filename - when a scripting language is
			// registered in bsf you can associate it with a set of
			// filename extension.  bsf provides some by default
			if(language!=null && !language.equals(""))
			{
				language = 
					(String) DefaultScriptEvaluator.sLanguageMapping.get(language);
			}
			else
			{
				String fileExt = FileUtil.getFilenameExtension(fileName);
				language = (String) sLanguageMapping.get(fileExt);
				//language = BSFManager.getLangFromFilename (fileName.toString());
			}
			
			String script = IOUtils.getStringFromReader(
					new InputStreamReader(fileName.openStream()));
			
 			// execute the script			
			manager.exec(language, fileName.toString(), 0, 0, script);
			
			// we need to un-declare args so that we can pool managers
			// and get the BSFManager to its initial state and avoid scope issues
			if (arguments != null)
			{
				argumentKeys = arguments.keySet().iterator();
				
				while(argumentKeys.hasNext())
				{
					Object key = argumentKeys.next();
					Object value = arguments.get(key);
					if(value!=null)
					{
						manager.undeclareBean(key.toString());
					}
				}
			}
		}
		catch(IOException e)
		{
			String message = "Encountered exception reading from script " 
				+ fileName;			
			sLogger.logp(Level.WARNING, CLASS_NAME, "runScript", message, e);
		}
		catch (BSFException e)
		{
			String message = "Encountered exception running script " 
				+ fileName;			
			sLogger.logp(Level.SEVERE, CLASS_NAME, "execute", message, e);
			System.out.println(" Cause:" + e.getMessage());
			
			throw new ScriptException(e.getMessage(), e);
		}
		finally
		{
			// release the manager for use for another script
			release(manager);
		}
	}
	
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
	public void execute(Script script, Map args)
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		if (args != null)
		{
			script.putArguments(args);
		}
		
		execute(script);
	}
	
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
	public void execute(Script script)
	throws ScriptException, InvalidScriptException, UnknownScriptException
	{
		// Validate the script - throws ScriptException if
		// not valid
		Irc.getScriptValidator().validate(script);
		
		if (script.isSynchronous())
		{
			doExecute(script);
		}
		else
		{
			// TODO implement thread pooling to reduce Thread creation overhead
			new EvaluationThread(script).start();
		}
	}
	
	/**
	 * Really execute the specified Script.  If the
	 * script requested asynchronous execution, this method will
	 * be called from an EvaluationThread.
	 *
	 * @param	script	The script invocation to execute
	 * @throws ScriptException	A problem occured while executing
	 *					the script.
	 * @see #execute
	 */
	private void doExecute(Script script) throws UnknownScriptException
	{
		ScriptDescriptor descriptor;
		
		URL scriptUrl;
		String language;
		
		descriptor = (ScriptDescriptor) script.getDescriptor();
		language   = descriptor.getLanguage();
		scriptUrl  = descriptor.getScriptLocation();
		
		if (scriptUrl != null)
		{
			try
			{
				execute(scriptUrl, language, script.getArguments());
			}
			catch (ScriptException e)
			{
				String message = "Encountered exception running script  " 
					+ script.getFullyQualifiedName();			
				
				sLogger.logp(Level.WARNING, CLASS_NAME, "doExecute", message, e);
			}
		}
		else
		{
			String message = "Script is null " 
				+ script.getFullyQualifiedName();			
			
			sLogger.logp(Level.WARNING, CLASS_NAME, "doExecute", message);
			
			throw new UnknownScriptException(script.getFullyQualifiedName());
		}
	}
	
	/**
	 * Release the BSFManager, indicating that the script that was
	 * being executed by it has completed execution.
	 */
	private void release(BSFManager manager)
	{
		synchronized(sAvailableManagers)
		{
			// System.out.println("++++++++RELEASING Adding BSFMANAGER to available");
			DefaultScriptEvaluator.sAvailableManagers.add(manager);
		}
	}
	
	/**
	 * This method maps names of languages to the actual language name
	 * that is registered with BSF.
	 */
	private void initializeLanguageMapping()
	{
		sLanguageMapping = new HashMap();
		sLanguageMapping.put("py", ScriptEvaluator.PYTHON_LANGUAGE);
		sLanguageMapping.put("JPython", ScriptEvaluator.PYTHON_LANGUAGE);
		sLanguageMapping.put("jpython", ScriptEvaluator.PYTHON_LANGUAGE);
		sLanguageMapping.put("python", ScriptEvaluator.PYTHON_LANGUAGE);
		sLanguageMapping.put("Python", ScriptEvaluator.PYTHON_LANGUAGE);
		sLanguageMapping.put("jython", ScriptEvaluator.PYTHON_LANGUAGE);
		sLanguageMapping.put("Jython", ScriptEvaluator.PYTHON_LANGUAGE);
		sLanguageMapping.put("js", ScriptEvaluator.JAVASCRIPT_LANGUAGE);
		sLanguageMapping.put("javascript", ScriptEvaluator.JAVASCRIPT_LANGUAGE);
		sLanguageMapping.put("Javascript", ScriptEvaluator.JAVASCRIPT_LANGUAGE);
		sLanguageMapping.put("JavaScript", ScriptEvaluator.JAVASCRIPT_LANGUAGE);		
	}
	
	/**
	 * Retrieve an available BSFManager for use. NOTE: In the Bean Scripting
	 * Framework, a BSFManager manages different scripting engines however,
	 * a single BSFManager only makes use of a single instance of each
	 * engine type. Therefore, if for example a script calls another script
	 * by default if you use a single BSFManager then the same scritpting
	 * engine instance will execute both scripts. This can cause scope issues -
	 * if the arguments into the first script are named the same as the arguments
	 * into the second script but have different values - the values for the
	 * first script will be overridden. Therefore, we will pool BSFManager
	 * to avoid this issue. A BSFManager will only be asked to execute a single
	 * script and it will maintain the namespace for that single script. If a script
	 * calls another script then another BSFManager will be used.
	 *
	 *       NOTE: CRITICAL!!!!
	 There is currently a bug in the BSF JavascriptEngine
	 it is not allowing me to declare and undeclare objects properly
	 for use within the scripts. To avoid this problem for now if we don't
	 pool BSFManagers and thus don't reuse JavaScript Engines then we
	 don't notice the problem. For now, I am creating a new BSFManager
	 for each script that is run!
	 
	 */
	private BSFManager getBSFManager()
	{
		BSFManager bsfM = null;
		
		/* NOTE: CRITICAL!!!!
		 There is currently a bug in the BSF JavascriptEngine
		 it is not allowing me to declare and undeclare objects properly
		 for use within the scripts. To avoid this problem for now if we don't
		 pool BSFManagers and thus don't reuse JavaScript Engines then we
		 don't notice the problem
		 */
		synchronized (sAvailableManagers)
		{
			if (sAvailableManagers.size() > 0)
			{
				bsfM = (BSFManager)sAvailableManagers.removeFirst();
			}
		}
		
		// if we have no bsf managers available create and initialize one
		if (bsfM == null)
		{
			//System.out.println("+++++++++++++++NEW BSF MANAGER+++++++++++");
			bsfM = new BSFManager();
			
			//register the irc jython engine
			String className = 
				Irc.getDescriptorFramework().findInGlobalMap(
						"ScriptType", ScriptEvaluator.PYTHON_LANGUAGE);
			
			String[] extensions = {".py"};
			
			BSFManager.registerScriptingEngine(
					ScriptEvaluator.PYTHON_LANGUAGE, className, extensions);
			
			//bsfM.setClassLoader(Irc.getResourceManager().getDefaultLoader());
			
			// below is not needed b/c BSF provides this as default
			//String[] jsext = {".js"};
			//bsfM.registerScriptingEngine(JAVASCRIPT_LANGUAGE,"com.ibm.bsf.engines.javascript.JavaScriptEngine",jsext);
		}
		
		return bsfM;
	}
	
	public static class NonNumericResultException extends Exception
	{
		private String fExpr;
		private Object fResult;
		
		public NonNumericResultException(String expr, Object result)
		{
			fExpr = expr;
			fResult = result;
		}
		
		public String getMessage()
		{
			String message = "The expression '" + fExpr 
			+ "' did not return a numeric result (" + fResult + ")";
			
			return message;
		}
	}
	
	/**
	 * Thread subclass used to execute a backgrounded Script
	 * in a separate Thread.
	 */
	protected class EvaluationThread extends Thread
	{
		private Script fScript;
		
		public EvaluationThread(Script script)
		{
			fScript = script;
		}
		
		public void run()
		{
			try
			{
				doExecute(fScript);
			}
			//Catch all other exceptions as well
			catch(Exception ex)
			{
				String message = "Encountered exception running script " 
					+ fScript.getFullyQualifiedName();			
				
				sLogger.logp(Level.WARNING, CLASS_NAME, "EvaluationThread.run",
						message, ex);
			}
		}
	}
	
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultScriptEvaluator.java,v $
//  Revision 1.7  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.6  2005/02/09 21:30:04  tames_cvs
//  Added method and capability to determine script language by
//  file extension.
//
//  Revision 1.5  2005/01/07 20:45:15  tames
//  Removed some obsolete code and comments related to
//  argument abbreviations
//
//  Revision 1.4  2004/10/01 15:47:41  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.3  2004/08/12 03:18:14  tames
//  Scripting support
//
//  Revision 1.2  2004/08/12 02:22:06  tames
//  *** empty log message ***
//
//  Revision 1.1  2004/08/11 23:01:17  tames
//  Scripting support
//
//  Revision 1.1  2004/08/11 05:42:57  tames
//  Script support
//
//
