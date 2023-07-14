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

package gov.nasa.gsfc.irc.gui.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

/**
 *    This panel presents the user with an editable text field displaying
 *  the currently selected file and a button which allows the user to
 *  launch a file chooser to change that file.
 *
 *  TBD:  In the future, we might want to support the addition/removal
 *        of multiple file filters using the chooser.  Right now it
 *        it is just extra work that no one would use.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/08/05 19:50:46 $
 *  @author	    Ken Wootton
 */
public class FileField extends JPanel
{
	//  GUI strings
	protected static final String SELECT_BUT_TXT = "Select";
    protected static final String NULL_FILE_TXT = "";

    private static final String CHOOSER_BUT_TXT = "...";

	private static final int FILE_COLUMNS = 5;

	//  Use a static file chooser for all fields.  All file fields use a modal
	//  file chooser within the AWT thread so this shouldn't be a problem.
	//  The benefits are that it saves on memory and the initial start up time.
	private static JFileChooser sFileChooser = null;

	//  Cache the following chooser information on a per field basis.
	private String fChooserTitle = null;
	private File fChooserDirectory = null;
	private int fChooserSelectionMode = JFileChooser.FILES_ONLY;
	private FileFilter fChooserFilter = null;
	private File fRootDirectory = null;

	//  GUI components
	private JTextField fFileField = null;
	private JButton fChooserBut = null;

	/**
	 *    Create a new file field component.
	 */
	public FileField()
	{
		setLayout(new GridBagLayout());

		//  File text field
		fFileField = new JTextField(FILE_COLUMNS);
        LayoutUtil.gbConstrain(this, fFileField, 0, 0, 1, 0, 1, 1,
                               GridBagConstraints.EAST,
                               GridBagConstraints.HORIZONTAL);

		//  File chooser button
		fChooserBut = new JButton(CHOOSER_BUT_TXT);
		fChooserBut.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					browseForFile();
				}
			});
        LayoutUtil.gbConstrain(this, fChooserBut, 1, 0, 0, 0, 1, 1,
                               GridBagConstraints.EAST,
                               GridBagConstraints.NONE);


		//  Set up the file chooser
		if (sFileChooser == null)
		{
			sFileChooser = new JFileChooser();
			sFileChooser.setDialogTitle(fChooserTitle);
			sFileChooser.setMultiSelectionEnabled(false);
			sFileChooser.setFileSelectionMode(fChooserSelectionMode);
		}

		setChooserFilter(sFileChooser.getAcceptAllFileFilter());
	}

	/**
	 *    Set the title of the file chooser.
	 *
	 *  @param chooserTitle  the title of the file chooser
	 */
	public void setChooserTitle(String chooserTitle)
	{
		fChooserTitle = chooserTitle;
	}

	/**
	 *    Get the title of the file chooser.
	 *
	 *  @return  the title of the file chooser
	 */
	public String getChooserTitle()
	{
		return fChooserTitle;
	}
	
	/**
	 *    Set the file selection mode for the file chooser.
	 *
	 *  @param chooserSelectionMode  one of the selection modes advertised
	 *                               by the <code>JFileChooser</code>
	 *
	 *  @see JFileChooser
	 */
	public void setChooserSelectionMode(int chooserSelectionMode)
	{
		fChooserSelectionMode = chooserSelectionMode;
	}

	/**
	 *    Get the file selection mode for the file chooser.
	 *
	 *  @return  the current selection mode, which must be one of the 
	 *           selection modes advertised by the <code>JFileChooser</code>
	 *
	 *  @see JFileChooser
	 */
	public int getChooserSelectionMode()
	{
		return fChooserSelectionMode;
	}

	/**
	 *    Set the file filter for the file chooser.  Note that each newly
	 *  specified filter replaces the last.  Setting this to 'null'
	 *  will cause the file chooser to accept all files.
	 *
	 *  @param chooserFilter  the file filter for the file chooser
	 */
	public void setChooserFilter(FileFilter chooserFilter)
	{
		fChooserFilter = chooserFilter;

		//  A 'null' filter is not really allowed.
		if (fChooserFilter == null)
		{
			fChooserFilter = sFileChooser.getAcceptAllFileFilter();
		}
	}

	/**
	 *    Get the file filter for the file chooser.
	 *
	 *  @return  the file filter for the file chooser
	 *
	 *  @see #setChooserFilter(FileFilter)
	 */
	public FileFilter getChooserFilter()
	{
		return fChooserFilter;
	}

	/**
	 *    Set the directory to use within the file chooser when no file
	 *  has been selected yet.  Note that the chooser will always default
	 *  to the directory of the selected file, if there is one.
	 *
	 *  @param dir  the current directory for the file chooser
	 */
	public void setChooserDirectory(File dir)
	{
		fChooserDirectory = dir;
	}

	/**
	 *    Get the directory to use within the file chooser when no file
	 *  has been selected yet.
	 *
	 *  @return  the current directory for the file chooser
	 */
	public File getChooserDirectory()
	{
		return fChooserDirectory;
	}

	/**
	 *  Sets the directory used to compute the relative path of the selected file.
	 *  If the root directory is defined, the selected file path will be
	 *  displayed as a relative path relative to this directory.
	 *  If the root directory is not defined, an absolute file path will
	 *  be displayed.
	 *
	 *  @param dir  the root directory for the file chooser
	 */
	public void setRootDirectory(File dir)
	{
		fRootDirectory = dir;
	}

	/**
	 *  Gets the directory used to compute the relative path of the selected file.
	 *
	 *  @return  the root directory for the file chooser
	 */
	public File getRootDirectory()
	{
		return fRootDirectory;
	}

	/**
	 *    Set the currently selected file in the field.
	 *
	 *  @param selectedFile  the selected file to display
	 */
	public void setSelectedFile(File selectedFile)
	{
		//  Set the text within the file text field.
		if (selectedFile != null)
		{
			if (fRootDirectory != null)
			{
				final String back = "..";
				
				String text = gov.nasa.gsfc.commons.system.io.FileUtil.getRelativePath(selectedFile, fRootDirectory);
				if (text != null && text.indexOf(back + File.separator + back + File.separator + "..") == -1)
				{
					// Display the path of the file relative to the root path
					fFileField.setText(text);					
				}				
				else
				{
					// No reasonably close relative path exists. Display the absolute path.
					fFileField.setText(selectedFile.getPath());
				}
			}
			else
			{
				// Display the absolute path
				fFileField.setText(selectedFile.getPath());
			}
		}
		else
		{
			fFileField.setText(NULL_FILE_TXT);
		}
	}

	/**
	 *    Get the currently selected file in the field.  Note that this
	 *  field allows user input so it is recommended that the file
	 *  be checked for validity.
	 *
	 *  @return  the selected file
	 */
	public File getSelectedFile()
	{
		File selectedFile = null;
		String path = fFileField.getText().trim();

		//  Only make the file if the user specified something.
		if (path.length() > 0)
		{
			if (fRootDirectory != null && !(new File(path)).isAbsolute())
			{
				selectedFile = new File(fRootDirectory, path);
			}
			else
			{
				selectedFile = new File(path);
			}
		}

		return selectedFile;
	}

    /**
     *    Get the text field that displays the selected file
     *
     *  @return fileField the text field
     */
    public JTextField getTextField()
    {
        return fFileField;
    }

    /**
     *    Get the button that is used to launch the file chooser
     *
     *  @return button the button
     */
    public JButton getButton()
    {
        return fChooserBut;
    }

	/**
	 *    Set the enabled state of the component.  This will also
	 *  set the enabled state of all of its subcomponents.
	 *
	 *  @param enabledState  whether or not the component should be
	 *                       enabled
	 */
	public void setEnabled(boolean enabledState)
	{
		super.setEnabled(enabledState);

		fFileField.setEnabled(enabledState);
		fChooserBut.setEnabled(enabledState);
	}

	/**
	 *    Add a listener to the document of the file field.
	 *
	 *  @param listener  the document listener to add
	 */
	public void addDocumentListener(DocumentListener listener)
	{
		fFileField.getDocument().addDocumentListener(listener);
	}

	/**
	 *    Remove a listener to the document of the file field.
	 *
	 *  @param listener  the document listener to remove
	 */
	public void removeDocumentListener(DocumentListener listener)
	{
		fFileField.getDocument().removeDocumentListener(listener);
	}

	/**
	 *    Get the file chooser that is launched from the file field's
	 *  button.  It is recommended that the title and current directory
	 *  of the chooser be customized accoording to need.
	 *
	 *  @return  the file chooser
	 */
	protected JFileChooser getFileChooser()
	{
		return sFileChooser;
	}

	/**
	 *    Perform and needed set up and then use the file chooser to browse 
	 *  for the desired file.
	 */
	protected void browseForFile()
	{
		File fieldFile = getSelectedFile();

		//  Restore the chooser specific properties.
		sFileChooser.setDialogTitle(fChooserTitle);
		sFileChooser.setFileSelectionMode(fChooserSelectionMode);

		//  If it changed, restore the file filter for this field.
		if (!fChooserFilter.equals(sFileChooser.getFileFilter()))
		{
			sFileChooser.resetChoosableFileFilters();
			sFileChooser.setFileFilter(fChooserFilter);
		}

		//  Set the file and, by association, the directory
		//  in the chooser, if it is valid.
		if (fieldFile != null && fieldFile.exists())
		{
			sFileChooser.setSelectedFile(fieldFile);
		}

		//  Otherwise, try to use the cached/ desired current
		//  directory.
		else if (fChooserDirectory != null &&
				 !sFileChooser.getCurrentDirectory().equals(
				 fChooserDirectory))
		{
			sFileChooser.setCurrentDirectory(fChooserDirectory);
		}

		//  We are all set up.  Select the file.
		chooseFile();

		//  Cache the current directory so that we can
		//  restore it when needed.
		fChooserDirectory = sFileChooser.getCurrentDirectory();
	}

	/**
	 *    Use the file chooser to actually select the file.  This method
	 *  allows subclasses to alter the actual viewing and selection
	 *  of the chooser without having to worry about the work done
	 *  in <code>browseForFile</code>.
	 *
	 *  @see browseForFile
	 */
	protected void chooseFile()
	{
		//  Let the user choose a file.  If one is chosen,
		//  put its name in the file field.
		if (sFileChooser.showDialog(this, SELECT_BUT_TXT) ==
			JFileChooser.APPROVE_OPTION)
		{
			setSelectedFile(sFileChooser.getSelectedFile());
		}	
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   8    IRC       1.7         8/1/2002 5:00:50 PM  Ken Wootton     This no longer
//        use the IrcJFileChooser.  It has also been updated to statically cache the
//        file chooser for use by all fields.
//   7    IRC       1.6         4/12/2002 1:33:04 PM Uri Kerbel      Updated to
//        enable flexible subclassing
//   6    IRC       1.5         2/12/2002 8:17:51 PM Uri Kerbel      Plugged in the
//        IrcJFileChooser which is more flexible than the standard JFileChooser as
//        it remembers previous directories and refreshes the contents to account
//        for underlying changes
//   5    IRC       1.4         2/5/2002 4:59:42 PM  Ken Wootton     Added the
//        concept of a current directory.
//   4    IRC       1.3         1/24/2002 6:28:44 PM Ken Wootton     Modified to
//        show the full path.
//   3    IRC       1.2         1/21/2002 6:37:39 PM Ken Wootton     This now
//        watches for null files better.
//   2    IRC       1.1         12/14/2001 6:22:44 PMKen Wootton     
//   1    IRC       1.0         12/13/2001 12:21:43 PMKen Wootton     
//  $
