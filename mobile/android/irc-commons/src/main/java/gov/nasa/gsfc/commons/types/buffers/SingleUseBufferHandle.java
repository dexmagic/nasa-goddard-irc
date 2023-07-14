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

package gov.nasa.gsfc.commons.types.buffers;

import java.nio.Buffer;
import java.nio.ByteBuffer;


/**
 * A SingleUseBufferHandle unlike the typical BufferHandle is not shared. Users
 * should mark the buffer as in use with the <code>setInUse</code> method
 * before getting access to the buffer region using the <code>getBuffer</code>
 * method. Users must notify the implementing class when the buffer is no longer
 * needed by releasing the buffer handle with the <code>release</code> method.
 * Multiple calls of <code>setInUse</code> must be paired with an equal number
 * of <code>release</code> method calls in order for the buffer region to be
 * reclaimed.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version 03/09/2002
 * @author T. Ames/588
 */
public class SingleUseBufferHandle extends AbstractBufferHandle
{
	private ByteBuffer fBuffer;
	
	public SingleUseBufferHandle(ByteBuffer buffer)
	{
		fBuffer = buffer;
	}
	
	/**
	 * Gets the buffer that this BufferHandle refers to. The method
	 * <code>setInUse</code> should be called before getting the buffer.
	 *
	 * @return the Buffer
	 * @see #setInUse
	**/
	public Buffer getBuffer()
	{
		return fBuffer.asReadOnlyBuffer();
	}

	/**
	 * Gets the offset of the containing buffer's position with respect
	 * to it's parent buffer.
	 *
	 * @return the offset
	**/
	public int getParentOffset()
	{
		return 0;
	}

	/**
	 * Releases buffer from use. This is only called if there are no current
	 * users of the buffer.
	 */
	protected void releaseBuffer()
	{
		fBuffer = null;
	}
}

//--- Development History:
// $Log: SingleUseBufferHandle.java,v $
// Revision 1.4  2005/09/09 21:25:49  tames
// Reverted back to allowing only ByteBuffers as the underlying buffer.
//
//Revision 1.3  2005/09/08 22:18:32  chostetter_cvs
//Massive Data Transformation-related changes
//
//Revision 1.2  2004/08/23 13:51:19  tames
//Javadoc change only
//
//Revision 1.1  2004/08/03 20:24:36  tames_cvs
//Initial Version
//
//Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//Initial version
//
//Revision 1.1.2.2  2004/03/24 20:31:34  chostetter_cvs
//New package structure baseline
//
//Revision 1.2  2003/10/24 08:42:29  mnewcomb_cvs
//Added 
//Added Revision 1.3  2005/09/08 22:18:32  chostetter_cvs
//Added Massive Data Transformation-related changes
//Added
//Added Revision 1.2  2004/08/23 13:51:19  tames
//Added Javadoc change only
//Added
//Added Revision 1.1  2004/08/03 20:24:36  tames_cvs
//Added Initial Version
//Added
//Added Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//Added Initial version
//Added
//Added Revision 1.1.2.2  2004/03/24 20:31:34  chostetter_cvs
//Added New package structure baseline
//Added flag to the header of these files..
//
//---------------------------------------------------
//
//03/03/2002	T. Ames/588
//
//	Initial version.
