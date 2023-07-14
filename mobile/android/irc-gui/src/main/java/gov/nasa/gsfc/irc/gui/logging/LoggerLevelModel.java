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

import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;

/**
 * Logger List Model populates the combo box model with logging levels.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version Aug 10, 2005 11:20:26 AM
 * @author Peyush Jain
 */

public class LoggerLevelModel extends DefaultComboBoxModel
{
    public LoggerLevelModel()
    {
        super(new Level[] { null, Level.OFF, Level.SEVERE, Level.WARNING,
                        Level.INFO, Level.CONFIG, Level.FINE, Level.FINER,
                        Level.FINEST });
    }
}

// --- Development History ---------------------------------------------------
//
// $Log: LoggerLevelModel.java,v $
// Revision 1.1  2005/09/01 17:18:38  pjain_cvs
// Adding to CVS.
//
//