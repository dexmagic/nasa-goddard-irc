//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Compressor.java,v $
//  Revision 1.4  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.1  2004/05/05 19:11:52  chostetter_cvs
//  Further restructuring
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.commons.system.storage;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import gov.nasa.gsfc.commons.processing.progress.ProgressEvent;
import gov.nasa.gsfc.commons.processing.progress.ProgressListener;
import gov.nasa.gsfc.commons.system.io.FileUtil;


/**
 *  Support for compressing a directory and its contents into
 *  a single jar file and vice versa while running in a separate thread.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2006/01/23 17:59:54 $
 *  @author	John Higinbotham 
**/
public class Compressor implements Runnable 
{
	//---Constants 
	private final static int ERROR			 = ProgressEvent.ERROR; 
	private final static int NOMINAL		   = ProgressEvent.NOMINAL; 
	private final static int BUFFER_SIZE	   = 1000;
	private final static int COMPRESSION_LEVEL = 5;

	//---Vars
	private String fSrc				= null; 
	private String fDest			   = null;
	private boolean fDel			   = false;
	private ProgressListener fListener = null;
	private int fCurrentPosition	   = 0;
	private int fLastPosition		  = 0;
	private int fState				 = NOMINAL;
	private boolean fStarted		   = false;
	private boolean fCompressionMode   = true;
	private byte[] fBuffer			 = new byte[BUFFER_SIZE];
	private JarEntry fEntry			= null;

	/**
	 * Create a new Compressor. 
	 *
	 * @param src	   Source directory to compress. 
	 * @param dest	  Destination file to write compressed contents 
	 * @param del	   If true, delete directory and contents after compressed file produced. 
	 * @param listener  Listener of progress events 
	**/
	public Compressor(String src, String dest, boolean del, ProgressListener listener)
	{
		fSrc	  = src;
	 	fDest	 = dest;	
		fDel	  = del;
		fListener = listener;
	}

	/**
	 * Create a new Compressor. 
	 *
	 * @param src	   Source directory to compress. 
	 * @param dest	  Destination file to write compressed contents 
	 * @param del	   If true, delete directory and contents after compressed file produced. 
	**/
	public Compressor(String src, String dest, boolean del)
	{
		fSrc  = src;
	 	fDest = dest;	
		fDel  = del;
	}

	/**
	 * Set to compression mode. 
	 *
	**/
	public void setCompressionMode()
	{
		if (!fStarted)
		{
			fCompressionMode = true;
		}
	}

	/**
	 * Set to decompression mode. 
	 *
	**/
	public void setDecompressionMode()
	{
		if (!fStarted)
		{
			fCompressionMode = false;
		}
	}

	/**
	 * Kick off the compression/decompression processing. 
	 *
	**/
	public void start()
	{
		if (!fStarted)
		{
			fCurrentPosition = 0;
			fLastPosition	= Integer.MAX_VALUE; 
			fState		   = NOMINAL;
			Thread t		 = new Thread(this);
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();
		}
	}

	/**
	 * Notify a listener about some progress. 
	 *
	 * @param message Message for listener.
	**/
	private void notifyListener(String message)
	{
		if (fListener != null)
		{
			float complete = (float) fCurrentPosition / (float) fLastPosition;
			fListener.handleEvent(new ProgressEvent(complete, fState, message));		
		}
	}

	/**
	 * Run this thread. 
	 *
	**/
	public void run()
	{
		if (fCompressionMode)
		{
			compress();
		}
		else 
		{
			decompress();
		}
	}

	/**
	 * Determine how my of the items in an array are actual files. 
	 *
	 * @param array File[] which may contain both files and directories
	 * @return number of items that were files
	**/
	public int fileCount(File[] array)
	{
		int rval = 0;
		for (int i=0; i<array.length; i++)
		{
			if (array[i].isFile())
			{
				rval++;
			}
		}
		return rval;
	}

	/**
	 * Perform compression. 
	 *
	**/
	private void compress()
	{
		File srcFile = new File(fSrc);
		File[] list  = null;
		String root  = null;
		int rootLen  = 0;
		String msg   = null;
		int count	= 0;

		//---Verify that we were provided a directory for the source
		if (srcFile.isDirectory())
		{
			//---Determine the root of the source
			root = srcFile.getParent();
			if (root != null)
			{
				rootLen = root.length();
			}
		}
		else
		{
			//---Terminate if bogus directory 
			fState = ERROR;
			notifyListener("Failed!");
			return;
		}

		//---Determine what files need compressed and process them
		list = FileUtil.listRecursive(fSrc);
		if (list != null)
		{
			//---Set up the output file
			JarOutputStream jos = StorageUtil.openJOS(fDest);
			jos.setLevel(COMPRESSION_LEVEL);

			try 
			{
				//fLastPosition = list.length;
				fLastPosition = fileCount(list); 
				for (int i=0; i<list.length; i++)
				{
					//---Build entry with relative path
					String relPath = null;
					if (root.endsWith(File.separator))
					{
						relPath = new String(list[i].toString().substring(rootLen));
					}
					else
					{
						relPath = new String(list[i].toString().substring(rootLen + 1));
					}
					fEntry = new JarEntry(relPath);

					//---Store file 
					if (list[i].isFile())
					{
						//---Update listener
						count++;
						msg = "file " + Integer.toString(count) + " of " + Integer.toString(fLastPosition);
						notifyListener(msg);

						FileInputStream fis = null;
						try
						{
							fEntry.setMethod(JarEntry.DEFLATED);
							jos.putNextEntry(fEntry);
							fis = new FileInputStream(list[i]); 
							int readSize = 0;
							boolean done = false;

							while (!done)
							{
								readSize = fis.read(fBuffer);
								if (readSize > 0) 
								{
									jos.write(fBuffer, 0, readSize);
									readSize = 0;
								}
								else if (readSize == -1)
								{
									done = true;
								}
								StorageUtil.sleep(0);
							}
							if (fis != null)
							{
								fis.close();
							}
						}
						catch (EOFException e)
						{
							e.printStackTrace();
						}
						jos.closeEntry();
						fCurrentPosition = i+1;
					}
				}
				notifyListener("Complete!");
				jos.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				fState = ERROR;
				notifyListener("Failed!");
			}
		}
		remove();
	}

	/**
	 * Perform decompression. 
	 *
	**/
	private void decompress()
	{
		File srcFile		= new File(fSrc);
		JarInputStream jis  = null;
		String curName	  = null;
		String outName	  = null;
		int count		   = 0;

		if (!srcFile.isFile())
		{
			//---Complain if we were not given a src file
			fState = ERROR;
			notifyListener("Failed!");
		}
		else
		{
			try 
			{
				//---Determine how many items we have to process (to support status)
				JarFile jarfile		 = new JarFile(fSrc);
				Enumeration enumeration = jarfile.entries();
				fLastPosition = 0;
				while (enumeration.hasMoreElements())
				{
					enumeration.nextElement();
					fLastPosition++;
				}
				jarfile.close();

				//---Start processing	
				jis = StorageUtil.openJIS(fSrc);
				fEntry = jis.getNextJarEntry();

				while (fEntry != null)
				{
					//---Update listener
					count++;
					curName = fEntry.getName();
					notifyListener("file " + Integer.toString(count) + " of " + Integer.toString(fLastPosition));

					//---Build output name 
					outName = fDest + File.separator + curName;

					//---Verify that there is a directory to store it in
					File outdir = new File(outName);
					StorageUtil.checkDirectory(outdir.getParent());
					
					//---Build output stream
					FileOutputStream fos = new FileOutputStream(outName);

					//---Decompress
					int readSize = 0;
					boolean done = false;

					while (!done)
					{
						readSize = jis.read(fBuffer);
						if (readSize > 0) 
						{
							fos.write(fBuffer, 0, readSize);
							readSize = 0;
						}
						else if (readSize == -1)
						{
							done = true;
						}
					}
					fos.close();
					fEntry = jis.getNextJarEntry();
					fCurrentPosition++;
				}
				jis.close();
				notifyListener("Complete!");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				fState = ERROR;
				notifyListener("Failed!");
			}
		}
		remove();
	}

	/**
	 * Remove the source of the compressed item if requested and all is nominal.
	 *
	**/
	public void remove()
	{
		if (fState == NOMINAL && fDel) 
		{
			FileUtil.rmdir(fSrc);
		}
	}
}
