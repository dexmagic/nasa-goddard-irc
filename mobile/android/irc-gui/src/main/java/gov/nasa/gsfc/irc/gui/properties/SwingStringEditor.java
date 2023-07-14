//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//  This class is a modified version of code originally developed by 
//  Sun Microsystems. The copyright notice for the original code is given at 
//  the end of the file.
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

import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * A property editor for editing strings. This editor also supports 
 * value constraints defined in a descriptor set by 
 * <code>setFieldDescriptor</code>.
 *
 * @version	$Date: 2006/01/03 15:52:05 $
 * @author 	Troy Ames
 */
public class SwingStringEditor extends ChoiceEditorSupport 
	implements FocusListener, ActionListener
{
	// Default representation
	private JTextField fTextfield;
	
	/**
	 * Default constructor.
	 */
	public SwingStringEditor()
	{
		fTextfield = new JTextField();
		fTextfield.setDragEnabled(true);

		fPanel = new JPanel();
		fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));
		fPanel.add(fTextfield);
		fTextfield.addFocusListener(this);
        fTextfield.addActionListener(this);
      
	}

	/**
     * Set (or change) the object that is to be edited.
     * 
     * @param value The new target object to be edited.
     * @see java.beans.PropertyEditor#setValue(java.lang.Object)
	 */
	public void setValue(Object value)
	{
		if (validateValue(value))
		{
			super.setValue(value);

			if (value != null)
			{
				fTextfield.setText(value.toString());
			}
			else
			{
				fTextfield.setText("");
			}
		}
	}

    /**
	 * Set the descriptor for this editor.
	 * 
	 * @param descriptor	the descriptor to apply.
	 */
	public void setDescriptor(FieldDescriptor descriptor)
	{
		super.setDescriptor(descriptor);
		
		if (descriptor != null)
		{
			// Check if the descriptor contains a ListConstraint
			if (!fIsChoice)
			{
				if (descriptor.isReadOnly())
				{
					fTextfield.setEditable(false);
				}
				else
				{
					fTextfield.setEditable(true);
				}
			}
		}
	}

	/**
	 * Stops the editing session by setting the value to the current contents of
	 * the editor.
	 * 
	 * @return true
	 */
	public boolean stopEditing()
	{
		if (!fIsChoice)
		{
			setValue(fTextfield.getText());
		}

		return true;
	}

	/**
	 * Cancel editing by restoring the value.
	 */
	public void cancelEditing()
	{
		if (!fIsChoice)
		{
			fTextfield.setText(getValue().toString());
		}
	}

	/**
	 * Invoked when the component gains the keyboard focus. This implementation
	 * selects the content of the text field for editing.
	 */
	public void focusGained(FocusEvent e)
	{
		//fTextfield.selectAll();
	}

	/**
	 * Invoked when the component loses the keyboard focus. This implementation
	 * ignores the event.
	 */
	public void focusLost(FocusEvent e)
	{
        stopEditing();
	}

	/**
	 * Handle action event by setting the value of the property to the content
	 * of the text field.
	 * 
	 * @param event the ActionEvent performed.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event)
	{
		setValue(fTextfield.getText());
	}
}

//--- Development History ---------------------------------------------------
//
//	$Log: SwingStringEditor.java,v $
//	Revision 1.7  2006/01/03 15:52:05  tames
//	Removed unused fChoiceEditor reference.
//	
//	Revision 1.6  2005/07/01 18:33:16  smaher_cvs
//	Organized imports.
//	
//	Revision 1.5  2005/07/01 17:58:40  smaher_cvs
//	Added call to setAsText() in focusLost() record changes when the user
//	simply leaves the edit field (consistent with the other property editors).
//	
//	Revision 1.4  2005/02/03 07:04:35  tames
//	Changed focus method not to select all when the editor is selected.
//	
//	Revision 1.3  2005/01/26 19:38:13  tames
//	More code cleanup and additional support for properties with choice
//	constraints.
//	
//	Revision 1.2  2005/01/20 08:08:14  tames
//	Changes to support choice descriptors and message editing bug fixes.
//	
//	Revision 1.1 2005/01/07 21:01:09 tames
//	Relocated.
//	
//	
