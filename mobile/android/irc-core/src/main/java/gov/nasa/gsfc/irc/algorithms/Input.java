//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/algorithms/Input.java,v 1.8 2006/01/23 17:59:53 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
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

package gov.nasa.gsfc.irc.algorithms;

import gov.nasa.gsfc.irc.components.ManagedComponent;
import gov.nasa.gsfc.irc.components.IrcComponent;
import gov.nasa.gsfc.irc.data.DataRequester;


/**
 *  An Input is a DataRequester Component.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:53 $
 *  @author	Carl F. Hostetter
**/

public interface Input extends ManagedComponent, IrcComponent, DataRequester
{
	public static final String DEFAULT_NAME = "Input";
	
	/**
	 * Adds the given InputListener as a listener for various Data Events 
	 * from this Input.
	 *
	 * @param listener An InputListener
	 **/
	
	public void addInputListener(InputListener listener);
	
	
	/**
	 * Removes the given InputListener as a listener for Data Events from this 
	 * Input.
	 *
	 * @param listener An InputListener
	 **/
	
	public void removeInputListener(InputListener listener);
}

//--- Development History  ---------------------------------------------------
//
//	$Log: Input.java,v $
//	Revision 1.8  2006/01/23 17:59:53  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.7  2005/09/13 22:27:47  tames
//	Changes to reflect DataRequester refactoring
//	
//	Revision 1.6  2004/11/28 17:06:17  tames
//	Updated to reflect change in the ManagedComponent interface.
//	
//	Revision 1.5  2004/07/21 14:26:15  chostetter_cvs
//	Various architectural and event-passing revisions
//	
//	Revision 1.4  2004/07/06 21:57:12  chostetter_cvs
//	More BasisRequester, DataRequester work
//	
//	Revision 1.3  2004/06/11 17:27:56  chostetter_cvs
//	Further Input-related work
//	
//	Revision 1.2  2004/06/08 14:21:53  chostetter_cvs
//	Added child/parent relationship to Components
//	
//	Revision 1.1  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//	Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//	
