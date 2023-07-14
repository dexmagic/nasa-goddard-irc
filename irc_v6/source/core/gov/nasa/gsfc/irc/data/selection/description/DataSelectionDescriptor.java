//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataSelectionDescriptor.java,v $
//  Revision 1.9  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

package gov.nasa.gsfc.irc.data.selection.description;

import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataSelectionDescriptor describes a means of selecting data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public interface DataSelectionDescriptor extends IrcElementDescriptor
{
	/**
	 * Sets the DataSelectorDescriptor associated with this 
	 * DataSelectionDescriptor to the given DataSelectorDescriptor.
	 *
	 * @param descriptor The DataSelectorDescriptor associated with this 
	 * 		DataSelectionDescriptor 
	**/
	
	public void setDataSelector(DataSelectorDescriptor descriptor);
	

	/**
	 * Returns the DataSelectorDescriptor associated with this 
	 * 		DataSelectionDescriptor (if any).
	 *
	 * @return The DataSelectorDescriptor associated with this 
	 * 		DataSelectionDescriptor (if any) 
	**/
	
	public DataSelectorDescriptor getDataSelector();
}
