//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//  $Log: NiDaq.java,v $
//  Revision 1.2  2005/11/14 05:40:21  tames
//  Renamed sNiDaqLibrary field.
//
//  Revision 1.1  2004/08/23 17:52:12  tames
//  Updated for version 6
//
//  Revision 1.6  2003/07/01 15:49:24  tames_cvs
//  Updated comments
//
//  Revision 1.5  2003/06/17 03:01:45  tames_cvs
//  New NI function
//
//  Revision 1.4  2003/06/16 21:01:01  tames_cvs
//  added NI functions
//
//  Revision 1.3  2003/06/12 21:33:26  tames_cvs
//  *** empty log message ***
//
//
//	06/06/99	T. Ames/588
//		Initial version.
//	07/21/99	T. Ames/588
//		Added initDaBoards method.
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

package gov.nasa.gsfc.irc.library.ports.connections.ni.daq;

import java.nio.Buffer;

/**
 *  NiDaq is a Java interface to a subset of the
 *	National Instruments NI-DAQ driver. NI-DAQ is a set of functions
 *	that control all of the National Instruments
 *	plug-in DAQ devices for analog I/O, digital I/O, timing I/O, SCXI signal
 *	conditioning, and RTSI multiboard synchronization. Refer to the documents:
 *	<i><a href="ftp://ftp.natinst.com/support/manuals/321644d.pdf">NI-DAQ User Manual</a></i>
 *	and <i><a href="ftp://ftp.natinst.com/support/manuals/321645d.pdf">NI-DAQ Function Reference Manual</a></i>
 *	available from <a href="http://www.natinst.com/">National Instruments</a>.
 *
 * <p>You must call one of the {@link #loadLibrary loadLibrary} methods before
 * using any of the NI-DAQ methods.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/11/14 05:40:21 $
 *  @author		T. Ames
**/

public class NiDaq	extends	Object
{

	/**
	 *  Name of the NI-DAG Java Native Interface library. This library is
	 *  loaded with the {@link #loadLibrary loadLibrary} method. The default is
	 *  "NiDaqAdapter".
	**/
	public static String sNiDaqLibrary = "NiDaqAdapter";

	/**
	 *  Construct a new NiDaqAdapter.
	**/
	private NiDaq()
	{
		super();
	}

	/**
	 * Loads the NI-DAG Java Native Interface library specified by the
	 * {@link #sNiDaqLibrary sNiDaqLibrary} field.
	 * The manner in which a library name is mapped to the
	 * actual library is system dependent.
	 *
	 * @exception  SecurityException  if a security manager exists and its
	 *             <code>checkLink</code> method doesn't allow
	 *             loading of the specified dynamic library
	 * @exception  UnsatisfiedLinkError  if the library does not exist.
	 * @see        loadLibrary(java.lang.String)
	 * @see        java.lang.SecurityManager#checkLink(java.lang.String)
	 */
	public static void loadLibrary()
	{
		loadLibrary(sNiDaqLibrary);
	}

	/**
	 * Loads the NI-DAG Java Native Interface library specified by the
	 * <code>libname</code>
	 * argument. The manner in which a library name is mapped to the
	 * actual library is system dependent.
	 * <p>
	 * Uses the <code>System.loadLibrary(name)</code> method call.
	 *
	 * @param      libname   the name of the library.
	 * @exception  SecurityException  if a security manager exists and its
	 *             <code>checkLink</code> method doesn't allow
	 *             loading of the specified dynamic library
	 * @exception  UnsatisfiedLinkError  if the library does not exist.
	 * @see        java.lang.System#loadLibrary(java.lang.String)
	 * @see        java.lang.SecurityManager#checkLink(java.lang.String)
	 */
	public static void loadLibrary(String libname) throws UnsatisfiedLinkError
	{
		System.loadLibrary(libname);
	}

	/**
	 *  Returns the number of items remaining to be transferred after
	 *	a DIG_Block_In or DIG_Block_Out call.
	 *	Refer to the <code>DIG_Block_Check</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	group			the group to be configured
	 *  @return					number of items yet to be transferred
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native long digBlockCheck(short deviceNumber, short group)
		throws NiDaqException;

	/**
	 *  Halts any ongoing asynchronous transfer, allowing another transfer
	 *	to be initiated.
	 *	Refer to the <code>DIG_Block_Clear</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	group			the group involved in the asynchronous transfer
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digBlockClear(short deviceNumber, short group)
		throws NiDaqException;

	/**
	 *  Initiates an asynchronous data transfer from the specified
	 *	group to memory. The parameter <code>count</code> is the number of
	 *	items (for example, 8-bit items for a group of size 1, 16-bit items for
	 *	a group of size 2, and 32-bit items for a group of size 4) to be
	 *	transferred to the area of memory specified by <code>buffer</code>
	 *	from the group indicated by <code>group</code>.
	 *	Refer to the <code>DIG_Block_In</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	group			the group to be read from
	 *  @param	buffer			conversion samples returned
	 *  @param	offset			index at which to start storing items.
	 *  @param	count			number of items to be transferred
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digBlockIn(
		short deviceNumber, short group, byte[] buffer, long offset, long count)
		throws NiDaqException;

	/**
	 *  Initiates an asynchronous data transfer from the specified
	 *	group to memory. The parameter <code>count</code> is the number of
	 *	items (for example, 8-bit items for a group of size 1, 16-bit items for
	 *	a group of size 2, and 32-bit items for a group of size 4) to be
	 *	transferred to the area of memory specified by <code>buffer</code>
	 *	from the group indicated by <code>group</code>.
	 *	Refer to the <code>DIG_Block_In</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	group			the group to be read from
	 *  @param	buffer			conversion samples returned
	 *  @param	offset			index at which to start storing items.
	 *  @param	count			number of items to be transferred
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digBlockIn(
		short deviceNumber, short group, Buffer buffer, long offset, long count)
		throws NiDaqException;

	/**
	 *  Initiates an asynchronous transfer of data from memory to
	 *	the specified group. The parameter <code>count</code> is the number of
	 *	items (for example, 8-bit items for a group of size 1, 16-bit items for
	 *	a group of size 2, and 32-bit items for a group of size 4) to be
	 *	transferred from the area of memory specified by <code>buffer</code>
	 *	to the group indicated by <code>group</code>.
	 *	Refer to the <code>DIG_Block_In</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	group			the group to be written to
	 *  @param	buffer			array containing the user's data
	 *  @param	offset			index where items to be transferred starts.
	 *  @param	count			number of items to be transferred
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digBlockOut(
		short deviceNumber, short group, byte[] buffer, long offset, long count)
		throws NiDaqException;

	/**
	 *  Initiates an asynchronous transfer of data from memory to
	 *	the specified group. The parameter <code>count</code> is the number of
	 *	items (for example, 8-bit items for a group of size 1, 16-bit items for
	 *	a group of size 2, and 32-bit items for a group of size 4) to be
	 *	transferred from the area of memory specified by <code>buffer</code>
	 *	to the group indicated by <code>group</code>.
	 *	Refer to the <code>DIG_Block_In</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	group			the group to be written to
	 *  @param	buffer			array containing the user's data
	 *  @param	offset			index where items to be transferred starts.
	 *  @param	count			number of items to be transferred
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digBlockOut(
		short deviceNumber, short group, Buffer buffer, long offset, long count)
		throws NiDaqException;

	/**
	 *  Configures the specified group for port assignment,
	 *	direction (input or output), and size.
	 *	Refer to the <code>DIG_Grp_Config</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	group			the group to be configured
	 *  @param	groupSize		size of the group
	 *  @param	port			digital I/O port assigned to the group
	 *  @param	direction		input or output
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digGroupConfig(
		short deviceNumber, short group, short groupSize,
		short port, short direction)
		throws NiDaqException;

	/**
	 *  Configures the specified group for handshake signal modes.
	 *	Refer to the <code>DIG_Grp_Mode</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	group			the group to be configured
	 *  @param	protocol		basic handshaking system
	 *  @param	edge			rising-edge or falling-edge pulsed signals
	 *  @param	reqpol			request signal is to be active high or active low
	 *  @param	ackpol			acknowledge handshake signal is to be active
	 *							high or active low
	 *  @param	delayTime		data settling time allowed
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digGroupMode(
		short deviceNumber, short group, short protocol, short edge,
		short reqpol, short ackpol, short delayTime)
		throws NiDaqException;

	/**
	 * Returns the digital logic state of the specified digital line in the 
	 * specified port.
	 * Refer to the <code>DIG_In_Line</code> function in the
	 * <i>NI-DAQ Function Reference Manual</i>.
	 *
	 * @param	deviceNumber	number assigned by configuration utility
	 * @param	port			the digital I/O port number
	 * @param	line			the digital line to be read
	 * @return the digital logic state low (0) or high (1).
	 *
	 * @throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native short digInLine(
		short deviceNumber, short port, short line)
		throws NiDaqException;

	/**
	 * Returns digital input data from the specified digital I/O port.
	 * Refer to the <code>DIG_In_Prt</code> function in the
	 * <i>NI-DAQ Function Reference Manual</i>.
	 *
	 * @param	deviceNumber	number assigned by configuration utility
	 * @param	port			the digital I/O port number
	 * @return 8-bit digital data read from the specified port.
	 *
	 * @throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native int digInPrt(short deviceNumber, short port)
		throws NiDaqException;

	/**
	 * Reads digital input data from the specified digital group.
	 * Refer to the <code>DIG_In_Grp</code> function in the
	 * <i>NI-DAQ Function Reference Manual</i>.
	 *
	 * @param	deviceNumber	number assigned by configuration utility
	 * @param	group			group to read from
	 * @return digital data read from the ports.
	 *
	 * @throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native short digInGrp(short deviceNumber, short group)
		throws NiDaqException;

	/**
	 *  Configures the specified port for direction (input or output) and 
	 *  output type (standard or wired-OR).
	 *	Refer to the <code>DIG_Prt_Config</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	port	digital I/O port number of the port to write to
	 *  @param	mode	handshake mode the port uses
	 *  @param	dir	direction, input, or output
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digPrtConfig(
		short deviceNumber, short port, short mode, short dir)
		throws NiDaqException;

	/**
	 *  Writes digital output data to the specified digital group
	 *  on the specified device.
	 *	Refer to the <code>DIG_Out_Grp</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	group	group to write to
	 *  @param	groupPattern	digital data to be written
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digOutGroup(
		short deviceNumber, short group, short groupPattern)
		throws NiDaqException;

	/**
	 *  Sets or clears the specified digital output line in the
	 *	specified digital port.
	 *	Refer to the <code>DIG_Out_Line</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	port	digital I/O port number
	 *  @param	line	digital output line
	 *  @param	state	new digital logic state
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digOutLine(
		short deviceNumber, short port, short line, short state)
		throws NiDaqException;

	/**
	 *  Writes digital output data to the specified digital port.
	 *	Refer to the <code>DIG_Out_Prt</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	port	digital I/O port number of the port to write to
	 *  @param	pattern	8-bit digital pattern for the data written
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void digOutPrt(
		short deviceNumber, short port, short pattern)
		throws NiDaqException;

	/**
	 *  Initializes the hardware and software states of a National Instruments
	 *	DAQ device to its default state, and then returns a numeric device
	 *	code that corresponds to the type of device initialized. Any operation
	 *	that the device is performing is halted.
	 *	Refer to the <code>Init_DA_Brds</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @return					the type of device initialized
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native short initDaBoards(short deviceNumber)
		throws NiDaqException;

	/**
	 *  Selects parameters pertaining to the device operation.
	 *	Refer to the <code>set_DAQ_Device_Info</code> function in the
	 *	<i>NI-DAQ Function Reference Manual</i>.
	 *
	 *  @param	deviceNumber	number assigned by configuration utility
	 *  @param	infoType		parameter you want to modify
	 *  @param	infoValue		new value you want to assign to the parameter
	 *							specified by <code>infoType</code>
	 *
	 *	@throws	NiDaqException	If a NI DAQ error occurred.
	**/
	public static native void setDaqDeviceInfo(
		short deviceNumber, int infoType, int infoValue)
		throws NiDaqException;


	/**
	 *  Main method for testing.
	**/
	public static void main(String[] args)
	{
		short deviceNumber = 1;
		short group = 2;
		short count = 5;
		short offset = 0;
		short[] buffer = new short[100];
		short groupSize = 2;
		short port = 1;
		short direction = 1;

		try
		{
			NiDaq.loadLibrary();
			NiDaq.digBlockClear(deviceNumber, group);
//			NiDaq.digBlockIn(deviceNumber, group, buffer, offset, count);
			System.out.println("digBlockIn buffer:" + buffer);
			NiDaq.digGroupConfig(
				deviceNumber, group, groupSize, port, direction);
		}
		catch (NiDaqException ex)
		{
			System.out.println("Exception occurred during test: " + ex);
		}
	}
} // End class
