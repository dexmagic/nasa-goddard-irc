//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//	$Log: HasFullyQualifiedName.java,v $
//	Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
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


/**
 *  Implemented by named Objects that also have a fully-qualified (and thus  
 *  globally unique) name.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface HasFullyQualifiedName extends HasName
{
	public static final String S_FULLY_QUALIFIED_NAME_SEPARATOR = ".";
	public static final int S_FULLY_QUALIFIED_NAME_SEPARATOR_LENGTH = 
		S_FULLY_QUALIFIED_NAME_SEPARATOR.length();
	
	/**
	 *  Returns the name qualifier of this fully-qualified-named Object.
	 *
	 *  @return	The name qualifier of this fully-qualified-named Object
	 **/

	public String getNameQualifier();
	
	
	/**
	 *  Returns the fully-qualified (i.e., globally unique) name of this  
	 *  Object.
	 *
	 *  @return	The fully-qualified (i.e., globally unique) name of this  
	 *  	Object
	 **/

	public String getFullyQualifiedName();
}
