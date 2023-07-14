//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: FieldParser.java,v $
//  Revision 1.10  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.9  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.8  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.7  2006/03/31 21:57:39  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.6  2006/03/30 22:26:59  chostetter_cvs
//  Fixed behavior when value after label is empty
//
//  Revision 1.5  2006/03/29 21:31:12  chostetter_cvs
//  First stage of IRC schema cleanup
//
//  Revision 1.4  2006/01/25 20:06:04  chostetter_cvs
//  Changed scheme for formatting and parsing arbitrary-lengthsequences of uniform field types
//
//  Revision 1.3  2006/01/25 17:02:23  chostetter_cvs
//  Support for arbitrary-length Message parsing
//
//  Revision 1.2  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.transformation.description.DataParseDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.FieldParserDescriptor;


/**
 * A FieldParser parses data according to a field structure.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $ 
 * @author Carl F. Hostetter   
**/

public class FieldParser extends AbstractDataParser
{
	private FieldParserDescriptor fDescriptor;
	
	private DataSelector fSource;
	
	private DataParser fPrefix;
	private DataParser fLabel;
	private DataParser fLabelSeparator;
	private DataParser fValue;
	private DataParser fPostfix;
	
	private boolean fApplyToRemainingFields = false;
	private boolean fUsesLabelAsParseKey = false;
	
	private Map fLastParse = new LinkedHashMap();
	private Map fTempParse = new LinkedHashMap();
	
	private Object fParseKey;
	
	
	/**
	 * Default constructor of a new FieldParser.
	 *
	**/
	
	public FieldParser()
	{
		
	}
	

	/**
	 * Constructs a new FieldParser and configures it in accordance with 
	 * the given FieldParserDescriptor.
	 *
	 * @param descriptor The FieldParserDescriptor according to which to 
	 * 		configure the new FieldParser
	**/
	
	public FieldParser(FieldParserDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Configures this FieldParser in accordance with its current 
	 * FieldParserDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		fSource = getSource();
		
		fApplyToRemainingFields = fDescriptor.applyToRemainingFields();
		fUsesLabelAsParseKey = fDescriptor.usesLabelAsParseKey();
		
		DataParseDescriptor prefixDescriptor = fDescriptor.getPrefix();
		
		if (prefixDescriptor != null)
		{
			fPrefix = sDataParserFactory.getDataParser(prefixDescriptor);
		}
		
		DataParseDescriptor labelDescriptor = fDescriptor.getLabel();
		
		if (labelDescriptor != null)
		{
			fLabel = sDataParserFactory.getDataParser(labelDescriptor);
		}
		
		DataParseDescriptor labelSeparatorDescriptor = 
			fDescriptor.getLabelSeparator();
		
		if (labelSeparatorDescriptor != null)
		{
			fLabelSeparator = 
				sDataParserFactory.getDataParser(labelSeparatorDescriptor);
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
		
		fParseKey = getName();
	}
	

	/**
	 * Returns true if this FieldParser is configured to be applied to all the 
	 * remaining fields in the input, false otherwise.
	 *
	 * @return True if this FieldParser is configured to be applied to all the 
	 * 	remaining fields in the input, false otherwise
	**/
	
	public boolean applyToRemainingFields()
	{
		return (fApplyToRemainingFields);
	}
	

	/**
	 * Causes this FieldParser to parse the given data Object into a Map as  
	 * specified by its associated FieldParserDescriptor and then return the 
	 * resulting Map.
	 *
	 * @param data The data Object to be parsed
	 * @param context An optional Map of contextual information
	 * @return The result of parsing the given data Object
	 * @throws UnsupportedOperationException if this FieldParser is unable to 
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
		
		if (fPrefix != null)
		{
			fTempParse.clear();
			fTempParse = fPrefix.parse(selectedData, context);
		}
		
		if (fLabel != null)
		{
			fTempParse.clear();
			fTempParse = fLabel.parse(selectedData, context);
			
			if (fUsesLabelAsParseKey)
			{
				if  ((fTempParse != null) && (fTempParse.size() > 0))
				{
					Object label = fTempParse.values().iterator().next();
					
					if (label != null)
					{
						fParseKey = label;
					}
				}
			}
		}
		
		if (fLabelSeparator != null)
		{
			fTempParse.clear();
			fTempParse = fLabelSeparator.parse(selectedData, context);
		}
		
		if (fValue != null)
		{
			fTempParse.clear();
			fTempParse = fValue.parse(selectedData, context);
			
			if (fParseKey != null)
			{
				if ((fTempParse == null) || (fTempParse.size() == 0))
				{
					fTempParse.put(fParseKey, "");
				}
				else
				{
					Object value = fTempParse.values().iterator().next();
					
					fTempParse.clear();
					fTempParse.put(fParseKey, value);
				}
			}
			
			fLastParse.putAll(fTempParse);
		}
		else if (fParseKey != null)
		{
			
			fTempParse.clear();
			fTempParse.put(fParseKey, "");
			
			fLastParse.putAll(fTempParse);
		}
		
		if (fPostfix != null)
		{
			fTempParse.clear();
			fTempParse = fPostfix.parse(selectedData, context);
		}
		
		return (fLastParse);
	}
	
	
	/** 
	 *  Returns a String representation of this FieldParser.
	 *
	 *  @return A String representation of this FieldParser
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("FieldParser: ");
		
		if (fDescriptor != null)
		{
			result.append("\nDescriptor: " + fDescriptor);
		}
		
		return (result.toString());
	}
}
