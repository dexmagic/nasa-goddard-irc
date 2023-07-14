//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: PolynomialBias.java,v $
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/07/06 13:47:34  chostetter_cvs
//	More commons package restructuring
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

package gov.nasa.gsfc.commons.numerics.math.calculations;

/**
 * The PolynomialBias is used when input data is to be calibrated 
 * using a polynomial function for a array.
 *  
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:24 $
 *  @author John Higinbotham 
**/
public class PolynomialBias implements Calculation
{

	final double[] fCoeffs;
	final int fOrder;
	final int fMaxOrder = 5;

	/**
	 * Create a new PolynomialBias. 
	 * <P>
	 * @param coeffs Polynomial coffecients from low to high order.
	**/
	public PolynomialBias(double[] coeffs)
	{
		fCoeffs = coeffs;
		fOrder  = coeffs.length - 1;
		if (fOrder > fMaxOrder)
		{
			throw new IllegalArgumentException ("PolynomialCalculation does not support order > " + fMaxOrder);
		}
	}

	/**
	 * Apply calculation. 
	 * <P>
	 * @param   src			Source array.
	 * @param   srcOffset	Starting offset in source array.
	 * @param   dst			Destination array.
	 * @param   dstOffset	Starting offset in destination array.
	 * @param   count		Number of elements to process.
	**/
	public void apply(double[] src, int srcOff, double[] dst, int dstOff, int count)
	{
		srcOff--;
		dstOff--;
		final double[] cc = fCoeffs;

		while (count-- > 0)
		{
			double x = src[++srcOff];
			switch (fOrder)
			{
				case -1: dst[++dstOff] = 0;
				case 0: dst[++dstOff] = cc[0];
						break;
				case 1: dst[++dstOff] = cc[0] + cc[1]*x;
						break;
				case 2: dst[++dstOff] = cc[0] + (cc[1] + cc[2]*x)*x;
						break;
				case 3: dst[++dstOff] = cc[0] + (cc[1] + (cc[2] + cc[3]*x)*x)*x;
						break;
				case 4: dst[++dstOff] = 
							cc[0] + (cc[1] + (cc[2] + (cc[3] 
							+ cc[4]*x)*x)*x)*x;
						break;
				case 5: dst[++dstOff] = 
							cc[0] + (cc[1] + (cc[2] + (cc[3] 
								+ (cc[4] + cc[5]*x)*x)*x)*x)*x;
						break;
				default:
				throw new Error ("PolynomialCalculation Internal error, order=" + fOrder);
			}
		}
	}
}
