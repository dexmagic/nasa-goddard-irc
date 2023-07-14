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


/**
 * A ReferenceManager provides access to an application's resources and 
 * resource files.
 *
 * @version	$Date: 2005/05/23 19:58:41 $
 * @author Troy Ames
 *
 * @see ResourceLocator
 * @see DefaultResourceLocator
**/
public interface ResourceManager extends ResourceLocator
{
	/**
	 * Returns the resource path separator defined by this ResourceManager.
	 * 
	 * @return The resource path separator defined by this ResourceManager
	 */	
	public String getResourcePathSeparator();	
	
	/**
	 * Sets the resource locator that this manager will use to find resources.
	 *
	 * @param  locator resource locator
	 *
	 * @see ResourceLocator
	 */	 
	public void setDefaultResourceLocator(ResourceLocator locator);
	
	/**
	 * Computes the resource name for a file at the specified absolute path.
	 * Returns null if no resource name could be computed for the absolute path.
	 *
	 * @param	absolutePath	absolute file path to some resource
	 * @return					relative resource name for file, or null
	**/	
	public String getResourceNameForPath(String absolutePath);	

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
	public String getResourceNameForPath(URL absolutePath);
	
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
	public void setSearchPaths(String paths, String delim);
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ResourceManager.java,v $
//  Revision 1.6  2005/05/23 19:58:41  tames_cvs
//  Relocated getDefaultLoader() to SysManager.
//
//  Revision 1.5  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.4  2004/05/29 03:07:35  chostetter_cvs
//  Organized imports
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
