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

package gov.nasa.gsfc.irc.logging;

import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import gov.nasa.gsfc.commons.publishing.messages.Message;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.messages.InvalidMessageException;
import gov.nasa.gsfc.irc.messages.Messages;
import gov.nasa.gsfc.irc.messages.UnknownMessageException;

/**
 * A <tt>MessageHandler</tt> object takes log messages from a <tt>Logger</tt> and
 * exports them as a {@link gov.nasa.gsfc.commons.publishing.messages.Message}.  
 * <p>
 * A <tt>Handler</tt> can be disabled by doing a <tt>setLevel(Level.OFF)</tt>
 * and can  be re-enabled by doing a <tt>setLevel</tt> with an appropriate level.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/04/18 04:14:46 $
 * @author 	Troy Ames
 */
public class MessageHandler extends Handler
{
	private static final String LEVEL_KEY = "level";
	private static final String MESSAGE_KEY = "message";
	private static final String TIME_KEY = "time";
	private static final String RECORD_KEY = "record";
	private static final String SOURCE_KEY = "source";
	
	private static final String MESSAGE_NAME_PROPERTY =
		"gov.nasa.gsfc.irc.logging.MessageHandler.message";
	
	private boolean fClosed = false;
	private String fMessageName = null;
	
	/**
     * Default constructor.  The resulting <tt>MessageHandler</tt> has a log
     * level of <tt>Level.ALL</tt>, no <tt>Formatter</tt>, and no 
     * <tt>Filter</tt>.  A default <tt>ErrorManager</tt> instance is installed
     * as the <tt>ErrorManager</tt>.
	 */
	public MessageHandler()
	{
		super();
		
		fMessageName = 
			LogManager.getLogManager().getProperty(MESSAGE_NAME_PROPERTY);
		
		if (fMessageName == null)
		{
			// Since the log properties did not have it check preferences.
			fMessageName = Irc.getPreference(MESSAGE_NAME_PROPERTY);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	public void close() throws SecurityException
	{
		fClosed = true;
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	public void flush()
	{
		// Nothing to do here.
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	public void publish(LogRecord record)
	{
		// If closed silently ignore the publish method
		if (fClosed)
		{
			return;
		}
		
		Message message = null;
		
		try
		{
			if (fMessageName != null)
			{
				message = Messages.createMessage(fMessageName);
			}
			else
			{
				message = Messages.createMessage();
			}
			
			message.put(SOURCE_KEY, record.getSourceClassName());
			message.put(LEVEL_KEY, record.getLevel());
			message.put(TIME_KEY, new Date(record.getMillis()));
			message.put(MESSAGE_KEY, record.getMessage());
			message.put(RECORD_KEY, record);
			
			Messages.publishMessage(message);
		}
		catch (UnknownMessageException e)
		{
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

    /* (non-Javadoc)
     * @see java.util.logging.Handler#setLevel(java.util.logging.Level)
     */
    public synchronized void setLevel(Level newLevel) throws SecurityException 
    {
    	System.out.println("*********** setLevel:" + newLevel);
    	super.setLevel(newLevel);
    }

	/**
	 * Get the name of the message used for publishing log events.
	 * 
	 * @return Returns the messageName.
	 */
	public String getMessageName()
	{
		return fMessageName;
	}

	/**
	 * Set the message to use for publishing log events.
	 * 
	 * @param messageName The messageName to set.
	 */
	public void setMessageName(String messageName)
	{
		fMessageName = messageName;
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: MessageHandler.java,v $
//  Revision 1.2  2006/04/18 04:14:46  tames
//  Added the capability to specify MessageHandler properties in the plist.
//
//  Revision 1.1  2006/02/01 23:00:26  tames
//  Initial version.
//
//