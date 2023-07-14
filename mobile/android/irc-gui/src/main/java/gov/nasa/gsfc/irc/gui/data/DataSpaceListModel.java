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

package gov.nasa.gsfc.irc.gui.data;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.DefaultListModel;

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.DataSpace;

/**
 * A ListModel that populates the List from the available BasisBundles in 
 * the DataSpace.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 22, 2005 1:55:42 PM
 * @author Troy Ames
 */
public class DataSpaceListModel extends DefaultListModel
{
     /**
     * Default constructor.
     */
    public DataSpaceListModel()
    {
        super();
        
        // Add basis bundles
        buildList();
    }
    
    /**
     * Build the list of available BasisBundles.
     */
    private void buildList()
    {
    	DataSpace dataSpace = Irc.getDataSpace();
       	Collection bundles = dataSpace.getBasisBundles();
       	
    	for (Iterator iter = bundles.iterator(); iter.hasNext();)
    	{
    		addElement(iter.next());
    	}
    }
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DataSpaceListModel.java,v $
//  Revision 1.1  2006/01/02 03:52:39  tames
//  Initial version.
//
//