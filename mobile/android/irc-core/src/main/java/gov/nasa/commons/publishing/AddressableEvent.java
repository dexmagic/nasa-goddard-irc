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

package gov.nasa.gsfc.commons.publishing;

import java.util.EventObject;

import gov.nasa.gsfc.commons.publishing.paths.Path;



/**
 * An AddressableEvent is an event that can hold a destination and a return
 * reply specification in the form of a
 * {@link gov.nasa.gsfc.commons.publishing.paths.Path Path}. The meaning of
 * the destination and reply are application specific.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/18 14:02:49 $
 * @author Troy Ames
 */
public class AddressableEvent extends EventObject
{
	private Path fDestination = null;
	private Path fReply = null;
	
    /**
     * Constructs an AddressableEvent with a null Path.
     *
     * @param    source    The object on which the event initially occurred.
     */
	public AddressableEvent(Object source)
	{
		this(source, null, null);
	}

	/**
	 * Construct an AddressableEvent with the specified destination.
	 *
	 * @param source	The object on which the event initially occurred.
	 * @param destination	The destination path for the event
	 */
	public AddressableEvent(Object source, Path destination)
	{
		this(source, destination, null);
	}
	
	/**
	 * Construct an AddressableEvent with the specified destination and
	 * reply to specification.
	 *
	 * @param source	The object on which the event initially occurred.
	 * @param destination	The destination path for the event
	 * @param reply	The reply to path for the event
	 */
	public AddressableEvent(Object source, Path destination, Path reply)
	{
		super(source);
		fDestination = destination;
		fReply = reply;
	}	

	/**
	 * Returns the destination path for this event. 
	 * 
	 * @return the destination for this event
	 */
	public Path getDestination()
	{
		return fDestination;
	}

	/**
	 * Returns the reply to path for this event. 
	 * 
	 * @return the reply to path for this event
	 */
	public Path getReplyTo()
	{
		return fReply;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AddressableEvent.java,v $
//  Revision 1.2  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.1  2006/04/18 03:51:18  tames
//  Relocated or new implementation.
//
//  Revision 1.3  2006/03/31 16:25:06  smaher_cvs
//  Added timestamp ability.
//
//  Revision 1.2  2005/04/08 20:58:33  tames_cvs
//  Added a protected setPath method.
//
//  Revision 1.1  2005/02/04 21:48:22  tames_cvs
//  Initial version
//
//