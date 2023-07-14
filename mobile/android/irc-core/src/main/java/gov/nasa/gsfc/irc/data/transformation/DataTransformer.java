//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataTransformer.java,v $
//  Revision 1.5  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.4  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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

package gov.nasa.gsfc.irc.data.transformation;

import java.util.Map;


/**
 * A DataTransformer transforms data from one form to another in accordance 
 * with a related transformation description.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/14 19:05:53 $ 
 * @author Carl F. Hostetter   
**/

public interface DataTransformer
{
	/**
	 * Causes this DataTransformer to transform the given data Object and return 
	 * the results.
	 *
	 * @param data The data to be transformed
	 * @param context An optional Map of contextual information
	 * @return The result of transforming the given data
	 * @throws UnsupportedOperationException if this DataTransformer is unable to 
	 * 		transform the given data
	**/
	
	public Object transform(Object data, Map context)
		throws UnsupportedOperationException;
}
