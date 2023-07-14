//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: UrlUtil.java,v $
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
//
//  Revision 1.1.2.3  2004/03/24 20:31:34  chostetter_cvs
//  New package structure baseline
//
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

package gov.nasa.gsfc.commons.net.urls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;


/**
 *  This class provides a number of useful utlities for working with URLs.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:24 $
 *  @author		Carl Hostetter
**/

public class UrlUtil
{
	public static final boolean DEBUG = false;

	public static final char URL_ESCAPE_CHAR = '%';
	public static final String URL_SPACE = "%20";
	public static final char ASCII_SPACE = ' ';

	/**
	 * Translates a URL containing URL coded spaces "<code>%20</code>" to a 
	 * url containing ASCII spaces.
	 * 
	 * @param url source URL
	 * @return URL result URL
	 */
	public static URL makeAsciiSpaces(URL url)
	{
		URL fixedUrl = url;

		if (DEBUG)
		{
			System.out.println("UrlUtil original URL: " + url);
		}

		try
		{
			// Scan the URL for any URL_SPACEs, replacing them with
			// ASCII_SPACEs

			String urlString = url.toString();

			char[] urlChars = urlString.toCharArray();
			StringBuffer newUrl = new StringBuffer();

			for (int i = 0; i < urlChars.length; i++)
			{
				char urlChar = urlChars[i];

				if (urlChar == URL_ESCAPE_CHAR)
				{
					String escapeSequence = urlString.substring(i, i + 3);

					if (DEBUG)
					{
						System.out.println("Found escape sequence: " +
							escapeSequence);
					}

					if (escapeSequence.equals(URL_SPACE))
					{
						newUrl.append(ASCII_SPACE);
					}

					i += 2;
				}
				else
				{
					newUrl.append(urlChar);
				}
			}

			fixedUrl = new URL(newUrl.toString());
		}
		catch (Exception e)
		{

		}

		if (DEBUG)
		{
			System.out.println("UrlUtil fixed URL: " + fixedUrl);
		}

		return (fixedUrl);
	}
	
	/**
	 * Translates a URL containing ASCII spaces to a URL with 
	 * coded "<code>%20</code>" spaces.
	 * 
	 * @param url source URL
	 * @return URL result URL
	 */
	public static URL makeCodedSpaces(URL url)
	{
		URL fixedUrl = url;

		if (DEBUG)
		{
			System.out.println("UrlUtil original URL: " + url);
		}

		try
		{
			// Scan the URL for any URL_SPACEs, replacing them with
			// ASCII_SPACEs

			String urlString = url.toString();

			char[] urlChars = urlString.toCharArray();
			StringBuffer newUrl = new StringBuffer();

			for (int i = 0; i < urlChars.length; i++)
			{
				char urlChar = urlChars[i];

				if (urlChar == ASCII_SPACE)
				{
					if (DEBUG)
					{
						System.out.println("Found space at index: " +
							i);
					}
					
					newUrl.append(URL_SPACE);
				}
				else
				{
					newUrl.append(urlChar);
				}
			}

			fixedUrl = new URL(newUrl.toString());
		}
		catch (Exception e)
		{

		}

		if (DEBUG)
		{
			System.out.println("UrlUtil fixed URL: " + fixedUrl);
		}

		return (fixedUrl);
	}
	
	/**
	 * Gets an InputStream connected to the specified URsLog. If URL can be
	 * an absolute local path or a remote http connection.
	 * 
	 * @param url
	 * @return InputStream
	 * @throws IOException
	 */
	public static InputStream getInputStream(URL url) throws IOException
	{
		InputStream inStream = null;
		
		if (url != null)
		{
			URLConnection connection = url.openConnection();
			connection.connect();

			inStream = connection.getInputStream();
		}
		else
		{
			throw new IllegalArgumentException("URL parameter cannot be null");
		}

		return inStream;
	}

	/**
	 * Get an OutputStream to the specified URsLog. Throws an 
	 * UnsupportedOperationException if the destination does not accept output.
	 * 
	 * @param url
	 * @return OutputStream
	 * @throws IOException
	 * @throws UnsupportedOperationException
	 */
	public static OutputStream getOutputStream(URL url) throws IOException
	{
		OutputStream stream = null;

		if (url != null)
		{
			try
			{
				//we need a URI to do the test because the URL class does not
				//provide a test for an absolute URL
				URI uri = new URI(UrlUtil.makeCodedSpaces(url).toString());

				if (uri.isAbsolute())
				{
					File file = new File(uri);

					if (!file.canWrite())
					{
						throw new UnsupportedOperationException(
							"OutputStream for \"" + uri + "\" is unsupported");
					}
					else
					{
						stream = new FileOutputStream(file);
					}
				}
				else
				{
					// File is not local
					URLConnection connection = url.openConnection();
					//connection.setDoOutput(true);
					connection.connect();

					// Check if connection supports writing to it
					if (connection.getDoOutput())
					{
						stream = connection.getOutputStream();
					}
					else
					{
						// throw unsupported exception
						throw new UnsupportedOperationException(
							"OutputStream for \"" + url + "\" is unsupported");
					}
				}
			}
			catch (IllegalArgumentException e)
			{
				throw new UnsupportedOperationException(e.getMessage());
			}
			catch (URISyntaxException e)
			{
				throw new UnsupportedOperationException(e.getMessage());
			}
			catch (FileNotFoundException e)
			{
				throw new UnsupportedOperationException(e.getMessage());
			}
		}
		else
		{
			throw new IllegalArgumentException("URL parameter cannot be null");
		}

		return stream;
	}
}
