//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	
// $Log: HasNamespaces.java,v $
// Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
// Massive Namespace-related changes
//
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

package gov.nasa.gsfc.commons.types.namespaces;

import java.util.Set;


/**
 *  The HasNamespace interface specifies the methods that all Objects having 
 *  Namespaces must implement.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface HasNamespaces
{
	/**
	 *  Returns the Namespace associated with this Object. If this Object has 
	 *  more than one such Namespace, the first such Namespace is returned.
	 *
	 *  @return The (first) Namespace associated with this Object
	 **/

	public Namespace getNamespace();
	
	
	/**
	 *  Returns the Set of Namespaces associated with this Object.
	 *
	 *  @return The Set of Namespaces associated with this Object
	 **/

	public Set getNamespaces();
	
	
	/**
	 *  Returns the number of Namespaces currently associated with this Object.
	 *
	 *  @return The number of Namespaces currently associated with this Object
	 **/

	public int getNumNamespaces();
	
	
	/**
	 *  Returns true if this Objec currently has at least one Namespace, false 
	 *  otherwise.
	 *
	 *  @return True if this Objec currently has at least one Namespace, false 
	 *  		otherwise
	 **/

	public boolean hasNamespace();
	
	
	/**
	 *  Returns true if the given Namespace is among the Set of Namespaces 
	 *  associated with this Object, false otherwise.
	 *
	 *  @return True if the given Namespace is among the Set of Namespaces 
	 *  		associated with this Object, false otherwise
	 **/

	public boolean hasNamespace(Namespace namespace);
}
