//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ConsoleInterpreter.java,v $
//  Revision 1.17  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.16  2005/04/12 15:36:06  tames_cvs
//  Updated catch clause for ScriptExceptions.
//
//  Revision 1.15  2005/02/01 18:44:30  tames
//  Changes to reflect DescriptorLibrary changes.
//
//  Revision 1.14  2005/01/10 23:08:00  tames_cvs
//  Updated to reflect GuiBuilder name change to GuiFactory.
//
//  Revision 1.13  2004/08/29 19:09:29  tames
//  Added trim() to input
//
//  Revision 1.12  2004/08/26 14:35:01  tames
//  *** empty log message ***
//
//  Revision 1.11  2004/08/23 13:56:56  tames
//  Added support for exception handling when rendering the gui.
//
//  Revision 1.10  2004/08/12 03:19:23  tames
//  Script support
//
//  Revision 1.9  2004/08/09 17:28:55  tames_cvs
//  added message test command
//
//  Revision 1.8  2004/08/03 20:34:54  tames_cvs
//  Added start command
//
//  Revision 1.7  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.6  2004/07/10 02:02:07  chostetter_cvs
//  Organized imports
//
//  Revision 1.5  2004/07/10 00:15:56  tames_cvs
//  Added commands to read in GUI and Instrument descriptions.
//
//  Revision 1.4  2004/06/30 20:45:49  tames_cvs
//  Changed text of info message
//
//  Revision 1.3  2004/06/01 18:51:31  tames_cvs
//  Added threads command
//
//  Revision 1.2  2004/05/29 03:07:35  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/05/28 22:04:50  tames_cvs
//  Initial version
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
package gov.nasa.gsfc.irc.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

// FIXME: these imports create a circular dependency. Combine modules?
import gov.nasa.gsfc.commons.system.Sys;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.messages.MessageException;
import gov.nasa.gsfc.irc.messages.Messages;
import gov.nasa.gsfc.irc.scripts.ScriptException;
import gov.nasa.gsfc.irc.scripts.Scripts;

/**
 * This class provides a simple user interface by interpreting commands
 * entered in the console. The following commands are supported:
 *
 *  <P>
 *  <center><table border="1">
 *  <tr align="center">
 *	  <th>Command</th>
 *	  <th>Description</th>
 *  </tr>
 *  <tr align="center">
 *	  <td>threads</td>
 *	  <td align="left">Dumps a list of current threads to System out by 
 * 			calling the  
 * 			{@link gov.nasa.gsfc.commons.app.App@dumpThreads} method.
 * 		</td>
 *  </tr>
 *  <tr align="center">
 *	  <td>quit</td>
 *	  <td align="left">Quits the application by calling the 
 * 			{@link gov.nasa.gsfc.irc.app.Irc@shutdown shutdown} method.
 * 		</td>
 *  </tr>
 *  <tr align="center">
 *	  <td>exit</td>
 *	  <td align="left">Same as quit</td>
 *  </tr>
 *  </table>
 *  </center>
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/01/23 17:59:55 $
 * @author 	Troy Ames
**/
public class ConsoleInterpreter implements Runnable
{
	private static final String CLASS_NAME = ConsoleInterpreter.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	/** Start command */
	public static final String START_CMD = "start";

	/** Threads command */
	public static final String THREADS_CMD = "threads";

	/** Load GUI description command */
	public static final String LOAD_GUI_CMD = "loadgui";

	/** Load external device description command */
	public static final String LOAD_DEVICE_CMD = "loaddev";

	/** Message command */
	public static final String MESSAGE_CMD = "message";

	/** Script command */
	public static final String SCRIPT_CMD = "script";

	/** Exit command */
	public static final String EXIT_CMD = "exit";

	/** Quit command */
	public static final String QUIT_CMD = "quit";
	
	/** Help command */
	public static final String HELP_CMD = "help";
	
	// Help message
	private static final String CMD_INDENT = "  ";
	private static final String DES_INDENT = "\n" + CMD_INDENT + CMD_INDENT;
	private static final String HELP_MESSAGE = 
		"Commands:\n"
		+ CMD_INDENT + START_CMD + DES_INDENT + "- starts all components\n"
		+ CMD_INDENT + LOAD_DEVICE_CMD + " <filename>" 
		+ DES_INDENT + "- loads the specified external device description file\n"
		+ CMD_INDENT + LOAD_GUI_CMD + " <filename>" 
		+ DES_INDENT + "- loads the specified GUI description file\n"
		+ CMD_INDENT + THREADS_CMD + DES_INDENT + "- prints info on active Threads\n"
		+ CMD_INDENT + HELP_CMD + DES_INDENT + "- Prints this message\n"
		+ CMD_INDENT + QUIT_CMD + DES_INDENT + "- quits the application\n"
		+ CMD_INDENT + EXIT_CMD + DES_INDENT + "- same as " + QUIT_CMD + "\n";

	/**
	 * Waits for and interprets user input in the console window.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		try
		{
			BufferedReader buffer =
				new BufferedReader(new InputStreamReader(System.in));
			
			boolean quitRequested = false;

		   //---Wait for user input to quit
			while (!quitRequested)
			{
				//---Blocks until something is typed on the command line
				String line = buffer.readLine().trim();
				if (line == null)
				{
					/*
					End of input stream has been reached. IRC may be
					running in an environment that does not support
					System.in
					*/
					Thread.sleep(1000);
				}
				else if (line.equalsIgnoreCase(EXIT_CMD)
						|| line.equalsIgnoreCase(QUIT_CMD))
				{
					// Quit requested
					quitRequested = true;
				}
				else if (line.equalsIgnoreCase(THREADS_CMD))
				{
					// Threads command
					Irc.dumpThreads();
				}
				else if (line.equalsIgnoreCase(START_CMD))
				{
					// start command
					Irc.getComponentManager().startAllComponents();
				}
				else if (line.startsWith(MESSAGE_CMD))
				{
					// interpret message command
					handleMessage(line);
				}
				else if (line.startsWith(SCRIPT_CMD))
				{
					// interpret script command
					handleScript(line);
				}
				else if (line.startsWith(LOAD_DEVICE_CMD))
				{
					// Load device command
					handleLoadDevice(line);
				}
				else if (line.startsWith(LOAD_GUI_CMD))
				{
					// Load GUI command
					handleLoadGui(line);
				}
				else if (line.startsWith(HELP_CMD))
				{
					// help command
					System.out.println(HELP_MESSAGE);
				}
				else
				{
					// Unknown input command
					String message = 
						"Unknown command:" + line 
						+ "\n Type help for commands";

					//sLogger.logp(Level.WARNING, CLASS_NAME, "run", message);
					System.err.println(message);
				}
				
				Thread.yield();
			}
			
			Irc.shutdown();
		}
		catch (IOException e)
		{
			String message = "Exception reading from System.in";
			sLogger.logp(Level.SEVERE, CLASS_NAME, "run", message, e);
		}
		catch (InterruptedException e)
		{
			String message = "Exception reading from System.in";
			sLogger.logp(Level.SEVERE, CLASS_NAME, "run", message, e);
		}
	}
	
	/**
	 * Interpret a script command by running the specified script
	 * 
	 * @param line input command and parameters
	 */
	protected void handleScript(String line)
	{
		// Handle script command
		String[] elements = line.split("\\s");
		
		if (elements.length > 1)
		{
			String scriptName = elements[1];

			try
			{
				// Execute script
				Scripts.callScript(scriptName.trim());
			}
			catch (ScriptException e)
			{
				String message = 
					"Error calling script:" + line 
					+ "\n" + e.getLocalizedMessage();
					
				//sLogger.logp(Level.WARNING, CLASS_NAME, "run", message);
				System.err.println(message);						
			}
		}
		else
		{
			String message = 
				"Invalid parameter:" + line + "\nExpecting:\n"
				+ CMD_INDENT + SCRIPT_CMD + " <script name>";
				
			//sLogger.logp(Level.WARNING, CLASS_NAME, "run", message);
			System.err.println(message);						
		}		
	}

	/**
	 * 
	 * @param line input command and parameters
	 */
	protected void handleMessage(String line)
	{
		// Handle message command
		String[] elements = line.split("\\s");
		
		if (elements.length > 2)
		{
//			String destination = elements[1];
			String messageName = elements[2];

			try
			{
				// Send message
				Messages.publishMessage(messageName);
			}
			catch (MessageException e)
			{
				String message = 
					"Invalid message:" + line + "\n" + e.getLocalizedMessage();
					
				//sLogger.logp(Level.WARNING, CLASS_NAME, "run", message);
				System.err.println(message);						
			}
		}
		else
		{
			String message = 
				"Invalid parameter:" + line + "\nExpecting:\n"
				+ CMD_INDENT + MESSAGE_CMD + " <destination>" 
				+ " <message name>";
				
			//sLogger.logp(Level.WARNING, CLASS_NAME, "run", message);
			System.err.println(message);						
		}		
	}

	/**
	 * Loads an XML description file for a device. The first 
	 * parameter after the command is a path and file name relative to 
	 * the class path. Example: <code>/resources/xml/fibre.xml</code>
	 * 
	 * @param line input command and parameters
	 */
	protected void handleLoadDevice(String line)
	{
		// Load device command
		String[] elements = line.split("\\s");
		
		if (elements.length > 1)
		{
			URL url = Sys.getResourceManager().getResource(elements[1]);

			if (url != null)
			{
				Irc.getIrcManager().addExternalDevice(url);
			}
			else
			{
				System.out.println("Could not find file:" + elements[1]);
			}
		}
		else
		{
			String message = 
				"Invalid parameter:" + line + "\nExpecting:\n"
				+ CMD_INDENT + LOAD_DEVICE_CMD + " <filename>";
				
			//sLogger.logp(Level.WARNING, CLASS_NAME, "run", message);
			System.err.println(message);						
		}		
	}

	/**
	 * Loads an XML description file and instantiates the GUI. The first 
	 * parameter after the command is a path and file name relative to 
	 * the class path. Example: <code>/resources/xml/defaults/gui/gui.xml</code>
	 * 
	 * @param line input command and parameters
	 */
	protected void handleLoadGui(String line)
	{
		System.out.println("GUI not supported on Android");
	}
}
