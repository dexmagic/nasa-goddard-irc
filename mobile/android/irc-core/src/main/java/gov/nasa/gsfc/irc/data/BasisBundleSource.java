//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/BasisBundleSource.java,v 1.8 2006/08/01 19:55:47 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//	$Log: BasisBundleSource.java,v $
//	Revision 1.8  2006/08/01 19:55:47  chostetter_cvs
//	Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//	
//	Revision 1.7  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.6  2005/09/14 19:05:53  chostetter_cvs
//	Added optional context Map to data transformation operations
//	
//	Revision 1.5  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.4  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.3  2004/05/28 05:58:19  chostetter_cvs
//	More Namespace, DataSpace, Descriptor worl
//	
//	Revision 1.2  2004/05/16 21:54:27  chostetter_cvs
//	More work
//	
//	Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//	Initial version, checked in to support initial version of new components package
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

package gov.nasa.gsfc.irc.data;

import gov.nasa.gsfc.commons.processing.creation.Creator;


/**
 *  A BasisBundleSource is a Creator of BasisBundles.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/08/01 19:55:47 $
 *  @author	Carl F. Hostetter
**/

public interface BasisBundleSource extends Creator
{
	public static final String BASIS_BUNDLE_SOURCE_KEY = "BasisBundleSource";
}

