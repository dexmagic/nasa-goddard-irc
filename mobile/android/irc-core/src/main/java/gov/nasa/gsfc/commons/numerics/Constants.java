//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/Constants.java,v 1.3 2005/11/14 22:01:12 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: Constants.java,v $
//	Revision 1.3  2005/11/14 22:01:12  chostetter_cvs
//	Removed dependency on ibm array lib
//	
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
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

package gov.nasa.gsfc.commons.numerics;

import org.jscience.mathematics.numbers.Complex;


/**
 *  This Constants class holds various mathematical constants.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version	 $Date: 2005/11/14 22:01:12 $
 *	@author Carl Hostetter
**/

public interface Constants
{
	public static final double SQRT_OF_TWO = Math.sqrt(2.0D);

	public static final double LOG_OF_TWO = Math.log(2.0D);
	public static final double INVERSE_LOG_OF_TWO = 1.0D / LOG_OF_TWO;

	public static final double PI = Math.PI;
	public static final double MINUS_PI = (-PI);

	public static final double TWO_PI = PI * 2.0D;
	public static final double MINUS_TWO_PI = (-TWO_PI);

	public static final Complex TWO_PI_I = Complex.I.multiply(TWO_PI);
	public static final Complex MINUS_TWO_PI_I = TWO_PI_I.negate();
}
