//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: FileUtil.java,v $
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/09/16 21:12:51  jhiginbotham_cvs
//  Port from IRC v5.
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

package gov.nasa.gsfc.irc.gui.util;

import java.awt.Component;
import java.io.File;
import java.net.URL;

import javax.swing.JOptionPane;

import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.app.preferences.IrcPrefKeys;


/**
 *    This class provides simple utility methods for handling files and
 *  reporting associated errors.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/12/01 21:50:58 $
 *  @author	    Ken Wootton
 */
public class FileUtil
{
	//  Warning dialog properties
	private static final String FILE_WARNING_TITLE = "File Warning";
	private static final String EXISTS_WARNING_MSG =
		"This file already exists.  Would you like to overwrite it?";

	//  Error dialog properties
	private static final String FILE_ERROR_TITLE = "File Error";
	private static final String NOT_READABLE_MSG =
		"Cannot read the specified file.";
	private static final String NOT_WRITABLE_MSG =
		"Cannot write to the specified file.";

	//  Creation dialog properties
	private static final String CREATE_TITLE = "Creation Error";
	private static final String CREATE_MSG =
		"The specified file does not exist.  Create it?";
	private static final String CREATE_ERR_MSG =
		"Could not create the specified file.";

	// file url prefix
	private static final String FILE_URL_PREFIX = "file:/";

	//  Separator for file extensions
	private static final String EXTENSION_SEP = ".";

	/**
	 *    Check whether or not the given file exists.  If it does, warn
	 *  the user.  This function assumes that the caller wants to do
	 *  an operation that may be cancelled if the file exists.
	 *
	 *  @param file  the file to check
	 *  @param parentComponent  the component on which to place the warning
	 *                          dialog
	 *
	 *  @return  whether or not the operation should continue.  This will
	 *           be false if the user cancelled the operation because the
	 *           file exists.
	 */
	public static boolean existsVeto(File file, Component parentComponent)
	{
		boolean continueWithOp = true;	//  Be optimistic.

		//  If the file exists, let the user choose whether or not to
		//  continue.
		if (file.exists())
		{
			int continueOption =
				JOptionPane.showConfirmDialog(
					parentComponent,
					EXISTS_WARNING_MSG,
					FILE_WARNING_TITLE,
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.ERROR_MESSAGE);
			if (continueOption != JOptionPane.OK_OPTION)
			{
				continueWithOp = false;
			}
		}

		return continueWithOp;
	}

	/**
	 *    Check whether or not the given file is readable.  If not, show
	 *  the user an error.
	 *
	 *  @param file  the file to check
	 *  @param parentComponent  the component on which to place the error
	 *                          dialog
	 *
	 *  @return  whether or not the file was readable
	 */
	public static boolean checkReadable(File file, Component parentComponent)
	{
		boolean isReadable = false;

		if (file != null)
		{
			isReadable = file.canRead();
		}

		//  If needed, show an error.
		if (!isReadable)
		{
			JOptionPane.showMessageDialog(parentComponent,
										  NOT_READABLE_MSG,
										  FILE_ERROR_TITLE,
										  JOptionPane.ERROR_MESSAGE);
		}

		return isReadable;
	}

	/**
	 *    Check whether or not the given file is writable.  If not, show
	 *  the user an error.
	 *
	 *  @param file  the file to check
	 *  @param parentComponent  the component on which to place the error
	 *                          dialog
	 *
	 *  @return  whether or not the file was writable
	 */
	public static boolean checkWritable(File file, Component parentComponent)
	{
		boolean isWritable = false;

		if (file != null)
		{
			//  If the file exists, check directly.
			if (file.exists())
			{
				isWritable = file.canWrite();
			}

			//  Otherwise, make sure we have permission to write to this
			//  directory.
			else
			{
				File parentDir = file.getParentFile();
				if (parentDir == null || parentDir.canWrite())
				{
					isWritable = true;
				}
			}
		}

		//  If needed, show an error.
		if (!isWritable)
		{
			JOptionPane.showMessageDialog(parentComponent,
										  NOT_WRITABLE_MSG,
										  FILE_ERROR_TITLE,
										  JOptionPane.ERROR_MESSAGE);
		}

		return isWritable;
	}

	/**
	 *    Check whether or not the given file exists.  If it doesn't
	 *  ask the user if it should be created.
	 *
	 *  @param file  the file to check for existence
	 *  @param parentComponent  the component on which to place the error
	 *                          dialog
	 *
	 *  @return  whether or not the file exists (either before of after
	 *           creation by this method
	 */
	public static boolean checkCreate(File file, Component parentComponent)
	{
		boolean fileExists = file.exists();

		if (!fileExists)
		{
			//  Ask the user if they want to create the file.  If so,
			//  do it.
			if (JOptionPane.showConfirmDialog(parentComponent,
											  CREATE_MSG,
											  CREATE_TITLE,
											  JOptionPane.OK_CANCEL_OPTION,
											  JOptionPane.ERROR_MESSAGE) ==
				JOptionPane.OK_OPTION)
			{
				try
				{
					file.createNewFile();
					fileExists = true;
				}

				//  We couldn't create the file.
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(parentComponent,
												  CREATE_ERR_MSG,
												  CREATE_TITLE,
												  JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		return fileExists;
	}

	/**
	 *    Return a file with the proper extension.  If the given file
	 *  does not have the desired extension, this extension is added to the
	 *  file.  If it does, the given file is returned.
	 *
	 *  @param file  the file that is checked to make sure it has an extension
	 *  @param extension  the extension to add to the file, if needed
	 *
	 *  @return  a file with the proper selection, possibly the given file
	 */
    public static File addExtension(File file, String extension)
	{
        String filePath = file.getAbsolutePath();

		if(!filePath.endsWith(EXTENSION_SEP + extension))
		{
			//  Don't add the separator unless we need to.
			if (!filePath.endsWith(EXTENSION_SEP))
			{
				filePath += EXTENSION_SEP;
			}

			file = new File(filePath + extension);
		}

		return file;
	}

    /**
     * returns a file representing the path to the resources
     * directory based on the user-specified propery, or null
     * if the property has not been set
     */
    public static File getResourcesDirectory()
    {
        File resourcesFile = null;
        URL resourcesUrl = getResourcesDirectoryURL();

        if(resourcesUrl != null)
        {
            resourcesFile = new File(resourcesUrl.getFile());
        }

        return resourcesFile;
    }

    /**
     * returns a URL representing the full path to the resources
     * directory based on the user-specified property, or null
     * if the property has not been specified
     */
    public static URL getResourcesDirectoryURL()
    {
		// Determine what the relative resources directory is
        String relativeResourcesDir = System.getProperty(
			    IrcPrefKeys.RESOURCES_DIR);

		URL relativeResourcesDirUrl;

		try
		{
			relativeResourcesDirUrl =
					Sys.getResourceManager().getResource(relativeResourcesDir);
		}
        catch(Exception e)
        {
            relativeResourcesDirUrl = null;
        }

        return relativeResourcesDirUrl;
    }

	/**
	 * returns true if the path of the given file is relative to the
	 * resources directory, false otherwise
	 */
	public static boolean isRelativeToResourcesDir(File file)
	{
		boolean relative = false;

		URL relativeUrl = null;
		String remainder = null;

		try
		{
			relativeUrl = file.toURL();

			//---Get URL for relative script dir
			URL relativeResourcesDirUrl = getResourcesDirectoryURL();

			//---Does the new location refer to something in the relative script directory?
			if (relativeUrl.toString().indexOf(
				    relativeResourcesDirUrl.toString()) == 0)
			{
				relative = true;
			}
		}
		catch(Exception e)
		{
			relative = false;
		}

		return relative;
	}

	/**
	 * returns a path which is relative to the resources directory, or
	 * the entire path if it is not relative
	 */
	public static String getRelativeToResourcesPath(File file)
	{
		URL relativeUrl = null;
		String remainder = null;

        String relativeResourcesDir = System.getProperty(IrcPrefKeys.RESOURCES_DIR);

		try
		{
			relativeUrl = file.toURL();

			//---Get URL for relative script dir
			URL relativeResourcesDirUrl = getResourcesDirectoryURL();
			//---Does the new location refer to something in
            //the relative script directory?
			if (relativeUrl.toString().indexOf(
				    relativeResourcesDirUrl.toString()) == 0)
			{
				//---Determine length of matching parts
				int len = relativeResourcesDirUrl.toString().length();
				//---Get a hold of the part of the new location
                // that is different
				remainder = relativeResourcesDir +
                        relativeUrl.toString().substring(len);
			}
			else
			{
				//---Save url file path
				remainder = relativeUrl.toString().substring(
					    FILE_URL_PREFIX.length());
			}
		}
		catch(Exception e)
		{
            remainder = file.getPath();
		}

		// remove trailing slash if there is one
        if(remainder.endsWith("/"))
		{
			remainder = remainder.substring(0, remainder.length() - 1);
		}

		return remainder;
	}

	/**
	 * If the given file's path is relative to the resources
	 * directory then it is converted to a full path based on
	 * the resources path. Otherwise, the existing path is
	 * returned
	 */
	public static String convertToResourcesPath(File file)
	{
		String convertedPath;

        String relativeResourcesDir = System.getProperty(IrcPrefKeys.RESOURCES_DIR);

		try
		{
			// get the URL for the resources directory
			URL relativeResourcesDirUrl = getResourcesDirectoryURL();
			String path = relativeResourcesDirUrl.toString().substring(
				    FILE_URL_PREFIX.length());

			// convert the given file to a URL
			URL fileUrl = file.toURL();
			// get the part after the resources path
			int index = fileUrl.toString().lastIndexOf(relativeResourcesDir);
			if(index > -1)
			{
				// relative
			    String filePath = fileUrl.toString().substring(index +
					    relativeResourcesDir.length());
				convertedPath = path + filePath;
			}
			else
			{
				// not relative
				convertedPath = file.getPath();
			}
		}
		catch(Exception e)
		{
			convertedPath = file.getPath();
		}

        // remove trailing slash if there is one
		if(convertedPath.endsWith("/"))
		{
			convertedPath = convertedPath.substring(
				    0, convertedPath.length() - 1);
		}

		// create a file so we get it in the system format
		File conversionFile = new File(convertedPath);
		return conversionFile.getPath() ;
	}

    /**
     * convert the given file's path to a path which is based on the
     * URL format (containing single forward slashes)
     */
    public static String convertToUrlFormat(File file)
    {
        String path = null;

        try
        {
            URL url = file.toURL();
            path = url.toString().substring(FILE_URL_PREFIX.length());

            // remove trailing slash if there is one
            if(path.endsWith("/"))
		    {
			    path = path.substring(
				    0, path.length() - 1);
		    }
        }
        catch(Exception e)
        {
            path = file.getPath();
        }

        return path;
    }
 }
