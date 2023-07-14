//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/description/CounterDataSelectorDescriptor.java,v 1.1 2005/09/15 15:34:08 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: CounterDataSelectorDescriptor.java,v $
//  Revision 1.1  2005/09/15 15:34:08  chostetter_cvs
//  Added support for command counter value selection
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

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A CounterDataSelectorDescriptor describes the means to select the current value 
 * of a named, persistent counter that increments itself each time it is 
 * selected.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/15 15:34:08 $
 * @author Carl F. Hostetter
 */

public class CounterDataSelectorDescriptor extends AbstractDataSelectorDescriptor
{
	private int fInitialCount = 0;
	
	
	/**
	 * Constructs a new CounterDataSelectorDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new CounterDataSelectorDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		CounterDataSelectorDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		CounterDataSelectorDescriptor		
	**/
	
	public CounterDataSelectorDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given CounterDataSelectorDescriptor.
	 *
	 * @param descriptor The CounterDataSelectorDescriptor to be copied
	**/
	
	protected CounterDataSelectorDescriptor
		(CounterDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fInitialCount = descriptor.fInitialCount;
	}
	

	/**
	 * Returns a (deep) copy of this CounterDataSelectorDescriptor.
	 *
	 * @return A (deep) copy of this CounterDataSelectorDescriptor 
	**/
	
	public Object clone()
	{
		CounterDataSelectorDescriptor result = 
			(CounterDataSelectorDescriptor) super.clone();
		
		result.fInitialCount = fInitialCount;
		
		return (result);
	}
	
	
	/**
	 * Returns the initial count value of this CounterDataSelectorDescriptor.
	 *
	 * @return The initial count value of this CounterDataSelectorDescriptor
	**/
	
	public int getInitialCount()
	{
		return (fInitialCount);
	}
	
	
	/** 
	 *  Returns a String representation of this CounterDataSelectorDescriptor.
	 *
	 *  @return A String representation of this CounterDataSelectorDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("CounterDataSelectorDescriptor:");
		result.append("\n" + super.toString());
		result.append("\nInitial count = " + fInitialCount);
		
		return (result.toString());
	}
	
	
	/**
	 * Unmarshalls a CounterDataSelectorDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Unmarshall the initial count attribute (if any)
		fInitialCount = fSerializer.loadIntAttribute
			(Dataselml.A_INITIAL_COUNT, 0, fElement);
	}
}
