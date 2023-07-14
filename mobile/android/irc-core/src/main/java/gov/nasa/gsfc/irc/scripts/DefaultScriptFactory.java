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

import java.util.Iterator;

import gov.nasa.gsfc.commons.system.storage.PersistentObjectStore;
import gov.nasa.gsfc.commons.types.namespaces.MemberId;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;


/**
 * This is the default ScriptFactory for IRC.  A ScriptFactory is a
 * factory object which is responsible for creating and caching Script 
 * objects. Typically the object created is a container for arguments
 * and attributes about a script including a pointer or file reference to
 * a script but not the script itself. 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2006/08/01 19:55:48 $
 * @author		Troy Ames
 */
public class DefaultScriptFactory implements ScriptFactory
{
	PersistentObjectStore fScriptCache = null;

	/**
	 * Default constructor
	 */
	public DefaultScriptFactory()
	{
		fScriptCache = Irc.getPersistentStore();
	}

	/**
	 * Create a new Script.
	 *
	 * @return	a new Script
	 */
	public Script createScript()
	{
		return createScript(null, null);
	}

	/**
	 * Create a new Script from an existing script.
	 *
	 * @param	script	the script to use as a template.
	 * @return	a new Script
	 */
	public Script createScript(Script script)
	{
		Script scriptClone = null;
		
		if (script != null)
		{
			scriptClone = 
				createScript((ScriptDescriptor) script.getDescriptor());
			
			Iterator keys = script.keySet().iterator();
			
			while (keys.hasNext())
			{
				Object key = keys.next();
				scriptClone.put(key, script.get(key));
			}
			
			scriptClone.setName(script.getName());
			scriptClone.setNameQualifier(script.getNameQualifier());
			scriptClone.setCreatorId(script.getCreatorId());
		}
		
		return scriptClone;
	}

	/**
	 * Create a new Script from the specified Descriptor.
	 *
	 * @param	descriptor	the descriptor from which to create the script.
	 * @return	a new Script
	 */
	public Script createScript(ScriptDescriptor descriptor)
	{
		return createScript(descriptor, null);
	}

	/**
	 * Create a new Script from the specified Descriptor and Creator Id.
	 *
	 * @param	descriptor	the descriptor from which to create the script.
	 * @param	creator	The MemberId of the caller
	 * @return	a new Script
	 */
	public Script createScript(ScriptDescriptor descriptor, MemberId creatorId)
	{
		Script script = new DefaultScript(descriptor, creatorId);
		
		return script;
	}
	
	/**
	 * Retrieve the script from the script cache.
	 *
	 * @param	descriptor	key from which to locate the script.
	 * @return	a copy of a cached script or null if one does not exist.
	 * @see #storeScript(Script)
	 */
	public Script retrieveScript(ScriptDescriptor descriptor)
	{
		Script script = null;
		
		if (fScriptCache != null)
		{
			script = 
				(Script) fScriptCache.retrieve(descriptor.getFullyQualifiedName());
			
			if (script != null)
			{
				script.setDescriptor(descriptor);
			}
		}
		
		return script;
	}
	
	/**
	 * Store the script in the script cache.
	 *
	 * @param	script	script to store in cache.
	 * @see #retrieveScript(ScriptDescriptor)
	 */
	public void storeScript(Script script)
	{
		if (fScriptCache != null)
		{
			fScriptCache.store(script.getFullyQualifiedName(), script);
		}
	}
}

//--- Development History ----------------------------------------------------
//
//	$Log: DefaultScriptFactory.java,v $
//	Revision 1.5  2006/08/01 19:55:48  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.4  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.3  2005/02/02 19:32:17  tames_cvs
//	Renamed IrcScript class to DefaultScript for naming
//	consistency.
//	
//	Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//	Initial version
//	
//	Revision 1.1  2005/01/07 20:43:36  tames
//	Initial version.
//	
//	
