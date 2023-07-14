//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: BinaryDataValueDescriptor.java,v $
//  Revision 1.1  2005/01/11 21:35:46  chostetter_cvs
//  Initial version
//
//  Revision 1.1  2004/12/13 04:14:32  chostetter_cvs
//  Message formatting: text and (initial form) binary
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

package gov.nasa.gsfc.irc.data.description;

import org.jdom.Element;

import gov.nasa.gsfc.commons.types.arrays.BitArray;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A BinaryDataValueDescriptor describes a binary data value 
 * representing a sequence of binary bits.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2005/01/11 21:35:46 $ 
 * @author Carl F. Hostetter 
**/

public class BinaryDataValueDescriptor extends DataValueDescriptor
{
	private BitArray fValue;
	
	
	/**
	 * Constructs a new BinaryDataValueDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new BinaryDataValueDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		BinaryDataValueDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		BinaryDataValueDescriptor		
	**/
	
	public BinaryDataValueDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element);
		
		fValue = new BitArray(toString());
	}
	

	/**
	 * Returns the BitArray representation of the binary value described by 
	 * this BinaryDataValueDescriptor.
	 *
	**/
	
	public BitArray toBitArray()
	{
		return (fValue);
	}
}
