//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/RegExPatternDataSelector.java,v 1.2 2005/09/16 00:29:37 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: RegExPatternDataSelector.java,v $
//  Revision 1.2  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.gsfc.irc.data.selection.description.RegExPatternDataSelectorDescriptor;


/**
 * A RegExPatternDataSelector performs Pattern matching on a data Object
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/16 00:29:37 $
 * @author Carl F. Hostetter
 */

public class RegExPatternDataSelector extends CharDataSelector
{
	private boolean fIsAscii;
	private Pattern fPattern;
	private int fGroupNumber = 0;
	
	
	/**
	 * Constructs a new RegExPatternDataSelector that will perform the regex Pattern 
	 * matching selection indicated by the given RegExPatternDataSelectorDescriptor.
	 *
	 * @param descriptor A RegExPatternDataSelectorDescriptor
	 * @return A new RegExPatternDataSelector that will perform the regex Pattern  
	 * 		matching selection indicated by the given 
	 * 		RegExPatternDataSelectorDescriptor		
	**/
	
	public RegExPatternDataSelector(RegExPatternDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fIsAscii = isAscii();
		fPattern = descriptor.getPattern();
		fGroupNumber = descriptor.getGroupNumber();
	}
	

	/**
	 *  Returns the data selected by this RegExPatternDataSelector from the given 
	 *  byte array, or null if it selects nothing from the given byte array.
	 *  
	 *  @para data A byte array of data
	 *  @return The data selected by this RegExPatternDataSelector from the given 
	 *  		byte array, or null if it selects nothing from the given byte 
	 *  		array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given byte array
	 */
	
	protected Object select(byte[] data) 
		throws IllegalArgumentException
	{
		String result = null;
		
		char[] dataChars = null;
		
		if (fIsAscii)
		{
			int numChars = data.length;
			dataChars = new char[numChars];
			
			for (int i = 0; i < numChars; i++)
			{	
				dataChars[i] = (char) data[i];
			}
		}
		else
		{
			int numChars = data.length / 2;
			dataChars = new char[numChars];
			
			ByteBuffer dataBuffer = ByteBuffer.wrap(data);
			
			for (int i = 0; i < numChars; i++)
			{
				dataChars[i] = dataBuffer.getChar();
			}
		}
		
		if (dataChars != null)
		{
			String dataString = new String(dataChars);
			
			Matcher matcher = fPattern.matcher(dataString);
			
			if (matcher.lookingAt())
			{
				result = matcher.group(fGroupNumber);
			}
		}
		
		return (result);
	}



	/**
	 *  Returns the data selected by this RegExPatternDataSelector from the given 
	 *  char array, or null if it selects nothing from the given char array.
	 *  
	 *  @para data A char array of data
	 *  @return The data selected by this RegExPatternDataSelector from the given 
	 *  		char array, or null if it selects nothing from the given byte array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given char array
	 */
	
	protected Object select(char[] data)
		throws IllegalArgumentException
	{
		return (select(new String(data)));
	}


	/**
	 *  Returns the data selected by this RegExPatternDataSelector from the given 
	 *  ByteBuffer, starting at its current position, as an array of bytes, or null 
	 *  if it selects nothing from the given ByteBuffer. The position of the given 
	 *  ByteBuffer will advance to the first position beyond the selected data.
	 *  
	 *  @para data A ByteBuffer of data
	 *  @return The data selected by this RegExPatternDataSelector from the given 
	 *  		ByteBuffer, starting at its current position, as an array of bytes, or 
	 *  		null if it selects nothing from the given ByteBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given ByteBuffer
	 */
	
	protected Object select(ByteBuffer data)
		throws IllegalArgumentException
	{
		String result = null;
		
		int numBytes = data.remaining();
			
		if (numBytes > 0)
		{
			char[] dataChars = null;
			
			int position = data.position();

			if (fIsAscii)
			{
				int numChars = data.remaining();
				dataChars = new char[numChars];
				
				for (int i = 0; i < numChars; i++)
				{	
					dataChars[i] = (char) data.get();
				}
			}
			else
			{
				int numChars = data.remaining() / 2;
				dataChars = new char[numChars];
				
				for (int i = 0; i < numChars; i++)
				{
					dataChars[i] = data.getChar();
				}
			}
			
			data.position(position);
			
			String dataString = new String(dataChars);
			
			Matcher matcher = fPattern.matcher(dataString);
			
			if (matcher.lookingAt())
			{
				result = matcher.group(fGroupNumber);
				
				int remainderStart = matcher.end();
				
				if (! fIsAscii)
				{
					remainderStart *= 2;
				}
				
				data.position(position + remainderStart);
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the data selected by this RegExPatternDataSelector from the given 
	 *  CharBuffer, starting at its current position, as an array of chars, or null 
	 *  if it selects nothing from the given CharBuffer. The position of the given 
	 *  CharBuffer will advance to the first position beyond the selected data.
	 *  
	 *  @para data A CharBuffer of data
	 *  @return The data selected by this RegExPatternDataSelector from the given 
	 *  		CharBuffer, starting at its current position, as an array of chars, or 
	 *  		null if it selects nothing from the given CharBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given CharBuffer
	 */
	
	protected Object select(CharBuffer data)
		throws IllegalArgumentException
	{
		String result = null;
		
		int numBytes = data.remaining();
			
		if (numBytes > 0)
		{
			int position = data.position();

			String dataString = data.toString();
			
			Matcher matcher = fPattern.matcher(dataString);
			
			if (matcher.lookingAt())
			{
				result = matcher.group(fGroupNumber);
				
				int remainderStart = matcher.end();
				
				data.position(position + remainderStart);
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the data selected by this RegExPatternDataSelector from the given 
	 *  String, or null if it selects nothing from the given String.
	 *  
	 *  @para data A String of data
	 *  @return The data selected by this RegExPatternDataSelector from the given 
	 *  		String, or null if it selects nothing from the given String
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given String
	 */
	
	protected Object select(String data)
		throws IllegalArgumentException
	{
		String result = null;
		
		Matcher matcher = fPattern.matcher(data);
		
		if (matcher.lookingAt())
		{
			result = matcher.group(fGroupNumber);
		}
		
		return (result);
	}
}
