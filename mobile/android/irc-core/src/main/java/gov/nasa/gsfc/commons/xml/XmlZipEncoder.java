//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: XmlZipEncoder.java,v $
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
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

package gov.nasa.gsfc.commons.xml;

import java.beans.Expression;
import java.beans.Statement;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.OutputStream;


/**
 *
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:24 $
 *  @author	Ken Wootton
 */

public class XmlZipEncoder extends XMLEncoder
{
	//  Postamble
	private static final String POSTAMBLE = "</java>\n";

	private OutputStream fOut = null;
	
	//  Has the encoder been declared as finished?
	private boolean fFinished = false;

	/**
	 *   Create a new XmlZipEncoder
	 * .
	 * @param out  output stream
	 */
	public XmlZipEncoder(OutputStream out)
	{
		super(out);
		
		fOut = out;
	}
	
	/**
	 *	This method calls the superclass's implementation and records 
	 *  oldExp so that it can produce the actual output when the stream 
	 *  is flushed.	 
	 *  
	 *  @param oldExp  The expression to be written to the stream
	 * 
	 */
	public void writeExpression(Expression oldExp)
	{
	}

	/**
	 *	This method calls the superclass's implementation and
	 *  records <code>oldStm</code> so that it can produce the
	 *  actual output when the stream is flushed.
	 *
	 *  @param oldStm The statement to be written to the stream.
	 *
	 */
	public void writeStatement(Statement oldStm)
	{
	}

	/**
	 *	Finishes writing the contents of the encoding to the output stream.
	 *  This includes flushing the stream and writing the postamble of the 
	 *  encoding.<p>
	 *	Note that this will not close the output stream.  This allows the
	 *  stream to continue to be used by other parties for other things, 
	 *  e.g. a zip output stream could have more entries added to it.<p>
	 *	Also note that the encoder should NOT be used after making this
	 *  call.  It is essentially closed, without closing the wrapped
	 *  output stream.
	 */ 
	public void finish()
	{
		fFinished = true;
		writePostamble();
	}
	
	/**
	 *	This method calls <code>flush</code>, calls <code>finish</code>,
	 *  and then closes the output stream associated with this stream.
	 */
	public void close() {

		if (!fFinished)
		{
			finish();
		}
		
		try 
		{
			fOut.close();
		}
		catch (IOException e) 
		{
		}
	}
	
	/**
	 *	Write the postamble to the output stream.
	 */
	protected void writePostamble()
	{
		//  Write out the simple line.
		try 
		{
			fOut.write(POSTAMBLE.getBytes());
		}
		
		catch (IOException ex) 
		{
		}
	}

}
