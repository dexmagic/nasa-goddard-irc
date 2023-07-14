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

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  A NamespaceMemberInfo is a bean that maintains Namespace-context-specific 
 *  information about a NamespaceMember as a member of a particular Namespace.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/03/02 22:20:40 $
 *  @author Carl F. Hostetter
**/

public class DefaultNamespaceMemberInfo extends DefaultMemberInfo 
	implements NamespaceMemberInfo
{
	private static final String CLASS_NAME = NamespaceMemberInfo.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private String fName;
	private int fSequenceNumber;
	private String fSequencedName;
	private Namespace fNamespace;
	
	
	/**
	 * Constructs a new NamespaceMemberInfo describing a NamespaceMember having the 
	 * given base name and occupying the given Namespace.
	 * 
	 * @param name The base name of the described NamespaceMember
	 * @param Namespace The namespace of the described NamespaceMember
	 */
	
	protected DefaultNamespaceMemberInfo(String name, Namespace namespace)
	{
		super(name, namespace);
		
		fName = getName();
		fSequencedName = fName;
		
		if (namespace != null)
		{
			try
			{
				setNamespace(namespace);
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to set Namespace of " + name +  
						" to " + namespace.getFullyQualifiedName();
			
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"constructor", message, ex);
				}
			}
		}
	}
	
	
	/**
	 * Constructs a new NamespaceMemberInfo by parsing the given fully-qualified 
	 * name of some NamespaceMember.
	 * 
	 * @param fullyQualifiedName The fully-qualified name of some NamespaceMember
	 * @return A new NamespaceMemberInfo formed by parsing the given fully-qualified 
	 * 		name of some NamespaceMember
	 * @param creeatorId The CreatorId of the Creator of the described 
	 * 		NamespaceMember
	 */
	
	protected DefaultNamespaceMemberInfo(String fullyQualifiedName)
	{
		super(Namespaces.getBaseName(fullyQualifiedName), 
			Namespaces.getNameQualifier(fullyQualifiedName));
		
		if (fullyQualifiedName != null)
		{
			String baseName = Namespaces.getBaseName(fullyQualifiedName);
			int sequenceNumber = Namespaces.getSequenceNumber(fullyQualifiedName);
			
			fName = baseName;
			fSequenceNumber = sequenceNumber;
			
			try
			{
				updateNames();
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "Could not construct NamespaceMemberInfo for " + 
						fullyQualifiedName;
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"constructor", message, ex);
				}
			}
		}
	}
	

	/**
	 * Returns a clone of this NamespaceMemberInfo.
	 * 
	 */
	
	protected Object clone()
	{
		DefaultNamespaceMemberInfo clone = (DefaultNamespaceMemberInfo) super.clone();
			
		clone.fName = fName;
		clone.fNamespace = fNamespace;
		clone.fSequencedName = fSequencedName;
		clone.fSequenceNumber = fSequenceNumber;
		
		return (clone);
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * Namespace of this NamespaceMemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addNamespaceListener(PropertyChangeListener listener)
	{
		addPropertyChangeListener(NAMESPACE_PROPERTY, listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the Namespace of this NamespaceMemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removeNamespaceListener(PropertyChangeListener listener)
	{
		removePropertyChangeListener(NAMESPACE_PROPERTY, listener);
	}
	
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the 
	 * Namespace of this NamespaceMemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableNamespaceListener(VetoableChangeListener listener)
	{
		addVetoableChangeListener(NAMESPACE_PROPERTY, listener);
	}


	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * the Namespace of this NamespaceMemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableNamespaceListener(VetoableChangeListener listener)
	{
		removeVetoableChangeListener(NAMESPACE_PROPERTY, listener);
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * sequenced name of this NamespaceMemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addSequencedNameListener(PropertyChangeListener listener)
	{
		addPropertyChangeListener(SEQUENCED_NAME_PROPERTY, listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the sequenced name of this NamespaceMemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removeSequencedNameListener(PropertyChangeListener listener)
	{
		removePropertyChangeListener(SEQUENCED_NAME_PROPERTY, listener);
	}
	
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the 
	 * sequenced name of this NamespaceMemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableSequencedNameListener(VetoableChangeListener listener)
	{
		addVetoableChangeListener(SEQUENCED_NAME_PROPERTY, listener);
	}


	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * the sequenced name of this NamespaceMemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableSequencedNameListener(VetoableChangeListener listener)
	{
		removeVetoableChangeListener(SEQUENCED_NAME_PROPERTY, listener);
	}
	
	
	/**
	 * Updates the internal info of this NamespaceMemberInfo after a name, sequence 
	 * number, or Namespace change.
	 * 
	 * @throws PropertyVetoException if any of the name changes are vetoed
	 */
	
	private void updateNames()
		throws PropertyVetoException
	{
		String previousFullyQualifiedName = getFullyQualifiedName();
		
		String previousName = Namespaces.getBaseName(previousFullyQualifiedName);
		String previousSequencedName = 
			Namespaces.getSequencedName(previousFullyQualifiedName);
		int previousSequenceNumber = 
			Namespaces.getSequenceNumber(previousFullyQualifiedName);
		String previousNameQualifier = getNameQualifier();
		
		if (! fName.equals(previousName) || 	// Base name has changed
				(fSequenceNumber != previousSequenceNumber) || // Sequence number has changed
				((fNamespace != null) && 		// Namespace has changed
					! fNamespace.getFullyQualifiedName().equals
						(previousNameQualifier)))
		{
			try
			{
				if (fNamespace == null)
				{
					fSequenceNumber = 0;
				}
				
				fSequencedName = Namespaces.formSequencedName
					(fName, fSequenceNumber);
				
				fireVetoableChange(SEQUENCED_NAME_PROPERTY, 
					previousSequencedName, fSequencedName);
				
				super.setName(fSequencedName);
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to change the name of " + 
						previousFullyQualifiedName + " to " + fSequencedName;
			
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"updateNames", message, ex);
				}
				
				fName = previousName;
				fSequenceNumber = previousSequenceNumber;
				fSequencedName = previousSequencedName;
				
				throw (ex);
			}
		}
	}
	
	
	/**
	 *  Sets the Namespace associated with this NamespaceMemberInfo to the given 
	 *  Namespace. The NamespaceMember described by this NamespaceMemberInfo is 
	 *  also removed from its current Namespace (if any). 
	 *  
	 *  <p>NOTE that the NamespaceMember specified by this NamespaceMemberInfo is 
	 *  NOT added to the given Namespace by this method: therefore further NOTE 
	 *  that this method should only be called BY the given Namespace, as a result 
	 *  of adding the NamespaceMember specified by this NamespaceMemberInfo to 
	 *  itself. 
	 *
	 *  @param namespace The Namespace to be associated with this 
	 *  		NamespaceMemberInfo
	 *  @throws PropertyVetoException if the Namespace could not be set to the 
	 *  		given Namespace
	 **/

	public void setNamespace(Namespace namespace)
		throws PropertyVetoException
	{
		if (namespace != fNamespace)
		{
			Namespace previousNamespace = fNamespace;
			
			fNamespace = namespace;
			
			try
			{
				if (namespace != null)
				{
					setNameQualifier(namespace.getMemberId());
				}
				else
				{
					setNameQualifier((String) null);
				}
				
				fireVetoableChange(NAMESPACE_PROPERTY, 
					previousNamespace, namespace);
				
				if (previousNamespace != null)
				{
					previousNamespace.remove((MemberId) this);
				}
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to set Namespace of " + 
						getFullyQualifiedName() + " to " + 
						namespace.getFullyQualifiedName();
			
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"setNamespace", message, ex);
				}
				
				fNamespace = previousNamespace;
				
				throw (ex);
			}
		}
	}
	
	
	/**
	 *  Returns true if this NamespaceMemberInfo currently has a Namespace, false 
	 *  otherwise.
	 *
	 *  @return Thrue if this NamespaceMemberInfo currently has a Namespace, false 
	 *  		otherwise
	 **/

	public boolean hasNamespace()
	{
		return (fNamespace != null);
	}
	
	
	/**
	 *  Returns the Namespace associated with this NamespaceMemberInfo.
	 *
	 *  @return The Namespace associated with this NamespaceMemberInfo
	 **/

	public Namespace getNamespace()
	{
		return (fNamespace);
	}
	
	
	/**
	 *  Returns true if the fully-qualified name of the Namespace associated with 
	 *  this NamespaceMemberInfo equals the fully-qualified name of the given 
	 *  Namespace, false otherwise.
	 *  
	 *  @param namespace A Namespace
	 *  @return True if the fully-qualified name of the Namespace associated with 
	 *  		this NamespaceMemberInfo equals the fully-qualified name of the given 
	 *  		Namespace, false otherwise
	 **/

	public boolean isMember(Namespace namespace)
	{
		boolean result = false;
		
		if (namespace != null)
		{
			result = fNamespace.equals(namespace);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the fully-qualified name of the Namespace associated with this 
	 *  NamespaceMemberInfo.
	 *
	 *  @return The fully-qualified name of the Namespace associated with this 
	 *  		NamespaceMemberInfo
	 **/

	public String getNamespaceName()
	{
		String result = null;
		
		if (fNamespace != null)
		{
			result = fNamespace.getFullyQualifiedName();
		}
		
		return (result);
	}
	
	
	/**
	 *  Sets the base name of this NamespaceMemberInfo to the given name. If the 
	 *  name is not unique in the Namespace associated with this 
	 *  NamespaceMemberInfo, a unique sequenced name will be generated for it. If 
	 *  the attempted name change is vetoed by some vetoable change listener on this 
	 *  NamespaceMemberInfo (if any), a PropertyVetoException is thrown and no 
	 *  change is made. Otherwise, the change is reported to any name property 
	 *  change listeners.
	 *
	 *  @param name The desired new base name of this NamespaceMemberInfo
	 *  @return The new, Namespace-unique (possibly sequenced) form of the name of 
	 *  		this NamespaceMemberInfo
	 *  @throws PropertyVetoException if the attempted name change is vetoed
	 **/

	public void setName(String name)
		throws PropertyVetoException
	{
		if (name != null && ! name.equals(fName))
		{
			fName = name;
			fSequencedName = name;
			
			try
			{
				updateNames();
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to change the base name of " + 
						getFullyQualifiedName() + " to " + name;
			
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"setName", message, ex);
				}
				
				throw (ex);
			}
		}
	}
	
	
	/**
	 *  Sets the sequence number associated with this NamespaceMemberInfo to the 
	 *  given number.
	 *
	 *  <p>NOTE that the given sequence number MUST be available for the current 
	 *  base name of the NamespaceMember described by this NamespaceMemberInfo, in 
	 *  the Namespace to which the NamespaceMember described by this 
	 *  NamespaceMemberInfo belongs; therefore further NOTE that this method should 
	 *  only be called either BY this NamespaceMemberInfo or BY that Namespace. 
	 *
	 *  @param sequenceNumber The new sequence number of the NamespaceMember 
	 *  		described by this NamespaceMemberInfo
	 *  @throws PropertyVetoException if the attempted sequence number change is 
	 *  		vetoed
	 **/

	public void setSequenceNumber(int sequenceNumber)
		throws PropertyVetoException
	{
		if (sequenceNumber != fSequenceNumber)
		{
			int previousSequenceNumber = fSequenceNumber;
			fSequenceNumber = sequenceNumber;
			
			try
			{
				updateNames();
			}
			catch (PropertyVetoException ex)
			{
				String message = "Unable to set sequence number of " + 
					getFullyQualifiedName() + " to " + sequenceNumber;
			
				if (sLogger.isLoggable(Level.WARNING))
				{
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"setSequenceNumber", message, ex);
				}
				
				fSequenceNumber = previousSequenceNumber;
				
				throw (ex);
			}
		}
	}
	
	
	/**
	 *  Returns the sequence number associated this NamespaceMemberInfo.
	 *
	 *  @return The sequence number associated this NamespaceMemberInfo
	 **/

	public int getSequenceNumber()
	{
		return (fSequenceNumber);
	}
	
	
	/**
	 *  Returns the sequenced name associated with this NamespaceMemberInfo.
	 *
	 *  @return The sequenced name associated with this NamespaceMemberInfo
	 **/

	public String getSequencedName()
	{
		return (fSequencedName);
	}
}


//--- Development History ----------------------------------------------------
//
//	$Log: DefaultNamespaceMemberInfo.java,v $
//	Revision 1.3  2006/03/02 22:20:40  chostetter_cvs
//	Fixed bug in NamespaceMember name sequencing
//	
//	Revision 1.2  2006/02/14 20:02:32  chostetter_cvs
//	Fixed possible null pointer exception in setNamespace
//	
//	Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.4  2005/12/06 19:55:10  tames_cvs
//	Added equals and hashCode methods.
//	
//	Revision 1.3  2005/05/02 18:44:51  chostetter_cvs
//	Removed equals() method from (by definition) unique ID objects (since they can use == reliably)
//	
//	Revision 1.2  2004/07/12 19:04:45  chostetter_cvs
//	Added ability to find BasisBundleId, Components by their fully-qualified name
//	
//	Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.1  2004/06/02 22:33:27  chostetter_cvs
//	Namespace revisions
//	
//
