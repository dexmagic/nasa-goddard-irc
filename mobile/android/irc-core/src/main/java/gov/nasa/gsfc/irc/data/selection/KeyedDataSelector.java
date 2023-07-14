//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/KeyedDataSelector.java,v 1.2 2006/02/07 21:07:50 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: KeyedDataSelector.java,v $
//  Revision 1.2  2006/02/07 21:07:50  chostetter_cvs
//  Keyed data selector now removes selected entry from input data map
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.2  2004/11/15 20:33:08  chostetter_cvs
//  Fixed remaining Message formatting issues
//
//  Revision 1.1  2004/10/18 22:58:15  chostetter_cvs
//  More data transformation work
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

package gov.nasa.gsfc.irc.data.selection;

import java.util.Map;

import gov.nasa.gsfc.irc.data.selection.description.KeyedDataSelectorDescriptor;


/**
 * A KeyedDataSelector selects a keyd data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/02/07 21:07:50 $
 * @author Carl F. Hostetter
 */

public class KeyedDataSelector extends AbstractDataSelector
{
	private Object fKey;
	

	/**
	 * Constructs a new KeyedDataSelector that will perform the data key 
	 * selection indicated by the given KeyedDataSelectorDescriptor.
	 *
	 * @param descriptor A KeyedDataSelectorDescriptor
	 * @return A new KeyedDataSelector that will perform the data range 
	 * 		selection indicated by the given KeyedDataSelectorDescriptor		
	**/
	
	public KeyedDataSelector(KeyedDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fKey = descriptor.getKey();
	}
	

	/**
	 * Constructs a new KeyedDataSelector that will select data by use of the given 
	 * key.
	 *
	 * @param key The key to be used for data selection
	 * @return A new KeyedDataSelector that will select data by use of the given 
	 * 		key		
	**/
	
	public KeyedDataSelector(Object key)
	{
		fKey = key;
	}
	

	/**
	 *  Returns the data selected by this DataSelector from the given data 
	 *  Object, or null if it selects nothing from the given data Object. 
	 *  NOTE that if the given data is a Map, the selected data is removed from 
	 *  the Map!
	 *  
	 *  @return The data selected by this DataSelector from the given data 
	 *  		Object, or null if it selects nothing from the given data Object
	 */
	
	public Object select(Object data)
	{
		Object result = null;
		
		if (data != null)
		{
			if (data instanceof Map)
			{
				Map dataMap = (Map) data;
				
				result = dataMap.get(fKey);
				
				dataMap.remove(fKey);
			}
			else
			{
				super.select(data);
			}
		}
		else
		{
			super.select(data);
		}
		
		return (result);
	}
}
