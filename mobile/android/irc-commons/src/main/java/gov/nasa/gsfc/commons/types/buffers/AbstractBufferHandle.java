//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//	$Log: AbstractBufferHandle.java,v $
//	Revision 1.2  2004/08/03 20:24:54  tames_cvs
//	Added context field
//	
//	Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Initial version
//	
//	Revision 1.1.2.2  2004/03/24 20:31:34  chostetter_cvs
//	New package structure baseline
//	
//	Revision 1.2  2003/10/24 08:42:29  mnewcomb_cvs
//	Added $Log: AbstractBufferHandle.java,v $
//	Added Revision 1.2  2004/08/03 20:24:54  tames_cvs
//	Added Added context field
//	Added
//	Added Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//	Added Initial version
//	Added
//	Added Revision 1.1.2.2  2004/03/24 20:31:34  chostetter_cvs
//	Added New package structure baseline
//	Added flag to the header of these files..
//	
//  ---------------------------------------------------
//
//	03/03/2002	T. Ames/588
//
//		Initial version.
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


/**
 *  A AbstractBufferHandle is a class that defines a region of a larger buffer
 *  that is available for use by other classes.
 *  Users should mark the buffer as in use with the <code>setInUse</code>
 *  method before getting access to the buffer region using the
 *  <code>getBuffer</code> method.
 *  Users must notify the implementing class when the buffer is no longer
 *  needed by releasing the buffer handle with the <code>release</code> method.
 *  Multiple calls of <code>setInUse</code> must be paired with
 *  an equal number of <code>release</code> method calls in order for the
 *  buffer region to be reclaimed.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	03/09/2002
 *  @author		T. Ames/588
**/

public abstract class AbstractBufferHandle implements BufferHandle
{
	private int fNumberOfUsers = 0;
	private Object fContext = null;

	/**
	 * Gets the buffer that this BufferHandle refers to. The method
	 * <code>setInUse</code> should be called before getting the buffer.
	 *
	 * @return the Buffer
	 * @see #setInUse
	**/
	public abstract Buffer getBuffer();

	/**
	 * Gets the offset of the containing buffer's position with respect
	 * to it's parent buffer.
	 *
	 * @return the offset
	**/
	public abstract int getParentOffset();

	/**
	 *  Releases buffer from use of the caller. Should be called after
	 *  <code>setInUse</code> when the buffer is no longer needed by the
	 *  caller. This implementation causes <code>releaseBuffer</code> to be
	 *  called if and only if there are no current users of the buffer.
	 *
	 *  @see #setInUse
	 *  @see #releaseBuffer
	**/
	public synchronized void release()
	{
		fNumberOfUsers--;

		if (fNumberOfUsers < 0)
		{
			System.out.println(
				"Warning BufferHandler release() was called more times than needed. "
				+ "Number of users is " + fNumberOfUsers
				+ " from thread: " + Thread.currentThread());
			fNumberOfUsers = 0;
		}
		if (fNumberOfUsers == 0)
		{
			releaseBuffer();
		}
	}

	/**
	 *  Releases buffer from use. This is only called if there are no current
	 *  users of the buffer.
	**/
	protected abstract void releaseBuffer();
		
	/**
	 * Get the optional context of this BufferHandle.
	 * @return context or null if not available
	 */
	public Object getContext()
	{
		return fContext;
	}

	/**
	 * Set the optional context of this BufferHandle.
	 * @param context the context or null if not meaningful
	 */
	public void setContext(Object context)
	{
		fContext = context;
	}

	/**
	 *  Marks the buffer as in use to prevent it from being overwritten or
	 *  reclaimed. Should be paired with calls to <code>release</code>.
	 *  @see #release
	**/
	public synchronized void setInUse()
	{
		fNumberOfUsers++;
	}

	/**
	 *  Returns true if the buffer is in use by one or more users.
	 *
	 *  @return true if in use.
	**/
	public boolean isInUse()
	{
		if (fNumberOfUsers > 0)
			return(true);
		else
			return(false);
	}
}
