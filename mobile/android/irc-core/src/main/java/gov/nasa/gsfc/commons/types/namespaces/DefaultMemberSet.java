//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	
// $Log: DefaultMemberSet.java,v $
// Revision 1.5  2006/06/16 19:27:51  smaher_cvs
// Fixed bug in getByBaseName where result was never initialized.
//
// Revision 1.4  2006/03/14 17:33:33  chostetter_cvs
// Beefed up toString() method
//
// Revision 1.3  2006/02/28 16:05:44  chostetter_cvs
// Fixed error in adding MemberIds by fully-qualified name
//
// Revision 1.2  2006/01/24 20:09:33  chostetter_cvs
// Fixed MemberId hash issue
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.gsfc.irc.components.MinimalComponent;


/**
 *  A MemberSet contains an ordered Set of Members.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/06/16 19:27:51 $
 *  @author Carl F. Hostetter
**/

public class DefaultMemberSet implements MemberSet, PropertyChangeListener
{
	private Map fMembersByFullyQualifiedName = new LinkedHashMap();
	private Map fMemberIdsByFullyQualifiedName = new LinkedHashMap();
	
	
	/**
	 * Lock controlling access to the Set of Members in this MemberSet.
	 */
	private final Object fConfigurationChangeLock = 
		new String("ConfigurationChangeLock");


	/**
	 *  Returns the lock used to synchronize access to the Set of Members in this 
	 *  MemberSet.
	 *  
	 *  @return The lock used to synchronize access to the Set of Members in this 
	 *  		MemberSet
	 **/

	protected Object getConfigurationChangeLock()
	{
		return (fConfigurationChangeLock);
	}
	
	
    /**
     * Causes this MemberSet to receive the given PropertyChangeEvent. Typically, 
     * this will indicate a change in the name of a Member of this MemberSet.
     * 
     * @param event A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */

    public void propertyChange(PropertyChangeEvent event)
    {
    		if (event != null)
    		{
			Object source = event.getSource();
			
			if ((source != null) && (source instanceof MemberId) && 
				HasBoundFullyQualifiedNameProperty.
					FULLY_QUALIFIED_NAME_PROPERTY.equals
						(event.getPropertyName()))
			{
				MemberId memberId = (MemberId) source;
				String oldName = (String) event.getOldValue();
				String newName = (String) event.getNewValue();
					
    	    			synchronized (fConfigurationChangeLock)
    	    			{
    	    				Member member = (Member) 
    	    					fMembersByFullyQualifiedName.remove(oldName);
    	    				
    	    				if (member != null)
    	    				{
    	    					fMembersByFullyQualifiedName.put(newName, member);
    	    					
    	    					fMemberIdsByFullyQualifiedName.remove(oldName);
    	    					fMemberIdsByFullyQualifiedName.put(newName, memberId);
    	    				}
    				}
    			}
   		}
    }
    
    
	/**
	 *  Returns true if this MemberSet is currently empty, false otherwise.
	 *  
	 *  @return	True if this MemberSet is currently empty, false otherwise
	 **/

	public final boolean isEmpty()
	{
		return (fMembersByFullyQualifiedName.isEmpty());
	}


	/**
	 *  Adds the given Member to this MemberSet. If a Member of this MemberSet 
	 *  already has the same fully-qualified name as the given Member, the given 
	 *  Member is not added, and the result is false. Otherwise, it is added and 
	 *  the result is true.
	 *  
	 *  @param The Member to add to this MemberSet
	 *  @return True if the given Member was actually added, false otherwise
	 **/

	public boolean add(Member member)
	{
		boolean result = false;
		
		if (member != null)
		{
			synchronized (fConfigurationChangeLock)
			{
				fMembersByFullyQualifiedName.put
					(member.getFullyQualifiedName(), member);
				
				fMemberIdsByFullyQualifiedName.put
					(member.getFullyQualifiedName(), member.getMemberId());
			
				if (member instanceof HasBoundFullyQualifiedNameProperty)
				{
					((HasBoundFullyQualifiedNameProperty) member).
						addFullyQualifiedNameListener(this);
				}
				
				result = true;
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Adds the given Collection of Members to this MemberSet. If a Member of this 
	 *  MemberSet already has the same fully-qualified name as any of the given 
	 *  Members, that given Member is not added.
	 *  
	 *  @param The Members to add to this MemberSet
	 *  @return True if all of the given Members were actually added, false otherwise
	 **/

	public final boolean addAll(Collection members)
	{
		boolean result = true;
		
		if (members != null)
		{
			synchronized (fConfigurationChangeLock)
			{
				Iterator memberIterator = members.iterator();
				
				while (memberIterator.hasNext())
				{
					Member member = (Member) memberIterator.next();
					
					boolean added = add(member);
					
					if (! added)
					{
						result = false;
					}
				}
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Adds the given Member to this MemberSet, replacing the existing Member 
	 *  having the same fully-qualified name (if any). If such Member was replaced, 
	 *  the replaced Member is returned.
	 *  
	 *  @param The Member to add to this MemberSet
	 *  @return The previous Member with the same fully-qualified name (if any) 
	 *  		replaced by the given Member
	 **/

	public final Member replace(Member member)
	{
		Member result = null;
		
		synchronized (fConfigurationChangeLock)
		{
			result = get(member.getMemberId());
			
			if (result != null)
			{
				remove(member);
			}
	
			add(member);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns an Iterator over the Set of Members in this MemberSet. 
	 *  
	 *  @return	An Iterator over the Set of Members in this MemberSet
	 **/

	public final Iterator iterator()
	{
		return (fMembersByFullyQualifiedName.values().iterator());
	}
	
	
	/**
	 *  Returns the current number of Members in this MemberSet.
	 *  
	 *  @return	The current number of Members in this MemberSet
	 **/

	public final int size()
	{
		return (fMembersByFullyQualifiedName.size());
	}


	/**
	 *  Returns the Set of Members in this MemberSet as an array of Objects.
	 *  
	 *  @return The Set of Members in this MemberSet as an array of Objects
	 **/

	public final Object[] toArray()
	{
		return (fMembersByFullyQualifiedName.values().toArray());
	}
	
	
	/**
	 *  Returns the Set of MemberIds of the Members in this MemberSet.
	 *  
	 *  @return	The Set of MemberIds of the Members in this MemberSet
	 **/

	public Set getMemberIds()
	{
		return (fMemberIdsByFullyQualifiedName.keySet());
	}
	
	
	/**
	 *  Returns the Set of fully-qualified (and thus globally-unique) names of 
	 *  the Members in this MemberSet.
	 *  
	 *  @return	The Set of fully-qualified (and thus globally-unique) names of 
	 *  		the Members in this MemberSet
	 **/

	public Set getFullyQualifiedNames()
	{
		return (fMembersByFullyQualifiedName.keySet());
	}
	
	
	/**
	 *  Returns the Set of Members in this MemberSet.
	 *  
	 *  @return	The Set of Members in this MemberSet
	 **/

	public final Set getMembers()
	{
		Set result = new LinkedHashSet(fMembersByFullyQualifiedName.values());
		
		return (result);
	}
	
	
	/**
	 *  Returns a Map of the Members associated with this Object keyed by their 
	 *  MemberIds.
	 *
	 *  @return A Map of the Members associated with this Object keyed by their 
	 *  		MemberIds
	 */

	public final Map getMembersByMemberId()
	{
		Map result = new LinkedHashMap();
		
		Iterator members = fMembersByFullyQualifiedName.values().iterator();
		
		while (members.hasNext())
		{
			Member member = (Member) members.next();
			
			result.put(member.getMemberId(), member);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns a Map of the Members in this MemberSet, keyed by 
	 *  their fully-qualified (and thus globally unique) names.
	 *
	 *  @return A Map of the Members in this MemberSet, keyed by 
	 *  		their fully-qualified (and thus globally unique) names
	 */

	public final Map getMembersByFullyQualifiedName()
	{
		return (Collections.unmodifiableMap(fMembersByFullyQualifiedName));
	}


	/**
	 *  Returns the (at most single) Member in this MemberSet that has the given 
	 *  MemberId. If no such Member exists, the result is null.
	 *
	 *  @param memberId The MemberId of some Member in this MemberSet
	 *  @return The (at most single) Member in this MemberSet that has the given 
	 *  		MemberId
	 **/

	public final Member get(MemberId memberId)
	{
		return ((Member) fMembersByFullyQualifiedName.get
			(memberId.getFullyQualifiedName()));
	}
	
	
	/**
	 *  Returns the (at most single) Member in this MemberSet that has the given 
	 *  fully-qualified (and thus globally unique) name. If there is no such Member, 
	 *  the result is null.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Member in this MemberSet
	 *  @return The Member in this MemberSet that has the given fully-qualified 
	 *  		(and thus globally unique) name
	 */

	public final Object get(String fullyQualifiedName)
	{
		return (fMembersByFullyQualifiedName.get(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the Set of Members in this MemberSet whose fully-qualified names  
	 *  match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Members in this MemberSet
	 *  @return The Set of Members in this MemberSet whose fully-qualified names  
	 *  		match the given regular expression pattern
	 **/

	public final Set getByPatternMatching(String regExPattern)
	{
		return (getByFullyQualifiedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Returns the Set of Members associated with this Object that have the given 
	 *  base (and thus potentially shared) name. If no such Objects exists, the 
	 *  result is null.
	 *
	 *  @param baseName The base (and thus potentially shared) name of some 
	 *  		Member(s) associated with this Object
	 *  @return The Set of Members associated with this Object that have the given 
	 *  		base (and thus potentially shared) name
	 **/

	public final Set getByBaseName(String baseName)
	{
		Set result = new HashSet();
		
		synchronized (fConfigurationChangeLock)
		{
			Iterator members = fMembersByFullyQualifiedName.values().iterator();
			
			while (members.hasNext())
			{
				Member member = (Member) members.next();
				
				String memberName = member.getName();
				
				if (memberName.equals(baseName))
				{
					result.add(member);
				}
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the Set of Members associated with this Object whose base names 
	 *  match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the base names of the Members associated with this Object
	 *  @return The Set of Members associated with this Object whose base names 
	 *  		match the given regular expression pattern
	 **/

	public final Set getByBaseNamePatternMatching(String regExPattern)
	{
		Set result = new HashSet();
		
		synchronized (fConfigurationChangeLock)
		{
			if (fMembersByFullyQualifiedName.size() > 0)
			{
				Pattern pattern = Pattern.compile(regExPattern);
				
				Iterator members = fMembersByFullyQualifiedName.values().iterator();
				
				while (members.hasNext())
				{
					Member member = (Member) members.next();
					
					String baseName = member.getName();
					
					Matcher matcher = pattern.matcher(baseName);
					
					if (matcher.lookingAt())
					{
						result.add(member);
					}
				}
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the Member associated with this Object that has the given 
	 *  fully-qualified (and thus globally unique) name. If no such Object exists, 
	 *  the result is null.
	 *
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Member
	 *  @return The Member associated with this Object that has the given 
	 *  		fully-qualified (and thus globally unique) name
	 **/

	public final Member getByFullyQualifiedName(String fullyQualifiedName)
	{
		return ((Member) fMembersByFullyQualifiedName.get(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the Set of Members associated with this Object whose fully-qualified 
	 *  names match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Members associated with this Object
	 *  @return The Set of Members associated with this Object whose fully-qualified 
	 *  		names match the given regular expression pattern
	 **/

	public final Set getByFullyQualifiedNamePatternMatching(String regExPattern)
	{
		Set result = new HashSet();
		
		synchronized (fConfigurationChangeLock)
		{
			if (fMembersByFullyQualifiedName.size() > 0)
			{
				Pattern pattern = Pattern.compile(regExPattern);
				
				Iterator members = fMembersByFullyQualifiedName.values().iterator();
				
				while (members.hasNext())
				{
					Member member = (Member) members.next();
					
					String fullyQualifiedName = member.getFullyQualifiedName();
					
					Matcher matcher = pattern.matcher(fullyQualifiedName);
					
					if (matcher.lookingAt())
					{
						result.add(member);
					}
				}
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the MemberId of the Member associated with this Object that has 
	 *  the given fully-qualified (and thus globally unique) name. If no such Object 
	 *  exists, the result is null.
	 *
	 *  @param fullyQualifiedName A fully-qualified (and thus globally unique) name 
	 *  		of some Member
	 *  @return The MemberId of the Member associated with this Object that has 
	 *  		the given fully-qualified (and thus globally unique) name
	 **/

	public final MemberId getMemberId(String fullyQualifiedName)
	{
		MemberId result = null;
		
		synchronized (fConfigurationChangeLock)
		{
			Member member = (Member) 
				fMembersByFullyQualifiedName.get(fullyQualifiedName);
			
			if (member != null)
			{
				result = member.getMemberId();
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns true if a Member having the same fully-qualified name as the given 
	 *  Member is in this MemberSet, false otherwise.
	 *  
	 *  @param member A Member
	 *  @return	True if a Member having the same fully-qualified name as the given 
	 *  		Member is in this MemberSet, false otherwise
	 **/

	public final boolean contains(HasName member)
	{
		boolean result = false;
		
		if ((member != null) && (member instanceof HasFullyQualifiedName))
		{
			String name = ((HasFullyQualifiedName) member).getFullyQualifiedName();
			
			result = (fMembersByFullyQualifiedName.get(name) != null);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns true if the given Member Object is in this MemberSet, false 
	 *  otherwise.
	 *  
	 *  @param member A Member
	 *  @return	True if the given Member Object is in this MemberSet, false 
	 *  		otherwise
	 **/

	public final boolean containsReference(Member member)
	{
		boolean result = false;
		
		if (member != null)
		{
			synchronized (fConfigurationChangeLock)
			{
				Member storedMember = (Member) 
					fMembersByFullyQualifiedName.get(member.getFullyQualifiedName());
				
				if (storedMember != null)
				{
					result = (member == storedMember);
				}
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Removes the given Member from this MemberSet.
	 * 
	 *  @param member The Member to be removed
	 *  @return True if the given Member was actually removed
	 */

	public boolean remove(Member member)
	{
		boolean result = false;
		
		synchronized (fConfigurationChangeLock)
		{
			result = containsReference(member);
			
			if (result == true)
			{
				fMembersByFullyQualifiedName.remove(member.getFullyQualifiedName());
				fMemberIdsByFullyQualifiedName.remove(member.getFullyQualifiedName());
				
				if (member instanceof HasBoundFullyQualifiedNameProperty)
				{
					((HasBoundFullyQualifiedNameProperty) member).
						removeFullyQualifiedNameListener(this);
				}
			}
		}
		
		return (result);
	}


	/**
	 *  Removes the Member in this MemberSet that has the given MemberId.
	 * 
	 *  @param memberId The MemberId of the Member to be removed
	 *  @return The Member that was removed
	 */

	public final Member remove(MemberId memberId)
	{
		Member result = null;
			
		synchronized (fConfigurationChangeLock)
		{
			result = get(memberId);
		
			if (result != null)
			{
				remove(result);
			}
		}
		
		return (result);
	}


	/**
	 *  Removes the Member in this MemberSet that has the given fully-qualified 
	 *  (and thus globally unique) name.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the Member to be 
	 *  		removed
	 *  @return The Member that was removed
	 */

	public final Member remove(String fullyQualifiedName)
	{
		Member result = null;
			
		synchronized (fConfigurationChangeLock)
		{
			result = (Member) getByFullyQualifiedName(fullyQualifiedName);
			
			if (result != null)
			{
				remove(result);
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Removes all Members from this MemberSet.
	 *  
	 **/

	public final void clear()
	{
		synchronized (fConfigurationChangeLock)
		{
			Iterator members = getMembers().iterator();
			
			while (members.hasNext())
			{
				Member member = (Member) members.next();
				
				remove(member);
			}
		}
	}
	
	
	/**
	 *	Returns a String representation of this MemberSet. 
	 *  
	 *  @return A String representation of this MemberSet
	 */
	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer();
		
		int numComponents = size();
		
		if (numComponents > 0)
		{
			synchronized (getConfigurationChangeLock())
			{
				Iterator components = iterator();
				
				stringRep.append("Has " + numComponents + " members:");
				
				int i = 0;
			
				while (components.hasNext())
				{
					MinimalComponent component = 
						(MinimalComponent) components.next();
					
					stringRep.append("\n" + ++i + ": " + component);
				}
			}
		}
		else
		{
			stringRep.append("Has no members");
		}
		
		return (stringRep.toString());
	}
}
