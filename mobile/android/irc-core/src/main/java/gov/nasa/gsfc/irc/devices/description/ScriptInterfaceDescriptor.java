//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//	for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.devices.description;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.description.xml.DescriptorSerializer;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.Ircml;
import gov.nasa.gsfc.irc.scripts.description.ScriptContainer;
import gov.nasa.gsfc.irc.scripts.description.ScriptDescriptor;

/**
 * The class provides access to information describing an instrument's 
 * script interface as well as the following items logicaly contained 
 * within the message interface: <P>
 *  <BR>
 *  1) scripts <BR>
 *
 * <P>
 * It is important to note that a script interface is simply a means 
 * to group a set of similar scripts and will be referred to elsewhere. 
 * Regardless of the number of script interfaces,
 * all scripts on the instrument must have a unique names to allow the IRC scripting
 * system to easily access them without having to dig through instruments to find
 * them.<P>
 *
 * The object is built based on information contained in an IML XML file
 * which describes the instrument being interfaced with by IRC.
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			 $Date: 2005/09/13 20:30:12 $ 
 * @author			  John Higinbotham 
 * @author			  Troy Ames
**/
public class ScriptInterfaceDescriptor extends AbstractIrcElementDescriptor
	implements ScriptContainer
{
	private Map fScript = null; // ScriptDescriptor collection
	
	// Elements mapped by local name
	private Map fScriptsByLocalName; 

	private ArrayList fOrderedScripts; // Cmds/Procs ordered as in xml


	/**
	 * Constructs a new ScriptInterfaceDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ScriptInterfaceDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ScriptInterfaceDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ScriptInterfaceDescriptor		
	**/
	
	public ScriptInterfaceDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Iml.N_DEVICES);
		
		init();
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new ScriptInterfaceDescriptor having the given name.
	 * 
	 * @param name The name of the new ScriptInterfaceDescriptor
	**/
	
	public ScriptInterfaceDescriptor(String name)
	{
		super(name);
		
		init();
	}

	
	/**
	 * Initialize class. 
	**/
	private void init()
	{
		fScript = new LinkedHashMap();
	}

	/**
	 * Get the interface's script descriptor with the 
	 * specified name.
	 *
	 * @param  name Name of script.
	 * @return Script descriptor with the specified name.
	**/
	public ScriptDescriptor getScriptByName(String name)
	{
		return (ScriptDescriptor) fScript.get(name);
	}

	/**
	 * Get the interface's script descriptor with the 
	 * specified localized name. 
	 *
	 * @param  name localized name of script.
	 * @return Script descriptor with the specified localized name.
	**/
	public ScriptDescriptor getScriptByLocalName(String name)
	{
		return (ScriptDescriptor) fScriptsByLocalName.get(name);
	}

	/**
	 * Add a script to this interface.
	 *
	 * @param descriptor ScriptDescriptor to add.
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void addScript(ScriptDescriptor descriptor)
	{
		fScript.put(descriptor.getName(), descriptor);
		fOrderedScripts.add(descriptor);
		String name = descriptor.getDisplayName();
		if (name != null)
		{
			fScriptsByLocalName.put(name, descriptor);
		}
	}

	/**
	 * Remove a script from this interface.
	 *
	 * @param descriptor ScriptDescriptor to remove.
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void removeScript(ScriptDescriptor descriptor)
	{
		fScript.remove(descriptor.getName());
		fOrderedScripts.remove(descriptor);
		String name = descriptor.getDisplayName();
		if (name != null)
		{
			fScriptsByLocalName.remove(name);
		}
	}

	/**
	 * Find the script descriptor with the given name. 
	 * If the key does not exist in the name namespace then null is returned.
	 *
	 * @param  key Name of command. 
	 * @return Script descriptor with the specified key.
	**/
	public ScriptDescriptor findScript(String key)
	{
		ScriptDescriptor rval = null;
		rval = getScriptByName(key);
		if (rval == null)
		{
			rval = getScriptByLocalName(key);
		}
		return rval; 
	}

	/**
	 * Get the message interface's collection of script descriptors.
	 *
	 * @return All of the message interface's script descriptors.
	**/
	public Iterator getScripts()
	{
		return fScript.values().iterator();
	}

	private void getOrderedScripts()
	{
		ArrayList list = new ArrayList();
		String elementName;
		Element element;
		Iterator i = fElement.getChildren().iterator();
		Object o;
		while (i.hasNext())
		{
			element = (Element) i.next();
			elementName = element.getName();
			if (elementName.equals(Iml.E_SCRIPT))
			{
				o = null;
				o = getScriptByName(
						element.getAttribute(Ircml.A_NAME).getValue());
				if (o != null)
				{
					list.add(o);
				}
			}
		}

		fOrderedScripts = list;
	}

	/**
	  * Unmarshall descriptor from XML. 
	 **/
	private void xmlUnmarshall()
	{
		//---Load scripts
		fSerializer.loadChildDescriptorElements(
			Iml.E_SCRIPT,
			fScript,
			Iml.C_SCRIPT,
			fElement,
			this,
			fDirectory);

		//The actual resolution of references is put off until first use. If we determine the
		//the command response mapping is to only be for the given commands and responses in
		//this interface we can do the resolution here.

		//--- Finally support lookups based on localized names
		fScriptsByLocalName = DescriptorSerializer.toMapByDisplayName(getScripts());

		//--- Build an ordered list of cmds/procs to preserve the xml ordering
		getOrderedScripts();
	}

	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into.
	**/
	public void xmlMarshall(Element element)
	{
		//---Note: This marshall method uses the existing tree and swaps 
		// out the branches for the scripts. When/If the IML tree 
		// is changed to have more items settable above this point, 
		// this method may have to change. 

		//---Prune old command procedures
		List removeElements = new ArrayList();
		List l = fElement.getChildren();
		Iterator i = l.iterator();
		Element e;
		while (i.hasNext())
		{
			e = (Element) i.next(); 
			if (e.getName().equals(Iml.E_SCRIPT))
			{
				removeElements.add(e);
			}
		}
		
		i = removeElements.iterator();
		while (i.hasNext())
		{
			((Element) i.next()).detach();
		}

		//---Attach new scripts
		i = fOrderedScripts.iterator();
		Descriptor des;
		while (i.hasNext())
		{
			des = (Descriptor) i.next();
			if (des instanceof ScriptDescriptor)
			{
				fSerializer.storeDescriptorElement(
					Iml.E_SCRIPT,
					des,
					Iml.C_SCRIPT,
					fElement,
					fDirectory);
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ScriptInterfaceDescriptor.java,v $
//  Revision 1.6  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.5  2005/02/01 18:14:14  tames
//  Updated to reflect changes in DescriptorSerializer.
//
//  Revision 1.4  2005/01/07 20:33:34  tames
//  Changed localizedName to displayName to match bean naming
//  conventions.
//
//  Revision 1.3  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.1  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.4  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.3  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.2  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.1  2004/09/04 13:29:00  tames
//  *** empty log message ***
//
