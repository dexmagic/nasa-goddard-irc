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

package gov.nasa.gsfc.commons.types;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.gsfc.commons.types.namespaces.AbstractNamedObject;


/**
 *  A Type enumerates an extensible set of possible types.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:51 $
 *  @author Carl F. Hostetter
 */

public abstract class Type extends AbstractNamedObject implements Serializable
{
	/**
	 * All known Types, keyed by name.
	**/
	// NB: For initialization sequence, this has to come before any 
	// Type constants.
	private static Map sTypes = new HashMap();
	

	/**
	 * Constructs a new Type having the given name.
	 *
	 * @param name The name of the new Type
	**/
	
	protected Type(String name)
	{
		super(name);
		
		sTypes.put(name, this);
	}
	

	/**
	 *  Returns a String representation of this Type.
	 *
	 *  @return A String representation of this Type
	**/
	
	public String toString()
	{
		return (getName());
	}
	

	/**
	 * Returns the Type corresponding to the given Type name.
	 *
	 * @param The name of the desired  Type
	 * @return The Type that has the given name (if any)
	**/
	
	public static Type forName(String name)
	{
		Type result = (Type) sTypes.get(name);
		
		return (result);
	}
	

	/**
	 * This method supports serialization.
	 *
	**/
	
	public Object readResolve() throws ObjectStreamException
	{
		String typeName = getName();
		
		if (!sTypes.containsKey(typeName))
		{
			sTypes.put(typeName, this);
		}
		
		return (Type) sTypes.get(typeName);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: Type.java,v $
//  Revision 1.5  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.4  2005/01/07 20:18:27  tames
//  Code comment changes only.
//
//  Revision 1.3  2004/07/12 19:04:45  chostetter_cvs
//  Added ability to find BasisBundleId, Components by their fully-qualified name
//
//  Revision 1.2  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.1  2004/05/29 02:40:06  chostetter_cvs
//  Lots of data-related changes
//
