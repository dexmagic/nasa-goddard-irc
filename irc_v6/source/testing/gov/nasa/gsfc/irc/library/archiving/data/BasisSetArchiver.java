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

package gov.nasa.gsfc.irc.library.archiving.data;

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.library.archiving.data.BasisSetFileReader;
import gov.nasa.gsfc.irc.library.archiving.data.BasisSetFileWriter;

/**
 *  BasisSetArchiver contains main for testing BasisSet Archiver.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Jun 10, 2005 1:32:57 PM
 *  @author Peyush Jain
 */

public class BasisSetArchiver
{
    public static void main(String[] args)
    {
        Irc.main(args);
        System.out.println("Starting Algorithms...\n");
  
        //Data source
//        Algorithm signalGenerator = new SignalGenerator
//                                        ("Signal Generator", 60, 1, 100000, 60);
//        Irc.getComponentManager().addComponent(signalGenerator);   

        Irc.getComponentManager().startAllComponents();

        //Writer
        BasisSetFileWriter writer = new BasisSetFileWriter("Writer");
        Irc.getComponentManager().addComponent(writer); 
        
        //Reader
        BasisSetFileReader reader = new BasisSetFileReader("Reader");
        Irc.getComponentManager().addComponent(reader);   
        
        
//        System.out.println(Irc.getComponentManager().toString());        
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetArchiver.java,v $
//  Revision 1.1  2005/07/25 18:59:30  pjain_cvs
//  Moved from gov.nasa.gsfc.irc.library.algorithms package
//
//  