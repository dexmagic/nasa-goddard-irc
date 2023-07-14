//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/ByteDelimitedDataSelector.java,v 1.2 2006/01/25 17:02:23 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ByteDelimitedDataSelector.java,v $
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
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.selection.description.ByteDelimitedDataSelectorDescriptor;


/**
 * A ByteDelimitedSelector performs byte-delimited data selection from a given 
 * ByteBuffer of data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/25 17:02:23 $
 * @author Carl F. Hostetter
 */

public class ByteDelimitedDataSelector extends ByteBufferDataSelector
{
	private static final String CLASS_NAME = 
		ByteDelimitedDataSelector.class.getName();
	private final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private ByteDelimitedDataSelectorDescriptor fDescriptor;
	private byte fDelimiter;
	
	
	/**
	 * Constructs a new ByteDelimitedSelector that will perform the 
	 * byte-delimited data selection indicated by the given 
	 * ByteDelimitedDataSelectorDescriptor.
	 *
	 * @param descriptor A ByteDelimitedDataSelectorDescriptor
	 * @return A new ByteDelimitedSelector that will perform the 
	 * 		byte-delimited data selection indicated by the given 
	 * 		ByteDelimitedDataSelectofDescriptor		
	**/
	
	public ByteDelimitedDataSelector
		(ByteDelimitedDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		setDescriptor(descriptor);
	}
	
	
	/**
	 *  Sets the ByteDelimitedDataSelectorDescriptor of this 
	 *  ByteDelimitedDataSelector to the given ByteDelimitedDataSelectorDescriptor, 
	 *  and reconfigures this ByteDelimitedDataSelector accordingly.
	 *  
	 *  @param
	 */
	
	public void setDescriptor(ByteDelimitedDataSelectorDescriptor descriptor)
	{
		if (descriptor != null)
		{
			fDescriptor = descriptor;
			
			configureFromDescriptor();
		}
	}
	
	
	/**
	 *  Configures this ByteDelimitedDataSelector in accordance with its current 
	 *  ByteDelimitedDataSelectorDescriptor.
	 *  
	 */
	
	private void configureFromDescriptor()
	{
		fDelimiter = fDescriptor.getDelimiter();
	}
	

	/**
	 *  Returns the data selected by this DataSelector from the given ByteByffer, 
	 *  or null if it selects nothing. The position of the given ByteBuffer will 
	 *  advance to the first position of the delimiter; further, the delimiter is 
	 *  not included in the selection.
	 *  
	 *  @param data The ByteBuffer from which this ByteDelimitedSelector is to select
	 *  @return The data selected by this DataSelector from the given data 
	 *  		Object, or null if it selects nothing from the given data Object
	 */
	
	protected Object select(ByteBuffer data)
	{
		ByteBuffer result = null;
		
		if (data != null)
		{
			int numBytes = data.capacity();
			result = ByteBuffer.allocate(numBytes);
			
			int position = data.position();
			boolean delimiterFound = false;
			
			for (int i = 0; i < numBytes && ! delimiterFound; i++)
			{
				byte thisByte = data.get();
				
				if (thisByte == fDelimiter)
				{
					delimiterFound = true;
					data.position(position + i);
				}
				else
				{
					result.put(thisByte);
				}
			}
			
			if (! delimiterFound)
			{
				data.position(position);
				result = null;
			}
			
			if (result != null)
			{
				result.flip();
			}
		}
		else
		{
			String message = "The given data is null";
			
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, "select", message);
			}
		}
		
		return (result);
	}
}
