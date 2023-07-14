//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/events/DataSetEvent.java,v 1.3 2005/09/13 22:29:38 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.data.events;

import java.util.EventObject;

import gov.nasa.gsfc.irc.data.DataSet;


/**
 * A DataSetEvent contains newly-available data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/13 22:29:38 $
 * @author Carl F. Hostetter
 */

public class DataSetEvent extends EventObject
{
	private DataSet fDataSet;
	
	
	/**
	 * Constructs a new DataSetEvent for the given source and DataSet.
	 * 
	 * @param source The DataSetSource of the given DataSet
	 * @param dataSet A DataSet
	 */
	
	public DataSetEvent(Object source, DataSet dataSet)
	{
		super(source);
		
		fDataSet = dataSet;
	}
	
	
	/**
	 * Returns the DataSet contained in this DataSetEvent.
	 * 
	 * @return The DataSet contained in this DataSetEvent
	 */
	
	public DataSet getDataSet()
	{
		return (fDataSet);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataSetEvent.java,v $
//  Revision 1.3  2005/09/13 22:29:38  tames
//  Updates to reflect BasisBundleEvent refactoring.
//
//  Revision 1.2  2005/07/13 20:11:01  tames
//  File header change only.
//
//  Revision 1.1  2004/07/21 14:21:41  chostetter_cvs
//  Moved into subpackage
//
//  Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//  Initial version, checked in to support initial version of new components package
//
