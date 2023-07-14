//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/CharRangeDataSelector.java,v 1.2 2005/09/29 18:18:23 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: CharRangeDataSelector.java,v $
//  Revision 1.2  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
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

import gov.nasa.gsfc.irc.data.selection.description.CharRangeDataSelectorDescriptor;


/**
 * A CharRangeSelector performs char-range data selection on a given data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/29 18:18:23 $
 * @author Carl F. Hostetter
 */

public class CharRangeDataSelector extends CharDataSelector
{
	private int fStart = -1;
	private int fLength = -1;
	private int fEnd = -1;
	
	private boolean fIsAscii = false;
	
	
	/**
	 * Constructs a new CharRangeDataSelector that will perform the char-range data 
	 * data selection indicated by the given CharRangeDataSelectorDescriptor.
	 *
	 * @param descriptor A CharRangeDataSelectorDescriptor
	 * @return A new CharRangeDataSelector that will perform the char-range data 
	 * 		data selection indicated by the given CharRangeDataSelectorDescriptor		
	**/
	
	public CharRangeDataSelector(CharRangeDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fStart = descriptor.getStart();
		fLength = descriptor.getLength();
		fEnd = fStart + fLength;
		
		fIsAscii = descriptor.isAscii();
	}
	

	/**
	 *  Returns the data selected by this CharRangeDataSelector from the given 
	 *  byte array, or null if it selects nothing from the given byte array.
	 *  
	 *  @para data A byte array of data
	 *  @return The data selected by this CharRangeDataSelector from the given 
	 *  		byte array, or null if it selects nothing from the given byte array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given byte array
	 */
	
	protected Object select(byte[] data) 
		throws IllegalArgumentException
	{
		String result = null;
		
		char[] resultChars = null;
		
		if ((fStart >= 0) && (fLength >= 0) && (data.length >= fEnd))
		{
			resultChars = new char[fLength];
			
			if (fIsAscii)
			{
				for (int i = 0, j = fStart; i < fLength; i++, j++)
				{	
					resultChars[i] = (char) data[j];
				}
			}
			else
			{
				int start = fStart * 2;
				int length = fStart * 2;
				
				ByteBuffer dataBuffer = ByteBuffer.wrap(data, start, length);
				
				for (int i = 0; i < fLength; i++)
				{
					resultChars[i] = dataBuffer.getChar();
				}
			}
		}
		else
		{
			throw (new IllegalArgumentException());
		}
		
		if (resultChars != null)
		{
			result = new String(resultChars);
		}
		
		return (result);
	}


	/**
	 *  Returns the data selected by this CharRangeDataSelector from the given 
	 *  char array, or null if it selects nothing from the given char array.
	 *  
	 *  @para data A char array of data
	 *  @return The data selected by this CharRangeDataSelector from the given 
	 *  		char array, or null if it selects nothing from the given byte array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given char array
	 */
	
	protected Object select(char[] data)
	{
		String result = null;
		
		char[] resultChars = null;
		
		if ((fStart >= 0) && (fLength >= 0) && (data.length >= fEnd))
		{
			resultChars = new char[fLength];
			
			System.arraycopy(data, fStart, result, 0, fLength);
		}
		else
		{
			throw (new IllegalArgumentException());
		}
		
		if (resultChars != null)
		{
			result = new String(resultChars);
		}
		
		return (result);
	}


	/**
	 *  Returns the data selected by this CharRangeDataSelector from the given 
	 *  ByteBuffer, starting at its current position, as an array of chars, or null 
	 *  if it selects nothing from the given ByteBuffer. The position of the given 
	 *  ByteBuffer will advance by the number of chars selected.
	 *  
	 *  @para data A ByteBuffer of data
	 *  @return The data selected by this CharRangeDataSelector from the given 
	 *  		ByteBuffer, starting at its current position, as an array of chars, or 
	 *  		null if it selects nothing from the given ByteBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given ByteBuffer
	 */
	
	protected Object select(ByteBuffer data)
	{
		String result = null;
		
		char[] resultChars = null;
		
		if ((fStart >= 0) && (fLength >= 0) && (data.remaining() >= fEnd))
		{
			data.position(fStart);
			
			if (fIsAscii)
			{
				for (int i = 0; i < fLength; i++)
				{
					resultChars[i] = (char) data.get();
				}
			}
			else
			{
				for (int i = 0; i < fLength; i++)
				{
					resultChars[i] = data.getChar();
				}
			}
		}
		else
		{
			throw (new IllegalArgumentException());
		}
		
		if (resultChars != null)
		{
			result = new String(resultChars);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the data selected by this CharRangeDataSelector from the given 
	 *  CharBuffer, starting at its current position, as an array of chars, or null 
	 *  if it selects nothing from the given CharBuffer. The position of the given 
	 *  CharBuffer will advance by the number of chars selected.
	 *  
	 *  @para data A ByteBuffer of data
	 *  @return The data selected by this CharRangeDataSelector from the given 
	 *  		CharBuffer, starting at its current position, as an array of chars, or 
	 *  		null if it selects nothing from the given CharBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given CharBuffer
	 */
	
	protected Object select(CharBuffer data)
	{
		String result = null;
		
		char[] resultChars = null;
		
		if ((fStart >= 0) && (fLength >= 0) && (data.remaining() >= fEnd))
		{
			resultChars = new char[fLength];
			data.get(resultChars, fStart, fLength);
		}
		else
		{
			throw (new IllegalArgumentException());
		}
		
		if (resultChars != null)
		{
			result = new String(resultChars);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the data selected by this CharRangeDataSelector from the given 
	 *  String, or null if it selects nothing from the given String.
	 *  
	 *  @para data A String of data
	 *  @return The data selected by this CharRangeDataSelector from the given 
	 *  		String, or null if it selects nothing from the given String
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given String
	 */
	
	protected Object select(String data)
	{
		String result = null;
		
		if ((fStart >= 0) && (fLength >= 0) && (data.length() >= fEnd))
		{
			result = data.substring(fStart, fEnd);
		}
		else
		{
			throw (new IllegalArgumentException());
		}
		
		return (result);
	}
}
