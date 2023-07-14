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

package gov.nasa.gsfc.irc.gui.swing;

import gov.nasa.gsfc.irc.app.IrcException;

/**
 *    This exception indicates there was a problem terminating an
 *  editing session in a propery editor.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/09/22 18:13:31 $
 *  @author	    Ken Wootton
 */
public class TerminateEditException extends IrcException
{
	/**
	 *    Constructor.
	 */
	public TerminateEditException()
	{
		super();
	}

	/**
	 *    Constructor.
	 *
	 *  @param msg  the message describing the problem.
	 */
	public TerminateEditException(String msg)
	{
		super(msg);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: TerminateEditException.java,v $
//  Revision 1.1  2004/09/22 18:13:31  tames_cvs
//  Relocated class or interface.
//
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
// 
