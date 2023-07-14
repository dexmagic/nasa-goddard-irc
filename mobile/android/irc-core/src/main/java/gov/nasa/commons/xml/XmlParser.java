//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, 
//  Code 588 for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//     any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//     explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The class contains XML parsing support as well as the associated
 * error handling.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version  $Date: 2005/08/19 15:24:08 $
 * @author John Higinbotham
**/

public class XmlParser implements ErrorHandler
{
	private static final String CLASS_NAME = XmlParser.class.getName();	
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	//---Consts
	private final static String fExceptionMsg  = "XML parsing errors encountered while processing: ";

	//---Vars
	private SAXBuilder fParser        = null;   // XML parser
	private boolean    fError         = false;  // Parse error flag
	private Exception  fRootException = null;   // Root exception of parse error
	private boolean    fValidation    = true;   // Validation enabled?
	private SubtreeMap fSubtreeMap    = null;   // Map of nodes to source files
	private boolean    fVerbose       = false;   // Should error messages be logged

//----------------------------------------------------------------------------------------

    /**
     * Construct a new XmlParser with validation on.
    **/
    public XmlParser()
    {
	 	this(true);
    }

    /**
     * Construct a new XmlParser.
     *
     * @param validation Validation enabled.
    **/
	public XmlParser(boolean validation)
	{
	 	//---Save enabled flags
		fValidation = validation;

		fParser = new SAXBuilder();
		fParser.setErrorHandler(this);

        fParser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        fParser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        fParser.setEntityResolver(new XmlEntityResolver());

        if (fValidation)
        {
        	/* This did not work to prevent looking for a DTD
        	fParser.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(
						String publicId,
						String systemId)
				{
					System.out.println("publicId=" + publicId 
							+ " systemId=" + systemId);
					return new InputSource(new StringBufferInputStream(""));
				}
			});
        	fParser.setExpandEntities(false);
        	*/
        	
            fParser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            fParser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            fParser.setFeature("http://xml.org/sax/features/validation", true);
            fParser.setFeature("http://apache.org/xml/features/validation/dynamic", true);
            fParser.setFeature("http://apache.org/xml/features/validation/schema", true);
            fParser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            fParser.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
            fParser.setValidation(true);
        }
	}

    /**
     * Set verbose mode. If true, then errors will be logged.
     *
     * @param verbose boolean
    **/
	public void setVerbose(boolean verbose)
	{
		fVerbose = verbose;
	}

	/**
	 * Get the subtree map.
	 *
	 * @return SubtreeMap
	**/
	public SubtreeMap getSubtreeMap()
	{
		return fSubtreeMap;
	}

    /**
     * Parse the XML file at the specified URL and return the
     * associated JDOM Document.
     *
     * @param url Location of the input XML file.
     * @return Document generated from XML file.
    **/
    public Document parse(URL url) throws XmlException
    {
        return parse(url, null);
    }
    
    /**
     * Parse the XML file at the specified URL and return the
     * associated JDOM Document.
     *
     * @param url Location of the input XML file.
     * @return Document generated from XML file.
    **/
    public Document parse(URL url, String base) throws XmlException
    {
        Document rval = null;
        fError = false;
        
		try
		{
			//---  Get the input stream to the URL.  Note that we set the
			//---  URL to avoid using caches.  This is required because the
			//---  URL connection will sometimes cache these connections/
			//---  file references, leaving them open in some cases.  For
			//---  example, with a JAR URL this will leave an open JAR file
			//---  reference, disabling the ability to delete the file.
			URLConnection urlCon = url.openConnection();
			urlCon.setDefaultUseCaches(false);
			InputStream urlInput = urlCon.getInputStream();
            
            //---Process document
            rval = parse(urlInput, base);
		}
		catch (Exception e)
		{
			throw (
				new XmlException(
					fExceptionMsg + url.toString() + " because " 
					+ e.getMessage()));
		}

        //--- Throw an exception if any significant parse errors were encountered
        if (fError == true)
        {
            throw (new XmlException(fExceptionMsg + fRootException));
        }

		return rval;
    }

    /**
     * Parse the XML file at the specified URL and return the
     * associated JDOM Document.
     *
     * @param is InputStream for the input XML file.
     * @return Document generated from XML file.
    **/
    public Document parse(InputStream is) throws XmlException
    {
        return parse(is, null);
    }
    
    /**
     * Parse the XML file at the specified URL and return the
     * associated JDOM Document.
     *
     * @param is InputStream for the input XML file.
     * @return Document generated from XML file.
    **/
    public Document parse(InputStream is, String base) throws XmlException
    {
        Document rval = null;
        fError        = false;
        
        try
        {
/*******************************/
// JAXP Solution?
//DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//                 dbf.setValidating(true);
// 
//                 dbf.setNamespaceAware(true);
//                 dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
// 
//                 /*SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
//                 Schema mySchema = sf.newSchema(schemaFile);
//                 dbf.setSchema(mySchema);*/
// 
//                 DocumentBuilder db = dbf.newDocumentBuilder();
//                 ErrorChecker errors = new ErrorChecker();
//                 db.setErrorHandler(errors);
// 
//                 System.out.println("[Parser]: Begin...");
//                 db.parse(xmldocFile);
//                 System.out.println("[Parser]: ...End");

/******************************* */            
            //---Process document
            Document baseDocument;

            baseDocument = fParser.build(is, base);
            is.close();
    
            //--- Dump DOM to help debug merged docs
            if (sLogger.isLoggable(Level.FINER))
            {
                XmlUtil.dumpDom(baseDocument);
            }
            
            // Assign result
            rval = baseDocument;
        }
        catch (Exception e)
        {
            throw (
                new XmlException(
                    fExceptionMsg + " because " 
                    + e.getMessage() + fRootException));
        }

        //--- Throw an exception if any significant parse errors were encountered
        if (fError == true)
        {
            throw (new XmlException(fExceptionMsg + fRootException));
        }

        return rval;
    }

	/**
	 * Helper method to simply check that a document is valid.
	 *
	 * @param document Document to validate
	 * @param base URL to use to resolve relative URL's (including schema)
	 * @return boolean value (true if valid, false otherwise)
	**/
	public boolean isValid(Document document, String base)
	{
		return isValid(document, base, true);
	}

	/**
	 * Helper method to simply check that a document is valid.
	 *
	 * @param document Document to validate
	 * @param base to resolve relative URL's
	 * @param verbose boolean value (true if error messages should be logged)
	 * @return boolean value (true if valid, false otherwise)
	**/
	public boolean isValid(Document document, String base, boolean verbose)
	{
		//---Save the original verbose setting
		boolean orgVerbose = fVerbose;

		//---Setup the new verbose setting
		setVerbose(verbose);

		//---Clear old errors
		fError = false;

		//---Valid unless otherwise determined to be invalid
	 	boolean rval = true;

		try
		{
			//---Set up validating parser
			SAXBuilder parser = new SAXBuilder(true);
			parser.setErrorHandler(this);
			parser.setEntityResolver(new XmlEntityResolver());

			//--- Route the results to the validating parser
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			XMLOutputter xmloutputter = new XMLOutputter();
			xmloutputter.output(document, bos);
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

			//--- Validate document
			parser.build(bis, base);

			//--- Cleanup
			bos.close();
			bis.close();

        	if (fError == true)
			{
				rval = false;
			}
		}
		catch (Exception e)
		{
			rval = false;
		}

		//---Restore original verbose setting
		fVerbose = orgVerbose;

		//---Return result
		return rval;
	 }

    /**
     * Warning handler.
     *
     * @param exception SAXParseException
    **/
	public void warning(SAXParseException exception)
    {
		if (fVerbose)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = getLocationString(exception) + 
        				": " + exception.getMessage();
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"warning", message);
			}
		}
    }

    /**
     * Error handler.
     *
     * @param exception SAXParseException
    **/
	public void error(SAXParseException exception)
	{
	    if (fError != true)
		{
			//--- Save the root exception of the first parse error
			fError         = true;
			fRootException = exception;
		}
	    
		if (sLogger.isLoggable(Level.SEVERE))
		{
			String message = getLocationString(exception) + 
    				": " + exception.getMessage();
			
			sLogger.logp(Level.SEVERE, CLASS_NAME, 
				"error", message);
		}
	}

    /**
     * Fatal error handler.
     *
     * @param exception SAXParseException
    **/
    public void fatalError(SAXParseException exception) throws SAXException
	{
		if (sLogger.isLoggable(Level.SEVERE))
		{
			String message = getLocationString(exception) + 
    				": " + exception.getMessage();
			
			sLogger.logp(Level.SEVERE, CLASS_NAME, 
				"error", message);
		}
		
		throw exception;
	}

    /**
     * Determine the location of the error in the XML file from the exception
     * and create an error message string for output.
     *
     * @param exception SAXParseException
     * @return Error message string.
    **/
	public static String getLocationString(SAXParseException exception)
	{
		StringBuffer sb = new StringBuffer();
		String systemId  = exception.getSystemId();

		if (systemId != null)
		{
			int index = systemId.lastIndexOf('/');
			if (index != -1)
			{
				systemId = systemId.substring(index + 1);
			}
			sb.append(systemId);
		}
		sb.append(" Line:");
		sb.append(exception.getLineNumber());
		sb.append(" Column:");
		sb.append(exception.getColumnNumber());

		return sb.toString();
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: XmlParser.java,v $
//  Revision 1.8  2005/08/19 15:24:08  tames_cvs
//  Removed some incorrect comments related to xincludes
//
//  Revision 1.7  2005/05/23 15:23:28  tames_cvs
//  Updated to support Xerces parser, XIncludes, and validation.
//
//  Revision 1.4  2005/05/17 19:55:30  jhosler_cvs
//  Added deprecated constructor, and additional validation features
//
//  Revision 1.3  2005/05/16 18:22:13  jhosler_cvs
//  Added the method parse(URL, String)
//
//  Revision 1.2  2005/05/16 18:17:26  jhosler_cvs
//  Returned the method parse(InputStream, String)
//
//  Revision 1.1  2005/05/13 20:15:03  jhosler_cvs
//  Updated IRCs XmlParser to use standard Xerces XInclude and schema validation features
//
//  Revision 1.6  2004/07/19 21:17:15  tames_cvs
//  *** empty log message ***
//
//  Revision 1.4  2004/06/03 01:38:55  chostetter_cvs
//  Organized inputs
//
//  Revision 1.3  2004/06/01 15:54:16  tames_cvs
//  Remove use of depricated XMLOutputter constructor
//
//  Revision 1.2  2004/05/27 18:23:15  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.1  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
//
