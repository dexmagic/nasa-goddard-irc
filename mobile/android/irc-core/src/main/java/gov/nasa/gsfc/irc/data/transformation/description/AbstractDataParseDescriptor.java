//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataParseDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

package gov.nasa.gsfc.irc.data.transformation.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A AbstractDataParseDescriptor describes a means of parsing data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class AbstractDataParseDescriptor extends AbstractIrcElementDescriptor 
	implements DataParseDescriptor
{
	protected DataParserDescriptor fDataParser;
	
	
	/**
	 * Constructs a new AbstractDataParseDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new AbstractDataParseDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataParseDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataParseDescriptor		
	**/
	
	public AbstractDataParseDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Datatransml.N_PARSES);
	}
	

	/**
	 * Constructs a (shallow) copy of the given AbstractDataParseDescriptor.
	 *
	 * @param descriptor The AbstractDataParseDescriptor to be copied
	**/
	
	protected AbstractDataParseDescriptor(AbstractDataParseDescriptor descriptor)
	{
		super(descriptor);
		
		fDataParser = descriptor.fDataParser;
	}
	

	/**
	 * Returns a (deep) copy of this AbstractDataParseDescriptor.
	 *
	 * @return A (deep) copy of this AbstractDataParseDescriptor
	**/
	
	public Object clone()
	{
		AbstractDataParseDescriptor result = 
			(AbstractDataParseDescriptor) super.clone();
		
		result.fDataParser = (DataParserDescriptor) fDataParser.clone();
		
		return (result);
	}
	
	
	/**
	 * Sets the DataParserDescriptor associated with this 
	 * 		AbstractDataParseDescriptor to the given DataParserDescriptor.
	 *
	 * @param parser The new DataParserDescriptor associated with this 
	 * 		AbstractDataParseDescriptor 
	**/
	
	public void setDataParser(DataParserDescriptor parser)
	{
		fDataParser = parser;
	}
	

	/**
	 * Returns the DataParserDescriptor associated with this 
	 * 		AbstractDataParseDescriptor (if any).
	 *
	 * @return The DataParserDescriptor associated with this 
	 * 		AbstractDataParseDescriptor (if any) 
	**/
	
	public DataParserDescriptor getDataParser()
	{
		return (fDataParser);
	}
	

	/** 
	 *  Returns a String representation of this AbstractDataParseDescriptor.
	 *
	 *  @return A String representation of this AbstractDataParseDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataParseDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fDataParser != null)
		{
			result.append("\nData Parser: " + fDataParser);
		}
		
		return (result.toString());
	}
}
