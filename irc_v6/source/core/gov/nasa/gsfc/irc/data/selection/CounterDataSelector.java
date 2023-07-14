//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/CounterDataSelector.java,v 1.1 2005/09/15 15:34:08 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: CounterDataSelector.java,v $
//  Revision 1.1  2005/09/15 15:34:08  chostetter_cvs
//  Added support for command counter value selection
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

import gov.nasa.gsfc.irc.data.selection.description.CounterDataSelectorDescriptor;


/**
 * A CounterDataSelector selects the current value of a named, persistent counter 
 * that increments itself each time it is selected.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/15 15:34:08 $
 * @author Carl F. Hostetter
 */

public class CounterDataSelector extends AbstractDataSelector
{
	private CounterDataSelectorDescriptor fDescriptor;
	private long fCount = 0;
	
	
	/**
	 * Constructs a new CounterDataSelector that will select the name of any given 
	 * named data Object.
	 *
	 * @param descriptor A DataSelectorDescriptor
	 * @return A new CounterDataSelector that will select the name of any given 
	 * 		named data Object		
	**/
	
	public CounterDataSelector(CounterDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		configureFromDescriptor();
	}
	

	/**
	 *  Configures this CounterDataSelector in accordance with its current 
	 *  CounterDataSelectorDescriptor.
	 *  
	 */
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			fCount = fDescriptor.getInitialCount();
		}
	}
	
	
	/**
	 *  Returns the current value of this CounterDataSelector as a Long.
	 *  
	 *  @param data A named data Object
	 *  @return The current value of this CounterDataSelector as a Long
	 */
	
	public Object select(Object data)
	{
		fCount++;
		
		Long result = new Long(fCount);
		
		return (result);
	}
}
