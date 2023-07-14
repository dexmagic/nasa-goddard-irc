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
//	 1	IRC	   1.0		 1/10/2002 3:10:09 PM John Higinbotham 
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

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The goal of the TPrintStream is to serve as a sink for which data is written to 
 * both a primary and a secondary stream when a write is performed on the TPrintStream. 
 * The stream must be provided with a single primary stream at creation. The secondary
 * stream can be added and changed after the TPrintStream has been created.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @author John Higinbotham 
 * @version $Date: 2004/07/12 14:26:23 $
**/
public class TPrintStream extends PrintStream
{
	private OutputStream fPrimaryStream;
	private OutputStream fSecondaryStream;

	//--------------------------------------------------------------

	/**
	 * Build a new TPrintStream.
	 *
	 * @param stream OutputStream (primary) that the stream will write to. 
	**/
	public TPrintStream(OutputStream stream)
	{
		super(stream, true);
		fPrimaryStream = stream;
	}

	/**
	 * Set the secondary stream.
	 *
	 * @param stream OutputStream serving as a secondary stream.
	**/
	public void setSecondaryStream(OutputStream stream)
	{
		closeSecondaryStream();
		fSecondaryStream = stream;
	}

	/**
	 * Close the TPrintStream by closing the primary and secondary streams. 
	 * 
	**/
	public void close()
	{
		super.close();
		closeSecondaryStream();
	}

	/**
	 * Close the secondary stream only.
	 *
	**/
	public void closeSecondaryStream()
	{
		if (fSecondaryStream != null)
		{
			try
			{
				fSecondaryStream.flush();
				fSecondaryStream.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * Write data.
	 *
	 * @param buf[] Array of bytes to write.
	 * @param off   Offset into array to start writting from.
	 * @param len   How much to write. 
	**/
	public void write(byte buf[], int off, int len)
	{
		writeOutput(buf, off, len, fPrimaryStream);
		writeOutput(buf, off, len, fSecondaryStream);
	}

	/**
	 * Write data.
	 *
	 * @param buf[]  Array of bytes to write.
	 * @param off	Offset into array to start writting from.
	 * @param len	How much to write. 
	 * @param output OutputStream to write to.
	 *
	**/
	private void writeOutput(byte buf[], int off, int len, OutputStream output)
	{
		if (output != null)
		{
			try
			{
				output.write(buf, off, len);
				output.flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
