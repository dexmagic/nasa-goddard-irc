//=== File Prolog ============================================================
//  This code was developed by NASA Goddard Space Flight Center, 
//  Code 588 for the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.scripts.description;

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
import gov.nasa.gsfc.irc.devices.description.Iml;
import gov.nasa.gsfc.irc.scripts.Sml;

/**
 * The class provides access to information describing a collection of
 * scripts. The script group enables 
 * scripts to be defined external to the IML files. This is useful when
 * a collection of scripts is going to be shared with multiple
 * set of instruments and users want to avoid duplication. Keeping 
 * scripts seperate also allows for smaller IML files. The script
 * groups can contain additional groups as well as scripts.<P>
 *
 * The object is built based on information contained in a CPML XML file
 * which describes a collection of scripts available to IRC.
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			 $Date: 2005/09/13 20:30:12 $ 
 * @author			  John Higinbotham   
**/
public class ScriptGroupDescriptor extends AbstractIrcElementDescriptor
	implements ScriptContainer
{
	private Map fScript;  // script collection
	private Map fScriptGroup;  // Nested script groups 

	// Elements of fScript by localized name 
	private Map fScriptByLocalName;  


	/**
	 * Constructs a new ScriptGroupDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ScriptGroupDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ScriptGroupDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ScriptGroupDescriptor		
	**/
	
	public ScriptGroupDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Iml.N_DEVICES);
		
		fScript = new LinkedHashMap();
		fScriptGroup = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new ScriptGroupDescriptor having the given name.
	 * 
	 * @param name The name of the new ScriptGroupDescriptor
	**/
	
	public ScriptGroupDescriptor(String name)
	{
		super(name);
		
		fScript = new LinkedHashMap();
		fScriptGroup = new LinkedHashMap();
	}

	
	/**
	 * Get the specified script from the group.
	 *
	 * @param  name Name of script to return. 
	 * @return script with specified name. 
	**/
	public ScriptDescriptor getScriptByName(String name)
	{
		return (ScriptDescriptor)fScript.get(name);
	}

	/**
	 * Get the specified script from the group using a localized name.
	 *
	 * @param  name name of script to return. 
	 * @return script with specified name. 
	**/
	public ScriptDescriptor getScriptByLocalName(String name)
	{
		return (ScriptDescriptor)fScriptByLocalName.get(name);
	}

	/**
	 * Find the script descriptor with the given name. 
	 * If the key does not exist in the name namespace then null is returned.
	 *
	 * @param  key Name of script. 
	 * @return script descriptor with the specified key.
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
	 * Get all the scripts in the group. 
	 *
	 * @return All scripts the group. 
	**/
	public Iterator getScripts()
	{
		return fScript.values().iterator();
	}

	/**
	 * Get a sub script group by name. 
	 *
	 * @param  name Name of script group. 
	 * @return script group with the specified name.
	**/
	public ScriptGroupDescriptor getSubGroupByName(String name)
	{
		return (ScriptGroupDescriptor) fScriptGroup.get(name);
	}

	/**
	 * Get all the sub script groups (1-level deep) contained
	 * in this group.
	 *
	 * @return All the sub script groups contained in this group. 
	**/
	public Iterator getSubGroups()
	{
		return fScriptGroup.values().iterator();
	}

	/**
	 * Add a script to the group.
	 *
	 * @param script ScriptDescriptor to add.
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void addScript(ScriptDescriptor script)
	{
		fScript.put(script.getName(), script);
		String abv = script.getDisplayName();
		if (abv != null)
		{
			fScriptByLocalName.put(abv, script);
		}
	}

	/**
	 * Remove a script from the group.
	 *
	 * @param script ScriptDescriptor to remove 
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void removeScript(ScriptDescriptor script)
	{
		fScript.remove(script.getName());
		String abv = script.getDisplayName();
		if (abv != null)
		{
			fScriptByLocalName.remove(abv);
		}
	}

// For the moment we are not going to support adding and removing groups
// manually. The script editor is the only entity that will
// modify the cmpl tree manually. If we were to allow groups to change
// then xinclude issues would likely crop up when saving if a file
// is built up from multiple files which is currently permitted.

//	/**
//	 * Add a script group.
//	 *
//	 * @param group ScriptGroupDescriptor to add.
//	**/
//	public void addScriptGroup(ScriptGroupDescriptor group)
//	{
//	   fScriptGroup.add(group);
//	}

//	/**
//	 * Remove a script group.
//	 *
//	 * @param group ScriptGroupDescriptor to remove. 
//	**/
//	public void removeScriptGroup(ScriptGroupDescriptor group)
//	{
//	   fScriptGroup.remove(group.getName());
//	}


	/**
	 * Unmarshall descriptor from XML. 
	**/
	private void xmlUnmarshall()
	{
		fSerializer.loadChildDescriptorElements(Iml.E_SCRIPT, fScript, Iml.C_SCRIPT, 
			fElement, this, fDirectory);
		fSerializer.loadChildDescriptorElements(Sml.E_SCRIPT_GRP, fScriptGroup, Sml.C_SCRIPT_GRP,
			fElement, this, fDirectory);

		//--- Support lookup by localized name
		fScriptByLocalName =
			DescriptorSerializer.toMapByDisplayName(getScripts());
	}

	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into.
	**/
	public void xmlMarshall(Element element)
	{
		//super.xmlMarshall(element);

		//---Detach old scripts
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

		//---Add in existing scripts
		fSerializer.storeDescriptorElement(Iml.E_SCRIPT, fScript, Iml.C_SCRIPT, fElement, fDirectory);

		//---Deal with groups
		fSerializer.storeDescriptorElement(Sml.E_SCRIPT_GRP, fScriptGroup, Sml.C_SCRIPT_GRP, null, fDirectory);

	}

	/**
	 * Do XML marshalling on this descriptor tree using the hybrid approach. 
	 *
	 * @return Root element of marshalled descriptor tree.
	**/
	public Element doHybridXmlMarshall()
	{
		xmlMarshall(null);	
		return fElement;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ScriptGroupDescriptor.java,v $
//  Revision 1.12  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.11  2005/02/01 18:44:06  tames
//  Changes to reflect DescriptorSerializer changes.
//
//  Revision 1.10  2005/01/07 20:50:13  tames
//  Changed localizedName to displayName to match bean naming
//  conventions.
//
//  Revision 1.9  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.8  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.7  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.6  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.5  2004/09/07 05:30:16  tames
//  Descriptor cleanup
//
//  Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/07/11 21:24:54  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2004/06/30 20:58:36  tames_cvs
//  Updated reference to markup language
//
//  Revision 1.1  2004/06/30 20:45:14  tames_cvs
//  Initial Version
//
