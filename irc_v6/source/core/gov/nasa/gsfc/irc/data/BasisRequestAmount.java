//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/BasisRequestAmount.java,v 1.3 2006/01/02 18:30:31 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//	$Log: BasisRequestAmount.java,v $
//	Revision 1.3  2006/01/02 18:30:31  tames
//	Updated to reflect depricated BasisRequestAmount class. Uses Amount instead.
//	
//	Revision 1.2  2005/03/16 18:36:50  chostetter_cvs
//	Refactored BasisRequestAmount
//	
//	Revision 1.1  2005/03/15 00:36:02  chostetter_cvs
//	Implemented covertible Units compliments of jscience.org packages
//	
//	Revision 1.5  2004/07/12 14:26:23  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.4  2004/07/11 07:30:35  chostetter_cvs
//	More data request work
//	
//	Revision 1.3  2004/07/06 13:40:00  chostetter_cvs
//	Commons package restructuring
//	
//	Revision 1.2  2004/05/16 15:44:36  chostetter_cvs
//	Further data-handling definition
//	
//	Revision 1.1  2004/05/16 07:15:43  chostetter_cvs
//	Initial Interval definitions
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

import org.jscience.physics.quantities.Quantity;
import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.commons.numerics.types.Amount;


/**
 *  A BasisRequestAmount describes an interval on a basis Buffer.
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/02 18:30:31 $
 *  @author	Carl F. Hostetter
 *  @deprecated use Amount
**/

public class BasisRequestAmount extends Amount
{
	private Amount fInitialSkip;
	private Amount fFinalSkip;
	
	
	/**
	 *  Default constructor of a BasisRequestAmount.
	 *  
	 */
	 
	 public BasisRequestAmount()
	 {
		 fInitialSkip = new Amount();
		 fFinalSkip = new Amount();
	 }
	 

	/**
	 *  Sets the initial skip amount of this BasisRequestAmount to the given 
	 *  Quantity.
	 *  
	 *  @param amount The initial skip amount of this BasisRequestAmount
	 */
	 
	 public void setInitialSkipAmount(Quantity amount)
	 {
		 fInitialSkip.setAmount(amount);
	 }


	/**
	 *  Sets the initial skip amount of this BasisRequestAmount to the given 
	 *  amount.
	 *  
	 *  @param amount The initial skip amount of this BasisRequestAmount
	 */
	 
	 public void setInitialSkipAmount(double amount)
	 {
		 fInitialSkip.setAmount(amount);
	 }


	/**
	 *  Sets the initial skip amount of this BasisRequestAmount to the given amount, 
	 * 	which must be expressed in terms of the given Unit. If the given Unit is 
	 * 	null, the result will be Scalar (i.e., unitless).
	 *  
	 *  @param amount The initial skip amount of this BasisRequestAmount
	 *  @param unit The Unit of the initial skip amount of this BasisRequestAmount
	 */
	 
	 public void setInitialSkipAmount(double amount, Unit unit)
	 {
	 	fInitialSkip.setAmount(amount, unit);
	 }


	/**
	 *  Sets the Unit of the initial skip amount of this BasisRequestAmount to the 
	 *  given Unit. If necessary, the current amount will be converted as 
	 *  appropriate to the given Unit. If the given Unit is null, the result will 
	 *  be Scalar (i.e., unitless).
	 *  
	 *  @param unit The Unit of the initial skip amount of this BasisRequestAmount
	 */
	 
	 public void setInitialSkipUnit(Unit unit)
	 {
	 	fInitialSkip.setUnit(unit);
	 }


	/**
	 *  Returns the initial skip Amount of this BasisRequestAmount.
	 *  
	 *  @return The initial skip Amount of this BasisRequestAmount
	 */
		 
	 public Amount getInitialSkip()
	 {
		 return (fInitialSkip);
	 }
	 
	 
	/**
	 *  Sets the keep amount of this BasisRequestAmount to the given Quantity.
	 *  
	 *  @param amount The keep amount of this BasisRequestAmount
	 */
	 
	 public void setKeepAmount(Quantity amount)
	 {
		setAmount(amount);
	 }


	/**
	 *  Sets the keep amount of this BasisRequestAmount to the given amount.
	 *  
	 *  @param amount The keep amount of this BasisRequestAmount
	 */
	 
	 public void setKeepAmount(double amount)
	 {
		setAmount(amount);
	 }


	/**
	 *  Sets the keep amount of this BasisRequestAmount to the given amount, which 
	 *  must be expressed in terms of the given Unit. If the given Unit is null, 
	 *  the result will be Scalar (i.e., unitless).
	 *  
	 *  @param amount The keep amount of this BasisRequestAmount
	 *  @param unit The Unit of the keep amount of this BasisRequestAmount
	 */
	 
	 public void setKeepAmount(double amount, Unit unit)
	 {
		 setAmount(amount, unit);
	 }


	/**
	 *  Sets the Unit of the keep amount of this BasisRequestAmount to the given 
	 *  Unit. If necessary, the current amount will be converted as appropriate to 
	 *  the given Unit. If the given Unit is null, the result will be Scalar (i.e., 
	 *  unitless).
	 *  
	 *  @param unit The Unit of the keep amount of this BasisRequestAmount
	 */
	 
	 public void setKeepUnit(Unit unit)
	 {
		 setUnit(unit);
	 }


	/**
	 *  Returns the keep amount of this BasisRequestAmount.
	 *  
	 *  @return The keep amount of this BasisRequestAmount
	 */
		 
	 public Amount getKeep()
	 {
		 return (this);
	 }
	 
	 
	/**
	 *  Sets the final skip amount of this BasisRequestAmount to the given Quantity.
	 *  
	 *  @param amount The final skip amount of this BasisRequestAmount
	 */
	 
	 public void setFinalSkipAmount(Quantity amount)
	 {
		fFinalSkip.setAmount(amount);
	 }


	/**
	 *  Sets the final skip amount of this BasisRequestAmount to the given 
	 *  amount.
	 *  
	 *  @param amount The final skip amount of this BasisRequestAmount
	 */
	 
	 public void setFinalSkipAmount(double amount)
	 {
	 	fFinalSkip.setAmount(amount);
	 }


	/**
	 *  Sets the final skip amount of this BasisRequestAmount to the given amount, 
	 * 	which must be expressed in terms of the given Unit. If the given Unit is 
	 * 	null, the result will be Scalar (i.e., unitless).
	 *  
	 *  @param amount The final skip amount of this BasisRequestAmount
	 *  @param unit The Unit of the final skip amount of this BasisRequestAmount
	 */
	 
	 public void setFinalSkipAmount(double amount, Unit unit)
	 {
	 	fFinalSkip.setAmount(amount, unit);
	 }


	/**
	 *  Sets the Unit of the final skip amount of this BasisRequestAmount to the 
	 *  given Unit. If necessary, the current amount will be converted as 
	 *  appropriate to the given Unit. If the given Unit is null, the result will 
	 *  be Scalar (i.e., unitless).
	 *  
	 *  @param unit The Unit of the final skip amount of this BasisRequestAmount
	 */
	 
	 public void setFinalSkipUnit(Unit unit)
	 {
	 	fFinalSkip.setUnit(unit);
	 }


	/**
	 *  Returns the final skip Amount of this BasisRequestAmount.
	 *  
	 *  @return The final skip Amount of this BasisRequestAmount
	 */
		 
	 public Amount getFinalSkip()
	 {
		 return (fFinalSkip);
	 }	 
	 
	 
	/**
	 * Returns a String representation of this BasisRequestAmount.
	 * 
	 * @return A String representation of this BasisRequestAmount
	 */
	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer();
		
		if (! fInitialSkip.isZero())
		{
			stringRep.append("Initial skip: " + fInitialSkip);
		}
		
		if (! isZero())
		{
			stringRep.append("Keep: " + super.toString());
		}
		
		if (! fInitialSkip.isZero())
		{
			stringRep.append("Final skip: " + fFinalSkip);
		}
		
		return (stringRep.toString());
	}
}
