//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 580 
//  for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.scripts;

import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;


/**
 * A ScriptFactory is a factory object which is responsible for
 * creating Scripts. Typically the object created is a container for arguments
 * and attributes about a script including a pointer or file reference to
 * a script but not the script itself. 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2006/08/01 19:55:48 $
 * @author		Troy Ames
 */
public interface ScriptFactory
{
	/**
	 * Create a new Script with a null descriptor.
	 *
	 * @return	a new Script
	 */
	public Script createScript();

	/**
	 * Create a new Script from an existing script.
	 *
	 * @param	script	the script to use as a template.
	 * @return	a new Script
	 */
	public Script createScript(Script script);

	/**
	 * Create a new Script from the specified Descriptor.
	 *
	 * @param	descriptor	descriptor from which to create the script.
	 * @return	a new Script
	 */
	public Script createScript(ScriptDescriptor descriptor);

	/**
	 * Create a new Script from the specified Descriptor and Creator Id.
	 *
 	 * @param	descriptor	the descriptor from which to create the script.
	 * @param	creator	the MemberId of the caller
	 * @return	a new Script
	 */
	public Script createScript(ScriptDescriptor descriptor, MemberId creatorId);
	
	/**
	 * Retrieve the script from the script cache.
	 *
	 * @param	descriptor	key from which to locate the script.
	 * @return	a copy of a cached script or null if one does not exist.
	 * @see #storeScript(Script)
	 */
	public Script retrieveScript(ScriptDescriptor descriptor);
	
	/**
	 * Store the script in the script cache.
	 *
	 * @param	script	script to store in cache.
	 * @see #retrieveScript(ScriptDescriptor)
	 */
	public void storeScript(Script script);
}

//--- Development History ----------------------------------------------------
//
//	$Log: ScriptFactory.java,v $
//	Revision 1.4  2006/08/01 19:55:48  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.3  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//	Initial version
//	
//	Revision 1.1  2005/01/07 20:43:36  tames
//	Initial version.
//	
//
