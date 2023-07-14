//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, Code 588 for 
//  the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.messages.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.description.DataMapEntryDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A FieldDescriptor describes a named field of a Message.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/01/07 20:42:56 $ 
 * @author Carl F. Hostetter   
**/

public class FieldDescriptor extends DataMapEntryDescriptor
{
	private Class fPropertyEditorClass;
	
	/**
	 * Constructs a new FieldDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new FieldDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		FieldDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		FieldDescriptor		
	**/
	
	public FieldDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
	}
	

	/**
	 * Constructs a new FieldDescriptor having the given name.
	 * 
	 * @param name The name of the new FieldDescriptor
	**/
	
	public FieldDescriptor(String name)
	{
		super(name);
	}

	/**
	 * Normally PropertyEditors will be found using the PropertyEditorManager.
	 * However if for some reason you want to associate a particular
	 * PropertyEditor with a given property, then you can do it with
	 * this method.
	 *
	 * @param propertyEditorClass  The Class for the desired PropertyEditor.
	 */
	public void setPropertyEditorClass(Class propertyEditorClass)
	{
		fPropertyEditorClass = propertyEditorClass;
	}

	/**
	 * Gets any explicit PropertyEditor Class that has been registered
	 * for this property.
	 *
	 * @return Any explicit PropertyEditor Class that has been registered
	 *		for this property.  Normally this will return "null",
	 *		indicating that no special editor has been registered,
	 *		so the PropertyEditorManager should be used to locate
	 *		a suitable PropertyEditor.
	 */
	public Class getPropertyEditorClass()
	{
		return fPropertyEditorClass;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: FieldDescriptor.java,v $
//  Revision 1.3  2005/01/07 20:42:56  tames
//  Added the capability to specify a custom property editor.
//
//  Revision 1.2  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
