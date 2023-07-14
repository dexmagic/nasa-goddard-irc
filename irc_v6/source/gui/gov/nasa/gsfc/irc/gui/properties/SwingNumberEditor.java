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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;

/**
 * A property editor for editing Numbers. Supports the following primitive types
 * and Object wrappers: byte, short, int, long, BigInteger, float, double, and 
 * BigDecimal.
 * <p>
 * This editor also supports value constraints defined in a descriptor set by 
 * <code>setFieldDescriptor</code>.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/03 15:51:48 $
 * @author 	Troy Ames
 */
public class SwingNumberEditor extends ChoiceEditorSupport
	implements FocusListener, ActionListener
{
	private JTextField fTextfield;

	/**
	 * Constructs a Number editor.
	 */
	public SwingNumberEditor()
	{
		Document textDocument = getDocument();
		
		fTextfield = new JTextField();
		
		if (textDocument != null)
		{
			fTextfield.setDocument(textDocument);
		}
		
		fTextfield.setDragEnabled(true);

		fPanel = new JPanel();
		fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));
		fPanel.add(fTextfield);
		fTextfield.addFocusListener(this);
		fTextfield.addActionListener(this);
	}

    /**
     * Set (or change) the Number object to be edited.
     * 
     * @param value The new target object to be edited.
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
		if (getValue() instanceof Byte)
		{
			setValue(new Byte(text));
		}
		else if (getValue() instanceof Short)
		{
			setValue(new Short(text));
		}
		else if (getValue() instanceof Integer)
		{
			setValue(new Integer(text));
		}
		else if (getValue() instanceof Long)
		{
			setValue(new Long(text));
		}
		else if (getValue() instanceof Float)
		{
			setValue(new Float(text));
		}
		else if (getValue() instanceof Double)
		{
			setValue(new Double(text));
		}
		else if (getValue() instanceof BigInteger)
		{
			setValue(new BigInteger(text));
		}
		else if (getValue() instanceof BigDecimal)
		{
			setValue(new BigDecimal(text));
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
	 * Get a Document for specific to this Class. Subclasses should 
	 * over ride this method if they restrict input into the textfield.
	 * This implementation returns a text document that will reject any 
	 * characters that are not digits, a decimal point, or a dash ("-"). 
	 * The dash can only occur as the first character of the field. The 
	 * decimal point can only occur once in the field.
	 * 
	 * @return a Document
	 */
	protected Document getDocument()
	{
		return new PlainDocument()
		{
			private final char DECIMAL_CHAR = '.';
			private final char MINUS_CHAR = '-';

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
					else if (str.charAt(0) == MINUS_CHAR)
					{
						Content content = getContent();
						int length = content.length();
						String currentString = content.getString(0, length);

						// There can only be one minus character - at the
						// beginning
						// If the string length is one, this is the case.
						if (currentString.indexOf(MINUS_CHAR) == -1)
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
			setAsText(fTextfield.getText());
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
		setAsText(fTextfield.getText());
	}
}

//--- Development History ---------------------------------------------------
//
//	$Log: SwingNumberEditor.java,v $
//	Revision 1.7  2006/01/03 15:51:48  tames
//	Removed unused fChoiceEditor reference.
//	
//	Revision 1.6  2005/02/03 07:04:35  tames
//	Changed focus method not to select all when the editor is selected.
//	
//	Revision 1.5  2005/02/02 18:18:22  smaher_cvs
//	Fixed Document types to allow adding a minus sign to beginning of number after part of
//	number has already been entered.
//	
//	Revision 1.4  2005/01/26 19:38:13  tames
//	More code cleanup and additional support for properties with choice
//	constraints.
//	
//	Revision 1.3  2005/01/25 23:40:17  tames
//	General editor cleanup and bug fixes.
//	
//	Revision 1.2 2005/01/20 08:08:14 tames
//	Changes to support choice descriptors and message editing bug fixes.
//	
//	Revision 1.1 2005/01/07 21:01:09 tames
//	Relocated.
//	
//	

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 * 
 * @author Mark Davidson
 */
