//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.irc.library.ports.connections;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;


/**
 * Connection to read lines of text from a file.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:15 $
 * @author	 smaher
 */
public class TextFileInputConnection extends TextLineInputConnection 
	implements Connection
{
	private static final String CLASS_NAME = TextFileInputConnection.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	public static final String DEFAULT_NAME = "Text File Input Connection";
	
	// Parameter Keys
	public final static String FILE_NAME_KEY = "filename";
	
	private ConnectionDescriptor fDescriptor;
	
	// Name of file
	private String fFileName;
	

	/**
	 *	Constructs a new TextFileInputConnection having a default name.
	 *
	 */
	
	public TextFileInputConnection()
	{
		this(DEFAULT_NAME);
	}
	

	/**
	 *	Constructs a new TextFileInputConnection having the given base name.
	 *  
	 *  @param name The base name of the new TextFileInputConnection
	 */
	
	public TextFileInputConnection(String name)
	{
		super(name);
	}
	

	/**
	 * Constructs a new TextFileInputConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new TextFileInputConnection
	 */
	public TextFileInputConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);

		fDescriptor = descriptor;
		
		configureFromDescriptor(descriptor);
	}
	
	
	public void setDescriptor(Descriptor descriptor)
	{
	    super.setDescriptor(descriptor);
	    
		if (descriptor instanceof ConnectionDescriptor)
		{
			configureFromDescriptor((ConnectionDescriptor) descriptor);
		}
	}	
	
	
	/**
	 * Causes this TextFileInConnection to (re)configure itself in accordance 
	 * with the descriptor.
	 *  
	 * @param descriptor a ConnectionDescriptor
	 */
	
	private void configureFromDescriptor(ConnectionDescriptor descriptor)
	{
		if (descriptor == null)
		{
			return;
		}
		
		// Set FileName
		setFileName(descriptor.getParameter(FILE_NAME_KEY));
	}


	/**
	 * Returns the name of the text file to be read.
	 * 
	 * @return The name of the text file to be read
	 */
	
	public String getFileName()
	{
		return (fFileName);
	}
	

	/**
	 * Sets the name of the text file to be read to the given name.
	 * 
	 * @param filename The name of the text file to be read
	 */
	
	public void setFileName(String filename)
	{
        String oldValue = fFileName;
        fFileName = filename;
        firePropertyChange(FILE_NAME_KEY, oldValue, fFileName);
        
        try
        {
        		InputStream inputStream = new FileInputStream(fFileName);
        		
        		setInputStream(inputStream);
        }
        catch (IOException ex)
        {
        		String message = "Unable to open file " + fFileName + " due to ";
        		
        		if (sLogger.isLoggable(Level.SEVERE))
        		{
        			sLogger.logp(Level.SEVERE, CLASS_NAME, "setFileName", message, ex);
        		}
        		
        		throw (new IllegalArgumentException(message));
        }
    }


	/**
	 *  Returns a String representation of this Connection.
	 * 
	 *  @return A String representation of this Connection
	 */
	
	public String toString() 
	{
		StringBuffer stringRep = new StringBuffer(super.toString());

		stringRep.append("\nFile Name = " + fFileName);

		return (stringRep.toString());
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: TextFileInputConnection.java,v $
//  Revision 1.3  2006/03/14 14:57:15  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.2  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//
