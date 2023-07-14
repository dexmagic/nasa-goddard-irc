//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/state/DefaultDataComparator.java,v 1.5 2006/04/27 23:31:09 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataComparator.java,v $
//  Revision 1.5  2006/04/27 23:31:09  chostetter_cvs
//  Added support for field value selection in determinants
//
//  Revision 1.4  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.3  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.2  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
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

package gov.nasa.gsfc.irc.data.state;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import gov.nasa.gsfc.commons.types.arrays.BitArray;
import gov.nasa.gsfc.irc.data.description.DataValueType;


/**
 * A DataComparator compares two data items.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 23:31:09 $
 * @author Carl F. Hostetter
 */

public class DefaultDataComparator extends AbstractDataComparator
{
	private static final String CLASS_NAME = 
		DefaultDataComparator.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private DataComparatorDescriptor fDescriptor;
	
	private DataComparatorType fComparatorType;
	private String fTargetValue;
	private DataValueType fTargetType;
	
	private Object fTargetData;
	private Pattern fTargetPattern;
	private Class fTargetClass;

	
	/**
	 * Default constructor of a new DataComparator.
	 *
	**/
	
	public DefaultDataComparator()
	{
		
	}
	

	/**
	 * Constructs a new DataComparator that will perform the data comparison 
	 * described by the given DataComparatorDescriptor.
	 *
	 * @param descriptor A DataComparatorDescriptor
	 * @return A new DataComparator that will perform the data comparison 
	 * 		described by the given DataComparatorDescriptor		
	**/
	
	public DefaultDataComparator(DataComparatorDescriptor descriptor)
	{
		super (descriptor);
		
		setDescriptor(descriptor);
	}
	

	/**
	 * Sets the DataComparatorDescriptor associated with this DataComparator to 
	 * the given DataComparatorDescriptor, and configures this DataComparator 
	 * in accordance with it.
	 *
	 * @param descriptor The DataComparatorDescriptor according to which to 
	 * 		configure this DataComparator
	**/
	
	public void setDescriptor(DataComparatorDescriptor descriptor)
	{
		super.setDescriptor(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this DataComparator in accordance with its current 
	 * DataComparatorDescriptor
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			fComparatorType = fDescriptor.getComparatorType();
			
			fTargetType = fDescriptor.getTargetType();
			fTargetValue = fDescriptor.getTargetValue();
			
			fTargetData = fTargetValue;
			
			if (fComparatorType == DataComparatorType.MATCHES)
			{
				fTargetPattern = Pattern.compile(fTargetValue);
			}
			else if (fComparatorType == DataComparatorType.INSTANCEOF)
			{
				try
				{
					fTargetClass = Class.forName((String) fTargetData);
				}
				catch (Exception ex)
				{
					if (sLogger.isLoggable(Level.SEVERE))
					{
						String message = 
							"Could not resolve class name: " + fTargetData;
						
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"configureFromDescriptor", message, ex);
					}
				}
			}
			
			if (fTargetType != null)
			{
				if (fTargetType.equals(DataValueType._BOOLEAN) || 
					fTargetType.equals(DataValueType._BYTE) || 
					fTargetType.equals(DataValueType._CHAR) || 
					fTargetType.equals(DataValueType._SHORT) || 
					fTargetType.equals(DataValueType._INT) || 
					fTargetType.equals(DataValueType._LONG) || 
					fTargetType.equals(DataValueType.BOOLEAN) || 
					fTargetType.equals(DataValueType.BYTE) || 
					fTargetType.equals(DataValueType.CHARACTER) || 
					fTargetType.equals(DataValueType.SHORT) || 
					fTargetType.equals(DataValueType.INTEGER) || 
					fTargetType.equals(DataValueType.LONG))
				{
					fTargetData = new Long(fTargetValue);
				}
				else if (fTargetType.equals(DataValueType._FLOAT) || 
					fTargetType.equals(DataValueType._DOUBLE) || 
					fTargetType.equals(DataValueType.FLOAT) || 
					fTargetType.equals(DataValueType.DOUBLE))
				{
					fTargetData = new Double(fTargetValue);
				}
				else if (fTargetType.equals(DataValueType.BIT_ARRAY))
				{
					fTargetData = new BitArray(fTargetValue);
				}
				else if (fTargetType.equals(DataValueType.DATE))
				{
					// TODO: Implement this
				}
			}
		}
	}
	
	
	/**
	 *  Compares the given data Object and returns true if the comparison matches, 
	 *  false otherwise.
	 *  
	 *	@param data A data Object
	 *  @return True if the comparison is a match, false otherwise
	 */
	
	public boolean compare(Object data)
	{
		boolean result = false;
		
		if (fComparatorType != DataComparatorType.FALSE)
		{
			if (fComparatorType == DataComparatorType.TRUE)
			{
				result = true;
			}
			else if (data != null)
			{
				if (fComparatorType == DataComparatorType.NOT_NULL)
				{
					result = true;
				}
				else if (fComparatorType != DataComparatorType.NULL)
				{
					if (fComparatorType.equals(DataComparatorType.INSTANCEOF) && 
						(fTargetClass != null))
					{
						result = fTargetClass.isInstance(data);
					}
					else
					{
						if (data instanceof byte[])
						{
							data = new String((byte[]) data);
						}
						else if (data instanceof char[])
						{
							data = new String((char[]) data);
						}
						
						if (fComparatorType.equals(DataComparatorType.EQUALS))
						{
							result = data.equals(fTargetData);
						}
						else if (fComparatorType.equals(DataComparatorType.NOT_EQUALS))
						{
							result = (! data.equals(fTargetData));
						}
						else if (fComparatorType.equals(DataComparatorType.MATCHES))
						{
							result = fTargetPattern.matcher((String) data).matches();
						}
						else if (fTargetData instanceof Number)
						{
							double targetValue = ((Number) fTargetData).doubleValue();
							double dataValue = new Double(data.toString()).doubleValue();
							
							if (fComparatorType.equals(DataComparatorType.EQ))
							{
								result = (dataValue == targetValue);
							}
							else if (fComparatorType.equals(DataComparatorType.NEQ))
							{
								result = (dataValue != targetValue);
							}
							else if (fComparatorType.equals(DataComparatorType.LT))
							{
								result = (dataValue < targetValue);
							}
							else if (fComparatorType.equals(DataComparatorType.LTEQ))
							{
								result = (dataValue <= targetValue);
							}
							else if (fComparatorType.equals(DataComparatorType.GT))
							{
								result = (dataValue > targetValue);
							}
							else if (fComparatorType.equals(DataComparatorType.GTEQ))
							{
								result = (dataValue >= targetValue);
							}
						}
					}
				}
			}
			else if (fComparatorType == DataComparatorType.NULL)
			{
				result = true;
			}
		}
		
		return (result);
	}
}
