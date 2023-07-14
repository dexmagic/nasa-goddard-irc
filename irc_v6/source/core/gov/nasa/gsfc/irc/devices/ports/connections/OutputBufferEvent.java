// === File Prolog ============================================================
//
// $Header:
// /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/connections/OutputBufferEvent.java,v
// 1.3 2005/02/04 21:46:59 tames_cvs Exp $
//
// This code was developed by NASA, Goddard Space Flight Center, Code 580
// for the Instrument Remote Control (IRC) project.
//
// --- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
// --- Warning ----------------------------------------------------------------
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// * Neither the author, their corporation, nor NASA is responsible for
// any consequence of the use of this software.
// * The origin of this software must not be misrepresented either by
// explicit claim or by omission.
// * Altered versions of this software must be plainly marked as such.
// * This notice may not be removed or altered.
//
// === End File Prolog ========================================================

package gov.nasa.gsfc.irc.devices.ports.connections;

import java.nio.ByteBuffer;
import java.util.EventObject;

import gov.nasa.gsfc.commons.publishing.paths.Path;

/**
 * OutputBufferEvent is used to notify interested parties that a source
 * OutputAdapter has created output data. The send context Path for this event
 * should be set from an associated OutputMessageEvent if one exists.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/18 14:02:49 $
 * @author T. Ames
 */
public class OutputBufferEvent extends EventObject
{
	private ByteBuffer fData;
	private Path fSendContext = null;

	/**
	 * Creates a new OutputBufferEvent with the specified data. The path is set
	 * to null.
	 * 
	 * @param source The object on which the Event initially occurred.
	 * @param data The data received by the source.
	 */
	public OutputBufferEvent(Object source, ByteBuffer data)
	{
		this(source, data, null);
	}

	/**
	 * Creates a new OutputBufferEvent with the specified data and context.
	 * 
	 * @param source The object on which the Event initially occurred.
	 * @param data The data received by the source.
	 * @param path The send context for this event
	 */
	public OutputBufferEvent(Object source, ByteBuffer data, Path sendContext)
	{
		super(source);
		fData = data;
		fSendContext = sendContext;
	}

	/**
	 * Returns the send context for this event.
	 * 
	 * @return the send context for this event
	 */
	public Path getSendContext()
	{
		return fSendContext;
	}

	/**
	 * Gets the formatted data.
	 * 
	 * @return the data contained in this Event
	 */
	public ByteBuffer getData()
	{
		return fData;
	}
}

// --- Development History ---------------------------------------------------
//
// $Log: OutputBufferEvent.java,v $
// Revision 1.5  2006/04/18 14:02:49  tames
// Reflects relocated Path and DefaultPath.
//
// Revision 1.4  2006/04/18 04:11:09  tames
// Changed to reflect relocated Path related classes.
//
// Revision 1.3 2005/02/04 21:46:59 tames_cvs
// Added constructors to allow specifying a path.
//
// Revision 1.2 2005/01/11 21:35:46 chostetter_cvs
// Initial version
//
// Revision 1.1 2004/10/14 15:16:51 chostetter_cvs
// Extensive descriptor-oriented refactoring
//
// Revision 1.1 2004/09/27 20:33:45 tames
// Reflects a refactoring of Connection input and output events.
//
// Revision 1.1 2004/07/27 21:11:34 tames_cvs
// Port redesign implementation
//
