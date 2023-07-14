//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/DataNameSelector.java,v 1.1 2005/09/08 22:18:32 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataNameSelector.java,v $
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

package gov.nasa.gsfc.irc.data.selection;

import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.irc.data.selection.description.DataNameSelectorDescriptor;


/**
 * A DataNameSelector selects only the name of a given named data Object
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/08 22:18:32 $
 * @author Carl F. Hostetter
 */

public class DataNameSelector extends AbstractDataSelector
{
	/**
	 * Constructs a new DataNameSelector that will select the name of any given 
	 * named data Object.
	 *
	 * @param descriptor A DataSelectorDescriptor
	 * @return A new DataNameSelector that will select the name of any given 
	 * 		named data Object		
	**/
	
	public DataNameSelector(DataNameSelectorDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 *  Returns the name of the given data Object (if any).
	 *  
	 *  @param data A named data Object
	 *  @return The name of the given data Object (if any)
	 */
	
	public Object select(Object data)
	{
		String result = null;
		
		if (data != null)
		{
			if (data instanceof HasName)
			{
				result = ((HasName) data).getName();
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
