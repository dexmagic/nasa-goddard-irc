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

package gov.nasa.gsfc.commons.system.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class behaves like an InputStream, except that the call to close
 * doesn't do anything. The need for this class necessitated from the fact
 * that a call to close on the ObjectInputStream, closes the underlying 
 * input stream. However, we may not want to close the underlying 
 * InputStream everytime (for example if the underlying stream is a socket 
 * connection), hence this class.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/05/02 15:25:28 $
 *  @author	 Bhavana Singh
**/
public class NonClosingInputStream extends InputStream
{
	private InputStream fInputStream = null;	

	/**
	 * Constructor
	**/
	public NonClosingInputStream (InputStream inputStream) 
	{
		super();
		fInputStream = inputStream;
	}

	/**
	 * Perform a read on the inputstream.
	 * @return amount read in bytes from stream
	**/
	public int read() throws IOException
	{
		int i = fInputStream.read();
		return i;
	}

	/**
	 * Do nothing here. See the class description for explanation.
	**/
	public void close() 
	{
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: NonClosingInputStream.java,v $
//  Revision 1.4  2005/05/02 15:25:28  tames
//  Updated JavaDoc.
//
//  Revision 1.3  2004/09/27 21:46:06  tames
//  Reflects renaming of port adapters
//
//
//   1	IRC	   1.0		 10/3/00 11:22:25 AM  Ibrahim Shafi
//  
