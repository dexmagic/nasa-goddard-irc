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

package gov.nasa.gsfc.irc.gui.actions;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gov.nasa.gsfc.commons.system.io.ExtFileFilter;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.app.preferences.IrcPrefKeys;
import gov.nasa.gsfc.irc.gui.GuiFactory;

/**
 * Loads one or more GUI files selected by the user via JFileChooser dialog.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/31 16:26:36 $
 * @author 	Troy Ames
 */
public class LoadGuiAction extends AbstractAction
{
	private static final String CLASS_NAME = LoadGuiAction.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	private static final String DIALOG_TITLE = "Load GUI";
	
	private static JFileChooser fFileChooser = null;
	private String fDirectory = 
		Irc.getPreference(IrcPrefKeys.GUI_DIALOG_DEFAULT_DIRECTORY);
	
	/**
     * Defines LoadGuiAction object with a default description string 
     * and default icon.
	 */
	public LoadGuiAction()
	{
		super();
	}

    /**
     * Invoked when a load GUI action occurs.
     */
	public synchronized void actionPerformed(ActionEvent event)
	{
		if (fFileChooser == null)
		{
			initializeFileChooser();
		}
		
		String title = ((Container) event.getSource()).getName();
		fFileChooser.setName(title);
		
		// Schedule the dialog selection
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				int status = fFileChooser.showOpenDialog(null);
				
				if (status == JFileChooser.APPROVE_OPTION)
				{
					File[] files = fFileChooser.getSelectedFiles();
					GuiFactory builder = Irc.getGuiFactory();
					
					for (int i = 0; i < files.length; i++)
					{
						try
						{
							Component gui = builder.render(files[i].toURL());
							
							if (gui instanceof JFrame)
							{
								((JFrame) gui).pack();
							}
							
							gui.setVisible(true);
						}
						catch (Exception e)
						{
							String message = 
								"Exception rendering frame " + files[i];

							sLogger.logp(Level.WARNING, CLASS_NAME, 
									"actionPerformed", message, e);
						}
						
						fDirectory = files[i].getAbsolutePath();
					}
				}
				
				fFileChooser = null;
			}
		});
	}
	
	/**
	 * Initialize JFileChooser dialog.
	 */
	private void initializeFileChooser()
	{
		fFileChooser = new JFileChooser();
		fFileChooser.setFileFilter(ExtFileFilter.XML_FILTER);
		fFileChooser.setMultiSelectionEnabled(true);
		fFileChooser.setDialogTitle(DIALOG_TITLE);		
		if (fDirectory != null)
		{
			fFileChooser.setCurrentDirectory(new File(fDirectory));
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: LoadGuiAction.java,v $
//  Revision 1.6  2006/03/31 16:26:36  smaher_cvs
//  Added descriptive title and separate starting directories for device and gui files.
//
//  Revision 1.5  2005/01/10 23:09:23  tames_cvs
//  Updated to reflect GuiBuilder name change to GuiFactory.
//
//  Revision 1.4  2004/12/01 21:50:58  chostetter_cvs
//  Organized imports
//
//  Revision 1.3  2004/09/23 20:11:42  tames_cvs
//  Loaded GUI description files are now packed before being set visible.
//
//  Revision 1.2  2004/09/04 13:31:06  tames
//  *** empty log message ***
//
//  Revision 1.1  2004/08/29 19:08:36  tames
//  Initial version
//
