//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	
// $Log: AbstractNamespaceMember.java,v $
// Revision 1.3  2006/08/03 20:20:55  chostetter_cvs
// Fixed proxy BasisBundle name creation bug
//
// Revision 1.2  2006/03/07 23:32:42  chostetter_cvs
// NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
// Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
// Massive Namespace-related changes
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

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  A NamespaceMember is a Member that can belong to a Namespace, and within which 
 *  it is guaranteed to have a unique and context-specific sequenced name, as well 
 *  as a globaly-unique, fully-qualifed name.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/08/03 20:20:55 $
 *  @author Carl F. Hostetter
**/

public abstract class AbstractNamespaceMember extends AbstractMember 
	implements NamespaceMember
{
	private static final String CLASS_NAME = 
		AbstractNamespaceMemberBean.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	
	/**
	 *  Constructs a new NamespaceMember whose MemberId is the given MemberId. 
	 *  
	 *  @param memberId The MemberId of the new NamespaceMember
	 **/

	protected AbstractNamespaceMember(MemberId memberId)
	{
		super(memberId);
	}
		
	
	/**
	 *  Constructs a new NamespaceMember having the given base name.
	 * 
	 *  @param name The base name of the new NamespaceMember
	 **/

	public AbstractNamespaceMember(String name)
	{
		super(name, (String) null);
	}
		
	
	/**
	 *  Constructs a new NamespaceMember having the given base name and belonging 
	 *  to the given Namespace.
	 * 
	 *  @param name The base name of the new NamespaceMember
	 *  @param namespace The Namespace of the new NamespaceMember
	 **/

	public AbstractNamespaceMember(String name, Namespace namespace)
	{
		super(name, namespace);
		
		if (namespace != null)
		{
			try
			{
				namespace.add(this);
			}
			catch (MembershipException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to add " + name + " to Namespace " + 
						namespace;
						
					sLogger.logp(Level.WARNING, CLASS_NAME, "constructor", 
						message, ex);
				}
			}
		}
	}
	
	
	/**
	 *  Returns the NamespaceMemberInfo of this NamespaceMember.
	 *
	 *  @return The NamespaceMemberInfo of this NamespaceMember
	 **/

	protected DefaultNamespaceMemberInfo getNamespaceMemberInfo()
	{
		return ((DefaultNamespaceMemberInfo) getMemberId());
	}
	
	
	/**
	 *  Returns the NamespaceMemberId of this NamespaceMember.
	 *
	 *  @return The NamespaceMemberId of this NamespaceMember
	 **/

	public NamespaceMemberId getNamespaceMemberId()
	{
		return ((NamespaceMemberId) getMemberId());
	}
	
	
	/**
	 *  Returns true if this NamespaceMember currently has a Namespace, false 
	 *  otherwise.
	 *
	 *  @return True if this NamespaceMember currently has a Namespace, false 
	 *  		otherwise
	 **/

	public boolean hasNamespace()
	{
		return (getNamespaceMemberInfo().hasNamespace());
	}
	
	
	/**
	 *  Returns true if this NamespaceMember is currently a member of the given 
	 *  Namespace, false otherwise.
	 *
	 *  @param namespace A Namespace
	 *  @return True if this NamespaceMember is currently a member of the given 
	 *  	Namespace, false otherwise
	 **/

	public boolean isMember(Namespace namespace)
	{
		return (getNamespaceMemberInfo().isMember(namespace));
	}


	/**
	 *  Returns the Namespace to which this NamespaceMember currently belongs 
	 *  (if any). If this NamespaceMember does not currently belong to a 
	 *  Namespace, the result is null.
	 *
	 *  @return The Namespace to which this NamespaceMember currently belongs 
	 *  		(if any)
	 **/

	public Namespace getNamespace()
	{
		return (getNamespaceMemberInfo().getNamespace());
	}
	
	
	/**
	 *  Returns the fully-qualified name of the Namespace to which this 
	 *  NamespaceMember currently belongs (if any). If this NamespaceMember does not 
	 *  currently belong to a Namespace, the result is null.
	 *
	 *  @return The fully-qualified name of the Namespace to which this 
	 *  		NamespaceMember currently belongs (if any)
	 **/

	public String getNamespaceName()
	{
		return (getNamespaceMemberInfo().getNamespaceName());
	}
	
	
	/**
	 *  Returns the sequence number associated this NamespaceMember.
	 *
	 *  @return The sequence number associated this Object
	 **/

	public int getSequenceNumber()
	{
		return (getNamespaceMemberInfo().getSequenceNumber());
	}
	
	
	/**
	 *  Returns the sequenced (and this Namespace-unique) name of this 
	 *  NamespaceMember. If this NamespaceMember does not currently belong to a 
	 *  Namespace, the result is equivalent to calling <code>getName()</code>.
	 *
	 *  @return The sequenced (and this Namespace-unique) name of this 
	 *  		NamespaceMember
	 **/

	public String getSequencedName()
	{
		return (getNamespaceMemberInfo().getSequencedName());
	}
}
