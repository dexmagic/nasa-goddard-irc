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

package gov.nasa.gsfc.irc.library.ports.connections.ni.gpib;

/**
 * Proxy for native GPIB interface code
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2004/09/08 15:05:22 $
 * @author	 smaher
 */
public class GPIB

{
   
    /**
     * Construct a new GPIB, loading the JNI library.
     */
    public GPIB ()
    {
        //System.loadLibrary("GPIB");
    }

    static  { System.loadLibrary("GPIB"); }
    
    /**
     * Open the device or board and return the unit descriptor
     *
     * @param     device   the name of the device
     * @return    integer number representing the unit descriptor
     * @exception GPIBException if no device or board has the symbolic
     *            name given in the device String
     */
    public static native int ibfind (String device) throws GPIBException;
    

    /**
     * Read the given number of data bytes 
     *
     * @param     ud    the unit descriptor for the device
     * @param     rd    the string into which the read data bytes are stored
     * @param     count number of bytes to read
     * @exception GPIBException if no there aren't 'count' data bytes
     *            waiting to be read.
     */
    public static native void ibrd(int ud, String rd, long count) throws GPIBException;
    

    /**
     * Write data bytes to a device.
     *
     * @param     ud    the unit descriptor of the device to write to
     * @param     wrt   the data bytes to write to the device
     * @param     count number of bytes to write
     * @exception GPIBException if no device or board has the symbolic
     *            name given in the device String
     */
    public static native void ibwrt(int ud, String wrt, long count) throws GPIBException;

    /**
     * Main method for testing
    **/
    public static void main (String args[])
    {

        try 
        {
            GPIB jgpib = new GPIB();
            int ud = GPIB.ibfind("dev1");
        
            String wrt = "*RST";
            GPIB.ibwrt(ud, "*RST", 4L);
        
            wrt = "SOUR:VOLT 2";
            GPIB.ibwrt(ud, wrt, 11L);

            wrt = "SOUR:VOLT?";
            GPIB.ibwrt(ud, wrt, 10L);

            String rd = "                                ";
            GPIB.ibrd(ud, rd, 16L);

            System.out.println(rd);
        }
        catch (GPIBException ex)
        {
            System.out.println("Exception occurred during test: " + ex);
        }
        
    }

}


//--- Development History  ---------------------------------------------------
//
//$Log: GPIB.java,v $
//Revision 1.2  2004/09/08 15:05:22  smaher_cvs
//Moved dev history to bottome of file and removed extraneous library load from constructor
//
