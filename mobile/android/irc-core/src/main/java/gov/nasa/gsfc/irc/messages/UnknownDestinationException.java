//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: UnknownDestinationException.java,v $
//	Revision 1.1  2004/08/06 14:26:28  tames_cvs
//	Initial version
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

package gov.nasa.gsfc.irc.messages;


/**
 * Indicates that no message destination exists with the specified type name.
 *
 * @version     $Date: 2004/08/06 14:26:28 $
 * @author      Jeremy Jones
**/
public class UnknownDestinationException extends MessageException
{
	public UnknownDestinationException(String destinationName)
	{
		super(null, "Unknown message destination: " + destinationName);
	}
}
