//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: Lookup.java,v $
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Lookup is used when input data is to be calibrated 
 * using a lookup table for a given channel processed by an algorithm.
 *  
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:24 $
 *  @author John Higinbotham 
**/
public class Lookup implements Calculation
{

	private Map fHA = null;

	/**
	 * Create a new LookupCalculation. 
	 * <P>
	 * @param hasharray Map of key/values 
	**/
	public Lookup(Map hasharray)
	{
		//Because a double could be specified numerous ways by the user, we need
		//to make sure that it is in the simpilist form, thus the conversion to
		//Double then to String below.

		fHA		   = new LinkedHashMap();
		Iterator i	= hasharray.keySet().iterator();
		String s	  = null;
		String sclean = null;
		Double d	  = null;
		while (i.hasNext())
		{
			s = (String) i.next();
			d = new Double(s);
			sclean = d.toString();
			fHA.put(sclean, hasharray.get(s));
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
		Double d = null;
		double in;
		double out =0 ;

		while (count-- > 0)
		{
			in = src[++srcOff];	
			d=(Double) fHA.get(Double.toString(in));
			if (d != null)
			{
				out = d.doubleValue();
			}
			else
			{
				out = in;
			}
			dst[++dstOff] = out;
		}
	}
}
