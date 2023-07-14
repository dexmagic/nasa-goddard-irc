//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/types/namespaces/MembershipException.java,v 1.1 2006/01/23 17:59:50 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: MembershipException.java,v $
//  Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
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



/**
 * A MembershipException is the abstract basis of all Namespace-related 
 * Exceptions.
 *
 * @version $Date: 2006/01/23 17:59:50 $
 * @author Carl F. Hostetter
 */

public class MembershipException extends Exception
{
	private Namespace fNamespace;
	

	/**
	 *  Contructs a new MembershipException for the given Namespace.
	 *
	 *  @param namespace The Namespace of the new MembershipException
	 **/
	 
	public MembershipException(Namespace namespace)
	{
		this(namespace, (String) null);
	}
	

	/**
	 *  Contructs a new MembershipException for the given Namespace that wraps 
	 *  the given Exception.
	 *
	 *  @param namespace The Namespace of the new MembershipException
	 *  @param ex An Exception
	 **/
	 
	public MembershipException(Namespace namespace, Exception ex)
	{
		this(namespace, ex.getMessage());
	}
	

	/**
	 *  Contructs a new MembershipException for the given Namespace, and 
	 *  having the given Exception message.
	 *
	 *  @param namespace The Namespace of the new MembershipException
	 *  @param message The Exception message of the new MembershipException
	 **/
	 
	public MembershipException(Namespace namespace, String message)
	{
		super(message);
		
		fNamespace = namespace;
	}
	

	/**
	 *  Returns the Namespace associated with this MembershipException.
	 *
	 *  @return The Namespace associated with this MembershipException
	 */
	 
	public Namespace getNamespace()
	{
		return (fNamespace);
	}
}
