//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/AbstractCharDataSelectorDescriptor.java,v 1.2 2006/04/27 19:46:21 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractCharDataSelectorDescriptor.java,v $
//  Revision 1.2  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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

package gov.nasa.gsfc.irc.data.selection.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.data.description.Dataml;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A CharDataSelectorDescriptor describes the means to select char data.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 19:46:21 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractCharDataSelectorDescriptor 
	extends AbstractDataSelectorDescriptor implements CharDataSelectorDescriptor
{
	private boolean fIsAscii = false;
	
	
	/**
	 * Constructs a new CharDataSelectorDescriptor that treats the char data it 
	 * selects from as ASCII according to the given flag.
	 *
	 * @param isAscii True if the selected char data is ASCII, false otherwise	
	**/
	
	public AbstractCharDataSelectorDescriptor(boolean isAscii)
	{
		fIsAscii = isAscii;
	}


	/**
	 * Constructs a new CharDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new 
	 * 		CharDataSelectorDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		CharDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		CharDataSelectorDescriptor		
	**/
	
	public AbstractCharDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}


	/**
	 * Constructs a (shallow) copy of the given AbstractCharDataSelectorDescriptor.
	 *
	 * @param descriptor The AbstractCharDataSelectorDescriptor to be copied
	**/
	
	protected AbstractCharDataSelectorDescriptor
		(AbstractCharDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fIsAscii = descriptor.fIsAscii;
	}
	

	/**
	 * Returns a (deep) copy of this AbstractCharDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this AbstractCharDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		AbstractCharDataSelectorDescriptor result = 
			(AbstractCharDataSelectorDescriptor) super.clone();
		
		result.fIsAscii = fIsAscii;
		
		return (result);
	}
	

	/**
	 * Returns true if this CharDataSelectorDescriptor is configured to select 
	 * only ASCII data, false otherwise (i.e., if it selects Unicode data).
	 *
	 * @return True if this CharDataSelectorDescriptor is configured to select 
	 * 		only ASCII data, false otherwise (i.e., if it selects Unicode data)		
	**/
	
	public boolean isAscii()
	{
		return (fIsAscii);
	}
	
	
	/** 
	 *  Returns a String representation of this CharDataSelectorDescriptor.
	 *
	 *  @return A String representation of this CharDataSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("CharDataSelectorDescriptor:");
		result.append("\n" + super.toString());
		result.append("\nIs ASCII: " + fIsAscii);
		
		return (result.toString());
	}
	
				
	/**
	 * Unmarshalls a CharDataSelectorDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Unmarshall the type attribute (if any).
		String type = getType();
		
		if (type != null)
		{
			if (type.equals(Dataml.ASCII))
			{
				fIsAscii = true;
			}
			else
			{
				fIsAscii = false;
			}
		}
		else
		{
			fIsAscii = true;
		}
	}
}
