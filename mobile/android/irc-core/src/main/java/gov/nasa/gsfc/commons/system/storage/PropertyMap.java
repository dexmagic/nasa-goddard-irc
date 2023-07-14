//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: PropertyMap.java,v $
//  Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/05/27 18:22:51  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.1  2004/05/05 19:11:52  chostetter_cvs
//  Further restructuring
//
//  Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//  Initial version
// 
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.system.storage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  A PropertyMap is a serializable collection of key/value pairs that describe 
 *  some object. The PropertyMap will serve as a starting point for storing the 
 *  metadata associated with various archived objects. The metadata will also 
 *  be used for searching the archive for interesting pieces of data.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:24 $
 *  @author	John Higinbotham 
**/
public class PropertyMap 
{
	private static final String CLASS_NAME = 
		PropertyMap.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	private HashMap fProps	= null;  // Properties 
	private HashSet fReadOnly = null;  // Properties referenced here are readonly 
	private String fPath	  = null;  // Full path to storage location 

	/**
	 * Create a new PropertyMap. 
	 *
	**/
	public PropertyMap()
	{
		fProps	= new HashMap();
		fReadOnly = new HashSet();
	}

	/**
	 * Set the value of the property with the specified name. 
	 *
	 * @param name  Property name.
	 * @param value Property value.
	 *
	**/
	public void set(String name, String value)
	{
		fProps.put(name, value);
	}

	/**
	 * Remove property with the specified name. 
	 *
	 * @param name  Property name.
	 *
	**/
	public void remove(String name)
	{
		fProps.remove(name);
		fReadOnly.remove(name);
	}

	/**
	 * Return an iterator of property names. 
	 *
	 * @return Iterator (of String objects)
	**/
	public Iterator getNames()
	{
		return fProps.keySet().iterator(); 
	}

	/**
	 * Get the value of the property with the specified name. 
	 *
	 * @param name Property name.
	 * @retrun String 
	**/
	public String get(String name)
	{
		String rval = (String) fProps.get(name);
		return rval;
	}

	/**
	 * This method loads the property map based on the specified checkpoint path. 
	 *
	**/
	public void load()
	{
		ObjectInputStream ois = StorageUtil.openOIS(fPath);
		if (ois != null)
		{
			try
			{
				fProps	= (HashMap) ois.readObject();
				fReadOnly = (HashSet) ois.readObject();

				if (sLogger.isLoggable(Level.INFO))
				{
					String message = "Loaded property map file";
					
					sLogger.logp(Level.INFO, CLASS_NAME, 
						"load", message);
				}
			}
			catch (Exception ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Could not load property map file";
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"load", message, ex);
				}
			}
		}
	}

	/**
	 * This method stores the property map based on the specified checkpoint path. 
	 *
	**/
	public void store()
	{
		ObjectOutputStream oos = StorageUtil.openOOS(fPath);
		if (oos != null)
		{
			try
			{
				oos.writeObject(fProps);
				oos.writeObject(fReadOnly);
			}
			catch (Exception e)
			{
			}
		}
	}

	/**
	 * This method sets the checkpoint path. 
	 *
	 * @param path Checkpoint path (including filename)
	**/
	public void setCheckpointPath(String path)
	{
	 	fPath = path;	
	}

	/**
	 * This method returns the checkpoint path. 
	 *
	 * @return String (checkpoint path) 
	**/
	public String getCheckpointPath()
	{
		return fPath;
	}

	/**
	 * Set a specific property as readonly.  
	 *
	 * @param name Name of property to set readonly.
	**/
	public void setReadOnly(String name)
	{
		fReadOnly.add(name);
	}

	/**
	 * Determine if a specific property is readonly. 
	 *
	 * @param name Name of property
	 * @return true if readonly, false otherwise
	**/
	public boolean isReadOnly(String name)
	{
		return fReadOnly.contains(name);
	}

	/**
	 * Returns a string representation of this object.
	 *
	 * @return String representation of object.
	**/
	public String toString()
	{
		return "PropertyMap:: \nProperties: " + fProps.toString() + "\nReadOnly Properties:" + fReadOnly.toString(); 
	}
}
