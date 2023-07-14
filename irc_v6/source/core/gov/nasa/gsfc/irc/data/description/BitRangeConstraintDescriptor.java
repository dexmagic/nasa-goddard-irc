//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: BitRangeConstraintDescriptor.java,v $
//  Revision 1.2  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.4  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.3  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/05/27 16:10:17  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.irc.data.description;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;

import gov.nasa.gsfc.commons.types.arrays.BitArray;
import gov.nasa.gsfc.irc.data.InvalidValueException;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * The class provides access to information describing a constraint applied to a 
 * bit field.  <P>
 *
 * The object is built based on information contained in an IML XML file
 * which describes the insturment being interfaced with by IRC.
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2004/10/14 15:16:50 $
 * @author John Higinbotham 	
**/

public class BitRangeConstraintDescriptor extends ConstraintDescriptor
{
	private static final String CLASS_NAME = 
		BitRangeConstraintDescriptor.class.getName();
	
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	//BitRange Constraint:
	public static final String A_SIGNED	= "signed";
	public static final String A_NUM_BITS	= "numBits";

	//Range Constraint:
	public static final String A_HIGH	= "high";
	public static final String A_LOW	= "low";

	private boolean fSigned = false; // Is the bit field signed (2's comp.) 
	private int fNumBits	= 0;	 // Number of bits in the field 
	private long fHigh = 0;	 // Maximum value of bit string that can be put in the field 
	private long fLow = 0;	 // Minimum value of bit string that can be put in the field


	/**
	 * Constructs a new BitRangeConstraintDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new BitRangeConstraintDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		BitRangeConstraintDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		BitRangeConstraintDescriptor		
	**/
	
	public BitRangeConstraintDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Construct a new bit range constraint descriptor. 
	 *
	 * @param numBits Max number of bits in field
	 * @param isSigned true if 2's complement, false if not 
	**/
	public BitRangeConstraintDescriptor(int numBits, boolean isSigned)
	{
		fSigned  = isSigned;
		fNumBits = numBits;
		computeHighLow();
	}

	
	/**
	 * Construct a new bit range constraint descriptor. 
	 *
	**/
	public BitRangeConstraintDescriptor()
	{
		
	}


	/**
	 * Construct a new bit range constraint descriptor. If the caller attempts
	 * to specify a low/high that are out of bounds of the default low/high
	 * for the entire range, then an exception is thrown.
	 *
	 * @param numBits Max number of bits in field
	 * @param isSigned true if 2's complement, false if not 
	 * @param high A high value that may be less than or equal to the default high 
	 * @param low A low value that may be greater than or equal to the defalt low 
	 * @throws InvalidFieldValueException
	 *
	**/
	public BitRangeConstraintDescriptor(int numBits, boolean isSigned, 
		long high, long low)
		throws InvalidValueException
	{
		//---Determine the max range of the field
		fSigned  = isSigned;
		fNumBits = numBits;
		computeHighLow();

		//---Validate that the custom high/low are in range before using
		validateValue(new Long(high));
		validateValue(new Long(low));

		//---Set the custom high/low
		fHigh= high;
		fLow = low;
	}


	/**
	 * Determine if this field is signed (2's comp.) 
	 *
	 * @return True if signed, false otherwise. 
	 *						
	**/
	public boolean isSigned()
	{
		return fSigned;
	}

	/**
	 * Set if this field is signed (2's comp.). Callers
	 * should be aware that by changing the sign, they 
	 * will also be causing the high/low to be reset to
	 * the max range supported by the new bit field.
	 *
	 * @param flag True if signed, false otherwise. 
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setIsSigned(boolean flag)
	{
		fSigned = flag;
		computeHighLow();
	}

	/**
	 * Determine the number of bits in this field. 
	 *
	 * @return Number of bits in this field. 
	 *						
	**/
	public int getNumBits()
	{
		return fNumBits;
	}

	/**
	 * Set the number of bits in this field. Callers
	 * should be aware that by changing the number of bit,
	 * they will also be causing the high/low to be rest to 
	 * the max range supported by the new bit field. 
	 *
	 * @param numBits Number of bits in this field. 
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setNumBits(int numBits)
	{
	  	fNumBits = numBits;
		computeHighLow();
	}

	/**
	 * Get max long value that may be represented as a bit string in this field. 
	 *
	 * @return Max long value. 
	 *						
	**/
	public long getHigh()
	{
	  return fHigh;
	}

	/**
	 * Get min long value that may be represented as a bit string in this field. 
	 *
	 * @return Max long value. 
	 *						
	**/
	public long getLow()
	{
	  return fLow;
	}

	/**
	 * Compute the high/low values based on the provided numBits and signed attributes. 
	 * These values can then be acquired via the getLow() and getHigh() methods.
	 *
	**/
	private void computeHighLow()
	{
		if (fSigned)
		{
			fLow  = (long) (-1 * ((Math.pow((double) 2, (double) fNumBits)/2)- 1));
			fHigh = (long) (Math.pow((double) 2, (double) fNumBits)/2);
		}
		else
		{
			fLow = 0;
			fHigh = (long) Math.pow((double) 2, (double) fNumBits);
		}
	}

	/**
	 * Validate a value against the constraint. If the value violates the constraint, then an exception 
	 * is thrown.
	 *
	 * @param Value to validate.
	 * @throws InvalidFieldValueException
	 *			
	**/
	public void validateValue(Object value) throws InvalidValueException
	{
		if (value == null)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Passed null value";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"validateValue", message);
			}

			return;
		}

		if (value instanceof BitArray)
		{
			if (((BitArray)value).length() > fNumBits)
			{
				InvalidValueException fieldEx = new InvalidValueException(value, this,
					"Value " + value.toString() + " contains more than " + Integer.toString(fNumBits) + " bits.");
				throw fieldEx;
			}
		}
		else if ((value instanceof Integer) || (value instanceof Long) ||
				 (value instanceof Byte) || (value instanceof Short)) 
		{
			long longValue = ((Number) value).longValue();

			if ((longValue > fHigh) || (longValue < fLow))
			{
				InvalidValueException fieldEx = new InvalidValueException(value, this,
					"Value " + value.toString() + " not between " + Long.toString(fLow) + " and "
					+ Long.toString(fHigh) + ".");
				throw fieldEx;
			}
		}
		else
		{
			InvalidValueException fieldEx = new InvalidValueException(value, this,
				"Value " + value.toString() + " of type " + value.getClass().toString() +
				" not suitable for constrainment by BitRangeConstraint.");
			throw fieldEx;
		}
	}

	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	private void xmlUnmarshall()
	{
		long low;
		long high;
		fSigned  = fSerializer.loadBooleanAttribute(A_SIGNED, fSigned, fElement);
		fNumBits = fSerializer.loadIntAttribute(A_NUM_BITS, fNumBits, fElement);
		computeHighLow();

		low	  = fSerializer.loadLongAttribute(A_LOW,  fLow, fElement);
		high	 = fSerializer.loadLongAttribute(A_HIGH, fHigh, fElement);

		if(isValid(low))
		{
			fLow = low;
		}
		else
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "BitRangeConstraint low value ("+ 
					Long.toString(low) +") is out of range, default used";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"xmlUnmarshall", message);
			}
		}
		if(isValid(high))
		{
			fHigh = high;
		}
		else
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "BitRangeConstraint high value ("+ 
					Long.toString(high) +") is out of range, default used";
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"xmlUnmarshall", message);
			}
		}
	}

	/**
	 * Support method to determine if value is valid. 
	 *
	 * @param value Value to range test
	 * @return true if in range, false otherwise 
	**/
	private boolean isValid(long value)
	{
		boolean rval = true;
		try
		{
			validateValue(new Long(value));
		}
		catch (Exception e)
		{
			rval = false;
		}
		return rval;
	}

	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into.
	 *
	**/
	public void xmlMarshall(Element element)
	{
		super.xmlMarshall(element);
		fSerializer.storeAttribute(A_SIGNED, fSigned, element);
		fSerializer.storeAttribute(A_NUM_BITS, fNumBits, element);
		fSerializer.storeAttribute(A_LOW, fLow, element);
		fSerializer.storeAttribute(A_HIGH, fHigh, element);
	}
}
