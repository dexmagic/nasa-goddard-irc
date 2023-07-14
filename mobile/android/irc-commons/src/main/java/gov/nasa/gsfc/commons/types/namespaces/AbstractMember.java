//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

import java.beans.PropertyVetoException;


/**
 *  A Member is an Object that has a fully-qualified (i.e., globally unique) name. 
 *  A Member can optionally be linked to another Object having a fully-qualified 
 *  name in such a manner that the fully-qualified name of that Object will become 
 *  the name qualifier of the Member, and any change in the fully-qualified name of 
 *  that Object will be reflected in the name qualifier of the Member.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/08/03 20:20:55 $
 *  @author Carl F. Hostetter
**/

public class AbstractMember extends AbstractNamedObject implements Member
{
	private MemberId fMemberId;
	private DefaultMemberInfo fMemberInfo;

	
	/**
	 *  Constructs a new Member whose MemberId is the given MemberId.
	 *  
	 *  @param memberId The MemberId of the new Member
	 **/

	protected AbstractMember(MemberId memberId)
	{
		fMemberId = memberId;
		fMemberInfo = (DefaultMemberInfo) fMemberId;
	}
		
	
	/**
	 *  Constructs a new Member having the given fully-qualified name.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the new Member
	 **/

	public AbstractMember(String fullyQualifiedName)
	{
		fMemberId = new DefaultMemberId
			(Namespaces.getSequencedName(fullyQualifiedName), 
			Namespaces.getNameQualifier(fullyQualifiedName));
			
		fMemberInfo = (DefaultMemberInfo) fMemberId;
	}
		
	
	/**
	 *  Constructs a new Member having the given base name and (fixed) name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new Member
	 *  @param nameQualifier The name qualifier of the new Member
	 **/

	public AbstractMember(String name, String nameQualifier)
	{
		fMemberId = new DefaultMemberId(name, nameQualifier);
		fMemberInfo = (DefaultMemberInfo) fMemberId;
	}
		
	
	/**
	 *  Constructs a new Member having the given base name, and whose name qualifier 
	 *  is set to the fully-qualified name of the given Object. If the given Object 
	 *  has a fully-qualified name property, the name qualifier of this Member will 
	 *  be updated as needed to reflect any subsequent changes in the fully-
	 *  qualified name of the given Object.
	 * 
	 *  @param name The base name of the new Member
	 *  @param nameQualifier The Object whose fully-qualified name will be used 
	 *  		and maintained as the name qualifier of the new Member
	 **/

	public AbstractMember(String name, HasFullyQualifiedName nameQualifier)
	{
		fMemberId = new DefaultMemberId(name, nameQualifier);
		fMemberInfo = (DefaultMemberInfo) fMemberId;
	}
	
	
	/**
	 * Returns a clone of this Member.
	 *
	 * @return A clone of this Member
	 **/

	protected Object clone()
	{
		AbstractMember result = (AbstractMember) super.clone();
		
//		result.fMemberId = fMemberId;
//		result.fMemberInfo = fMemberInfo;
		
		result.fMemberId = new DefaultMemberId(getName(), getNameQualifier());
		result.fMemberInfo = (DefaultMemberInfo) result.fMemberId;
		
		return (result);
	}
	
	
	/**
	 * Returns true if this Member equals the given Object, false otherwise. 
	 * Two Members (or a Member and a MemberId) are considered equal if they have 
	 * the same Object reference, or if their fully-qualified names are identical.
	 * 
	 * @param object An Object
	 * @return True if this Member equals the given Object, false otherwise
	 */
	
	public boolean equals(Object object)
	{
		boolean result = false;
		
		if (this == object)
		{
			result = true;
		}
		else
		{
			if (object instanceof MemberId)
			{
				result = fMemberId.equals((MemberId) object);
			}
			else if (object instanceof Member)
			{
				result = fMemberId.equals(((Member) object).getMemberId());
			}
		}
		
		return (result);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	
	public int hashCode()
	{
		return (fMemberId.hashCode());
	}
	
	
	/**
	 *  Sets the MemberId of this Member to the given MemmberId.
	 *
	 *  @param memberId The new MemberId of this Member
	 **/

	void setMemberId(MemberId memberId)
	{
		fMemberId = memberId;
		fMemberInfo = (DefaultMemberInfo) fMemberId;
	}
	
	
	/**
	 *  Returns the MemberId of this Member.
	 *
	 *  @return The MemberId of this Member
	 **/

	public MemberId getMemberId()
	{
		return (fMemberId);
	}
	
	
	/**
	 *  Returns the MemberInfo of this Member.
	 *
	 *  @return The MemberInfo of this Member
	 **/

	protected DefaultMemberInfo getMemberInfo()
	{
		return (fMemberInfo);
	}
	
	
	/**
	 *  Sets the base name of this Member to the given name.
	 *
	 *  @param name The new base name of this Member
	 *  @throws PropertyVetoException if the attempted name change is vetoed
	 **/

	public void setName(String name)
		throws PropertyVetoException
	{
		super._setName(name);
		
		fMemberInfo.setName(name);
	}
	
	
	/**
	 *  Returns the base name of this Member.
	 *
	 *  @return The base name of this Member
	 **/

	public String getName()
	{
		return (fMemberInfo.getName());
	}
	
	
	/**
	 *  Sets the name qualifier of this Member to the given (fixed) name qualifier.
	 *
	 *  @param nameQualifier The desired new (fixed) name qualifier of this Member
	 *  @throws PropertyVetoException if the attempted name change is vetoed
	 **/

	protected void setNameQualifier(String nameQualifier)
		throws PropertyVetoException
	{
		fMemberInfo.setNameQualifier(nameQualifier);
	}
	
	
	/**
	 *  Links the name qualifier of this Member to the fully-qualified name of 
	 *  the given Object. If the given Object has a fully-qualified name property, 
	 *  the name qualifier of this Member will be updated as needed to reflect any 
	 *  subsequent changes in the fully-qualified name of the given Object. 
	 *
	 *  @param nameQualifier The desired new name qualifier of this Member
	 *  @throws PropertyVetoException if the attempted name qualifier change is 
	 *  		vetoed
	 **/

	protected void setNameQualifier(HasFullyQualifiedName nameQualifier)
		throws PropertyVetoException
	{
		fMemberInfo.setNameQualifier(nameQualifier);
	}
	
	
	/**
	 *  Returns the name qualifier of this Member.
	 *
	 *  @return	The name qualifier of this Member
	 **/

	public String getNameQualifier()
	{
		return (fMemberInfo.getNameQualifier());
	}
	
	
	/**
	 *  Returns the fully-qualified name of this Member.
	 *
	 *  @return The fully-qualified name of this Member
	 **/

	public String getFullyQualifiedName()
	{
		return (fMemberInfo.getFullyQualifiedName());
	}
	
	
	/**
	 *  Returns a String representation of this Member.
	 *
	 *  @return A String representation of this Member
	 **/

	public String toString()
	{
		return (fMemberInfo.getFullyQualifiedName());
	}
}

//--- Development History ----------------------------------------------------
//
// $Log: AbstractMember.java,v $
// Revision 1.6  2006/08/03 20:20:55  chostetter_cvs
// Fixed proxy BasisBundle name creation bug
//
// Revision 1.5  2006/08/01 19:55:47  chostetter_cvs
// Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
// Revision 1.4  2006/07/18 19:58:59  chostetter_cvs
// Cloned members no long share MemberInfo/MemberId of original, instead have new MemberInfo/MemberId with same base name and no namespace/name qualifier
//
// Revision 1.3  2006/06/07 16:22:21  smaher_cvs
// Undid making some fields transient as they are needed (at least when serializing a BasisBundleDescriptor).
//
// Revision 1.2  2006/06/02 19:19:40  smaher_cvs
// Made some fields transient to avoid circular dependencies when serializing.
//
// Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
// Massive Namespace-related changes
//
//
