//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/BasisBundleId.java,v 1.15 2006/08/01 19:55:47 chostetter_cvs Exp $
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

package gov.nasa.gsfc.irc.data;

import gov.nasa.gsfc.commons.types.namespaces.MemberId;


/**
 * A BasisBundleId serves as a unique and persistent identifier of a 
 * BasisBundle.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/08/01 19:55:47 $
 * @author Carl F. Hostetter
 */

public interface BasisBundleId extends MemberId
{

}

//--- Development History  ---------------------------------------------------
//
//  $Log: BasisBundleId.java,v $
//  Revision 1.15  2006/08/01 19:55:47  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.14  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//
