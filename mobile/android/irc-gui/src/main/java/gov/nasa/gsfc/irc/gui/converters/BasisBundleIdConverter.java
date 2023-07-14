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

import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.swixml.Converter;
import org.swixml.Localizer;

import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.DefaultBasisBundleId;
import gov.nasa.gsfc.irc.data.DefaultBasisBundleIdFactory;

/**
 * Converts a String representation of a BasisBundleId to an 
 * instance of a BasisBundleId.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:55 $
 * @author 	Troy Ames
 */
public class BasisBundleIdConverter implements Converter
{
    private static final String CLASS_NAME = BasisBundleIdConverter.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

    /** Converter's return type */
	public static final Class TEMPLATE = DefaultBasisBundleId.class;

	/** BasisBundleIdFactory */
	private static final DefaultBasisBundleIdFactory FACTORY = 
		new DefaultBasisBundleIdFactory();

	/**
	 * Returns a <code>BasisBundleId</code>
	 * 
	 * @param type <code>Class</code> not used
	 * @param attr <code>Attribute</code> value needs to provide BasisBundle 
	 * 		fully qualified name or BasisBundle name and source name.
	 * @return <code>Object</code> runtime type is <code>BasisBundleId</code>
	 */
	public Object convert(final Class type, final Attribute attr,
			Localizer localizer)
	{
		BasisBundleId bundleId = null;
		
		// Fully qualified name or bundle name and source name
		StringTokenizer st = new StringTokenizer(attr.getValue(), ",");
		int n = st.countTokens();
		
		Method method = null;

		try
		{
			if (n == 1)
			{
				// Case for single parameter construction with fully qualified
				// name
				method = FACTORY.getClass().getMethod("createBasisBundleId",
					new Class[] { String.class });
			}
			else if (n == 2)
			{
				// Case for two parameter construction with bundle and source
				// names.
				method = FACTORY.getClass().getMethod("createBasisBundleId",
					new Class[] { String.class, String.class });
			}

			Object[] args = new Object[n];

			// Fill argument array
			for (int i = 0; i < n; i++)
			{
				args[i] = st.nextToken().trim();
			}

			// Create BasisBundleId
			bundleId = (BasisBundleId) method.invoke(FACTORY, args);
		}
		catch (Exception e)
		{
            String msg = "Could not create BasisBundleId from, " + attr.getValue();
            sLogger.logp(Level.WARNING, CLASS_NAME, "convert", msg, e);
		}
		
		return bundleId;
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
//  $Log: BasisBundleIdConverter.java,v $
//  Revision 1.3  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2006/01/02 22:42:55  tames
//  Javadoc update only.
//
//  Revision 1.1  2006/01/02 03:52:15  tames
//  Initial version.
//
//