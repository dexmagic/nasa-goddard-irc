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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;


/**
 * A property editor for a swing enumerated type. Handles the case in which the
 * PropertyDescriptor has a value for "enumerationValues".
 * Note: the init() method must be called before the set/get methods can be
 * called.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public class SwingEnumEditor extends SwingEditorSupport implements ActionListener {

    public JComboBox combobox;

    public void setValue(Object value) {
        super.setValue(value);

        // Set combo box if it's a new value. We want to reduce number
        // of extraneous events.
        EnumeratedItem item = (EnumeratedItem)combobox.getSelectedItem();
        if (value != null && !value.equals(item.getValue()))  {
            for (int i = 0; i < combobox.getItemCount(); ++i ) {
                item = (EnumeratedItem)combobox.getItemAt(i);
                if (item.getValue().equals(value)) {
                    // XXX - hack! Combo box shouldn't call action event
                    // for setSelectedItem!!
                    combobox.removeActionListener(this);
                    combobox.setSelectedItem(item);
                    combobox.addActionListener(this);
                    return;
                }
            }
        }
    }

    /**
     * Initializes this property editor with the enumerated items. Instances
     * can be shared but there are issues.
     * <p>
     * This method does a lot of jiggery pokery since enumerated
     * types are unlike any other homogenous types. Enumerated types may not
     * represent the same set of values.
     * <p>
     * One method would be to empty the list of values which would have the side
     * effect of firing notification events. Another method would be to recreate
     * the combobox.
     */
    public void init(PropertyDescriptor descriptor) {
        Object[] values = (Object[])descriptor.getValue( "enumerationValues" );
        if (values != null) {
            if (combobox == null)  {
                combobox = new JComboBox();

                fPanel = new JPanel();
                fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));
                fPanel.add(combobox);
            } else {
                // Remove action listener to reduce extra events.
                combobox.removeActionListener(this);
                combobox.removeAllItems();
            }

            for ( int i = 0; i < values.length; i += 3 ) {
                combobox.addItem(new EnumeratedItem((Integer)values[i+1], (String)values[i] ) );
            }

            combobox.addActionListener(this);
        }
    }

    /**
     * Event is set when a combo selection changes.
     */
    public void actionPerformed(ActionEvent evt)  {
        EnumeratedItem item = (EnumeratedItem)combobox.getSelectedItem();
        if (item != null && !getValue().equals(item.getValue()))  {
            setValue(item.getValue());
        }
    }

    /**
     * Object which holds an enumerated item plus its label.
     */
    private class EnumeratedItem  {
        private Integer value;
        private String name;

        public EnumeratedItem(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String toString() {
            return name;
        }
    }
}

//--- Development History ---------------------------------------------------
//
//	$Log: SwingEnumEditor.java,v $
//	Revision 1.3  2005/04/04 15:40:59  chostetter_cvs
//	Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//	
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
