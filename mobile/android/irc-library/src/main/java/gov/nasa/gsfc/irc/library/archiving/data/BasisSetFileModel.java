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

import java.util.Date;

import javax.swing.table.DefaultTableModel;

/**
 *  BasisSetFileModel overwrites some of the methods in DefaultTableModel
 *  and gives additional menthods like removeRows() and getRow() to
 *  interact with the table.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version May 31, 2005 1:32:37 PM
 *  @author Peyush Jain
 */

public class BasisSetFileModel extends DefaultTableModel
{
    private static final String[] COLUMN_NAMES = {"Basis Bundle Id", "Start Time", "Stop Time"};
    private ArchiveCatalog fCatalog;
    
    /**
     * Constructor
     */
    public BasisSetFileModel()
    {
        fCatalog = ArchiveCatalog.getArchiveCatalog();

        if(fCatalog != null)
        {
            for(int i = 0; i < fCatalog.getSessions().size(); i++)
            {
                ArchiveSession session = (ArchiveSession)fCatalog.getSessions().elementAt(i);
                String name = session.getBasisBundleId().getFullyQualifiedName();
                long startTime = session.getStartTime();
                long stopTime = session.getStopTime();
                
                Date d1 = new Date(startTime);
                Date d2 = new Date(stopTime);
                
                Object row[] = {name, d1, d2};
//                SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy 'at' hh:mm:ss a z");
//                Object row[] = {name, format.format(d1), format.format(d2)};
//                Object row[] = {name, DateFormat.getDateInstance(DateFormat.FULL).format(d1),
//                        DateFormat.getDateInstance(DateFormat.FULL).format(d2)};
//                
                addRow(row);
            }
        }        
    }
  
    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }

    /** 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount()
    {
        return COLUMN_NAMES.length;
    }
    
    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int i)
    {
        return COLUMN_NAMES[i];
    }
    
    /**
     * Removes the given table rows
     * 
     * @param row array with table indices to be removed
     */
    public void removeRows(int[] row)
    {
        for(int i = 0; i < row.length; i++)
        {
            removeRow(row[i] - i);
        }
    }
    
    /**
     * Gets the ArchiveSession corresponding to the given table row
     * 
     * @param row row index in the table
     * @return ArchiveSession
     */
    public ArchiveSession getArchiveSession(int row)
    {
        return (ArchiveSession)fCatalog.getSessions().elementAt(row);
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetFileModel.java,v $
//  Revision 1.2  2005/10/14 13:53:36  pjain_cvs
//  Added getArchiveSession method and removed getRow method. Also,
//  getArchiveCatalog method is used to get a reference to catalog object.
//
//  Revision 1.1  2005/08/19 19:18:47  pjain_cvs
//  Relocated to gov.nasa.gsfc.irc.library.archiving.data package
//
//  Revision 1.1  2005/06/15 18:30:39  pjain_cvs
//  Adding to CVS
//
//