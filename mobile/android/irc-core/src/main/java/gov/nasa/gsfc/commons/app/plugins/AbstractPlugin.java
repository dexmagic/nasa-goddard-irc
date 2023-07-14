//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractPlugin.java,v $
//  Revision 1.5  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/05/27 17:51:15  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.2  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
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

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import bsh.Interpreter;

/**
 *  This class serves as a basis for other plugins.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2006/01/23 17:59:54 $
 *  @author John Higinbotham	
**/
public abstract class AbstractPlugin implements Plugin
{
	private static final String CLASS_NAME = 
		AbstractPlugin.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	//---Constants
	protected static String PROPS			= "properties.txt";
	protected static String INSTALL_SCRIPT   = "install.bsh";
	protected static String UNINSTALL_SCRIPT = "uninstall.bsh";
	protected static String CLASS			= "class";

	//---Vars
	protected File fFile					 = null; 
	protected URL fUrl					   = null; 
	protected String fName				   = null;
	protected String fAuthor				 = null;
	protected String fVersion				= null;
	protected Class[] fClasses			   = null;
	protected boolean fBroken				= false;
	protected Properties fProperties		 = null;
	protected JarFile fJarFile			   = null; 

//-------------------------------------------------------------------

	/**
	 * Constructor 
	 *
	 * @param File Plugin file 
	**/
	public AbstractPlugin(File file)
	{
		try
		{
			fFile	= file;
			fUrl	 = fFile.toURL();
			fJarFile = new JarFile(fFile);
			PluginClassLoader.getInstance().addPlugin(this);
			loadProperties();
		}
		catch (Exception e)
		{
			fBroken = true;
			e.printStackTrace();
		}
	}

	/**
	 * Get plugin file.
	 *
	 * @return File
	**/
	public File getFile()
	{
		return fFile;
	}

	/**
	 * Get plugin name.
	 *
	 * @return filename
	**/
	public String getName()
	{
		return fName;
	}

	/**
	 * Get plugin author.
	 *
	 * @return author
	**/
	public String getAuthor()
	{
		return fAuthor;
	}

	/**
	 * Get plugin version.
	 *
	 * @return version
	**/
	public String getVersion()
	{
		return fVersion;
	}

	/**
	 * Get plugin classes.
	 *
	 * @return Class[]
	**/
	public Class[] getClasses()
	{
		return fClasses;
	}

	/**
	 * Determine if plugin is broken or not. 
	 *
	 * @return boolean
	**/
	public boolean isBroken()
	{
		return fBroken;
	}

//	/**
//	 * Load classes associated with this plugin. 
//	 *
//	**/
//	private void loadClasses()
//	{
//		ZipEntry entry = null;
//		String name	= null;
//		String ext	 = null;
//		Enumeration i  = fJarFile.entries();
//
//		while (i.hasMoreElements())
//		{
//			entry = (ZipEntry) i.nextElement();
//			name = entry.getName();
//			ext = FileUtil.getFilenameExtension(new File(name));
//			Class c = null;
//			if (ext.compareTo(CLASS) == 0)
//			{
//				try
//				{
//					c = PluginClassLoader.getInstance().loadClass(FileUtil.stripExtension(name));
//				}
//				catch (Exception ex)
//				{
//					if (sLogger.isLoggable(Level.WARNING))
//					{
//						String message = "Could not load plugin class: " + c;
//						
//						sLogger.logp(Level.WARNING, CLASS_NAME, 
//							"loadClasses", message, ex);
//					}
//				}
//			}
//		}
//	}

	/**
	 * Unload classes associated with this plugin. 
	 *
	**/
	private void unloadClasses()
	{
		PluginClassLoader.getInstance().removePlugin(this);
	}

	/**
	 * Load plugin properties.
	 *
	**/
	protected void loadProperties()
	{
		try
		{
			ZipEntry entry = fJarFile.getEntry(PROPS);
			if (entry != null)
			{
				fProperties = new Properties();
				InputStream is = fJarFile.getInputStream(entry);
				fProperties.load(is);
				is.close();
				fName	   = fProperties.getProperty(NAME);
				fVersion	= fProperties.getProperty(VERSION);
				fAuthor	 = fProperties.getProperty(AUTHOR);
			}
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Could not load properties";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"loadProperties", message, ex);
			}
		}
	}

	/**
	 * Uninstall plugin.
	 *
	**/
	public void uninstall()
	{
		try
		{
			unloadClasses();
			runScript(UNINSTALL_SCRIPT);
			fJarFile.close();
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Could not unload classes";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"uninstall", message, ex);
			}
		}
	}

	/**
	 * Install plugin.
	 *
	**/
	public void install()
	{
		runScript(INSTALL_SCRIPT);
	}

	/**
	 * Run script contained in plugin file. 
	 *
	 * @param name Script name 
	**/
	public void runScript(String name)
	{
		try
		{
			ZipEntry entry = fJarFile.getEntry(name);
			if (entry != null)
			{
				Interpreter bsh	   = new Interpreter();
				InputStream is		= fJarFile.getInputStream(entry);
				InputStreamReader isr = new InputStreamReader(is);
				bsh.eval(isr);
				is.close();
				bsh = null;
			}
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Could not run script";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"runScript", message, ex);
			}
		}
	}

	/**
	 * Return string representation of object.
	 * 
	 * @return String
	**/
	public String toString()
	{
		return fName;
	}
}
