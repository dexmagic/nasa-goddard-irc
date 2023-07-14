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

/**
 *  A NamespaceMemberInfo is a bean that maintains Namespace-context-specific 
 *  information about a NamespaceMember as a member of a particular Namespace.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface NamespaceMemberInfo extends MemberInfo, HasNamespace, 
	HasNamespaceMemberProperties
{
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
		throws PropertyVetoException;
	
	
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
		throws PropertyVetoException;
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * Namespace of this NamespaceMemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/

	public void addNamespaceListener(PropertyChangeListener listener);

	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the Namespace of this NamespaceMemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/

	public void removeNamespaceListener(PropertyChangeListener listener);

	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the 
	 * Namespace of this NamespaceMemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/

	public void addVetoableNamespaceListener(VetoableChangeListener listener);

	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * the Namespace of this NamespaceMemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/

	public void removeVetoableNamespaceListener(VetoableChangeListener listener);

	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * sequenced name of this NamespaceMemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/

	public void addSequencedNameListener(PropertyChangeListener listener);

	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the sequenced name of this NamespaceMemberInfo.
	 *
	 * @param listener A PropertyChangeListener
	 **/

	public void removeSequencedNameListener(PropertyChangeListener listener);

	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the 
	 * sequenced name of this NamespaceMemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/

	public void addVetoableSequencedNameListener(VetoableChangeListener listener);

	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * the sequenced name of this NamespaceMemberInfo.
	 *
	 * @param listener A VetoableChangeListener
	 **/

	public void removeVetoableSequencedNameListener
		(VetoableChangeListener listener);
}