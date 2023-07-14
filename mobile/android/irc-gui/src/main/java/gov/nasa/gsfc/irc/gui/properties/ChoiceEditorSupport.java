//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.gui.properties;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import gov.nasa.gsfc.irc.data.description.ListConstraintDescriptor;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;

/**
 * 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/26 19:38:13 $
 * @author 	Troy Ames
 */
public class ChoiceEditorSupport extends SwingEditorSupport
{
    /**
     * Set if this editor's descriptor contains an enumerated  
     * choice constraint. Subclasses are responsible for recognizing and
     * handling this condition.
     */
    protected boolean fIsChoice = false;

	// Editor to use if this editor represents an enumerated choice field
	private SwingChoiceEditor fChoiceEditor = new SwingChoiceEditor();

	/**
     * Gets the value of the property.
     *
     * @return The value of the property.
     */
    public Object getValue()
	{
		if (fIsChoice)
		{
			return fChoiceEditor.getValue();
		}
		else
		{
			return super.getValue();
		}
	}

	/**
     * Set (or change) the object that is to be edited.
     * 
     * @param value The new target object to be edited.
     * @see java.beans.PropertyEditor#setValue(java.lang.Object)
	 */
	public void setValue(Object value)
	{
		if (fIsChoice)
		{
			fChoiceEditor.setValue(value);
		}
		else
		{
			super.setValue(value);
		}
	}

    /**
	 * Returns the panel responsible for rendering this PropertyEditor.
	 * 
	 * @return the PropertyPanel panel
	 */
	public Component getCustomEditor()
	{
		Component panel = super.getCustomEditor();
		
		if (fIsChoice)
		{
			panel = fChoiceEditor.getCustomEditor();
		}
		
		return panel;
	}

	/**
	 * Stops the editing session by setting the value to the current contents of
	 * the editor.
	 * 
	 * @return true
	 */
	public boolean stopEditing()
	{
		if (fIsChoice)
		{
			fChoiceEditor.stopEditing();
			setValue(fChoiceEditor.getValue());
		}

		return true;
	}

	/**
	 * Cancel editing by restoring the value.
	 */
	public void cancelEditing()
	{
		if (fIsChoice)
		{
			fChoiceEditor.cancelEditing();
		}
	}

	/**
	 * For property editors that must be initialized with values from a 
	 * descriptor. Since the editor can be shared among many properties
	 * this method must verify and update all internal state based on the 
	 * descriptor to prevent an incorrect state being carried over and applied
	 * to another property. This implementation checks if the descriptor
	 * contains an enumerated choice constraint and sets the 
	 * <code>fIsChoice</code> flag.
	 * 
	 * @param descriptor	the descriptor to apply to this property.
	 */
	public void setDescriptor(FieldDescriptor descriptor)
	{
		super.setDescriptor(descriptor);

		// Reset state to the default
		fIsChoice = false;
		
		if (descriptor != null)
		{
			// Check if the descriptor contains a ListConstraint
			Iterator constraints = descriptor.getConstraints();
			
			while (!fIsChoice && constraints.hasNext())
			{
				Object constraintDescriptor = constraints.next();
				
				if (constraintDescriptor instanceof ListConstraintDescriptor)
				{
					fIsChoice = true;
				}
			}
		}
		
		if (fIsChoice)
		{
			// Delegate to the Choice editor
			fChoiceEditor.setDescriptor(descriptor);
		}
	}

    /**
     * Register a listener for the PropertyChange event.  The class will
     * fire a PropertyChange value whenever the value is updated.
     *
     * @param listener  An object to be invoked when a PropertyChange
     *		event is fired.
     */
    public void addPropertyChangeListener(PropertyChangeListener l)  {
        fChoiceEditor.addPropertyChangeListener(l);
        super.addPropertyChangeListener(l);
    }

    /**
     * Remove a listener for the PropertyChange event.
     *
     * @param listener  The PropertyChange listener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener l)  {
        fChoiceEditor.removePropertyChangeListener(l);
        super.removePropertyChangeListener(l);
    }

}


//--- Development History  ---------------------------------------------------
//
//  $Log: ChoiceEditorSupport.java,v $
//  Revision 1.1  2005/01/26 19:38:13  tames
//  More code cleanup and additional support for properties with choice
//  constraints.
//
//