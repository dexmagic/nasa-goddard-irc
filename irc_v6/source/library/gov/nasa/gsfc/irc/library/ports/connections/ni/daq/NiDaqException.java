//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   1    IRC       1.0         4/2/2001 9:58:41 AM  Troy Ames       
//  $
//	06/28/99	T. Ames/588
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

package gov.nasa.gsfc.irc.library.ports.connections.ni.daq;

/**
 *  This exception can be thrown by the NI-DAQ device driver. NI-DAQ is a set of
 *  functions that control all of the National Instruments
 *	plug-in DAQ devices for analog I/O, digital I/O, timing I/O, SCXI signal
 *	conditioning, and RTSI multiboard synchronization. Refer to the documents:
 *	<i><a href="ftp://ftp.natinst.com/support/manuals/321644d.pdf">NI-DAQ User Manual</a></i>
 *	and <i><a href="ftp://ftp.natinst.com/support/manuals/321645d.pdf">NI-DAQ Function Reference Manual</a></i>
 *	available from <a href="http://www.natinst.com/">National Instruments</a>.
 *
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/08/23 17:52:12 $
 *  @author		T. Ames
**/
public class NiDaqException extends Exception
{
	/**
	 * Constructs an <code>NiDaqException</code> with no specified detail message.
     */
	public NiDaqException()
	{
		super();
    }

    /**
     * Constructs an <code>NiDaqException</code> with the specified detail message.
     *
     * @param   s   the detail message.
     */
	public NiDaqException(String s)
	{
		super(s);
    }
}
