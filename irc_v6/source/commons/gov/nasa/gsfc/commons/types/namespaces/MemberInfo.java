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
import java.io.Serializable;


/**
 *  A MemberInfo is a bean that maintains information about a Member.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface MemberInfo extends Serializable, NamedBean, HasFullyQualifiedName, 
	HasMemberProperties
{
	/**
	 *  Sets the name qualifier of this MemberInfo to the given name qualifier.  
	 *  
	 *  @param nameQualifier The new name qualifier of this MemberInfo
	 *  @throws PropertyVetoException if the name qualifier could not be set to the 
	 *  		given name qualifier
	 **/

	public void setNameQualifier(String nameQualifier)
		throws PropertyVetoException;
	
	
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
		throws PropertyVetoException;
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * fully-qualified name of this MemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/

	public void addFullyQualifiedNameListener(PropertyChangeListener listener);

	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the fully-qualified name of this MemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/

	public void removeFullyQualifiedNameListener(PropertyChangeListener listener);

	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the 
	 * fully-qualified name of this MemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/

	public void addVetoableFullyQualifiedNameListener
		(VetoableChangeListener listener);

	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * the fully-qualified name of this MemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/

	public void removeVetoableFullyQualifiedNameListener
		(VetoableChangeListener listener);
}