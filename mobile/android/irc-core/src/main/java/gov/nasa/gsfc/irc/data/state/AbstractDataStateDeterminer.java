//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/state/AbstractDataStateDeterminer.java,v 1.3 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataStateDeterminer.java,v $
//  Revision 1.3  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

package gov.nasa.gsfc.irc.data.state;

import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.selection.DataSelectorFactory;
import gov.nasa.gsfc.irc.data.selection.DefaultDataSelectorFactory;


/**
 * A DataStateDeterminer determines the current state of a sequence of data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractDataStateDeterminer implements DataStateDeterminer
{
	private static final DataSelectorFactory sDataSelectorFactory =
		DefaultDataSelectorFactory.getInstance();
	
	private DataStateDeterminerDescriptor fDescriptor;
	
	private DataSelector fDataSelector;
	
	
	/**
	 * Default constructor of a new AbstractDataStateDeterminer.
	 *
	**/
	
	public AbstractDataStateDeterminer()
	{
		
	}
	

	/**
	 * Constructs a new AbstractDataStateDeterminer that will perform the data 
	 * state determination described by the given DataStateDeterminerDescriptor.
	 *
	 * @param descriptor A DataStateDeterminerDescriptor
	 * @return A new AbstractDataStateDeterminer that will perform the data state 
	 * 		determination described by the given DataStateDeterminerDescriptor		
	**/
	
	public AbstractDataStateDeterminer(DataStateDeterminerDescriptor descriptor)
	{
		setDescriptor(descriptor);
	}
	

	/**
	 * Sets the DataStateDeterminerDescriptor associated with this 
	 * DataStateDeterminer to the given DataStateDeterminerDescriptor, and 
	 * configures this DataStateDeterminer in accordance with it.
	 *
	 * @param descriptor The DataStateDeterminerDescriptor according to which to 
	 * 		configure this DataStateDeterminer
	**/
	
	public void setDescriptor(DataStateDeterminerDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this AbstractDataStateDeterminer in accordance with its current 
	 * DataStateDeterminerDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			DataStateDeterminantDescriptor determinant = 
				fDescriptor.getDeterminant();
			
			if (determinant != null)
			{
				fDataSelector = sDataSelectorFactory.getDataSelector
					(determinant);
			}
		}
	}
	
	
	/**
	 *  Returns the DataStateDeterminerDescriptor that describes the data parsing 
	 *  performed by this DataStateDeterminer.
	 *  
	 *  @return The DataStateDeterminerDescriptor that describes the data parsing 
	 *  		performed by this DataStateDeterminer
	 */
	
	public DataStateDeterminerDescriptor getDescriptor()
	{
		return (fDescriptor);
	}
	
	
	/**
	 *  Returns the DataSelector associated with this DataStateDeterminer (if any).
	 *  
	 *  @return The DataSelector associated with this DataStateDeterminer (if any)
	 */
	
	public DataSelector getDataSelector()
	{
		return (fDataSelector);
	}
	
	
	/** 
	 *  Returns a String representation of this DataStateDeterminer.
	 *
	 *  @return A String representation of this DataStateDeterminer
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataStateDeterminer: ");
		result.append("\n" + fDescriptor);
		
		return (result.toString());
	}
}
