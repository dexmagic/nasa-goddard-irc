//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/algorithms/Algorithm.java,v 1.4 2006/01/23 17:59:53 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//	$Log: Algorithm.java,v $
//	Revision 1.4  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.3  2004/12/01 22:37:48  tames_cvs
//	Updated to reflect changes to the Composite interface. This allows the
//	ComponentManager and Composite components to use the same
//	interface.
//	
//	Revision 1.2  2004/07/11 21:19:44  chostetter_cvs
//	More Algorithm work
//	
//	Revision 1.1  2004/06/03 00:06:00  chostetter_cvs
//	Further Algorithm-related changes
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

import gov.nasa.gsfc.irc.components.ComponentNamespaceManager;
import gov.nasa.gsfc.irc.components.ManagedCompositeComponent;


/**
 *  An Algorithm is a ManagedCompositeComponent that comprises and manages a 
 *  Namespace of associated Inputs, Outputs, and Processors.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:53 $
 *  @author	Carl F. Hostetter
**/

public interface Algorithm extends ManagedCompositeComponent, 
	ComponentNamespaceManager, HasInputs, HasOutputs, HasProcessors
{
	public static final String DEFAULT_NAME = "Algorithm";
	
	/**
	 *  Returns the Input of this Algorithm that has the given sequenced (and thus 
	 *  Algorithm-unique) name.
	 *  
	 *  @param sequencedName The sequenced name of an Input of this Algorithm
	 *  @return The Input of this Algorithm that has the given sequenced name
	 */

	public Input getInputBySequencedName(String sequencedName);
	
	
	/**
	 *  Returns the Processor of this Algorithm that has the given sequenced (and 
	 *  thus Algorithm-unique) name.
	 *  
	 *  @param sequencedName The sequenced name of a Processor of this Algorithm
	 *  @return The Processor of this Algorithm that has the given sequenced name
	 */

	public Processor getProcessorBySequencedName(String sequencedName);
	

	/**
	 *  Returns the Output of this Algorithm that has the given sequenced (and thus 
	 *  Algorithm-unique) name.
	 *  
	 *  @param sequencedName The sequenced name of an Output of this Algorithm
	 *  @return The Output of this Algorithm that has the given sequenced name
	 */

	public Output getOutputBySequencedName(String sequencedName);
}
