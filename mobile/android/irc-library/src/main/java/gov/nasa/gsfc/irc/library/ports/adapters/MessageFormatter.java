//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/library/gov/nasa/gsfc/irc/library/ports/adapters/MessageFormatter.java,v 1.6 2006/04/18 04:24:23 tames Exp $
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

package gov.nasa.gsfc.irc.library.ports.adapters;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.commons.types.arrays.BitArray;
import gov.nasa.gsfc.irc.data.transformation.DataTransformer;
import gov.nasa.gsfc.irc.devices.ports.adapters.AbstractMessageOutputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapter;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputAdapterDescriptor;
import gov.nasa.gsfc.irc.devices.ports.adapters.OutputException;


/**
 *  A MessageFormatter class is an OutputAdapter that tranforms 
 *  output Messages into ByteBuffers according to a serialization and formatting 
 *  process described by an associated Descriptor.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/04/18 04:24:23 $
 *  @author Carl F. Hostetter
 */
 
public class MessageFormatter extends AbstractMessageOutputAdapter 
	implements OutputAdapter
{
	private static final String CLASS_NAME = 
		MessageFormatter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Message Output Adapter";
	
	
	/**
	 *  Constructs a new MessageFormatter having a default name.
	 * 
	 **/

	public MessageFormatter()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new MessageFormatter having the given name.
	 *  
	 *  @param name The name of the new MessageFormatter
	 **/

	public MessageFormatter(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new MessageFormatter configured according to the given 
	 *  OutputAdapterDescriptor.
	 *
	 *  @param descriptor The OutputAdapterDescriptor of the new MessageFormatter
	 */
	
	public MessageFormatter(OutputAdapterDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 *  Applies the output Message to ByteBuffer transformation described by the 
	 *  DataTransformationDescriptor associated with this 
	 *  MessageFormatter to the given output Message and returns 
	 *  the result as a ByteBuffer.
	 *
	 * @param message The output Message to be transformed into a ByteBuffer
	 * @return The ByteBuffer result of the transformation
	 * @throws OutputException
	 */
	 
	public ByteBuffer process(Message message) throws OutputException
	{
		if (sLogger.isLoggable(Level.FINE))
		{
			String info = getFullyQualifiedName() + " processing Message:\n" + 
				message;
			
			sLogger.logp(Level.FINE, CLASS_NAME, "process", info);
		}
		
		ByteBuffer result = null;
		
		DataTransformer dataTransformer = getDataTransformer();
		
		if (dataTransformer != null)
		{
			Object formattedMessage = 
				dataTransformer.transform(message, null);
			
			if (formattedMessage != null)
			{
				byte[] resultBytes = null;
				
				if (formattedMessage instanceof BitArray)
				{
					resultBytes = ((BitArray) formattedMessage).getBytes();
				}
				else
				{
					resultBytes = formattedMessage.toString().getBytes();
				}
				
				result = ByteBuffer.wrap(resultBytes);
			}
		}
		
		if (result == null)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String info = getFullyQualifiedName() + 
					" data transformation failed:\n" + message;
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, "process", info);
			}
		}
		
		return (result);
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: MessageFormatter.java,v $
//  Revision 1.6  2006/04/18 04:24:23  tames
//  Changed to reflect refactored Message related classes.
//
//  Revision 1.5  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.4  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2005/10/25 21:38:42  chostetter_cvs
//  Fixed binary data formatting
//
//  Revision 1.2  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.6  2005/04/06 14:59:46  chostetter_cvs
//  Adjusted logging levels
//
//  Revision 1.5  2005/01/11 22:12:21  tames
//  Removed bufferflip on result.
//
//  Revision 1.4  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.3  2004/12/13 04:14:32  chostetter_cvs
//  Message formatting: text and (initial form) binary
//
//  Revision 1.2  2004/11/15 21:09:14  chostetter_cvs
//  Flips resulting ByteBuffer for stream processing
//
//  Revision 1.1  2004/11/10 23:09:56  chostetter_cvs
//  Initial debugging of Message formatting. Mostly works except for C-style formatting.
//
//
