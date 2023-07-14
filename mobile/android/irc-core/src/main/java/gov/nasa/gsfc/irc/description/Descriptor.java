//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Descriptor.java,v $
//  Revision 1.6  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.4  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.3  2004/05/29 03:07:35  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2004/05/28 05:58:20  chostetter_cvs
//  More Namespace, DataSpace, Descriptor worl
//
//  Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//  Initial version
// 
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

import java.io.Serializable;

import gov.nasa.gsfc.commons.types.namespaces.HasFullyQualifiedName;


/**
 * A Descriptor is a named Object that describes other Objects.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/01/23 17:59:54 $
 * @author John Higinbotham   
**/

public interface Descriptor extends Cloneable, Serializable, HasFullyQualifiedName
{
	/**
	 * Returns a clone of this Descriptor.
	 *
	 * @return A clone of this Descriptor
	**/
	
	public Object clone();
}
