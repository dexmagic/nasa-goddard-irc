//=== File Prolog ============================================================
//	This code was developed by AppNet, Inc. and NASA Goddard Space
//	Flight Center, Code 580 for the Instrument Remote Control (IRC)
//	project.
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
// $Log: 
//  3	IRC	   1.2		 11/13/2001 10:31:25 AMJohn Higinbotham Javadoc
//	   update.
//  2	IRC	   1.1		 8/18/1999 4:19:36 PM Melissa Hess	added
//	   implementation and comments
//  1	IRC	   1.0		 8/16/1999 9:45:18 AM Steve Clark	 
// $
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

package gov.nasa.gsfc.irc.app;

import gov.nasa.gsfc.commons.app.exceptions.AppException;


/**
 * Abstract superclass of all exceptions in IRC.
 *
 * @version	$Date: 2004/08/11 05:42:57 $	
 * @author	Steve Clark
**/
public class IrcException extends AppException
{
	/**
	 * Create a new IrcException.
	 **/
	public IrcException()
	{
		super();
	}
	
	/**
	 *  Creates a new IrcException with the specified message string.
	 *
	 *  @param text error message
	 **/
	public IrcException(String text)
	{
		super(text);
	}
	
	/**
	 * Constructs a new exception with the specified detail message and
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
	 */
	public IrcException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new exception with the specified cause and a detail
	 * message of <tt>(cause==null ? null : cause.toString())</tt> (which
	 * typically contains the class and detail message of <tt>cause</tt>).
	 * This constructor is useful for exceptions that are little more than
	 * wrappers for other throwables (for example, {@link
	 * java.security.PrivilegedActionException}).
	 *
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A <tt>null</tt> value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 */
	public IrcException(Throwable cause) {
		super(cause);
	}
}
