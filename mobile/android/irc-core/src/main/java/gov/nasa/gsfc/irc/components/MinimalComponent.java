//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/MinimalComponent.java,v 1.5 2006/01/23 17:59:51 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
// $Log: MinimalComponent.java,v $
// Revision 1.5  2006/01/23 17:59:51  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.4  2004/07/22 20:14:58  chostetter_cvs
// Data, Component namespace work
//
// Revision 1.3  2004/07/06 13:40:00  chostetter_cvs
// Commons package restructuring
//
// Revision 1.2  2004/06/03 00:19:59  chostetter_cvs
// Organized imports
//
// Revision 1.1  2004/06/02 23:59:41  chostetter_cvs
// More Namespace, DataSpace tweaks, created alogirthms package
//
//
//--- Warning ----------------------------------------------------------------
//This software is property of the National Aeronautics and Space
//Administration. Unauthorized use or duplication of this software is
//strictly prohibited. Authorized users are subject to the following
//restrictions:
//*	Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//*	The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//*	Altered versions of this software must be plainly marked as such.
//*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.components;

import gov.nasa.gsfc.commons.types.namespaces.NamespaceMemberBean;


/**
 *	A MinimalComponent is a Serializable bean that occupies a Namespace.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2006/01/23 17:59:51 $
 *	@author Carl F. Hostetter
 */

public interface MinimalComponent extends NamespaceMemberBean
{
	/**
	 * Returns the ComponentId of this Component.
	 *
	 * @return The ComponentId of this Component
	 **/
	
	public ComponentId getComponentId();
}
