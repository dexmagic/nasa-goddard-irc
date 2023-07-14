//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/StringConstantValueSelector.java,v 1.1 2006/04/27 19:46:21 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: StringConstantValueSelector.java,v $
//  Revision 1.1  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
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

package gov.nasa.gsfc.irc.data.selection;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import gov.nasa.gsfc.irc.data.selection.description.StringConstantValueSelectorDescriptor;


/**
 * A StringConstantValueSelector performs char-delimited data selection on a given 
 * ByteBuffer of data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 19:46:21 $
 * @author Carl F. Hostetter
 */

public class StringConstantValueSelector extends CharDataSelector
{
	private StringConstantValueSelectorDescriptor fDescriptor;
	
	private String fValue;
	private char[] fValueChars;
	private int fNumChars = 0;
	private boolean fIsAscii;
	
	
	/**
	 * Constructs a new StringConstantValueSelector that will perform the String value 
	 * selection indicated by the given StringConstantValueSelectorDescriptor.
	 *
	 * @param descriptor A StringConstantValueSelectorDescriptor
	 * @return A new StringConstantValueSelector that will perform the String value  
	 * 		selection indicated by the given StringConstantValueSelectorDescriptor		
	**/
	
	public StringConstantValueSelector
		(StringConstantValueSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		setDescriptor(descriptor);
	}
	
	
	/**
	 *  Sets the StringConstantValueSelectorDescriptor of this 
	 *  StringConstantValueSelector to the given StringConstantValueSelectorDescriptor, 
	 *  and reconfigures this StringConstantValueSelector accordingly.
	 *  
	 *  @param
	 */
	
	public void setDescriptor(StringConstantValueSelectorDescriptor descriptor)
	{
		if (descriptor != null)
		{
			fDescriptor = descriptor;
			
			configureFromDescriptor();
		}
	}
	
	
	/**
	 *  Configures this StringConstantValueSelector in accordance with its current 
	 *  StringConstantValueSelectorDescriptor.
	 *  
	 */
	
	private void configureFromDescriptor()
	{
		fValue = fDescriptor.getValue();
		fValueChars = fValue.toCharArray();
		fNumChars = fValueChars.length;
		fIsAscii = fDescriptor.isAscii();
	}
	
	
	/**
	 *  Returns the data selected by this StringConstantValueSelector from the given 
	 *  byte array, or null if it selects nothing from the given byte array.
	 *  
	 *  @para data A byte array of data
	 *  @return The data selected by this StringConstantValueSelector from the given 
	 *  		byte array, or null if it selects nothing from the given byte 
	 *  		array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given byte array
	 */
	
	protected Object select(byte[] data) 
		throws IllegalArgumentException
	{	
		String result = null;
		
		boolean done = false;
		
		if (fIsAscii)
		{
			if (fNumChars <= data.length)
			{
				result = fValue;
				
				for (int i = 0; i < fNumChars && ! done; i++)
				{	
					char valueChar = (char) fValueChars[i];
					char dataChar = (char) data[i];
					
					if (valueChar != dataChar)
					{
						done = true;
						result = null;
					}
				}
			}
		}
		else
		{
			if (fNumChars <= data.length / 2)
			{
				result = fValue;
				
				ByteBuffer dataBuffer = ByteBuffer.wrap(data);
				
				for (int i = 0; i < fNumChars && ! done; i++)
				{	
					char valueChar = (char) fValueChars[i];
					char dataChar = dataBuffer.getChar();
					
					if (valueChar != dataChar)
					{
						done = true;
						result = null;
					}
				}
			}
		}
		
		return (result);
	}

	/**
	 *  Returns the data selected by this StringConstantValueSelector from the given 
	 *  char array, or null if it selects nothing from the given char array.
	 *  
	 *  @para data A char array of data
	 *  @return The data selected by this StringConstantValueSelector from the given 
	 *  		char array, or null if it selects nothing from the given byte array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given char array
	 */
	
	protected Object select(char[] data)
		throws IllegalArgumentException
	{
		String result = fValue;
		
		boolean done = false;
		
		for (int i = 0; i < fNumChars && ! done; i++)
		{	
			char valueChar = (char) fValueChars[i];
			char dataChar = (char) data[i];
			
			if (valueChar != dataChar)
			{
				done = true;
				result = null;
			}
		}
		
		return (result);
	}


	/**
	 *  Returns the data selected by this StringConstantValueSelector from the given 
	 *  ByteBuffer, starting at its current position, as an array of bytes, or null 
	 *  if it selects nothing from the given ByteBuffer. The position of the given 
	 *  ByteBuffer will advance to the first position of the delimiter; further, 
	 *  the delimiter is not included in the selection.
	 *  
	 *  @para data A ByteBuffer of data
	 *  @return The data selected by this StringConstantValueSelector from the given 
	 *  		ByteBuffer, starting at its current position, as an array of bytes, or 
	 *  		null if it selects nothing from the given ByteBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given ByteBuffer
	 */
	
	protected Object select(ByteBuffer data)
		throws IllegalArgumentException
	{
		String result = null;
		
		int position = data.position();
		
		boolean done = false;
		
		if (fIsAscii)
		{
			if (fNumChars <= data.remaining())
			{
				result = fValue;
				
				for (int i = 0; i < fNumChars && ! done; i++)
				{	
					char valueChar = (char) fValueChars[i];
					char dataChar = (char) data.get();
					
					if (valueChar != dataChar)
					{
						done = true;
						result = null;
						data.position(position);
					}
				}
			}
		}
		else
		{
			if (fNumChars <= data.remaining() / 2)
			{
				result = fValue;
				
				for (int i = 0; i < fNumChars && ! done; i++)
				{	
					char valueChar = (char) fValueChars[i];
					char dataChar = data.getChar();
					
					if (valueChar != dataChar)
					{
						done = true;
						result = null;
						data.position(position);
					}
				}
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the data selected by this StringConstantValueSelector from the given 
	 *  CharBuffer, starting at its current position, as an array of chars, or null 
	 *  if it selects nothing from the given CharBuffer. The position of the given 
	 *  CharBuffer will advance to the first position of the delimiter; further, 
	 *  the delimiter is not included in the selection.
	 *  
	 *  @para data A CharBuffer of data
	 *  @return The data selected by this StringConstantValueSelector from the given 
	 *  		CharBuffer, starting at its current position, as an array of chars, or 
	 *  		null if it selects nothing from the given CharBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given CharBuffer
	 */
	
	protected Object select(CharBuffer data)
		throws IllegalArgumentException
	{
		String result = null;
		
		if (fNumChars <= data.remaining())
		{
			int position = data.position();
			
			boolean done = false;
					
			for (int i = 0; i < fNumChars && ! done; i++)
			{	
				char valueChar = (char) fValueChars[i];
				char dataChar = data.get();
				
				if (valueChar != dataChar)
				{
					done = true;
					result = null;
					data.position(position);
				}
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the data selected by this StringConstantValueSelector from the given 
	 *  String, or null if it selects nothing from the given String.
	 *  
	 *  @para data A String of data
	 *  @return The data selected by this StringConstantValueSelector from the given 
	 *  		String, or null if it selects nothing from the given String
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given String
	 */
	
	protected Object select(String data)
		throws IllegalArgumentException
	{
		String result = fValue;
		
		if (! data.startsWith(fValue))
		{
			result = null;
		}
		
		return (result);
	}
}