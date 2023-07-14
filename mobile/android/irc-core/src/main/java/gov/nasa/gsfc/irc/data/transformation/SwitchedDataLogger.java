//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SwitchedDataLogger.java,v $
//  Revision 1.5  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.4  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.3  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.2  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.1  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
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

import gov.nasa.gsfc.irc.data.state.DataStateDeterminer;
import gov.nasa.gsfc.irc.data.state.DataStateDeterminerDescriptor;
import gov.nasa.gsfc.irc.data.state.DataStateDeterminerFactory;
import gov.nasa.gsfc.irc.data.state.DefaultDataStateDeterminerFactory;
import gov.nasa.gsfc.irc.data.transformation.description.DataLogDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.SwitchedDataLoggerDescriptor;


/**
 * A SwitchedDataLogger selects among a set of switched log cases.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class SwitchedDataLogger extends AbstractDataLogger
{
	private static DataStateDeterminerFactory sDataStateDeterminerFactory = 
		DefaultDataStateDeterminerFactory.getInstance();
	
	private SwitchedDataLoggerDescriptor fDescriptor;
	
	private DataStateDeterminer fCaseDeterminer;
	private Map fCasesByName = new LinkedHashMap();
	
	
	/**
	 * Constructs a new SwitchedDataLogger and configures it in accordance with 
	 * the given SwitchedDataLoggerDescriptor.
	 *
	 * @param descriptor The SwitchedDataLoggerDescriptor according to which to 
	 * 		configure the new SwitchedDataLogger
	**/
	
	public SwitchedDataLogger(SwitchedDataLoggerDescriptor descriptor)
	{
		super(descriptor);
		
		setDescriptor(descriptor);
	}
	

	/**
	 * Configures this SwitchedDataLogger in accordance with its current 
	 * SwitchedDataLoggerDescriptor.
	 *
	**/
	
	public void setDescriptor(SwitchedDataLoggerDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this SwitchedDataLogger in accordance with its current 
	 * SwitchedDataLoggerDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			DataStateDeterminerDescriptor determiner = fDescriptor.getCaseSelector();
			
			if (determiner != null)
			{
				fCaseDeterminer = sDataStateDeterminerFactory.
					getDataStateDeterminer(determiner);
			}
			
			fCasesByName.clear();
			
			Map logCasesByName = fDescriptor.getLogCases();
			
			Iterator cases = logCasesByName.entrySet().iterator();
			
			while (cases.hasNext())
			{
				Map.Entry caseEntry = (Map.Entry) cases.next();
				
				String name = (String) caseEntry.getKey();
				
				DataLogDescriptor caseDescriptor = 
					(DataLogDescriptor) caseEntry.getValue();
				
				DataLogger logger = 
					sDataLoggerFactory.getDataLogger(caseDescriptor);
				
				fCasesByName.put(name, logger);
			}
		}
	}
	
	
	/**
	 * Causes this SwitchedDataLogger to log the given data Object as specified by 
	 * its associated SwitchedDataLoggerDescriptor.
	 *
	 * @param data The data to be logged
	 * @param context An optional Map of contextual information
	 * @return The result of logting the given data
	 * @throws UnsupportedOperationException if this SwitchedDataLogger is unable 
	 * 		to log the given data
	**/
	
	public void log(Object data, Map context)
		throws UnsupportedOperationException
	{
		Object selectedData = data;
		
		String caseName = fCaseDeterminer.determineDataState(selectedData);
		
		if (caseName != null)
		{
			DataLogger logger = (DataLogger) fCasesByName.get(caseName);
			
			logger.log(selectedData, context);
		}
	}
	
	
	/** 
	 *  Returns a String representation of this SwitchedDataLogger.
	 *
	 *  @return A String representation of this SwitchedDataLogger
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("SwitchedDataLogger: ");
		result.append("\nDescriptor: " + fDescriptor);
		
		return (result.toString());
	}
}
