//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SimpleValueFormatter.java,v $
//  Revision 1.2  2006/05/09 23:20:48  chostetter_cvs
//  Fixed parser and formatter instance configuration case bug
//
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.10  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.9  2005/10/20 15:58:54  chostetter_cvs
//  Beefed up error condition handling
//
//  Revision 1.8  2005/09/30 22:06:27  chostetter_cvs
//  Fixed value parsing and formatting type error
//
//  Revision 1.7  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.6  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
//
//  Revision 1.5  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.4  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.3  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.2  2005/09/13 13:26:10  chostetter_cvs
//  Fixes for HAWC command formatting
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.braju.format.Format;
import com.braju.format.Parameters;

import gov.nasa.gsfc.commons.types.arrays.BitArray;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataSourceSelectionDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.SimpleValueFormatterDescriptor;


/**
 * A SimpleValueFormatter formats atomic data values.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/09 23:20:48 $ 
 * @author Carl F. Hostetter   
**/

public class SimpleValueFormatter extends AbstractDataFormatter 
	implements ValueFormatter
{
	private static final String CLASS_NAME = SimpleValueFormatter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final Integer INTEGER_ZERO = new Integer(0);
	public static final Integer INTEGER_ONE = new Integer(1);
	
	private SimpleValueFormatterDescriptor fDescriptor;
	
	private DataSelector fSource;
	private DataSelector fTarget;
	private DataSelector fSelector;
	
	private DataValueFormatType fFormat;
	private String fPattern;
	private DataFormatRuleType fRule;
	private String fValue;
	
	private SimpleDateFormat fDateFormatter;

	
	/**
	 * Default constructor of a new SimpleValueFormatter.
	 *
	**/
	
	public SimpleValueFormatter()
	{
		
	}
	

	/**
	 * Constructs a new SimpleValueFormatter and configures it in accordance with 
	 * the given SimpleValueFormatterDescriptor.
	 *
	 * @param descriptor The SimpleValueFormatterDescriptor according to which to 
	 * 		configure the new SimpleValueFormatter
	**/
	
	public SimpleValueFormatter(SimpleValueFormatterDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Constructs a new SimpleValueFormatter that will use the given String value as its 
	 * constant format value.
	 *
	 * @param value The constant String format value of the new SimpleValueFormatter
	**/
	
	public SimpleValueFormatter(String value)
	{
		fFormat = DataValueFormatType.TEXT;
		fValue = value;
	}
	

	/**
	 * Sets the SimpleValueFormatterDescriptor associated with this 
	 * SimpleValueFormatter to the given SimpleValueFormatterDescriptor, and 
	 * configures this SimpleValueFormatter in accordance with it.
	 *
	 * @param descriptor The SimpleValueFormatterDescriptor according to which 
	 * 		to configure this SimpleValueFormatter
	**/
	
	public void setDescriptor(DataFormatterDescriptor descriptor)
	{
		super.setDescriptor(descriptor);
		
		if (descriptor instanceof SimpleValueFormatterDescriptor)
		{
			fDescriptor = (SimpleValueFormatterDescriptor) descriptor;
			
			configureFromDescriptor();
		}
	}
	
	
	/**
	 * Sets the SimpleValueFormatterDescriptor associated with this 
	 * SimpleValueFormatter to the given SimpleValueFormatterDescriptor, and 
	 * configures this SimpleValueFormatter in accordance with it.
	 *
	 * @param descriptor The SimpleValueFormatterDescriptor according to which 
	 * 		to configure this SimpleValueFormatter
	**/
	
	public void setDescriptor(SimpleValueFormatterDescriptor descriptor)
	{
		super.setDescriptor(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this SimpleValueFormatter in accordance with its current 
	 * SimpleValueFormatterDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		fTarget = getTarget();
		
		if (fDescriptor != null)
		{
			fFormat = fDescriptor.getDataFormatType();
			fPattern = fDescriptor.getPattern();
			fRule = fDescriptor.getDataFormatRuleType();
			fValue = fDescriptor.getConstantValue();

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
				
				if (selection != null)
				{
					fSelector = sDataSelectorFactory.getDataSelector
						(selection);
				}
			}
			else
			{
				fSource = null;
				fSelector = null;
			}
		}
	}
	
	
	/**
	 * Causes this SimpleValueFormatter to format the given data Object as specified by 
	 * its associated SimpleValueFormatterDescriptor into the given target Object, and 
	 * return the results.
	 *
	 * @param data The data to be formatted
	 * @param context An optional Map of contextual information
	 * @param target The target to receive the formatted data
	 * @return The result of formatting the given data
	 * @throws UnsupportedOperationException if this SimpleValueFormatter is unable to 
	 * 		format the given data
	**/
	
	public Object format(Object data, Map context, Object target)
		throws UnsupportedOperationException
	{
		Object result = null;
		
		Object selectedData = data;
		
		if (fValue != null)
		{
			selectedData = fValue;
		}
		else
		{
			if (fSource != null)
			{
				selectedData = fSource.select(selectedData);	
			}
			
			if (fSelector != null)
			{
				selectedData = fSelector.select(selectedData);	
			}
			
			if (selectedData instanceof Map.Entry)
			{
				selectedData = ((Map.Entry) selectedData).getValue();
			}
		}
			
		if (selectedData != null)
		{
			try
			{
				if ((fFormat == null) || (fFormat == DataValueFormatType.NONE))
				{
					result = selectedData.toString();
				}
				else if ((fFormat == DataValueFormatType.STRING) || 
					(fFormat == DataValueFormatType.TEXT))
				{
					result = selectedData.toString();
				}
				else if (fFormat == DataValueFormatType.PRINTF)
				{
					if (fPattern != null)
					{
						// Apply C-style formatting pattern
						result = Format.sprintf(fPattern, 
							new Parameters(selectedData));
					}
					else
					{
						result = selectedData.toString();
					}
				}
				else if (fFormat == DataValueFormatType.BINARY)
				{
					BitArray formattedData;
					
					if (selectedData instanceof Number)
					{
						formattedData = new BitArray((Number) selectedData);
					}
					else
					{
						Long asLong = new Long((String) selectedData);
						
						formattedData = new BitArray(asLong);
					}
					
					if (fPattern != null)
					{
						BitArray mask = new BitArray(fPattern);
						
						if (fRule != null)
						{
							if (fRule == DataFormatRuleType.AND)
							{
								formattedData.and(mask);
							}
							else if (fRule == DataFormatRuleType.NAND)
							{
								formattedData.or(mask);
							}
							else if (fRule == DataFormatRuleType.OR)
							{
								formattedData.or(mask);
							}
							else if (fRule == DataFormatRuleType.XOR)
							{
								formattedData.xor(mask);
							}
						}
					}
					
					result = formattedData;
				}
				else if ((fFormat == DataValueFormatType.DATE) || 
					(fFormat == DataValueFormatType.TIME))
				{
					if (fDateFormatter == null)
					{
						fDateFormatter = new SimpleDateFormat(fPattern);
					}
					
					Date formattedData;
					
					if (selectedData instanceof Date)
					{
						formattedData = (Date) selectedData;
					}
					else
					{
						formattedData = fDateFormatter.
							parse(selectedData.toString());
					}
					
					result = formattedData;
				}
				else if ((fFormat == DataValueFormatType._BOOLEAN) || 
					(fFormat == DataValueFormatType.BOOLEAN))
				{
					Boolean formattedData;
					
					if (selectedData instanceof Boolean)
					{
						formattedData = ((Boolean) selectedData);
					}
					else
					{
						String stringValue = selectedData.toString();
						
						formattedData = new Boolean(stringValue);
					}
					
					result = formattedData;
				}
				else if ((fFormat == DataValueFormatType._BYTE) || 
						(fFormat == DataValueFormatType.BYTE))
				{
					Byte formattedData;
					
					if (selectedData instanceof Byte)
					{
						formattedData = ((Byte) selectedData);
					}
					else
					{
						String stringValue = selectedData.toString();
						
						formattedData = new Byte(stringValue);
					}
					
					result = formattedData;
				}
				else if ((fFormat == DataValueFormatType._CHAR) || 
						(fFormat == DataValueFormatType.CHARACTER))
				{
					Character formattedData;
					
					if (selectedData instanceof Character)
					{
						formattedData = ((Character) selectedData);
					}
					else
					{
						char charValue = selectedData.toString().charAt(0);
						
						formattedData = new Character(charValue);
					}
					
					result = formattedData;
				}
				else if ((fFormat == DataValueFormatType._SHORT) || 
						(fFormat == DataValueFormatType.SHORT))
				{
					Short formattedData;
					
					if (selectedData instanceof Short)
					{
						formattedData = ((Short) selectedData);
					}
					else
					{
						String stringValue = selectedData.toString();
						
						formattedData = new Short(stringValue);
					}
					
					result = formattedData;
				}
				else if ((fFormat == DataValueFormatType._INT) || 
						(fFormat == DataValueFormatType.INTEGER))
				{
					Integer formattedData;
					
					if (selectedData instanceof Integer)
					{
						formattedData = ((Integer) selectedData);
					}
					else if (selectedData instanceof Boolean)
					{
						boolean value = ((Boolean) selectedData).
							booleanValue();
						
						if (value)
						{
							formattedData = INTEGER_ONE;
						}
						else
						{
							formattedData = INTEGER_ZERO;
						}
					}
					else
					{
						String stringValue = selectedData.toString();
						
						formattedData = new Integer(stringValue);
					}
					
					result = formattedData;
				}
				else if ((fFormat == DataValueFormatType._LONG) || 
						(fFormat == DataValueFormatType.LONG)|| 
						(fFormat == DataValueFormatType.DECIMAL))
				{
					Long formattedData;
					
					if (selectedData instanceof Long)
					{
						formattedData = ((Long) selectedData);
					}
					else
					{
						String stringValue = selectedData.toString();
						
						formattedData = new Long(stringValue);
					}
					
					result = formattedData;
				}
				else if ((fFormat == DataValueFormatType._FLOAT) || 
						(fFormat == DataValueFormatType.FLOAT))
				{
					Float formattedData;
					
					if (selectedData instanceof Float)
					{
						formattedData = ((Float) selectedData);
					}
					else
					{
						String stringValue = selectedData.toString();
						
						formattedData = new Float(stringValue);
					}
					
					result = formattedData;
				}
				else if ((fFormat == DataValueFormatType._DOUBLE) || 
						(fFormat == DataValueFormatType.DOUBLE)|| 
						(fFormat == DataValueFormatType.REAL))
				{
					Double formattedData;
					
					if (selectedData instanceof Double)
					{
						formattedData = ((Double) selectedData);
					}
					else
					{
						String stringValue = selectedData.toString();
						
						formattedData = new Double(stringValue);
					}
					
					result = formattedData;
				}
			}
			catch (Exception ex)
			{
				String message = "Unable to format " + selectedData + 
					" as " + fDescriptor + " due to:\n" + ex;
				
				if (sLogger.isLoggable(Level.SEVERE))
				{
					sLogger.logp(Level.SEVERE, CLASS_NAME, "format", 
						message, ex);
				}
				
				throw (new UnsupportedOperationException(message));
			}
		}
		else
		{
			String message = "Received no data (or constant) to format";
		
			if (sLogger.isLoggable(Level.SEVERE))
			{
				sLogger.logp(Level.SEVERE, CLASS_NAME, "format", message);
			}
			
			throw (new IllegalArgumentException(message));
		}
	
		if (result != null)
		{
			Object selectedTarget = target;
			
			if (fTarget != null)
			{
				selectedTarget = fTarget.select(selectedTarget);	
			}
			
			if (selectedTarget != null)
			{
				try
				{
					if (selectedTarget instanceof ByteBuffer)
					{
						if (result instanceof String)
						{
							byte[] resultBytes = ((String) result).getBytes();
							
							((ByteBuffer) selectedTarget).put(resultBytes);
						}
						else
						{
							String message = "Cannot format " + result + 
								" into a ByteBuffer";
							
							if (sLogger.isLoggable(Level.SEVERE))
							{
								sLogger.logp(Level.SEVERE, CLASS_NAME, "format", 
									message);
							}
						
							throw (new UnsupportedOperationException(message));
						}
					}
					else if (selectedTarget instanceof byte[])
					{
						if (result instanceof String)
						{
							byte[] resultBytes = ((String) result).getBytes();
							
							System.arraycopy(resultBytes, 0, 
								((byte[]) selectedTarget), 0, resultBytes.length);
						}
						else if (result instanceof BitArray)
						{
							byte[] resultBytes = ((BitArray) result).getBytes();
							
							System.arraycopy(resultBytes, 0, 
								((byte[]) selectedTarget), 0, resultBytes.length);
						}
						else
						{
							String message = "Cannot format " + result + 
								" into a byte[]";
							
							if (sLogger.isLoggable(Level.SEVERE))
							{
								sLogger.logp(Level.SEVERE, CLASS_NAME, "format", 
									message);
							}
						
							throw (new UnsupportedOperationException(message));
						}
					}
					else if (selectedTarget instanceof CharBuffer)
					{
						String resultString = result.toString();
						
						((CharBuffer) selectedTarget).put(resultString);
					}
					else if (selectedTarget instanceof char[])
					{
						if (result instanceof String)
						{
							char[] resultChars = ((String) result).toCharArray();
							
							System.arraycopy(resultChars, 0, 
								((char[]) selectedTarget), 0, resultChars.length);
						}
						else
						{
							String message = "Cannot format " + result + 
								" into a char[]";
							
							if (sLogger.isLoggable(Level.SEVERE))
							{
								sLogger.logp(Level.SEVERE, CLASS_NAME, "format", 
									message);
							}
						
							throw (new UnsupportedOperationException(message));
						}
					}
					else if (selectedTarget instanceof StringBuffer)
					{
						String resultString = result.toString();
							
						((StringBuffer) selectedTarget).append(resultString);
					}
					else if (selectedTarget instanceof BitArray)
					{
						if (result instanceof BitArray)
						{
							((BitArray) selectedTarget).prepend((BitArray) result);
						}
						else if (result instanceof Number)
						{
							((BitArray) selectedTarget).prepend((Number) result);
						}
						else if (result instanceof String)
						{
							((BitArray) selectedTarget).prepend((String) result);
						}
						else
						{
							String message = "Cannot format " + result + 
								" into a BitArray";
							
							if (sLogger.isLoggable(Level.SEVERE))
							{
								sLogger.logp(Level.SEVERE, CLASS_NAME, "format", 
									message);
							}
						
							throw (new UnsupportedOperationException(message));
						}
					}
					else if (selectedTarget instanceof DataBuffer)
					{
						if (result instanceof Date)
						{
							long millisecondsSinceEpoch = ((Date) result).getTime();
							
							((DataBuffer) selectedTarget).put
								(0, millisecondsSinceEpoch);
						}
						else
						{
							((DataBuffer) selectedTarget).put(0, result);
						}
					}
					else
					{
						String message = "Cannot format " + result + " into " + 
							selectedTarget;
						
						if (sLogger.isLoggable(Level.SEVERE))
						{
							sLogger.logp(Level.SEVERE, CLASS_NAME, "format", 
								message);
						}
					
						throw (new UnsupportedOperationException(message));
					}
				}
				catch (Exception ex)
				{
					String message = "Cannot format " + result + " into " + 
						selectedTarget + " due to ";
				
					if (sLogger.isLoggable(Level.SEVERE))
					{
						sLogger.logp(Level.SEVERE, CLASS_NAME, "format", 
							message, ex);
					}
				
					throw (new UnsupportedOperationException(message));
				}
			}
		}
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this SimpleValueFormatter.
	 *
	 *  @return A String representation of this SimpleValueFormatter
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("SimpleValueFormatter: ");
		result.append("\nDescriptor: " + getDescriptor());
		
		return (result.toString());
	}
}
