//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/state/DefaultDataStateDeterminer.java,v 1.4 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataStateDeterminer.java,v $
//  Revision 1.4  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.3  2006/04/27 23:31:09  chostetter_cvs
//  Added support for field value selection in determinants
//
//  Revision 1.2  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
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

import java.nio.Buffer;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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

public class DefaultDataStateDeterminer extends AbstractDataStateDeterminer
{
	private static final DataSelectorFactory sDataSelectorFactory = 
		DefaultDataSelectorFactory.getInstance();
	private static final DataComparatorFactory sDataComparatorFactory = 
		DefaultDataComparatorFactory.getInstance();
	
	private DataStateDeterminerDescriptor fDescriptor;
	
	private DataSelector fDeterminant;
	private Set fComparators;
	
	
	/**
	 * Default constructor of a new DataStateDeterminer.
	 *
	**/
	
	public DefaultDataStateDeterminer()
	{
		
	}
	

	/**
	 * Constructs a new DataStateDeterminer that will perform the data state  
	 * determination described by the given DataStateDeterminerDescriptor.
	 *
	 * @param descriptor A DataStateDeterminerDescriptor
	 * @return A new DataStateDeterminer that will perform the data state  
	 * 		determination described by the given DataStateDeterminerDescriptor		
	**/
	
	public DefaultDataStateDeterminer(DataStateDeterminerDescriptor descriptor)
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
	 * Configures this DataStateDeterminer in accordance with its current 
	 * DataStateDeterminerDescriptor
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
				fDeterminant = 
					sDataSelectorFactory.getDataSelector(determinant);
			}
			else
			{
				fDeterminant = null;
			}
			
			fComparators = new LinkedHashSet();
			
			Iterator comparators = fDescriptor.getComparators().iterator();
			
			while (comparators.hasNext())
			{
				DataComparatorDescriptor comparatorDescriptor = 
					(DataComparatorDescriptor) comparators.next();
				
				DataComparator comparator = 
					sDataComparatorFactory.getDataComparator(comparatorDescriptor);
				
				fComparators.add(comparator);
			}
		}
	}
	
	
	/**
	 *  Determines and returns the name of the current state of the given data.
	 *  
	 *	@param data A data Object
	 *  @return The name of the current state of the given data Object
	 */
	
	public String determineDataState(Object data)
	{
		String result = null;
		
		if (data instanceof Buffer)
		{
			((Buffer) data).mark();
		}
		
		Object determinantData = data;
		
		if (fDeterminant != null)
		{
			determinantData = fDeterminant.select(determinantData);
		}
		
		Iterator comparators = fComparators.iterator();
		
		boolean matches = false;
		
		while (comparators.hasNext() && ! matches)
		{
			DataComparator comparator = (DataComparator) comparators.next();
			
			matches = comparator.compare(determinantData);
			
			if (matches)
			{
				result = comparator.getName();
			}
		}
		
		if (data instanceof Buffer)
		{
			((Buffer) data).reset();
		}
		
		return (result);
	}
}
