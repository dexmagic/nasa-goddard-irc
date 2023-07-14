//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/DefaultBasisBundleFactory.java,v 1.7 2006/01/23 17:59:51 chostetter_cvs Exp $
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

import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;


/**
 *  A BasisBundleFactory creates and configures new instances of BasisBundles 
 *  upon request.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:51 $
 *  @author Carl F. Hostetter
 */
public class DefaultBasisBundleFactory implements BasisBundleFactory
{
	/**
	 *	Creates and returns a new BasisBundle for the given BasisBundleSource, 
	 *  as described by the given BasisBundleDescriptor, and whose Buffers are 
	 *  all of the given size.
	 *
	 *	@param descriptor A BasisBundleDescriptor that describes the desired new 
	 *		BasisBundle
	 *	@param source The BasisBundleSource requesting the new BasisBundle
	 *	@param bufferSize The size of the Buffers of the new BasisBundle
	 *  @return A new BasisBundle as described by the given 
	 *  		BasisBundleDescriptor
	 */
	
	public BasisBundle createBasisBundle(BasisBundleDescriptor descriptor, 
		BasisBundleSource source, int bufferSize)
	{
		return (new DefaultBasisBundle(descriptor, source, bufferSize));	
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultBasisBundleFactory.java,v $
//	Revision 1.7  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.6  2005/07/14 22:01:40  tames
//	Refactored data package for performance.
//	
//	Revision 1.5  2004/09/02 19:39:57  chostetter_cvs
//	Initial data-description redesign work
//	
//	Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.3  2004/05/27 15:57:16  chostetter_cvs
//	Data-related changes
//	
//	Revision 1.2  2004/05/16 21:54:27  chostetter_cvs
//	More work
//	
//	Revision 1.1  2004/05/14 19:59:58  chostetter_cvs
//	Initial version, checked in to support initial version of new components package
//		  
