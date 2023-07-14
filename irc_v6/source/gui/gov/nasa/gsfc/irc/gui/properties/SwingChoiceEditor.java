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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

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
public class SwingChoiceEditor extends SwingEditorSupport
	implements ActionListener
{
    /**
     * Set if this editor's descriptor contains an enumerated  
     * choice constraint. Subclasses are responsible for recognizing and
     * handling this condition.
     */
    protected boolean fIsChoice = true;

    private JComboBox fComboBox;
	private DefaultComboBoxModel fComboBoxModel = new DefaultComboBoxModel();
	private ArrayList fChoiceName = new ArrayList(5);
	private ArrayList fChoiceValue = new ArrayList(5);
	
	/**
	 * 
	 */
	public SwingChoiceEditor()
	{
		super();
		fComboBox = new JComboBox(fComboBoxModel);

		fPanel = new JPanel();
		fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));
		fPanel.add(fComboBox);
		fComboBox.addActionListener(this);
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
			// See if the combo box selection needs to be updated
			int currentSelection = fComboBox.getSelectedIndex();
			int valueIndex = fChoiceValue.indexOf(value);
			
			if (currentSelection != valueIndex)
			{
				// The value does not match the current selected item in the
				// Combo Box
				if (valueIndex >= 0)
				{
					fComboBox.setSelectedIndex(valueIndex);
				}
			}
			
			setValue0(value);
		}
	}

	/**
	 * Set the value. Since this can only be called by this class it 
	 * bypasses validation and updating the ComboBox since by definition they 
	 * will be correct.
	 * 
	 * @param value the value to set
	 */
	private void setValue0(Object value)
	{
		super.setValue(value);
	}
	
	/**
	 * For property editors that must be initialized with values from a field
	 * descriptor.
	 * 
	 * @param descriptor the descriptor
	 */
	public void setDescriptor(FieldDescriptor descriptor)
	{
		super.setDescriptor(descriptor);
		
		// Reset to default state
		fComboBoxModel.removeAllElements();
		fChoiceName.clear();
		fChoiceValue.clear();
		
		// Look for defined choices in the descriptor
		Iterator constraints = descriptor.getConstraints();

		while (constraints.hasNext())
		{
			Object constraintDescriptor = constraints.next();
			
			if (constraintDescriptor instanceof ListConstraintDescriptor)
			{
				Iterator nameIter = 
					((ListConstraintDescriptor) constraintDescriptor)
						.getChoiceDisplayNames();

				Iterator valueIter = 
					((ListConstraintDescriptor) constraintDescriptor)
						.getChoiceValues();
	
				while (nameIter.hasNext() && valueIter.hasNext())
				{
					Object name = nameIter.next();
					
					// Update cache
					fChoiceName.add(name);
					fChoiceValue.add(valueIter.next());

					fComboBoxModel.addElement(name);
				}
			}
		}
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
		//System.out.println("Choice actionPerformed:" + fComboBox.getSelectedItem());
		
		int selectedIndex = fComboBox.getSelectedIndex();
		
		// Guard against action events caused by clearing the combo box.
		if (selectedIndex >= 0)
		{
			setValue0(fChoiceValue.get(selectedIndex));
			//System.out.println("Choice actionPerformed value:" + fChoiceValue.get(selectedIndex));
		}
	}

}


//--- Development History  ---------------------------------------------------
//
//  $Log: SwingChoiceEditor.java,v $
//  Revision 1.3  2005/01/26 19:38:13  tames
//  More code cleanup and additional support for properties with choice
//  constraints.
//
//  Revision 1.2  2005/01/25 23:40:17  tames
//  General editor cleanup and bug fixes.
//
//  Revision 1.1  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//