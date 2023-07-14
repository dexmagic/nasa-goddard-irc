//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/AbstractDataSelector.java,v 1.3 2006/04/07 22:27:18 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataSelector.java,v $
//  Revision 1.3  2006/04/07 22:27:18  chostetter_cvs
//  Fixed problem with applying field formatting to all fields, tightened syntax
//
//  Revision 1.2  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
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

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.selection.description.DataSelectorDescriptor;


/**
 * A DataSelector selects data from a data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/07 22:27:18 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractDataSelector implements DataSelector
{
	private static final String CLASS_NAME = 
		AbstractDataSelector.class.getName();
	private final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private DataSelectorDescriptor fDescriptor;
	
	
	/**
	 * Default constructor of a new AbstractDataSelector.
	 *
	**/
	
	public AbstractDataSelector()
	{
		
	}
	

	/**
	 * Constructs a new DataSelector that will perform the data selection 
	 * described by the given DataSelectorDescriptor.
	 *
	 * @param descriptor A DataSelectorDescriptor
	 * @return A new DataSelector that will perform the data selection 
	 * 		described by the given DataSelectorDescriptor		
	**/
	
	public AbstractDataSelector(DataSelectorDescriptor descriptor)
	{
		fDescriptor = descriptor;
	}
	

	/**
	 *  Returns the DataSelectorDescriptor that describes the data selection 
	 *  performed by this DataSelector.
	 *  
	 *  @return The DataSelectorDescriptor that describes the data selection 
	 *  	performed by this DataSelector
	 */
	
	public DataSelectorDescriptor getDescriptor()
	{
		return (fDescriptor);
	}
	
	
	/**
	 *  Returns the data selected by this DataSelector from the given data 
	 *  Object, or null if it selects nothing from the given data Object.
	 *  
	 *  @param data The data to be selected from
	 *  @return The data selected by this DataSelector from the given data 
	 *  		Object, or null if it selects nothing from the given data Object
	 */
	
	public Object select(Object data)
	{
		if (data != null)
		{
			String message = "Unable to apply " + this + " to " + data;
			
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, "select", message);
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
		
		return (null);
	}
	
	
	/** 
	 *  Returns a String representation of this DataSelector.
	 *
	 *  @return A String representation of this DataSelector
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataSelector " + fDescriptor);
		
		return (result.toString());
	}
}
