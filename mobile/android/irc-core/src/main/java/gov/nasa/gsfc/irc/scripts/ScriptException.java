//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/scripts/ScriptException.java,v 1.4 2006/01/23 17:59:53 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ScriptException.java,v $
//  Revision 1.4  2006/01/23 17:59:53  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2005/04/12 15:35:18  tames_cvs
//  Modified the Scripts and ScriptException classes to make them consistent
//  with the pattern used in the Messages class.
//
//  Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2004/08/11 05:42:57  tames
//  Script support
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


package gov.nasa.gsfc.irc.scripts;

import org.apache.bsf.BSFException;

import gov.nasa.gsfc.irc.app.IrcException;



/**
 * An ScriptException may wrap another exception 
 * (such as an BSFException) as a ScriptException.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2006/01/23 17:59:53 $
 * @author		Troy Ames
**/
public class ScriptException extends IrcException
{
	/**
	 *  Creates a new ScriptException with the specified message string.
	 *
	 *  @param text error message
	 **/
	public ScriptException(String text)
	{
		super(text);
	}

	/**
     * Constructs a new ScriptException with the specified cause.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public ScriptException(Throwable cause)
    {
		super(cause.getMessage(), cause);

		if (cause instanceof BSFException)
		{
			//TODO do something reasaonable with this info
			((BSFException) cause).getReason();			
		}
    }

    /**
     * Constructs a new ScriptException with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
	 * @param	bsfException	The underlying BSF exception
     */
    public ScriptException(String message, Throwable cause)
    {
		super(message, cause);
		
		if (cause instanceof BSFException)
		{
			//TODO do something reasaonable with this info
			((BSFException) cause).getReason();			
		}
    }
}