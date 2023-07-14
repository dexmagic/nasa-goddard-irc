//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/AbstractDataLogger.java,v 1.7 2006/06/01 22:22:43 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataLogger.java,v $
//  Revision 1.7  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.6  2006/05/03 23:20:16  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.5  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.4  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2005/10/28 23:03:41  chostetter_cvs
//  Fixed problem with formatting results casting
//
//  Revision 1.2  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.1  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
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

package gov.nasa.gsfc.irc.data.transformation;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.transformation.description.DataLoggerDescriptor;


/**
 * A DataLogger logs data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractDataLogger implements DataLogger
{
	protected static DataLoggerFactory sDataLoggerFactory = 
		DefaultDataLoggerFactory.getInstance();
	protected static final DataFormatterFactory sDataFormatterFactory =
		DefaultDataFormatterFactory.getInstance();
	
	private DataLoggerDescriptor fDescriptor;
	
	private DataFormatter fDataFormatter;
	private Logger fLogger;
	private String fLogName;
	private Level fLevel;
	
	
	/**
	 * Constructs a new AbstractDataLogger that will perform the data 
	 * selection described by the given DataLoggerDescriptor.
	 *
	 * @param descriptor A DataLoggerDescriptor
	 * @return A new NamedDataLogger that will perform the data selection 
	 * 		described by the given NamedDataLoggerDescriptor		
	**/
	
	public AbstractDataLogger(DataLoggerDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Sets the DataLoggerDescriptor associated with this DataLogger to 
	 * the given DataLoggerDescriptor, and configures this DataLogger 
	 * in accordance with it.
	 *
	 * @param descriptor The DataLoggerDescriptor according to which to 
	 * 		configure this DataLogger
	**/
	
	public void setDescriptor(DataLoggerDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this AbstractDataLogger in accordance with its current 
	 * DataLoggerDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			fDataFormatter = sDataFormatterFactory.getDataFormatter
				(fDescriptor.getDataFormat());
			
			fLogName = fDescriptor.getLogName();
			fLevel = fDescriptor.getLevel();
			
			if (fLogName != null)
			{
				fLogger = Logger.getLogger(fLogName);
			}
		}
	}
	
	
	/**
	 * Causes this AbstractDataLogger to log the given data Object as specified by 
	 * its associated DataLoggerDescriptor.
	 *
	 * @param data The data to be logged
	 * @param context An optional Map of contextual information
	 * @throws UnsupportedOperationException if this RecordLogger is unable to 
	 * 		log the given data
	**/
	
	public void log(Object data, Map context)
		throws UnsupportedOperationException
	{
		String message;
		
		if (fDataFormatter != null)
		{
			message = fDataFormatter.format(data, context, null).toString();
		}
		else if (data != null)
		{
			message = data.toString();
		}
		else
		{
			message = "null";
		}
		
		if (fLogger != null)
		{
			fLogger.logp(fLevel, fLogName, "log", message);
		}
	}
	
	
	/** 
	 *  Returns a String representation of this DataLogger.
	 *
	 *  @return A String representation of this DataLogger
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataLogger: ");
		result.append("\n" + fDescriptor);
		
		return (result.toString());
	}
}
