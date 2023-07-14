//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/ByteRangeDataSelector.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ByteRangeDataSelector.java,v $
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

import gov.nasa.gsfc.irc.data.selection.description.ByteRangeDataSelectorDescriptor;


/**
 * A ByteRangeSelector performs byte-delimited data selection on a given 
 * data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public class ByteRangeDataSelector extends ByteDataSelector
{
	private int fStart = -1;
	private int fLength = -1;
	private int fEnd = -1;
	
	
	/**
	 * Constructs a new ByteRangeDataSelector that will perform the byte-range data 
	 * data selection indicated by the given ByteRangeDataSelectorDescriptor.
	 *
	 * @param descriptor A ByteRangeDataSelectorDescriptor
	 * @return A new ByteRangeDataSelector that will perform the byte-range data 
	 * data selection indicated by the given ByteRangeDataSelectorDescriptor		
	**/
	
	public ByteRangeDataSelector(ByteRangeDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fStart = descriptor.getStart();
		fLength = descriptor.getLength();
		fEnd = fStart + fLength;
	}
	

	/**
	 *  Returns the data selected by this ByteRangeDataSelector from the given 
	 *  byte array, or null if it selects nothing from the given byte array.
	 *  
	 *  @para data A byte array of data
	 *  @return The data selected by this ByteRangeDataSelector from the given 
	 *  		byte array, or null if it selects nothing from the given byte array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given byte array
	 */
	
	protected Object select(byte[] data) 
		throws IllegalArgumentException
	{
		ByteBuffer result = null;
		
		if ((fStart >= 0) && (fLength >= 0) && (data.length >= fEnd))
		{
			byte[] resultBytes = new byte[fLength];
			
			System.arraycopy(data, fStart, result, 0, fLength);
			
			result = ByteBuffer.wrap(resultBytes);
		}
		else
		{
			throw (new IllegalArgumentException());
		}
		
		return (result);
	}


	/**
	 *  Returns the data selected by this ByteRangeDataSelector from the given 
	 *  char array, or null if it selects nothing from the given char array.
	 *  
	 *  @para data A char array of data
	 *  @return The data selected by this ByteRangeDataSelector from the given 
	 *  		char array, or null if it selects nothing from the given byte array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given char array
	 */
	
	protected Object select(char[] data)
		throws IllegalArgumentException
	{
		ByteBuffer result = null;
		
		String dataString = new String(data);
		
		byte[] dataBytes = dataString.getBytes();
		
		if ((fStart >= 0) && (fLength >= 0) && (dataBytes.length >= fEnd))
		{
			byte[] resultBytes = new byte[fLength];
			
			System.arraycopy(dataBytes, fStart, result, 0, fLength);
			
			result = ByteBuffer.wrap(resultBytes);
		}
		else
		{
			throw (new IllegalArgumentException());
		}
		
		return (result);
	}


	/**
	 *  Returns the data selected by this ByteRangeDataSelector from the given 
	 *  ByteBuffer, starting at its current position, as an array of bytes, or null 
	 *  if it selects nothing from the given ByteBuffer. The position of the given 
	 *  ByteBuffer will advance by the number of bytes selected.
	 *  
	 *  @para data A ByteBuffer of data
	 *  @return The data selected by this ByteRangeDataSelector from the given 
	 *  		ByteBuffer, starting at its current position, as an array of bytes, or 
	 *  		null if it selects nothing from the given ByteBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given ByteBuffer
	 */
	
	protected Object select(ByteBuffer data)
		throws IllegalArgumentException
	{
		ByteBuffer result = null;
		
		if ((fStart >= 0) && (fLength >= 0) && (data.remaining() >= fEnd))
		{
			byte[] resultBytes = new byte[fLength];
			
			data.get(resultBytes, fStart, fLength);
			
			result = ByteBuffer.wrap(resultBytes);
		}
		else
		{
			throw (new IllegalArgumentException());
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the data selected by this ByteRangeDataSelector from the given 
	 *  String, or null if it selects nothing from the given String.
	 *  
	 *  @para data A String of data
	 *  @return The data selected by this ByteRangeDataSelector from the given 
	 *  		String, or null if it selects nothing from the given String
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given String
	 */
	
	protected Object select(String data)
		throws IllegalArgumentException
	{
		ByteBuffer result = null;
		
		byte[] dataBytes = data.getBytes();
		
		if ((fStart >= 0) && (fLength >= 0) && (dataBytes.length >= fEnd))
		{
			byte[] resultBytes = new byte[fLength];
			
			System.arraycopy(dataBytes, fStart, resultBytes, 0, fLength);
			
			result = ByteBuffer.wrap(resultBytes);
		}
		else
		{
			throw (new IllegalArgumentException());
		}
		
		return (result);
	}
}
