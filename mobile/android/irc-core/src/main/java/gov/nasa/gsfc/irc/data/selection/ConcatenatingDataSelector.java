//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/ConcatenatingDataSelector.java,v 1.3 2006/06/01 22:22:43 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ConcatenatingDataSelector.java,v $
//  Revision 1.3  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.2  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.selection.description.DataConcatenationDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectorDescriptor;


/**
 * A ConcatenatingDataSelector performs two or more data selections and 
 * concatenations the results together.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $
 * @author Carl F. Hostetter
 */

public class ConcatenatingDataSelector implements DataSelector
{
	private static final String CLASS_NAME = 
		ConcatenatingDataSelector.class.getName();
	private final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	DataSelectorFactory fDataSelectorFactory = 
		DefaultDataSelectorFactory.getInstance();
	
	private Set fDataSelectors = new LinkedHashSet();
	private String fSeparator;
	
	
	/**
	 * Constructs a new ConcatenatingDataSelector that will perform the data 
	 * concatenation described by the given DataConcatenationDescriptor.
	 *
	 * @param descriptor A DataConcatenationDescriptor
	 * @return A new ConcatenatingDataSelector that will perform the data 
	 * 		concatenation described by the given DataConcatenationDescriptor
	**/
	
	public ConcatenatingDataSelector(DataConcatenationDescriptor descriptor)
	{
		configureFromDescriptor(descriptor);
	}
	

	/**
	 *  Configures this ConcatenatingDataSelector according to the given 
	 *  DataConcatenationDescriptor
	 *  
	 *  @param descriptor A DataConcatenationDescriptor
	 */
	
	private void configureFromDescriptor(DataConcatenationDescriptor descriptor)
	{
		fDataSelectors.clear();
		fSeparator = descriptor.getSeparator();
		
		Iterator selectors = descriptor.getDataSelectors().iterator();
		
		while (selectors.hasNext())
		{
			DataSelectorDescriptor selectorDescriptor = 
				(DataSelectorDescriptor) selectors.next();
				
			DataSelector selector = 
				fDataSelectorFactory.getDataSelector(selectorDescriptor);
				
			fDataSelectors.add(selector);
		}
	}
	
	
	/**
	 *  Returns the set of DataSelectors associated with this 
	 *  ConcatenatingDataSelector.
	 *  
	 *  @return The set of DataSelectors associated with this 
	 *  	ConcatenatingDataSelector
	 */
	
	public Set getDataSelectors()
	{
		return (Collections.unmodifiableSet(fDataSelectors));
	}
	
	
	/**
	 *  Returns the separator defined on this ConcatenatingDataSelector.
	 *  
	 *  @return The separator defined on this ConcatenatingDataSelector
	 */
	
	public String getSeparator()
	{
		return (fSeparator);
	}
	
	
	/**
	 *  Returns the data selected by this ConcatenatingDataSelector from the 
	 *  given data Object, or null if it selects nothing from the given data 
	 *  Object.
	 *  
	 *  @return The data selected by this ConcatenatingDataSelector from the 
	 *  	given data Object, or null if it selects nothing from the given data 
	 *  	Object
	 */
	
	public Object select(Object data)
	{
		StringBuffer resultsBuffer = new StringBuffer();
		
		if (data != null)
		{
			Iterator selectors = fDataSelectors.iterator();
			
			while (selectors.hasNext())
			{
				DataSelector selector = (DataSelector) selectors.next();
				
				Object selection = selector.select(data);
				
				if (selectors.hasNext() && (fSeparator != null))
				{
					resultsBuffer.append(selection + fSeparator);
				}
				else
				{
					resultsBuffer.append(selection);
				}
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
		
		return (resultsBuffer.toString());
	}
	
	
	/**
	 *  Returns the data not selected by this ConcatenatingDataSelector from the 
	 *  	given data Object.
	 *
	 *  <p>Here, always returns the given data.</p>
	 *  
	 *  @return The data not selected by this ConcatenatingDataSelector from the 
	 *  	given data Object
	 */
	
	public Object remainder(Object data)
	{
		if (data == null)
		{
			String message = "The given data is null";
			
			if (sLogger.isLoggable(Level.WARNING))
			{
				sLogger.logp(Level.WARNING, CLASS_NAME, "remainder", message);
			}
		}
		
		return (data);
	}
	
	
	/** 
	 *  Returns a String representation of this ConcatenatingDataSelector.
	 *
	 *  @return A String representation of this ConcatenatingDataSelector
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("ConcatenatingDataSelector: ");
		result.append("\nSeparator" + fSeparator);
		
		Iterator selectors = fDataSelectors.iterator();
		
		while (selectors.hasNext())
		{
			DataSelector selector = (DataSelector) selectors.next();
			
			result.append("\nSelector: " + selector);
		}
		
		return (result.toString());
	}
}
