//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultXmlErrorHandler.java,v $
//  Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/05/27 18:23:15  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.1  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
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

package gov.nasa.gsfc.commons.xml;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


/**
 * This class implements a default XML error handler. 
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2004/07/12 14:26:24 $
 * @author John Higinbotham 
**/

public class DefaultXmlErrorHandler implements ErrorHandler
{
	private static final String CLASS_NAME = 
		DefaultXmlErrorHandler.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);


	/**
	 * Construct a new error handler. 
	 *
	**/
	public DefaultXmlErrorHandler()
	{
		
	}
	

	/**
	 * Handle warnings. 
	 *
	 * @param exception SAXParseException
	**/
	
	public void warning(SAXParseException exception)
	{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = XmlParser.getLocationString(exception);
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"warning", message, exception);
			}
	}
	

	/**
	 * Handle errors. 
	 *
	 * @param exception SAXParseException
	**/
	
	public void error(SAXParseException exception)
	{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = XmlParser.getLocationString(exception);
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"error", message, exception);
			}
	}
	

	/**
	 * Handle fatal errors. 
	 *
	 * @param exception SAXParseException
	**/
	
	public void fatalError(SAXParseException exception)
	{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = XmlParser.getLocationString(exception);
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
					"fatalError", message, exception);
			}
	}
}
