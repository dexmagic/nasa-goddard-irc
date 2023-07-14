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


/**
 *  A MemberBean is a bean that has a set of constrained 
 *  fully-qualified name properties.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public class AbstractMemberBean extends AbstractMember implements MemberBean
{
	/**
	 *  Constructs a new MemberBean whose MemberId is the given MemberId.
	 *  
	 *  @param memberId The MemberId of the new MemberBean
	 **/

	protected AbstractMemberBean(MemberId memberId)
	{
		super(memberId);
	}
		
	
	/**
	 *  Constructs a new MemberBean having the given fully-qualified name.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the new MemberBean
	 **/

	public AbstractMemberBean(String fullyQualifiedName)
	{
		super(fullyQualifiedName);
	}
		
	
	/**
	 *  Constructs a new MemberBean having the given base name and (fixed) name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new MemberBean
	 *  @param nameQualifier The name qualifier of the new MemberBean
	 **/

	public AbstractMemberBean(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
		
	
	/**
	 *  Constructs a new MemberBean having the given base name, and whose name 
	 *  qualifier is set to the fully-qualified name of the given Object. If the 
	 *  given Object has a fully-qualified name property, the name qualifier of 
	 *  the new MemberBean will be updated as needed to reflect any subsequent 
	 *  changes in the fully-qualified name of the given Object.
	 * 
	 *  @param name The base name of the new MemberBean
	 *  @param nameQualifier The Object whose fully-qualified name will be used 
	 *  		and maintained as the name qualifier of the new MemberBean
	 **/

	public AbstractMemberBean(String name, HasFullyQualifiedName nameQualifier)
	{
		super(name, nameQualifier);
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in 
	 * any Property of this MemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getMemberInfo().addPropertyChangeListener(listener);		
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * any Property of this MemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getMemberInfo().removePropertyChangeListener(listener);		
	}


	/**
	 * Adds the given PropertyChangeListener as a listener for changes in 
	 * the specified Property of this MemberBean.
	 *
	 * @param propertyName The name of a Property of this MemberBean
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addPropertyChangeListener
		(String propertyName, PropertyChangeListener listener)
	{
		getMemberInfo().addPropertyChangeListener(propertyName, listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the specified property of this MemberBean.
	 *
	 * @param propertyName The name of a property of this MemberBean
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removePropertyChangeListener
		(String propertyName, PropertyChangeListener listener)
	{
		getMemberInfo().removePropertyChangeListener(propertyName, listener);
	}
		
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in 
	 * any property of this MemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableChangeListener(VetoableChangeListener listener)
	{
		getMemberInfo().addVetoableChangeListener(listener);
	}
		
	
	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * any property of this MemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableChangeListener(VetoableChangeListener listener)
	{
		getMemberInfo().removeVetoableChangeListener(listener);
	}
		

	/**
	 * Sends the given PropertyChangeEvent to all of the general 
	 * PropertyChangeListeners on this MemberBean. The given event is not sent to 
	 * the listeners if its old and new property values are equal and non-null.
	 * 
	 * @param event A PropertyChangeEvent reflecting the old and new values of some 
	 * 		property of this MemberBean
	 */
	 
	protected void firePropertyChange(PropertyChangeEvent event)
	{
		getMemberInfo().firePropertyChange(event);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated property to all 
	 * of the PropertyChangeListeners on the indicated property. No change is 
	 * reported to the listeners if the given old and new property values are equal 
	 * and non-null.
	 * 
	 * @param propertyName The name of the changed property
	 * @param oldValue The old value of the indicated changed property
	 * @param newValue The new value of the indicated changed property
	 */
	 
	protected void firePropertyChange(String propertyName, Object oldValue, 
		Object newValue)
	{
		getMemberInfo().firePropertyChange(propertyName, oldValue, newValue);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated boolean-valued 
	 * property to all of the PropertyChangeListeners on the indicated property. No 
	 * change is reported to the listeners if the given old and new property values 
	 * are equal.
	 * 
	 * @param propertyName The name of the changed boolean-valued property
	 * @param oldValue The old value of the indicated changed boolean-valued property
	 * @param newValue The new value of the indicated changed boolean-valued property
	 */
	 
	protected void firePropertyChange(String propertyName, boolean oldValue, 
		boolean newValue)
	{
		getMemberInfo().firePropertyChange(propertyName, oldValue, newValue);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated byte-valued 
	 * property to all of the PropertyChangeListeners on the indicated property. No 
	 * change is reported to the listeners if the given old and new property values 
	 * are equal.
	 * 
	 * @param propertyName The name of the changed byte-valued property
	 * @param oldValue The old value of the indicated changed byte-valued property
	 * @param newValue The new value of the indicated changed byte-valued property
	 **/
	
	protected void firePropertyChange(String propertyName, byte oldValue, 
		byte newValue)
	{
		getMemberInfo().firePropertyChange
			(propertyName, new Byte(oldValue), new Byte(newValue));
	}
	
	
	/**
	 * Reports the specified change in the value of the indicated char-valued 
	 * property to all of the PropertyChangeListeners on the indicated property. No 
	 * change is reported to the listeners if the given old and new property values 
	 * are equal.
	 * 
	 * @param propertyName The name of the changed char-valued property
	 * @param oldValue The old value of the indicated changed char-valued property
	 * @param newValue The new value of the indicated changed char-valued property
	 **/
	
	protected void firePropertyChange(String propertyName, char oldValue, 
		char newValue)
	{
		getMemberInfo().firePropertyChange
			(propertyName, new Character(oldValue), new Character(newValue));
	}
	
	
	/**
	 * Reports the specified change in the value of the indicated short-valued 
	 * property to all of the PropertyChangeListeners on the indicated property. No 
	 * change is reported to the listeners if the given old and new property values 
	 * are equal.
	 * 
	 * @param propertyName The name of the changed short-valued property
	 * @param oldValue The old value of the indicated changed short-valued property
	 * @param newValue The new value of the indicated changed short-valued property
	 **/
	
	protected void firePropertyChange(String propertyName, short oldValue, 
		short newValue)
	{
		getMemberInfo().firePropertyChange
			(propertyName, new Short(oldValue), new Short(newValue));
	}
	
	
	/**
	 * Reports the specified change in the value of the indicated int-valued 
	 * property to all of the PropertyChangeListeners on the indicated property. No 
	 * change is reported to the listeners if the given old and new property values 
	 * are equal.
	 * 
	 * @param propertyName The name of the changed int-valued property
	 * @param oldValue The old value of the indicated changed int-valued property
	 * @param newValue The new value of the indicated changed int-valued property
	 */
	 
	protected void firePropertyChange(String propertyName, int oldValue, 
		int newValue)
	{
		getMemberInfo().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	
	/**
	 * Reports the specified change in the value of the indicated long-valued 
	 * property to all of the PropertyChangeListeners on the indicated property. No 
	 * change is reported to the listeners if the given old and new property values 
	 * are equal.
	 * 
	 * @param propertyName The name of the changed long-valued property
	 * @param oldValue The old value of the indicated changed long-valued property
	 * @param newValue The new value of the indicated changed long-valued property
	 **/
	
	protected void firePropertyChange(String propertyName, long oldValue, 
		long newValue)
	{
		getMemberInfo().firePropertyChange
			(propertyName, new Long(oldValue), new Long(newValue));
	}
	
	
	/**
	 * Reports the specified change in the value of the indicated float-valued 
	 * property to all of the PropertyChangeListeners on the indicated property. No 
	 * change is reported to the listeners if the given old and new property values 
	 * are equal.
	 * 
	 * @param propertyName The name of the changed float-valued property
	 * @param oldValue The old value of the indicated changed float-valued property
	 * @param newValue The new value of the indicated changed float-valued property
	 **/
	
	protected void firePropertyChange(String propertyName, float oldValue, 
		float newValue)
	{
		getMemberInfo().firePropertyChange
			(propertyName, new Float(oldValue), new Float(newValue));
	}
	
	
	/**
	 * Reports the specified change in the value of the indicated double-valued 
	 * property to all of the PropertyChangeListeners on the indicated property. No 
	 * change is reported to the listeners if the given old and new property values 
	 * are equal.
	 * 
	 * @param propertyName The name of the changed double-valued property
	 * @param oldValue The old value of the indicated changed double-valued property
	 * @param newValue The new value of the indicated changed double-valued property
	 **/
	
	protected void firePropertyChange(String propertyName, double oldValue, 
		double newValue)
	{
		getMemberInfo().firePropertyChange
			(propertyName, new Double(oldValue), new Double(newValue));
	}
	
	
	/**
	 * Sends the given PropertyChangeEvent to, first, all of the general 
	 * VetoableChangeListeners, and then, if not vetoed, to all of the general 
	 * PropertyChangeListeners on this MemberBean. The given event is not sent 
	 * to the listeners if its old and new property values are equal and non-null. 
	 * If any VetoableChangeListener vetoes the change, then a new event reversing 
	 * the change is sent to all VetoableChangeListeners and a PropertyVetoException 
	 * is thrown. Otherwise, the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this MemberBean.
	 * 
	 * @param event A PropertyChangeEvent reflecting the old and new values of some 
	 * 		constrained property of this MemberBean
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(PropertyChangeEvent event)
		throws PropertyVetoException
	{
		getMemberInfo().fireVetoableChange(event);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated property to, 
	 * first, all of the VetoableChangeListeners, and then, if not vetoed, to all of 
	 * the PropertyChangeListeners on this MemberBean on the indicated 
	 * property. No change is reported to the listeners if the given old and new 
	 * property values are equal and non-null. If any VetoableChangeListener vetoes 
	 * the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this MemberBean.
	 * 
	 * @param propertyName The name of the changed property
	 * @param oldValue The old value of the indicated changed property
	 * @param newValue The new value of the indicated changed property
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(String propertyName, Object oldValue, 
		Object newValue)
		throws PropertyVetoException
	{
		getMemberInfo().fireVetoableChange(propertyName, oldValue, newValue);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated boolean-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this MemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this MemberBean.
	 * 
	 * @param propertyName The name of the changed boolean-valued property
	 * @param oldValue The old value of the indicated changed boolean-valued property
	 * @param newValue The new value of the indicated changed boolean-valued property
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(String propertyName, boolean oldValue, 
		boolean newValue)
		throws PropertyVetoException
	{
		getMemberInfo().fireVetoableChange(propertyName, oldValue, newValue);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated byte-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this MemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this MemberBean.
	 * 
	 * @param propertyName The name of the changed byte-valued property
	 * @param oldValue The old value of the indicated changed byte-valued property
	 * @param newValue The new value of the indicated changed byte-valued property
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(String propertyName, byte oldValue, 
		byte newValue)
		throws PropertyVetoException
	{
		getMemberInfo().fireVetoableChange(propertyName, new Byte(oldValue), 
			new Byte(newValue));
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated char-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this MemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this MemberBean.
	 * 
	 * @param propertyName The name of the changed char-valued property
	 * @param oldValue The old value of the indicated changed char-valued property
	 * @param newValue The new value of the indicated changed char-valued property
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(String propertyName, char oldValue, 
		char newValue)
		throws PropertyVetoException
	{
		getMemberInfo().fireVetoableChange(propertyName, new Character(oldValue), 
			new Character(newValue));
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated short-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this MemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this MemberBean.
	 * 
	 * @param propertyName The name of the changed short-valued property
	 * @param oldValue The old value of the indicated changed short-valued property
	 * @param newValue The new value of the indicated changed short-valued property
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(String propertyName, short oldValue, 
		short newValue)
		throws PropertyVetoException
	{
		getMemberInfo().fireVetoableChange(propertyName, new Short(oldValue), 
			new Short(newValue));
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated int-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this MemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this MemberBean.
	 * 
	 * @param propertyName The name of the changed int-valued property
	 * @param oldValue The old value of the indicated changed int-valued property
	 * @param newValue The new value of the indicated changed int-valued property
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(String propertyName, int oldValue, 
		int newValue)
		throws PropertyVetoException
	{
		getMemberInfo().fireVetoableChange(propertyName, oldValue, newValue);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated long-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this MemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this MemberBean.
	 * 
	 * @param propertyName The name of the changed long-valued property
	 * @param oldValue The old value of the indicated changed long-valued property
	 * @param newValue The new value of the indicated changed long-valued property
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(String propertyName, long oldValue, 
		long newValue)
		throws PropertyVetoException
	{
		getMemberInfo().fireVetoableChange(propertyName, new Long(oldValue), 
			new Long(newValue));
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated float-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this MemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this MemberBean.
	 * 
	 * @param propertyName The name of the changed float-valued property
	 * @param oldValue The old value of the indicated changed float-valued property
	 * @param newValue The new value of the indicated changed float-valued property
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(String propertyName, float oldValue, 
		float newValue)
		throws PropertyVetoException
	{
		getMemberInfo().fireVetoableChange(propertyName, new Float(oldValue), 
			new Float(newValue));
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated double-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this MemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this MemberBean.
	 * 
	 * @param propertyName The name of the changed double-valued property
	 * @param oldValue The old value of the indicated changed double-valued property
	 * @param newValue The new value of the indicated changed double-valued property
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(String propertyName, double oldValue, 
		double newValue)
		throws PropertyVetoException
	{
		getMemberInfo().fireVetoableChange(propertyName, new Double(oldValue), 
			new Double(newValue));
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the name 
	 * of this MemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addNameListener(PropertyChangeListener listener)
	{
		getMemberInfo().addNameListener(listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in the 
	 * name of this MemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removeNameListener(PropertyChangeListener listener)
	{
		getMemberInfo().removeNameListener(listener);
	}
	
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the name 
	 * of this MemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableNameListener(VetoableChangeListener listener)
	{
		getMemberInfo().addVetoableNameListener(listener);
	}


	/**
	 * Removes the given VetoableChangeListener as a listener for changes in the 
	 * name of this MemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableNameListener(VetoableChangeListener listener)
	{
		getMemberInfo().removeVetoableNameListener(listener);
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * fully-qualified name of this MemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addFullyQualifiedNameListener(PropertyChangeListener listener)
	{
		getMemberInfo().addFullyQualifiedNameListener(listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the fully-qualified name of this MemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removeFullyQualifiedNameListener(PropertyChangeListener listener)
	{
		getMemberInfo().removeFullyQualifiedNameListener(listener);
	}
	
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the 
	 * fully-qualified name of this MemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableFullyQualifiedNameListener
		(VetoableChangeListener listener)
	{
		getMemberInfo().addVetoableFullyQualifiedNameListener(listener);
	}


	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * the fully-qualified name of this MemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableFullyQualifiedNameListener
		(VetoableChangeListener listener)
	{
		getMemberInfo().removeVetoableFullyQualifiedNameListener(listener);
	}
}

//--- Development History ----------------------------------------------------
//
// $Log: &
//
