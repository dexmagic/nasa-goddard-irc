//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: 
//	 2	IRC	   1.1		 11/13/2001 11:19:21 AMJohn Higinbotham Javadoc
//		  udpate.
//	 1	IRC	   1.0		 12/12/2000 8:15:12 AMMike Bandy	  
//	$
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Direct stdout and stderr to a file along with the usual stdout.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @author Mike Bandy
**/
public class Tee
{
	protected PrintStream fDefaultStdout;
	protected PrintStream fDefaultStderr;
	protected PrintStream fRedirectedStream = null;
	private static final Tee sTheInstance = new Tee();

	/**
	 * Constructor.
	**/
	private Tee()
	{
		// Grab the stdout/stderr PrintStreams so they can be restored when
		// Tee is closed
		fDefaultStdout = System.out;
		fDefaultStderr = System.err;
	}

	/**
	 *  Get an instance of this class.	
	 *
	 *	@return the Singleton instance
	**/
	public static Tee getInstance()
	{
		return sTheInstance;
	}

	/**
	 * Set the output filename.
	 *
	 * @param	fileName the name of the file to direct stdout and stderr to.
	 *				If the Tee is currently directed to a file, the file is
	 *				flushed and closed first.
	 *
	 * @exception IOException - if the file can't be opened
	**/
	public void setOutput( String fileName ) throws IOException
	{
		// If the log file is currently open, close it
		if( fRedirectedStream != null )
		{
			close();
		}

		// Open the output file
		FileOutputStream fileStream = new FileOutputStream( fileName );
		// Pass the TeePrintStream both Streams
		fRedirectedStream = new TeePrintStream( fileStream, fDefaultStdout );
		// Redirect stdout and stderr to file
		System.setOut( fRedirectedStream );
		System.setErr( fRedirectedStream );
	}

	/**
	 * Close log file.
	 *
	**/
	public void close()
	{
		if( fRedirectedStream != null )
		{
			// Close the log file stream
			fRedirectedStream.flush();
			fRedirectedStream.close();
			fRedirectedStream = null;

			// Restore default stdout/stderr assignments
			System.setOut( fDefaultStdout );
			System.setErr( fDefaultStderr );
		}
	}

// -----------------------------------------------------------------

	/**
	 *	Inner class that will do the actual receiving of messages directed to
	 *	stderr and stdout.  It extends the PrintStream that's directed to the
	 *	log file and has instance information about the JVM's default stdout.
	 *	The unimplemented calls to the PrintStream's multitude of print, println
	 *	and write calls all wind up calling this subclasses write method
	 *  implemented below.
	 *
	 *	This is registered with the JVM via System.setXXX calls.
	 *
	**/
	private class TeePrintStream extends PrintStream
	{
		protected PrintStream fDefaultStdout;

		/*
		 *	Constructor
		 *	@param	fileOut	FileOutputStream open to disk log file
		 *	@param	ttOut	PrintStream open to where stdout output should go
		**/
		public TeePrintStream( OutputStream fileOut, PrintStream ttOut )
		{
			super( fileOut, true );		// Auto-flush on
			fDefaultStdout = ttOut;		// Save off default stdout
		}

		/**
		 *	write
		 *
		 *	Calls to this instances print, println and write methods result
		 *	in being handled by the superclass PrintStream methods which
		 *	(determined experimentally-JDK 1.2) ultimately call this method.
		 *	So this is the only method that needs implementing.
		 *
		**/
		public void write( byte buf[], int off, int len )
		{
			// Write to log file and stdout, then flush in case we crash
			super.write( buf, off, len );		// Log file
			fDefaultStdout.write( buf, off, len );	// Stdout
			super.flush();
			fDefaultStdout.flush();
		}
	}
}
