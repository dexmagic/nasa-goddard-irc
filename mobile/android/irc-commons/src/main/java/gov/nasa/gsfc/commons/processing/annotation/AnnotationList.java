//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AnnotationList.java,v $
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.4  2004/05/27 17:51:38  tames_cvs
//  CLASS_NAME assignment fix
//
//  Revision 1.3  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
//
//  Revision 1.2  2004/05/05 19:11:52  chostetter_cvs
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

package gov.nasa.gsfc.commons.processing.annotation;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.system.storage.StorageUtil;


/**
 *  A collection of Annotation objects. This list provides support for
 *  preserving its contents across executions of an application via serialization.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	 $Date: 2004/07/12 14:26:24 $
 *  @author	John Higinbotham
**/

public class AnnotationList
{
	private static final String CLASS_NAME = 
		AnnotationList.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	
	//--- Vars
	private ArrayList fList = null; // Collection of annotations
	private String fPath	= null; // Full path to storage location
	

	/**
	 * Default constructor of an AnnotationList.
	 *
	**/
	
	public AnnotationList()
	{
		fList = new ArrayList();
	}

	
	/**
	 * Adds the given Annotation to this AnnotationList.
	 *
	 * @param annotation The Annotation to add
	**/
	
	public void add(Annotation annotation)
	{
		fList.add(annotation);
	}
	

	/**
	 * Removes the given Annotation from this AnnotationList.
	 *
	 * @param annotation The Annotation to remove
	**/
	
	public void remove(Annotation annotation)
	{
		Date d = annotation.getTimeStamp();
		Annotation a = null;

		// Traverse backwards through the list because
		// of the way ArrayLists handle a remove.
		for (int i = fList.size() - 1; i >= 0 ; --i)
		{
			a = (Annotation) fList.get(i);
			
			if (d.compareTo(a.getTimeStamp()) == 0)
			{
				fList.remove(i);
			}
		}
	}

	/**
	 * Get an iterator over all the Annotations in the list.
	 *
	 * @return Returns Iterator of Annotation objects
	**/
	
	public Iterator getAll()
	{
		ArrayList list = (ArrayList) fList.clone();
		
		return (list.iterator());
	}
	

	/**
	 * Loads the contents of this AnnotationList from its current 
	 * checkpoint path.
	 *
	**/
	
	public void load()
	{
		ObjectInputStream ois = StorageUtil.openOIS(fPath);
		
		if (ois != null)
		{
			try
			{
				fList = (ArrayList) ois.readObject();
			}
			catch (Exception ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Could not load annotation file";
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"load", message, ex);
				}
			}
		}
	}
	

	/**
	 * Stores the contents of this AnnotationList to its current checkpoint 
	 * path.
	 *
	**/
	
	public void store()
	{
		ObjectOutputStream oos = StorageUtil.openOOS(fPath);
		
		if (oos != null)
		{
			try
			{
				oos.writeObject(fList);
			}
			catch (Exception ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Could not store tp file";
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"store", message, ex);
				}
			}
		}
	}
	

	/**
	 * Sets the checkpoint path of this AnnotationList to the given \
	 * path String.
	 *
	 * @param path The checkpoint path (including filename)
	**/
	
	public void setCheckpointPath(String path)
	{
		fPath = path;
	}
	

	/**
	 * Returns the checkpoint path of this AnnotationList.
	 *
	 * @return The checkpoint path of this AnnotationList
	**/
	
	public String getCheckpointPath()
	{
		return (fPath);
	}
		

	/**
	 * Returns a string representation of this list.
	 *
	 * @return Returns String representation of this list.
	**/
		
	public String toString()
	{
		return (fList.toString());
	}
}
