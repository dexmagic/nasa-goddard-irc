//=== File Prolog ============================================================
//	This code was developed by AppNet, Inc. and NASA Goddard Space
//	Flight Center, Code 580 for the Instrument Remote Control (IRC)
//	project.
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
// $Log: XmlException.java,v $
// Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
// Initial version
//
// Revision 1.1.2.2  2004/03/24 20:31:32  chostetter_cvs
// New package structure baseline
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

package gov.nasa.gsfc.commons.xml;


/**
 * Abstract superclass of all XML exceptions.
 *
 * @version	$Date: 2004/07/12 14:26:24 $	
 * @author Carl F. Hostetter
**/

public class XmlException extends Exception
{
	/**
	 * Default constructor of a new XmlException.
	 *
	**/
	
	public XmlException()
	{

	}

	/**
	 *  Constructs a new XmlException having the given message String.
	 *
	 *  @param message The message of the new XmlException
	**/
	
	public XmlException(String message)
	{
		super(message);
	}
}
