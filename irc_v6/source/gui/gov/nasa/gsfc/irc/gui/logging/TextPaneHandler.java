// === File Prolog ============================================================
//
// This code was developed by NASA, Goddard Space Flight Center, Code 580
// for the Instrument Remote Control (IRC) project.
//
// --- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
// --- Warning ----------------------------------------------------------------
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// * Neither the author, their corporation, nor NASA is responsible for
// any consequence of the use of this software.
// * The origin of this software must not be misrepresented either by
// explicit claim or by omission.
// * Altered versions of this software must be plainly marked as such.
// * This notice may not be removed or altered.
//
// === End File Prolog ========================================================

package gov.nasa.gsfc.irc.gui.logging;

import java.awt.Rectangle;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * TextPaneHandler publishes log records to text pane in the log viewer GUI.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version Aug 10, 2005 11:20:12 AM
 * @author Peyush Jain
 */

public class TextPaneHandler extends Handler
{
    private JTextPane fTextPane;
    
    /**
     * Constructor
     */
    public TextPaneHandler(JTextPane textPane)
    {
        fTextPane = textPane;
        configure();
    }
    
    /** 
     * Configure a TextPaneHandler from LogManager properties and/or 
     * default values as specified in the class javadoc.
     */ 
    private void configure()
    {        
        String cname = TextPaneHandler.class.getName();

        setLevel(HandlerUtil.getLevelProperty(cname + ".level", Level.INFO));
        setFilter(HandlerUtil.getFilterProperty(cname + ".filter", null));
        setFormatter(HandlerUtil.getFormatterProperty(cname + ".formatter",
                new SimpleFormatter()));
        try
        {
            setEncoding(HandlerUtil.getStringProperty(cname + ".encoding", null));
        }
        catch (Exception ex)
        {
            try
            {
                setEncoding(null);
            }
            catch (Exception ex2)
            {
                // doing a setEncoding with null should always work.
                // assert false;
            }
        }
    }

    /**
     * Inserts the given log record in the text pane
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    public void publish(LogRecord record)
    {
        if (isLoggable(record))
        {
            //get document from text pane
            Document doc = fTextPane.getDocument();
            String msg = null;
         
            try
            {
                //format the log record
                msg = this.getFormatter().format(record);
            }
            catch (Exception e)
            {
                this.reportError(null, e, ErrorManager.FORMAT_FAILURE);
            }
            
            if (msg != null)
            {
                try
                {
                    //insert the log record in the document
                    doc.insertString(doc.getLength(), msg, null);
                    fTextPane.scrollRectToVisible(new Rectangle(0, fTextPane.getHeight()-2, 1, 1));
                }
                catch (BadLocationException e)
                {
                    this.reportError(null, e, ErrorManager.WRITE_FAILURE);
                }
            }
        }
    }
    
    /**
     * @see java.util.logging.Handler#close()
     */
    public void close() throws SecurityException
    {
    }

    /**
     * @see java.util.logging.Handler#flush()
     */
    public void flush()
    {
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: TextPaneHandler.java,v $
// Revision 1.1  2005/09/01 19:14:29  pjain_cvs
// Adding to CVS.
//
//