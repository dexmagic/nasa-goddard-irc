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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorSupport;

import javax.swing.JComponent;
import javax.swing.JPanel;

import gov.nasa.gsfc.irc.data.InvalidValueException;
import gov.nasa.gsfc.irc.data.Values;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;

/**
 * Base class of all Swing based property editors.
 *
 */
public class SwingEditorSupport extends PropertyEditorSupport 
{
    /** 
     * Component which holds the editor. Subclasses are responsible for
     * instantiating and managing this panel.
     */
    protected JPanel fPanel;
    
    protected static final Dimension LARGE_DIMENSION = new Dimension(150,20);
    protected static final Dimension MEDIUM_DIMENSION = new Dimension(120,20);
    protected static final Dimension SMALL_DIMENSION = new Dimension(50,20);
    protected static final Insets BUTTON_MARGIN = new Insets(0,0,0,0);
    protected FieldDescriptor fDescriptor;

    /**
	 * Returns the panel responsible for rendering this PropertyEditor.
	 * 
	 * @return a graphical component
	 */
	public Component getCustomEditor()
	{
		return fPanel;
	}

	/** 
	 * Returns true indicating this editor supports a custom editor.
	 * 
	 * @return	true
	 * @see java.beans.PropertyEditor#supportsCustomEditor()
	 */
	public boolean supportsCustomEditor()
	{
		return true;
	}

	// layout stuff
	protected final void setAlignment(JComponent c)
	{
		c.setAlignmentX(Component.CENTER_ALIGNMENT);
		c.setAlignmentY(Component.CENTER_ALIGNMENT);
	}

	/**
	 * For property editors that must be initialized with values from the
	 * property descriptor. Since the editor can be shared among many properties
	 * this method must verify and update all internal state based on the 
	 * descriptor to prevent an incorrect state being carried over and applied
	 * to another property. This implementation does nothing.
	 */
	public void init(PropertyDescriptor descriptor)
	{
	}

	/**
	 * For property editors that must be initialized with values from a 
	 * descriptor. Since the editor can be shared among many properties
	 * this method must verify and update all internal state based on the 
	 * descriptor to prevent an incorrect state being carried over and applied
	 * to another property.
	 * 
	 * @param descriptor	the descriptor to apply to this property.
	 */
	public void setDescriptor(FieldDescriptor descriptor)
	{
		fDescriptor = descriptor;
	}

	/**
	 * Stop current editing session and return true if successful. This 
	 * implementation returns true.
	 * 
	 * @return true
	 */
	public boolean stopEditing()
	{
		return true;
	}

	/**
	 * Cancel editing. This implementation does nothing.
	 */
	public void cancelEditing()
	{
	}


    /**
     * Validates a value against all constraints specified in the 
     * descriptor set by the <code>setDescriptor</code> method. 
     * Returns true if value passes all constraints or if there
     * are no constraints.
     * 
     * @param value the value to validate
     * @return true if the value passes all constraints, false otherwise.
     */
    protected boolean validateValue(Object value)
	{
		boolean isValid = true;

		if (fDescriptor != null)
		{
			try
			{
				Values.validate(value, fDescriptor.getConstraints());
			}
			catch (InvalidValueException fieldex)
			{
				//TODO log exception
				//String errorstring = errorstring
				//			+ fieldex.getMessage()
				//			+ "\n";

				isValid = false;
			}
		}

		return isValid;
	}
}

//--- Development History ---------------------------------------------------
//
//	$Log: SwingEditorSupport.java,v $
//	Revision 1.3  2005/01/26 19:38:13  tames
//	More code cleanup and additional support for properties with choice
//	constraints.
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
 * @author  Tom Santos
 * @author  Mark Davidson
 */
