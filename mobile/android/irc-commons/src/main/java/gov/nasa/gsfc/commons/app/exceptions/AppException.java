//=== File Prolog ============================================================
//	This code was developed by AppNet, Inc. and NASA Goddard Space
//	Flight Center, Code 580 for the Instrument Remote Control (IRC)
//	project.
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
// $Log: AppException.java,v $
// Revision 1.3  2004/08/11 05:42:57  tames
// Script support
//
// Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
// Initial version
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

package gov.nasa.gsfc.commons.app.exceptions;


/**
 * Abstract superclass of all application exceptions.
 *
 * @version	$Date: 2004/08/11 05:42:57 $	
 * @author Carl F. Hostetter
**/

public class AppException extends Exception
{
	/**
	 * Default constructor of a new AppException.
	 *
	**/
	
	public AppException()
	{

	}

	/**
	 *  Constructs a new AppException having the given message String.
	 *
	 *  @param message The message of the new AppException
	**/
	
	public AppException(String message)
	{
		super(message);
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
    public AppException(String message, Throwable cause) {
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
    public AppException(Throwable cause) {
        super(cause);
    }
}
