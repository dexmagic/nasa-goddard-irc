//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//  $Log: AbstractBoundBean.java,v $
//  Revision 1.3  2006/04/11 16:31:11  chostetter_cvs
//  No longer clones listener list
//
//  Revision 1.2  2006/04/11 03:06:03  tames
//  Added readObject method to fix serialization.
//
//  Revision 1.1  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
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

package gov.nasa.gsfc.commons.properties.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 *  A BoundBean is an abstract bean that has a set of bound bean properties, and 
 *  reports changes in those bound properties to any interested listeners.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/04/11 16:31:11 $
 *  @author Carl F. Hostetter
**/

public abstract class AbstractBoundBean implements BoundBean
{
	private transient PropertyChangeSupport fListeners;

	
	/**
	 *  Default constructor of a new BoundBean.
	 *
	 **/

	public AbstractBoundBean()
	{
		fListeners = new PropertyChangeSupport(this);
	}

	
	/**
	 * Constructs a new BoundBean that is a clone of the given BoundBean. 
	 * 
	 * @param bean The BoundBean to clone
	 */
	
	protected AbstractBoundBean(AbstractBoundBean bean)
	{

	}
	
	
	/**
	 * Returns a clone of this BoundBean.
	 * 
	 */
	
	protected Object clone()
	{
		AbstractBoundBean clone = null;
			
		try
		{
			clone = (AbstractBoundBean) super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			
		}
		
		return (clone);
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in 
	 * any bound property of this BoundBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		fListeners.addPropertyChangeListener(listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * any bound property of this BoundBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		fListeners.removePropertyChangeListener(listener);
	}


	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * specified bound property of this BoundBean.
	 *
	 * @param propertyName The name of a Property of this BoundBean
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addPropertyChangeListener
		(String propertyName, PropertyChangeListener listener)
	{
		fListeners.addPropertyChangeListener(propertyName, listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the specified bound property of this BoundBean.
	 *
	 * @param propertyName The name of a property of this BoundBean
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removePropertyChangeListener
		(String propertyName, PropertyChangeListener listener)
	{
		fListeners.removePropertyChangeListener(propertyName, listener);
	}
		
	
	/**
	 * Removes all PropertyChangeListeners from this BoundBean.
	 *
	 **/
	
	public void removeListeners()
	{
		fListeners = new PropertyChangeSupport(this);
	}
	
	
	/**
	 * Returns true if this BoundBean currently has at least one listener for 
	 * changes in the bound property indicated by the given name, false otherwise.
	 * 
	 * @param propertyName The name of a bound property of this BoundBean
	 * @return True if this BoundBean currently has at least one listener for the 
	 * 		specific bound property indicated by the given name, false otherwise
	 */
	 
	public boolean hasListeners(String propertyName)
	{
		return (fListeners.hasListeners(propertyName));
	}
		
	
	/**
	 * Returns an array of all the listeners for changes in the bound 
	 * properties of this BoundBean.
	 * 
	 * @return An array of all the listeners for changes in the bound 
	 * 		properties of this BoundBean
	 */
	 
	public PropertyChangeListener[] getPropertyChangeListeners()
	{
		return (fListeners.getPropertyChangeListeners());
	}
		
	
	/**
	 * Returns an array of all the PropertyChangeListeners of this BoundBean 
	 * that are listeners to the property indicated by the given property name.
	 * 
	 * @param propertyName The name of a property of this BoundBean
	 * @return All of the <code>PropertyChangeListeners</code> added or an 
	 *		 empty array if no listeners have been added
	 */
	
	public PropertyChangeListener[] getPropertyChangeListeners
		(String propertyName) 
	{
		return (fListeners.getPropertyChangeListeners(propertyName));
	}
	
	
	/**
	 * Sends the given PropertyChangeEvent to all of the general 
	 * PropertyChangeListeners on this BoundBean. The given event is not sent to 
	 * the listeners if its old and new property values are equal and non-null.
	 * 
	 * @param event A PropertyChangeEvent reflecting the old and new values of some 
	 * 		property of this BoundBean
	 */
	 
	public void firePropertyChange(PropertyChangeEvent event)
	{
		fListeners.firePropertyChange(event);
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
	 
	public void firePropertyChange(String propertyName, Object oldValue, 
		Object newValue)
	{
		fListeners.firePropertyChange(propertyName, oldValue, newValue);
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
	 
	public void firePropertyChange(String propertyName, boolean oldValue, 
		boolean newValue)
	{
		fListeners.firePropertyChange(propertyName, oldValue, newValue);
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
	
	public void firePropertyChange(String propertyName, byte oldValue, 
		byte newValue)
	{
		fListeners.firePropertyChange
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
	
	public void firePropertyChange(String propertyName, char oldValue, 
		char newValue)
	{
		fListeners.firePropertyChange
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
	
	public void firePropertyChange(String propertyName, short oldValue, 
		short newValue)
	{
		fListeners.firePropertyChange
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
	 
	public void firePropertyChange(String propertyName, int oldValue, 
		int newValue)
	{
		fListeners.firePropertyChange(propertyName, oldValue, newValue);
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
	
	public void firePropertyChange(String propertyName, long oldValue, 
		long newValue)
	{
		fListeners.firePropertyChange
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
	
	public void firePropertyChange(String propertyName, float oldValue, 
		float newValue)
	{
		fListeners.firePropertyChange
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
	
	public void firePropertyChange(String propertyName, double oldValue, 
		double newValue)
	{
		fListeners.firePropertyChange
			(propertyName, new Double(oldValue), new Double(newValue));
	}
	
	/**
	 * Read a serialized version of this Object.
	 * 
	 * @param stream The stream to read from
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream stream) 
		throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		fListeners = new PropertyChangeSupport(this);
	}
}
