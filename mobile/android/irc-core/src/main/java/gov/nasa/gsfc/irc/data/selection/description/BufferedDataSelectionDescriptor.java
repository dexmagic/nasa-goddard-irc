//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: BufferedDataSelectionDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.selection.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A BufferedDataSelectionDescriptor describes a means of buffering and then 
 * selecting data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class BufferedDataSelectionDescriptor 
	extends AbstractDataSelectionDescriptor
{
	private static final int DEFAULT_BUFFER_SIZE = 1024;
	
	private int fSize = 0;
	

	/**
	 * Default constructor of a new BufferedDataSelectionDescriptor.
	 *
	**/
	
	public BufferedDataSelectionDescriptor()
	{

	}
	

	/**
	 * Constructs a new BufferedDataSelectionDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by 
	 * unmarshalling from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		BufferedDataSelectionDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		BufferedDataSelectionDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		BufferedDataSelectionDescriptor		
	**/
	
	public BufferedDataSelectionDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given DataSelectionDescriptor.
	 *
	 * @param descriptor The DataSelectionDescriptor to be cloned
	**/
	
	protected BufferedDataSelectionDescriptor
		(BufferedDataSelectionDescriptor descriptor)
	{
		super(descriptor);
		
		fSize = descriptor.fSize;
	}
	

	/**
	 * Returns a (deep) copy of this DataSelectionDescriptor.
	 *
	 * @return A (deep) copy of this DataSelectionDescriptor
	**/
	
	public Object clone()
	{
		BufferedDataSelectionDescriptor result = 
			(BufferedDataSelectionDescriptor) super.clone();
		
		result.fSize = fSize;
		
		return (result);
	}
	
	
	/**
	 * Returns the size of the selection buffer specified by this 
	 * DataSelectionDescriptor (if any).
	 *
	 * @return The size of the selection buffer specified by this 
	 * 		DataSelectionDescriptor (if any)
	**/
	
	public int getSize()
	{
		return (fSize);
	}
	
	
	/** 
	 *  Returns a String representation of this DataSelectionDescriptor.
	 *
	 *  @return A String representation of this DataSelectionDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("BufferedDataSelectionDescriptor:\n" + 
			super.toString());

		result.append("\nBuffer size = " + fSize);
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this DataSelectionDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the buffer size attribute (if any).
		fSize = fSerializer.loadIntAttribute
			(Dataselml.A_SIZE, DEFAULT_BUFFER_SIZE, fElement);
		
		// Unmarshall the DataSelectionDescriptor (if any).
		DataSelectionDescriptor selection = (DataSelectionDescriptor) 
			fSerializer.loadSingleChildDescriptorElement(Dataselml.E_SELECTION, 
				Dataselml.C_DATA_SELECTION, fElement, this, fDirectory);
		
		if (selection != null)
		{
			fDataSelector = selection.getDataSelector();
		}
	}
}
