//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	
// $Log: HasNamespace.java,v $
// Revision 1.2  2006/01/23 17:59:50  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
// Commons package restructuring
//
// Revision 1.5  2004/06/03 01:39:39  chostetter_cvs
// More Namespace tweaks
//
// Revision 1.4  2004/05/28 05:58:20  chostetter_cvs
// More Namespace, DataSpace, Descriptor worl
//
// Revision 1.3  2004/05/27 23:29:26  chostetter_cvs
// More Namespace related changes
//
// Revision 1.2  2004/05/17 22:01:10  chostetter_cvs
// Further data-related work
//
// Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
// Initial version
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
 *  The HasNamespace interface specifies the methods that all Objects having 
 *  a Namespace must implement.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface HasNamespace
{
	/**
	 *  Returns true if this Object currently has a Namespace, false otherwise.
	 *
	 *  @return True if this Object currently has a Namespace, false otherwise
	 **/

	public boolean hasNamespace();
	
	
	/**
	 *  Returns the Namespace associated with this Object.
	 *
	 *  @return The Namespace associated with this Object
	 **/

	public Namespace getNamespace();
	

	/**
	 *  Returns true if the Namespace associated with this Object equals the 
	 *  given Namespace, false otherwise.
	 *  
	 *  @param namespace A Namespace
	 *  @return True if the Namespace associated with this Object equals the 
	 *  		given Namespace, false otherwise
	 **/

	public boolean isMember(Namespace namespace);
	

	/**
	 *  Returns the fully-qualified name of the Namespace associated with this 
	 *  Object.
	 *
	 *  @return The fully-qualified name of the Namespace associated with this 
	 *  		Object
	 **/

	public String getNamespaceName();
	

	/**
	 *  Returns the sequence number associated this Object within its current 
	 *  Namespace (if any).
	 *
	 *  @return The sequence number associated this Object
	 **/

	public int getSequenceNumber();
	

	/**
	 *  Returns the sequenced name associated with this Object within its current 
	 *  Namespace (if any).
	 *
	 *  @return The sequenced name associated with this Object
	 **/

	public String getSequencedName();
}
