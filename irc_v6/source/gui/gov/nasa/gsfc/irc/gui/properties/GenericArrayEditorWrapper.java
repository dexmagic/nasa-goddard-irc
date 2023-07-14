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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import weka.gui.GenericArrayEditor;

import gov.nasa.gsfc.commons.types.arrays.ArrayHelpers;

/**
 * Allows editing of indexed properties (e.g., arrays). It is designed to work
 * with arrays of arbitrary types (assuming the type has a property editor), but
 * currently only works with primitive types (e.g., short, int, float, etc) and
 * some other types (e.g., BitArray).
 * <p>
 * This class is actually a wrapper around <code>GenericArrayEditor</code>,
 * which comes from an <a href="http://www.cs.waikato.ac.nz/~ml/weka/"> open
 * source project </a>. The <code>GenericArrayEditor</code> class was modified
 * to work with the SwingIntegerEditor and also to fix a bug when dealing with
 * empty arrays of primitive types.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2005/01/11 21:35:46 $
 * @author smaher
 */

public class GenericArrayEditorWrapper implements PropertyEditor
{
    /** The editor being wrapped */
    private GenericArrayEditor fGenericArrayEditor = new GenericArrayEditor();
    
    /** The panel showing the summary field and edit button */
    private JPanel fLaunchPanel;
    private JTextField fSummaryField;
    
    private static final String EDIT_BTN_TEXT = "Edit Array";
    private static final String EDITOR_TITLE = "Array Editor";
    
    /** The array */
    private Object fValue;
    
    /** Handles property change notification */
    private PropertyChangeSupport fSupport = new PropertyChangeSupport(this);
    
    public GenericArrayEditorWrapper()
    {
	    // Create a text field and button to launch the "real" editor
	    
	    fLaunchPanel = new JPanel(new GridBagLayout());
        fSummaryField = new JTextField();
        fSummaryField.setEditable(false);

        fLaunchPanel.setLayout(new BoxLayout(fLaunchPanel, BoxLayout.X_AXIS));
        
        fLaunchPanel.add(fSummaryField);
        JButton editButton = new JButton(EDIT_BTN_TEXT);
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                showEditor();
            }
        });
               
        
        fLaunchPanel.add(editButton);   
    }
    
    /**
     * Show the dialog window and wait for results
     */
    protected void showEditor()
    {
        fGenericArrayEditor = new GenericArrayEditor();
        fGenericArrayEditor.setValue(fValue);
        
        ArrayEditorDialog dialog = new ArrayEditorDialog(fLaunchPanel.getParent(), EDITOR_TITLE);

	    dialog.showDialog();
	    if (!(dialog.isCancelled()))
	    {
	        setValue(getValue());
	        firePropertyChangeEvent();
	    }    
    }
    
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#getAsText()
     */
    public String getAsText()
    {
        return fGenericArrayEditor.getAsText();
    }
    
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#getCustomEditor()
     */
    public Component getCustomEditor()
    {
        return fLaunchPanel;
    }
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#getValue()
     */
    public Object getValue()
    {
        return fGenericArrayEditor.getValue();
    }
    
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#isPaintable()
     */
    public boolean isPaintable()
    {
        return fGenericArrayEditor.isPaintable();
    }
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#setAsText(java.lang.String)
     */
    public void setAsText(String text) throws IllegalArgumentException
    {
        fGenericArrayEditor.setAsText(text);
    }
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#setValue(java.lang.Object)
     */
    public void setValue(Object value)
    {
        fValue = value;
        // Update value in summary field
        fSummaryField.setText(ArrayHelpers.arrayToString(value));

    }
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#supportsCustomEditor()
     */
    public boolean supportsCustomEditor()
    {
        return true;
    }
    
    /**
     * Convenience function for firing property change events
     */
    public void firePropertyChangeEvent()
    {
        fSupport.firePropertyChange("", null, null);
    }
    
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        fSupport.addPropertyChangeListener(listener);
    }
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#getJavaInitializationString()
     */
    public String getJavaInitializationString()
    {
        return fGenericArrayEditor.getJavaInitializationString();
    }
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        fSupport.removePropertyChangeListener(listener);
    }
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#paintValue(java.awt.Graphics, java.awt.Rectangle)
     */
    public void paintValue(Graphics gfx, Rectangle box)
    {
        fGenericArrayEditor.paintValue(gfx, box);
    }
    
    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#getTags()
     */
    public String[] getTags()
    {
        return fGenericArrayEditor.getTags();
    }

    /**
     * Dialog box to contain array editor
     * Pretty ugly .. and doesn't resize.  Someone
     * who knows swing better should clean this up!
     */
    class ArrayEditorDialog extends JDialog {
        JPanel pane;
        JButton okButton;
    
        Border border = null;
        boolean cancel = false;
        
        public ArrayEditorDialog(Component c, String title){
            
            super(JOptionPane.getFrameForComponent(c), title, true);
//            Frame frame = JOptionPane.getFrameForComponent(c);

            Container contentPane = getContentPane();

            pane = new JPanel();
//            frame.add(pane);
            
            pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
            setLocation(100,100);
            
//            contentPane.setLayout(new BorderLayout());
            okButton = new JButton("OK"); // new BorderTracker(pane);
    
            
            getRootPane().setDefaultButton(okButton);
            okButton.setActionCommand("OK");
    
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    hide();
                }
            });
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setActionCommand("cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancel = true;
                    hide();
                }
            });
    
            pane.add(fGenericArrayEditor.getCustomEditor()); 
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
            btnPanel.add(okButton);
            btnPanel.add(cancelButton);
            pane.add(btnPanel);
            contentPane.add(pane, BorderLayout.CENTER);
            
            pack();

            
            this.addWindowListener(new Closer());
            this.addComponentListener(new DisposeOnClose());
        }
        
        public void showDialog(){
            this.cancel = false;
            this.show();
        }
        
        public boolean isCancelled(){
            return this.cancel;
        }
        
        class Closer extends WindowAdapter {
            public void windowClosing(WindowEvent e) {
                Window w = e.getWindow();
                w.hide();
            }
        }
        
        class DisposeOnClose extends ComponentAdapter {
            public void componentHidden(ComponentEvent e) {
                Window w = (Window)e.getComponent();
                w.dispose();
            }
        }
    }
}



//--- Development History  ---------------------------------------------------
//
//$Log: GenericArrayEditorWrapper.java,v $
//Revision 1.3  2005/01/11 21:35:46  chostetter_cvs
//Initial version
//
//Revision 1.2  2005/01/10 20:14:29  smaher_cvs
//Trying to get things to resize.  (Didn't!)
//
//Revision 1.1  2005/01/07 21:01:09  tames
//Relocated.
//
//Revision 1.1  2005/01/05 20:58:43  smaher_cvs
//Initial checkin - wrapper around weka.gui.GenericArrayEditor in lib/weka/weka-gsfc.jar
//