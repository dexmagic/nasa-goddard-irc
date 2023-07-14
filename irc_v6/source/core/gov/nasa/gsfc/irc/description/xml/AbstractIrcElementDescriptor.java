//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;

import gov.nasa.gsfc.commons.types.namespaces.HasFullyQualifiedName;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.description.Dataml;
import gov.nasa.gsfc.irc.description.Descriptor;


/**
 * An AbstractIrcElementDescriptor is the basis for all Descriptors of IRC Objects. 
 * 
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version  $Date: 2006/01/23 17:59:54 $
 * @author John Higinbotham   
**/

public abstract class AbstractIrcElementDescriptor extends AbstractXmlDescriptor 
	implements IrcElementDescriptor
{
	private static final String CLASS_NAME = 
		AbstractIrcElementDescriptor.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private String fTypeNamespace = Dataml.N_DATA;
	
	private Map fParametersByName;
	
	// This is the full name by which this descriptor is accessible in the
	// directory. Currently this is only used when building from XML.
	private String fDisplayName;
	
	private String fType;
	private Class fClass;
	private String fClassName;
	
	private String fToolTipText;
	private String fShortDescription;
	

	/**
	 *  Constructs an anonymous IrcElementDescriptor.
	 * 
	 **/

	public AbstractIrcElementDescriptor()
	{
		this(null, null);
	}
	
	
	/**
	 *  Constructs a new IrcElementDescriptor having the given base name and no name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new IrcElementDescriptor
	 **/

	public AbstractIrcElementDescriptor(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new IrcElementDescriptor having the given base name and name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new IrcElementDescriptor
	 *  @param nameQualifier The name qualifier of the new IrcElementDescriptor
	 **/

	public AbstractIrcElementDescriptor(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
	
	
	/**
	 * Constructs a new IrcElementDescriptor having the given parent Descriptor and 
	 * belonging to the given DescriptorDirectory by unmarshalling from the given 
	 * JDOM Element within the indicated namespace.
	 *
	 * @param parent The parent Descriptor of the new IrcElementDescriptor
	 * @param directory The DescriptorDirectory to which the new 
	 * 		IrcElementDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		IrcElementDescriptor		
	 * @param typeNamespace The type namespace to which the new 
	 * 		IrcElementDescriptor should belong		
	**/
	
	public AbstractIrcElementDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element, String typeNamespace)
	{
		super(parent, directory, element);
		
		if (typeNamespace != null)
		{
			fTypeNamespace = typeNamespace;
		}

		fParametersByName = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	
	
	/**
	 * Constructs a a (shallow) copy of the given AbstractIrcElementDescriptor.
	 *
	 * @param descriptor An AbstractIrcElementDescriptor
	**/
	
	protected AbstractIrcElementDescriptor(AbstractIrcElementDescriptor descriptor)
	{
		this (descriptor.getName(), descriptor.getNameQualifier());
		
		fTypeNamespace = descriptor.fTypeNamespace;
		fParametersByName = descriptor.fParametersByName;
		
		fDisplayName = descriptor.fDisplayName;
		
		fType = descriptor.fType;
		fClass = descriptor.fClass;
		fClassName = descriptor.fClassName;
		
		fToolTipText = descriptor.fToolTipText;
		fShortDescription = descriptor.fShortDescription;
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#clone()
	 */
	
	public Object clone()
	{
		AbstractIrcElementDescriptor result = 
			(AbstractIrcElementDescriptor) super.clone();
		
		result.fTypeNamespace = fTypeNamespace;
		
		if (fParametersByName != null)
		{
			result.fParametersByName = new LinkedHashMap(fParametersByName);
		}
		
		result.fDisplayName = fDisplayName;
		result.fType = fType;
		result.fClass = fClass;
		result.fClassName = fClassName;
		result.fToolTipText = fToolTipText;
		result.fShortDescription = fShortDescription;
		
		return (result);
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#getXmlNamespace()
	 */

	public String getTypeNamespace()
	{
		return (fTypeNamespace);
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#setName(java.lang.String)
	 */

	public void setName(String name)
	{
		String currentName = getFullyQualifiedName();
		
		try
		{
			super.setName(name);
		}
		catch (PropertyVetoException ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Unable to set base name of Descriptor " + 
					getFullyQualifiedName() + " to " + name;
				
				sLogger.logp(Level.WARNING, CLASS_NAME, "setName", message, ex);
			}
		}
		
		if (currentName != null)
		{
			if (fDirectory != null)
			{
				fDirectory.remove(currentName);
			}
		}
		
		String newName = getFullyQualifiedName();
		
		if (newName != null)
		{
			if (fDirectory != null)
			{
				fDirectory.add(newName, this);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#setNameQualifier(java.lang.String)
	 */

	public void setNameQualifier(String nameQualifier)
	{
		String currentName = getFullyQualifiedName();
		
		try
		{
			super.setNameQualifier(nameQualifier);
		}
		catch (PropertyVetoException ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Unable to set name qualifier of Descriptor " + 
					getFullyQualifiedName() + " to " + nameQualifier;
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"setNameQualifier", message, ex);
			}
		}
		
		if (currentName != null)
		{
			if (fDirectory != null)
			{
				fDirectory.remove(currentName);
			}
		}
		
		String newName = getFullyQualifiedName();
		
		if (newName != null)
		{
			if (fDirectory != null)
			{
				fDirectory.add(newName, this);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#setNameQualifier(gov.nasa.gsfc.commons.types.namespaces.HasFullyQualifiedName)
	 */

	public void setNameQualifier(HasFullyQualifiedName nameQualifier)
	{
		String currentName = getFullyQualifiedName();
		
		try
		{
			super.setNameQualifier(nameQualifier);
		}
		catch (PropertyVetoException ex)
		{
			if (sLogger.isLoggable(Level.WARNING))
			{
				String message = "Unable to set name qualifier of Descriptor " + 
					getFullyQualifiedName() + " to " + nameQualifier;
				
				sLogger.logp(Level.WARNING, CLASS_NAME, 
					"setNameQualifier", message, ex);
			}
		}
		
		if (currentName != null)
		{
			if (fDirectory != null)
			{
				fDirectory.remove(currentName);
			}
		}
		
		String newName = getFullyQualifiedName();
		
		if (newName != null)
		{
			if (fDirectory != null)
			{
				fDirectory.add(newName, this);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#setType(java.lang.String)
	 */
	
	public void setType(String type)
	{
		fType = type;

		if (fType != null) 
		{
			LookupTable globalTypeLookupTable = Irc.getDescriptorLibrary().
				getLookupTable(Ircml.LOOKUP_TABLE);
			
			if (globalTypeLookupTable != null)
			{
				fClassName = globalTypeLookupTable.lookup
					(fTypeNamespace, fType);				
			}		
		}
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#getType()
	 */
	
	public String getType()
	{
		return (fType);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#getTypeClass()
	 */
	public Class getTypeClass()
	{
		if (fClass == null && fClassName != null)
		{
			fClass = (Irc.getCorrespondingClass(fClassName));
		}

		return (fClass);
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#getClassName()
	 */
	
	public String getClassName()
	{
		return (fClassName);
	}


	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#getDisplayName()
	 */
	public String getDisplayName()
	{
		// TODO Determine how to form the localized name.
		if (fDisplayName == null)
		{
			return getName();
		}
		
		return fDisplayName;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#setDisplayName(java.lang.String)
	 */
	public void setDisplayName(String displayName)
	{
		fDisplayName = displayName;
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#getShortDescription()
	 */
	public String getShortDescription()
	{
		if (fShortDescription == null)
		{
			return getDisplayName();
		}
		
		return fShortDescription;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#setShortDescription(java.lang.String)
	 */
	public void setShortDescription(String text)
	{
		fShortDescription = text;
	}
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#getToolTip()
	 */
	
	public String getToolTip()
	{
		String result = fToolTipText;
		
		if (fToolTipText == null)
		{
			result = getDisplayName();
		}
		
		return (result);
	}


	/** 
	 *  Returns a String representation of this AbstractIrcElementDescriptor.
	 *
	 *  @return A String representation of this AbstractIrcElementDescriptor
	**/
	
	public String toString()
	{
		String result = "IRC element";
		
		String name = getName();
		
		if (name != null)
		{
			result += "\nName: " + name;
		}
		
		String fullyQualifiedName = getFullyQualifiedName();
		
		if (fullyQualifiedName != null)
		{
			result += "\nFully-Qualified Name: " + fullyQualifiedName;
		}
		
		if ((fDisplayName != null) && (fDisplayName != name))
		{
			result += "\nLocalized name: " + fDisplayName;
		}
		
		if (fType != null)
		{
			result += "\nType: " + fType;
		}
		
		if ((fClassName != null) && (fClassName != fType))
		{
			result += "\nClass: " + fClassName;
		}
		
		if (fToolTipText != null)
		{
			result += "\nTool-tip: " + fToolTipText;
		}
		
		return (result);
	} 
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#getParameter(java.lang.String)
	 */
	
	public String getParameter(String name)
	{
		String result = null;
		
		ParameterDescriptor descriptor = (ParameterDescriptor) 
			fParametersByName.get(name);
		
		if (descriptor != null)
		{
			result = descriptor.getValue();
		}
		
		return (result);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#getParameterNames()
	 */
	
	public Set getParameterNames()
	{
		return (Collections.unmodifiableSet(fParametersByName.keySet()));
	}
	

	/**
	 * Unmarshalls this AbstractIrcElementDescriptor from its associated JDOM Element. 
	 *
	**/
	private void xmlUnmarshall()
	{
		// Unmarshall the Element's name attribute (if any).
		String name  = fSerializer.loadStringAttribute
			(Ircml.A_NAME, null, fElement); 
		
		setName(name);
		
		// Unmarshall the Element's display name attribute (if any).
		String displayName  = fSerializer.loadStringAttribute
			(Ircml.A_DISPLAY_NAME, null, fElement); 
		setDisplayName(displayName);
		
		// Unmarshall the Element's short description attribute (if any).
		String description  = fSerializer.loadStringAttribute
			(Ircml.A_DESCRIPTION, null, fElement); 
		setShortDescription(description);
		
		// Unmarshall the Element's type attribute (if any).
		String type  = fSerializer.loadStringAttribute
			(Ircml.A_TYPE, null, fElement);
		
		setType(type);

		//---Unmarshall the parameters (if any).
		fSerializer.loadChildDescriptorElements(Ircml.E_PARAMETER, 
			fParametersByName, Ircml.C_PARAMETER, fElement, this, fDirectory);		
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor#xmlMarshall(org.jdom.Element)
	 */
	
	public void xmlMarshall(Element element)
	{
		// Marshall the Element's name attribute.
		fSerializer.storeAttribute(Ircml.A_NAME, getName(), element);
		
		// Marshall the Element's type attribute.
		fSerializer.storeAttribute(Ircml.A_TYPE, getType(), element);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractIrcElementDescriptor.java,v $
//  Revision 1.4  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.2  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.1  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.8  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.7  2005/05/24 19:23:33  tames_cvs
//  Modified the order in unmarshall so that the descriptor attributes are
//  handled before child elements.
//
//  Revision 1.6  2005/02/01 18:54:52  tames
//  Updated to reflect DesriptorLibrary method name changes.
//
//  Revision 1.5  2005/01/21 21:54:14  tames
//  Removed redundant getElementClass method. getTypeClass does the same
//  thing.
//
//  Revision 1.4  2005/01/07 20:32:26  tames
//  Changed localizedName to displayName and added a shortDescription
//  property to match bean naming conventions.
//
//  Revision 1.3  2004/11/09 22:51:29  chostetter_cvs
//  Further data transformation work
//
//  Revision 1.2  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.4  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.3  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.2  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.1  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
