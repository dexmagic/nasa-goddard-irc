//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/DataParser.java,v 1.3 2005/09/14 20:14:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataParser.java,v $
//  Revision 1.3  2005/09/14 20:14:48  chostetter_cvs
//  Added ability to use context information to disambiguate (yes, Bob, disambiguate) BasisSet data selection
//
//  Revision 1.2  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
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

import java.util.Map;

import gov.nasa.gsfc.commons.types.namespaces.HasName;


/**
 * A DataStreamParser parses a given data Object into a Map representation.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/14 20:14:48 $
 * @author Carl F. Hostetter
 */

public interface DataParser extends HasName
{
	/**
	 *  Parses the given Object into a Map representation and returns the result.
	 *  
	 *  @param data The data Object to be parsed
	 *  @param context An optional Map of contextual information
	 *  @return The parsed Map representation of the given dataObject
	 */
	
	public Map parse(Object data, Map context);
}
