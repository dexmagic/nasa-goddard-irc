//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.library.scripts.engines;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.engines.jython.JythonEngine;
import org.apache.bsf.util.IOUtils;

import gov.nasa.gsfc.commons.app.preferences.PrefKeys;
import gov.nasa.gsfc.commons.app.preferences.PreferenceManager;
import gov.nasa.gsfc.commons.system.io.FileUtil;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.app.preferences.IrcPrefKeys;


/**
 *  This is the IRC-specific Bean Scripting Framework JPython Scripting Engine.
 *  It simply extends the capabilities of the BSF JPython Engine to take care
 *  of initializing the engine with IRC specific global functions and objects for
 *  use in IRC scripts
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/04/06 14:59:46 $
 *  @author	    Melissa Hess
 *  @author	    Troy Ames
 */
public class IrcJythonEngine extends JythonEngine
{
	private static final String CLASS_NAME = IrcJythonEngine.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	//---JPython root property
	//public static final String JPY_ROOT_PROP = PrefKeys.JPYTHON_ROOT;

	//---JPython startup script property
	public static final String JPY_CUSTOMIZATION_SCRIPT_PROP = 
		"irc.scripting.jpython.startup";

	//---JPython cache directory property
	public static final String PYTHON_CACHE_DIR_PROP = "python.cachedir";

	//---JPython install root property
	public static final String INSTALL_ROOT_PROP = "install.root";

	//---JPython temp directory property
	public static final String TMP_DIR_PROP = "scripting.jpython.tmpdir";

	//---Location of python files relative to the class path
    private static final String DEFAULT_JPY_ROOT = "resources/scripts/configurations/jython";

	//---Setup script
	public static final String JPY_LIB_SCRIPT = "resources/scripts/configurations/jython/irc_setup.py";

	//---JPython files
	public static final String LIB_DIR         = "Lib";
	public static final String REGISTRY_FILE   = "registry";
	public static final String EXCEPTIONS_FILE = "exceptions.py";
	public static final String STRING_FILE     = "string.py";
//	public static final String XDRLIB_FILE     = "xdrlib.py";


    static 
	{
		URL url       = null;
		PreferenceManager prefMgr = Irc.getPreferenceManager();

		//---Setup the root directory
		try
		{
			// TODO us a user specified path
			url = Irc.getResource(DEFAULT_JPY_ROOT);
		}
		catch (Exception e)
		{
			String message = "Encountered exception during static initialization: "; 
			sLogger.logp(Level.WARNING, CLASS_NAME, "IrcJythonEngine.static", message, e);

			url = Irc.getResource(DEFAULT_JPY_ROOT);
		}

		//---Setup a temp directory
		StringBuffer tempDir = new StringBuffer();
		tempDir.append(prefMgr.getPreference("java.io.tmpdir"));
		tempDir.append(prefMgr.getPreference("file.separator"));
		tempDir.append("jython");
		
		FileUtil.checkDirectory(tempDir.toString());

		//---Setup a default cache dir if one is not specifed
		if (prefMgr.getPreference(PYTHON_CACHE_DIR_PROP) == null)
		{
			StringBuffer cacheDir = new StringBuffer();
			cacheDir.append(prefMgr.getPreference(PrefKeys.USER_HOME_KEY));
			cacheDir.append(prefMgr.getPreference("file.separator"));
			cacheDir.append(prefMgr.getPreference(PrefKeys.USER_DIR_KEY));
			cacheDir.append(prefMgr.getPreference("file.separator"));
			cacheDir.append(prefMgr.getPreference(IrcPrefKeys.INSTRUMENT_SUBDIRECTORY));
			cacheDir.append(prefMgr.getPreference("file.separator"));
			cacheDir.append(DEFAULT_JPY_ROOT);
			// Create directory if necessary
			FileUtil.checkDirectory(cacheDir.toString());
			
			System.setProperty(PYTHON_CACHE_DIR_PROP, cacheDir.toString());
		}

		//---Deal with the install root (home of the registry and support files)
		if (url.getProtocol().compareTo("file")==0)
		{
			//---We can run directly from the resources directory
			System.setProperty(INSTALL_ROOT_PROP, url.getFile());
		}
		else
		{
			//---We are in a jar or remote location and need to run from a local copy
			System.setProperty(INSTALL_ROOT_PROP, tempDir.toString());
			String root = System.getProperty(INSTALL_ROOT_PROP);
			try
			{
				FileUtil.copyToDirectory(new URL(url.toString() + "/"
						+ REGISTRY_FILE), new File(root + "/"));
				String FROM_BASE = url.toString() + "/" + LIB_DIR + "/";
				String TO_DIR = root + "/" + LIB_DIR + "/";
				File DEST_DIR = new File(TO_DIR);
				FileUtil.copyToDirectory(new URL(FROM_BASE + STRING_FILE), DEST_DIR);
				FileUtil.copyToDirectory(new URL(FROM_BASE + EXCEPTIONS_FILE), DEST_DIR);
			}
			catch (Exception e)
			{
				String message = 
					"Encountered exception during static initialization: ";
				sLogger.logp(Level.WARNING, CLASS_NAME,
						"IrcJythonEngine.static", message, e);
			}
		}
	}


    /**
     * Construct an IRC specific BSF JPython Scripting Engine.
     */
    public IrcJythonEngine()
    {
        super();
    }

    /**
     * Overrides the initialize provided by the BSF JPython Scripting Engine.
     * This makes some IRC functions and object globally for all IRC
     * JPython scripts.
     *
     * @param mgr - the BSFManager that is managing this scripting engine.
     * @param lang - the language identifier for this scripting engine
     * @param declaredbeans - the java objects to make available as varialbles
     * in the scripts that this engine will execute
     */
    public void initialize(BSFManager mgr, String lang, Vector declaredBeans)
                throws BSFException
    {
        super.initialize(mgr, lang, declaredBeans);

        try
        {
		    URL libScript = getIrcPythonLibScript();
		    
		    if (libScript != null)
		    {
	            exec(libScript.toString(), 0, 0, 
	            		IOUtils.getStringFromReader(
	            				new InputStreamReader(libScript.openStream())));		    	
		    }

		    URL pythonStartupFilename =  getIrcPythonCustomizationScript();

		    if (pythonStartupFilename != null)
		    {
            	exec(pythonStartupFilename.toString(), 0, 0, 
            			IOUtils.getStringFromReader(
            					new InputStreamReader(
            							pythonStartupFilename.openStream())));       
		    }
        }
        catch(Exception ex)
        {
			String message = "Encountered exception initializing script engine ";			
			
			sLogger.logp(Level.WARNING, CLASS_NAME, "initialize", message, ex);
		}
    }

    /**
	 * Retrieve the path to the generic Python script which defines
	 * the mapping from Python into IRC.  This script is a part of the
	 * IRC framework, and is instrument-independent.
	 */
	private URL getIrcPythonLibScript()
	{
		URL url = Irc.getResource(JPY_LIB_SCRIPT);
		return url;
	}

	/**
	 * Retrieve the path to the installation-dependent Python
	 * configuration script.  This script sets up constants and
	 * functions specific to the installation described in the plist;
	 * it may also provide access to instrument-specific Java
	 * classes.
	 */
	private URL getIrcPythonCustomizationScript()
	{
		//TODO is this necessary
		URL url = Irc.getResource(
				Irc.getPreference(JPY_CUSTOMIZATION_SCRIPT_PROP));
		
		return url;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: IrcJythonEngine.java,v $
//  Revision 1.4  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.3  2005/02/08 23:21:57  tames_cvs
//  Added "irc.scripting.jpython.startup" property so applications can
//  do custom Python configurations
//
//  Revision 1.2  2004/08/12 02:23:01  tames
//  set cache directory outside of CVS workspace
//
//  Revision 1.1  2004/08/11 23:01:17  tames
//  Scripting support
//
