//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, 
//  Code 588 for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: ResponseDescriptor.java,v $
//  Revision 1.6  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.5  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.4  2004/08/09 17:27:43  tames_cvs
//  *** empty log message ***
//
//  Revision 1.3  2004/07/27 21:10:31  tames_cvs
//  Schema changes
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/06/30 20:44:22  tames_cvs
//  initial version
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

package gov.nasa.gsfc.irc.messages.description;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.devices.description.Iml;
import gov.nasa.gsfc.irc.devices.description.MessageInterfaceDescriptor;

/**
 * The class provides access to information describing a message response. <P>
 *
 * The object is built based on information contained in an IML XML file
 * which describes the insturment being interfaced with by IRC.
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			 $Date: 2004/10/14 15:16:51 $
 * @author			  John Higinbotham 
 * @author			  Troy Ames 
**/
public class ResponseDescriptor extends AbstractMessageDescriptor
{
	private boolean fIsFinal;  // True if final response in a sequence 
	private boolean fIsError;  // True if error response


	/**
	 * Constructs a new ResponseDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ResponseDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ResponseDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ResponseDescriptor		
	**/
	
	public ResponseDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new ResponseDescriptor having the given name.
	 * 
	 * @param name The name of the new ResponseDescriptor
	**/
	
	public ResponseDescriptor(String name)
	{
		super(name);
	}

	
	/**
	 * Get the message interface to which this response belongs. 
	 *
	 * @return Message interface to which this response belongs. 
	**/
	public MessageInterfaceDescriptor getMessageInterface()
	{
		return (MessageInterfaceDescriptor) fParent;
	}

	/**
	 * Determine if this is the final response in a series. 
	 *
	 * @return True if response is final in a sequence, False otherwise. 
	**/
	public boolean isFinal()
	{
		return fIsFinal;
	}

	/**
	 * Specify if this is the final response in a series. 
	 *
	 * @param value True if response is final in a sequence, False otherwise. 
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setIsFinal(boolean value)
	{
		fIsFinal = value;
	}

	/**
	 * Determine if this is an error response. 
	 *
	 * @return True if response indicates an error, False otherwise.
	**/
	public boolean isError()
	{
		return fIsError;
	}

	/**
	 * Specifiy if this is an error response. 
	 *
	 * @param value True if response indicates an error, False otherwise.
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void setIsError(boolean value)
	{
		fIsError = value;
	}

	/**
	 * Unmarshall descriptor from XML. 
	**/
	private void xmlUnmarshall()
	{
		fIsFinal = fSerializer.loadBooleanAttribute(Iml.A_IS_FINAL, false, fElement);
		fIsError = fSerializer.loadBooleanAttribute(Iml.A_IS_ERROR, false, fElement);
	}

	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into.
	**/
	public void xmlMarshall(Element element)
   	{
		super.xmlMarshall(element);
		fSerializer.storeAttribute(Iml.A_IS_FINAL, fIsFinal, element);
		fSerializer.storeAttribute(Iml.A_IS_ERROR, fIsError, element);
	}
}
