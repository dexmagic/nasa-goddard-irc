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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * A text document which will reject any characters that are not
 * digits or a decimal point or start with a dash ("-").
 *
 * @version	$Date: 2005/02/02 18:18:22 $
 * @author	Troy Ames
 */
public class NumberDocument extends PlainDocument
{
    private static final char DECIMAL_CHAR = '.';
    private static final char MINUS_CHAR = '-';
	
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
				
				// There can only be one minus character - at the beginning
				// If the string length is one, this is the case.
				if (currentString.indexOf(MINUS_CHAR) == -1)
				{
					super.insertString(offset, str, atts);
				}
				
			}
		}
	}
}

//--- Development History ---------------------------------------------------
//
//	$Log: NumberDocument.java,v $
//	Revision 1.3  2005/02/02 18:18:22  smaher_cvs
//	Fixed Document types to allow adding a minus sign to beginning of number after part of
//	number has already been entered.
//	
//	Revision 1.2  2005/01/10 16:47:33  smaher_cvs
//	Added support for negative numbers.
//	
//	Revision 1.1  2005/01/07 21:01:09  tames
//	Relocated.
//	
//	Revision 1.2  2004/12/22 06:22:28  tames
//	Fixed bug that prevented entering a decimal point in field.
//	
//	

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 * 
 * @author  Mark Davidson
 */
