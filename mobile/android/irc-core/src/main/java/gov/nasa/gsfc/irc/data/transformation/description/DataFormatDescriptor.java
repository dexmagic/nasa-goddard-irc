//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataFormatDescriptor.java,v $
//  Revision 1.6  2006/05/03 23:20:17  chostetter_cvs
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

package gov.nasa.gsfc.irc.data.transformation.description;

import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataFormatDescriptor describes a means of formatting data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public interface DataFormatDescriptor extends IrcElementDescriptor 
{
	/**
	 * Sets the DataFormatterDescriptor associated with this 
	 * DataFormatDescriptor to the given DataFormatterDescriptor.
	 *
	 * @param formatter The new DataFormatterDescriptor associated with this 
	 * 		DataFormatDescriptor 
	**/
	
	public void setDataFormatter(DataFormatterDescriptor formatter);
	

	/**
	 * Returns the DataFormatterDescriptor associated with this 
	 * DataFormatDescriptor (if any).
	 *
	 * @return The DataFormatterDescriptor associated with this 
	 * 		DataFormatDescriptor (if any) 
	**/
	
	public DataFormatterDescriptor getDataFormatter();
}
