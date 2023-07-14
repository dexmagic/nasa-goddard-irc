//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/numerics/types/Amount.java,v 1.4 2006/01/13 03:14:08 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

package gov.nasa.gsfc.commons.numerics.types;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jscience.physics.quantities.Quantity;
import org.jscience.physics.quantities.Scalar;
import org.jscience.physics.units.ConversionException;
import org.jscience.physics.units.Converter;
import org.jscience.physics.units.Unit;


/**
 *  An Amount represents a mutable Quantity in terms of an (optional) Unit. If 
 *  the Amount has no Unit, it is Scalar (i.e., unitless).
 * 
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/13 03:14:08 $
 *  @author	Carl F. Hostetter
**/

public class Amount
{
	private static final String CLASS_NAME = Amount.class.getName();
	private final Logger sLogger = Logger.getLogger(CLASS_NAME);

	private Quantity fQuantity;
	
	
	/**
	 *  Default constructor of an Amount.
	 *  
	 */
	 
	 public Amount()
	 {
		fQuantity = Scalar.ZERO;
	 }
	 

	/**
	 *  Sets the keep amount of this BasisRequestAmount to the given Quantity.
	 *  
	 *  @param amount The keep amount of this BasisRequestAmount
	 */
	 
	 public void setAmount(Quantity amount)
	 {
	 	if ((amount != null) && (! amount.isPossiblyZero()))
		{
			fQuantity = amount;
		}
	 	else
		{
			fQuantity = Scalar.ZERO;
		}
	 }


	/**
	 *  Sets the amount value of this Amount to the given amount, which must be 
	 *  expressed in terms of the current Unit of this Amount.
	 *  
	 *  @param amount The new amount value of this Amount
	 */
	 
	 public void setAmount(double amount)
	 {
		 Quantity quantity;
		 
		 if (fQuantity instanceof Scalar)
		 {
			 quantity = Scalar.valueOf(amount);
		 }
		 else
		 {
			 quantity = Quantity.valueOf(amount, fQuantity.getSystemUnit());
		 }
		 
		 setAmount(quantity);
	 }


	/**
	 *  Sets the amount value of this Amount to the given amount, which must be 
	 *  expressed in terms of the given Unit. If the given Unit is null, this 
	 *  Amount will become Scalar (i.e., unitless).
	 *  
	 *  @param amount The new amount value of this Amount
	 *  @param unit The new Unit of this Amount
	 */
	 
	 public void setAmount(double amount, Unit unit)
	 {
		 setAmount(Quantity.valueOf(amount, unit));
	 }


	/**
	 *  Returns the current value of this Amount, in terms of its current Unit 
	 *  (if any).
	 *  
	 *  @return The current value of this Amount, in terms of its current Unit 
	 *  		(if any)
	 */
		 
	 public double getAmount()
	 {
		 return (fQuantity.doubleValue());
	 }
	 
	 
	/**
	 * Returns a unit instance that is defined from the specified character
	 * sequence. If the specified character sequence is a combination of units
	 * (e.g. ProductUnit), then the corresponding unit is created if not already
	 * defined.
	 * 
	 * @param symbol the character sequence to parse.
	 * @return <code>Unit.valueOf(symbol)</code>
	 */	 
	 public static Unit lookUpUnit(String symbol)
	 {
		 Unit result = null;
		 
		 if (symbol != null)
		 {
			 result = Unit.valueOf(symbol);
		 }
		 
		 return (result);
	 }
	 
	 
	/**
	 *  Sets the Unit of this Amount to the given Unit. If necessary, and if an 
	 *  appropriate Converter exists, the current amount value of this Amount 
	 *  will be converted as appropriate to the given Unit. If the given Unit 
	 *  is null, this Amount will become Scalar (i.e., unitless).
	 *  
	 *  @param unit The new Unit of this Amount
	 */
	 
	 public void setUnit(Unit unit)
	 {
		 Quantity quantity = null;
		 
		 if ((unit != null) && (unit != Unit.ONE))
		 {
			 double amount = fQuantity.doubleValue();
			 
			 if (fQuantity instanceof Scalar)
			 {
				 fQuantity = Quantity.valueOf(amount, unit);
			 }
			 
			 Converter converter = 
			 	fQuantity.getSystemUnit().getConverterTo(unit);
			 
			 if (converter != null)
			 {
				amount = converter.convert(fQuantity.doubleValue());
				
			 	quantity = Quantity.valueOf(amount, unit);
			 }
		 }
		 else
		 {
			 quantity = Scalar.valueOf(fQuantity.doubleValue());
		 }
		 
		 setAmount(quantity);
	 }


	/**
	 *  Sets the Unit of this Amount to the Unit indicated by the given Unit symbol 
	 *  String. Note that this symbol String is not arbitrary, and must correspond 
	 *  exactly to one of the Unit symbol Strings defined by the Unit pacakge. 
	 *  If necessary, and if an appropriate Converter exists, the current amount 
	 *  value of this Amount will be converted as appropriate to the given Unit. 
	 *  If the given Unit symbol String is null, this Amount will become Scalar 
	 *  (i.e., unitless).
	 *  
	 *  @param unit The Unit symbol String of the desired new Unit of this Amount
	 *  @throws IllegalArgumentException if the given Unit symbol String is 
	 *  		not a recognized symbol
	 */
	 
	 public void setUnit(String symbol)
	 	throws IllegalArgumentException
	 {
		 Unit unit = Amount.lookUpUnit(symbol);
		 
		 if (unit == null)
		 {
			 String message = "Illegal Unit symbol String " + symbol;
			 
			 if (sLogger.isLoggable(Level.SEVERE))
			 {
				 sLogger.logp(Level.SEVERE, CLASS_NAME, "setUnit", message);
			 }
			 
			 throw (new IllegalArgumentException(message));
		 }
		 
		 setUnit(unit);
	 }


	/**
	 *  Returns the current Unit of this Amount (if any). If this Amount is 
	 *  Scalar, the result will be null.
	 *  
	 *  @return The current Unit of this Amount (if any). If this Amount is 
	 *		Scalar, the result will be null
	 */
		 
	 public Unit getUnit()
	 {
	 	Unit result = null;
	 	
	 	if (! (fQuantity instanceof Scalar))
	 	{
	 		result = fQuantity.getSystemUnit();
	 	}
	 	
	 	return (result);
	 }


	/**
	 *  Returns true if is (possibly) zero (that is, to within measurable error), 
	 *  false otherwise.
	 *  
	 *  @return True if is (possibly) zero (that is, to within measurable error), 
	 *  		false otherwise
	 */
		 
	 public boolean isZero()
	 {
		 return (fQuantity.isPossiblyZero());
	 }


	/**
	 *  Returns true if this Amount is Scalar, false otherwise.
	 *  
	 *  @return True if this Amount is Scalar, false otherwise
	 */
		 
	 public boolean isScalar()
	 {
		 boolean result = (fQuantity instanceof Scalar);
		 
		 return (result);
	 }


	/**
	 *  Makes this Amount Scalar (i.e., unitless).
	 *  
	 */
		 
	 public void makeScalar()
	 {
		 fQuantity = Scalar.valueOf(fQuantity.doubleValue());
	 }
	 
	 
	/**
	 *  Returns a Converter between the Unit of this Amount to a Quantity of the 
	 *  given Unit. If no such Converter exists, the result will be null.
	 *  
	 *  @return A Converter between the Unit of this Amount to a Quantity of the 
	 *  		given Unit. If no such Converter exists, the result will be null
	 */
		 
	 public Converter getConverterTo(Unit unit)
	 {
		 Converter result = null;
		 
		 try
		 {
			 result = fQuantity.getSystemUnit().getConverterTo(unit);
		 }
		 catch (ConversionException ex)
		 {
			 if (sLogger.isLoggable(Level.WARNING))
			 {
				 String message = "Unable to convert to " + unit;
				 
				 sLogger.logp(Level.WARNING, CLASS_NAME, 
					"getConverterTo", message, ex);
			 }
		 }
		 
		 return (result);
	 }


	/**
	 * Returns a String representation of this Amount.
	 * 
	 * @return A String representation of this Amount
	 */
	
	public String toString()
	{
		StringBuffer stringRep = new StringBuffer();
		
		stringRep.append(fQuantity.doubleValue());
		
		stringRep.append(" Unit: " + fQuantity.getSystemUnit());
		
		return (stringRep.toString());
	}
	
	/**
	 * Returns true if this Amount equals the given Object, false
	 * otherwise.
	 * 
	 * @param object An Object
	 * @return True if this Amount equals the given Object, false otherwise
	 */
	public boolean equals(Object object)
	{
		boolean result = false;
		
		if (this == object)
		{
			result = true;
		}
		else if (object instanceof Amount)
		{
			Amount target = (Amount) object;
			
			if (fQuantity.equals(target.fQuantity))
			{
				result = true;
			}
		}
		
		return (result);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return fQuantity.hashCode();
	}

}

//--- Development History  ---------------------------------------------------
//
//	$Log: Amount.java,v $
//	Revision 1.4  2006/01/13 03:14:08  tames
//	JavaDoc change only.
//	
//	Revision 1.3  2006/01/04 16:52:42  tames
//	Fixed setAmount method. Added equals and hashcode methods.
//	
//	Revision 1.2  2005/03/16 19:07:01  chostetter_cvs
//	Refactored Amount
//	
//	Revision 1.1  2005/03/15 00:36:02  chostetter_cvs
//	Implemented covertible Units compliments of jscience.org packages
//	
