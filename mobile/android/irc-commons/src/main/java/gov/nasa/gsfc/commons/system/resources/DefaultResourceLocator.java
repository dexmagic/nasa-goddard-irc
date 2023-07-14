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

package gov.nasa.gsfc.commons.system.resources;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This encapsulates a common method for clients to find
 * resources in the runtime environment. Some environments restrict access to
 * resources to specific class loaders. This class hides environment specific
 * details from clients.
 *
 *	@version	 $Date: 2006/04/24 18:32:36 $
 *	@author Troy Ames
**/
public class DefaultResourceLocator implements ResourceLocator
{
	private static final String CLASS_NAME = 
		DefaultResourceLocator.class.getName();
	
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String FILE_SEPARATOR = 
		DefaultResourceManager.FILE_SEPARATOR;

	// Default ClassLoader to look for resources
	private ClassLoader fClassLoader = null;
	
	// Default search path
	private Collection fPaths = null;

    /**
     * Constructs a ResourceLocator with the given class loader.
     * 
     * @param classLoader the class loader to use for finding resources.
     */
    public DefaultResourceLocator(ClassLoader classLoader)
    {
		fClassLoader = classLoader;
    }

	/**
	 * Finds the resource with the given name. A resource is some data
	 * (images, audio, text, etc) that can be accessed by class code in a way
	 * that is independent of the location of the code.<p>
	 *
	 * The name of a resource is a "/"-separated path name that identifies
	 * the resource.<p>
	 *
	 * @param  name resource name
	 * @return a URL for reading the resource, or <code>null</code> if
	 *         the resource could not be found or the caller doesn't have
	 *         adequate privileges to get the resource.
	 */
	public URL getResource(String name)
	{
		return getResource(name, fPaths);
	}

	/**
	 * This is a utility method to see if a URL refers to a directory. This will
	 * be called from the multiple file detection code in getResource and
	 * getResourceFromLoader
	 * 
	 * @param testUrl
	 * @return
	 */
	protected boolean urlIsDirectory(URL testUrl)
	{
		try
		{
			String path = testUrl.getFile();
			File f = new File(path);
			return f.isDirectory();
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Finds the resource with the given name by first searching in the
	 * locations specified by the given paths. A resource is some data
	 * (images, audio, text, etc) that can be accessed by class code in a way
	 * that is independent of the location of the code.<p>
	 *
	 * The name of a resource is a "/"-separated path name that identifies
	 * the resource. Paths is a Collection of "/"-separated path names that
	 * indicates where to look for the resource identified by the name parameter.<p>
	 *
	 * This method is implemented to use the <code>getResourceFromLoader</code>
	 * method by prepending each path in order (<path> + "/" + <name>) to the 
	 * name of the resource.
	 * 
	 * For each search path, if the path is a valid absolute path on the local
	 * file system, then this class will attempt to locate the resource
	 * at that absolute path location using 
	 * <code>getResourceFromAbsolutePath</code>. 
	 *
	 * @param  name resource name
	 * @param  paths resource name
	 * @return a URL for reading the resource, or <code>null</code> if
	 *         the resource could not be found or the caller doesn't have
	 *         adequate privileges to get the resource.
	 *
	 * @see #getResource(String)
	 */
	public URL getResource(String name, Collection paths)
	{
		URL url = null;

		if (name == null)
		{
			return null;
		}
		
		// Test if the name is already an absolute URL
		// We need a URI to do the test because the URL class does not
		// provide a test for an absolute URL
		try
		{
			URI uri = new URI(name);
			
			if (uri.isAbsolute())
			{
				// Name is already a string representation of a url.
				
				// Test if it is a local file and if it exists
				try
				{
					File file = new File(uri);
					
					if (file.exists())
					{
						url = file.toURL();
					}
				}
				catch (IllegalArgumentException e)
				{
					// URI is not a local file so pass as is since it is a
					// an absolute uri pointing to a remote resource
					url = new URL(name);
				}
			}
		}
		catch (Exception e)
		{
			// Do nothing since the name is not a valid url yet.
		}

		if (paths != null && url == null)
		{
			Iterator iter = paths.iterator();

			// the below had the stopping condition && url==null ) removed
			// so it could search the ENTIRE set of search paths.. that way
			// error messages
			// can be posted if there are multiple copies of a file..
			while (iter.hasNext() && url == null)
			// while (iter.hasNext())
			{
				String base = iter.next().toString();

				File localPath = new File(base);
				if (localPath.isAbsolute())
				{
					url = getResourceFromAbsolutePath(localPath, name);
				}
				else
				{
					url = getResourceFromLoader(base + FILE_SEPARATOR + name);
				}
			}
		}

		if (url == null)
		{
			url = getResourceFromLoader(name);
		}
		
		return url;
	}
	
	/**
	 * Finds the resource with the given name from the current class loader. A
	 * resource is some data (images, audio, text, etc) that can be accessed by
	 * class code in a way that is independent of the location of the code.
	 * <p>
	 * 
	 * The name of a resource is a "/"-separated path name that identifies the
	 * resource.
	 * <p>
	 * 
	 * @param name resource name
	 * @return a URL for reading the resource, or <code>null</code> if the
	 *         resource could not be found or the caller doesn't have adequate
	 *         privileges to get the resource.
	 */
	protected URL getResourceFromLoader(String name)
	{
		URL url = null;

		if (sLogger.isLoggable(Level.FINE))
		{
			String message = "Looking for resource=" + name;

			sLogger.logp(Level.FINE, CLASS_NAME, "getResourceFromLoader",
					message);
		}

		if (name != null)
		{
			try
			{
				Enumeration urlEnum = fClassLoader.getResources(name);

				if (urlEnum.hasMoreElements())
				{
					url = (URL) urlEnum.nextElement();
				}

				if (urlEnum.hasMoreElements() && !urlIsDirectory(url))
				{
					// there are multiple instances of a file..
					if (sLogger.isLoggable(Level.INFO))
					{
						String message = "Multiple instances of ( " + name
								+ " ) found at: ( " + url + " ) and ( "
								+ urlEnum.nextElement() + " ) using ( " + url
								+ " )";

						sLogger.logp(Level.INFO, CLASS_NAME,
								"getResourceFromLoader", message);
					}
				}

				// The first error here is a debug message because not finding
				// something isn't necessarily an error. We are generally
				// searching a list of paths to find something.
				if (url == null)
				{
					if (sLogger.isLoggable(Level.INFO))
					{
						// String message = "Could not find resource=" + name;
						//						
						// sLogger.logp(Level.INFO, CLASS_NAME,
						// "getResourceFromLoader", message);
					}
				}
				else
				{
					if (sLogger.isLoggable(Level.INFO))
					{
						String message = "Found resource=" + name;

						sLogger.logp(Level.INFO, CLASS_NAME,
								"getResourceFromLoader", message);
					}
				}

			}
			catch (IOException ex)
			{
				// URL still null, so error condition is covered.
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "Could not get resource=" + name;

					sLogger.logp(Level.SEVERE, CLASS_NAME,
							"getResourceFromLoader", message, ex);
				}
			}
		}
		
		return url;
	}
	
	/**
	 * Finds the resource with the given name by appending it to the
	 * given absolute path to a directory on an local disk.
	 * If the file exists and the resulting URL is valid, the URL
	 * is returned.
	 * 
	 * @param	localPath	absolute path to a local directory
	 * @param	name		resource name
	 * @return  a URL for reading the resource, or <code>null</code> if
	 *          the resource could not be found or the caller doesn't have
	 *          adequate privileges to get the resource.
	**/
	protected URL getResourceFromAbsolutePath(File localPath, String name)
	{
		URL url = null;
		
		try
		{
			File f = new File(localPath, name);
			if (f.exists())
			{ 
				url = f.toURL();
			}
		}
		catch (MalformedURLException e)
		{
			// This is not an error, so will just return null
		}
		
		return url;
	}

	/**
	 * Set the search paths that will be used as the default for any calls to
	 * <code>getResource(String)</code> and as a backup for failed searches in
	 * the <code>getResource(String, Collection)</code> method.
	 * 
	 * @param paths the paths to search by default
	 */
	public void setSearchPaths(Collection paths)
	{
		fPaths = paths;
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultResourceLocator.java,v $
//	Revision 1.7  2006/04/24 18:32:36  tames_cvs
//	Fixed getResource methods to correctly handle "file" urls to determine if the
//	file exist. If it does not exist then the method returns null.
//	
//	Revision 1.6  2004/07/19 21:16:24  tames_cvs
//	Undo tab changes
//	
//	Revision 1.4  2004/07/10 08:21:06  tames_cvs
//	Commented out debug println
//	
//	Revision 1.3  2004/06/01 15:45:31  tames_cvs
//	Comment change only
//	
//	Revision 1.2  2004/05/28 22:01:32  tames_cvs
//	removed irc specific code
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//	
