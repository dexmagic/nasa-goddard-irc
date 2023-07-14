//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/state/DataStateDeterminantDescriptor.java,v 1.1 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataStateDeterminantDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
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

package gov.nasa.gsfc.irc.data.state;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.selection.description.AbstractDataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.Dataselml;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataStateDeterminantDescriptor describes the determinant of the state of 
 * data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public class DataStateDeterminantDescriptor extends AbstractDataSelectionDescriptor 
{
	/**
	 * Constructs a new DataStateDeterminantDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DataStateDeterminantDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DataStateDeterminantDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DataStateDeterminantDescriptor		
	**/
	
	public DataStateDeterminantDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataStateDeterminantDescriptor.
	 *
	 * @param descriptor The DataStateDeterminantDescriptor to be copied
	**/
	
	protected DataStateDeterminantDescriptor
		(DataStateDeterminantDescriptor descriptor)
	{
		super(descriptor);
	}
	

	/**
	 * Returns a (deep) copy of this DataStateDeterminantDescriptor.
	 *
	 * @return A (deep) copy of this DataStateDeterminantDescriptor 
	**/
	
	public Object clone()
	{
		DataStateDeterminantDescriptor result = 
			(DataStateDeterminantDescriptor) super.clone();
		
		return (result);
	}
	

	/** 
	 *  Returns a String representation of this DataStateDeterminantDescriptor.
	 *
	 *  @return A String representation of this DataStateDeterminantDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataStateDeterminantDescriptor:");
		result.append("\n" + super.toString());

		return (result.toString());
	}


	/**
	  * Unmarshalls this DataStateDeterminantDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the determinant DataSelectionDescriptor (if any).
		DataSelectionDescriptor selection = (DataSelectionDescriptor) fSerializer.
			loadSingleChildDescriptorElement(Dataselml.E_SELECTION, 
				Dataselml.C_DATA_SELECTION, fElement, this, fDirectory);
		
		if (selection != null)
		{
			fDataSelector = selection.getDataSelector();
		}
	}
}
