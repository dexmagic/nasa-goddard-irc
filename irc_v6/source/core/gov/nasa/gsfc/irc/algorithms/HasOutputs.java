//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/algorithms/HasOutputs.java,v 1.5 2006/01/23 17:59:53 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//	$Log: HasOutputs.java,v $
//	Revision 1.5  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.4  2004/07/15 17:48:55  chostetter_cvs
//	ComponentManager, property change work
//	
//	Revision 1.3  2004/06/04 05:34:42  chostetter_cvs
//	Further data, Algorithm, and Component work
//	
//	Revision 1.2  2004/06/03 00:06:00  chostetter_cvs
//	Further Algorithm-related changes
//	
//	Revision 1.1  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//	Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//	
//	Revision 1.1.2.2  2004/03/24 20:31:32  chostetter_cvs
//	New package structure baseline
//	
//	Revision 1.1.2.1  2004/01/24 01:02:17  chostetter_cvs
//	Defined Processor component
//	
//	Revision 1.1  2004/01/12 15:21:44  chostetter_cvs
//	Pipeline redesign
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

package gov.nasa.gsfc.irc.algorithms;

import java.util.Map;
import java.util.Set;


/**
 *  Implemented by all Objects having an associated Set of Outputs.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:53 $
 *  @author	Carl F. Hostetter
**/

public interface HasOutputs
{
	/**
	 *  Returns the current number of Outputs associated with this Object.
	 *  
	 *  @return	The current number of Outputs associated with this Object
	 **/

	public int getNumOutputs();
	
	
	/**
	 *  Returns the Set of Outputs associated with this Object.
	 *
	 *  @return The Set of Outputs associated with this Object
	 */

	public Set getOutputs();


	/**
	 *  Returns a Map of the Outputs associated with this Object, keyed by their 
	 *  fully-qualified names.
	 *
	 *  @return A Map of the Outputs associated with this Object, keyed by their 
	 *  		fully-qualified names
	 */

	public Map getOutputsByFullyQualifiedName();


	/**
	 *  Returns the Output in the Set of Outputs associated with this Object 
	 *  that has the given name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified name of an Output associated 
	 *  		with this Object
	 *  @return The Output in the Set of Outputs associated with this Object 
	 *  		that has the given name
	 */

	public Output getOutput(String name);
}
