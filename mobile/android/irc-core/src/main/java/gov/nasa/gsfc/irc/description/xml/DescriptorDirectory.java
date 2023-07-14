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
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.description.xml;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.description.Descriptor;


/**
 * The class provides a simple directory for finding descriptors by name. 
 * Each descriptor stores its name in the directory if it has one. All 
 * descriptors receive a reference to the descriptor directory for which
 * they are to lookup references to other descriptors found in XML instance
 * files for example.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2005/05/24 20:26:33 $
 * @author John Higinbotham   
**/

public class DescriptorDirectory
{
	private static final String CLASS_NAME = 
		DescriptorDirectory.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	private HashMap fLookupTable  = null;  // Descriptor lookup table  
	private LookupTable fMapTable = null;  // Mapping table to be used by descriptor

	/**
	 * Construct a new descriptor directory 
	 *
	 * @param mapTable Mapping table for descriptors.
	 *
	**/
	public DescriptorDirectory(LookupTable mapTable)
	{
		fLookupTable = new HashMap();
		fMapTable	= mapTable;
	}

	/**
	 * Get the map table.
	 *
	 * @return Map table. 
	 *			
	**/
	public LookupTable getMapTable()
	{
		return fMapTable;
	}

	/**
	 * Add a descriptor name and reference to the directory. 
	 *
	 * @param  name	  Name of descriptor. 
	 * @param  reference Reference to descriptor. 
	 *			
	**/
	public void add(String name, Descriptor reference)
	{
		Object obj = fLookupTable.put(name, reference);

		if (obj != null) 
		{
			if (sLogger.isLoggable(Level.CONFIG))
			{
				String message = "Reference overwritten in directory for name: " 
					+ name;
				
				sLogger.logp(Level.CONFIG, CLASS_NAME, 
					"add", message);
			}
		}
	}

	/**
	 * Remove a descriptor name and reference from the directory. 
	 *
	 * @param  name Name of descriptor to remove. 
	 *			
	**/
	public void remove(String name)
	{
		fLookupTable.remove(name);  
	}

	/**
	 * Find a descriptor reference give a name. 
	 *
	 * @param  name Name of descriptor. 
	 * @return Reference to descriptor with given name. 
	 *			
	**/
	public Object find(String name)
	{
		Object obj = fLookupTable.get(name);
		if (obj == null) 
		{
			if (sLogger.isLoggable(Level.CONFIG))
			{
				String message = "Directory lookup failed for name: " + 
					name;
				
				sLogger.logp(Level.CONFIG, CLASS_NAME, 
					"find", message);
			}
		}
		return obj;
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: DescriptorDirectory.java,v $
//  Revision 1.7  2005/05/24 20:26:33  tames_cvs
//  Comments change only.
//
//  Revision 1.6  2005/05/24 20:24:30  tames_cvs
//  Changed log levels in add and find methods.
//
//  Revision 1.5  2004/09/07 05:22:19  tames
//  Descriptor cleanup
//
//  Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/05/27 18:24:03  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.2  2004/05/17 22:01:10  chostetter_cvs
//  Further data-related work
//
//  Revision 1.1  2004/05/12 22:46:04  chostetter_cvs
//  Initial version
// 
