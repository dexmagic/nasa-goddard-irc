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

import java.math.BigInteger;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * A property editor for editing Integral Numbers. Supports the following 
 * primitive types and Object wrappers: byte, short, int, long, 
 * Byte, Short, Integer, Long, and BigInteger.
 * <p>
 * This editor also supports value constraints defined in a descriptor set by 
 * <code>setFieldDescriptor</code>.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/02/02 18:18:22 $
 * @author 	Troy Ames
 */
public class IntegralNumberEditor extends SwingNumberEditor
{

	/**
	 * Construct a new editor.
	 */
	public IntegralNumberEditor()
	{
		super();
	}
	
    /**
     * Sets the property value by parsing a given String. Supports String 
     * representations of the following primitive types and Object wrappers: 
     * byte, short, int, long, BigInteger.
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
		else if (getValue() instanceof BigInteger)
		{
			setValue(new BigInteger(text));
		}
	}

	/**
	 * Get a Document for specific to this Class. Subclasses should 
	 * over ride this method if they restrict input into the textfield.
	 * This implementation returns a text document that will reject any 
	 * characters that are not digits or a dash ("-"). The dash can only occur
	 * as the first character of the field.
	 * 
	 * @return a Document
	 */
	protected Document getDocument()
	{
		return new PlainDocument()
		{
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

}


//--- Development History  ---------------------------------------------------
//
//  $Log: IntegralNumberEditor.java,v $
//  Revision 1.4  2005/02/02 18:18:22  smaher_cvs
//  Fixed Document types to allow adding a minus sign to beginning of number after part of
//  number has already been entered.
//
//  Revision 1.3  2005/01/26 19:55:18  tames
//  Removed debug print statement.
//
//  Revision 1.2  2005/01/26 19:38:13  tames
//  More code cleanup and additional support for properties with choice
//  constraints.
//
//  Revision 1.1  2005/01/25 23:40:17  tames
//  General editor cleanup and bug fixes.
//
//