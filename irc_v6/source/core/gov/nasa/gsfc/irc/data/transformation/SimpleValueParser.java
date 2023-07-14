//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SimpleValueParser.java,v $
//  Revision 1.2  2006/05/09 23:20:48  chostetter_cvs
//  Fixed parser and formatter instance configuration case bug
//
//  Revision 1.1  2006/05/03 23:20:16  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.7  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.6  2006/01/25 17:02:23  chostetter_cvs
//  Support for arbitrary-length Message parsing
//
//  Revision 1.5  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.4  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.3  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DefaultDataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.StringConstantValueSelectorDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataParserDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataSourceSelectionDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.SimpleValueParserDescriptor;


/**
 * A SimpleValueParser parses atomic data values.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/09 23:20:48 $ 
 * @author Carl F. Hostetter   
**/

public class SimpleValueParser extends AbstractDataParser implements ValueParser
{
	private static final String CLASS_NAME = SimpleValueParser.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private SimpleValueParserDescriptor fDescriptor;
	
	private String fValue;
	private boolean fIsAscii = true;
	
	private DataSelector fSource;
	private DataSelector fSelector;
	
	private Map fLastParse = new LinkedHashMap();
	
	
	/**
	 * Default constructor of a new SimpleValueParser.
	 *
	**/
	
	public SimpleValueParser()
	{
		
	}
	

	/**
	 * Constructs a new SimpleValueParser that will use the given String value as its 
	 * constant parse value.
	 *
	 * @param value The constant String parse value of the new SimpleValueParser
	**/
	
	public SimpleValueParser(String value)
	{
		fValue = value;
	}
	

	/**
	 * Constructs a new SimpleValueParser and configures it in accordance with 
	 * the given SimpleValueParserDescriptor.
	 *
	 * @param descriptor The SimpleValueParserDescriptor according to which to 
	 * 		configure the new SimpleValueParser
	**/
	
	public SimpleValueParser(SimpleValueParserDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Sets the SimpleValueParserDescriptor associated with this 
	 * SimpleValueParser to the given SimpleValueFormatterDescriptor, and 
	 * configures this SimpleValueParser in accordance with it.
	 *
	 * @param descriptor The SimpleValueParserDescriptor according to which 
	 * 		to configure this SimpleValueParser
	**/
	
	public void setDescriptor(DataParserDescriptor descriptor)
	{
		super.setDescriptor(descriptor);
		
		if (descriptor instanceof SimpleValueParserDescriptor)
		{
			fDescriptor = (SimpleValueParserDescriptor) descriptor;
			
			configureFromDescriptor();
		}
	}
	
	
	/**
	 * Sets the SimpleValueParser associated with this SimpleValueParser to the given 
	 * SimpleValueParser, and configures this SimpleValueParser in accordance with it.
	 *
	 * @param descriptor The SimpleValueParserDescriptor according to which to 
	 * 		configure this SimpleValueParser
	**/
	
	public void setDescriptor(SimpleValueParserDescriptor descriptor)
	{
		super.setDescriptor(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this SimpleValueParser in accordance with its current 
	 * SimpleValueParserDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			fIsAscii = fDescriptor.isAscii();
			
			fValue = fDescriptor.getConstantValue();
			
			DataSelectionDescriptor selection = null;
			
			if (fValue == null)
			{
				DataSourceSelectionDescriptor source = fDescriptor.getSource();
				
				if (source != null)
				{
					fSource = sDataSelectorFactory.getDataSelector
						(source);
				}
				
				selection = fDescriptor.getSelection();
			}
			else
			{
				selection = new DefaultDataSelectionDescriptor();
				
				DataSelectorDescriptor selector = 
					new StringConstantValueSelectorDescriptor
						(fValue, fIsAscii);
				
				selection.setDataSelector(selector);
			}
			
			if (selection != null)
			{
				fSelector = sDataSelectorFactory.getDataSelector
					(selection);
			}
		}
	}
	
	
	/**
	 * Causes this SimpleValueParser to parse the given data Object into a Map as  
	 * specified by its associated SimpleValueParserDescriptor and then return the 
	 * resulting Map.
	 *
	 * @param data The data Object to be parsed
	 * @param context An optional Map of contextual information
	 * @return The result of parsing the given data Object
	 * @throws UnsupportedOperationException if this SimpleValueParser is unable to 
	 * 		parse the given data Object
	**/
	
	public Map parse(Object data, Map context)
	{
		fLastParse.clear();
		
		Object selectedData = data;
		
		if (fSource != null)
		{
			selectedData = fSource.select(data);	
		}
		
		if (fSelector != null)
		{
			selectedData = fSelector.select(selectedData);	
		}
		
		if (selectedData != null)
		{
			fLastParse.put(getName(), selectedData);
		}
		else
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = "No data selected; skipping value " + 
					getName();
				
				sLogger.logp(Level.FINE, CLASS_NAME, "parse", message);
			}
		}
		
		return (fLastParse);
	}
	
	
	/** 
	 *  Returns a String representation of this SimpleValueParser.
	 *
	 *  @return A String representation of this SimpleValueParser
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("SimpleValueParser: ");
		result.append("\nDescriptor: " + getDescriptor());
		
		return (result.toString());
	}
}
