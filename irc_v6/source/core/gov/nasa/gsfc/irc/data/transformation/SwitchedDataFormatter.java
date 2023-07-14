//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SwitchedDataFormatter.java,v $
//  Revision 1.6  2006/05/03 23:20:16  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.5  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.4  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.3  2005/09/14 18:03:11  chostetter_cvs
//  Refactored descriptor-based factories
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.transformation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.state.DataStateDeterminer;
import gov.nasa.gsfc.irc.data.state.DataStateDeterminerDescriptor;
import gov.nasa.gsfc.irc.data.state.DataStateDeterminerFactory;
import gov.nasa.gsfc.irc.data.state.DefaultDataStateDeterminerFactory;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.SwitchedFormatterDescriptor;


/**
 * A SwitchedDataFormatter selects among a set of switched format cases.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:16 $ 
 * @author Carl F. Hostetter   
**/

public class SwitchedDataFormatter extends AbstractDataFormatter
{
	private static DataStateDeterminerFactory sDataStateDeterminerFactory = 
		DefaultDataStateDeterminerFactory.getInstance();
	
	private SwitchedFormatterDescriptor fDescriptor;
	
	private DataSelector fSourceSelector;
	private DataSelector fTargetSelector;
	
	private DataStateDeterminer fCaseDeterminer;
	private Map fCasesByName = new LinkedHashMap();
	
	
	/**
	 * Default constructor of a new SwitchedDataFormatter.
	 *
	**/
	
	public SwitchedDataFormatter()
	{
		
	}
	

	/**
	 * Constructs a new SwitchedDataFormatter and configures it in accordance with 
	 * the given SwitchedFormatterDescriptor.
	 *
	 * @param descriptor The SwitchedFormatterDescriptor according to which to 
	 * 		configure the new SwitchedDataFormatter
	**/
	
	public SwitchedDataFormatter(SwitchedFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		setDescriptor(descriptor);
	}
	

	/**
	 * Configures this SwitchedDataFormatter in accordance with its current 
	 * SwitchedFormatterDescriptor.
	 *
	**/
	
	public void setDescriptor(SwitchedFormatterDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this SwitchedDataFormatter in accordance with its current 
	 * SwitchedFormatterDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			fSourceSelector = getSource();
			fTargetSelector = getTarget();
			
			DataStateDeterminerDescriptor determiner = fDescriptor.getCaseSelector();
			
			if (determiner != null)
			{
				fCaseDeterminer = sDataStateDeterminerFactory.
					getDataStateDeterminer(determiner);
			}
			
			fCasesByName.clear();
			
			Map formatCasesByName = fDescriptor.getFormatCases();
			
			Iterator cases = formatCasesByName.entrySet().iterator();
			
			while (cases.hasNext())
			{
				Map.Entry caseEntry = (Map.Entry) cases.next();
				
				String name = (String) caseEntry.getKey();
				
				DataFormatDescriptor caseDescriptor = 
					(DataFormatDescriptor) caseEntry.getValue();
				
				DataFormatter formatter = 
					sDataFormatterFactory.getDataFormatter(caseDescriptor);
				
				fCasesByName.put(name, formatter);
			}
		}
	}
	
	
	/**
	 * Causes this SwitchedDataFormatter to format the given data Object as 
	 * specified by its associated SwitchedFormatterDescriptor and return the 
	 * results.
	 *
	 * @param data The data to be formatted
	 * @param context An optional Map of contextual information
	 * @return The result of formatting the given data
	 * @throws UnsupportedOperationException if this SwitchedDataFormatter is unable 
	 * 		to format the given data
	**/
	
	public Object format(Object data, Map context, Object target)
		throws UnsupportedOperationException
	{
		Object result = null;
		
		Object selectedData = data;
		
		if (fSourceSelector != null)
		{
			selectedData = fSourceSelector.select(data);
		}
		
		Object selectedTarget = target;
		
		if (fTargetSelector != null)
		{
			selectedTarget = fTargetSelector.select(target);
		}
		
		String caseName = fCaseDeterminer.determineDataState(selectedData);
		
		if (caseName != null)
		{
			DataFormatter formatter = (DataFormatter) fCasesByName.get(caseName);
			
			result = formatter.format(selectedData, context, selectedTarget);
		}
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this SwitchedDataFormatter.
	 *
	 *  @return A String representation of this SwitchedDataFormatter
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("SwitchedDataFormatter: ");
		result.append("\nDescriptor: " + getDescriptor());
		
		return (result.toString());
	}
}
