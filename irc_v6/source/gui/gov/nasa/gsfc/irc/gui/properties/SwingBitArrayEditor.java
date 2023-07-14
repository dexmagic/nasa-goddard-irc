//=== File Prolog ============================================================
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

import gov.nasa.gsfc.commons.types.arrays.BitArray;
import gov.nasa.gsfc.irc.gui.util.LayoutUtil;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 *  This property editor provides the ability to edit 'BitArray'
 *  values. 
 * <p>
 * This editor also supports value constraints defined in a descriptor set by 
 * <code>setFieldDescriptor</code>.
 * <P>The code is based on <code>BitArrayEditor</code>.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2005/07/01 18:33:16 $
 *  @author	    Ken Wootton
 *  @author	    Steve Maher
 */
public class SwingBitArrayEditor extends ChoiceEditorSupport
{
  
	//  Title
	private static final String EDITOR_TITLE = "Bit Array Editor";

	private static final String EDIT_BTN_TEXT = "Edit";
	//  Number of check boxes allowed in a row.
	private static final int NUM_BOXES_PER_ROW = 8;

	//  Spacing between check boxes.
	private static final int CHECK_BOX_INSET = 0;

	//  Message indicating a 'null' value.
	private static final String NULL_VALUE_MSG =
		"'null' bit array value encountered.  Bit array values CANNOT be " +
		"'null'.";

	//  GUI elements.
	//private JPanel fDialogPanel = new JPanel(new GridBagLayout());
	private JCheckBox[] fCheckBoxArr = null;
	private BitArrayEditorDialog dialog;

	/** Text field where the results are shown **/
    private JTextField fTextfield;
    
	/**
	 *    
	 */
	public SwingBitArrayEditor()
	{
	    // Create a text field and button to launch the "real" editor
	    
		Document textDocument = getDocument();
	    fPanel = new JPanel(new GridBagLayout());
        fTextfield = new JTextField();
        //fTextfield.setEditable(false);

		if (textDocument != null)
		{
			fTextfield.setDocument(textDocument);
		}
		
        fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));
        
        fPanel.add(fTextfield);
        JButton editButton = new JButton(EDIT_BTN_TEXT);
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                showEditor();
                stopEditing();
            }
        });
               
        fTextfield.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	setAsText(fTextfield.getText());
            }
        });
        
		fTextfield.addFocusListener(new FocusListener()
		{
			/**
			 * Invoked when the component gains the keyboard focus. This
			 * implementation selects the content of the text field for editing.
			 */
			public void focusGained(FocusEvent e)
			{
				//fTextfield.selectAll();
			}

			/**
			 * Invoked when the component loses the keyboard focus. This
			 * implementation ignores the event.
			 */
			public void focusLost(FocusEvent e)
			{
                stopEditing();
			}
		});
        
        fPanel.add(editButton);
         
	}


    /**
	 *  Set the working value
	 *
	 *  @param value  the current value of the cell
	 */
	public void setValue(Object value)
	{
		super.setValue(value);
		if (value != null)
		{
		    fTextfield.setText(((BitArray)value).toString(8));
		}
		else
		{
		    fTextfield.setText("");
		}
	}

    /* (non-Javadoc)
     * @see java.beans.PropertyEditor#setAsText(java.lang.String)
     */
    public void setAsText(String text) throws IllegalArgumentException
    {
        setValue(new BitArray(text));
    }

	/**
	 * @return the BitArray represented by the editor's checkboxes
	 */
	protected BitArray getBitArrayFromEditor()
	{
		BitArray value = null;

		if (fCheckBoxArr != null)
		{
			value = new BitArray(fCheckBoxArr.length);

			//  Set or clear each bit according to the current value of the	
			//  check box.
			for (int i = 0; i < fCheckBoxArr.length; i++)
			{
				if (fCheckBoxArr[i].isSelected())
				{
					value.set(i);
				}
				else
				{
					value.clear(i);
				}
			}
		}

		return value;
	}

    /**
     * Show the dialog window for editting bit arrays
     */
    protected void showEditor()
    {
//	    if (dialog == null)
//	    {
	        dialog = new BitArrayEditorDialog(fPanel.getParent(), EDITOR_TITLE);
//	    }
	    dialog.showDialog();
	    if (!(dialog.isCancelled()))
	    {
	        setValue(getBitArrayFromEditor());
	    }
	    
		    
    }

	/**
	 * Stops the editing session by setting the value to the current contents of
	 * the editor.
	 * 
	 * @return true
	 */
	public boolean stopEditing()
	{
		if (fIsChoice)
		{
			super.stopEditing();
		}
		else
		{
			setAsText(fTextfield.getText());
		}

		return true;
	}

	/**
	 * Cancel editing by restoring the value.
	 */
	public void cancelEditing()
	{
		if (fIsChoice)
		{
			super.stopEditing();
		}
		else
		{
			fTextfield.setText(getValue().toString());
		}
	}

	/**
	 * Get a Document for specific to this Class. Subclasses should 
	 * over ride this method if they restrict input into the textfield.
	 * This implementation returns a text document that will reject any 
	 * characters that are 0 or 1 digits.
	 * 
	 * @return a Document
	 */
	protected Document getDocument()
	{
		return new PlainDocument()
		{
			private final char ZERO_CHAR = '0';
			private final char ONE_CHAR = '1';

			public void insertString(int offset, String str, AttributeSet atts)
					throws BadLocationException
			{
				if (str != null && str.length() > 0)
				{
					// Check that the string is a digit or a character
					if (str.charAt(0) == ZERO_CHAR || str.charAt(0) == ONE_CHAR)
					{
						super.insertString(offset, str, atts);
					}
				}
			}
		};
	}


    /**
     * The bit array dialog box
     */
    class BitArrayEditorDialog extends JDialog {
        JPanel pane;
        JButton okButton;

        Border border = null;
        boolean cancel = false;
        
        public BitArrayEditorDialog(Component c, String title){
            
            super(JOptionPane.getFrameForComponent(c), title, true);
            Container contentPane = getContentPane();
            pane = new JPanel();
            pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
            setLocation(100,100);
            
            contentPane.setLayout(new BorderLayout());
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

            //pane.add(new JLabel("Numbers are bit positions (e.g., LSB is 0)"));
            pane.add(createCheckBoxesPanel()); 
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
            btnPanel.add(okButton);
            btnPanel.add(cancelButton);
            pane.add(btnPanel);
            contentPane.add(pane, BorderLayout.CENTER);
            pack();
            this.addWindowListener(new Closer());
            this.addComponentListener(new DisposeOnClose());
        }
        
        public JPanel createCheckBoxesPanel()
        {
            JPanel cbPanel = new JPanel(new GridBagLayout());
            BitArray bitArrayValue = (BitArray) getValue();
            
            //  Figure out how many check boxes we need.
            int numBits = bitArrayValue.length();
            
            //  Initialize the row and column.  Start the column on the right.
            int curRow = 0;
            int curColumn = NUM_BOXES_PER_ROW - 1;
            
            //  Create and lay out the check boxes.
            fCheckBoxArr = new JCheckBox[numBits];
            for (int i = 0; i < numBits ; i++)
            {
                //  Create and place the check box.  The text is shown above
                //  the check box.
                fCheckBoxArr[i] = new JCheckBox(String.valueOf(i), 
                        bitArrayValue.get(i));
                fCheckBoxArr[i].setVerticalTextPosition(SwingConstants.TOP);
                fCheckBoxArr[i].setHorizontalTextPosition(SwingConstants.CENTER);
                fCheckBoxArr[i].setFocusPainted(false);
                
                LayoutUtil.gbConstrain(cbPanel, fCheckBoxArr[i], 
                        curColumn, curRow, 0, 0, 1, 1, 
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE, CHECK_BOX_INSET);
                
                //  Move a column to the left.  If it is filled, go to the
                //  next row.
                curColumn--;
                if (curColumn < 0)
                {
                    curRow++;
                    curColumn = NUM_BOXES_PER_ROW - 1;
                }
            }
            return cbPanel;
        }

        
        public BitArray showDialog(){
            this.cancel = false;
            this.show();
            return null; // border should be ok
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
//  $Log: SwingBitArrayEditor.java,v $
//  Revision 1.8  2005/07/01 18:33:16  smaher_cvs
//  Organized imports.
//
//  Revision 1.7  2005/07/01 17:58:40  smaher_cvs
//  Added call to setAsText() in focusLost() record changes when the user
//  simply leaves the edit field (consistent with the other property editors).
//
//  Revision 1.6  2005/02/03 07:04:35  tames
//  Changed focus method not to select all when the editor is selected.
//
//  Revision 1.5  2005/01/31 14:44:07  smaher_cvs
//  Fixed bug where the bit editor dialog wasn't saving results because stopEditing()
//  was called before showEditor().
//
//  Revision 1.4  2005/01/26 22:01:55  tames
//  Added capability to directly bit array in text field and added support for
//  a BitArray selection list.
//
//  Revision 1.3  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//  Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2005/01/07 21:01:09  tames
//  Relocated.
//
//  Revision 1.3  2005/01/05 20:57:24  smaher_cvs
//  Added setAsText() to work with GenericArrayEditor
//
//  Revision 1.2  2004/12/21 20:06:28  smaher_cvs
//  Create dialog each time to easily get latest value.
//
//  Revision 1.1  2004/12/17 21:09:30  smaher_cvs
//  Initial checkin
//

