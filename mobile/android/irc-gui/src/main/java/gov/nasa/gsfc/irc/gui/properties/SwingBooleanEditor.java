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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;


/**
 * An editor which represents a boolean value. This editor is implemented
 * as a checkbox with the text of the check box reflecting the state of the
 * checkbox.
 *
 * @version 1.2 02/27/02
 * @author  Mark Davidson
 */
public class SwingBooleanEditor extends SwingEditorSupport
{

	private JCheckBox checkbox;

	public SwingBooleanEditor()
	{
		checkbox = new JCheckBox();
		checkbox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				if (evt.getStateChange() == ItemEvent.SELECTED)
				{
					setValue(Boolean.TRUE);
				}
				else
				{
					setValue(Boolean.FALSE);
				}
			}
		});
		fPanel = new JPanel();
		fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));
		fPanel.add(checkbox);
	}

	public void setValue(Object value)
	{
		super.setValue(value);
		if (value != null)
		{
			try
			{
				checkbox.setText(value.toString());
				if (checkbox.isSelected() != ((Boolean) value).booleanValue())
				{
					// Don't call setSelected unless the state actually changes
					// to avoid a loop.
					checkbox.setSelected(((Boolean) value).booleanValue());
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public Object getValue()
	{
		return new Boolean(checkbox.isSelected());
	}
}

//--- Development History ---------------------------------------------------
//
//	$Log: SwingBooleanEditor.java,v $
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
 * @author Mark Davidson
 */
