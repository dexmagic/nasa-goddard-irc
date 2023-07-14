//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/algorithms/Processor.java,v 1.10 2006/01/23 17:59:53 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//This software is property of the National Aeronautics and Space
//Administration. Unauthorized use or duplication of this software is
//strictly prohibited. Authorized users are subject to the following
//restrictions:
//*	Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//*	The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//*	Altered versions of this software must be plainly marked as such.
//*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.algorithms;

import gov.nasa.gsfc.irc.components.ManagedComponent;
import gov.nasa.gsfc.irc.components.IrcComponent;


/**
 *	A Processor is a Component that receives and processes input DataSets 
 *  and writes its results to output DataSets managed by by one or more 
 *  associated Outputs.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2006/01/23 17:59:53 $
 *	@author Carl F. Hostetter
 */

public interface Processor extends ManagedComponent, IrcComponent, InputListener
{
	/**
	 *  Adds the given Output to the Set of Outputs associated with this 
	 *  Processor.
	 *
	 *  @param output The Output to be added to this Processor
	 */

	public void addOutput(Output output);


	/**
	 *  Removes the Output having the given name from the Set of Outputs 
	 *  associated with this Processor.
	 * 
	 *  @param name The name of the Output to be removed from this Processor
	 */

	public void removeOutput(String name);
}

//--- Development History  ---------------------------------------------------
//
// $Log: Processor.java,v $
// Revision 1.10  2006/01/23 17:59:53  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.9  2005/02/02 20:47:01  chostetter_cvs
// Revised, refined input/output BasisBundle mapping, access to management methods, and documentation of same
//
// Revision 1.8  2004/11/28 17:00:21  tames
// Updated to reflect change in the ManagedComponent interface.
//
// Revision 1.7  2004/07/28 20:17:02  chostetter_cvs
// Implemented selectable and adaptive default output BasisBundle sizing
//
// Revision 1.6  2004/07/21 14:26:15  chostetter_cvs
// Various architectural and event-passing revisions
//
// Revision 1.5  2004/07/17 01:25:58  chostetter_cvs
// Refactored test algorithms
//
// Revision 1.4  2004/07/11 21:19:44  chostetter_cvs
// More Algorithm work
//
// Revision 1.3  2004/06/08 14:21:53  chostetter_cvs
// Added child/parent relationship to Components
//
// Revision 1.2  2004/06/04 05:34:42  chostetter_cvs
// Further data, Algorithm, and Component work
//
// Revision 1.1  2004/06/02 23:59:41  chostetter_cvs
// More Namespace, DataSpace tweaks, created alogirthms package
//
// Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
// Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//
