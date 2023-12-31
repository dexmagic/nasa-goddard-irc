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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;


/**
 * Modified from the standard color swatch chooser.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version @(#)SwatchChooserPanel.java	1.3 1.3
 */
public class SwatchChooserPanel extends AbstractColorChooserPanel {

  SwatchPanel swatchPanel;
  RecentSwatchPanel recentSwatchPanel;
  MouseListener mainSwatchListener;
  MouseListener recentSwatchListener;
  SwingColorEditor       chooser;
  ChooserComboPopup popup;

  private static String recentStr = UIManager.getString("ColorChooser.swatchesRecentText");

  public SwatchChooserPanel(SwingColorEditor c, ChooserComboPopup p) {
    super();
    this.chooser = c;
    this.popup = p;
  }

  public String getDisplayName() {
    return UIManager.getString("ColorChooser.swatchesNameText");
  }

  public Icon getSmallDisplayIcon() {
    return null;
  }

  public Icon getLargeDisplayIcon() {
    return null;
  }

  /**
   * The background color, foreground color, and font are already set to the
   * defaults from the defaults table before this method is called.
   */									
  public void installChooserPanel(JColorChooser enclosingChooser) {
    super.installChooserPanel(enclosingChooser);
  }

  protected void buildChooser() {
      
    JPanel superHolder = new JPanel();
    superHolder.setLayout(new BoxLayout(superHolder, BoxLayout.Y_AXIS)); // new BorderLayout());
    swatchPanel =  new MainSwatchPanel();
    swatchPanel.getAccessibleContext().setAccessibleName(getDisplayName());

    recentSwatchPanel = new RecentSwatchPanel();
    recentSwatchPanel.getAccessibleContext().setAccessibleName(recentStr);

    mainSwatchListener = new MainSwatchListener();
    swatchPanel.addMouseListener(mainSwatchListener);
    recentSwatchListener = new RecentSwatchListener();
    recentSwatchPanel.addMouseListener(recentSwatchListener);


    JPanel mainHolder = new JPanel(new BorderLayout());
    Border border = new CompoundBorder( new LineBorder(Color.black),
					new LineBorder(Color.white) );
    mainHolder.setBorder(border); 
    mainHolder.add(swatchPanel, BorderLayout.CENTER);
    //	mainHolder.add(recentSwatchPanel, BorderLayout.NORTH);

    JPanel recentHolder = new JPanel( new BorderLayout() );
    recentHolder.setBorder(border);
    recentHolder.add(recentSwatchPanel, BorderLayout.CENTER);

    superHolder.add(recentHolder); // , BorderLayout.NORTH);
    superHolder.add(Box.createRigidArea(new Dimension(0,3)));
    superHolder.add( mainHolder); // , BorderLayout.CENTER );



    //	JPanel recentLabelHolder = new JPanel(new BorderLayout());
    //	recentLabelHolder.add(recentHolder, BorderLayout.CENTER);
    //	JLabel l = new JLabel(recentStr);
    //	l.setLabelFor(recentSwatchPanel);
    //	recentLabelHolder.add(l, BorderLayout.NORTH);
    //	JPanel recentHolderHolder = new JPanel(new BorderLayout()); // was centerlayout
    //	recentHolderHolder.setBorder(new EmptyBorder(2,10,2,2));
    //	recentHolderHolder.add(recentLabelHolder);

    //        superHolder.add( recentHolderHolder, BorderLayout.NORTH );	
	
    add(superHolder);
	
  }

  public void uninstallChooserPanel(JColorChooser enclosingChooser) {
    super.uninstallChooserPanel(enclosingChooser);
    swatchPanel.removeMouseListener(mainSwatchListener);
    swatchPanel = null;
    mainSwatchListener = null;
    removeAll();  // strip out all the sub-components
  }

  public void updateChooser() {

  }

  class RecentSwatchListener extends MouseAdapter implements Serializable {
    public void mousePressed(MouseEvent e) {
      Color color = recentSwatchPanel.getColorForLocation(e.getX(), e.getY());
      //	    getColorSelectionModel().setSelectedColor(color);
      chooser.setValue(color);
      popup.setVisible(false);

    }
  }


  class MainSwatchListener extends MouseAdapter implements Serializable {
    public void mousePressed(MouseEvent e) {
      Color color = swatchPanel.getColorForLocation(e.getX(), e.getY());
      //	    getColorSelectionModel().setSelectedColor(color);
      chooser.setValue(color);
      recentSwatchPanel.setMostRecentColor(color);
      popup.setVisible(false);
    }
  }

}



class SwatchPanel extends JPanel {
  protected Color[] colors;
  protected Dimension swatchSize = new Dimension(13,13);
  protected Dimension numSwatches;
  protected Dimension gap;

  public SwatchPanel() {
    initValues();
    initColors();
    setToolTipText(""); // register for events
    setOpaque(true);
    setBackground(Color.gray);
    setFocusable(false);
  }

  public boolean isFocusAble() {
    return false;
  }

  protected void initValues() {
  }

  public void paintComponent(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0,0,getWidth(), getHeight());
    for (int row = 0; row < numSwatches.height; row++) {
      for (int column = 0; column < numSwatches.width; column++) {
	g.setColor( getColorForCell(column, row) ); 
	int x = column * (swatchSize.width + gap.width);
	int y = row * (swatchSize.height + gap.height);
	g.fillRect( x, y, swatchSize.width, swatchSize.height);
	g.setColor(Color.black);
	g.drawLine( x+swatchSize.width-1, y, x+swatchSize.width-1, y+swatchSize.height-1);
	g.drawLine( x, y+swatchSize.height-1, x+swatchSize.width-1, y+swatchSize.width-1);
      }
    }
  }

  public Dimension getPreferredSize() {
    int x = numSwatches.width * (swatchSize.width + gap.width) -1;
    int y = numSwatches.height * (swatchSize.height + gap.height) -1;
    return new Dimension( x, y );
  }

  protected void initColors() {
  }

  public String getToolTipText(MouseEvent e) {
    Color color = getColorForLocation(e.getX(), e.getY());
    return color.getRed()+", "+ color.getGreen() + ", " + color.getBlue();
  }

  public Color getColorForLocation( int x, int y ) {
    int column = x / (swatchSize.width + gap.width);
    int row = y / (swatchSize.height + gap.height);
    return getColorForCell(column, row);
  }

  private Color getColorForCell( int column, int row) {
      return colors[ (row * numSwatches.width) + column ]; // (STEVE) - change data orientation here
  }
}

class RecentSwatchPanel extends SwatchPanel {
  protected void initValues() {
    //        swatchSize = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize");
    swatchSize = new Dimension(13,13);
    numSwatches = new Dimension( 6, 1 );
    gap = new Dimension(1, 1);
  }


  protected void initColors() {
    Color defaultRecentColor = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor");
    int numColors = numSwatches.width * numSwatches.height;
	
    colors = new Color[numColors];
    for (int i = 0; i < numColors ; i++) {
      colors[i] = defaultRecentColor;
    } 
  }

  public void setMostRecentColor(Color c) {

    System.arraycopy( colors, 0, colors, 1, colors.length-1);
    colors[0] = c;
    repaint();
  }
}

class MainSwatchPanel extends SwatchPanel {
  protected void initValues() {
    //        swatchSize = UIManager.getDimension("ColorChooser.swatchesSwatchSize");
    swatchSize = new Dimension(13,13);
    //	numSwatches = new Dimension( 31, 10 );
    numSwatches = new Dimension(6,6);
    gap = new Dimension(1, 1);
  }

  protected void initColors() {
    int[] rawValues = initRawValues();
    int numColors = rawValues.length / 3;
	
    colors = new Color[numColors];
    for (int i = 0; i < numColors ; i++) {
      colors[i] = new Color( rawValues[(i*3)], rawValues[(i*3)+1], rawValues[(i*3)+2] );
    }
  }

  private int[] initRawValues() {

    int[] rawValues = {     255, 255, 255,
			    192,192,192,
			    128,128,128,
			    64,64,64,
			    0,0,0,
			    255,0,0,
			    100,100,100,
			    255,175,175,
			    255,200,0,
			    255,255,0,
			    0,255,0,
			    255,0,255,
			    0,255,255,
			    0,0,255,  // repeat here
			    255, 255, 255,
			    192,192,192,
			    128,128,128,
			    64,64,64,
			    0,0,0,
			    255,0,0,
			    100,100,100,
			    255,175,175,
			    255,200,0,
			    255,255,0,
			    0,255,0,
			    255,0,255,
			    0,255,255,
			    0,0,255,  // repeat here
			    100,100,100,
			    255,175,175,
			    255,200,0,
			    255,255,0,
			    0,255,0,
			    255,0,255,
			    0,255,255,
			    0,0,255,
    };
    return rawValues;
  }
}

//--- Development History ---------------------------------------------------
//
//	$Log: SwatchChooserPanel.java,v $
//	Revision 1.1  2005/01/07 21:01:09  tames
//	Relocated.
//	
//	

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 * 
 * @author  Steve Wilson
 */
