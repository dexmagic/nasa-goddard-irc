//=== File Prolog ============================================================
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.commons.numerics.types.Amount;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;

/**
 * A property editor for editing <code>Amount</code> objects. 
 * <p>
 * This editor also supports value constraints defined in a descriptor set by 
 * <code>setFieldDescriptor</code>.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/05 05:57:12 $
 * @author 	Troy Ames
 */
public class AmountEditor extends ChoiceEditorSupport
	implements FocusListener, ActionListener, ItemListener
{
	private JTextField fAmountTextfield;
	private JComboBox fUnitList;

	/**
	 * Default constructor.
	 */
	public AmountEditor()
	{
		Document textDocument = getDocument();
		
		fAmountTextfield = new JTextField();
		Collection units = Unit.getInstances();
		TreeSet sortedUnits = new TreeSet();
		
		// Sort the units
		for (Iterator iter = units.iterator(); iter.hasNext();)
		{
			sortedUnits.add(((Unit) iter.next()).toString());
		}

		fUnitList = new JComboBox(sortedUnits.toArray());
		fUnitList.setEditable(true);
		
		if (textDocument != null)
		{
			fAmountTextfield.setDocument(textDocument);
		}
		
		fAmountTextfield.setDragEnabled(true);

		fPanel = new JPanel();
		fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));
		fPanel.add(fAmountTextfield);
		fPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		fPanel.add(new JLabel("Unit:"));
		fPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		fPanel.add(fUnitList);
		fPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		fAmountTextfield.addFocusListener(this);
		fAmountTextfield.addActionListener(this);
		//fUnitList.addActionListener(this);
		fUnitList.addItemListener(this);
	}

    /**
     * Set (or change) the Number object to be edited.
     * 
     * @param value The new target object to be edited.
     */
	public void setValue(Object value)
	{
		if (value == null)
		{
			super.setValue(value);
			fAmountTextfield.setText(null);
			fUnitList.setSelectedItem(null);
		}
		else
		{
			if (value instanceof Amount)
			{
				Double amount = new Double(((Amount) value).getAmount());
	
				if (validateValue(amount))
				{
					super.setValue(value);
					fAmountTextfield.setText(amount.toString());
					Unit unit = ((Amount) value).getUnit();
					
					if (unit != null)
					{
						fUnitList.setSelectedItem(unit.toString());
					}
					else
					{
						fUnitList.setSelectedItem(null);
					}
				}
			}
		}
	}

    /**
     * Sets the property value by parsing a given String.
     *
     * @param text  The string to be parsed.
     */
	public void setAsText(String text)
	{
		// Convert text to proper type.
		double value = Double.parseDouble(text);
		Amount amount = new Amount();
		amount.setAmount(value);
		setValue(amount);
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
					fAmountTextfield.setEditable(false);
				}
				else
				{
					fAmountTextfield.setEditable(true);
				}
			}
		}
	}
	
	/**
	 * Get a Document for specific to this Class. Subclasses should 
	 * over ride this method if they restrict input into the textfield.
	 * This implementation returns a text document that will reject any 
	 * characters that are not digits or a decimal point. The 
	 * decimal point can only occur once in the field.
	 * 
	 * @return a Document
	 */
	protected Document getDocument()
	{
		return new PlainDocument()
		{
			private final char DECIMAL_CHAR = '.';

			public void insertString(int offset, String str, AttributeSet atts)
					throws BadLocationException
			{
				if (str != null && str.length() > 0)
				{
					// Check that the string is a digit or a character
					if (Character.isDigit(str.charAt(0)))
					{
						super.insertString(offset, str, atts);
					}
					else if (str.charAt(0) == DECIMAL_CHAR)
					{
						Content content = getContent();
						int length = content.length();
						String currentString = content.getString(0, length);

						// There can only be one decimal point
						if (currentString.indexOf(DECIMAL_CHAR) < 0)
						{
							super.insertString(offset, str, atts);
						}
					}
				}
			}
		};
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
			super.stopEditing();
		}
		else
		{
			updateAmount();
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
			super.stopEditing();
		}
		else
		{
			Amount amount = (Amount) getValue();
			fAmountTextfield.setText(Double.toString(amount.getAmount()));
			fUnitList.setSelectedItem(amount.getUnit().toString());
		}
	}

    /**
	 * Invoked when the component gains the keyboard focus. This implementation
	 * selects the content of the text field for editing.
	 */
	public void focusGained(FocusEvent e)
	{
		//fAmountTextfield.selectAll();
	}

	/**
	 * Invoked when the component loses the keyboard focus. This implementation
	 * ignores the event.
	 */
	public void focusLost(FocusEvent e)
	{
	}

	/**
	 * Handle value change action event by setting the value of the property to 
	 * the content of the amount and unit fields.
	 * 
	 * @param event the ActionEvent performed.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event)
	{
		updateAmount();
	}

	/**
	 * Handle unit change by setting the value of the property to the content
	 * of the amount and unit field.
	 * 
	 * @param event an ItemEvent
	 */
	public void itemStateChanged(ItemEvent event)
	{
		if (event.getStateChange() == event.SELECTED)
		{
			updateAmount();
		}
	}
	
	/**
	 * Updates the value based on the current amount and unit fields.
	 */
	private void updateAmount()
	{
		Amount amount = new Amount();
		Unit unit = Unit.ONE;
		double value = 1.0d;
		
		String amountString = fAmountTextfield.getText();
		
		if (amountString != null && amountString.length() > 0)
		{
			value = Double.parseDouble(fAmountTextfield.getText());		
		}
		
		String unitSymbol = (String) fUnitList.getSelectedItem();
		
		if (unitSymbol != null)
		{
			try
			{
				unit = Unit.valueOf(unitSymbol);
			}
			catch (IllegalArgumentException e)
			{
				// Do nothing and let unit default to Unit.ONE
			}
		}
		
		amount.setAmount(value, unit);
		setValue(amount);
	}
}

//--- Development History ---------------------------------------------------
//
//	$Log: AmountEditor.java,v $
//	Revision 1.3  2006/01/05 05:57:12  tames
//	Updates to handle null amounts and user entered units.
//	
//	Revision 1.2  2006/01/04 16:55:44  tames
//	A more complete implementation.
//	
//	Revision 1.1  2006/01/03 15:51:09  tames
//	Initial version
//	
//
