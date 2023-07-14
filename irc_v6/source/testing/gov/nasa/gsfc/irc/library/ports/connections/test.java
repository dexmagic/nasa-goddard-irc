package gov.nasa.gsfc.irc.library.ports.connections;

import gov.nasa.gsfc.irc.library.ports.connections.ni.gpib.GPIB;
import gov.nasa.gsfc.irc.library.ports.connections.ni.gpib.GPIBException;


/**
 * @author root
 * Simple test included in GBT GPIB package
 */
public class test
{

    public static void main (String args[])
    {
        System.out.println("Testing GPIB JNI...");
    
       GPIB jgpib = new GPIB();
        
        try
        {
            int ud = GPIB.ibfind("dev1");
	    System.out.println("completed command 1, ud = " + ud);    
         
	    GPIB.ibwrt(ud, "*RST", 4);
	    System.out.println("completed command 2");  
	    System.out.flush();
            
	    GPIB.ibwrt(ud, "SOURCE:VOLTAGE 2", 16);
	    System.out.println("completed command 3");    
            
	    GPIB.ibwrt(ud, "SOURCE:VOLTAGE?", 15);
	    System.out.println("completed command 4");    

            String rd = ""; 
            GPIB.ibrd(ud, rd, 16);
	    System.out.println("completed command 5");    
            System.out.println(rd);
        }
        catch (GPIBException ex)
        {
            System.out.println("Exception occurred during test: " + ex);
        }


    }
}
