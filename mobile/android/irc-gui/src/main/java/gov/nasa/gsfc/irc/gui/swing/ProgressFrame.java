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

package gov.nasa.gsfc.irc.gui.swing;

import java.awt.BorderLayout;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.gui.util.WindowUtil;

/**
 * This class implements a simple progress frame with a status field and 
 * progress bar.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/05/04 15:12:23 $
 * @author 	Troy Ames
 */
public class ProgressFrame extends JFrame
{
	/** Property key for getting startup image file */
	public static final String PROGRESS_IMAGE_KEY = "irc.manager.startupImage";

	private JProgressBar fProgressBar;
	private JLabel fLabel;
	private JPanel fPanel;
	private int fMaxValue = 0;
	private int fMinValue = 0;
	private String DEFAULT_PROGRESS_IMAGE = "resources/images/StartupImage.gif";

	public ProgressFrame()
	{
		String imagePath = Irc.getPreference(PROGRESS_IMAGE_KEY);
		int frameWidth = 360;
		int frameHeight = 200;

		if (imagePath == null)
		{
			imagePath = DEFAULT_PROGRESS_IMAGE;
		}
		
		URL imageUrl = Irc.getResource(imagePath);

		// Create panel
		fPanel = new JPanel();
		setSize(frameWidth, frameHeight);
		fPanel.setLayout(new BorderLayout(5, 5));
		getContentPane().add(fPanel);
		
		// Create and size image
		JLabel imageLabel = new JLabel();
		ScaleableImageIcon icon = new ScaleableImageIcon(imageUrl);
		icon.setWidth(frameWidth - 10);		

		imageLabel.setIcon(icon);
		fPanel.add(imageLabel, BorderLayout.CENTER);

		// Create status Panel
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout(2, 2));
		
		// Create status label
		fLabel = new JLabel("Waiting to start.");
		statusPanel.add(fLabel, BorderLayout.NORTH);
		
		// Create progress bar
		fProgressBar = new JProgressBar();
		fProgressBar.setIndeterminate(true);
		fProgressBar.setStringPainted(true);
		fProgressBar.setBorderPainted(true);
		
		statusPanel.add(fProgressBar, BorderLayout.SOUTH);
		
		fMaxValue = fProgressBar.getMaximum();
		fMinValue = fProgressBar.getMinimum();
		
		// Configure frame
		fPanel.add(statusPanel, BorderLayout.SOUTH);
		fPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setUndecorated(true);
		WindowUtil.centerFrame(this);
	}
	
	/**
	 * Set the percent complete for the progress display. 
	 * Note that this number is between 0.0 and 1.0.
	 * 
	 * @param percent the percent complete
	 */
	public void setPercentComplete(float percent)
	{
		if (percent < 0.0f || percent > 1.0f)
		{
			throw new IllegalArgumentException(
					"percent must be between 0.0 and 1.0");
		}

		float floatValue = ((float) (fMaxValue - fMinValue)) * percent;
		final int newValue = Math.min(Math.round(floatValue), 100);
		fProgressBar.setIndeterminate(false);

		if (fProgressBar.getValue() < newValue)
		{
			setValue(newValue);
		}

		if (newValue >= 100)
		{
			final JFrame frame = this;
			SwingUtilities.invokeLater(new Runnable() 
			{
				public void run()
				{
					frame.dispose();
				}
			});
		}
	}
	
	private void setValue(final int value)
	{

		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
					fProgressBar.setValue(value);
				}
			});
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the value of the progress label.
	 * 
	 * @param text the progress string or null
	 */
	public void setString(String text)
	{
		fLabel.setText(text);
	}
	
	private class ScaleableImageIcon extends ImageIcon
	{
	    /**
	     * Creates an ScaleableImageIcon from the specified URL. 
	     *
	     * @param location the URL for the image
	     */
		public ScaleableImageIcon(URL location)
		{
			super(location);			
		}
		
		/**
		 * Set the desired height of the scaled image. The aspect ratio of 
		 * original image is maintained.
		 * 
		 * @param height The height to set.
		 */
		public void setHeight(int height)
		{
			Image image = getImage();
			setImage(image.getScaledInstance(-1, height, Image.SCALE_DEFAULT));
		}
		
		/**
		 * Set the desired width of the scaled image. The aspect ratio of 
		 * original image is maintained.
		 * 
		 * @param width The width to set.
		 */
		public void setWidth(int width)
		{
			Image image = getImage();
			setImage(image.getScaledInstance(width, -1, Image.SCALE_DEFAULT));
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: ProgressFrame.java,v $
//	Revision 1.9  2006/05/04 15:12:23  tames
//	Changed the default splash screen file name.
//	
//	Revision 1.8  2006/05/03 18:39:37  tames
//	Reverted default gif back since most projects do not have access to the
//	HAWC gif.
//	
//	Revision 1.6  2005/01/20 00:51:33  jhiginbotham_cvs
//	Make property static.
//	
//	Revision 1.5  2004/12/22 04:43:09  tames
//	Layout changes as well as changing the default behavior
//	to scale the startup image.
//	
//	Revision 1.4  2004/12/01 21:50:58  chostetter_cvs
//	Organized imports
//	
//	Revision 1.3  2004/09/05 13:28:35  tames
//	Properties and preferences clean up
//	
//	Revision 1.2  2004/09/04 13:33:02  tames
//	*** empty log message ***
//	
//	Revision 1.1  2004/08/31 22:03:43  tames
//	Initial Version
//	
