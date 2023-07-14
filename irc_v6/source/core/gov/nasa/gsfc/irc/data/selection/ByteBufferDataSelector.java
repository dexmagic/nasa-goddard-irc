//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/ByteBufferDataSelector.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ByteBufferDataSelector.java,v $
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

import gov.nasa.gsfc.irc.data.selection.description.DataSelectorDescriptor;


/**
 * A ByteBufferDataSelector performs data selection from a given ByteBuffer of 
 * data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public abstract class ByteBufferDataSelector extends AbstractDataSelector
{
	private static final String CLASS_NAME = 
		ByteBufferDataSelector.class.getName();
	private final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	
	/**
	 * Constructs a new ByteBufferDataSelector that will perform the ByteBuffer 
	 * data selection indicated by the given AbstractDataSelectorDescriptor.
	 *
	 * @param descriptor A AbstractDataSelectorDescriptor
	 * @return A new ByteBufferDataSelector that will perform the ByteBuffer 
	 * 		data selection indicated by the given 
	 		AbstractDataSelectorDescriptor		
	**/
	
	public ByteBufferDataSelector(DataSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 *  Returns the data selected by this ByteBufferDataSelector from the given 
	 *  data Object, which must be a ByteBuffer, or null if it selects nothing 
	 *  from the given data Object.
	 *  
	 *  @param data The data from which this ByteBufferDataSelector is to 
	 *  	select, which must be a ByteBuffer
	 *  @return The data selected by this ByteBufferDataSelector from the given 
	 *  	data Object, or null if it selects nothing from the given data 
	 *  	Object
	 */
	
	public Object select(Object data)
	{
		Object result = null;
		
		if (data != null)
		{
			if (data instanceof ByteBuffer)
			{
				result = select((ByteBuffer) data);
			}
			else
			{
				String message = "Unable to apply this selector to the given data " + 
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
	 *  Returns the data selected by this ByteBufferDataSelector from the given 
	 *  ByteByffer, or null if it selects nothing.
	 *  
	 *  @param data The ByteBuffer from which this ByteBufferDataSelector is to 
	 *  	select
	 *  @return The data selected by this ByteBufferDataSelector from the given 
	 *  	ByteBuffer, or null if it selects nothing from the given ByteBuffer
	 */
	
	protected abstract Object select(ByteBuffer data);
}
