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

import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * An editor for editing Rectangle.
 *
 * @version  %I% %G%
 * @author  Mark Davidson
 */
public class SwingRectangleEditor extends SwingEditorSupport {
    
    private JTextField xTF;
    private JTextField yTF;
    private JTextField widthTF;
    private JTextField heightTF;

    public SwingRectangleEditor() {
        xTF = new JTextField();
        xTF.setDocument(new NumberDocument());
        yTF = new JTextField();
        yTF.setDocument(new NumberDocument());
        widthTF = new JTextField();
        widthTF.setDocument(new NumberDocument());
        heightTF = new JTextField();
        heightTF.setDocument(new NumberDocument());
        
        fPanel = new JPanel();
        fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));
        fPanel.add(new JLabel("X: "));
        fPanel.add(xTF);
        fPanel.add(new JLabel("Y: "));
        fPanel.add(yTF);
        fPanel.add(new JLabel("Width: "));
        fPanel.add(widthTF);
        fPanel.add(new JLabel("Height: "));
        fPanel.add(heightTF);
    }
    
    public void setValue(Object value)  {
        super.setValue(value);
        
	if (value != null) {
	    Rectangle rect = (Rectangle)value;
	    xTF.setText(Integer.toString(rect.x));
	    yTF.setText(Integer.toString(rect.y));
	    widthTF.setText(Integer.toString(rect.width));
	    heightTF.setText(Integer.toString(rect.height));
	} else {
	    // null rect
	    xTF.setText("");
	    yTF.setText("");
	    widthTF.setText("");
	    heightTF.setText("");
	}
    }
    
    public Object getValue()  {
	try {
	    int x = Integer.parseInt(xTF.getText());
	    int y = Integer.parseInt(yTF.getText());
	    int width = Integer.parseInt(widthTF.getText());
	    int height = Integer.parseInt(heightTF.getText());
	    
	    return new Rectangle(x, y, width, height);
	} catch (NumberFormatException ex) {
	    // Fall out but return null
	}
	return null;
    }

}

//--- Development History ---------------------------------------------------
//
//	$Log: SwingRectangleEditor.java,v $
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
 * @author  Mark Davidson
 */
