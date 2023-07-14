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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import gov.nasa.gsfc.commons.app.preferences.PrefKeys;
import gov.nasa.gsfc.commons.system.Sys;

/**
 * A ResourceManager provides access to resources in the runtime environment. 
 * 
 * <p>Some environments restrict access to resources to specific class 
 * loaders. This class hides environment specific details from clients.
 *
 * <p>By default this class uses the <tt>DefaultResourceLocator</tt>
 * class for locating resources. A different <tt>ResourceLocator</tt> 
 * implementation can be set with the 
 * {@link #setDefaultResourceLocator(ResourceLocator)} method.
 *
 * @version	$Date: 2006/04/24 18:34:20 $
 * @author Troy Ames
 *
 * @see ResourceLocator
 * @see DefaultResourceLocator
**/
public class DefaultResourceManager implements ResourceManager
{
	/**
	 * The file separator String to use when concatenating a relative path to
	 * a file name.
	 */
	public static final String FILE_SEPARATOR = "/";

	// Default ClassLoader to look for resources
	private ClassLoader fClassLoader = Sys.getDefaultLoader();

	// Default ResourceLocator
	private ResourceLocator fDefaultLocator = null;

	/**
	 *  Default constructor of a DefaultResourceManager.
	 * 
	 */
	public DefaultResourceManager()
	{
		fDefaultLocator = new DefaultResourceLocator(fClassLoader);
	}

	/**
	 * Returns the resource path separator defined by this ResourceManager.
	 * 
	 * @return The resource path separator defined by this ResourceManager
	 */
	public String getResourcePathSeparator()
	{
		return (FILE_SEPARATOR);
	}
	
	/**
	 * Finds the resource with the given name. A resource is some data
	 * (images, audio, text, etc) that can be accessed by class code in a way
	 * that is independent of the location of the code.<p>
	 *
	 * This implementation uses the ResourceLocator set by the
	 * <code>setDefaultResourceLocator</code> method.
	 *
	 * @param  name resource name
	 * @return a URL for reading the resource, or <code>null</code> if
	 *		 the resource could not be found or the caller doesn't have
	 *		 adequate privileges to get the resource.
	 *
	 * @see #setDefaultResourceLocator(ResourceLocator)
	 */
	public URL getResource(String name)
	{
		URL url = null;
		
		if (name != null && fDefaultLocator != null)
		{
			url = fDefaultLocator.getResource(name);
		}

		return url;
	}

	/**
	 * Finds the resource with the given name by first searching in the
	 * locations specified by the given paths. A resource is some data
	 * (images, audio, text, etc) that can be accessed by class code in a way
	 * that is independent of the location of the code.<p>
	 *
	 * This implementation uses the ResourceLocator set by the
	 * <code>setDefaultResourceLocator</code> method.
	 *
	 * @param  name resource name
	 * @param  paths resource name
	 * @return a URL for reading the resource, or <code>null</code> if
	 *		 the resource could not be found or the caller doesn't have
	 *		 adequate privileges to get the resource.
	 *
	 * @see #setDefaultResourceLocator(ResourceLocator)
	 */
	public URL getResource(String name, Collection paths)
	{
		URL url = null;

		if (name != null && fDefaultLocator != null)
		{
			url = fDefaultLocator.getResource(name, paths);
		}

		return url;
	}

	/**
	 *  Specify a default search path.  These will be used
	 *  as the default for any calls to <code>getResource(String)</code> method.
	 *
	 *  @param paths  the paths to search by default
	 */
	public void setSearchPaths(Collection paths)
	{
		if (fDefaultLocator != null)
		{
			fDefaultLocator.setSearchPaths(paths);
		}
	}

	/**
	 * Specify a default search path using a delimited string of paths. 
	 * The characters in the <code>delim</code>
	 * argument are the delimiters for separating paths. The 
	 * specified path will be used as the default for any calls to 
	 * <code>getResource(String)</code> method.
	 *
	 * @param  paths	a String of one or more paths
	 * @param  delim	the path delimiter(s).
	 */
	public void setSearchPaths(String paths, String delim)
	{
		setSearchPaths(pathStringToList(paths, delim));
	}

	/**
	 * Sets the resource locator that this manager will use to find resources.
	 *
	 * @param  locator resource locator
	 *
	 * @see ResourceLocator
	 */
	public void setDefaultResourceLocator(ResourceLocator locator)
	{
		if (locator != null)
		{
			fDefaultLocator = locator;
		}
	}

	/**
	 * Computes the resource name for a file at the specified absolute path.
	 * Returns null if no resource name could be computed for the absolute path.
	 *
	 * @param	absolutePath	absolute file path to some resource
	 * @return					relative resource name for file, or null
	**/
	public String getResourceNameForPath(String absolutePath)
	{
		String relativePath = null;

		String resourcesPath = 
			FILE_SEPARATOR + System.getProperty(PrefKeys.RESOURCES_DIR) 
			+ FILE_SEPARATOR;

		int relativeStart = absolutePath.indexOf(resourcesPath);
		if (relativeStart >= 0)
		{
			relativePath = absolutePath.substring(relativeStart + 1);
		}

		return relativePath;
	}

	/**
	 * Computes the resource name for a file at the specified absolute path be
	 * determining the longest reslative path. If a resource name cannot be
	 * found within the supplied URL, then the complete URL is returned as
	 * a String.
	 *
	 * Ex:
	 *   Suppose absolutePath is "file:/c:/irc/resources/irc/typemap.xml"
	 *   If c:/irc is in the search path, then "resources/irc/typmap.xml" is
	 *   return. If c:/irc is not in the search path, then
	 *   "file:/c:/irc/resources/irc/typemap.xml" is returned.
	 *
	 * @param	absolutePath	absolute file path to some resource
	 * @return					relative resource name
	**/
	public String getResourceNameForPath(URL absolutePath)
	{
		String origPath = absolutePath.toString();
		String result = origPath;
		StringBuffer path = new StringBuffer(result);
		StringBuffer relativePath = new StringBuffer();

		//try to get a resource for each sub path in the supplied URsLog.
		//the last locatable resource name composed of subpaths moving from
		//right to left will be the most relative for of the resource path
		//For example:
		//   suppose the resouce path consists of c:/irc and there
		//   is a file resources/typemap.xml and sharc/resources/typemap.xml
		//   in the c:/irc directory
		//
		//   if supplied absolutePath is file:/c:/irc/sharc/resources/typemap.xml
		//   there are two possible valid resource names in the subpath of the
		//   absolute path. The first one moving from right to left is:
		//   resources/typemap.xml. However this is an improper relative
		//   resource name because that relative path name will resolved to
		//   file:/c:/irc/resources/typemap.xml by the ResourceManager which is
		//   not what the supplied absolutepath refers to. So, we need to choose
		//   the last valid resource name from right to left, which is
		//   sharc/resources/typemap.xml
		for (int index = path.lastIndexOf("/"); index >= 0;
			 index = path.lastIndexOf("/"))
		{
			relativePath.insert(0, path.substring(index));

			//the test Path must not include the first /
			String testPath = relativePath.substring(1, relativePath.length());

			if (getResource(testPath) != null)
			{
				result = testPath;
			}

			//trim the path so we can search for another segment
			path.delete(index, path.length());
		}

		//if the resulting resource name is not the same as the absolutePath
		//test, to see if it resolves to the absolute path when we use the
		//resource manager to locate it. This is meant to catch the scenario
		//where there are two resources named resources/foo.xml such that one is
		//in the classpath (or search path) located at
		// (1) jar:file:/c:irc.jar!resources/foo but we have specified an
		//absolute path file:/c:/test/irc/resources/foo.xml. Without this
		//test our algorithm would simply return resources/foo.xml, but the
		//resource will select the one in the jar file instead of the file
		//system.
		if (!result.equals(origPath))
		{
			URL testURL = getResource(result);
			if (testURL == null || !testURL.toString().equals(origPath))
			{
				result = origPath;
			}
		}

		return result;
	}	

	/**
	 * Convenience method to convert a delimited string of paths to a
	 * ordered list of paths. The characters in the <code>delim</code>
	 * argument are the delimiters for separating paths. The result can be
	 * passed to the {@link #getResource(String, Collection)} method.
	 *
	 * @param  paths	a String of one or more paths
	 * @param  delim	the path delimiter(s).
	 * @return an ordered <tt>List</tt> of paths.
	 */
	public List pathStringToList(String paths, String delim)
	{
		ArrayList array = new ArrayList();

		if (paths!=null)
		{
			StringTokenizer t = new StringTokenizer(paths, delim);

			while (t.hasMoreTokens())
			{
				array.add(t.nextToken());
			}
		}

		return array;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultResourceManager.java,v $
//  Revision 1.7  2006/04/24 18:34:20  tames_cvs
//  Moved absolute url tests to the ResourceLocator class where they belong.
//
//  Revision 1.6  2005/05/23 19:57:55  tames_cvs
//  Relocated getDefaultLoader() to SysManager.
//
//  Revision 1.5  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/05/28 22:01:32  tames_cvs
//  removed irc specific code
//
//  Revision 1.2  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
//
