//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/PortException.java,v 1.1 2004/10/14 15:16:51 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: PortException.java,v $
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/04/30 20:32:19  tames_cvs
//  Initial Version
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

package gov.nasa.gsfc.irc.devices.ports;



/**
 *  A PortException is a generic Exception produced by Ports that may contain a
 *  more specific Port-related Exception (e.g., IOException,
 *  UnsupportCommOperationException, etc.).
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	07/01/99
 *  @author		C. Hostetter/588
**/

public class PortException extends Exception
{
	private Exception fSpecificException = null;

	/**
	 *  Constructs a generic PortException containing the given specific
	 *  Exception.
	 *
	 *  @param	specificException	A specific Exception
	**/

	public PortException(Exception specificException)
	{
		fSpecificException = specificException;
	}


	/**
	 *  Constructs a generic PortException containing the given Exception
	 *  detail String and specific Exception.
	 *
	 *  @param	exceptionMessage	An Exception detail String
	 *  @param	specificException	A specific Exception
	**/

	public PortException(String exceptionDetail, Exception specificException)
	{
		super(exceptionDetail);
		
		fSpecificException = specificException;
	}


	/**
	 *  Get the specific Exception contained in this generic PortException.
	 *
	 *  @return	an Exception object
	**/

	public Exception getSpecificException()
	{
		return (fSpecificException);
	}
}
