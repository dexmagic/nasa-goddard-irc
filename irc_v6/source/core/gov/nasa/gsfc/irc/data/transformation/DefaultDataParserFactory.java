//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/DefaultDataParserFactory.java,v 1.8 2006/05/09 23:20:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataParserFactory.java,v $
//  Revision 1.8  2006/05/09 23:20:48  chostetter_cvs
//  Fixed parser and formatter instance configuration case bug
//
//  Revision 1.7  2006/05/03 23:20:17  chostetter_cvs
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

import gov.nasa.gsfc.irc.data.transformation.description.DataParseDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataParserDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.FieldParserDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.ListValueParserDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.RecordParserDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.SimpleValueParserDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.SwitchedParserDescriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementFactory;
import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataParserFactory creates and returns instances of DataParsers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/09 23:20:48 $
 * @author Carl F. Hostetter
 */

public class DefaultDataParserFactory extends AbstractIrcElementFactory 
	implements DataParserFactory
{
	private static final String CLASS_NAME = 
		DefaultDataParserFactory.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static DataParserFactory fFactory;
	
	
	/**
	 *  Creates and returns a DataStreamParser appropriate to the given 
	 *  AbstractDataParserDescriptor.
	 *  
	 *  @param A AbstractDataParserDescriptor describing the desired selection scheme
	 *  @return A DataStreamParser appropriate to the given 
	 *  		AbstractDataParserDescriptor
	 */
	
	protected DefaultDataParserFactory()
	{
		
	}
	
	
	/**
	 *  Returns the singleton instance of a DefaultDataParserFactory.
	 *  
	 *  @return The singleton instance of a DefaultDataParserFactory
	 */
	
	public static DataParserFactory getInstance()
	{
		if (fFactory == null)
		{
			fFactory = new DefaultDataParserFactory();
		}
		
		return (fFactory);
	}
	
	
	/**
	 *  Creates and returns a DataParser appropriate to the given 
	 *  DataParserDescriptor.
	 *  
	 *  @param A DataParserDescriptor describing the data parser
	 *  @return A DataParser appropriate to the given DataParserDescriptor
	 */
	
	public DataParser getDataParser(DataParserDescriptor descriptor)
	{
		DataParser parser = null;
		
		if (descriptor != null)
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				parser = (DataParser) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}	
			
			if (parser == null)
			{
				if (descriptor instanceof SwitchedParserDescriptor)
				{
					parser = (DataParser) new SwitchedDataParser
						((SwitchedParserDescriptor) descriptor);
				}
				else if (descriptor instanceof RecordParserDescriptor)
				{
					parser = (DataParser) new RecordParser
						((RecordParserDescriptor) descriptor);
				}
				else if (descriptor instanceof FieldParserDescriptor)
				{
					parser = (DataParser) new FieldParser
						((FieldParserDescriptor) descriptor);
				}
				else if (descriptor instanceof ListValueParserDescriptor)
				{
					parser = (ListValueParser) new ListValueParser
						((ListValueParserDescriptor) descriptor);
				}
				else if (descriptor instanceof SimpleValueParserDescriptor)
				{
					parser = (DataParser) new SimpleValueParser
						((SimpleValueParserDescriptor) descriptor);
				}
				else
				{
					if (sLogger.isLoggable(Level.SEVERE))
					{
						String message = 
							"Unable to determine appropriate parser for " + 
								descriptor;
						
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"getDataParser", message);
					}
				}
			}
			else
			{
				if (parser instanceof AbstractDataParser)
				{
					((AbstractDataParser) parser).setDescriptor(descriptor);
				}
			}
		}
		
		return (parser);
	}
	
	
	/**
	 *  Creates and returns a DataParser appropriate to the given 
	 *  DataParseDescriptor.
	 *  
	 *  @param A DataParseDescriptor describing the desired parsing scheme
	 *  @return A DataParser appropriate to the given DataParseDescriptor
	 */
	
	public DataParser getDataParser(DataParseDescriptor descriptor)
	{
		DataParser parser = null;
		
		if (descriptor != null)
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				parser = (DataParser) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}	
			
			if (parser == null)
			{
				DataParserDescriptor parserDescriptor = 
					descriptor.getDataParser();
				
				parser = getDataParser(parserDescriptor);
			}
			else
			{
				if (parser instanceof AbstractDataParser)
				{
					((AbstractDataParser) parser).setDescriptor
						(descriptor.getDataParser());
				}
			}
		}
		
		return (parser);
	}
}
