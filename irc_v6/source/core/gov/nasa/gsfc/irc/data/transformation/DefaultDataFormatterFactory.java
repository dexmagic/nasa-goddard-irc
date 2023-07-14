//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/DefaultDataFormatterFactory.java,v 1.8 2006/05/09 23:20:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataFormatterFactory.java,v $
//  Revision 1.8  2006/05/09 23:20:48  chostetter_cvs
//  Fixed parser and formatter instance configuration case bug
//
//  Revision 1.7  2006/05/03 23:20:16  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.6  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.5  2005/09/30 22:06:27  chostetter_cvs
//  Fixed value parsing and formatting type error
//
//  Revision 1.4  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
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

import gov.nasa.gsfc.irc.data.transformation.description.BasisSetFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataBufferFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.FieldFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.ListValueFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.RecordFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.SimpleValueFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.SwitchedFormatterDescriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementFactory;
import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataFormatterFactory creates and returns instances of DataFormatters.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/09 23:20:48 $
 * @author Carl F. Hostetter
 */

public class DefaultDataFormatterFactory extends AbstractIrcElementFactory 
	implements DataFormatterFactory
{
	private static final String CLASS_NAME = 
		DefaultDataFormatterFactory.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static DataFormatterFactory fFactory;
	
	
	/**
	 *  Creates and returns a DataFormatter appropriate to the given 
	 *  DefaultDataFormatDescriptor.
	 *  
	 *  @param A DefaultDataFormatDescriptor describing the desired selection scheme
	 *  @return A DataFormatter appropriate to the given 
	 *  		DefaultDataFormatDescriptor
	 */
	
	protected DefaultDataFormatterFactory()
	{
		
	}
	
	
	/**
	 *  Returns the singleton instance of a DefaultDataFormatterFactory.
	 *  
	 *  @return The singleton instance of a DefaultDataFormatterFactory
	 */
	
	public static DataFormatterFactory getInstance()
	{
		if (fFactory == null)
		{
			fFactory = new DefaultDataFormatterFactory();
		}
		
		return (fFactory);
	}
	
	
	/**
	 *  Creates and returns a DataFormatter appropriate to the given 
	 *  DataFormatterDescriptor.
	 *  
	 *  @param A DataFormatterDescriptor describing the desired data formatter
	 *  @return A DataFormatter appropriate to the given DataFormatterDescriptor
	 */
	
	public DataFormatter getDataFormatter(DataFormatterDescriptor descriptor)
	{
		DataFormatter formatter = null;
		
		if (descriptor != null)
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				formatter = (DataFormatter) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}	
			
			if (formatter == null)
			{
				if (descriptor instanceof DataFormatDescriptor)
				{
					descriptor = ((DataFormatDescriptor) descriptor).
						getDataFormatter();
				}
				
				if (descriptor instanceof SwitchedFormatterDescriptor)
				{
					formatter = (DataFormatter) new SwitchedDataFormatter
						((SwitchedFormatterDescriptor) descriptor);
				}
				else if (descriptor instanceof BasisSetFormatterDescriptor)
				{
					formatter = (DataFormatter) new BasisSetFormatter
						((BasisSetFormatterDescriptor) descriptor);
				}
				else if (descriptor instanceof DataBufferFormatterDescriptor)
				{
					formatter = (DataFormatter) new DataBufferFormatter
						((DataBufferFormatterDescriptor) descriptor);
				}
				else if (descriptor instanceof RecordFormatterDescriptor)
				{
					formatter = (DataFormatter) new RecordFormatter
						((RecordFormatterDescriptor) descriptor);
				}
				else if (descriptor instanceof FieldFormatterDescriptor)
				{
					formatter = (DataFormatter) new FieldFormatter
						((FieldFormatterDescriptor) descriptor);
				}
				else if (descriptor instanceof ListValueFormatterDescriptor)
				{
					formatter = (DataFormatter) new ListValueFormatter
						((ListValueFormatterDescriptor) descriptor);
				}
				else if (descriptor instanceof SimpleValueFormatterDescriptor)
				{
					formatter = (DataFormatter) new SimpleValueFormatter
						((SimpleValueFormatterDescriptor) descriptor);
				}
				else
				{
					if (sLogger.isLoggable(Level.SEVERE))
					{
						String message = 
							"Unable to determine appropriate formatter for " + 
								descriptor;
						
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"getDataFormatter", message);
					}
				}
			}
			else
			{
				if (formatter instanceof AbstractDataFormatter)
				{
					((AbstractDataFormatter) formatter).setDescriptor
						(descriptor);
				}
			}
		}
		
		return (formatter);
	}
	
	
	/**
	 *  Creates and returns a DataFormatter appropriate to the given 
	 *  DataFormatDescriptor.
	 *  
	 *  @param A DataFormatDescriptor describing the desired formatting scheme
	 *  @return A DataFormatter appropriate to the given DataFormatDescriptor
	 */
	
	public DataFormatter getDataFormatter(DataFormatDescriptor descriptor)
	{
		DataFormatter formatter = null;
		
		if (descriptor != null)
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				formatter = (DataFormatter) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}	
			
			if (formatter == null)
			{
				DataFormatterDescriptor formatterDescriptor = 
					descriptor.getDataFormatter();
				
				formatter = getDataFormatter(formatterDescriptor);
			}
			else
			{
				if (formatter instanceof AbstractDataFormatter)
				{
					((AbstractDataFormatter) formatter).setDescriptor
						(descriptor.getDataFormatter());
				}
			}
		}
		
		return (formatter);
	}
}
