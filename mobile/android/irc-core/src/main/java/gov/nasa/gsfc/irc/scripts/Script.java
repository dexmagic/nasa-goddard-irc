//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.scripts;

import java.io.Serializable;
import java.util.Map;

import gov.nasa.gsfc.commons.processing.creation.HasCreator;
import gov.nasa.gsfc.commons.types.namespaces.HasFullyQualifiedName;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.description.HasDescriptor;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;

/**
 * A Script is a reference to an executable script. Typically an implementation 
 * of this interface is a container for arguments
 * and attributes about a script including a pointer or file reference to
 * a script but not necessarily the script itself. 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2006/08/01 19:55:47 $
 * @author		Troy Ames
**/
public interface Script 		
	extends Map, HasCreator, HasDescriptor, HasFullyQualifiedName, Serializable
{
	/**
	 * Sets the Descriptor of this script to the given Descriptor.
	 *
	 * @param	The Descriptor of this script
	**/
	public void setDescriptor(ScriptDescriptor descriptor);
	
	/**
	 *  Set the MemberId of the Creator of this script.
	 *
	 *  @param	creatorId The MemberId of the Creator of this Script
	**/
	public void setCreatorId(MemberId creatorId);
		
	/**
	 *  Sets the base name of this script to the given name. If the given name 
	 *  is null, the name will be set to the NamedObject.DEFAULT_NAME.
	 *
	 *  @param name The desired new base name of this script
	 **/
	
	public void setName(String name);

	/**
	 *  Sets the name qualifier of this script to the given name qualifier.
	 *
	 *  @param nameQualifier The desired new name qualifier of this script
	 **/
	
	public void setNameQualifier(String nameQualifier);
	
	/**
	 * Returns a Map of key/value pairs representing the arguments for this 
	 * script.
	 *
	 * @return	Map of key/value pairs
	**/
	public Map getArguments();

	/**
	 * Adds the Map of arguments to the script.
	 *
	 * @param	arguments Map of key/value pairs
	**/
	public void putArguments(Map arguments);

	/**
	 * Determine whether the script described should be handled
	 * synchronously.
	 *
	 * @return	true if the script should be handled synchronously
	**/
	public boolean isSynchronous();

	/**
	 * Set whether the script should be executed synchronously.
	 *
	 * @param	boolean - Should the script be executed synchronously?
	 */
	public void setSynchronous(boolean enable);
}

//--- Development History  ---------------------------------------------------
//
//	$Log: Script.java,v $
//	Revision 1.5  2006/08/01 19:55:47  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.4  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.3  2005/03/14 20:24:55  tames
//	Added a setSynchronous() method.
//	
//	Revision 1.2  2005/01/07 20:48:27  tames
//	Scripts now like Messages implement the Map interface for arguments.
//	Also removed obsolete dot notation for keys in map.
//	
//	Revision 1.1  2004/08/11 05:42:57  tames
//	Script support
//	
