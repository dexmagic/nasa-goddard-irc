//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import gov.nasa.gsfc.commons.types.buffers.BufferHandle;
import gov.nasa.gsfc.commons.types.buffers.DefaultBufferHandle;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.AbstractThreadedConnection;
import gov.nasa.gsfc.irc.devices.ports.connections.Connection;
import gov.nasa.gsfc.irc.devices.ports.connections.ConnectionDescriptor;
import gov.nasa.gsfc.irc.devices.ports.connections.InputBufferEvent;


/**
 * A SerialConnection is a connection component that connects to and 
 * reads data from a serial connection using the comm API. This class manages one 
 * Thread that reads data from a connection. Writing to 
 * a connection is done by the calling thread of the <code>write</code>
 * method. The packet size of each read attempt is 
 * determined by the packet size property.
 *
 * <P>The configuration of this connection is specified by a ConnectionDescriptor.
 * The table below gives the configuration parameters that this connection uses.
 * If the parameter is missing then the default value will be used. If there
 * is not a default value specified then the parameter is required to be
 * in the descriptor except where noted. 
 *
 *  <P>
 *  <center><table border="1">
 *  <tr align="center">
 *      <th>Key</th>
 *      <th>Default</th>
 *      <th>Description</th>
 *  </tr>
 *  <tr align="center">
 *      <td>deviceName</td><td>-</td>
 *      <td align="left">The logical name for the port such as "COM1" or
 * 				"/dev/ttyS0".</td>
 *  </tr>
 *  <tr align="center">
 *      <td>baudRate</td><td>9600</td>
 *      <td align="left">The flow rate of the port.</td>
 *  </tr>
 *  <tr align="center">
 *      <td>dataBits</td><td>8</td>
 *      <td align="left">Data bit format. Options are "5", "6", "7", or "8".</td>
 *  </tr>
 *  <tr align="center">
 *      <td>stopBits</td><td>1</td>
 *      <td align="left">Number of STOP bits. Options are "1", "1.5", or "2".</td>
 *  </tr>
 *  <tr align="center">
 *      <td>parity</td><td>none</td>
 *      <td align="left">Parity scheme. Options are "none", "odd", "even",
 * 				"mark", and "space".</td>
 *  </tr>
 *  <tr align="center">
 *      <td>inputFlowControl</td><td>none</td>
 *      <td align="left">Flow control either "hardware", "software", or "none".</td>
 *  </tr>
 *  <tr align="center">
 *      <td>outputFlowControl</td><td>none</td>
 *      <td align="left">Flow control either "hardware", "software", or "none".</td>
 *  </tr>
 *  <tr align="center">
 *      <td>packetSize</td><td>1024</td>
 *      <td align="left">The default packet size to use for 
 * 			receiving data</td>
 *  </tr>
 *  </table>
 *  </center>
 *
 *  <P>A partial example IML port description for this type of Connection:
 *  <BR>
 *  <pre>
 *     &lt;Connection name="Test Connection" type="Serial"&gt;
 *         &lt;Parameter name="deviceName" value="COM1" /&gt;
 *         &lt;Parameter name="baudRate" value="9600" /&gt;
 *         &lt;Parameter name="stopBits" value="2" /&gt;
 *         ...
 *     &lt;/Connection&gt;
 *  </pre>
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/05/01 13:37:58 $
 * @author	    Troy Ames
 */
public class SerialConnection extends AbstractThreadedConnection 
	implements Connection
{
	private static final String CLASS_NAME = SerialConnection.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	public static final String DEFAULT_NAME = "Serial Connection";

	/** The key for getting the packet size to use from a Descriptor. */
	public static final String PACKET_SIZE_KEY = "packetSize";

	/**
	 *	SerialCommunicationPort constants for the various port parameters
	 */
	public static final String FLOWCONTROL_NONE		= "none";
	public static final String FLOWCONTROL_HARDWARE	= "hardware";
	public static final String FLOWCONTROL_SOFTWARE	= "software";

	public static final String DATABITS_5	= "5";
	public static final String DATABITS_6	= "6";
	public static final String DATABITS_7	= "7";
	public static final String DATABITS_8	= "8";

	public static final String STOPBITS_1	= "1";
	public static final String STOPBITS_1_5	= "1.5";
	public static final String STOPBITS_2	= "2";

	public static final String PARITY_NONE	= "none";
	public static final String PARITY_ODD	= "odd";
	public static final String PARITY_EVEN	= "even";
	public static final String PARITY_MARK	= "mark";
	public static final String PARITY_SPACE	= "space";
	
	//Serial paramaters
	/**
	 *	The name of the CommPortIdentifier of the SerialPort to be managed
	 *	and communicated with by this SerialIoPort.
	 */
	private String fDeviceName = null;

	/**
	 *	The CommPortIdentifier of the the SerialPort to be managed and
	 *	communicated with by this SerialIoPort.
	 */
	private CommPortIdentifier fCommPortIdentifier = null;

	/** The SerialPort managed and communicated with by this SerialIoPort. */
	private SerialPort fSerialPort = null;

	/** The baud rate setting for the SerialPort. */
	private int fBaudRate = 9600;
	
	/** The data rate setting for the SerialPort. */
	private int fDataBits = SerialPort.DATABITS_8;
	private String fDataBitsString = DATABITS_8;
	
	/** The stop bits setting for the SerialPort. */
	private int fStopBits = SerialPort.STOPBITS_1;
	private String fStopBitsString = STOPBITS_1;
	
	/** The parity setting for the SerialPort. */
	private int fParity = SerialPort.PARITY_NONE;
	private String fParityString = PARITY_NONE;

	/** The input flow control bitmask of the SerialPort. */
	private int fInputFlowControl = SerialPort.FLOWCONTROL_NONE;
	private String fInputFlowControlString = FLOWCONTROL_NONE;

	/** The output flow control bitmask of the SerialPort. */
	private int fOutputFlowControl = SerialPort.FLOWCONTROL_NONE;
	private String fOutputFlowControlString = FLOWCONTROL_NONE;

	private int fPacketSize = 1024;

	private ReadableByteChannel fInputChannel;
	private WritableByteChannel fOutputChannel;

	// Cached receive buffer and handle
	private ByteBuffer fReceiveBuffer;
	private BufferHandle fReceiveBufferHandle;
	private boolean fPacketSizeChange = false;

	
	/**
	 *	Constructs a new SerialConnection having a default name.
	 *
	 */
	
	public SerialConnection()
	{
		this(DEFAULT_NAME);
	}
	

	/**
	 *	Constructs a new SerialConnection having the given base name.
	 *  
	 *  @param name The base name of the new SerialConnection
	 */
	
	public SerialConnection(String name)
	{
		super(name);
	}
	

	/**
	 * Constructs a new SerialConnection configured according to the given 
	 * Descriptor.
	 *
	 * @param descriptor The Descriptor of the new SerialConnection
	 */
	public SerialConnection(ConnectionDescriptor descriptor)
	{
		super(descriptor);

		configureFromDescriptor(descriptor);
	}
	

	/**
	 *  Sets the Descriptor of this Component to the given Descriptor. The 
	 *  Component will in turn be (re)configured in accordance with the given 
	 *  Descriptor.
	 *  
	 *  @param descriptor A Descriptor
	**/
	public void setDescriptor(Descriptor descriptor)
	{
	    super.setDescriptor(descriptor);
	    
		if (descriptor instanceof ConnectionDescriptor)
		{
			boolean wasStarted = isStarted();
			
			if (wasStarted)
			{
				stop();
			}
			
			configureFromDescriptor((ConnectionDescriptor) descriptor);
			
			if (wasStarted)
			{
				start();
			}
		}
	}	

	/**
	 * Causes this connection to (re)configure itself in accordance 
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
		
		fDeviceName = descriptor.getParameter("deviceName");
		
		String parmVal = descriptor.getParameter("baudRate");		
		if (parmVal != null)
		{
			try
			{
				setBaudRate(Integer.parseInt(parmVal));
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build SerialConnection with invalid number "
					+ parmVal;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}

		parmVal = descriptor.getParameter("dataBits");
		if (parmVal != null)
		{
			setDataBits(parmVal);
		}

		parmVal = descriptor.getParameter("stopBits");
		if (parmVal != null)
		{
			setStopBits(parmVal);
		}
		
		parmVal = descriptor.getParameter("parity");
		if (parmVal != null)
		{
			setParity(parmVal);
		}
		
		parmVal = descriptor.getParameter("inputFlowControl");
		if (parmVal != null)
		{
			setInputFlowControl(parmVal);
		}
		
		parmVal = descriptor.getParameter("outputFlowControl");
		if (parmVal != null)
		{
			setOutputFlowControl(parmVal);
		}
		
		parmVal = descriptor.getParameter(PACKET_SIZE_KEY);		
		if (parmVal != null)
		{
			try
			{
				setPacketSize(Integer.parseInt(parmVal));
				
			}
			catch (NumberFormatException e)
			{
				String message = 
					"Attempt to build SerialConnection with invalid number "
					+ parmVal;
	
				sLogger.logp(Level.WARNING, CLASS_NAME, 
						"configureFromDescriptor", message, e);
			}
		}
	}

	/**
	 * Writes the data to the given channel. This method will not return until 
	 * all the data from the buffer is written to the channel.
	 * 
	 * @param buffer	the data to write
	 * @param channel	the channel to write to
	 * @throws IOException if writing to the channel results in an exception
	 */
	protected void writeDataToChannel(
			ByteBuffer buffer, WritableByteChannel channel) throws IOException
	{
		try
		{
			int bytesToWrite = buffer.remaining();
			int bytesWritten = 0;
			
			//---Loop while data is available
			while (bytesWritten < bytesToWrite)
			{
				int bytesThisWrite = channel.write(buffer);
				bytesWritten += bytesThisWrite;
				
				if (bytesThisWrite == 0)
				{
					// Give the receiver time to catch up and reduce the
					// risk of saturating the local processor.
					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
						// Nothing to do here
					}
				}
			}
		}
		catch (IOException ex)
		{
			String message = getFullyQualifiedName()
					+ " exception writing to connection " + channel.toString();
			sLogger.logp(Level.WARNING, CLASS_NAME, "writeDataToChannel",
					message, ex);
			
			// Rethrow exception so caller can handle it
			throw ex;
		}
	}

	/**
	 * Writes the contents of the Buffer to the connection(s).
	 *
	 * @param buffer ByteBuffer to write to the connection
	 * @throws IOException if the write fails.
	 */
	public synchronized void process(ByteBuffer buffer)
	{
		if (!isStarted())
		{
    		sLogger.logp(
    			Level.WARNING, CLASS_NAME, "process", 
				getName()
				+ " connection could not process buffer because "
				+ " connection is " + getState());
			return;
		}
		
		try
		{
			if (fOutputChannel != null)
			{
				writeDataToChannel(buffer, fOutputChannel);
			}
			else
			{
				// TODO should we queue the data?
			}			
		}
		catch (IOException ex)
		{
			try
			{
				fOutputChannel.close();
			}
			catch (IOException e)
			{
				// Since we are closing the socket anyway there is 
				// nothing else to do here.
			}
			
			fOutputChannel = null;

			stop();
		}
	}


	/**
	 * Configures and opens a serial connection based on the current 
	 * configuration.
	 */
	protected void openConnection()
	{
		fReceiveBuffer = ByteBuffer.allocate(fPacketSize);
		fReceiveBufferHandle = new DefaultBufferHandle(fReceiveBuffer);

		try
		{
			fCommPortIdentifier = 
				CommPortIdentifier.getPortIdentifier(fDeviceName);
			
			fSerialPort = (SerialPort) fCommPortIdentifier.open(getName(), 10000);

			// From the API for SerialPort.setSerialPortParams:
			//		"If any of the above parameters
			//		are specified incorrectly all four of the parameters will revert
			//		to the values before the call was made.
			//		DEFAULT: 9600 baud, 8 data bits, 1 stop bit, no parity"
			// TBD - what does that mean when it can't open the port?
			
			
			// When using javacomm3.0 (Linux support), calling
			// setSerialPortParams apparently can generate
			// "java.io.IOException: Not all params are supported by kernel"
			// the first time.  Therefore, call it twice here, catching an 
			// IOException the first time.
			//
			// This can be demonstrated using the "BlackBox" demo included in 
			// the javacomm distro and selecting a parity after starting the program.
			//
			// Some discussion here: http://forum.java.sun.com/thread.jspa?threadID=673793&tstart=60
			//

			try {
				fSerialPort.setSerialPortParams(fBaudRate, fDataBits,
						fStopBits, fParity);
			} catch (Exception e) {
				// 
				String message = "Caught IOException calling setSerialPortParams on serial port - "
					+ " trying again since this may be known(?) strange error on javacomm3.0";
				sLogger.logp(Level.INFO, CLASS_NAME, "start", message, e);						
			}
			
			fSerialPort.setSerialPortParams( 
					fBaudRate, fDataBits, fStopBits, fParity );					

			// Input/Output flow control are bit masks - or together and then set
			int flowControl = fInputFlowControl | fOutputFlowControl;
			fSerialPort.setFlowControlMode(flowControl);

			// Serial port is opened create channels
		    fInputChannel = Channels.newChannel(fSerialPort.getInputStream());
		    fOutputChannel = Channels.newChannel(fSerialPort.getOutputStream());
		    publishConnectionAdded(fCommPortIdentifier);
		}
		catch(NoSuchPortException e)
		{
			String message = "No such port : " + fDeviceName;
			sLogger.logp(Level.WARNING, CLASS_NAME, "start", message, e);

			// Dump the available ports
			Enumeration commPortIdentifiers = CommPortIdentifier.getPortIdentifiers();
			
			if( commPortIdentifiers.hasMoreElements() )
			{
				message = "Available CommPortIdentifiers:";
				while( commPortIdentifiers.hasMoreElements() )
				{
					CommPortIdentifier commPortIdentifier =
							(CommPortIdentifier)commPortIdentifiers.nextElement();
					message += " " + commPortIdentifier.getName();
				}
			}
			else
			{
				message = "No CommPortIdentifiers exist. Make sure the " +
						"javax.comm classes and related files are properly installed.";
			}

			sLogger.logp(Level.WARNING, CLASS_NAME, "start", message);
		}
		catch(IOException e)
		{
			String message = "Could not get Streams";
			sLogger.logp(Level.WARNING, CLASS_NAME, "start", message, e);
		}
		catch(UnsupportedCommOperationException  e)
		{
			String message = "Could not set port " +
						"params - setting to default.";
			sLogger.logp(Level.WARNING, CLASS_NAME, "start", message, e);
		}
		catch(PortInUseException e)
		{
			String message = getClass().getName() +
				" attempted to open a Serial Port that is already in use:\n" +
				"Device Name: " + fDeviceName +
				" Connection:\n" +
				"Ref: " + fCommPortIdentifier +
				" Name: " + fCommPortIdentifier.getName() +
				" Current Owner: " + fCommPortIdentifier.getCurrentOwner() +
				" Port Type: " + fCommPortIdentifier.getPortType();
			sLogger.logp(Level.WARNING, CLASS_NAME, "start", message, e);
		}
		catch(NullPointerException e)
		{
			String message = getClass().getName() +
				" open method encountered a null CommPortIdentifier for device: " +
				fDeviceName;
			sLogger.logp(Level.WARNING, CLASS_NAME, "start", message, e);
		}
	}
	
	/**
	 * Reads data from the connection and fires a 
	 * <code>fireConnectionDataEvent</code> method on the Connection.
	 * 
	 * @throws IOException
	 */
	protected void serviceConnection() throws IOException
	{
		// Create a new buffer if a previous listener is still using it.
		if (fReceiveBufferHandle.isInUse() || fPacketSizeChange)
		{
			fReceiveBuffer = ByteBuffer.allocate(fPacketSize);
			fReceiveBufferHandle = new DefaultBufferHandle(fReceiveBuffer);
			fPacketSizeChange = false;
		}
		else
		{
			// Reset buffer for next read
			fReceiveBuffer.position(0);
			fReceiveBuffer.limit(fPacketSize);
		}

		int bytesRead = fInputChannel.read(fReceiveBuffer);
		
		//TODO is this while loop necessary?
		// Loop while data is available
//		while ((bytesRead = fInputChannel.read(fReceiveBuffer)) > 0)
//		{
//			totalBytes += bytesRead;
//		}
		
		if (bytesRead > 0)
		{
			fReceiveBuffer.flip();
			fireInputBufferEvent(new InputBufferEvent(this, fReceiveBufferHandle));
		}
	}
	
	/**
	 * Closes the serial connection and channels.
	 */
	protected void closeConnection()
	{
		if (fInputChannel != null)
		{
			try
			{
				fInputChannel.close();
			}
			catch (IOException e)
			{
	    		sLogger.logp(
	        			Level.WARNING, CLASS_NAME, "closeConnection", 
						"Exception closing input channel", e);
			}
			
			fInputChannel = null;
		}
		
		if (fOutputChannel != null)
		{
			try
			{
				fOutputChannel.close();
			}
			catch (IOException e)
			{
	    		sLogger.logp(
	        			Level.WARNING, CLASS_NAME, "closeConnection", 
						"Exception closing output channel", e);
			}
			
			fOutputChannel = null;
		}
		
		if( fSerialPort != null )
		{
			fSerialPort.close();
			fSerialPort = null;
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
		
		return (stringRep.toString());
	}

	// --- Property get/set methods -------------------------------------------
	
	/**
	 * Get the default buffer size used for reading data.
	 * 
	 * @return the packet size.
	 */
	public int getPacketSize()
	{
		return fPacketSize;
	}
	
	/**
	 * Set the default buffer size to use for reading data.
	 * 
	 * @param size the packet size to set.
	 */
	public void setPacketSize(int size)
	{
		if (size != fPacketSize)
		{
			int oldValue = fPacketSize;
			fPacketSize = size;
			fPacketSizeChange = true;
			firePropertyChange("packetSize", oldValue, fPacketSize);
		}
	}

	/**
	 * Get the logical name for the port.
	 * 
	 * @return the device name.
	 */
	public String getDeviceName()
	{
		return fDeviceName;
	}
	
	/**
	 * Set the logical name for the port such as "COM1" or "/dev/ttyS0".
	 * 
	 * @param deviceName The device name.
	 */
	public void setDeviceName(String deviceName)
	{
		if (deviceName != null && !deviceName.equalsIgnoreCase(fDeviceName))
		{
			boolean wasStarted = isStarted();
			
			stop();
			String oldValue = fDeviceName;
			fDeviceName = deviceName;
			firePropertyChange("deviceName", oldValue, fDeviceName);
			
			// Start only if it was started before this method call
			if (wasStarted)
			{
				start();
			}
		}
	}
	
	/**
	 * Get input flow control.
	 * 
	 * @return the input flow control type.
	 */
	public String getInputFlowControl()
	{
		return fInputFlowControlString;
	}
	
	/**
	 * Set input flow control either "hardware", "software", or "none".
	 * 
	 * @param flowControl The type of flow control.
	 */
	public void setInputFlowControl(String flowControl)
	{
		if (flowControl != null 
				&& !flowControl.equalsIgnoreCase(fInputFlowControlString))
		{
			String oldValue = fInputFlowControlString;
			
			if (flowControl.equalsIgnoreCase(FLOWCONTROL_NONE))
			{
				fInputFlowControl = SerialPort.FLOWCONTROL_NONE;
				fInputFlowControlString = FLOWCONTROL_NONE;
			}
			else if (flowControl.equalsIgnoreCase(FLOWCONTROL_HARDWARE))
			{
				fInputFlowControl = SerialPort.FLOWCONTROL_RTSCTS_IN;
				fInputFlowControlString = FLOWCONTROL_HARDWARE;
			}
			else if (flowControl.equalsIgnoreCase(FLOWCONTROL_SOFTWARE))
			{
				fInputFlowControl = SerialPort.FLOWCONTROL_XONXOFF_IN;
				fInputFlowControlString = FLOWCONTROL_SOFTWARE;
			}
			else
			{
				String message = "Unknown input flow control parm: " + flowControl;
				sLogger.logp(Level.WARNING, CLASS_NAME,
					"setInputFlowControl", message);
			}
			
			// Check if property was actually reset
			if (oldValue != fInputFlowControlString)
			{
				boolean wasStarted = isStarted();
				
				stop();
				firePropertyChange(
						"inputFlowControl", oldValue, fInputFlowControlString);
				
				// Start only if it was started before this method call
				if (wasStarted)
				{
					start();
				}
			}			
		}
	}
	
	/**
	 * Get output flow control.
	 * 
	 * @return the output flow control.
	 */
	public String getOutputFlowControl()
	{
		return fOutputFlowControlString;
	}
	
	/**
	 * Set output flow control either "hardware", "software", or "none".
	 * 
	 * @param flowControl The type of flow control.
	 */
	public void setOutputFlowControl(String flowControl)
	{
		if (flowControl != null 
				&& !flowControl.equalsIgnoreCase(fOutputFlowControlString))
		{
			String oldValue = fOutputFlowControlString;

			if (flowControl.equalsIgnoreCase(FLOWCONTROL_NONE))
			{
				fOutputFlowControl = SerialPort.FLOWCONTROL_NONE;
				fOutputFlowControlString = FLOWCONTROL_NONE;
			}
			else if (flowControl.equalsIgnoreCase(FLOWCONTROL_HARDWARE))
			{
				fOutputFlowControl = SerialPort.FLOWCONTROL_RTSCTS_OUT;
				fOutputFlowControlString = FLOWCONTROL_HARDWARE;
			}
			else if (flowControl.equalsIgnoreCase(FLOWCONTROL_SOFTWARE))
			{
				fOutputFlowControl = SerialPort.FLOWCONTROL_XONXOFF_OUT;
				fOutputFlowControlString = FLOWCONTROL_SOFTWARE;
			}
			else
			{
				String message = "Unknown output flow control parm: " 
					+ flowControl;
				sLogger.logp(Level.WARNING, CLASS_NAME,
					"setOutputFlowControl", message);
			}
			
			// Check if property was actually reset
			if (oldValue != fOutputFlowControlString)
			{
				boolean wasStarted = isStarted();
				
				stop();
				firePropertyChange(
						"outputFlowControl", oldValue, fOutputFlowControlString);
				
				// Start only if it was started before this method call
				if (wasStarted)
				{
					start();
				}
			}			
		}
	}
	
	/**
	 * Get Parity scheme.
	 * 
	 * @return the parity scheme.
	 */
	public String getParity()
	{
		return fParityString;
	}
	
	/**
	 * Set Parity scheme. Options are "none", "odd", "even", 
	 * "mark", and "space".
	 * 
	 * @param parity The parity scheme.
	 */
	public void setParity(String parity)
	{
		if (parity != null && !parity.equalsIgnoreCase(fParityString))
		{
			String oldValue = fParityString;

			if (parity.equalsIgnoreCase(PARITY_NONE))
			{
				fParity = SerialPort.PARITY_NONE;
				fParityString = PARITY_NONE;
			}
			else if (parity.equalsIgnoreCase(PARITY_ODD))
			{
				fParity = SerialPort.PARITY_ODD;
				fParityString = PARITY_ODD;
			}
			else if (parity.equalsIgnoreCase(PARITY_EVEN))
			{
				fParity = SerialPort.PARITY_EVEN;
				fParityString = PARITY_EVEN;
			}
			else if (parity.equalsIgnoreCase(PARITY_MARK))
			{
				fParity = SerialPort.PARITY_MARK;
				fParityString = PARITY_MARK;
			}
			else if (parity.equalsIgnoreCase(PARITY_SPACE))
			{
				fParity = SerialPort.PARITY_SPACE;
				fParityString = PARITY_SPACE;
			}
			else
			{
				String message = "SerialCommunicationPort - unknown parity parm: "
						+ parity;
				sLogger.logp(Level.WARNING, CLASS_NAME,
					"setParity", message);
			}

			// Check if property was actually reset
			if (oldValue != fParityString)
			{
				boolean wasStarted = isStarted();
				
				stop();
				firePropertyChange(
						"parity", oldValue, fParityString);
				
				// Start only if it was started before this method call
				if (wasStarted)
				{
					start();
				}
			}			
		}
	}
	
	/**
	 * Get number of STOP bits.
	 * @return the number of stop bits.
	 */
	public String getStopBits()
	{
		return fStopBitsString;
	}
	
	/**
	 * Set number of STOP bits. Options are "1", "1.5", or "2".
	 * 
	 * @param stopBits The number of stop bits.
	 */
	public void setStopBits(String stopBits)
	{
		if (stopBits != null && !stopBits.equalsIgnoreCase(fStopBitsString))
		{
			String oldValue = fStopBitsString;

			if (stopBits.equalsIgnoreCase(STOPBITS_1))
			{
				fStopBits = SerialPort.STOPBITS_1;
				fStopBitsString = STOPBITS_1;
			}
			else if (stopBits.equalsIgnoreCase(STOPBITS_1_5))
			{
				fStopBits = SerialPort.STOPBITS_1_5;
				fStopBitsString = STOPBITS_1_5;
			}
			else if (stopBits.equalsIgnoreCase(STOPBITS_2))
			{
				fStopBits = SerialPort.STOPBITS_2;
				fStopBitsString = STOPBITS_2;
			}
			else
			{
				String message = "Unknown stop bits parm: " + stopBits;
				sLogger.logp(Level.WARNING, CLASS_NAME,
					"setStopBits", message);
			}

			// Check if property was actually reset
			if (oldValue != fStopBitsString)
			{
				boolean wasStarted = isStarted();
				
				stop();
				firePropertyChange(
						"stopBits", oldValue, fStopBitsString);
				
				// Start only if it was started before this method call
				if (wasStarted)
				{
					start();
				}
			}			
		}
	}
	
	/**
	 * Get data bit format.
	 * 
	 * @return Returns the data bits format.
	 */
	public String getDataBits()
	{
		return fDataBitsString;
	}
	
	/**
	 * Set data bit format. Options are "5", "6", "7", or "8".
	 * 
	 * @param dataBits The data bits format.
	 */
	public void setDataBits(String dataBits)
	{
		if (dataBits != null && !dataBits.equalsIgnoreCase(fDataBitsString))
		{
			String oldValue = fDataBitsString;

			if (dataBits.equalsIgnoreCase(DATABITS_5))
			{
				fDataBits = SerialPort.DATABITS_5;
				fDataBitsString = DATABITS_5;
			}
			else if (dataBits.equalsIgnoreCase(DATABITS_6))
			{
				fDataBits = SerialPort.DATABITS_6;
				fDataBitsString = DATABITS_6;
			}
			else if (dataBits.equalsIgnoreCase(DATABITS_7))
			{
				fDataBits = SerialPort.DATABITS_7;
				fDataBitsString = DATABITS_7;
			}
			else if (dataBits.equalsIgnoreCase(DATABITS_8))
			{
				fDataBits = SerialPort.DATABITS_8;
				fDataBitsString = DATABITS_8;
			}
			else
			{
				String message = "Unknown data bits parm: " + dataBits;
				sLogger.logp(Level.WARNING, CLASS_NAME,
					"setDataBits", message);
			}

			// Check if property was actually reset
			if (oldValue != fDataBitsString)
			{
				boolean wasStarted = isStarted();
				
				stop();
				firePropertyChange(
						"dataBits", oldValue, fDataBitsString);
				
				// Start only if it was started before this method call
				if (wasStarted)
				{
					start();
				}
			}			
		}
	}
	
	/**
	 * Get the flow rate of the port.
	 * 
	 * @return the baud rate.
	 */
	public int getBaudRate()
	{
		return fBaudRate;
	}
	
	/**
	 * Set the flow rate of the port.
	 * 
	 * @param baudRate The rate to set.
	 */
	public void setBaudRate(int baudRate)
	{
		fBaudRate = baudRate;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: SerialConnection.java,v $
//  Revision 1.10  2006/05/01 13:37:58  smaher_cvs
//  Added error handling to "support" javacomm 3.0.
//
//  Revision 1.9  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.8  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.7  2005/10/21 20:27:14  tames_cvs
//  Added writeDataToChannel method that handles partial write cases better.
//
//  Revision 1.6  2005/06/21 04:13:33  jhiginbotham_cvs
//  Remove system.out message.
//
//  Revision 1.5  2005/05/04 17:17:58  tames_cvs
//  Added ConnectSource support.
//
//  Revision 1.4  2005/03/01 22:34:43  tames_cvs
//  Modified to use AbstractThreadedConnection as super class. Added
//  property get and set methods. Changed XML parameter names to be
//  consistent with other Connections.
//
//  Revision 1.3  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.2  2004/12/02 20:15:28  tames_cvs
//  Added code to better handle missing descriptor parameters.
//
//  Revision 1.1  2004/11/18 16:15:23  tames
//  Initial version
//
//
