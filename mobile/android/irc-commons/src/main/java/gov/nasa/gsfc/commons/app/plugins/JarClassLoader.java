//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: JarClassLoader.java,v $
//  Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gov.nasa.gsfc.commons.system.io.FileUtil;

/**
 *  Class loader for dealing with classes contained in jars. 
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:23 $
 *  @author John Higinbotham	
**/
public class JarClassLoader extends ClassLoader
{
	//---Consts
	private final static String CLASS = "class";

	//---Vars
	private File fFile = null;

//------------------------------------------------------

	/**
	 * Constructor 
	 *
	 * @param file File  
	**/
	public JarClassLoader(File file)
	{
		fFile = file;
	}
	
	/**
	 * Get file that this loader is for. 
	 *
	 * @return File  
	**/
	public File getFile()
	{
		return fFile;
	}

	/**
	 * Load a class.
	 *
	 * @param className Name of class to load.
	**/
	public Class loadClass(String className) throws ClassNotFoundException
	{
		Class c	  = null;
		boolean goon = false;

		//---Delegate to the parent first
		try
		{
			c = super.loadClass(className);
		}
		catch (Exception e)
		{
			goon = true;
		}
		if (!goon)
		{
			return c;
		}

		//---Otherwise do the class loading manually since the parent failed
		Class rval				 = null;
		JarEntry je				= null;
		String name				= null;
		String ext				 = null;
		String aClass			  = null;
		ByteArrayOutputStream boas = new ByteArrayOutputStream();
		byte[] buffer			  = new byte[100];
		char slash				 = '/';
		char dot				   = '.';
		int read;

		try
		{
			//---Look for the class in the jar
			JarFile jar   = new JarFile(fFile);
			Enumeration i = jar.entries();
			while (i.hasMoreElements())
			{
				je	 = (JarEntry) i.nextElement();
				name   = je.getName();
				ext	= FileUtil.getFilenameExtension(new File(name));
				aClass = FileUtil.stripExtension(name);
				aClass = aClass.replace(slash, dot);
				if (ext.compareTo(CLASS) == 0 && className.compareTo(aClass)==0)
				{
					//---Found the class, now load it
					InputStream is = jar.getInputStream(je);
					read		   = is.read(buffer);
					while (read >= 0)
					{
						boas.write(buffer, 0, read); 
						read = is.read(buffer);
					}
					is.close();
					buffer = boas.toByteArray();
					rval   = super.defineClass(aClass, buffer, 0, buffer.length);
				}
			}
			jar.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		//---If we could not find the class, let the caller know
		if (rval == null)
		{
			throw new ClassNotFoundException(aClass);
		}
		return rval;
	}
}
