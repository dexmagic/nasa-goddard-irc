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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  BasisSetFileNameGenerator class generates a file name in yymmdd_nnn format,
 *  where yy: year, mm: month, dd: day, and nnn: file number.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Jun 3, 2005 8:56:45 PM
 *  @author Peyush Jain
 */

public class BasisSetFileNameGenerator
{
    private static final String CLASS_NAME = BasisSetFileWriteProcessor.class.getName();
    private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

    private static BasisSetFileNameGenerator sInstance = new BasisSetFileNameGenerator();

    private Calendar fLastFileDate = null;
    private int fFileNum = 0;
    private String fExtension = "bsa"; //BasisSetArchive file

    private static TimeZone fTimezone = TimeZone.getDefault();

    private static final String fObjectKey = "FILENAME GENERATING KEY";
    private static final String fFileNumKey = "FILE NUM KEY";
    private static final String fDateKey = "LAST FILENAME DATE KEY";

    
    public BasisSetFileNameGenerator()
    {
        HashMap dObject = (HashMap)Irc.getPersistentStore().retrieve(fObjectKey);

        if (dObject == null)
        {
            fFileNum = 1;
            fLastFileDate = null;

            checkpoint();
        }
        else
        {
            // some information was pulled from the datastore
            fLastFileDate = (Calendar)dObject.get(fDateKey);
            Integer tmpInt = (Integer)dObject.get(fFileNumKey);
            if (tmpInt == null)
            {
                fFileNum = 0;
            }
            else
            {
                fFileNum = tmpInt.intValue();
            }
        }
    }
    
    /**
     * @return an instance of this class
     */
    public static BasisSetFileNameGenerator getInstance()
    {
        return sInstance;
    }

    /**
     * Checks if the given day is different from the last day used
     * 
     * @param calendar day
     * @return true, if a new day and false, if its not
     */
    private boolean isNewDay(Calendar rightNow)
    {

        if (fLastFileDate == null)
        {
            if (sLogger.isLoggable(Level.SEVERE))
            {   
                String msg = "fLastFileDate was null";
                sLogger.logp(Level.INFO, CLASS_NAME, "isNewDay", msg);
            }
            
            return true;
        }

        if ((rightNow.get(Calendar.DAY_OF_MONTH) != fLastFileDate
                .get(Calendar.DAY_OF_MONTH))
                || (rightNow.get(Calendar.MONTH) != fLastFileDate
                        .get(Calendar.MONTH))
                || (rightNow.get(Calendar.YEAR) != fLastFileDate
                        .get(Calendar.YEAR)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Gets the file number for the given calendar day
     * 
     * @param calendar day
     * @return fileNum
     */
    private int getFileNumber(Calendar rightNow)
    {
        //if the given day is a new day then reset fileNum to 1
        //otherwise increase fileNum by 1
        if (isNewDay(rightNow))
        {
            fFileNum = 1;
        }
        else
        {
            fFileNum++;
        }
        
        return fFileNum;
    }

    /**
     * Gets the date string
     * 
     * @param calendar day
     * @return date string
     */
    private String getDateString(Calendar rightNow)
    {
        int year = rightNow.get(Calendar.YEAR) % 100;
        int month = rightNow.get(Calendar.MONTH) + 1;
        int day = rightNow.get(Calendar.DAY_OF_MONTH);

        return customFormat("00", year) + customFormat("00", month)
                + customFormat("00", day);
    }

    /**
     * Formats value with the given pattern.
     * 
     * @param pattern
     * @param value
     * @return formatted string
     */
    private String customFormat(String pattern, int value)
    {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }

    /**
     * Gets a fileName in yymmdd_nnn format, where yy: year, 
     * mm: month, dd: day, nnn: file number 
     * 
     * @return fileName
     */
    public synchronized String getFileName()
    {
        Calendar rightNow = Calendar.getInstance(fTimezone);
        
        String dateStr = getDateString(rightNow);
        int fileNum = getFileNumber(rightNow);

        fLastFileDate = rightNow;
        checkpoint();

        String fileName = dateStr + "_" 
                + customFormat("000", fileNum) + "." + fExtension;

        return fileName;
    }
    
    /**
     * Saves the current date and fileNum to the disk.
     */
    private void checkpoint()
    {
        HashMap dObject = new HashMap();

        dObject.put(fDateKey, fLastFileDate);
        dObject.put(fFileNumKey, new Integer(fFileNum));

        Irc.getPersistentStore().store(fObjectKey, dObject);
        Irc.getPersistentStore().saveToDisk();
    }
    
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetFileNameGenerator.java,v $
//  Revision 1.1  2005/06/14 15:32:39  pjain_cvs
//  Adding to CVS
//
//