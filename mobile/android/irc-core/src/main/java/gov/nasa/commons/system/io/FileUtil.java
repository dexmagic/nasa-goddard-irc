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
//   17   IRC	   1.16		10/3/2002 2:05:38 PM John Higinbotham
//   16   IRC	   1.15		10/3/2002 1:43:37 PM John Higinbotham
//   15   IRC	   1.14		1/8/2002 4:33:03 PM  John Higinbotham Sort filtered
//		list of files searched for my extension.
//   14   IRC	   1.13		11/12/2001 6:01:02 PMJohn Higinbotham Javadoc
//		update.
//   13   IRC	   1.12		11/7/2001 6:11:37 PM John Higinbotham Add extension
//		support.
//   12   IRC	   1.11		10/26/2001 11:41:53 AMJohn Higinbotham Updated copy
//		methods.
//   11   IRC	   1.10		10/18/2001 4:02:52 PMJohn Higinbotham Add file
//		creation support method.
//   10   IRC	   1.9		 9/26/2001 6:34:31 PM John Higinbotham Updated to
//		support running from a jar file.
//   9	IRC	   1.8		 9/25/2001 3:06:01 PM John Higinbotham Fix null
//		pointer exception.
//   8	IRC	   1.7		 9/24/2001 2:03:43 PM John Higinbotham Add support
//		for maintaining no more than N files in a specified directory.
//   7	IRC	   1.6		 9/19/2001 4:59:01 PM John Higinbotham Add support
//		for getting url file extension.
//   6	IRC	   1.5		 9/13/2001 7:14:00 PM John Higinbotham Add support
//		for getting filename from URsLog.
//   5	IRC	   1.4		 9/6/2001 1:49:39 PM  John Higinbotham Added URL
//		support method.
//   4	IRC	   1.3		 8/24/2001 4:36:28 PM John Higinbotham Add url
//		support method.
//   3	IRC	   1.2		 8/22/2001 9:21:38 AM John Higinbotham Seperated
//		internal filter class.
//   2	IRC	   1.1		 8/14/2001 3:39:06 PM John Higinbotham Update based
//		on file utility reorg.
//   1	IRC	   1.0		 8/1/2001 2:18:52 PM  John Higinbotham
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

import gov.nasa.gsfc.commons.system.Sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;


/**
 *  This class provides support methods for working with files.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2006/06/16 19:27:13 $
 *  @author  	John Higinbotham
**/
public class FileUtil
{
	private static int BYTES_PER_READ				= 256;
	private final static String DATE_FORMAT		  = "yyyyMMdd_HHmmssSSS";
	private final static SimpleDateFormat fFormatter = new SimpleDateFormat(DATE_FORMAT);
	private final static String FILE_PROTO		   = "file";

	/**
	 * Find files with the specified extension in the specified directory.
	 *
	 * @param directory Directory to look in.
	 * @param extension File extension to look for.
	 * @return File[] Array of files meeting above criteria.
	**/
	public static File[] getFilesWithExtension(File directory, String extension)
	{
		File[] rval = null;
		if (directory != null)
		{
			FileFilter filter = new ExtensionFileFilter(extension);
			rval = directory.listFiles(filter);
		}

		//---Sort array so it appears more organized
		Arrays.sort(rval);

		return rval;
	}

	/**
	 * Clean up files with a specific extension in a specified directory.
	 * The caller indicates the number of files to maintain.
	 * The remainder will be deleted. This method is most useful when files have
	 * some numeric ordering. If the number of files is greater than the number
	 * to be maintained, file deletion will start from the begining of the
	 * the list of name ordered files.
	 *
	 * @param directory Directory to look in.
	 * @param extension File extension to look for.
	 * @param maintain Preserve the specified number of files if non-negative.
	 * @return File[] Array of files meeting above criteria.
	**/
	public static void cleanupDirectory(File directory, String extension, int maintain)
	{
		if (maintain >= 0)
		{
			File[] files = getFilesWithExtension(directory, extension);

			if (files != null && maintain < files.length)
			{
				for (int i=0; i<files.length-maintain; i++)
				{
					files[i].delete();
				}
			}
		}
	}

	/**
	 * Copy a file to a particular directory.
	 *
	 * @param file Source File.
	 * @param directory Directory to copy file to.
	**/
	public static void copyToDirectory(File file, File directory)
	{
		checkDirectory(directory);
		File dest = new File(directory.toString() + File.separator + file.getName());
		copy(file, dest);
	}

	/**
	 * Copy a file to a particular directory.
	 *
	 * @param file URL of file.
	 * @param directory Directory to copy file to.
	**/
	public static void copyToDirectory(URL file, File directory)
	{
		checkDirectory(directory);
		File dest = new File(directory.toString() + File.separator + getFilename(file));
		copy(file, dest);
	}

	/**
	 * Copy a file from one location to another.
	 *
	 * @param source Source File.
	 * @param destination Destination File.
	**/
	public static void copy(File source, File destination)
	{
		try
		{
			copy(source.toURL(), destination);
		}
		catch (Exception e)
		{
			System.out.println("Error copying " + source + " to " + destination);
		}
	}

	/**
	 * Copy a file from one location to another.
	 *
	 * @param source Source URsLog.
	 * @param destination Destination File.
	**/
	public static void copy(URL source, File destination)
	{
		try
		{
			InputStream in	   = source.openStream();
			FileOutputStream out = new FileOutputStream(destination);
			int amountRead	   = 0;
			byte[] buffer		= new byte[BYTES_PER_READ];
			amountRead = in.read(buffer);
			while (amountRead > 0)
			{
				out.write(buffer, 0, amountRead);
				amountRead = in.read(buffer);
			}
			out.flush();
			in.close();
			out.close();
		}
		catch (Exception e)
		{
			System.out.println("Error copying " + source + " to " + destination);
		}
	}

    /**
     * Read a file into a string
     * @param file
     * @return String contents of file
     * @throws java.io.IOException
     */
    public static String readFileIntoString(File file) throws java.io.IOException
    {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(file));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
    
	/**
	 * Dump File[] to stdout.
	 *
	 * @param files File[] to dump.
	**/
	public static void dumpFiles(File[] files)
	{
		if (files != null)
		{
			System.out.println("files found: " + files.length);
			for (int i=0; i<files.length; i++)
			{
				System.out.println(files[i].toString());
			}
		}
	}

	/**
	 * Return a standard date format.
	 *
	 * @param date Date to format.
	 * @return Formatted string.
	 *
	**/
	public static String formatDate(Date d)
	{
		return fFormatter.format(d);
	}

	/**
	 * Make a File based on a directory and file name string.
	 *
	 * @param directory File's base directory
	 * @param name File's name
	 * @return File
	**/
	public static File getFile(File directory, String name)
	{
		String s  = directory.toString() + File.separator + name;
		File rval = new File(s);
		return rval;
	}

	/**
	 * Make a File based on a directory and file. If the file
	 * represents a path then just the filename portion will be
	 * appended to the directory.
	 *
	 * @param directory File's base directory
	 * @param file File
	 * @return File
	**/
	public static File getFile(File directory, File file)
	{
		String s  = directory.toString() + File.separator + file.getName();
		File rval = new File(s);
		return rval;
	}

	/**
	 * Get file from a name. The value may either be a full path to a file
	 * or a relative path.
	 *
	 * @param file name of file
	 * @return File
	**/
	public static File getFile(String file)
	{
		File rval = null;
		URL url = Sys.getResourceManager().getResource(file);

		if (url != null)
		{
			rval  = FileUtil.urlToFile(url);
		}

		if (rval == null)
		{
			System.out.println("Warning: File not found: " + url + " file:" + file);
		}
		
		return rval;
	}
	
	/**
	 * Make a filename unique by inserting a timebased string.
	 *
	 * @param name Filename string.
	 * @return String
	 *
	**/
	public static String getUniqueFilename(String name)
	{
		return name + "_" + formatDate(new Date());
	}

	/**
	 * Make a filename unique by inserting a timebased string.
	 *
	 * @param name Filename string.
	 * @param ext  Extension string.
	 * @return String
	 *
	**/
	public static String getUniqueFilename(String name, String ext)
	{
		return name + "_" + formatDate(new Date()) + ext;
	}

	/**
	 * Check that the specified directory exists. If it does not exist, try to create it.
	 *
	 * @param path Directory path.
	 * @return Success (true if path exists or is successfully created, false otherwise)
	**/
	public static boolean checkDirectory(String path)
	{
		File d = new File(path);
		return checkDirectory(d);
	}

	/**
	 * Check that the specified directory exists. If it does not exist, try to create it.
	 *
	 * @param path Directory path.
	 * @return Success (true if path exists or is successfully created, false otherwise)
	**/
	public static boolean checkDirectory(File path)
	{
		boolean rval = true;
		if (!path.exists())
		{
			rval = path.mkdirs();
		}
		return rval;
	}

   /**
	 * This method will remove a directory and all of its contents.
	 *
	 * @param dir Root of tree to delete
	**/
	public static void rmdir(String dir)
	{
		File root = new File(dir);
		File[] files = root.listFiles();
		if (files != null)
		{
			for (int i=0; i<files.length; i++)
			{
				rmdir(files[i].toString());
			}
		}
		root.delete();
	}

	/**
	 * This method will recursively provide a listing of all files and directories contained
	 * in the specified directory.
	 *
	 * @param dir  Directory to start with.
	 * @return File[] (array of files/directories)
	**/
	public static File[] listRecursive(String dir)
	{
		File root	   = new File(dir);
		File[] files	= root.listFiles();
		LinkedList list = new LinkedList();
		File[] rval	 = null;

		if (files != null)
		{
			for (int i=0; i<files.length; i++)
			{
				list.add(files[i]);
				File[] tmp = listRecursive(files[i].toString());
				if (tmp != null)
				{
					for (int j=0; j<tmp.length; j++)
					{
						list.add(tmp[j]);
					}
				}
			}
		}

		if (list.size() > 0)
		{
			rval		 = new File[list.size()];
			Object[] src = list.toArray();
			for (int k=0; k<src.length; k++)
			{
				rval[k] = (File) src[k];
			}
		}

		return rval;
	}

	/**
	 * Get the local file path represented by a URL, or NULL if not local.
	 *
	 * @param url URL
	 * @return File
	**/
	public static File urlToFile(URL url)
	{
		File rval = null;
		if (url != null)
		{
			if (url.getProtocol().compareTo(FILE_PROTO) == 0)
			{
				String tmp = url.getPath();
				System.out.println("URL: " + url + " PATH:" + tmp);
				rval = new File(tmp);
			}
			else
			{
				System.out.println(
					"Warning: could not create a File from the url " + url);
			}
		}
		return rval;
	}

	/**
	 * Given a URL, keep the path the same and change out the filename
	 * with a new filename.
	 *
	 * @param original Original URL
	 * @param filename New filename
	 * @return URL
	**/
	public static URL changeFilename(URL original, String filename)
	{
		StringBuffer b = new StringBuffer();
		String buffer  = original.toString();
		URL rval	   = null;
		int lastsep	= -1;
		lastsep		= buffer.lastIndexOf("/");
		b.append(buffer.substring(0,lastsep+1));
		b.append(filename);
		try
		{
			rval = new URL(b.toString());
		}
		catch (Exception e)
		{
			System.out.println("ERROR:: building URL from string: " + b.toString());
		}
		return rval;
	}

	/**
	 * Get filename portion of a URsLog.
	 *
	 * @param url URL
	 * @return String
	**/
	public static String getFilename(URL url)
	{
		File file   = new File(url.getFile());
		String name = file.getName();
		return name;
	}

	/**
	 * Get filename extension.
	 *
	 * @param name file name
	 * @return String
	**/
	public static String getFilenameExtension(String name)
	{
		String rval = "";

		if (name != null)
		{
			int index = name.lastIndexOf(".");
			if (index >=0)
			{
				rval = name.substring(index + 1);
			}
		}
		return rval;
	}

	/**
	 * Get filename extension.
	 *
	 * @param url URL
	 * @return String
	**/
	public static String getFilenameExtension(URL url)
	{
		return getFilenameExtension(getFilename(url));
	}

	/**
	 * Get filename extension.
	 *
	 * @param file File
	 * @return String
	**/
	public static String getFilenameExtension(File file)
	{
		String rval = null;
		try
		{
			rval = getFilenameExtension(file.toURL());
		}
		catch (Exception e)
		{
		}
		return rval;
	}

	/**
	 * Strip extension off of file name.
	 *
	 * @param name String
	 * @return String
	**/
	public static String stripExtension(String name)
	{
		int dotindex = name.lastIndexOf(".");
		String rval = name;
		if (dotindex > 0)
		{
			rval = name.substring(0, dotindex);
		}
		return rval;
	}

	/**
	 * Strip path prefix off of file name.
	 *
	 * @param name String
	 * @return String
	**/
	public static String stripPath(String name)
	{
		int index = name.lastIndexOf("/");
		String rval = name;
		if (index > 0)
		{
			rval = name.substring(index + 1);
		}
		return rval;
	}

	/**
	 * Computes the path for the specified file that is relative to some
	 * root file. If a relative path can not be computed, this method will
	 * return null.
	 * 
	 * @param	file	compute relative path for this file
	 * @param	root	path should be relative to location of this file or directory
	 * @return			relative path location or null
	**/
	public static String getRelativePath(File file, File root)
	{
		// Get root directory
		File dir = root;
		if (!dir.isDirectory())
		{
			dir = dir.getParentFile();
		}
		
		// If separator is backslash, regular expression requires two of them
		String separator = File.separator;
		if (separator.equals("\\"))
		{
			separator = "\\\\";
		}
		
		// Get individual path elements
		String[] fileElements = file.getAbsolutePath().split(separator);
		String[] rootElements = dir.getAbsolutePath().split(separator);

		// Find where they start to differ
		int diffIndex = 0;
		while (diffIndex < fileElements.length && diffIndex < rootElements.length
				&& fileElements[diffIndex].equals(rootElements[diffIndex]))
		{
			++diffIndex;
		}
		
		if (diffIndex == 0)
		{
			// Then there is no possible relative path from file to root
			return null;
		}
		
		int remainingRootElements = rootElements.length - diffIndex;
		
		StringBuffer buf = new StringBuffer();
		
		// Add back path entries
		for (int i = 0; i < remainingRootElements; ++i)
		{
			if (buf.length() > 0)
			{
				buf.append(File.separator);
			}
			buf.append("..");
		}
		
		// Add remaining file path entries
		for (int i = diffIndex; i < fileElements.length; ++i)
		{
			if (buf.length() > 0)
			{
				buf.append(File.separator);
			}
			buf.append(fileElements[i]);
		}
		
		return buf.toString();
	}

    /**
     * Returns true if two files are identical.  If the files differ
     * or are unreadable, false is returned.  Dumb and slow; was built
     * for unit testing purposes.
     * 
     * @param filename1 First file to compare
     * @param filename2 Second file to compare
     * @return Whether the two files are identical
     */
    public static boolean areFilesIdentical(String filename1, String filename2)
    {
        FileInputStream fis1 = null;
        FileInputStream fis2 = null;
        int bytesRead = 0;
        try {
            fis1 = new FileInputStream( new File(filename1) );
            fis2 = new FileInputStream(new File(filename2));
            int rc1 = 0;
            int rc2 = 0;
            while ((rc1 = fis1.read()) == (rc2 =fis2.read()) && (rc1 >= 0) && (rc2 >=0) )
            {
                bytesRead++;
            }
            
            //System.out.println("filesAreIdentical: bytes compared = " + bytesRead + " " + fis1.available() + " " + fis2.available());
            
            return (rc1 == rc2) && fis1.available() == fis2.available();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return false;
        
    }

    /**
     * Convenience routine to return a classes
     * package name as a (relative) path name.
     * 
     * <P>Useful when accessing test (data) files that are colocated with test classes.
     * @param c The Class 
     * @return The path name for the class (e.g., "gov/nasa/gsfc/irc/app/")
     */
    public static String getClassNameAsFileLocation(Class c)
    {
        return c.getPackage().getName().replace( '.', File.separatorChar) + File.separator;
    }
    
    /**
     * Convenience routine to return a classes
     * name as a (relative) path name.
     * <P>Note, <code>getClassNameAsFileLocation()</code> actually returns the package
     * name, whereas this method returns a class name.  That method is legacy code
     * however so I didn't want to change it.  S. Maher
     * 
     * <P>Useful when accessing test (data) files that are colocated with test classes.
     * @param c The Class 
     * @return The path name for the class (e.g., "gov/nasa/gsfc/irc/app/")
     */
    public static String getClassNameAsFileLocationWithClassName(Class c)
    {
        return c.getName().replace( '.', File.separatorChar);
    }
}
