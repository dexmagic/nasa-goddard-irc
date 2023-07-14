//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/AbstractDataFormatter.java,v 1.8 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataFormatter.java,v $
//  Revision 1.8  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.7  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.6  2006/04/07 22:27:18  chostetter_cvs
//  Fixed problem with applying field formatting to all fields, tightened syntax
//
//  Revision 1.5  2005/10/25 21:38:42  chostetter_cvs
//  Fixed binary data formatting
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

import java.util.HashMap;
import java.util.Map;

import gov.nasa.gsfc.commons.types.arrays.BitArray;
import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.selection.DataSelectorFactory;
import gov.nasa.gsfc.irc.data.selection.DefaultDataSelectorFactory;
import gov.nasa.gsfc.irc.data.selection.KeyedDataSelector;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataSourceSelectionDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataTargetSelectionDescriptor;


/**
 * A DataFormatter formats data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractDataFormatter implements DataFormatter
{
	protected static final DataSelectorFactory sDataSelectorFactory =
		DefaultDataSelectorFactory.getInstance();
	protected static final DataFormatterFactory sDataFormatterFactory = 
		DefaultDataFormatterFactory.getInstance();
	
	private DataFormatterDescriptor fDescriptor;
	
	private String fName = "Anonymous";
	
	private DataSelector fSource;
	private DataSelector fTarget;
	private boolean fUseDataNameAsName = false;
	private boolean fUseNameAsKeyedValueSelector = false;
	
	private static Map fCachedNamedSourceSelectors;
	
	
	/**
	 * Default constructor of a new AbstractDataFormatter.
	 *
	**/
	
	public AbstractDataFormatter()
	{
		
	}
	

	/**
	 * Constructs a new AbstractDataFormatter that will perform the data 
	 * selection described by the given DataFormatterDescriptor.
	 *
	 * @param descriptor A DataFormatterDescriptor
	 * @return A new NamedDataFormatter that will perform the data selection 
	 * 		described by the given NamedDataFormatterDescriptor		
	**/
	
	public AbstractDataFormatter(DataFormatterDescriptor descriptor)
	{
		setDescriptor(descriptor);
	}
	

	/**
	 * Sets the DataFormatterDescriptor associated with this DataFormatter to 
	 * the given DataFormatterDescriptor, and configures this DataFormatter 
	 * in accordance with it.
	 *
	 * @param descriptor The DataFormatterDescriptor according to which to 
	 * 		configure this DataFormatter
	**/
	
	public void setDescriptor(DataFormatterDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this AbstractDataFormatter in accordance with its current 
	 * DataFormatterDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			fName = fDescriptor.getName();
			
			DataSourceSelectionDescriptor sourceSelectionDescriptor = 
				fDescriptor.getSource();
			
			if (sourceSelectionDescriptor != null)
			{
				fSource = sDataSelectorFactory.getDataSelector
					(sourceSelectionDescriptor);
			}
			
			DataTargetSelectionDescriptor targetSelectionDescriptor = 
				fDescriptor.getTarget();
			
			if (targetSelectionDescriptor != null)
			{
				fTarget = sDataSelectorFactory.getDataSelector
					(targetSelectionDescriptor);
			}
		}
		
		fUseDataNameAsName = fDescriptor.usesDataNameAsName();
		
		if ((fSource == null) && fUseDataNameAsName)
		{
			useDataNameAsName();
		}
		
		fUseNameAsKeyedValueSelector = fDescriptor.usesNameAsKeyedValueSelector();
		
		if (fUseNameAsKeyedValueSelector)
		{
			useNameAsKeyedValueSelector();
		}
	}
	
	
	/**
	 *  Returns the DataFormatterDescriptor that describes the data parsing 
	 *  performed by this DataFormatter.
	 *  
	 *  @return The DataFormatterDescriptor that describes the data parsing 
	 *  		performed by this DataFormatter
	 */
	
	public DataFormatterDescriptor getDescriptor()
	{
		return (fDescriptor);
	}
	
	
	/**
	 *  Updates the named source selector of this DataFormatter (if any) in response 
	 *  to a name change.
	 *  
	 */
	
	private void updateNamedSourceSelector()
	{
		String name = getName();
		
		if (name != null)
		{
			if (fCachedNamedSourceSelectors == null)
			{
				fCachedNamedSourceSelectors = new HashMap();
			}
			
			fSource = (KeyedDataSelector) 
				fCachedNamedSourceSelectors.get(name);
			
			if (fSource == null)
			{
				fSource = new KeyedDataSelector(name);
				fCachedNamedSourceSelectors.put(name, fSource);
			}
		}
	}
	
	
	/**
	 *  Sets the name of this DataFormatter to the given name.
	 *  
	 *  @param name The new name of this DataFormatter
	 */
	
	void setName(String name)
	{
		fName = name;
		
		if (fUseNameAsKeyedValueSelector)
		{
			updateNamedSourceSelector();
		}
	}
	
	
	/**
	 *  Returns the name of this DataFormatter.
	 *  
	 *  @return The name of this DataFormatter
	 */
	
	public String getName()
	{
		return (fName);
	}
	
	
	/**
	 *  Returns the fully-qualified name of this DataFormatter.
	 *  
	 *  @return The fully-qualified name of this DataFormatter
	 */
	
	public String getFullyQualifiedName()
	{
		return (fName);
	}
	
	
	/**
	 *  Returns the source DataSelector associated with this DataFormatter (if any).
	 *  
	 *  @return The source DataSelector associated with this DataFormatter (if any)
	 */
	
	public DataSelector getSource()
	{
		return (fSource);
	}
	
	
	/**
	 *  Returns the target DataSelector associated with this DataFormatter (if any).
	 *  
	 *  @return The target DataSelector associated with this DataFormatter (if any)
	 */
	
	public DataSelector getTarget()
	{
		return (fTarget);
	}
	
	
	/**
	 * Configures this DataFormatter to use the name of the data it is formatting 
	 * as its name.
	 *
	**/
	
	void useDataNameAsName()
	{
		fUseDataNameAsName = true;
	}
	

	/**
	 * Returns true if this DataFormatter is configured to use the name of the data 
	 * it is formatting as its name, false otherwise.
	 * 
	 * @return True if this DataFormatter is configured to use the name of the data 
	 * 		it is formatting as its name, false otherwise
	**/
	
	public boolean usesDataNameAsName()
	{
		return (fUseDataNameAsName);
	}
	

	/**
	 * Configures this FieldFormatter to use its name as its value key selector.
	 *
	**/
	
	void useNameAsKeyedValueSelector()
	{
		fUseNameAsKeyedValueSelector = true;
		
		updateNamedSourceSelector();
	}
	
	
	/**
	 * Returns true if this FieldFormatter is configured to use its name as a keyed 
	 * data selector for its value, false otherwise.
	 *
	 * @return True if this FieldFormatter is configured to use its name as a keyed 
	 * 		data selector for its value, false otherwise
	 **/

	public boolean usesNameAsKeyedValueSelector()
	{
		return (fUseNameAsKeyedValueSelector);
	}
	
	
	/**
	 * Appends the given new data to the given previous data.
	 *
	 * @param previousData The data onto which the given new data is to be appended
	 * @param newData The data to be appended to the given previous data
	 * @return The result of the append
	**/
	
	protected Object append(Object previousData, Object newData)
	{
		Object result = previousData;
		
		if (previousData != null)
		{
			if (newData != null)
			{
				if (newData instanceof BitArray)
				{
					if (previousData instanceof BitArray)
					{
						((BitArray) previousData).prepend((BitArray) newData);
						
						result = previousData;
					}
					else
					{
						byte[] previousBytes = previousData.toString().getBytes();
						BitArray previousBits = new BitArray(previousBytes);
						
						previousBits.prepend((BitArray) newData);
						
						result = previousBits;
					}
				}
				else
				{
					if (previousData instanceof BitArray)
					{
						byte[] newBytes = newData.toString().getBytes();
						BitArray newBits = new BitArray(newBytes);
						
						((BitArray) previousData).prepend(newBits);
						
						result = previousData;
					}
					else if (previousData instanceof StringBuffer)
					{
						((StringBuffer) previousData).append(newData);
						
						result = previousData;
					}
					else
					{
						StringBuffer previousBuffer = 
							new StringBuffer(previousData.toString());
						
						previousBuffer.append(newData);
						
						result = previousBuffer;
					}
				}
			}
		}
		else
		{
			result = newData;
		}
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this DataFormatter.
	 *
	 *  @return A String representation of this DataFormatter
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataFormatter " + fDescriptor);
		
		return (result.toString());
	}
}
