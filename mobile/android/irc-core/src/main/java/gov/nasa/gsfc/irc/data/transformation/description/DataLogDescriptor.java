//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataLogDescriptor.java,v $
//  Revision 1.3  2006/05/03 23:20:17  chostetter_cvs
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
 * A DataLogDescriptor describes a means of logging data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public interface DataLogDescriptor extends IrcElementDescriptor
{
	/**
	 * Sets the DataLoggerDescriptor associated with this DataLogDescriptor 
	 * to the given DataLoggerDescriptor.
	 *
	 * @param logger The new DataLoggerDescriptor associated with this 
	 * 		DataLogDescriptor 
	 **/

	public void setDataLogger(DataLoggerDescriptor logger);


	/**
	 * Returns the DataLoggerDescriptor associated with this DataLogDescriptor 
	 * (if any).
	 *
	 * @return The DataLoggerDescriptor associated with this DataLogDescriptor 
	 *		(if any) 
	 **/

	public DataLoggerDescriptor getDataLogger();

}