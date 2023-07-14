//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: ListValueParser.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.2  2006/04/27 21:37:29  chostetter_cvs
//  Fixed error in configuration from descriptor
//
//  Revision 1.1  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.transformation.description.DataParseDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.ListValueParserDescriptor;


/**
 * A ListValueParser parses data according to a list structure.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class ListValueParser extends AbstractDataParser implements ValueParser
{
	private static final String CLASS_NAME = ListValueParser.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private ListValueParserDescriptor fDescriptor;
	
	private DataSelector fSource;
		
	private DataParser fInitiator;
	private DataParser fInitiatorSeparator;
	private DataParser fPrefix;
	private DataParser fValue;
	private DataParser fPostfix;
	private DataParser fTerminator;
	
	private int fLength = 0;
	private boolean fSuppressFirstPrefix = false;
	private boolean fSuppressLastPostfix = false;
	
	private Map fLastParse = new LinkedHashMap();
	private Map fTempParse = new LinkedHashMap();
	
	
	/**
	 * Default constructor of a new ListValueParser.
	 *
	**/
	
	public ListValueParser()
	{
		
	}
	

	/**
	 * Constructs a new ListValueParser and configures it in accordance with 
	 * the given ListValueParserDescriptor.
	 *
	 * @param descriptor The ListValueParserDescriptor according to which to 
	 * 		configure the new ListValueParser
	**/
	
	public ListValueParser(ListValueParserDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Configures this ListValueParser in accordance with its current 
	 * ListValueParserDescriptor.
	**/
	
	private void configureFromDescriptor()
	{
		fSource = getSource();
		
		fLength = fDescriptor.getLength();
		fSuppressFirstPrefix = fDescriptor.suppressesFirstPrefix();
		fSuppressLastPostfix = fDescriptor.suppressesLastPostfix();
		
		DataParseDescriptor initiatorDescriptor = fDescriptor.getInitiator();
		
		if (initiatorDescriptor != null)
		{
			fInitiator = sDataParserFactory.getDataParser(initiatorDescriptor);
		}
		
		DataParseDescriptor initiatorSeparatorDescriptor = 
			fDescriptor.getInitiatorSeparator();
		
		if (initiatorSeparatorDescriptor != null)
		{
			fInitiatorSeparator = 
				sDataParserFactory.getDataParser(initiatorSeparatorDescriptor);
		}
		
		DataParseDescriptor prefixDescriptor = fDescriptor.getPrefix();
		
		if (prefixDescriptor != null)
		{
			fPrefix = sDataParserFactory.getDataParser(prefixDescriptor);
		}
		
		DataParseDescriptor valueDescriptor = fDescriptor.getValue();
		
		if (valueDescriptor != null)
		{
			fValue = sDataParserFactory.getDataParser(valueDescriptor);
		}
		
		DataParseDescriptor postfixDescriptor = fDescriptor.getPostfix();
		
		if (postfixDescriptor != null)
		{
			fPostfix = sDataParserFactory.getDataParser(postfixDescriptor);
		}
		
		DataParseDescriptor terminatorDescriptor = fDescriptor.getTerminator();
		
		if (terminatorDescriptor != null)
		{
			fTerminator = sDataParserFactory.getDataParser(terminatorDescriptor);
		}
	}
	

	/**
	 * Causes this ListValueParser to parse the given data Object into a Map as  
	 * specified by its associated ListValueParserDescriptor and then return the 
	 * resulting Map.
	 *
	 * @param data The data Object to be parsed
	 * @param context An optional Map of contextual information
	 * @return The result of parsing the given data Object
	 * @throws UnsupportedOperationException if this ListValueParser is unable to 
	 * 		parse the given data Object
	**/
	
	public Map parse(Object data, Map context)
	{
		fLastParse.clear();
		
		if (data instanceof byte[])
		{
			data = ByteBuffer.wrap((byte[]) data);
		}
		else if (data instanceof char[])
		{
			data = CharBuffer.wrap((char[]) data);
		}
		
		Object selectedData = data;
		
		if (fSource != null)
		{
			selectedData = fSource.select(data);	
		}
		
		if (fInitiator != null)
		{
			fTempParse.clear();
			fTempParse = fInitiator.parse(selectedData, context);
		}
		
		if (fInitiatorSeparator != null)
		{
			fTempParse.clear();
			fTempParse = fInitiatorSeparator.parse(selectedData, context);
		}
		
		if (fValue != null)
		{
			List results = new ArrayList();
			
			boolean isFirst = true;
			boolean isLast = false;
			int lastIndex = fLength - 1;
			
			if (fLength > 0)
			{
				for (int i = 0; i < fLength; i++)
				{
					if (i == lastIndex)
					{
						isLast = true;
					}
					
					if ((fPrefix != null) && 
						(! isFirst || ! fSuppressFirstPrefix || 
							(fSuppressFirstPrefix && ! isFirst)))
					{
						fTempParse.clear();
						fTempParse = fPrefix.parse(selectedData, context);
					}
					
					fTempParse.clear();
					fTempParse = fValue.parse(selectedData, context);
					
					if ((fTempParse != null) && (fTempParse.size() > 0))
					{
						results.add(fTempParse.values().iterator().next());
					}
					else
					{
						if (sLogger.isLoggable(Level.SEVERE))
						{
							String message = "Expected " + fLength + 
								" list elements, found only " + i;
							
							sLogger.logp(Level.SEVERE, CLASS_NAME, "parse", 
								message);
						}
					}
					
					if ((fPostfix != null) && 
						(! isLast || ! fSuppressLastPostfix || 
							(fSuppressLastPostfix && ! isLast)))
					{
						fTempParse.clear();
						fTempParse = fPostfix.parse(selectedData, context);
					}
					
					if (isFirst)
					{
						isFirst = false;
					}
				}
			}
			else
			{
				boolean done = false;
				
				while (! done)
				{
					if ((fPrefix != null) && 
						(! isFirst || ! fSuppressFirstPrefix || 
							(fSuppressFirstPrefix && ! isFirst)))
					{
						fTempParse.clear();
						fTempParse = fPrefix.parse(selectedData, context);
					}
					
					fTempParse.clear();
					fTempParse = fValue.parse(selectedData, context);
					
					if ((fTempParse != null) && (fTempParse.size() > 0))
					{
						results.add(fTempParse.values().iterator().next());
					}
					else
					{
						done = true;
					}
					
					if (fPostfix != null)
					{
						fTempParse.clear();
						fTempParse = fPostfix.parse(selectedData, context);
					}
					
					if (isFirst)
					{
						isFirst = false;
					}
				}
			}
		
			fLastParse.put(getName(), results);
		}
		
		if (fTerminator != null)
		{
			fTempParse.clear();
			fTempParse = fTerminator.parse(selectedData, context);
		}
		
		return (fLastParse);
	}


	/** 
	 *  Returns a String representation of this ListValueParser.
	 *
	 *  @return A String representation of this ListValueParser
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("ListValueParser: ");
		
		if (fDescriptor != null)
		{
			result.append("\nDescriptor: " + fDescriptor);
		}
		
		return (result.toString());
	}
}
