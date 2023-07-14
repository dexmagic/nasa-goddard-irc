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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;

import org.jdom.Element;

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.app.preferences.IrcPrefKeys;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;
import gov.nasa.gsfc.irc.devices.description.Iml;
import gov.nasa.gsfc.irc.messages.description.AbstractMessageDescriptor;
import gov.nasa.gsfc.irc.messages.description.FieldDescriptor;

/**
 * The class provides access to information describing a command procedure. A
 * command procedure is similar to a command except that there is a script
 * file associated with it that may itself perform some set of functionality
 * including the issuance of lower level commands or the execution of other
 * scripts.<P>
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
 * @version			 $Date: 2005/05/23 15:31:13 $
 * @author			  John Higinbotham
**/
public class ScriptDescriptor extends AbstractMessageDescriptor
{
	private URL fURL;   // URL of procedure
	private String fFilename;   // Location of procedure
	private String fLanguage;   // Language of procedure


	/**
	 * Constructs a new ScriptDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ScriptDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ScriptDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ScriptDescriptor		
	**/
	
	public ScriptDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new ScriptDescriptor having the given name.
	 * 
	 * @param name The name of the new ScriptDescriptor
	**/
	
	public ScriptDescriptor(String name)
	{
		super(name);
	}

	
	/**
	 * Get the URL/location of this script.
	 *
	 * @return URL of script file
	**/
	public URL getScriptLocation()
	{
		return fURL;
	}

	/**
	 * Set the URL/location of this script. Note that even
	 * though this method currently takes a URL, the script
	 * evaluator may not execute remote scripts.
	 *
	 * @param url URL of script file
	**/
	public void setScriptLocation(URL url)
	{
		//---Save the url
		fURL = url;

		//---Determine what the relitave script directory is
		String relativeScriptDirectory = 
			System.getProperty(IrcPrefKeys.SCRIPT_DIRECTORY);

		//---Get URL for relative script dir
		URL relativeScriptDirectoryUrl = Irc.getResource(relativeScriptDirectory);

		//---Does the new location refer to something in the relative script directory?
		String remainder = null;
		if (url.toString().indexOf(relativeScriptDirectoryUrl.toString()) == 0)
		{
			//---Determine length of matching parts
			int len = relativeScriptDirectoryUrl.toString().length();

			//---Get a hold of the part of the new location that is different
			remainder = url.toString().substring(len);

			//---Save relative file path
			fFilename = remainder;
		}
		else
		{
			//---Save url file path
			fFilename = fURL.getFile();
		}
	}

	/**
	 * Set the language for the script. Refer to IRC scripting support
	 * for additional details. Do to recent changes, this property may not even
	 * be needed in certain instances as the appropriate scripting engine is now
	 * being selected more appropriately via the file's extension. Look for this
	 * method to be deprecated in the near future.
	 *
	 * @param language Script language string
	**/
	public void setLanguage(String language)
	{
		fLanguage = language;
	}

	/**
	 * Set the file for the command procedure. This may be express in one of 
	 * two ways. It can be a filename string relative to the path under the 
	 * classes directory. Ex: resources/scripts/start.py As an alternative, 
	 * it can be an absolute path.
	 *
	 * @param filename Name of script as noted above
	**/
	public void setFile(String filename)
	{
		fFilename = filename;
		setScriptFile();
	}

	/**
	 *  This method locates the script file and
	 *  stores a handle to it.
	**/
	private void setScriptFile()
	{
		URL scriptUrl;
		scriptUrl = Irc.getResource(fFilename);
		if (scriptUrl == null)
		{
			String path = System.getProperty(IrcPrefKeys.SCRIPT_DIRECTORY)
						+ "/" + fFilename;
			scriptUrl = Irc.getResource(path);
		}
		if (scriptUrl == null)
		{
			try
			{
				//---Treat as absolute
				File f = new File (fFilename);
				scriptUrl = f.toURL();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		fURL = scriptUrl;
	}

	/**
	 *  Retrieve the full text of the script.
	 *
	 *  @return String  Full text of the script.
	**/
	public String getScriptText() throws IOException
	{
		StringBuffer procedureText = new StringBuffer();
		String line;
		BufferedReader in;

		//--- Build Buffered Reader to read file
		if (fURL != null)
		{
			in = new BufferedReader(new InputStreamReader(fURL.openStream()));
			while ((line = in.readLine()) != null)
			{
				procedureText.append(line + "\n");
			}
		}
		return procedureText.toString();
	}

	/**
	 * Get the script filename.
	 *
	 * @return filename.
	 *
	**/
	public String getFile()
	{
		return fFilename;
	}

	/**
	 * Get the script language. 
	 *
	 * @return script language.
	**/
	public String getLanguage()
	{
		return fLanguage;
	}

	/**
	 * Returns the argument of this Script that has the given name.
	 *
	 * @return The argument of this Script that has the given name
	**/
	
	public FieldDescriptor getArgument(String name)
	{
		return ((FieldDescriptor) getEntry(name));
	}
	

	/**
	 * Returns the (unmodifiable) Collection of arguments of this Script.
	 *
	 * @return The (unmodifiable) Collection of arguments of this Script
	**/
	
	public Collection getArguments()
	{
		return (getEntries());
	}
	

	/**
	 * Returns the name of the script if non-null, otherwise returns the
	 * script location.
	 */
	public String toString() {
		if(getName()==null) {
			if(getScriptLocation()!=null) {
				return getScriptLocation().toString();
			} 
			return null;
		} else {
			return getName();
		}
		
	}
	
	/**
	 * Unmarshall descriptor from XML.
	**/
	private void xmlUnmarshall()
	{
		//System.out.println("ScriptDescriptor xmlUnmarshall");
		setFile(fSerializer.loadStringAttribute(Iml.A_SCRIPT_FILENAME, null, fElement));
		//System.out.println("ScriptDescriptor xmlUnmarshall filename:" + getFile());
		fLanguage  = fSerializer.loadStringAttribute(Iml.A_SCRIPT_LANGUAGE, null, fElement);
		
		//--- Unmarshall the ArgumentDescriptors
		
//		fSerializer.loadChildDescriptorElements(Iml.E_ARGUMENT, 
//			fDataMapEntryDescriptorsByName, Iml.C_ARGUMENT, fElement, this, 
//			fDirectory);
	}

   /**
	 * Marshall descriptor to XML.
	 *
	 * @param element Element to marshall into.
	**/
	public void xmlMarshall(Element element)
	{
		super.xmlMarshall(element);
		fSerializer.storeAttribute(Iml.A_SCRIPT_FILENAME, fFilename, element);
		fSerializer.storeAttribute(Iml.A_SCRIPT_LANGUAGE, fLanguage, element);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ScriptDescriptor.java,v $
//  Revision 1.13  2005/05/23 15:31:13  tames_cvs
//  Changed super class extends.
//
//  Revision 1.12  2005/02/09 19:10:09  tames_cvs
//  Removed references to Argument Elements
//
//  Revision 1.11  2004/10/14 23:09:22  chostetter_cvs
//  Message, Adapter descriptor-related changes
//
//  Revision 1.10  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.9  2004/10/01 15:47:41  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.8  2004/09/27 22:19:02  tames
//  Reflects the relocation of Instrument descriptors and
//  implementation classes to the devices package.
//
//  Revision 1.7  2004/09/09 17:03:58  tames
//  More descriptor/IML cleanup as well as adding the foundation
//  for localization on descriptor names.
//
//  Revision 1.6  2004/09/05 13:28:35  tames
//  Properties and preferences clean up
//
//  Revision 1.5  2004/08/12 03:18:54  tames
//  Scripting support
//
//  Revision 1.4  2004/07/27 21:12:41  tames_cvs
//  *** empty log message ***
//
//  Revision 1.3  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/07/11 21:24:54  chostetter_cvs
//  Organized imports
//
//  Revision 1.1  2004/06/30 20:45:14  tames_cvs
//  Initial Version
//
