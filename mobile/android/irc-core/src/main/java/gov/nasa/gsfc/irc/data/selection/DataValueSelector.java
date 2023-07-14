//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/DataValueSelector.java,v 1.1 2006/04/27 23:31:09 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataValueSelector.java,v $
//  Revision 1.1  2006/04/27 23:31:09  chostetter_cvs
//  Added support for field value selection in determinants
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

import gov.nasa.gsfc.irc.data.selection.description.DataValueSelectorDescriptor;


/**
 * A DataValueSelector selects only the value of a given keyed data Object 
 * (e.g., a Map.Entry).
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 23:31:09 $
 * @author Carl F. Hostetter
 */

public class DataValueSelector extends AbstractDataSelector
{
	/**
	 * Constructs a new DataValueSelector that will select the value of a given 
	 * keyed data Object (e.g., a Map.Entry).
	 *
	 * @param descriptor A DataSelectorDescriptor
	 * @return A new DataValueSelector that will select the name of any given 
	 * 		named data Object		
	**/
	
	public DataValueSelector(DataValueSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 *  Returns the value of the given keyed data Object (if any).
	 *  
	 *  @param data A named data Object
	 *  @return The value of the given keyed data Object (if any)
	 */
	
	public Object select(Object data)
	{
		Object result = null;
		
		if (data != null)
		{
			if (data instanceof Map.Entry)
			{
				result = ((Map.Entry) data).getValue();
			}
		}
		else
		{
			super.select(data);
		}
		
		return (result);
	}
}
