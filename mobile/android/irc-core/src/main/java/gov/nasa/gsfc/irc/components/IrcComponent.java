//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/IrcComponent.java,v 1.10 2005/04/19 20:36:56 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
//  This class requires JDK 1.1 or later.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  * Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//  * The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//  * Altered versions of this software must be plainly marked as such.
//  * This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.components;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.HasDescriptor;


/**
 *	An IrcComponent is a MinimalComponent that has a dynamic Component State 
 *  and a Descriptor.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version $Date: 2005/04/19 20:36:56 $
 *	@author Carl F. Hostetter
 */

public interface IrcComponent extends 
	MinimalComponent, HasComponentState, HasDescriptor
{
	/**
	 *  Sets the Descriptor of this Component to the given Descriptor.
	 *  
	 *  @param descriptor A Descriptor
	**/

	public void setDescriptor(Descriptor descriptor);
}

//--- Development History  ---------------------------------------------------
//
// $Log: IrcComponent.java,v $
// Revision 1.10  2005/04/19 20:36:56  chostetter_cvs
// Organized imports
//
// Revision 1.9  2005/04/16 04:03:55  tames
// Changes to reflect refactored state and activity packages.
//
// Revision 1.8  2004/08/23 13:54:57  tames
// Added support for Startable components that are not IrcComponents.
//
// Revision 1.7  2004/07/16 15:18:31  chostetter_cvs
// Revised, refactored Component activity state
//
// Revision 1.6  2004/07/06 13:40:00  chostetter_cvs
// Commons package restructuring
//
// Revision 1.5  2004/06/15 20:04:03  chostetter_cvs
// Added ActivityStateModel, use for stative Objects
//
// Revision 1.4  2004/06/11 17:27:56  chostetter_cvs
// Further Input-related work
//
// Revision 1.3  2004/06/02 23:59:41  chostetter_cvs
// More Namespace, DataSpace tweaks, created alogirthms package
//
//
