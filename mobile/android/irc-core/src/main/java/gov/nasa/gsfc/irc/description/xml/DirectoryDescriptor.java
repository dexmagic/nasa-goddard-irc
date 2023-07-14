//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 588 for 
//	the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DirectoryDescriptor.java,v $
//  Revision 1.10  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.8  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.7  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.6  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.5  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/06/03 04:42:26  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2004/06/01 16:05:18  tames_cvs
//  Removed old references to obsolete package names and constants.
//
//  Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//  Initial version
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

package gov.nasa.gsfc.irc.description.xml;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;


/**
 * The class provides access to information describing a collection of
 * physical files and directories. The IRC archiving package makes use
 * of these descriptors for keeping tabs on files associated with archives.<P>
 * 
 * The object is built based on information contained in a DML XML file.<P>
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version			 $Date: 2006/01/23 17:59:54 $
 * @author			  John Higinbotham
**/

public class DirectoryDescriptor extends AbstractIrcElementDescriptor
{
	private Map fEntries;


	/**
	 *  Constructs a new DirectoryDescriptor having the given base name and no name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new DirectoryDescriptor
	 **/

	public DirectoryDescriptor(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new DirectoryDescriptor having the given base name and name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new IrcElementDescriptor
	 *  @param nameQualifier The name qualifier of the new IrcElementDescriptor
	 **/

	public DirectoryDescriptor(String name, String nameQualifier)
	{
		super(name, nameQualifier);
		
		fEntries = new LinkedHashMap();
	}
	
	
	/**
	 * Constructs a new DirectoryDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DirectoryDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DirectoryDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DirectoryDescriptor		
	**/
	
	public DirectoryDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Dml.N_DIRECTORIES);
		
		fEntries = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	

	/**
	 * Add an entry to the directory.
	 *
	 * @param name Name of entry.
	 * @param path Complete path of entry.
	 * @param type One of {Dml.DIRECTORY, Dml.FILE}.
	 * @param compressed True if compressed, false otherwise.
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void addEntry(String name, String path, String type, boolean compressed)
	{
		DirectoryEntryDescriptor descriptor = 
			new DirectoryEntryDescriptor(this, fDirectory, path, type, compressed);
		
		descriptor.setName(name);
		
		fEntries.put(name, descriptor);
	}
	

	/**
	 * Remove an entry from the directory.
	 *
	 * @param name Name of entry.
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void removeEntry(String name)
	{
		fEntries.remove(name);
	}

	/**
	 * Rename entry. 
	 *
	 * @param orgName Original entry name. 
	 * @param newName New entry name.
	 * @throws IllegalStateException if descriptor is finalized.
	**/
	public void renameEntry(String orgName, String newName)
	{
		DirectoryEntryDescriptor descriptor = getEntryByName(orgName);
		if (descriptor != null)
		{
			removeEntry(orgName);
			addEntry(newName, descriptor.getPath(), descriptor.getType(), descriptor.isCompressed());
		}
	}

	/**
	 * Get a directory entry by name.
	 *
	 * @param  name Name of requested entry. 
	 * @return FieldDescriptor
	 *			
	**/
	public DirectoryEntryDescriptor getEntryByName(String name)
	{
		return (DirectoryEntryDescriptor) fEntries.get(name);
	}

	/**
	 * Get iterator of directory entries. 
	 *
	 * @return Iterator (of DirectoryEntryDescriptor objects) 
	 *			
	**/
	public Iterator getEntries()
	{
		return fEntries.values().iterator();
	}

	/**
	 * Get iterator of directory entry names. 
	 *
	 * @return Iterator (of String objects) 
	 *			
	**/
	public Iterator getEntryNames()
	{
		return fEntries.keySet().iterator();
	}

	/**
	  * Unmarshall descriptor from XML. 
	  *
	 **/
	private void xmlUnmarshall()
	{
		//---Load pipeline algorithms
		fSerializer.loadChildDescriptorElements(Dml.E_DIRECTORY_ENTRY, fEntries, 
											Dml.C_DIRECTORY_ENTRY, fElement, this, fDirectory);
	}

	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into.
	 *
	**/
	public void xmlMarshall(Element element)
	{
		super.xmlMarshall(element);

		//---Store directory entries 
		fSerializer.storeDescriptorElement(Dml.E_DIRECTORY_ENTRY, fEntries, Dml.C_DIRECTORY_ENTRY, element, fDirectory);
	}
}
