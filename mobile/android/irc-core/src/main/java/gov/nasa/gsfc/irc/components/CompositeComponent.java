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

package gov.nasa.gsfc.irc.components;

/**
 *	A CompositeComponent is a Component that itself comprises and manages a 
 *  Namespace of Components.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 21:26:11 $
 * @author 	Troy Ames
 */

public interface CompositeComponent extends ComponentNamespaceManager, 
	IrcComponent
{

}


//--- Development History  ---------------------------------------------------
//
//  $Log: CompositeComponent.java,v $
//  Revision 1.4  2006/03/14 21:26:11  chostetter_cvs
//  Fixed manager proxy issue that was preventing component browser from updating
//
//  Revision 1.3  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.2  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2004/12/02 04:10:33  tames
//  Initial Version
//
//