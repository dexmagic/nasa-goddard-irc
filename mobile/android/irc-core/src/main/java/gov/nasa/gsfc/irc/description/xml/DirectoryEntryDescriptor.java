//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, Code 588 for 
//	the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DirectoryEntryDescriptor.java,v $
//  Revision 1.6  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.4  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.3  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.2  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
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

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;


/**
 * The class provides access to information describing a single directory 
 * entry. <P><BR>
 *
 * The object is built based on information contained in a DML XML file. <P>
 * Developers should be certain to refer to the IRC schemas to gain a better
 * understanding of the structure and content of the data used to build the
 * descriptors. <P>
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/01/23 17:59:54 $ 
 * @author John Higinbotham   
**/

public class DirectoryEntryDescriptor extends AbstractIrcElementDescriptor
{
	private String fPath		= null;	   // File/Directory path
	private String fType		= Dml.FILE;   // File or Directory
	private boolean fCompressed = false;	  // Is entry compressed? 


	/**
	 *  Constructs a new DirectoryEntryDescriptor having the given base name and 
	 *  no name qualifier.
	 * 
	 *  @param name The base name of the new DirectoryEntryDescriptor
	 **/

	public DirectoryEntryDescriptor(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new DirectoryEntryDescriptor having the given base name and 
	 *  name qualifier.
	 * 
	 *  @param name The base name of the new DirectoryEntryDescriptor
	 *  @param nameQualifier The name qualifier of the new DirectoryEntryDescriptor
	 **/

	public DirectoryEntryDescriptor(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
	
	
	/**
	 * Constructs a new DirectoryEntryDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new DirectoryEntryDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		DirectoryEntryDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		DirectoryEntryDescriptor		
	**/
	
	public DirectoryEntryDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Dml.N_DIRECTORIES);
		
		xmlUnmarshall();
	}
	

	/**
	 * Construct a point to point binding configuration manually. Unless
	 * you know what you are doing, you should use the "add" methods
	 * in the DirectoryDescriptor class.
	 *
	 * @param	parent			Manager descriptor. 
	 * @param	directory		Descriptor directory. 
	 * @param	path 		 	Path to item.	
	 * @param	type 		 	Type of item {file, directory}	
	 * @param	compressed 		Compression flag {true, false}
	 *						
	**/
	public DirectoryEntryDescriptor(Descriptor parent, DescriptorDirectory directory, 
		String path, String type, boolean compressed)
	{
		this(parent, directory, null);
		
		fPath = DescriptorSerializer.toXMLFilePath(path);
		fType = type; 

		if ((type.compareTo(Dml.FILE) != 0) && (type.compareTo(Dml.DIRECTORY) != 0)) 
		{
			throw (new IllegalArgumentException
				("IllegalArgumentException: type must be one of {Dml.FILE, Dml.DIRECTORY}"));
		}

		fCompressed = compressed; 
	}

	//---Property get methods
	/**
	 * Get the path 
	 *
	 * @return path 
	 *			
	**/
	public String getPath()
	{
		return fPath;
	}

	/**
	 * Get the type 
	 *
	 * @return type 
	 *			
	**/
	public String getType()
	{
		return fType;
	}

	/**
	 * Get the compressed flag 
	 *
	 * @return true if compressed, false otherwise 
	 *			
	**/
	public boolean isCompressed()
	{
		return fCompressed;
	}

	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	private void xmlUnmarshall()
	{
		//---Load path 
		fPath = fSerializer.loadStringAttribute(Dml.A_PATH, null, fElement);

		//---Load type 
		fType = fSerializer.loadStringAttribute(Dml.A_TYPE, null, fElement);

		//---Load compressed flag 
		fCompressed = fSerializer.loadBooleanAttribute(Dml.A_COMPRESSED, false, fElement);
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

		//---Store path 
		fSerializer.storeAttribute(Dml.A_PATH, fPath, element);

		//---Store type
		fSerializer.storeAttribute(Dml.A_TYPE, fType, element);

		//---Store compressed flag
		fSerializer.storeAttribute(Dml.A_COMPRESSED, fCompressed, element);
	}
}
