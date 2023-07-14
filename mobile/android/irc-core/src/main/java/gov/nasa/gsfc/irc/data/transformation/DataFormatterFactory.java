//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/DataFormatterFactory.java,v 1.2 2006/05/03 23:20:16 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataFormatterFactory.java,v $
//  Revision 1.2  2006/05/03 23:20:16  chostetter_cvs
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

package gov.nasa.gsfc.irc.data.transformation;

import gov.nasa.gsfc.irc.data.transformation.description.DataFormatDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataFormatterDescriptor;


/**
 * A DataFormatterFactory creates and returns instances of DataFormatters.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:16 $
 * @author Carl F. Hostetter
 */

public interface DataFormatterFactory
{
	/**
	 *  Creates and returns a DataFormatter appropriate to the given 
	 *  DataFormatDescriptor.
	 *  
	 *  @param A DataFormatDescriptor describing the desired data format scheme
	 *  @return A DataFormatter appropriate to the given DataFormatDescriptor
	 */
	
	public DataFormatter getDataFormatter(DataFormatDescriptor descriptor);
	
	
	/**
	 *  Creates and returns a DataFormatter appropriate to the given 
	 *  DataFormatterDescriptor.
	 *  
	 *  @param A DataFormatterDescriptor describing the desired data formatter
	 *  @return A DataFormatter appropriate to the given DataFormatterDescriptor
	 */
	
	public DataFormatter getDataFormatter(DataFormatterDescriptor descriptor);
}
