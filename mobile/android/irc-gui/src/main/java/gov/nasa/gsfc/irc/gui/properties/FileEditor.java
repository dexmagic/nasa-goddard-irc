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

package gov.nasa.gsfc.irc.gui.properties;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import gov.nasa.gsfc.commons.app.App;
import gov.nasa.gsfc.irc.app.preferences.IrcPrefKeys;

/**
 * This class provides property editor support for specifing a java.io.File via
 * a JFileChooser dialog.
 *
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/02/14 17:27:47 $
 * @author John Higinbotham (Emergent Space Technologies)
 */

public class FileEditor extends SwingEditorSupport
{
    //---Support strings
    protected static final String EDIT_TEXT = "Select";
	protected static final String EDITOR_TITLE = "Select File";
	protected static final String APPROVE_TEXT = "Select";

	//---Editor
	protected FileChooser fFileChooser = new FileChooser();
	
    //---Panel showing the summary field and select button
    protected JPanel fLaunchPanel;
    protected JTextField fSummaryField;
   
	//---Is this the first time shown?
	protected boolean fFirstTimeVisible = true;

    /**
     * Property editor for selecting a file.
     *
     */
    public FileEditor()
    {
	    //---Create a text field and button to launch the "real" editor
	    fLaunchPanel = new JPanel(new GridBagLayout());
        fSummaryField = new JTextField();
        fSummaryField.setEditable(false);
        fLaunchPanel.setLayout(new BoxLayout(fLaunchPanel, BoxLayout.X_AXIS));
        fLaunchPanel.add(fSummaryField);
        JButton editButton = new JButton(EDIT_TEXT);
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                showEditor();
            }
        });
        fLaunchPanel.add(editButton);   
        
        fFileChooser.setApproveButtonText(APPROVE_TEXT);
        fFileChooser.setFileSelectionMode(FileChooser.FILES_AND_DIRECTORIES);
        //---Set default direction location
        File dir = App.getFileFromProperty(IrcPrefKeys.GUI_DIALOG_DEFAULT_DIRECTORY);
        if (dir != null)
        {
        	fFileChooser.setCurrentDirectory(dir);
        }
    }
    
    /**
     * Show the dialog window and wait for results
     */
    protected void showEditor()
    {
    	Object val = getValue();
    	
    	if (val instanceof File)
    	{
    		fFileChooser.setSelectedFile((File)val);	
    	}
    	
    	if (fFirstTimeVisible)
    	{
    		Runnable doVisible = new Runnable()
			{
    			public void run()
    			{
    				fFileChooser.showOpenDialog(null);
    			}
			};	
			SwingUtilities.invokeLater(doVisible);
			
			fFirstTimeVisible = false;
    	}
    	
    	//  The rest of the time, just go ahead and show it.
    	else
    	{
    		fFileChooser.showOpenDialog(null);
    	}
    }
    
    /**
     *  (non-Javadoc)
     * @see java.beans.PropertyEditor#getCustomEditor()
     */
    public Component getCustomEditor()
    {
        return fLaunchPanel;
    }
   
    /**
     *  (non-Javadoc)
     * @see java.beans.PropertyEditor#setValue(java.lang.Object)
     */
    public void setValue(Object value)
    {
    	super.setValue(value);
    	System.out.println("FileEditor.setValue():" + value);

        
        String s = "";
        if (value != null)
        {
        	s = value.toString();
        }
        fSummaryField.setText(s);
        
        // refresh the current directory to account for the
        // modification of any files in the directory
        fFileChooser.rescanCurrentDirectory();
    }
   
    
	/**
	 * A file chooser, based on the JFileChooser, that updates the summary
	 * field shown to the user for a property.
	 */
	protected class FileChooser extends JFileChooser
	{
		/**
		 *    This method is called when the approve button is pressed
		 *  in the file chooser.  In response, editing is stopped.
		 */
		public void approveSelection()
		{
			super.approveSelection();
			setValue(this.getSelectedFile());
		}

		/**
		 *    This method is called when the cancel button is pressed
		 *  in the file chooser.  In response, editing is stopped.
		 */
		public void cancelSelection()
		{
			super.cancelSelection();
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//$Log: FileEditor.java,v $
//Revision 1.3  2006/02/14 17:27:47  rfl
//allows directories and files to be selected
//
//Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//Initial version
//
//Revision 1.1  2005/01/08 23:35:26  jhiginbotham_cvs
//Add support for File property editing.
//
//
//
