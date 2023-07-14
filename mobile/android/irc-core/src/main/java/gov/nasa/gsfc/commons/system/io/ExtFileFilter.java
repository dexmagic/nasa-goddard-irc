//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log:
//   1	IRC	   1.0		 11/29/01 5:20:08 PM  John Higinbotham
//  $
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

package gov.nasa.gsfc.commons.system.io;

import java.io.File;
import java.io.FileFilter;

/*
import javax.swing.filechooser.FileFilter;
*/


/**
 *  This class provides support to the file choosers for filtering on file extensions.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:23 $
 *  @author  	John Higinbotham
**/
public class ExtFileFilter implements FileFilter
{
	//---Common filter extensions
	private final static String XML_EXT	 = "xml";
	private final static String JAR_EXT	 = "jar";
	private final static String JPYTHON_EXT = "py";

	//---Common filter descriptions
	private final static String XML_DES		 = "XML File (*.xml)";
	private final static String JAR_DES		 = "JAR File (*.jar)";
	private final static String JPYTHON_DES	 = "JPython File (*.py)";

	//---Common filters
	public final static ExtFileFilter XML_FILTER		= new ExtFileFilter(XML_EXT, XML_DES);
	public final static ExtFileFilter JAR_FILTER		= new ExtFileFilter(JAR_EXT, JAR_DES);
	public final static ExtFileFilter JPYTHON_FILTER	= new ExtFileFilter(JPYTHON_EXT, JPYTHON_DES);

	//---Vars
	private String fExtension		   = null;
	private String fDescription		 = "";
	private ExtensionFileFilter fFilter = null;


	/**
	 * Create a new ExtFileFilter.
	 *
	 * @param extension Extension to filter on.
	 * @param description Description of filter.
	**/
	public ExtFileFilter(String extension, String description)
	{
		fExtension   = extension;
		fDescription = description;
		fFilter	  = new ExtensionFileFilter(fExtension);
	}

	/**
	 * Determine if a file is accepted by this filter.
	 *
	 * @param file File to apply filter to.
	 * @return boolean
	**/
	public boolean accept(File file)
	{
		boolean rval = true;
		if (file.isFile())
		{
			rval = fFilter.accept(file);
		}
		return rval;
	}

	/**
	 * Get description of filter.
	 *
	 * @return String
	**/
	public String getDescription()
	{
		return fDescription;
	}
}
