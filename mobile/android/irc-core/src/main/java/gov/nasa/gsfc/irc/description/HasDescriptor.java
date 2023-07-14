//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/description/HasDescriptor.java,v 1.4 2004/06/04 18:58:25 tames_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//	$Log: HasDescriptor.java,v $
//	Revision 1.4  2004/06/04 18:58:25  tames_cvs
//	Removed setDescriptor method. A generic setDescriptor method 
//	is not that very useful.
//	
//	Revision 1.3  2004/06/04 15:14:11  tames_cvs
//	Added setDescriptor method.
//	
//	Revision 1.2  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
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

package gov.nasa.gsfc.irc.description;


/**
 *  The HasDescriptor interface specifies the public methods that all 
 *  Objects having Descriptors must implement.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2004/06/04 18:58:25 $
 *	@author Carl F. Hostetter
**/
public interface HasDescriptor
{
	/**
	 * Returns the Descriptor associated with this Object
	 *  
	 * @return	The Descriptor associated with this Object
	**/
	public Descriptor getDescriptor();
}
