//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 
// for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.description.xml;

import java.beans.PropertyVetoException;
import java.io.IOException;

import org.jdom.Element;

import gov.nasa.gsfc.commons.xml.SubtreeMap;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.description.AbstractDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.DescriptorException;


/**
 * An AbstractXmlDescriptor is an abstract basis for all Descriptors that can be 
 * marshalled and unmarshalled to an from JDOM Elements (and this to and 
 * from XML representations).
 * 
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version	$Date: 2006/01/23 17:59:54 $
 * @author 	Troy Ames
**/

public abstract class AbstractXmlDescriptor extends AbstractDescriptor 
	implements XmlDescriptor
{
	//The directory allows a descriptor to find others by name. Currently this
	//is used to resolve reference in IML.
	transient protected DescriptorDirectory fDirectory;

	//Get a handle to the serializer to support xml marhshalling and unmarshalling.
	transient protected DescriptorSerializer fSerializer;
	
	//JDOM element that this object was built from, if loaded from XML.
	transient protected Element fElement;

	//The parent feature provides a handle to the parent of this descriptor. Not
	//all descriptor trees use this feature.
	protected Descriptor fParent;
	
	//---SubtreeMap
	private SubtreeMap fSubtreeMap;


	/**
	 *  Constructs a new XmlDescriptor having the given base name and no name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new XmlDescriptor
	 **/

	public AbstractXmlDescriptor(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new XmlDescriptor having the given base name and name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new XmlDescriptor
	 *  @param nameQualifier The name qualifier of the new XmlDescriptor
	 **/

	public AbstractXmlDescriptor(String name, String nameQualifier)
	{
		super(name, nameQualifier);
		
		fSerializer = DescriptorSerializer.getInstance();
	}
	
	
	/**
	 * Constructs a new XmlDescriptor having the given parent Descriptor and 
	 * belonging to the given DescriptorDirectory by unmarshalling from the given 
	 * JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new XmlDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new XmlDescriptor will 
	 * 		belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		XmlDescriptor		
	**/
	
	public AbstractXmlDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		this(null, null);
		
		if (parent != null)
		{
			try
			{
				setNameQualifier(parent.getFullyQualifiedName());
			}
			catch (PropertyVetoException ex)
			{
				
			}
		}
		
		fParent = parent;
		fDirectory = directory; 
		fElement	= element;
		
		fSerializer = DescriptorSerializer.getInstance();
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.XmlDescriptor#setSubtreeMap(gov.nasa.gsfc.commons.xml.SubtreeMap)
	 */
	
	public void setSubtreeMap(SubtreeMap subtreeMap)
	{
		fSubtreeMap = subtreeMap;
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.XmlDescriptor#getSubtreeMap()
	 */
	
	public SubtreeMap getSubtreeMap()
	{
		return (fSubtreeMap);
	}


	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.XmlDescriptor#getParent()
	 */
	
	public Descriptor getParent()
	{
		return (fParent);
	}
	

	/** 
	 *  Returns a String representation of this AbstractXmlDescriptor.
	 *
	 *  @return A String representation of this AbstractXmlDescriptor
	**/
	
	public String toString()
	{
		String result = getName();
		
		if (fElement != null)
		{
			result += ":\n" + fElement;
		}
		
		return (result);
	} 
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.XmlDescriptor#getElement()
	 */
	
	public Element getElement()
	{
		return (fElement);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.XmlDescriptor#xmlMarshall(org.jdom.Element)
	 */
	
	public void xmlMarshall(Element element)
	{
		// TODO Make this abstract and require every subclass to implement
	}
	
	/**
	 * Serialization support for writing out an instance of this class.
	 * 
	 * @param out the stream to write object out to.
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
	}

	/**
	 * Serialization support for reading in an instance of this class.
	 * 
	 * @param in the stream to read an object from.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException
	{
		in.defaultReadObject();
		
		try
		{
			fDirectory = Irc.getDescriptorFramework().getDirectory(null);
		}
		catch (DescriptorException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fSerializer = DescriptorSerializer.getInstance();
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractXmlDescriptor.java,v $
//  Revision 1.2  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.14  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.13  2005/04/20 14:34:44  tames_cvs
//  Added custom readObject and writeObject to support serialization of
//  Descriptors.
//
//  Revision 1.12  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.11  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
