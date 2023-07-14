//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, 
// Code 588 for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// * Neither the author, their corporation, nor NASA is responsible for
//   any consequence of the use of this software.
// * The origin of this software must not be misrepresented either by
//   explicit claim or by omission.
// * Altered versions of this software must be plainly marked as such.
// * This notice may not be removed or altered.
//
//=== End File Prolog ========================================================


package gov.nasa.gsfc.commons.xml;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.nasa.gsfc.commons.app.App;
import gov.nasa.gsfc.commons.app.preferences.PrefKeys;
import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.commons.system.io.FileUtil;
import gov.nasa.gsfc.irc.app.Irc;

/**
 * How the Xerces parser resolves local relative paths is flawed. It
 * resolves the base to an absolute path before the relative base is fully 
 * constructed which commonly results in incorrect paths. For local relative 
 * paths this resolver attempts
 * to correct this by deconstructing the system id given it and extracting 
 * out the fully constructed base path. The fully constructed base path is then
 * used to find the correct unique absolute path to the resource. For this
 * resolver to extract out the base correctly it needs to know the root of the
 * base. The base root must be specified by the 
 * {@link gov.nasa.gsfc.commons.app.preferences.PrefKeys#XML_BASE_DIR PrefKeys.XML_BASE_DIR}
 * preference. if the 
 * {@link gov.nasa.gsfc.commons.app.preferences.PrefKeys#XML_SCHEMA_DIR PrefKeys.XML_BASE_DIR}
 * preference is also specified then this directory is substituted for the 
 * XML base directory to locate schemas.
 * 
 * <p>
 * For nonlocal paths this resolver does nothing and defers to the parser
 * for the default behavior.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/24 18:37:04 $
 * @author	tames
**/
public class XmlEntityResolver implements EntityResolver
{
	private static final String CLASS_NAME = XmlEntityResolver.class.getName();	
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static final char PATH_SEPARATOR = '/';
	private static final String XML_BASE_DIR = 
		App.getPreference(PrefKeys.XML_BASE_DIR);
	private static final String XML_SCHEMA_DIR = 
		App.getPreference(PrefKeys.XML_SCHEMA_DIR);

	/**
	 * Default constructor.
	 */
	public XmlEntityResolver()
	{
	}

	/** 
     * Resolves the location of local external entities. Remote fully 
     * specified URLs are passed to the parser as is.
     *
     * <p>
     * The Parser will call this method before opening any external
     * entity except the top-level document entity (including the
     * external DTD subset, external entities referenced within the
     * DTD, and external entities referenced within the document
     * element).
     * </p>
     *
     * @param publicId The public identifier of the external entity
     *        being referenced, or null if none was supplied.
     * @param systemId The system identifier of the external entity
     *        being referenced.
     * @return An InputSource object describing the new input source,
     *         or null to request that the parser open a regular
     *         URI connection to the system identifier.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException A Java-specific IO exception,
     *            possibly the result of creating a new InputStream
     *            or Reader for the InputSource.
	 */
	public InputSource resolveEntity(
			String publicId, String systemId)
			throws SAXException, java.io.IOException
	{
		InputSource source = null;
		URL sourceUrl = null;

		if (sLogger.isLoggable(Level.FINE))
		{
			sLogger.logp(Level.FINE, CLASS_NAME, 
					"resolveEntity", "received publicId:" + publicId);
			sLogger.logp(Level.FINE, CLASS_NAME, 
					"resolveEntity", "received systemId:" + systemId);
		}

		// Try the location for the entity
		sourceUrl = Sys.getResource(systemId);

		if (sourceUrl != null)
		{
			String protocol = sourceUrl.getProtocol();
			
			// Check if source is a local reference
			if (protocol.compareToIgnoreCase("http") != 0)
			{
				String file = sourceUrl.getFile();
				String fileExtension = FileUtil.getFilenameExtension(file);
				
				// Create an InputSource object if entity was found
				source = new InputSource(sourceUrl.toExternalForm());
			}
		}
		else
		{
			URL url = new URL(systemId);
			String path = url.getPath();
			
			while (sourceUrl == null 
					&& path != null
					&& path.indexOf(PATH_SEPARATOR) > -1)
			{
				path = path.substring(path.indexOf(PATH_SEPARATOR) + 1);
				
				// Try the location for the entity
				sourceUrl = Sys.getResource(path);
			}
			
			if (sourceUrl != null)
			{
				// Create an InputSource object if entity was found
				source = new InputSource(sourceUrl.toExternalForm());
			}
		}

		if (sLogger.isLoggable(Level.FINE))
		{
			sLogger.logp(Level.FINE, CLASS_NAME, 
					"resolveEntity", 
					"result entity URL:" + sourceUrl);
		}

		return source;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: XmlEntityResolver.java,v $
//  Revision 1.7  2006/04/24 18:37:04  tames_cvs
//  Fixed resolving "file" urls created by xerces. Removed some schema
//  base directory handling that did not work in some cases. An alternative may
//  need to be added if the current implementation also does not work in all cases.
//
//  Revision 1.6  2005/05/23 15:23:28  tames_cvs
//  Updated to support Xerces parser, XIncludes, and validation.
//
//
