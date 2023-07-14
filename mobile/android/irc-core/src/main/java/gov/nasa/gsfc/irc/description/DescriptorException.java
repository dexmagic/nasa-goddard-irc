//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   5	IRC	   1.4		 6/10/2002 3:16:50 PM John Higinbotham Support to
//		allow xinlcude editing.
//   4	IRC	   1.3		 11/13/2001 5:34:29 PMJohn Higinbotham Javadoc
//		update.
//   3	IRC	   1.2		 12/8/2000 11:26:31 AMJohn Higinbotham Code review
//		changes.
//   2	IRC	   1.1		 11/16/2000 4:49:32 PMJohn Higinbotham Cleanup.
//   1	IRC	   1.0		 10/16/2000 9:55:12 AMJohn Higinbotham 
//  $ 
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.description;

import gov.nasa.gsfc.irc.app.IrcException;

/**
 *  The DescriptorException class is an Exception that is thrown
 *  when an error associated with the descriptor construction or 
 *  use occurs. One of the most common places a this exception will
 *  be thrown is during the actual construction of the descriptor
 *  via the xml content.<P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			 $Date: 2004/07/12 14:26:23 $
 * @author			  John Higinbotham   
**/
public class DescriptorException extends IrcException
{
	Exception fRootException = null;

	/**
	 *  Default constructor of a DescriptorException. 
	 *
	 **/
	public DescriptorException()
	{
		this("DescriptorException");
	}

	/**
	 *  Constructs a DescriptorException with the given Exception
	 *  detail message.
	 *
	 *  @param message An Exception detail message
	 **/
	public DescriptorException(String message)
	{
		super(message);
	}

	/**
	 *  Constructs a DescriptorException with the given message 
	 *  and root exception. 
	 *
	 *  @param message	   An Exception detail message
	 *  @param rootException Root cause exception. 
	 **/
	public DescriptorException(String message, Exception rootException)
	{
		super(message);
		fRootException = rootException;
	}

	/**
	 *  Returns the root cause exception.
	 *
	 *  @return Root cause exception. 
	 **/
	public Exception getRootException()
	{
		return fRootException;  
	}
}
