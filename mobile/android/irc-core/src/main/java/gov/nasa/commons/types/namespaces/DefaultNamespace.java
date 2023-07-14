//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//	$Log: DefaultNamespace.java,v $
//	Revision 1.15  2006/07/27 11:58:42  smaher_cvs
//	Added null pointer avoidance in remove()
//	
//	Revision 1.14  2006/03/14 21:26:11  chostetter_cvs
//	Fixed manager proxy issue that was preventing component browser from updating
//	
//	Revision 1.13  2006/03/14 17:33:33  chostetter_cvs
//	Beefed up toString() method
//	
//	Revision 1.12  2006/03/13 15:45:09  chostetter_cvs
//	Allows for proxy MemberSetBeans
//	
//	Revision 1.11  2006/03/07 23:32:42  chostetter_cvs
//	NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//	
//	Revision 1.10  2006/03/06 13:03:03  chostetter_cvs
//	Really fixed.
//	
//	Revision 1.9  2006/03/06 12:55:25  chostetter_cvs
//	Fixed bug with sequencing already-sequenced names
//	
//	Revision 1.8  2006/03/02 22:20:40  chostetter_cvs
//	Fixed bug in NamespaceMember name sequencing
//	
//	Revision 1.7  2006/02/15 18:08:38  smaher_cvs
//	Made member collections (fMembers, fMembersBySequencedName) transient to avoid serializing other components
//	
//	Revision 1.6  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.5  2005/11/14 20:59:56  tames_cvs
//	Changed iterator method to return a "safe" iterator that is independent from
//	underlying changes to the map. Updated JavaDoc for the methods that
//	return a Collection to note they are not independent from the map.
//	
//	Revision 1.4  2004/07/24 18:55:05  chostetter_cvs
//	Starts tagged count at 2
//	
//	Revision 1.3  2004/07/22 17:06:55  chostetter_cvs
//	Namespace-related changes
//	
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.15  2004/06/28 17:21:16  chostetter_cvs
//	And here, days later, you _still_ can't cast a Set to a Collection. Go figure.
//	
//	Revision 1.14  2004/06/24 19:43:24  chostetter_cvs
//	Turns out you can't cast a Set to a Collection. Grrrr.
//	
//	Revision 1.13  2004/06/05 06:49:20  chostetter_cvs
//	Debugged BasisBundle stuff. It works!
//	
//	Revision 1.12  2004/06/03 01:52:15  chostetter_cvs
//	More Namespace tweaks
//	
//	Revision 1.11  2004/06/03 00:19:59  chostetter_cvs
//	Organized imports
//	
//	Revision 1.10  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.9  2004/06/02 23:00:49  chostetter_cvs
//	Namespace, TimeStamp tweaks
//	
//	Revision 1.8  2004/06/02 22:33:27  chostetter_cvs
//	Namespace revisions
//	
//	Revision 1.7  2004/05/28 05:58:20  chostetter_cvs
//	More Namespace, DataSpace, Descriptor worl
//	
//	Revision 1.6  2004/05/27 23:29:26  chostetter_cvs
//	More Namespace related changes
//	
//	Revision 1.5  2004/05/27 20:03:45  chostetter_cvs
//	More Namespace, DataSpace tweaks
//	
//	Revision 1.4  2004/05/27 19:47:45  chostetter_cvs
//	More Namespace, DataSpace changes
//	
//	Revision 1.3  2004/05/27 16:10:59  tames_cvs
//	CLASS_NAME assignment fix
//	
//	Revision 1.2  2004/05/27 15:57:16  chostetter_cvs
//	Data-related changes
//	
//	Revision 1.1  2004/05/12 21:55:40  chostetter_cvs
//	Further tweaks for new structure, design
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *  A Namespace is a set of Members within which the base name of each Member is 
 *  guaranteed to be unique. A Namespace provides for automatic sequencing of the 
 *  names of Members as they are added, if necessary to generate a unique sequenced 
 *  name of the Member within the Namespace.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date $
 *  @author Carl F. Hostetter
**/

public class DefaultNamespace extends AbstractNamespaceMemberBean 
	implements Namespace, PropertyChangeListener
{
	private static final String CLASS_NAME = DefaultNamespace.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static final Integer S_INTEGER_1 = new Integer(1);
	
	private transient DefaultMemberSetBean fMembers = new DefaultMemberSetBean(this);
	private transient Map fMembersBySequencedName = new HashMap();
	private Map fSequenceCountersByBaseName = new HashMap();
	
	
	/**
	 *  Constructs a new Namespace having the given base name.
	 * 
	 *  @param name The base name of the new Namespace
	 **/

	public DefaultNamespace(String name)
	{
		super(name);
	}
		
	
	/**
	 *  Constructs a new Namespace that acts as a proxy for the given Namespace.
	 *
	 *  @param namespace The Namespace for which the new Namespace will act as a 
	 *  		proxy.
	 **/

	protected DefaultNamespace(Namespace namespace)
	{
		super(namespace.getMemberId());
	}
	
	
	/**
	 *  Returns the lock used to synchronize access to the Set of Members of this 
	 *  Namespace.
	 *  
	 *  @return The lock used to synchronize access to the Set of Members of this 
	 *  		Namespace
	 **/

	protected Object getConfigurationChangeLock()
	{
		return (fMembers.getConfigurationChangeLock());
	}
	
	
	/**
	 * Adds the given MembershipListener as a listener for changes in this Namespace.
	 * 
	 * @param listener A MembershipListener
	 **/

	public final void addMembershipListener(MembershipListener listener)
	{
		fMembers.addMembershipListener(listener);
	}


	/**
	 * Remove the given MembershipListener as a listener for changes in this 
	 * Namespace.
	 *  
	 * @param listener A MembershipListener
	 **/

	public final void removeMembershipListener(MembershipListener listener)
	{
		fMembers.removeMembershipListener(listener);
	}
	
	
	/**
	 *  Removes all Members from this Namespace.
	 *  
	 **/

	public final void clear()
	{
		synchronized (fMembers.getConfigurationChangeLock())
		{
			Iterator members = fMembers.getMembers().iterator();
			
			while (members.hasNext())
			{
				NamespaceMember member = (NamespaceMember) members.next();
				
				remove(member);
			}
			
			fSequenceCountersByBaseName.clear();
		}
	}


	/**
	 *  Returns true if this Namespace is currently empty, false otherwise.
	 *  
	 *  @return	True if this Namespace is currently empty, false otherwise
	 **/

	public final boolean isEmpty()
	{
		return (fMembers.isEmpty());
	}


	/**
	 *  Returns an Iterator over current Set of Members in this Namespace. 
	 *  
	 *  @return	An Iterator over the Set of Members in this Namespace
	 **/

	public final Iterator iterator()
	{
		return (fMembers.iterator());
	}
	
	
	/**
	 *  Returns the current number of Members in this Namespace.
	 *  
	 *  @return	The current number of Members in this Namespace
	 **/

	public final int size()
	{
		return (fMembers.size());
	}


	/**
	 *  Returns the set of Members in this Namespace as an array of 
	 *  Objects.
	 *  
	 *  @return The set of Members in this Namespace as an array of 
	 *  		Objects
	 **/

	public final Object[] toArray()
	{
		return (fMembers.toArray());
	}
	
	
	/**
	 *  Causes this Namespace to receive and process the given PropertyChangeEvent. 
	 *  Typically, this will be a change in the name of a member of this Namespace.
	 *
	 *  @param event A PropertyChangeEvent
	 **/

	public void propertyChange(PropertyChangeEvent event)
	{
		if (event != null)
		{
			Object source = event.getSource();
			Object oldValue = event.getOldValue();
			Object newValue = event.getNewValue();
			
			String propertyName = event.getPropertyName();
			
			synchronized (fMembers.getConfigurationChangeLock())
			{
				if (propertyName.equals
					(NamespaceMemberInfo.SEQUENCED_NAME_PROPERTY))
				{
					String oldSequencedName = (String) oldValue;
					String newSequencedName = (String) newValue;
					
					NamespaceMember Member = (NamespaceMember) 
						fMembersBySequencedName.remove(oldSequencedName);
					
					if ((newSequencedName != null) && (Member != null))
					{
						fMembersBySequencedName.put(newSequencedName, Member);
					}
				}
				else if (propertyName.equals
					(NamespaceMemberInfo.NAMESPACE_PROPERTY))
				{
					NamespaceMemberInfo info = (NamespaceMemberInfo) source;
					
					remove((MemberId) info);
				}
			}
		}
	}
	
	
	/**
	 *  Reserves and returns the next sequence number available for the given 
	 *  (possibly sequenced) name. If this is the first occurrence of the given 
	 *  name in this Namespace, the result will be 1.
	 *  
	 *  @param name The (possibly sequenced) name to be sequenced
	 *  @return The next sequence number available for the given name 
	 **/

	private int sequenceName(String name)
	{
		int result = 1;
		
		String baseName = Namespaces.getBaseName(name);
		
		Integer nameCounter = (Integer) fSequenceCountersByBaseName.get(baseName);
	
		if (nameCounter == null)
		{
			fSequenceCountersByBaseName.put(baseName, S_INTEGER_1);
		}
		else
		{
			result = nameCounter.intValue() + 1;
			
			fSequenceCountersByBaseName.put(baseName, new Integer(result));
		}
		
		return (result);
	}
	
	
	/**
	 *  Adds the given NamespaceMember to the Set of Members of this Namespace. If 
	 *  the base name of the given NamespaceMember already occurs in this Namespace, 
	 *  it will be sequenced as needed to make it unique within this Namespace.
	 *
	 *  @param member The NamespaceMember to be added to this Namespace
	 *  @return True if the given NamespaceMember was actually added to this 
	 *  		Namespace
	 **/

	public boolean add(NamespaceMember member)
	{
		boolean added = false;
		
		if (! fMembers.containsReference(member))
		{
			String name = member.getName();
			
			if (name != null)
			{
				synchronized (fMembers.getConfigurationChangeLock())
				{
					NamespaceMemberId id = member.getNamespaceMemberId();
					
					try
					{
						NamespaceMemberInfo info = (NamespaceMemberInfo) id;
						
						info.setNamespace(this);
						
						int sequenceNumber = sequenceName(name);
						
						info.setSequenceNumber(sequenceNumber);
						
						fMembers.add(member);
						fMembersBySequencedName.put
							(info.getSequencedName(), member);
						
						info.addNamespaceListener(this);
						info.addSequencedNameListener(this);
						
						added = true;
					}
					catch (Exception ex)
					{
						String message = "Unable to add " + 
							member.getFullyQualifiedName() + " to " + 
							getFullyQualifiedName();
				
						if (sLogger.isLoggable(Level.WARNING))
						{
							sLogger.logp(Level.WARNING, CLASS_NAME, 
								"add", message, ex);
						}
						
						throw (new IllegalArgumentException(message));
					}
				}
			}
			else
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Attempted to add unnamed NamespaceMember: " + 
						member + " to " + this;
			
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"add", message);
				}
			}
		}
		else
		{
			added = true;
		}
		
		return (added);
	}


	/**
	 *  Attempts to add the given Member, which must be a NamespaceMember, to the 
	 *  Set of NamespaceMembers of this Namespace. If the current base name of the 
	 *  given NamespaceMember is unique within this Namespace, it is added directly. 
	 *  Otherwise, if the base name of the given NamespaceMember already occurs in 
	 *  this Namespace, its base name will be sequenced as needed to make it 
	 *  unique within this Namespace.
	 *
	 *  @param member The Member to be added to this Namespace
	 *  @return True if the given Member was actually added to this Namespace
	 **/

	public final boolean add(Member member)
	{
		boolean result = false;
		
		if (member instanceof NamespaceMember)
		{
			result = add((NamespaceMember) member);
		}
		
		return (result);
	}
	
	
	/**
	 *  Adds the given Collection of NamespaceMembers to this Namespace.
	 *  
	 *  @param The NamespaceMembers to add to this MemberSet
	 *  @return True if all of the given NamespaceMembers were actually added, 
	 *  		false otherwise
	 **/

	public final boolean addAll(Collection members)
	{
		boolean result = true;
		
		if (members != null)
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
		Member result = (Member) fMembers.get(member.getFullyQualifiedName());
		
		if (result != null)
		{
			remove(member);
		}
		
		add(member);
		
		return (result);
	}
	
	
	/**
	 *  Reserves and returns the next sequence number available for the given 
	 *  base name in this Namespace. If the given base name does not yet occur in 
	 *  this Namespace, the result will be 1. Note that this method is only 
	 *  useful for the self-updating of its sequence number by a NamespaceMember of this 
	 *  Namespace (as otherwise the call will result in that sequence number 
	 *  being unavailable for use by a new NamespaceMember of this Namespace having the 
	 *  same given base name when it is added).
	 *  
	 *  @param name The name of a NamespaceMember of this Namespace 
	 *  @return The next sequence number available for the given base name in this 
	 *  		Namespace
	 **/

	public final int getNextSequenceNumber(String name)
	{
		int result = 0;
		
		if (name != null)
		{
			result = sequenceName(name);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the Set of Members associated with this Object.
	 *  
	 *  @return	The Set of Members associated with this Object
	 **/

	public final Set getMembers()
	{
		return (fMembers.getMembers());
	}
	
	
	/**
	 *  Returns the Set of MemberIds of the Members of this Namespace.
	 *  
	 *  @return The Set of MemberIds of the Members of this Namespace
	 **/

	public final Set getMemberIds()
	{
		return (fMembers.getMemberIds());
	}
	
	
	/**
	 *  Returns the Member of this Namespace that has the given MemberId.
	 *
	 *  @param memberId The MemberId of some Member of this Namespace
	 *  @return The Member of this Namespace that has the given MemberId
	 **/

	public final Member get(MemberId memberId)
	{
		return (fMembers.get(memberId));
	}
	
	
	/**
	 *  Returns the Set of the sequenced (and this Namespace-unique) names of all 
	 *  the Members of this Namespace.
	 *  
	 *  @return	The Set of the sequenced (and this Namespace-unique) names of all 
	 *  		the Members of this Namespace
	 **/

	public final Set getSequencedNames()
	{
		Set results = new HashSet();
		
		synchronized (fMembers.getConfigurationChangeLock())
		{
			Iterator members = fMembers.iterator();
			
			while (members.hasNext())
			{
				NamespaceMember member = (NamespaceMember) members.next();
				
				String fullyQualifiedName = member.getFullyQualifiedName();
				
				if (fullyQualifiedName != null)
				{
					results.add(fullyQualifiedName);
				}
			}
		}
		
		return (results);
	}
	
	
	/**
	 *  Returns the Set of fully-qualified (and thus globally-unique) names of the 
	 *  Members associated with this Object.
	 *  
	 *  @return	The Set of fully-qualified (and thus globally-unique) names of the 
	 *  		Members associated with this Object
	 **/

	public final Set getFullyQualifiedNames()
	{
		return (fMembers.getFullyQualifiedNames());
	}
	
	
	/**
	 *  Returns a Map of the Members of this Namespace keyed on their sequenced 
	 *  (and thus Namespace-unique) names.
	 *  
	 *  @return	A Map of the Members of this Namespace keyed on their 
	 * 		sequenced (and thus Namespace-unique) names
	 **/

	public final Map getMembersBySequencedName()
	{
		return (Collections.unmodifiableMap(fMembersBySequencedName));
	}
	
	
	/**
	 *  Returns a Map of the Members of this Namespace keyed on their 
	 * 	fully-qualified (and thus globally unique) names.
	 *  
	 *  @return	A Map of the Members of this Namespace keyed on their 
	 * 		fully-qualified (and thus globally unique) names
	 **/

	public final Map getMembersByFullyQualifiedName()
	{
		return (fMembers.getMembersByFullyQualifiedName());
	}
	
		
	/**
	 *  Returns a Map of the Members of this Namespace keyed on their 
	 * 	(globally unique) MemberIds.
	 *  
	 *  @return	A Map of the Members of this Namespace keyed on their 
	 * 		(globally unique) MemberIds
	 **/

	public final Map getMembersByMemberId()
	{
		return (fMembers.getMembersByMemberId());
	}
	
		
	/**
	 *  Returns the NamespaceMember of this Namespace that has the given 
	 *  fully-qualified  (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some NamespaceMember of this Namespace
	 *  @return The NamespaceMember of this Namespace that has the given 
	 *  		fully-qualified (and thus globally unique) name
	 **/

	public final Object get(String fullyQualifiedName)
	{
		return (getByFullyQualifiedName(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the MemberId of the NamespaceMember of this Namespace that has 
	 *  the given sequenced (and thus Namespace-unique) name.
	 *  
	 *  @param sequencedName The sequenced name of a NamespaceMember of this 
	 *  		Namespace
	 *  @return The MemberId of the NamespaceMember of this Namespace that has 
	 *  		the given sequenced (and thus Namespace-unique) name
	 **/

	public final MemberId getMemberId(String sequencedName)
	{
		MemberId result = null;
		
		NamespaceMember member = (NamespaceMember) 
			fMembersBySequencedName.get(sequencedName);
		
		if (member != null)
		{
			result = member.getMemberId();
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the Set of Members of this Namespace that have the given base (and 
	 *  thus potentially shared) name.
	 *
	 *  @param baseName The base (and thus potentially shared) name of some 
	 *  		NamespaceMember(s) of this Namespace
	 *  @return The Set of Members of this Namespace that have the given base (and 
	 *  		thus potentially shared) name
	 **/

	public final Set getByBaseName(String baseName)
	{
		return (fMembers.getByBaseName(baseName));
	}
	
	
	/**
	 *  Returns the Set of Members of this Namespace whose base names match the 
	 *  given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the base names of the Members of this Namespace
	 *  @return The Set of Members of this Namespace whose base names match the 
	 *  		given regular expression pattern
	 **/

	public final Set getByBaseNamePatternMatching(String regExPattern)
	{
		return (fMembers.getByBaseNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Returns the NamespaceMember of this Namespace that has the given sequenced 
	 *  (and thus Namespace-unique) name.
	 *  
	 *  @param sequencedName The sequenced (and thus Namespace-unique) name of a 
	 *  		NamespaceMember of this Namespace
	 *  @return The NamespaceMember of this Namespace that has the given sequenced 
	 *  		(and thus Namespace-unique) name
	 **/

	public final NamespaceMember getBySequencedName(String sequencedName)
	{
		return ((NamespaceMember) fMembersBySequencedName.get(sequencedName));
	}
	
	
	/**
	 *  Returns the Set of Members of this Namespace whose sequenced names match 
	 *  the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the sequenced names of the Members of this Namespace
	 *  @return The Set of Members of this Namespace whose sequenced names match 
	 *  		the given regular expression pattern
	 **/

	public final Set getBySequencedNamePatternMatching(String regExPattern)
	{
		Set results = new HashSet();
		
		if (fMembers.size() > 0)
		{
			Pattern pattern = Pattern.compile(regExPattern);
			
			Iterator members = fMembers.iterator();
			
			while (members.hasNext())
			{
				NamespaceMember member = (NamespaceMember) members.next();
				
				String sequencedName = member.getSequencedName();
				
				Matcher matcher = pattern.matcher(sequencedName);
				
				if (matcher.lookingAt())
				{
					results.add(member);
				}
			}
		}
		
		return (results);
	}
	
	
	/**
	 *  Returns the Member of this Namespace that has the given fully-qualified  
	 *  (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Member of this Namespace
	 *  @return The Member of this Namespace that has the given fully-qualified 
	 *  		(and thus globally unique) name
	 **/

	public final Member getByFullyQualifiedName(String fullyQualifiedName)
	{
		return ((NamespaceMember) fMembers.get(fullyQualifiedName));
	}
	
	
	/**
	 *  Returns the Set of Members of this Namespace whose fully-qualified names 
	 *  match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Members of this Namespace
	 *  @return The Set of Members of this Namespace whose fully-qualified names 
	 *  		match the given regular expression pattern
	 **/

	public final Set getByPatternMatching(String regExPattern)
	{
		return (getByFullyQualifiedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Returns the Set of Members of this Namespace whose fully-qualified names 
	 *  match the given regular expression pattern.
	 *
	 *  @param regExPattern The regular expression pattern against which to match 
	 *  		the fully-qualified names of the Members of this Namespace
	 *  @return The Set of Members of this Namespace whose fully-qualified names 
	 *  		match the given regular expression pattern
	 **/

	public final Set getByFullyQualifiedNamePatternMatching(String regExPattern)
	{
		return (fMembers.getByFullyQualifiedNamePatternMatching(regExPattern));
	}
	
	
	/**
	 *  Removes the given Member from the Set of Members of this Namespace. 
	 *
	 *  @param member The Member to be removed from this Namespace
	 *  @return True if the given Member was actually removed from this Namespace
	 **/

	public boolean remove(NamespaceMember member)
	{
		boolean removed = false;
		
		if (fMembers != null)
		{
			synchronized (fMembers.getConfigurationChangeLock())
			{
				if (fMembers.containsReference(member))
				{
					NamespaceMemberInfo info = (NamespaceMemberInfo) member
							.getNamespaceMemberId();

					info.removeNamespaceListener(this);
					info.removeSequencedNameListener(this);

					removed = fMembers.remove(member);

					fMembersBySequencedName.remove(member.getSequencedName());

					if (member.getNamespace() == this)
					{
						try
						{
							info.setNamespace(null);
							info.setSequenceNumber(0);
						} catch (PropertyVetoException ex)
						{
							String message = "Unable to remove "
									+ member.getSequencedName() + " from "
									+ this;

							if (sLogger.isLoggable(Level.WARNING))
							{
								sLogger.logp(Level.WARNING, CLASS_NAME,
										"remove", message, ex);
							}

							throw (new IllegalArgumentException(message));
						}
					}
				}
			}
		}
		
		return (removed);
	}
	
	
	/**
	 *  Removes the given Member from the Set of Members of this Namespace. 
	 *
	 *  @param member The Member to be removed from this Namespace
	 *  @return True if the given Member was actually removed from this Namespace
	 **/

	public final boolean remove(Member member)
	{
		boolean result = false;
		
		if (member instanceof NamespaceMember)
		{
			result = remove((NamespaceMember) member);
		}
		
		return (result);
	}


	/**
	 *  Removes the Member of this Namespace that has the given MemberId.
	 *  
	 *  @param memberId The NMemberInfo of a Member of this Namespace
	 *  @return The Member that was removed (if any)
	 **/

	public final Member remove(MemberId memberId)
	{
		NamespaceMember result = (NamespaceMember) fMembers.get(memberId);
		
		if (result != null)
		{
			remove(result);
		}
		
		return (result);
	}


	/**
	 *  Removes the Member of this Namespace that has the given fully-qualified 
	 *  (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Member of this Namespace
	 *  @return The Member that was removed (if any)
	 **/

	public final Member remove(String fullyQualifiedName)
	{
		return (removeByFullyQualifiedName(fullyQualifiedName));
	}


	/**
	 *  Removes the Member of this Namespace that has the given fully-qualified 
	 *  (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified (and thus globally unique) 
	 *  		name of some Member of this Namespace
	 *  @return The Member that was removed (if any)
	 **/

	public final NamespaceMember removeByFullyQualifiedName
		(String fullyQualifiedName)
	{
		NamespaceMember result = (NamespaceMember) fMembers.get(fullyQualifiedName);
	
		if (result != null)
		{
			remove(result);
		}
	
		return (result);
	}


	/**
	 *  Removes the Member having the given sequenced (and thus Namespace-unique) 
	 *  name from this Namespace.
	 *  
	 *  @param sequencedName The sequenced (and thus Namespace-unique) name of 
	 *  		some Member of this Namespace
	 *  @return The Member that was removed (if any)
	 **/

	public final NamespaceMember removeBySequencedName(String sequencedName)
	{
		NamespaceMember result = (NamespaceMember) 
			fMembersBySequencedName.get(sequencedName);
	
		if (result != null)
		{
			remove(result);
		}
	
		return (result);
	}
	
	
	/**
	 *  Returns true if the given Member occurs among the Set of Members of this 
	 *  Namespace, false otherwise.
	 *  
	 *  @param member A Member
	 *  @return	True if the given Member occurs among the Set of Members of this 
	 *  		Namespace, false otherwise
	 **/

	public final boolean contains(Member member)
	{
		return (fMembers.contains(member));
	}
	
	
	/**
	 *  Returns true if the Member having the given MemberInfo occurs among the Set 
	 *  of Members of this Namespace, false otherwise.
	 *  
	 *  @param memberId The MemberInfo of a Member of this Namespace
	 *  @return	True if the Member having the given MemberId occurs among the Set 
	 *  		of Members of this Namespace, false otherwise
	 **/

	public final boolean contains(MemberId memberId)
	{
		return (fMembers.contains(memberId));
	}


	/**
	 *  Returns true if the given Member occurs among the Set of Members of this 
	 *  Namespace, false otherwise.
	 *  
	 *  @param member A Member
	 *  @return	True if the given Member occurs among the Set of Members of this 
	 *  		Namespace, false otherwise
	 **/

	public final boolean contains(HasName member)
	{
		boolean result = false;
		
		if (member instanceof NamespaceMember)
		{
			result = contains((NamespaceMember) member);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns a String representation of this Namespace.
	 *  
	 *  @return A String representation of this Namespace
	 **/

	public String toString()
	{
		StringBuffer stringRep = 
			new StringBuffer("Namespace: " + getFullyQualifiedName());
		
		synchronized (fMembers.getConfigurationChangeLock())
		{
			int numMembers = fMembers.size();
			
			if (numMembers > 0)
			{
				stringRep.append("\nHas " + numMembers + " members:");
				
				Iterator entries = fMembersBySequencedName.entrySet().iterator();
				
				for (int i = 1; entries.hasNext(); i++)
				{
					Map.Entry entry = (Map.Entry) entries.next();
					
					String sequencedName = (String) entry.getKey();
					
					stringRep.append("\n" + i + ": " + sequencedName);
				}
			}
			else
			{
				stringRep.append("\nHas no members");
			}
		}
		
		return (stringRep.toString());
	}
}
