//=== File Prolog ============================================================
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

package gov.nasa.gsfc.commons.properties.state;

import java.io.Serializable;


/**
 *  A StateModel maintains a State.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2005/04/16 03:58:58 $
 *  @author Carl F. Hostetter
 */

public interface StateModel extends Serializable, HasState
{	
	/**
	 *	Sets the State of this StateModel to the given State
	 *
	 *  @param state The new State of this StateModel
	 */	 
	public void setState(State state);
}

//--- Development History  ---------------------------------------------------
//
//  $Log: StateModel.java,v $
//  Revision 1.2  2005/04/16 03:58:58  tames
//  Refactored state package
//
//  Revision 1.1  2004/07/16 15:18:31  chostetter_cvs
//  Revised, refactored Component activity state
//