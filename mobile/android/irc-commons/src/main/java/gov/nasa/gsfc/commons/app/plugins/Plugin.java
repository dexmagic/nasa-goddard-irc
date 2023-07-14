//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Plugin.java,v $
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

import java.io.File;

/**
 *  Plugin interface. 
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:23 $
 *  @author John Higinbotham	
**/
public interface Plugin
{

	public final static String NAME	= "name";
	public final static String VERSION = "version";
	public final static String AUTHOR  = "author";

//------------------------------------------------------------------

	/**
	 * Get plugin file.
	 *
	 * @return File
	**/
	public File getFile();

	/**
	 * Get plugin name.
	 *
	 * @return String
	**/
	public String getName();

	/**
	 * Get plugin author.
	 *
	 * @return String
	**/
	public String getAuthor();

	/**
	 * Get plugin version.
	 *
	 * @return String
	**/
	public String getVersion();

	/**
	 * Determine if plugin is broken or not. 
	 *
	 * @return boolean
	**/
	public boolean isBroken();

	/**
	 * Install plugin.
	 *
	**/
	public void install();

	/**
	 * Uninstall plugin.
	 *
	**/
	public void uninstall();
}
