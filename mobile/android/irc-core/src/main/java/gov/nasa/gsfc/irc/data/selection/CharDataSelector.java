//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/CharDataSelector.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: CharDataSelector.java,v $
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

import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.selection.description.CharDataSelectorDescriptor;


/**
 * A CharDataSelector performs char-based data selection on a given data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public abstract class CharDataSelector extends ByteDataSelector
{
	private static final String CLASS_NAME = 
		CharDataSelector.class.getName();
	private final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private boolean fIsAscii = false;


	/**
	 * Constructs a new CharDataSelector that will perform the char-based data 
	 * data selection indicated by the given CharDataSelectorDescriptor.
	 *
	 * @param descriptor A CharDataSelectorDescriptor
	 * @return A new CharDataSelector that will perform the char-based data 
	 * 		selection indicated by the given CharDataSelectorDescriptor		
	**/
	
	public CharDataSelector(CharDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		configureFromDescriptor();
	}
	

	/**
	 *  Configures this CharDelimitedDataSelector in accordance with its current 
	 *  CharDelimitedDataSelectorDescriptor.
	 *  
	 */
	
	private void configureFromDescriptor()
	{
		fIsAscii = ((CharDataSelectorDescriptor) getDescriptor()).isAscii();
	}
	
	
	/**
	 * Returns true if this CharDataSelectorDescriptor is configured to select 
	 * only ASCII data, false otherwise (i.e., if it selects Unicode data).
	 *
	 * @return True if this CharDataSelectorDescriptor is configured to select 
	 * 		only ASCII data, false otherwise (i.e., if it selects Unicode data)		
	**/
	
	public boolean isAscii()
	{
		return (fIsAscii);
	}
	
	
	/**
	 *  Returns the data selected by this CharDataSelector from the given data 
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
				if (data instanceof CharBuffer)
				{
					result = select((CharBuffer) data);
				}
				else
				{
					result = super.select(data);
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
	 *  Returns the data selected by this CharDataSelector from the given 
	 *  CharBuffer, starting at its current position, as an array of chars, or null 
	 *  if it selects nothing from the given CharBuffer. The position of the given 
	 *  CharBuffer will advance to the first position beyond the selected data.
	 *  
	 *  @para data A CharBuffer of data
	 *  @return The data selected by this CharDataSelector from the given 
	 *  		CharBuffer, starting at its current position, as an array of chars, or 
	 *  		null if it selects nothing from the given CharBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given CharBuffer
	 */
	
	protected abstract Object select(CharBuffer data)
		throws IllegalArgumentException;
	
	
	/** 
	 *  Returns a String representation of this CharDataSelector.
	 *
	 *  @return A String representation of this CharDataSelector
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("CharDataSelector:");
		result.append("\n" + super.toString());
		result.append("\nIs ASCII: " + fIsAscii);
		
		return (result.toString());
	}	
}
