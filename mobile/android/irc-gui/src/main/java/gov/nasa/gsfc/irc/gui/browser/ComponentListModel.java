//=== File Prolog ============================================================
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *   Neither the author, their corporation, nor NASA is responsible for
//      any consequence of the use of this software.
//  *   The origin of this software must not be misrepresented either by
//      explicit claim or by omission.
//  *   Altered versions of this software must be plainly marked as such.
//  *   This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.gui.browser;

import gov.nasa.gsfc.irc.app.DescriptorFramework;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.description.xml.NamespaceTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractListModel;

/**
 *  Component List Model populates the list with available components in
 *  Add Component GUI.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Oct 06, 2004 10:18:30 PM
 *  @author Peyush Jain
 */

public class ComponentListModel extends AbstractListModel
{
    private Vector fItems;

    /**
     * Constructor
     */
    public ComponentListModel()
    {
        DescriptorFramework desciptorFramework = 
            Irc.getIrcManager().getDescriptorFramework();
            
        NamespaceTable nsTable = 
            desciptorFramework.getGlobalMap().getNamespaceTable("ComponentLibraryType");
        
        fItems = new Vector();
        
        Iterator i = nsTable.getValueNames();
        ArrayList list = new ArrayList();
        while (i.hasNext()) list.add(i.next());
        Collections.sort(list);
        i = list.iterator();
        
        while(i.hasNext())
        {
            fItems.add(i.next());
        }
    }

    /**
     * Returns the value at the specified index. 
     *  
     * @param index the requested index
     * @return the value at <code>index</code>
     */
    public Object getElementAt(int index)
    {
        if (0 <= index && index < fItems.size())
            return fItems.elementAt(index);
        else
            return null;
    }

    /** 
     * Returns the length of the list.
     * 
     * @return the length of the list
     */
    public int getSize()
    {
        return fItems.size();
    }
    
    /**
     * Returns all the available components.<p>
     *
     * @return vector with available components.
     */
    public Vector getAvailableComponents()
    {
        return fItems;
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: ComponentListModel.java,v $
//  Revision 1.3  2006/06/26 20:09:58  smaher_cvs
//  Sort list of components
//
//  Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/11/18 20:53:12  pjain_cvs
//  Adding to CVS
//
//