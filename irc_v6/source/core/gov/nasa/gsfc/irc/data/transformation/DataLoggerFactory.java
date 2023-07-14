//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/DataLoggerFactory.java,v 1.2 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataLoggerFactory.java,v $
//  Revision 1.2  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.1  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
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

import gov.nasa.gsfc.irc.data.transformation.description.DataLogDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataLoggerDescriptor;


/**
 * A DataLoggerFactory creates and returns instances of DataLoggers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public interface DataLoggerFactory
{
	/**
	 *  Creates and returns a DataLogger appropriate to the given 
	 *  DefaultDataLogDescriptor.
	 *  
	 *  @param A DefaultDataLogDescriptor describing the desired data log scheme
	 *  @return A DataLogger appropriate to the given DefaultDataLogDescriptor
	 */
	
	public DataLogger getDataLogger(DataLogDescriptor descriptor);
	
	
	/**
	 *  Creates and returns a DataLogger appropriate to the given 
	 *  DataLoggerDescriptor.
	 *  
	 *  @param A DataLoggerDescriptor describing the desired data logger
	 *  @return A DataLogger appropriate to the given DataLoggerDescriptor
	 */
	
	public DataLogger getDataLogger(DataLoggerDescriptor descriptor);
}
