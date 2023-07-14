//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/ByteDataSelector.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ByteDataSelector.java,v $
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
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.selection.description.ByteDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectorDescriptor;


/**
 * A ByteDataSelector performs byte-based data selection on a given data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public abstract class ByteDataSelector implements DataSelector
{
	private static final String CLASS_NAME = 
		ByteDataSelector.class.getName();
	private final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private DataSelectorDescriptor fDescriptor;
	

	/**
	 * Constructs a new ByteDataSelector that will perform the byte-based data 
	 * data selection indicated by the given ByteDataSelectorDescriptor.
	 *
	 * @param descriptor A ByteDataSelectorDescriptor
	 * @return A new ByteDataSelector that will perform the byte-based data 
	 * 		selection indicated by the given ByteDataSelectorDescriptor		
	**/
	
	public ByteDataSelector(ByteDataSelectorDescriptor descriptor)
	{
		fDescriptor = descriptor;
	}
	

	/**
	 *  Returns the ByteDataSelectorDescriptor associated with this ByteDataSelector 
	 *  (if any).
	 *  
	 *  @return The ByteDataSelectorDescriptor associated with this ByteDataSelector 
	 *  		(if any)
	 */
	
	public DataSelectorDescriptor getDescriptor()
	{
		return (fDescriptor);
	}
	
	
	/**
	 *  Returns the data selected by this DataSelector from the given data 
	 *  Object, or null if it selects nothing from the given data Object.
	 *  
	 *  @return The data selected by this DataSelector from the given data 
	 *  		Object, or null if it selects nothing from the given data Object
	 */
	
	public Object select(Object data)
	{
		Object result = null;
		
		if (data != null)
		{
			try
			{
				if (data instanceof ByteBuffer)
				{
					result = select((ByteBuffer) data);
				}
				else if (data instanceof byte[])
				{
					result = select((byte[]) data);
				}
				else if (data instanceof char[])
				{
					result = select((char[]) data);
				}
				else if (data instanceof String)
				{
					result = select((String) data);
				}
				else
				{
					throw (new IllegalArgumentException());
				}
			}
			catch (IllegalArgumentException ex)
			{
				String message = "Unable to apply this selector to the given data: " + 
				data;
		
				if (sLogger.isLoggable(Level.SEVERE))
				{
					sLogger.logp(Level.SEVERE, CLASS_NAME, "select", message);
				}
			}
		}
		else
		{
			String message = "The given data is null; skipping...";
			
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, "select", message);
			}
		}
		
		return (result);
	}


	/**
	 *  Returns the data selected by this ByteDataSelector from the given 
	 *  byte array, or null if it selects nothing from the given byte array.
	 *  
	 *  @para data A byte array of data
	 *  @return The data selected by this ByteDataSelector from the given 
	 *  		byte array, or null if it selects nothing from the given byte 
	 *  		array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given byte array
	 */
	
	protected abstract Object select(byte[] data) 
		throws IllegalArgumentException;


	/**
	 *  Returns the data selected by this ByteDataSelector from the given 
	 *  char array, or null if it selects nothing from the given char array.
	 *  
	 *  @para data A char array of data
	 *  @return The data selected by this ByteDataSelector from the given 
	 *  		char array, or null if it selects nothing from the given byte array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given char array
	 */
	
	protected abstract Object select(char[] data)
		throws IllegalArgumentException;


	/**
	 *  Returns the data selected by this ByteDataSelector from the given 
	 *  ByteBuffer, starting at its current position, as an array of bytes, or null 
	 *  if it selects nothing from the given ByteBuffer. The position of the given 
	 *  ByteBuffer will advance to the first position beyond the selected data.
	 *  
	 *  @para data A ByteBuffer of data
	 *  @return The data selected by this ByteDataSelector from the given 
	 *  		ByteBuffer, starting at its current position, as an array of bytes, or 
	 *  		null if it selects nothing from the given ByteBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given ByteBuffer
	 */
	
	protected abstract Object select(ByteBuffer data)
		throws IllegalArgumentException;
	
	
	/**
	 *  Returns the data selected by this ByteDataSelector from the given 
	 *  String, or null if it selects nothing from the given String.
	 *  
	 *  @para data A String of data
	 *  @return The data selected by this ByteDataSelector from the given 
	 *  		String, or null if it selects nothing from the given String
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given String
	 */
	
	protected abstract Object select(String data)
		throws IllegalArgumentException;
}
