//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: StorageUtil.java,v $
//  Revision 1.4  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/05/27 18:22:51  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.1  2004/05/05 19:11:52  chostetter_cvs
//  Further restructuring
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

package gov.nasa.gsfc.commons.system.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  Collection of utility methods shared by the storage package.
 *  
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2005/04/06 14:59:46 $
 *  @author	John Higinbotham 
**/
public class StorageUtil 
{
	private static final String CLASS_NAME = 
		StorageUtil.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	private final static String DATE_FORMAT = "yyyyMMdd_HHmmssSSS";
	private final static SimpleDateFormat fFormatter = new SimpleDateFormat(DATE_FORMAT);

	/**
	 * Check that the specified directory exists. If it does not exist, try to create it. 
	 *
	 * @param path Directory path. 
	 * @return true if path exists or is successfully created, false otherwise
	**/
	public static boolean checkDirectory(String path)
	{
		File d = new File(path);
		return checkDirectory(d);
	}

	/**
	 * Check that the specified directory exists. If it does not exist, try to create it. 
	 *
	 * @param path Directory path. 
	 * @return true if path exists or is successfully created, false otherwise
	**/
	public static boolean checkDirectory(File path)
	{
		boolean rval = true;

		if (path.exists())
		{
			if (sLogger.isLoggable(Level.INFO))
			{
				String message = "Directory already exists!";
				
				sLogger.logp(Level.INFO, CLASS_NAME, 
					"checkDirectory", message);
			}
		}
		else 
		{
			if (sLogger.isLoggable(Level.INFO))
			{
				String message = "Creating directory...";
				
				sLogger.logp(Level.INFO, CLASS_NAME, 
					"checkDirectory", message);
			}
			rval = path.mkdirs();
		}
		return rval;
	}

	/**
	 * Get an object output stream for the given full file name. If problems are encountered
	 * in setting up the stream, null will be returned.
	 *
	 * @param filename Full file name. 
	 * @return ObjectOutputStream 
	**/
	public static ObjectOutputStream openOOS(String filename)
	{
		File file			   = new File(filename);
		ObjectOutputStream rval = null;
		try
		{
			FileOutputStream fos   = new FileOutputStream(file);
			rval = new ObjectOutputStream(fos);
			
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "Opened output stream for file:" + file;
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"openOOS", message);
			}
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Could not open output stream for file:" + file;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"openOOS", message, ex);
			}
		}
		return rval;
	}

	/**
	 * Get an object input stream for the given full file name. If problems are encountered
	 * in setting up the stream, null will be returned.
	 *
	 * @param filename Full file name. 
	 * @return ObjectInputStream 
	**/
	public static ObjectInputStream openOIS(String filename)
	{
		File file			  = new File(filename);
		ObjectInputStream rval = null;
		try
		{
			FileInputStream fis   = new FileInputStream(file);
			rval = new ObjectInputStream(fis);
			
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "Opened input stream for file:" + file;
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"openOIS", message);
			}
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Could not open input stream for file:" + file;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"openOIS", message, ex);
			}
		}
		return rval;
	}

	/**
	 * Get a Jar output stream for the given full file name. If problems are encountered
	 * in setting up the stream, null will be returned.
	 *
	 * @param filename Full file name. 
	 * @return JarOutputStream 
	**/
	public static JarOutputStream openJOS(String filename)
	{
		File file			   = new File(filename);
		JarOutputStream rval = null;
		try
		{
			FileOutputStream fos   = new FileOutputStream(file);
			rval = new JarOutputStream(fos);
			
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "Opened output stream for file:" + file;
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"openJOS", message);
			}
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Could not open output stream for file:" + file;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"openJOS", message, ex);
			}
		}
		return rval;
	}

	/**
	 * Get a jar input stream for the given full file name. If problems are encountered
	 * in setting up the stream, null will be returned.
	 *
	 * @param filename Full file name. 
	 * @return JarInputStream 
	**/
	public static JarInputStream openJIS(String filename)
	{
		File file			  = new File(filename);
		JarInputStream rval = null;
		try
		{
			FileInputStream fis   = new FileInputStream(file);
			rval = new JarInputStream(fis);
			
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "Opened input stream for file:" + file;
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"openJIS", message);
			}
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Could not open output stream for file:" + file;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"openJIS", message, ex);
			}
		}
		return rval;
	}

	/**
	 * Notify the processing thread to sleep. 
	 *
	 * @param how long to sleep in ms
	**/
	public static void sleep(long time)
	{
		try 
		{
			if (time > 0)
			{
				Thread.sleep(time);
			}
			Thread.yield();
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * Return a standard date format for archiving.
	 *
	 * @param date Date to format.
	 * @return Formatted date string. 
	 *
	**/
	public static String formatDate(Date d)
	{
		return fFormatter.format(d);
	}
}
