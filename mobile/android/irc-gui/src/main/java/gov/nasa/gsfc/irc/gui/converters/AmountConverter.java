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

package gov.nasa.gsfc.irc.gui.converters;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.swixml.Converter;
import org.swixml.Localizer;

import gov.nasa.gsfc.commons.numerics.types.Amount;
import gov.nasa.gsfc.irc.data.DefaultBasisBundleIdFactory;

/**
 * Converts a String representation of an <code>Amount</code> to an 
 * instance of one. Examples:
 * <pre>
 * 	requestAmount="3"
 * 	requestAmount="3.2 s"
 * 	requestAmount="3.2,s"
 * </pre>
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/02 22:43:06 $
 * @author 	Troy Ames
 */
public class AmountConverter implements Converter
{
    private static final String CLASS_NAME = AmountConverter.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

    /** Converter's return type */
	public static final Class TEMPLATE = Amount.class;

	/** BasisBundleIdFactory */
	private static final DefaultBasisBundleIdFactory FACTORY = 
		new DefaultBasisBundleIdFactory();

	/**
	 * Returns an <code>Amount</code>
	 * 
	 * @param type <code>Class</code> not used
	 * @param attr <code>Attribute</code> value needs to provide numeric amount
	 * 		and optional unit.
	 * @return <code>Object</code> runtime type is <code>BasisBundleId</code>
	 */
	public Object convert(final Class type, final Attribute attr,
			Localizer localizer)
	{
		Amount amount = new Amount();
		
		try
		{
			// Amount value and optional unit
			StringTokenizer st = new StringTokenizer(attr.getValue(), " ,");
			int n = st.countTokens();
			
			if (n > 0)
			{
				// The first parameter is the value
				Double value = Double.valueOf(st.nextToken());
				amount.setAmount(value.doubleValue());
			}

			// Check if optional unit is specified
			if (n > 1)
			{
				amount.setUnit(st.nextToken().trim());
			}
			
			// All other parameters are ignored			
		}
		catch (Exception e)
		{
            String msg = "Could not create Amount from, " + attr.getValue();
            sLogger.logp(Level.WARNING, CLASS_NAME, "convert", msg, e);
		}
		
		return amount;
	}

	/**
	 * A <code>Converters</code> convertsTo method provides the Class type
	 * the converter is returning when its <code>convert</code> method is
	 * called
	 * 
	 * @return <code>Class</code> - the Class the converter is returning when
	 *         its convert method is called
	 */
	public Class convertsTo()
	{
		return TEMPLATE;
	}
}


// --- Development History ---------------------------------------------------
//
//  $Log: AmountConverter.java,v $
//  Revision 1.1  2006/01/02 22:43:06  tames
//  Initial version.
//
//