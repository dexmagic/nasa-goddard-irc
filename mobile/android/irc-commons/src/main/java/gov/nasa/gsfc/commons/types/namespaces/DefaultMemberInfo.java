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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  A MemberInfo is a bean that describes and uniquely identifies a Member.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/03/14 16:12:22 $
 *  @author Carl F. Hostetter
**/

public class DefaultMemberInfo extends AbstractNamedBean 
	implements MemberInfo, PropertyChangeListener
{
	private static final String CLASS_NAME = MemberInfo.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private String fName;
	private String fNameQualifier;
	private String fPreviousNameQualifier;
	private transient HasBoundFullyQualifiedNameProperty fNameQualifierObject;
	
	
	/**
	 *  Constructs a new MemberInfo describing a Member having the given base name 
	 *  and (fixed) name qualifier.
	 * 
	 *  @param name The base name of the new MemberInfo
	 *  @param nameQualifier The (fixed) name qualifier of the new MemberInfo
	 **/

	public DefaultMemberInfo(String name, String nameQualifier)
	{
		super(name);
		
		fName = getName();
		fNameQualifier = nameQualifier;
	}
		
	
	/**
	 *  Constructs a new MemberInfo describing a Member having the given base name, 
	 *  and whose name qualifier is set to the fully-qualified name of the given 
	 *  Object. If the given Object has a fully-qualified name property, the name 
	 *  qualifier of this MemberInfo will be updated as needed to reflect any 
	 *  subsequent changes in the fully-qualified name of the given Object.
	 * 
	 *  @param name The base name of the new MemberInfo
	 *  @param nameQualifier The Object whose fully-qualified name will be used 
	 *  		and maintained as the name qualifier of the new MemberInfo
	 **/

	public DefaultMemberInfo(String name, HasFullyQualifiedName nameQualifier)
	{
		this(name, (String) null);
		
		if (nameQualifier != null)
		{
			try
			{
				setNameQualifier(nameQualifier);
			}
			catch (PropertyVetoException ex)
			{
				
			}
		}
	}
	
	
	/**
	 * Constructs a new MemberInfo by parsing the given fully-qualified 
	 * name of some Member.
	 * 
	 * @param fullyQualifiedName The fully-qualified name of some Member
	 * @return A new MemberInfo formed by parsing the given fully-qualified 
	 * 		name of some Member
	 * @param creeatorId The CreatorId of the Creator of the described Member
	 */
	
	protected DefaultMemberInfo(String fullyQualifiedName)
	{
		super(Namespaces.getSequencedName(fullyQualifiedName));
		
		if (fullyQualifiedName != null)
		{
			fName = Namespaces.getSequencedName(fullyQualifiedName);
			fNameQualifier = Namespaces.getNameQualifier(fullyQualifiedName);
			
			try
			{
				updateNames();
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "Could not construct MemberInfo for " + 
						fullyQualifiedName;
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"constructor", message, ex);
				}
			}
		}
	}
		
	
	/**
	 * Returns a clone of this MemberInfo.
	 * 
	 */
	
	protected Object clone()
	{
		DefaultMemberInfo clone = (DefaultMemberInfo) super.clone();
			
		clone.fName = fName;
		clone.fNameQualifier = fNameQualifier;
		clone.fPreviousNameQualifier = fPreviousNameQualifier;
		
		return (clone);
	}
	
	
	/**
	 * Returns true if this MemberInfo equals the given Object, false otherwise. 
	 * Two MemberInfos are considered equal if they have the same Object reference, 
	 * or if their fully-qualified names are identical.
	 * 
	 * @param object An Object
	 * @return True if this MemberInfo equals the given Object, false otherwise
	 */
	
	public boolean equals(Object object)
	{
		boolean result = false;
		
		if (this == object)
		{
			result = true;
		}
		else if (object instanceof MemberInfo)
		{
			MemberInfo target = (MemberInfo) object;
			
			result = getFullyQualifiedName().equals(target.getFullyQualifiedName());
		}
		else if (object instanceof Member)
		{
			MemberInfo target = (MemberInfo) ((Member) object).getMemberId();
			
			result = getFullyQualifiedName().equals(target.getFullyQualifiedName());
		}
		
		return (result);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	
	public int hashCode()
	{
		return (getFullyQualifiedName().hashCode());
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * fully-qualified name of this MemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/

	public void addFullyQualifiedNameListener(PropertyChangeListener listener)
	{
		addPropertyChangeListener
			(HasBoundFullyQualifiedNameProperty.FULLY_QUALIFIED_NAME_PROPERTY, 
				listener);
	}
	

	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the fully-qualified name of this MemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/

	public void removeFullyQualifiedNameListener(PropertyChangeListener listener)
	{
		removePropertyChangeListener
			(HasBoundFullyQualifiedNameProperty.FULLY_QUALIFIED_NAME_PROPERTY, 
				listener);
	}
	

	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the 
	 * fully-qualified name of this MemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/

	public void addVetoableFullyQualifiedNameListener
		(VetoableChangeListener listener)
	{
		addVetoableChangeListener
			(HasBoundFullyQualifiedNameProperty.FULLY_QUALIFIED_NAME_PROPERTY, 
				listener);
	}
	

	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * the fully-qualified name of this MemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/

	public void removeVetoableFullyQualifiedNameListener
		(VetoableChangeListener listener)
	{
		removeVetoableChangeListener
			(HasBoundFullyQualifiedNameProperty.FULLY_QUALIFIED_NAME_PROPERTY, 
				listener);
	}
	
	
	/**
	 *  Causes this MemberInfo to receive and process the given PropertyChangeEvent. 
	 *  Typically, this will be a change in the fully-qualified name of the 
	 *  name qualifier of this MemberInfo
	 *
	 *  @param event A PropertyChangeEvent
	 **/

	public void propertyChange(PropertyChangeEvent event)
	{
		if (event != null)
		{
			Object source = event.getSource();
			
			String propertyName = event.getPropertyName();
			
			if ((source == fNameQualifierObject) && propertyName.equals
				(MemberInfo.FULLY_QUALIFIED_NAME_PROPERTY))
			{
				String nameQualifier = fNameQualifierObject.getFullyQualifiedName();
				
				try
				{
					setNameQualifier(nameQualifier);
				}
				catch (PropertyVetoException ex)
				{

				}
			}
		}
	}
	
	
	/**
	 * Updates the internal info of this MemberInfo after a base name or name 
	 * qualifier change.
	 * 
	 * @throws PropertyVetoException if any of the name changes are vetoed
	 */
	
	private void updateNames()
		throws PropertyVetoException
	{
		String previousName = getName();
		String previousFullyQualifiedName = 
			Namespaces.formFullyQualifiedName(previousName, fPreviousNameQualifier);
		
		if (previousName != fName)
		{
			try
			{
				super.setName(fName);
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to change the base name of " + 
						previousFullyQualifiedName + " to " + fName;
			
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"updateNames", message, ex);
				}
				
				throw (ex);
			}
		}
		
		if (previousFullyQualifiedName != getFullyQualifiedName())
		{
			try
			{
				fireVetoableChange
					(MemberInfo.FULLY_QUALIFIED_NAME_PROPERTY, 
						previousFullyQualifiedName, getFullyQualifiedName());
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = "Unable to change the name of " + 
						previousFullyQualifiedName + " to " + 
							getFullyQualifiedName();
			
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"updateNames", message, ex);
				}
				
				throw (ex);
			}
		}
	}
	
	
	/**
	 * Sets the base name of this MemberInfo to the given name. 
	 *
	 * @param name The new name of this MemberInfo
	 * @throws PropertyVetoException if the attempted name change is vetoed
	 **/

	public void setName(String name)
		throws PropertyVetoException
	{
		fName = name;
		
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
	
	
	/**
	 *  Sets the name qualifier of this MemberInfo to the given name qualifier.  
	 *  
	 *  @param nameQualifier The new name qualifier of this MemberInfo
	 *  @throws PropertyVetoException if the name qualifier could not be set to the 
	 *  		given name qualifier
	 **/

	public void setNameQualifier(String nameQualifier)
		throws PropertyVetoException
	{
		if (nameQualifier != fNameQualifier)
		{
			fPreviousNameQualifier = fNameQualifier;
			fNameQualifier = nameQualifier;
				
			try
			{
				updateNames();
				
				fPreviousNameQualifier = fNameQualifier;
			}
			catch (PropertyVetoException ex)
			{
				fNameQualifier = fPreviousNameQualifier;
				
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to set the name qualifier of " + 
						getFullyQualifiedName() + " to " + nameQualifier;
			
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"setNameQualifier", message, ex);
				}
				
				throw (ex);
			}
		}
	}
	
	
	/**
	 *  Links the name qualifier of this MemberInfo to the fully-qualified name of 
	 *  the given Object. If the given Object has a fully-qualified name property, 
	 *  the name qualifier of this MemberInfo will be updated as needed to reflect 
	 *  any subsequent changes in the fully-qualified name of the given Object. 
	 *
	 *  @param nameQualifier The desired new name qualifier of this MemberInfo
	 *  @throws PropertyVetoException if the attempted name qualifier change is 
	 *  		vetoed
	 **/

	public void setNameQualifier(HasFullyQualifiedName nameQualifier)
		throws PropertyVetoException
	{
		if (nameQualifier != fNameQualifierObject)
		{
			if (fNameQualifierObject != null)
			{
				fNameQualifierObject.removeFullyQualifiedNameListener(this);
			}
			
			if (nameQualifier != null)
			{
				if (nameQualifier instanceof HasBoundFullyQualifiedNameProperty)
				{
					fNameQualifierObject = (HasBoundFullyQualifiedNameProperty) 
						nameQualifier;
					
					fNameQualifierObject.addFullyQualifiedNameListener(this);
				}
				
				setNameQualifier(nameQualifier.getFullyQualifiedName());
			}
			else
			{
				fNameQualifierObject = null;
				
				setNameQualifier((String) null);
			}
		}
	}
	
	
	/**
	 *  Returns the name qualifier of this MemberInfo.
	 *
	 *  @return The name qualifier of this MemberInfo
	 **/

	public String getNameQualifier()
	{
		return (fNameQualifier);
	}
	
	
	/**
	 *  Returns the fully-qualified (i.e., globally unique) name of the Member 
	 *  described by this MemberInfo.
	 *
	 *  @return	The fully-qualified (i.e., globally unique) name of the Member 
	 *  		described by this MemberInfo
	 **/

	public String getFullyQualifiedName()
	{
		return (Namespaces.formFullyQualifiedName(getName(), fNameQualifier));
	}
	
	
	/**
	 *  Returns a String representation of this MemberInfo.
	 *
	 *  @return A String representation of this MemberInfo
	 **/

	public String toString()
	{
		return (getFullyQualifiedName());
	}
}


//--- Development History ----------------------------------------------------
//
//	$Log: DefaultMemberInfo.java,v $
//	Revision 1.3  2006/03/14 16:12:22  chostetter_cvs
//	Removed redundant code
//	
//	Revision 1.2  2006/01/24 20:09:33  chostetter_cvs
//	Fixed MemberId hash issue
//	
//	Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
//	
//	
//
