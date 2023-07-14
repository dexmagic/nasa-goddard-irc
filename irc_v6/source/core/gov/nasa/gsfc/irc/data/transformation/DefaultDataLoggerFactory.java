//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/DefaultDataLoggerFactory.java,v 1.7 2006/05/09 23:20:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataLoggerFactory.java,v $
//  Revision 1.7  2006/05/09 23:20:48  chostetter_cvs
//  Fixed parser and formatter instance configuration case bug
//
//  Revision 1.6  2006/05/03 23:20:16  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.5  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.4  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.3  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.2  2005/09/14 18:03:11  chostetter_cvs
//  Refactored descriptor-based factories
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

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.transformation.description.DataLogDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataLoggerDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DefaultDataLoggerDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.SwitchedDataLoggerDescriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementFactory;
import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataLoggerFactory creates and returns instances of DataLoggers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/09 23:20:48 $
 * @author Carl F. Hostetter
 */

public class DefaultDataLoggerFactory extends AbstractIrcElementFactory 
	implements DataLoggerFactory
{
	private static final String CLASS_NAME = 
		DefaultDataLoggerFactory.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static DataLoggerFactory fFactory;
	
	
	/**
	 *  Creates and returns a DataLogger appropriate to the given 
	 *  DefaultDataLogDescriptor.
	 *  
	 *  @param A DefaultDataLogDescriptor describing the desired selection scheme
	 *  @return A DataLogger appropriate to the given 
	 *  		DefaultDataLogDescriptor
	 */
	
	protected DefaultDataLoggerFactory()
	{
		
	}
	
	
	/**
	 *  Returns the singleton instance of a DefaultDataLoggerFactory.
	 *  
	 *  @return The singleton instance of a DefaultDataLoggerFactory
	 */
	
	public static DataLoggerFactory getInstance()
	{
		if (fFactory == null)
		{
			fFactory = new DefaultDataLoggerFactory();
		}
		
		return (fFactory);
	}
	
	
	/**
	 *  Creates and returns a DataLogger appropriate to the given 
	 *  DataLoggerDescriptor.
	 *  
	 *  @param A DataLoggerDescriptor describing the desired data logger
	 *  @return A DataLogger appropriate to the given DefaultDataLogDescriptor
	 */
	
	public DataLogger getDataLogger(DataLoggerDescriptor descriptor)
	{
		DataLogger logger = null;
		
		if (descriptor != null)
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				logger = (DataLogger) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}	
			
			if (logger == null)
			{
				if (descriptor instanceof SwitchedDataLoggerDescriptor)
				{
					logger = (DataLogger) new SwitchedDataLogger
						((SwitchedDataLoggerDescriptor) descriptor);
				}
				else if (descriptor instanceof DefaultDataLoggerDescriptor)
				{
					logger = (DataLogger) new DefaultDataLogger
						((DefaultDataLoggerDescriptor) descriptor);
				}
				else
				{
					if (sLogger.isLoggable(Level.SEVERE))
					{
						String message = 
							"Unable to determine appropriate logger for " + 
								descriptor;
						
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"getDataLogger", message);
					}
				}
			} 
			else
			{
				if (logger instanceof AbstractDataLogger)
				{
					((AbstractDataLogger) logger).setDescriptor(descriptor);
				}
			}
		}
		
		return (logger);
	}
	
	
	/**
	 *  Creates and returns a DataLogger appropriate to the given 
	 *  DefaultDataLogDescriptor.
	 *  
	 *  @param A DefaultDataLogDescriptor describing the desired logging scheme
	 *  @return A DataLogger appropriate to the given DefaultDataLogDescriptor
	 */
	
	public DataLogger getDataLogger(DataLogDescriptor descriptor)
	{
		DataLogger logger = null;
		
		if (descriptor != null)
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				logger = (DataLogger) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}	
			
			if (logger == null)
			{
				DataLoggerDescriptor loggerDescriptor = 
					descriptor.getDataLogger();
				
				logger = getDataLogger(loggerDescriptor);
			}
			else
			{
				if (logger instanceof AbstractDataLogger)
				{
					((AbstractDataLogger) logger).setDescriptor
						(descriptor.getDataLogger());
				}
			}
		}
		
		return (logger);
	}
}
