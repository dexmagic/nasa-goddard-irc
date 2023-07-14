//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: CoaddInfo.java,v $
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
 * This is a data structure to hold Coadd info items.  It is created and
 * returned by the Coadd Calculation when asked to calculate.
 *  
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:24 $
 *  @author Lynne Case
**/
public class CoaddInfo
{
	private double fValue;
	private double fRms;
	private long   fSampleCount;

	/**
	 * Create a new CoaddCalculation. 
	 * <P>
	 * @param   factor Coadd factor.
	 *
	**/
	public CoaddInfo(double value, double rms, long samples)
	{
		fValue = value;
		fRms = rms;
		fSampleCount = samples;
	}

	/**
	 * Get the sum or average item.
	**/
	public double getValue()
	{
		return fValue;
	}

	/**
	 * Get the root mean square value
	**/
	public double getRms()
	{
		return fRms;
	}


	/**
	 * Get the number of samples used to calculate these numbers.
	**/
	public long getSampleCount()
	{
		return fSampleCount;
	}

}
