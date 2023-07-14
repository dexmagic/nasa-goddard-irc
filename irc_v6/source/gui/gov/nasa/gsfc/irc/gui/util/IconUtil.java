//=== File Prolog ============================================================
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

package gov.nasa.gsfc.irc.gui.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.plaf.IconUIResource;

import gov.nasa.gsfc.irc.app.Irc;

/**
 * This class provides simple icon helper methods.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/07 21:34:56 $
 * @author	Ken Wootton
 * @author	Troy Ames
 */
public class IconUtil
{
	/**
	 * Get the icon in the specified location.
	 *
	 * @param relIconLocation  relative location of the icon from the
	 *                         class path.
	 *
	 * @return  the icon or 'null', if the icon could not be found
	 */
	public static ImageIcon getIcon(String relIconLocation)
	{
		URL resource = Irc.getResource(relIconLocation);
		ImageIcon icon = null;

		if (resource != null)
		{
			icon = new ImageIcon(resource);
		}

		return icon;
	}

    /**
	 * Utility method that creates a UIDefaults.LazyValue that creates an
	 * ImageIcon UIResource for the specified icon file.
	 * 
	 * @param relIconLocation  relative location of the icon from the
	 *                         class path.
	 */
	public static Object makeIcon(final String relIconLocation)
	{
		return new UIDefaults.LazyValue()
		{
			public Object createValue(UIDefaults table)
			{
				Object result = null;
				
				/*
				 * Copy resource into a byte array. This is necessary because
				 * several browsers consider Class.getResource a security risk
				 * because it can be used to load additional classes.
				 * Class.getResourceAsStream just returns raw bytes, which we
				 * can convert to an image.
				 */
				final byte[][] buffer = new byte[1][];
				
				URL url = Irc.getResource(relIconLocation);
				try
				{
					InputStream resource = url.openStream();
					
					if (resource != null)
					{									
						BufferedInputStream in = 
							new BufferedInputStream(resource);
						ByteArrayOutputStream out = 
							new ByteArrayOutputStream(1024);
						buffer[0] = new byte[1024];
						int n;
						while ((n = in.read(buffer[0])) > 0)
						{
							out.write(buffer[0], 0, n);
						}
						in.close();
						out.flush();
						buffer[0] = out.toByteArray();
					}
				}
				catch (IOException ioe)
				{
					System.err.println(ioe.toString());
				}

				if (buffer[0] == null)
				{
					System.err.println(relIconLocation + " not found.");
				}
				else if (buffer[0].length == 0)
				{
					System.err.println("warning: " + relIconLocation
							+ " is zero-length");
				}
				else
				{
					result = new IconUIResource(new ImageIcon(buffer[0]));
				}

				return result;
			}
		};
	}

}

//--- Development History  ---------------------------------------------------
//
//  $Log: IconUtil.java,v $
//  Revision 1.5  2005/01/07 21:34:56  tames
//  Added makeIcon utility method.
//
//  Revision 1.4  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.3  2004/09/22 18:15:39  tames_cvs
//  Modified Comments only
//
//  Revision 1.2  2004/09/20 21:12:17  tames
//  *** empty log message ***
//
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
// 
