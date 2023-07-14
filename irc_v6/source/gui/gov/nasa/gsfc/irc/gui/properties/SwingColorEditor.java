//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//  This class is a modified version of code originally developed by 
//  Sun Microsystems. The copyright notice for the original code is given at 
//  the end of the file.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalComboBoxIcon;

import gov.nasa.gsfc.irc.gui.util.IconUtil;


/**
 * Swing version of a Color property editor.
 * 
 * @version 1.3 02/27/02
 * @author Tom Santos
 * @author Mark Davidson
 */
public class SwingColorEditor extends SwingEditorSupport
{
	private JTextField fRgbValue;
	private JButton fColorChooserButton;
	private Color fColor = Color.black;
	private ChooserComboButton fColorChooserCombo;

	public SwingColorEditor()
	{
		createComponents();
		addComponentListeners();
	}

	private void addComponentListeners()
	{
		fRgbValue.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					setAsText(getAsText());
				}
				catch (IllegalArgumentException e2)
				{
					JOptionPane.showMessageDialog(fPanel.getParent(), e2
							.toString());
				}
			}
		});

		fColorChooserButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fColor = JColorChooser.showDialog(fPanel.getParent(),
					"Color Chooser", fColor);
				setValue(fColor);
			}
		});
	}

	public boolean isPaintable()
	{
		return true;
	}

	/**
	 * Paints a representation of the value into a given area of screen.
	 */
	public void paintValue(Graphics g, Rectangle rect)
	{
		Color oldColor = g.getColor();
		g.setColor(Color.black);
		g.drawRect(rect.x, rect.y, rect.width - 3, rect.height - 3);
		g.setColor(fColor);
		g.fillRect(rect.x + 1, rect.y + 1, rect.width - 4, rect.height - 4);
		g.setColor(oldColor);
	}

	private void createComponents()
	{
		UIDefaults table = UIManager.getDefaults();

		table.put("beaninfo.ColorIcon", 
			IconUtil.makeIcon("resources/icons/ColorIcon.gif"));
		table.put("beaninfo.ColorPressedIcon", 
			IconUtil.makeIcon("resources/icons/ColorPressedIcon.gif"));
		
		Icon colorIcon = UIManager.getIcon("beaninfo.ColorIcon");
		Icon colorPressedIcon = UIManager.getIcon("beaninfo.ColorPressedIcon");

		fRgbValue = new JTextField();
		fColorChooserCombo = new ChooserComboButton();
		fColorChooserButton = new JButton(colorIcon);

		Dimension d = new Dimension(colorIcon.getIconWidth(), colorIcon
				.getIconHeight());
		fRgbValue.setPreferredSize(SwingEditorSupport.MEDIUM_DIMENSION);
		fRgbValue.setMaximumSize(SwingEditorSupport.MEDIUM_DIMENSION);
		fRgbValue.setMinimumSize(SwingEditorSupport.MEDIUM_DIMENSION);
		fColorChooserCombo.setPreferredSize(SwingEditorSupport.SMALL_DIMENSION);
		fColorChooserCombo.setMaximumSize(SwingEditorSupport.SMALL_DIMENSION);
		fColorChooserCombo.setMinimumSize(SwingEditorSupport.SMALL_DIMENSION);

		fColorChooserButton.setPressedIcon(colorPressedIcon);
		fColorChooserButton.setToolTipText("press to bring up color chooser");
		fColorChooserButton.setMargin(SwingEditorSupport.BUTTON_MARGIN);
		fColorChooserButton.setBorderPainted(false);
		fColorChooserButton.setContentAreaFilled(false);

		setAlignment(fRgbValue);
		setAlignment(fColorChooserCombo);
		setAlignment(fColorChooserButton);
		fPanel = new JPanel();
		fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));
		fPanel.add(fRgbValue);
		fPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		fPanel.add(fColorChooserCombo);
		fPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		fPanel.add(fColorChooserButton);
		fPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	}

	public String getJavaInitializationString()
	{
		return "new java.awt.Color(" + getAsText() + ")";
	}

	public String getAsText()
	{
		return fRgbValue.getText();
	}

	public void setAsText(String s) throws java.lang.IllegalArgumentException
	{
		int c1 = s.indexOf(',');
		int c2 = s.indexOf(',', c1 + 1);
		if (c1 < 0 || c2 < 0)
		{
			// Invalid string.
			throw new IllegalArgumentException(s);
		}
		try
		{
			int r = Integer.parseInt(s.substring(0, c1));
			int g = Integer.parseInt(s.substring(c1 + 1, c2));
			int b = Integer.parseInt(s.substring(c2 + 1));
			setValue(new Color(r, g, b));
		}
		catch (Exception ex)
		{
			throw new IllegalArgumentException(s);
		}
	}

	public void setValue(Object value)
	{
		super.setValue(value);
		editorChangeValue(value);
	}

	public void editorChangeValue(Object value)
	{
		Color c = (Color) value;
		if (c == null)
		{
			fRgbValue.setText("                  ");
			fColorChooserCombo.setBackground(fPanel.getBackground());
			return;
		}
		fColor = c;
		// set the combo rect foreground color
		// and the textfield to the rgb value
		fRgbValue.setText("" + c.getRed() + "," + c.getGreen() + ","
				+ c.getBlue());
		fColorChooserCombo.setBackground(c);
	}

	public SwingColorEditor getSwingColorEditor()
	{
		return this;
	}

	// for testing

	// custom combolike rect button
	class ChooserComboButton extends JButton
	{
		ChooserComboPopup popup;

		Icon comboIcon = new MetalComboBoxIcon();

		public ChooserComboButton()
		{
			super("");
			popup = new ChooserComboPopup(getSwingColorEditor());
			addMouseListener(new PopupListener());
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Insets insets = getInsets();

			int width = getWidth() - (insets.left + insets.right);
			int height = getHeight() - (insets.top + insets.bottom);

			if (height <= 0 || width <= 0)
			{
				return;
			}

			int left = insets.left;
			int top = insets.top;
			int right = left + (width - 1);
			int bottom = top + (height - 1);

			int iconWidth = 0;
			int iconLeft = right;

			// Paint the icon
			if (comboIcon != null)
			{
				iconWidth = comboIcon.getIconWidth();
				int iconHeight = comboIcon.getIconHeight();
				int iconTop = 0;
				iconTop = (top + ((bottom - top) / 2)) - (iconHeight / 2);
				comboIcon.paintIcon(this, g, iconLeft, iconTop);
			}

		}

		class PopupListener extends MouseAdapter
		{
			public void mouseReleased(MouseEvent e)
			{
				// bring up ChooserComboPopup
				// bring it up at the comopnent height location!
				JComponent c = (JComponent) e.getComponent();
				popup.show(c, 0, 0);
			}
		}
	}

	public static void main(String argv[])
	{
		JFrame f = new JFrame();
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				JFrame frame = (JFrame) e.getSource();
				frame.dispose();
				System.exit(0);
			}
		});

		SwingColorEditor editor = new SwingColorEditor();
		f.getContentPane().add(editor.getCustomEditor());
		f.pack();
		f.show();
	}
}

//--- Development History ---------------------------------------------------
//
//	$Log: SwingColorEditor.java,v $
//	Revision 1.2  2005/01/20 08:08:14  tames
//	Changes to support choice descriptors and message editing bug fixes.
//	
//	Revision 1.1  2005/01/07 21:01:09  tames
//	Relocated.
//	
//	

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 * 
 * @author Tom Santos @author Mark Davidson
 */
