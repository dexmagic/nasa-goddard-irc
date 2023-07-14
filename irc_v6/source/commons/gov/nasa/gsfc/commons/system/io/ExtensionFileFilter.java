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
//   2	IRC	   1.1		 11/12/2001 5:01:02 PMJohn Higinbotham Javadoc
//		update.
//   1	IRC	   1.0		 8/22/2001 8:21:06 AM John Higinbotham 
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


/**
 *  This class provides support for filtering on file extensions. 
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:23 $
 *  @author  	John Higinbotham	
**/
public class ExtensionFileFilter implements FileFilter
{
	private String fExtension = null;

	/**
	 * Create a new ExtensionFileFilter.
	 *
	 * @param extension File extension 
	**/
	public ExtensionFileFilter(String extension)
	{
		fExtension = extension;
	}

	/**
	 * Determine if a file is accepted by this filter. 
	 *
	 * @param file File to apply filter to.
	 * @return boolean
	**/
	public boolean accept(File file)
	{
		boolean rval = false;
		String s	 = file.toString();
		int index	= s.lastIndexOf(".");
		String ext   = s.substring(index+1);

		if (ext.compareTo(fExtension) == 0)
		{
		 	rval = true;
		}
		return rval;
	}
}
