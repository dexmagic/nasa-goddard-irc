//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/DataParserFactory.java,v 1.3 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataParserFactory.java,v $
//  Revision 1.3  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
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

import gov.nasa.gsfc.irc.data.transformation.description.DataParseDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataParserDescriptor;


/**
 * A DataParserFactory creates and returns instances of DataParsers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public interface DataParserFactory
{
	/**
	 *  Creates and returns a DataParser appropriate to the given 
	 *  DataParseDescriptor.
	 *  
	 *  @param A DataParseDescriptor describing the desired data parse scheme
	 *  @return A DataParser appropriate to the given DefaultDataParseDescriptor
	 */
	
	public DataParser getDataParser(DataParseDescriptor descriptor);
	
	
	/**
	 *  Creates and returns a DataParser appropriate to the given 
	 *  DataParserDescriptor.
	 *  
	 *  @param A DataParserDescriptor describing the desired data parser
	 *  @return A DataParser appropriate to the given DataParserDescriptor
	 */
	
	public DataParser getDataParser(DataParserDescriptor descriptor);
}
