//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	
// $Log: HasNamedObjects.java,v $
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

import java.util.Iterator;
import java.util.Set;


/**
 *  The HasNamedObjects interface specifies the methods that all Objects having 
 *  named Objects must implement.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface HasNamedObjects
{
	/**
	 *  Returns true if there are no named Objects currently associated with this 
	 *  Object, false otherwise.
	 *  
	 *  @return True if there are no named Objects currently associated with this 
	 *  		Object, false otherwise
	 **/

	public boolean isEmpty();


	/**
	 *  Returns true if the given named Object occurs among the named Objects 
	 *  associated with this Object, false otherwise.
	 *  
	 *  @param object A named Object
	 *  @return	True if the given named Object occurs among the named Objects 
	 *  		associated with this Object, false otherwise
	 **/

	public boolean contains(HasName object);
	
	
	/**
	 *  Returns the named Object associated with this Object that has the given 
	 *  name. If no such Object exists, the result is null.
	 *
	 *  @param name The name of a named Object associated with this Object
	 *  @return The named Object associated with this Object that has the given 
	 *  		name
	 **/

	public Object get(String name);
	
	
	/**
	 *  Returns the Set of named Objects associated with this Object whose names 
	 *  match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the names of the named Objects associated with this Object
	 *  @return The Set of named Objects associated with this Object whose names 
	 *  		match the given regular expression pattern
	 **/

	public Set getByPatternMatching(String regExPattern);
	
	
	/**
	 *  Returns an Iterator over the Set of NamedObjects currently associated with 
	 *  this Object.
	 *  
	 *  @return An Iterator over the Set of NamedObjects currently associated with 
	 *  		this Object
	 **/

	public Iterator iterator();
	
	
	/**
	 *  Returns the number of NamedObjects currently associated with this Object.
	 *  
	 *  @return The number of NamedObjects currently associated with this Object
	 **/

	public int size();


	/**
	 *  Returns the Set of NamedObjects associated with this Object.
	 *  
	 *  @return	The Set of NamedObjects associated with this Object
	 **/

	public Set getMembers();
	
	
	/**
	 *  Returns the Set of NamedObjects currently associated with this Object as an 
	 *  array of Objects.
	 *  
	 *  @return The Set of NamedObjects currently associated with this Object as an 
	 *  		array of Objects
	 **/

	public Object[] toArray();
}
