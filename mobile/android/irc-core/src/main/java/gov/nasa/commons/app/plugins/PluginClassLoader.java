//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: PluginClassLoader.java,v $
//  Revision 1.3  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/05/27 18:21:26  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.commons.app.plugins;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Class loader for dealing with plugins.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:23 $
 *  @author John Higinbotham	
**/

public class PluginClassLoader
{
	private static final String CLASS_NAME = 
		PluginClassLoader.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	private static PluginClassLoader fPluginClassLoader = null;
	ArrayList fLoaders = null; 

	/**
	 * Creates a new JarClassLoader for the specified url.
	 *
	 * @param url the url of the jar file
	**/
	private PluginClassLoader()
	{
		if (sLogger.isLoggable(Level.FINER))
		{
			sLogger.entering(CLASS_NAME, "PluginClassLoader");
		}
		
		fLoaders = new ArrayList();
	}

	/**
	 * Get instance of class.
	 *
	 * @return PluginClassLoader
	**/
	public static PluginClassLoader getInstance()
	{
		if (fPluginClassLoader == null)
		{
			fPluginClassLoader = new PluginClassLoader();
		}
		return fPluginClassLoader;
	}

	/**
	 * Add a plugin.
	 *
	 * @param plugin Plugin
	**/
	public void addPlugin(Plugin plugin)
	{
		if (sLogger.isLoggable(Level.FINER))
		{
			sLogger.entering(CLASS_NAME, "addPlugin", plugin);
		}
		
		fLoaders.add(new JarClassLoader(plugin.getFile()));
	}

	/**
	 * Remove plugin. 
	 *
	 * @param plugin Plugin
	**/
	public void removePlugin(Plugin plugin)
	{
		if (sLogger.isLoggable(Level.FINER))
		{
			sLogger.entering(CLASS_NAME, "removePlugin", plugin);
		}
		
		JarClassLoader jcl;
		for (int i=0; i<fLoaders.size(); i++)
		{
		 	jcl = (JarClassLoader) fLoaders.get(i);	
			if (jcl.getFile().equals(plugin.getFile()))
			{
				fLoaders.remove(i);
				i=fLoaders.size();
			}
		}
	}

	/**
	 * Load class 
	 *
	 * @param name Class name
	 * @return Class
	**/
	public Class loadClass(String name) throws ClassNotFoundException
	{
		if (sLogger.isLoggable(Level.FINER))
		{
			sLogger.entering(CLASS_NAME, "loadClass", name);
		}
		
		JarClassLoader loader;
		Class rval = null;
		boolean continueLooking;
		for (int i=0; i<fLoaders.size(); i++)
		{
			loader = (JarClassLoader) fLoaders.get(i);
			try 
			{
				rval = loader.loadClass(name);
				continueLooking = false;
			}
			catch (Exception e)
			{
				continueLooking = true;
			}
			if (!continueLooking)
			{
				i=fLoaders.size();
			}
		}
		if (rval == null)
		{
			throw new ClassNotFoundException();
		}
		return rval;
	}
}
