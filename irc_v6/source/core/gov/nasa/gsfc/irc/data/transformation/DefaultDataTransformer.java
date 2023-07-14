//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataTransformer.java,v $
//  Revision 1.16  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.15  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.14  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.13  2006/04/18 14:00:50  tames
//  Reflects relocated MessageSender interface.
//
//  Revision 1.12  2006/04/18 04:00:47  tames
//  Modified to reflect relocated Message classes.
//
//  Revision 1.11  2006/01/25 17:02:23  chostetter_cvs
//  Support for arbitrary-length Message parsing
//
//  Revision 1.10  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.9  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.8  2005/09/14 19:32:07  chostetter_cvs
//  Added ability to publish parse results as a Message
//
//  Revision 1.7  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.6  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.5  2005/09/08 22:18:32  chostetter_cvs
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.publishing.messages.MessageSender;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.selection.BufferedDataSelector;
import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.selection.DataSelectorFactory;
import gov.nasa.gsfc.irc.data.selection.DefaultDataSelectorFactory;
import gov.nasa.gsfc.irc.data.selection.description.BufferedDataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataLogDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataParseDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataSourceSelectionDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataTargetSelectionDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataTransformerDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DefaultDataTransformerDescriptor;
import gov.nasa.gsfc.irc.messages.MessageFactory;


/**
 * A DataTransformer transforms data into a different, specifiable format.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $ 
 * @author Carl F. Hostetter   
**/

public class DefaultDataTransformer extends AbstractDataTransformer
{
	private static final String CLASS_NAME = DefaultDataTransformer.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static final DataSelectorFactory sDataSelectorFactory =
		DefaultDataSelectorFactory.getInstance();
	private static final DataParserFactory sDataParserFactory =
		DefaultDataParserFactory.getInstance();
	private static final DataLoggerFactory sDataLoggerFactory =
		DefaultDataLoggerFactory.getInstance();
	private static final DataFormatterFactory sDataFormatterFactory =
		DefaultDataFormatterFactory.getInstance();

	private DataTransformerDescriptor fDescriptor;
	
	private DataSelector fSource;
	private DataSelector fTarget;
	private BufferedDataSelector fBuffer;
	private DataParser fParser;
	private DataLogger fLogger;
	private DataFormatter fFormatter;
	
	private boolean fIsEnabled = true;
	private boolean fPublishParseAsMessage = false;
	private MessageFactory fMessageFactory;
	
	private boolean fIsBuffered = false;
	
	
	/**
	 * Constructs a new DefaultDataTransformer and configures it in accordance 
	 * with the given DataTransformerDescriptor.
	 *
	 * @param descriptor The DataTransformerDescriptor according to which to 
	 * 		configure the new DefaultDataTransformer
	**/
	
	public DefaultDataTransformer(DefaultDataTransformerDescriptor descriptor)
	{
		setDescriptor(descriptor);
	}
	

	/**
	 * Sets the DataTransformerDescriptor associated with this DataTransformer 
	 * to the given DataTransformerDescriptor, and configures this 
	 * DataTransformer in accordance with it.
	 *
	 * @param descriptor The DataTransformerDescriptor according to which to 
	 * 		configure this DataTransformer
	**/
	
	public void setDescriptor(DefaultDataTransformerDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this DataTransformer in accordance with its current 
	 * DataTransformerDescriptor
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			fIsEnabled = fDescriptor.isEnabled();
			fPublishParseAsMessage = fDescriptor.publishesParseAsMessage();
			
			DataSourceSelectionDescriptor source = fDescriptor.getSource();
			
			if (source != null)
			{
				fSource = sDataSelectorFactory.getDataSelector(source);
			}

			DataTargetSelectionDescriptor target = fDescriptor.getTarget();
			
			if (target != null)
			{
				fTarget = sDataSelectorFactory.getDataSelector(target);
			}

			BufferedDataSelectionDescriptor buffer = fDescriptor.getBuffer();
			
			if (buffer != null)
			{
				fBuffer = (BufferedDataSelector) 
					sDataSelectorFactory.getDataSelector(buffer);
				
				fIsBuffered = true;
			}

			DataParseDescriptor parse = fDescriptor.getParse();
			
			if (parse != null)
			{
				fParser = sDataParserFactory.getDataParser(parse);
			}

			DataLogDescriptor log = fDescriptor.getLog();
			
			if (log != null)
			{
				fLogger = sDataLoggerFactory.getDataLogger(log);
			}

			DataFormatDescriptor format = fDescriptor.getFormat();
			
			if (format != null)
			{
				fFormatter = sDataFormatterFactory.getDataFormatter(format);
			}
		}
	}
	
	
	/**
	 * Causes this DataTransformer to transform the given data Object and return 
	 * the results.
	 *
	 * @param data The data to be transformed
	 * @param context An optional Map of contextual information
	 * @return The result of transforming the given data
	 * @throws UnsupportedOperationException if this DataTransformer is unable to 
	 * 		transform the given data
	**/
	
	public Object transform(Object data, Map context)
		throws UnsupportedOperationException
	{
		Object result = null;
		
		if (fIsEnabled)
		{
			Map parse = null;
			
			if (data instanceof Map)
			{
				parse = (Map) data;
			}
			
			Object source = data;
			
			if (fSource != null)
			{
				source = fSource.select(data);
					
				if (source != null)
				{
					if (sLogger.isLoggable(Level.FINE))
					{
						String message = new String("Source: " + source);
	
						sLogger.logp(Level.FINE, CLASS_NAME, "transform", message);
					}
					
					result = source;
				}
			}
			
			boolean done = false;
			
			if (fBuffer != null)
			{
				source = fBuffer.select(data);
					
				if (source != null)
				{
					if (sLogger.isLoggable(Level.FINE))
					{
						String message = new String("Buffered source: " + source);
	
						sLogger.logp(Level.FINE, CLASS_NAME, "transform", message);
					}
					
					result = source;
				}
				else
				{
					done = true;
				}
			}
			
			while (! done)
			{
				if (fParser != null)
				{
					parse = fParser.parse(source, context);
					
					if (fPublishParseAsMessage && (parse != null))
					{
						if (context != null)
						{
							MessageSender sender = (MessageSender) 
								context.get(MessageSender.MESSAGE_SENDER_KEY);
							
							if (sender != null)
							{
								if (fMessageFactory == null)
								{
									fMessageFactory = Irc.getMessageFactory();
								}
								
								Message message = fMessageFactory.createMessage();
								message.putAll(parse);
								
								String name = fParser.getName();
								
								if (name != null)
								{
									message.setName(name);
								}
								
								sender.sendMessage(message);
							}
						}
					}
					
					if (sLogger.isLoggable(Level.FINE))
					{
						StringBuffer message = new StringBuffer("Parse results: ");
						
						if (parse == null)
						{
							message.append(parse);
						}
						else
						{
							Map parseMap = (Map) parse;
							
							int numFields = parseMap.size();
							
							message.append("has " + numFields + " fields: ");
							
							Iterator fields = parseMap.entrySet().iterator();
							
							for (int i = 1; fields.hasNext(); i++)
							{
								Map.Entry field = (Map.Entry) fields.next();
								
								String name = (String) field.getKey();
								Object value = field.getValue();
								
								message.append("\n>" + i + " name=\'" + name + 
									"\' value=\'" + value + "\'");
							}
						}
						
						sLogger.logp(Level.FINE, CLASS_NAME, "transform", 
							message.toString());
					}
					
					result = parse;
				}
				
				if (fLogger != null)
				{
					fLogger.log(parse, context);
				}
				
				Object target = null;
				
				if (fTarget != null)
				{
					target = fTarget.select(target);
					
					if (sLogger.isLoggable(Level.FINE))
					{
						String message = new String("Target: " + target);
		
						sLogger.logp(Level.FINE, CLASS_NAME, "transform", message);
					}
				}
				
				if (fFormatter != null)
				{
					Object format = fFormatter.format(parse, context, target);
					
					if (sLogger.isLoggable(Level.FINE))
					{
						String message = new String("Formatted results: " + format);
		
						sLogger.logp(Level.FINE, CLASS_NAME, "transform", message);
					}
					
					if (format != null)
					{
						result = format;
					}
				}
				
				if (fIsBuffered)
				{
					if (fBuffer.hasRemainingData())
					{
						source = fBuffer.select(null);
						
						done = (source == null);
					}
					else
					{
						done = true;
					}
				}
				else
				{
					done = true;
				}
			}
		}
				
		return (result);
	}

	
	/** 
	 *  Returns a String representation of this DataTransformer.
	 *
	 *  @return A String representation of this DataTransformer
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DefaultDataTransformer: ");
		result.append("\nDescriptor: " + fDescriptor);
		
		if (! fIsEnabled)
		{
			result.append("\nIs disabled");
		}
		
		if (fSource != null)
		{
			result.append("\nSource: " + fSource);
		}
		
		if (fTarget != null)
		{
			result.append("\nTarget: " + fTarget);
		}
		
		if (fParser != null)
		{
			result.append("\nParser: " + fParser);
		}
		
		if (fFormatter != null)
		{
			result.append("\nFormatter: " + fFormatter);
		}

		return (result.toString());
	}
}
