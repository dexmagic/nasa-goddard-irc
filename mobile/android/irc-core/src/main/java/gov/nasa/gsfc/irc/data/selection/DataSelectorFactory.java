//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/DataSelectorFactory.java,v 1.2 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataSelectorFactory.java,v $
//  Revision 1.2  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

package gov.nasa.gsfc.irc.data.selection;

import gov.nasa.gsfc.irc.data.selection.description.DataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectorDescriptor;


/**
 * A DataSelectorFactory creates and returns instances of Selectors.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public interface DataSelectorFactory
{
	/**
	 *  Creates and returns a DataSelector appropriate to the given 
	 *  DataSelectorDescriptor.
	 *  
	 *  @param A DataSelectorDescriptor describing the desired data selector
	 *  @return A DataSelector appropriate to the given 
	 *  		DataSelectorDescriptor
	 */
	
	public DataSelector getDataSelector(DataSelectorDescriptor descriptor);
	
	
	/**
	 *  Creates and returns a DataSelector appropriate to the given 
	 *  DataSelectionDescriptor.
	 *  
	 *  @param A DataSelectionDescriptor describing the desired selection scheme
	 *  @return A DataSelector appropriate to the given 
	 *  		DataSelectionDescriptor
	 */
	
	public DataSelector getDataSelector
		(DataSelectionDescriptor descriptor);
}
