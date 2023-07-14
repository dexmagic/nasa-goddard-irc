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

package gov.nasa.gsfc.irc.data;

import gov.nasa.gsfc.irc.app.Irc;

/**
 * Factory class for vending <code>BasisBundleId</code> objects. Where ever
 * possible, this factory will hand out references to shared
 * <code>BasisBundleId</code> instances.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/01/23 17:59:51 $
 * @author Troy Ames
 */
public class DefaultBasisBundleIdFactory
{
	/**
	 * Creates a <code>BasisBundleId</code> with the given fully qualified
	 * name.
	 * 
	 * @param fullyQualifiedName the fully qualified name of a BasisBundle
	 * @return a <code>BasisBundleId</code> object
	 */
	public BasisBundleId createBasisBundleId(String fullyQualifiedName)
	{
		BasisBundleId bundleId = 
			Irc.getDataSpace().getBasisBundleId(fullyQualifiedName);
		
		if (bundleId == null)
		{
			bundleId = new DefaultBasisBundleId(fullyQualifiedName);
		}
		
		return (bundleId);
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultBasisBundleIdFactory.java,v $
//  Revision 1.2  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2006/01/02 03:48:29  tames
//  Initial version.
//
//