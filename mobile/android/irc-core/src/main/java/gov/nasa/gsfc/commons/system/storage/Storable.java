//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
// $Log: Storable.java,v $
// Revision 1.2  2004/07/06 13:40:00  chostetter_cvs
// Commons package restructuring
//
// Revision 1.1  2004/05/05 19:11:52  chostetter_cvs
// Further restructuring
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

package gov.nasa.gsfc.commons.system.storage;

import java.io.Serializable;

import gov.nasa.gsfc.commons.processing.annotation.HasAnnotations;
import gov.nasa.gsfc.commons.types.namespaces.HasName;


/**
 *  A Storable is any Object that can be stored.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2004/07/06 13:40:00 $
 *  @author C. Hostetter/588
**/
public interface Storable extends Serializable, HasName, HasAnnotations
{

}
