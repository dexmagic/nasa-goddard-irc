//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/CharDelimitedDataSelector.java,v 1.3 2006/04/27 19:46:21 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: CharDelimitedDataSelector.java,v $
//  Revision 1.3  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.2  2006/01/25 17:02:23  chostetter_cvs
//  Support for arbitrary-length Message parsing
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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

import gov.nasa.gsfc.irc.data.selection.description.CharDelimitedDataSelectorDescriptor;


/**
 * A CharDelimitedDataSelector performs char-delimited data selection on a given 
 * ByteBuffer of data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 19:46:21 $
 * @author Carl F. Hostetter
 */

public class CharDelimitedDataSelector extends CharDataSelector
{
	private CharDelimitedDataSelectorDescriptor fDescriptor;
	
	private boolean fIsAscii = false;
	private char fDelimiter;
	
	
	/**
	 * Constructs a new CharDelimitedDataSelector that will perform the char-delimited 
	 * data selection indicated by the given CharDelimitedDataSelectorDescriptor.
	 *
	 * @param descriptor A CharDelimitedDataSelectorDescriptor
	 * @return A new CharDelimitedDataSelector that will perform the char-delimited 
	 * 		data selection indicated by the given CharDelimitedDataSelectorDescriptor		
	**/
	
	public CharDelimitedDataSelector
		(CharDelimitedDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		setDescriptor(descriptor);
	}
	
	
	/**
	 *  Sets the CharDelimitedDataSelectorDescriptor of this 
	 *  CharDelimitedDataSelector to the given CharDelimitedDataSelectorDescriptor, 
	 *  and reconfigures this CharDelimitedDataSelector accordingly.
	 *  
	 *  @param
	 */
	
	public void setDescriptor(CharDelimitedDataSelectorDescriptor descriptor)
	{
		if (descriptor != null)
		{
			fDescriptor = descriptor;
			
			configureFromDescriptor();
		}
	}
	
	
	/**
	 *  Configures this CharDelimitedDataSelector in accordance with its current 
	 *  CharDelimitedDataSelectorDescriptor.
	 *  
	 */
	
	private void configureFromDescriptor()
	{
		fDelimiter = fDescriptor.getDelimiter();
		
		fIsAscii = isAscii();
	}
	
	
	/**
	 *  Returns the data selected by this CharDelimitedDataSelector from the given 
	 *  byte array, or null if it selects nothing from the given byte array.
	 *  
	 *  @para data A byte array of data
	 *  @return The data selected by this CharDelimitedDataSelector from the given 
	 *  		byte array, or null if it selects nothing from the given byte 
	 *  		array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given byte array
	 */
	
	protected Object select(byte[] data) 
		throws IllegalArgumentException
	{	
		String result = null;
		
		StringBuffer resultsBuffer = new StringBuffer();
		
		if (fIsAscii)
		{
			int numChars = data.length;
			boolean done = false;
			
			for (int i = 0; i < numChars && ! done; i++)
			{	
				char thisChar = (char) data[i];
				
				if (thisChar == fDelimiter)
				{
					done = true;
				}
				else
				{
					resultsBuffer.append(thisChar);
				}
			}
		}
		else
		{
			int numChars = data.length / 2;
			boolean done = false;
						
			ByteBuffer dataBuffer = ByteBuffer.wrap(data);
			
			for (int i = 0; i < numChars && ! done; i++)
			{	
				char thisChar = dataBuffer.getChar();
				
				if (thisChar == fDelimiter)
				{
					done = true;
				}
				else
				{
					resultsBuffer.append(thisChar);
				}
			}
		}
		
		if (resultsBuffer.length() > 0)
		{
			result = resultsBuffer.toString();
		}
		
		return (result);
	}

	/**
	 *  Returns the data selected by this CharDelimitedDataSelector from the given 
	 *  char array, or null if it selects nothing from the given char array.
	 *  
	 *  @para data A char array of data
	 *  @return The data selected by this CharDelimitedDataSelector from the given 
	 *  		char array, or null if it selects nothing from the given byte array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given char array
	 */
	
	protected Object select(char[] data)
		throws IllegalArgumentException
	{
		String result = null;
		
		StringBuffer resultsBuffer = new StringBuffer();
		
		int numChars = data.length;
		boolean done = false;
		
		for (int i = 0; i < numChars && ! done; i++)
		{	
			char thisChar = data[i];
			
			if (thisChar == fDelimiter)
			{
				done = true;
			}
			else
			{
				resultsBuffer.append(thisChar);
			}
		}
		
		if (resultsBuffer.length() > 0)
		{
			result = resultsBuffer.toString();
		}
		
		return (result);
	}


	/**
	 *  Returns the data selected by this CharDelimitedDataSelector from the given 
	 *  ByteBuffer, starting at its current position, as an array of bytes, or null 
	 *  if it selects nothing from the given ByteBuffer. The position of the given 
	 *  ByteBuffer will advance to the first position of the delimiter; further, 
	 *  the delimiter is not included in the selection.
	 *  
	 *  @para data A ByteBuffer of data
	 *  @return The data selected by this CharDelimitedDataSelector from the given 
	 *  		ByteBuffer, starting at its current position, as an array of bytes, or 
	 *  		null if it selects nothing from the given ByteBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given ByteBuffer
	 */
	
	protected Object select(ByteBuffer data)
		throws IllegalArgumentException
	{
		String result = null;
		
		StringBuffer resultsBuffer = new StringBuffer();
		
		int position = data.position();
		
		boolean delimiterFound = false;
		
		if (fIsAscii)
		{
			int numChars = data.remaining();
			
			for (int i = 0; i < numChars && ! delimiterFound; i++)
			{	
				char thisChar = (char) data.get();
				
				if (thisChar == fDelimiter)
				{
					delimiterFound = true;
					data.position(position + i);
				}
				else
				{
					resultsBuffer.append(thisChar);
				}
			}
		}
		else
		{
			int numChars = data.remaining() / 2;
						
			for (int i = 0; i < numChars && ! delimiterFound; i++)
			{	
				char thisChar = data.getChar();
				
				if (thisChar == fDelimiter)
				{
					delimiterFound = true;
					data.position(position + i);
				}
				else
				{
					resultsBuffer.append(thisChar);
				}
			}
		}
		
		if (! delimiterFound)
		{
			data.position(position);
			resultsBuffer = null;
		}
		
		if ((resultsBuffer != null) && (resultsBuffer.length() > 0))
		{
			result = resultsBuffer.toString();
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the data selected by this CharDelimitedDataSelector from the given 
	 *  CharBuffer, starting at its current position, as an array of chars, or null 
	 *  if it selects nothing from the given CharBuffer. The position of the given 
	 *  CharBuffer will advance to the first position of the delimiter; further, 
	 *  the delimiter is not included in the selection.
	 *  
	 *  @para data A CharBuffer of data
	 *  @return The data selected by this CharDelimitedDataSelector from the given 
	 *  		CharBuffer, starting at its current position, as an array of chars, or 
	 *  		null if it selects nothing from the given CharBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given CharBuffer
	 */
	
	protected Object select(CharBuffer data)
		throws IllegalArgumentException
	{
		String result = null;
		
		StringBuffer resultsBuffer = new StringBuffer();
		
		int numChars = data.remaining();

		int position = data.position();
		boolean delimiterFound = false;
				
		for (int i = 0; i < numChars && ! delimiterFound; i++)
		{	
			char thisChar = data.get();
			
			if (thisChar == fDelimiter)
			{
				delimiterFound = true;
				data.position(position + i);
			}
			else
			{
				resultsBuffer.append(thisChar);
			}
		}
		
		if (! delimiterFound)
		{
			data.position(position);
			resultsBuffer = null;
		}
		
		if ((resultsBuffer != null) && (resultsBuffer.length() > 0))
		{
			result = resultsBuffer.toString();
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the data selected by this CharDelimitedDataSelector from the given 
	 *  String, or null if it selects nothing from the given String.
	 *  
	 *  @para data A String of data
	 *  @return The data selected by this CharDelimitedDataSelector from the given 
	 *  		String, or null if it selects nothing from the given String
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given String
	 */
	
	protected Object select(String data)
		throws IllegalArgumentException
	{
		String result = null;
		
		StringBuffer resultsBuffer = new StringBuffer();
		
		int numChars = data.length();
		boolean done = false;
		
		for (int i = 0; i < numChars && ! done; i++)
		{	
			char thisChar = data.charAt(i);
			
			if (thisChar == fDelimiter)
			{
				done = true;
			}
			else
			{
				resultsBuffer.append(thisChar);
			}
		}
		
		if (resultsBuffer.length() > 0)
		{
			result = resultsBuffer.toString();
		}
		
		return (result);
	}
}