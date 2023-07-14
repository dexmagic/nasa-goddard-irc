//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/AbstractBufferedDataSelector.java,v 1.1 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractBufferedDataSelector.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.2  2006/03/31 21:57:38  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
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


/**
 * A BufferedDataSelector selects data in a buffered fashion.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractBufferedDataSelector implements BufferedDataSelector
{
	private static final String CLASS_NAME = 
		AbstractBufferedDataSelector.class.getName();
	private final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static final DataSelectorFactory sDataSelectorFactory = 
		DefaultDataSelectorFactory.getInstance();
	
	private int fSize = 0;
	private DataSelector fDataSelector;
	
	private ByteBuffer fDataBuffer;
	
	private boolean fHasRemainingData = false;
	
	
	/**
	 * Constructs a new AbstractBufferedDataSelector that will buffer all 
	 * selection data into a buffer of the given size and apply the given 
	 * DataSelector to the buffer.
	 *
	 * @param selector A DataSelector
	 * @param size The size of the data selection buffer
	 * @return A new AbstractBufferedDataSelector that will buffer all 
	 * 		selection data into a buffer of the given size and apply the given 
	 * 		DataSelector to the buffer		
	**/
	
	public AbstractBufferedDataSelector(DataSelector selector, int size)
	{
		fDataSelector = selector;
		
		fSize = size;
		
		if (fSize > 0)
		{
			fDataBuffer = ByteBuffer.allocate(fSize);
			fDataBuffer.limit(0);
		}
	}
	

	/**
	 *  Appends the given data to the data buffer.
	 *  
	 *  @param data The data to be appended to the data buffer
	 */
	
	private void append(ByteBuffer data)
	{
		int inputSize = data.remaining();
		
		if (fDataBuffer == null)
		{
			fDataBuffer = ByteBuffer.allocate(inputSize);
		}
		else
		{
			ByteBuffer currentBuffer = fDataBuffer;
			
			int remaining = currentBuffer.remaining();
			
			int position = currentBuffer.position();
			int limit = currentBuffer.limit();
			int capacity = currentBuffer.capacity();
			
			int free = capacity - limit;
			
			if (inputSize > free)
			{
				fDataBuffer = ByteBuffer.allocate(limit + inputSize);
				fDataBuffer.put(currentBuffer);
			}
			else
			{
				fDataBuffer.limit(capacity);
				fDataBuffer.position(position + remaining);
			}
		}
		
		fDataBuffer.put(data);
		fDataBuffer.flip();
	}
	
	
	/**
	 *  Appends the given data to the back of the data buffer.
	 *  
	 *  @param data The data to be append to the back of the data buffer
	 */
	
	private void append(Object data)
	{
		if (data != null)
		{
			if (data instanceof ByteBuffer)
			{
				append((ByteBuffer) data);
			}
			else if (data instanceof byte[])
			{
				ByteBuffer dataBytes = ByteBuffer.wrap((byte[]) data);
				
				append(dataBytes);
			}
			else if (data instanceof String)
			{
				ByteBuffer dataBytes = 
					ByteBuffer.wrap(((String) data).getBytes());
				
				append(dataBytes);
			}
			else if (data instanceof char[])
			{
				ByteBuffer dataBytes = 
					ByteBuffer.wrap(new String((char[]) data).getBytes());
				
				append(dataBytes);
			}
			else
			{
				ByteBuffer dataBytes = 
					ByteBuffer.wrap((data.toString()).getBytes());
				
				append(dataBytes);
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
	}


	/**
	 *  Pushes the given data back onto the front of the data buffer.
	 *  
	 *  @param data The data to be pushed back onto the front of the data buffer
	 */
	
	private void pushback(ByteBuffer data)
	{
		fDataBuffer.clear();
		fDataBuffer.put(data);
		fDataBuffer.flip();
	}
	
	
	/**
	 *  Returns true if this BufferedDataSelector has data remaining in it 
	 *  after a select(), false otherwise.
	 *  
	 *  @return True if this BufferedDataSelector has data remaining in it 
	 *  	after a select(), false otherwise
	 */
	
	public boolean hasRemainingData()
	{
		return (fHasRemainingData);
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.selection.DataSelector#select()
	 */
	
	public Object select(Object data)
	{
		ByteBuffer result = null;
		
		if (data != null)
		{
			append(data);
		}
		
		Object selection = fDataSelector.select(fDataBuffer);
		
		if (selection != null)
		{
			if (selection instanceof String)
			{
				String resultString = (String) selection;
				byte[] resultBytes = resultString.getBytes();
				result = ByteBuffer.wrap(resultBytes);
			}
			else
			{
				result = (ByteBuffer) selection;
			}
			
			int amountUnselected = fDataBuffer.remaining();
			
			if (amountUnselected > 0)
			{
				byte[] unselectedBytes = new byte[amountUnselected];
				fDataBuffer.get(unselectedBytes);
				ByteBuffer unselectedData = ByteBuffer.wrap(unselectedBytes);
				pushback(unselectedData);
				
				fHasRemainingData = true;
			}
			else
			{
				fDataBuffer.clear();
				fDataBuffer.limit(0);
				
				fHasRemainingData = false;
			}
		}
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this AbstractBufferedDataSelector.
	 *
	 *  @return A String representation of this AbstractBufferedDataSelector
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("BufferedDataSelector:");
		result.append("\n" + fDataSelector);
		
		return (result.toString());
	}
}
