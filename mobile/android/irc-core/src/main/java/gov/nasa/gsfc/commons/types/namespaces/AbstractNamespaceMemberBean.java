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
 *  A NamespaceMemberBean is a bean that has a constrained name property.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/03/07 23:32:42 $
 *  @author Carl F. Hostetter
**/

public abstract class AbstractNamespaceMemberBean extends AbstractNamespaceMember 
	implements NamespaceMemberBean
{
	/**
	 *  Constructs a new NamespaceMemberBean whose MemberId is the given MemberId.
	 *  
	 *  @param memberId The MemberId of the new NamespaceMemberBean
	 **/

	protected AbstractNamespaceMemberBean(MemberId memberId)
	{
		super(memberId);
	}
		
	
	/**
	 *  Constructs a new NamespaceMemberBean having the given base name.
	 * 
	 *  @param name The base name of the new NamespaceMemberBean
	 **/

	public AbstractNamespaceMemberBean(String name)
	{
		super(name);
	}
		
	
	/**
	 *  Constructs a new NamespaceMemberBean having the given base name and 
	 *  belonging to the given Namespace.
	 * 
	 *  @param name The base name of the new NamespaceMemberBean
	 *  @param namespace The Namespace of the new NamespaceMemberBean
	 **/

	public AbstractNamespaceMemberBean(String name, Namespace namespace)
	{
		super(name, namespace);
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in 
	 * any Property of this NamespaceMemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{

	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * any Property of this NamespaceMemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{

	}


	/**
	 * Adds the given PropertyChangeListener as a listener for changes in 
	 * the specified Property of this NamespaceMemberBean.
	 *
	 * @param propertyName The name of a Property of this NamespaceMemberBean
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addPropertyChangeListener
		(String propertyName, PropertyChangeListener listener)
	{
		getNamespaceMemberInfo().addPropertyChangeListener(propertyName, listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the specified property of this NamespaceMemberBean.
	 *
	 * @param propertyName The name of a property of this NamespaceMemberBean
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removePropertyChangeListener
		(String propertyName, PropertyChangeListener listener)
	{
	}
		
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in 
	 * any property of this NamespaceMemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableChangeListener(VetoableChangeListener listener)
	{
		getNamespaceMemberInfo().addVetoableChangeListener(listener);
	}
		
	
	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * any property of this NamespaceMemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableChangeListener(VetoableChangeListener listener)
	{
		getNamespaceMemberInfo().removeVetoableChangeListener(listener);
	}
		

	/**
	 * Sends the given PropertyChangeEvent to all of the general 
	 * PropertyChangeListeners on this NamespaceMemberBean. The given event is not sent to 
	 * the listeners if its old and new property values are equal and non-null.
	 * 
	 * @param event A PropertyChangeEvent reflecting the old and new values of some 
	 * 		property of this NamespaceMemberBean
	 */
	 
	protected void firePropertyChange(PropertyChangeEvent event)
	{
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

	}
	
	
	/**
	 * Sends the given PropertyChangeEvent to, first, all of the general 
	 * VetoableChangeListeners, and then, if not vetoed, to all of the general 
	 * PropertyChangeListeners on this NamespaceMemberBean. The given event is not sent 
	 * to the listeners if its old and new property values are equal and non-null. 
	 * If any VetoableChangeListener vetoes the change, then a new event reversing 
	 * the change is sent to all VetoableChangeListeners and a PropertyVetoException 
	 * is thrown. Otherwise, the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this NamespaceMemberBean.
	 * 
	 * @param event A PropertyChangeEvent reflecting the old and new values of some 
	 * 		constrained property of this NamespaceMemberBean
	 * @throws PropertyVetoException if the indicated property change is vetoed by 
	 * 		some VetoableChangeListener
	 */
	 
	protected void fireVetoableChange(PropertyChangeEvent event)
		throws PropertyVetoException
	{
		getNamespaceMemberInfo().fireVetoableChange(event);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated property to, 
	 * first, all of the VetoableChangeListeners, and then, if not vetoed, to all of 
	 * the PropertyChangeListeners on this NamespaceMemberBean on the indicated 
	 * property. No change is reported to the listeners if the given old and new 
	 * property values are equal and non-null. If any VetoableChangeListener vetoes 
	 * the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this NamespaceMemberBean.
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
		getNamespaceMemberInfo().fireVetoableChange(propertyName, oldValue, newValue);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated boolean-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this NamespaceMemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this NamespaceMemberBean.
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
		getNamespaceMemberInfo().fireVetoableChange(propertyName, oldValue, newValue);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated byte-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this NamespaceMemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this NamespaceMemberBean.
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
		getNamespaceMemberInfo().fireVetoableChange(propertyName, new Byte(oldValue), 
			new Byte(newValue));
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated char-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this NamespaceMemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this NamespaceMemberBean.
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
		getNamespaceMemberInfo().fireVetoableChange(propertyName, new Character(oldValue), 
			new Character(newValue));
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated short-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this NamespaceMemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this NamespaceMemberBean.
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
		getNamespaceMemberInfo().fireVetoableChange(propertyName, new Short(oldValue), 
			new Short(newValue));
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated int-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this NamespaceMemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this NamespaceMemberBean.
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
		getNamespaceMemberInfo().fireVetoableChange(propertyName, oldValue, newValue);
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated long-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this NamespaceMemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this NamespaceMemberBean.
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
		getNamespaceMemberInfo().fireVetoableChange(propertyName, new Long(oldValue), 
			new Long(newValue));
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated float-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this NamespaceMemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this NamespaceMemberBean.
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
		getNamespaceMemberInfo().fireVetoableChange(propertyName, new Float(oldValue), 
			new Float(newValue));
	}
		
	
	/**
	 * Reports the specified change in the value of the indicated double-valued 
	 * property to, first, all of the VetoableChangeListeners, and then, if not 
	 * vetoed, to all of the PropertyChangeListeners on this NamespaceMemberBean on the 
	 * indicated property. No change is reported to the listeners if the given old 
	 * and new property values are equal and non-null. If any VetoableChangeListener 
	 * vetoes the change, then a new event reversing the change is sent to all 
	 * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise, 
	 * the given event is then also sent to all of the general 
	 * PropertyChangeListeners of this NamespaceMemberBean.
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
		getNamespaceMemberInfo().fireVetoableChange(propertyName, new Double(oldValue), 
			new Double(newValue));
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the name 
	 * of this NamespaceMemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addNameListener(PropertyChangeListener listener)
	{
		getNamespaceMemberInfo().addNameListener(listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in the 
	 * name of this NamespaceMemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removeNameListener(PropertyChangeListener listener)
	{
		getNamespaceMemberInfo().removeNameListener(listener);
	}
	
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the name 
	 * of this NamespaceMemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableNameListener(VetoableChangeListener listener)
	{
		getNamespaceMemberInfo().addVetoableNameListener(listener);
	}


	/**
	 * Removes the given VetoableChangeListener as a listener for changes in the 
	 * name of this NamespaceMemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableNameListener(VetoableChangeListener listener)
	{
		getNamespaceMemberInfo().removeVetoableNameListener(listener);
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * Namespace of this NamespaceMemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addNamespaceListener(PropertyChangeListener listener)
	{
		getNamespaceMemberInfo().addNamespaceListener(listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the Namespace of this NamespaceMemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removeNamespaceListener(PropertyChangeListener listener)
	{
		getNamespaceMemberInfo().removeNamespaceListener(listener);
	}
	
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the 
	 * Namespace of this NamespaceMemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableNamespaceListener(VetoableChangeListener listener)
	{
		getNamespaceMemberInfo().addVetoableNamespaceListener(listener);
	}


	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * the Namespace of this NamespaceMemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableNamespaceListener(VetoableChangeListener listener)
	{
		getNamespaceMemberInfo().removeVetoableNamespaceListener(listener);
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * sequenced name of this NamespaceMemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addSequencedNameListener(PropertyChangeListener listener)
	{
		getNamespaceMemberInfo().addSequencedNameListener(listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the sequenced name of this NamespaceMemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removeSequencedNameListener(PropertyChangeListener listener)
	{
		getNamespaceMemberInfo().removeSequencedNameListener(listener);
	}
	
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the 
	 * sequenced name of this NamespaceMemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableSequencedNameListener(VetoableChangeListener listener)
	{
		getNamespaceMemberInfo().addVetoableSequencedNameListener(listener);
	}


	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * the sequenced name of this NamespaceMemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableSequencedNameListener(VetoableChangeListener listener)
	{
		getNamespaceMemberInfo().removeVetoableSequencedNameListener(listener);
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * fully-qualified name of this NamespaceMemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addFullyQualifiedNameListener(PropertyChangeListener listener)
	{
		getNamespaceMemberInfo().addFullyQualifiedNameListener(listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the fully-qualified name of this NamespaceMemberBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removeFullyQualifiedNameListener(PropertyChangeListener listener)
	{
		getNamespaceMemberInfo().removeFullyQualifiedNameListener(listener);
	}
	
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the 
	 * fully-qualified name of this NamespaceMemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableFullyQualifiedNameListener
		(VetoableChangeListener listener)
	{
		getNamespaceMemberInfo().addVetoableFullyQualifiedNameListener(listener);
	}


	/**
	 * Removes the given VetoableChangeListener as a listener for changes in 
	 * the fully-qualified name of this NamespaceMemberBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableFullyQualifiedNameListener
		(VetoableChangeListener listener)
	{
		getNamespaceMemberInfo().removeVetoableFullyQualifiedNameListener(listener);
	}
}

//--- Development History ----------------------------------------------------
//
// $Log: &
//
