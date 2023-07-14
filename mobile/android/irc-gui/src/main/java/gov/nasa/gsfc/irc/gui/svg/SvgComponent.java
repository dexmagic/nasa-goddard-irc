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

package gov.nasa.gsfc.irc.gui.svg;

import java.net.URL;

import org.apache.batik.swing.svg.JSVGComponent;
import org.apache.batik.swing.svg.SVGUserAgent;

import gov.nasa.gsfc.irc.app.Irc;

/**
 * This is a simple wrapper class for <code>JSVGComponent</code> in the
 * <a href="http://xml.apache.org/batik/">Apache Batik</a> library. 
 * This class adds the <code>setFile</code>, <code>setURI</code>, and
 * <code>getURI</code> convenience methods that makes it usable from an 
 * XML description.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/11/07 22:12:02 $
 * @author 	Troy Ames
 */
public class SvgComponent extends JSVGComponent
{
	private String fUriString = null;
	
	/**
     * Creates a new SvgComponent.
	 */
	public SvgComponent()
	{
		super();
	}

	/**
     * Creates a new SvgComponent.
     * 
     * @param ua a SVGUserAgent instance or null.
     * @param eventsEnabled Whether the GVT tree should be reactive
     *        to mouse and key events.
     * @param selectableText Whether the text should be selectable.
	 */
	public SvgComponent(SVGUserAgent ua, boolean eventsEnabled,
			boolean selectableText)
	{
		super(ua, eventsEnabled, selectableText);
	}

    /**
     * Returns the URI of the current document.
     */
    public String getURI() {
        return fUriString;
    }

    /**
     * Sets the URI to the specified uri. If the input 'newURI'
     * string is null, then the canvas will display an empty
     * document.
     *
     * @param newURI the new uri of the document to display
     */
    public void setURI(String newURI) 
    {
        fUriString = newURI;

        if (fUriString != null)
		{
			loadSVGDocument(fUriString);
		}
		else
		{
			setSVGDocument(null);
		}
    }
    
	/**
	 * Sets the displayed SVG file to the given file. The path can be specified
	 * as a relative path to the class path or a fully qualified path.
	 * If the file cannot be found, then the canvas will display an empty
     * document.
     * 
	 * @param  path the file resource path
	 */
    public void setFile(String path) 
    {
		URL url = Irc.getResource(path);

        if (url != null)
		{
        	setURI(url.toExternalForm());
		}
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: SvgComponent.java,v $
//  Revision 1.1  2005/11/07 22:12:02  tames
//  SVG support
//
//