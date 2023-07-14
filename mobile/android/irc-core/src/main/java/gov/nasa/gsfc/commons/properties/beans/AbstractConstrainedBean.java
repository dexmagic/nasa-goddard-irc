//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//  $Log: AbstractConstrainedBean.java,v $
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
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 *  A Constrained Bean has a set of constrained bean properties, permits any
 *  registered VetoableChangeListeners to veto a requested change in those
 *  constrained properties, and reports non-vetoed changes in those constrained
 *  properties to any interested listeners.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/04/11 16:31:11 $
 *  @author Carl F. Hostetter
 **/

public abstract class AbstractConstrainedBean
        implements ConstrainedBean
{

    /**
     *  Default constructor of a new ConstrainedBean.
     *
     **/

    public AbstractConstrainedBean()
    {

    }


    /**
     * Constructs a new ConstrainedBean that is a clone of the given ConstrainedBean.
     *
     * @param bean The ConstrainedBean to clone
     */

    protected AbstractConstrainedBean(AbstractConstrainedBean bean)
    {

    }


    /**
     * Returns a clone of this ConstrainedBean.
     *
     */

    protected Object clone(AbstractConstrainedBean bean)
    {
      return null;
    }


    /**
     * Adds the given VetoableChangeListener as a listener for changes in
     * any constrained property of this ConstrainedBean.
     *
     * @param listener A VetoableChangeListener
     **/

    public void addVetoableChangeListener(VetoableChangeListener listener)
    {

    }


    /**
     * Removes the given VetoableChangeListener as a listener for changes in
     * any constrained property of this ConstrainedBean.
     *
     * @param listener A VetoableChangeListener
     **/

    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {

    }


    /**
     * Adds the given VetoableChangeListener as a listener for changes in the
     * specified constrained property of this ConstrainedBean.
     *
     * @param propertyName The name of a constrained property of this
     * 		ConstrainedBean
     * @param listener A VetoableChangeListener
     **/

    public void addVetoableChangeListener
    (String propertyName, VetoableChangeListener listener)
    {

    }


    /**
     * Removes the given VetoableChangeListener as a listener for changes in
     * the specified bound property of this ConstrainedBean.
     *
     * @param propertyName The name of a constrained property of this
     * 		ConstrainedBean
     * @param listener A VetoableChangeListener
     **/

    public void removeVetoableChangeListener
    (String propertyName, VetoableChangeListener listener)
    {

    }


    /**
     * Returns an array of all the listeners for changes in the constrained
     * properties of this ConstrainedBean.
     *
     * @return An array of all the listeners for changes in the constrained
     * 		properties of this ConstrainedBean
     */

    public VetoableChangeListener[] getVetoableChangeListeners()
    {
       return null;
    }


    /**
     * Removes all listeners from this ConstrainedBean.
     *
     **/

    public void removeListeners()
    {

    }


    /**
     * Removes (only) all PropertyChangeListeners from this ConstrainedBean.
     *
     **/

    public void removePropertyChangeListeners()
    {

    }


    /**
     * Removes (only) all VetoableChangeListeners from this ConstrainedBean.
     *
     **/

    public void removeVetoableChangeListeners()
    {

    }


    /**
     * Returns true if this ConstrainedBean currently has at least one listener for
     * changes in the property (whether bound or constrained) indicated by the given
     * name, false otherwise.
     *
     * @param propertyName The name of a property (whether bound or constrained) of
     * 		this ConstrainedBean
     * @return True if this ConstrainedBean currently has at least one listener for
     * 		the specific property (whether bound or constrained) indicated by the
     * 		given name, false otherwise
     */

    public boolean hasListeners(String propertyName)
    {
       return false;
    }


    /**
     * Returns true if this ConstrainedBean currently has at least one
     * VetoableChangeListener for changes in the constrained property indicated by
     * the given name, false otherwise.
     *
     * @param propertyName The name of a constrained property of this
     * 		ConstrainedBean
     * @return True if this ConstrainedBean currently has at least one
     * 		VetoableChangeListener for the specific constrained property indicated
     * 		by the given name, false otherwise
     */

    public boolean hasVetoableListeners(String propertyName)
    {
        return false;
    }


    /**
     * Sends the given PropertyChangeEvent to, first, all of the general
     * VetoableChangeListeners, and then, if not vetoed, to all of the general
     * PropertyChangeListeners on this ConstrainedBean. The given event is not sent
     * to the listeners if its old and new property values are equal and non-null.
     * If any VetoableChangeListener vetoes the change, then a new event reversing
     * the change is sent to all VetoableChangeListeners and a PropertyVetoException
     * is thrown. Otherwise, the given event is then also sent to all of the general
     * PropertyChangeListeners of this ConstrainedBean.
     *
     * @param event A PropertyChangeEvent reflecting the old and new values of some
     * 		constrained property of this ConstrainedBean
     * @throws PropertyVetoException if the indicated property change is vetoed by
     * 		some VetoableChangeListener
     */

    public void fireVetoableChange(PropertyChangeEvent event)
            throws PropertyVetoException
    {

    }


    /**
     * Reports the specified change in the value of the indicated property to,
     * first, all of the VetoableChangeListeners, and then, if not vetoed, to all of
     * the PropertyChangeListeners on this ConstrainedBean on the indicated
     * property. No change is reported to the listeners if the given old and new
     * property values are equal and non-null. If any VetoableChangeListener vetoes
     * the change, then a new event reversing the change is sent to all
     * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise,
     * the given event is then also sent to all of the general
     * PropertyChangeListeners of this ConstrainedBean.
     *
     * @param propertyName The name of the changed property
     * @param oldValue The old value of the indicated changed property
     * @param newValue The new value of the indicated changed property
     * @throws PropertyVetoException if the indicated property change is vetoed by
     * 		some VetoableChangeListener
     */

    public void fireVetoableChange(String propertyName, Object oldValue,
                                   Object newValue)
            throws PropertyVetoException
    {

    }


    /**
     * Reports the specified change in the value of the indicated boolean-valued
     * property to, first, all of the VetoableChangeListeners, and then, if not
     * vetoed, to all of the PropertyChangeListeners on this ConstrainedBean on the
     * indicated property. No change is reported to the listeners if the given old
     * and new property values are equal and non-null. If any VetoableChangeListener
     * vetoes the change, then a new event reversing the change is sent to all
     * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise,
     * the given event is then also sent to all of the general
     * PropertyChangeListeners of this ConstrainedBean.
     *
     * @param propertyName The name of the changed boolean-valued property
     * @param oldValue The old value of the indicated changed boolean-valued property
     * @param newValue The new value of the indicated changed boolean-valued property
     * @throws PropertyVetoException if the indicated property change is vetoed by
     * 		some VetoableChangeListener
     */

    public void fireVetoableChange(String propertyName, boolean oldValue,
                                   boolean newValue)
            throws PropertyVetoException
    {

    }


    /**
     * Reports the specified change in the value of the indicated byte-valued
     * property to, first, all of the VetoableChangeListeners, and then, if not
     * vetoed, to all of the PropertyChangeListeners on this ConstrainedBean on the
     * indicated property. No change is reported to the listeners if the given old
     * and new property values are equal and non-null. If any VetoableChangeListener
     * vetoes the change, then a new event reversing the change is sent to all
     * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise,
     * the given event is then also sent to all of the general
     * PropertyChangeListeners of this ConstrainedBean.
     *
     * @param propertyName The name of the changed byte-valued property
     * @param oldValue The old value of the indicated changed byte-valued property
     * @param newValue The new value of the indicated changed byte-valued property
     * @throws PropertyVetoException if the indicated property change is vetoed by
     * 		some VetoableChangeListener
     */

    public void fireVetoableChange(String propertyName, byte oldValue,
                                   byte newValue)
            throws PropertyVetoException
    {

    }


    /**
     * Reports the specified change in the value of the indicated char-valued
     * property to, first, all of the VetoableChangeListeners, and then, if not
     * vetoed, to all of the PropertyChangeListeners on this ConstrainedBean on the
     * indicated property. No change is reported to the listeners if the given old
     * and new property values are equal and non-null. If any VetoableChangeListener
     * vetoes the change, then a new event reversing the change is sent to all
     * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise,
     * the given event is then also sent to all of the general
     * PropertyChangeListeners of this ConstrainedBean.
     *
     * @param propertyName The name of the changed char-valued property
     * @param oldValue The old value of the indicated changed char-valued property
     * @param newValue The new value of the indicated changed char-valued property
     * @throws PropertyVetoException if the indicated property change is vetoed by
     * 		some VetoableChangeListener
     */

    public void fireVetoableChange(String propertyName, char oldValue,
                                   char newValue)
            throws PropertyVetoException
    {

    }


    /**
     * Reports the specified change in the value of the indicated short-valued
     * property to, first, all of the VetoableChangeListeners, and then, if not
     * vetoed, to all of the PropertyChangeListeners on this ConstrainedBean on the
     * indicated property. No change is reported to the listeners if the given old
     * and new property values are equal and non-null. If any VetoableChangeListener
     * vetoes the change, then a new event reversing the change is sent to all
     * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise,
     * the given event is then also sent to all of the general
     * PropertyChangeListeners of this ConstrainedBean.
     *
     * @param propertyName The name of the changed short-valued property
     * @param oldValue The old value of the indicated changed short-valued property
     * @param newValue The new value of the indicated changed short-valued property
     * @throws PropertyVetoException if the indicated property change is vetoed by
     * 		some VetoableChangeListener
     */

    public void fireVetoableChange(String propertyName, short oldValue,
                                   short newValue)
            throws PropertyVetoException
    {

    }


    /**
     * Reports the specified change in the value of the indicated int-valued
     * property to, first, all of the VetoableChangeListeners, and then, if not
     * vetoed, to all of the PropertyChangeListeners on this ConstrainedBean on the
     * indicated property. No change is reported to the listeners if the given old
     * and new property values are equal and non-null. If any VetoableChangeListener
     * vetoes the change, then a new event reversing the change is sent to all
     * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise,
     * the given event is then also sent to all of the general
     * PropertyChangeListeners of this ConstrainedBean.
     *
     * @param propertyName The name of the changed int-valued property
     * @param oldValue The old value of the indicated changed int-valued property
     * @param newValue The new value of the indicated changed int-valued property
     * @throws PropertyVetoException if the indicated property change is vetoed by
     * 		some VetoableChangeListener
     */

    public void fireVetoableChange(String propertyName, int oldValue,
                                   int newValue)
            throws PropertyVetoException
    {

    }


    /**
     * Reports the specified change in the value of the indicated long-valued
     * property to, first, all of the VetoableChangeListeners, and then, if not
     * vetoed, to all of the PropertyChangeListeners on this ConstrainedBean on the
     * indicated property. No change is reported to the listeners if the given old
     * and new property values are equal and non-null. If any VetoableChangeListener
     * vetoes the change, then a new event reversing the change is sent to all
     * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise,
     * the given event is then also sent to all of the general
     * PropertyChangeListeners of this ConstrainedBean.
     *
     * @param propertyName The name of the changed long-valued property
     * @param oldValue The old value of the indicated changed long-valued property
     * @param newValue The new value of the indicated changed long-valued property
     * @throws PropertyVetoException if the indicated property change is vetoed by
     * 		some VetoableChangeListener
     */

    public void fireVetoableChange(String propertyName, long oldValue,
                                   long newValue)
            throws PropertyVetoException
    {

    }


    /**
     * Reports the specified change in the value of the indicated float-valued
     * property to, first, all of the VetoableChangeListeners, and then, if not
     * vetoed, to all of the PropertyChangeListeners on this ConstrainedBean on the
     * indicated property. No change is reported to the listeners if the given old
     * and new property values are equal and non-null. If any VetoableChangeListener
     * vetoes the change, then a new event reversing the change is sent to all
     * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise,
     * the given event is then also sent to all of the general
     * PropertyChangeListeners of this ConstrainedBean.
     *
     * @param propertyName The name of the changed float-valued property
     * @param oldValue The old value of the indicated changed float-valued property
     * @param newValue The new value of the indicated changed float-valued property
     * @throws PropertyVetoException if the indicated property change is vetoed by
     * 		some VetoableChangeListener
     */

    public void fireVetoableChange(String propertyName, float oldValue,
                                   float newValue)
            throws PropertyVetoException
    {

    }


    /**
     * Reports the specified change in the value of the indicated double-valued
     * property to, first, all of the VetoableChangeListeners, and then, if not
     * vetoed, to all of the PropertyChangeListeners on this ConstrainedBean on the
     * indicated property. No change is reported to the listeners if the given old
     * and new property values are equal and non-null. If any VetoableChangeListener
     * vetoes the change, then a new event reversing the change is sent to all
     * VetoableChangeListeners and a PropertyVetoException is thrown. Otherwise,
     * the given event is then also sent to all of the general
     * PropertyChangeListeners of this ConstrainedBean.
     *
     * @param propertyName The name of the changed double-valued property
     * @param oldValue The old value of the indicated changed double-valued property
     * @param newValue The new value of the indicated changed double-valued property
     * @throws PropertyVetoException if the indicated property change is vetoed by
     * 		some VetoableChangeListener
     */

    public void fireVetoableChange(String propertyName, double oldValue,
                                   double newValue)
            throws PropertyVetoException
    {

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

    }
}
