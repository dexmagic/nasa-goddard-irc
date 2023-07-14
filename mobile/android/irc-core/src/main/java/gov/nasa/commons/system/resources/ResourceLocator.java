//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: 
//	 2	IRC	   1.1		 12/31/2002 3:15:01 PMKen Wootton	 Added the
//		  setPaths method.
//	 1	IRC	   1.0		 12/23/2002 5:53:28 PMTroy Ames	   
//	$
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
import java.util.Collection;

/**
 * This encapsulates a common method for clients to find
 * resources in the runtime environment.
 *
 *	@version	$Date: 2004/07/12 14:26:23 $
 *	@author		Troy Ames
**/
public interface ResourceLocator
{
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
	 *		 the resource could not be found or the caller doesn't have
	 *		 adequate privileges to get the resource.
	 */
	public URL getResource(String name);

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
	 * @param  name resource name
	 * @param  paths resource name
	 * @return a URL for reading the resource, or <code>null</code> if
	 *		 the resource could not be found or the caller doesn't have
	 *		 adequate privileges to get the resource.
	 */
	public URL getResource(String name, Collection paths);
	
	/**
	 * Specify a set default search paths.  These will typically be used 
	 * as the default for any calls to <code>getResource(String)</code> method.
	 * 
	 * @param paths  the paths to search by default
	 */
	public void setSearchPaths(Collection paths);
}