//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.commons.publishing.paths;

import java.util.ArrayList;



/**
 * Represents a concrete implementation of a
 * {@link gov.nasa.gsfc.commons.publishing.paths.Path Path} to a device or
 * node. The typical case is that the first element (index 0) in the path
 * represents the first node in a tree structure leading to a destination.
 * DefaultPath is immutable, however the Objects that represent each node in the
 * path may not be.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/05/04 14:50:49 $
 * @author Troy Ames
 */
public class DefaultPath implements Path
{
	private ArrayList fPath = new ArrayList(3);
	
	/**
	 * Constructs an empty path.
	 */
	public DefaultPath()
	{
		super();
	}

	/**
	 * Constructs a path with the one element provided.
	 * 
	 * @param element the element of the path
	 */
	public DefaultPath(Object element)
	{
		super();
		
		if (element == null)
		{
			throw new IllegalArgumentException("Argument cannot be null");
		}
		
		fPath.add(element);
	}

	/**
	 * Constructs a path with the elements provided by the Object array.
	 * 
	 * @param pathArray the elements of the path
	 */
	public DefaultPath(Object [] pathArray)
	{
		super();
		
		if (pathArray == null)
		{
			throw new IllegalArgumentException("Argument cannot be null");
		}
		
		for (int i = 0; i < pathArray.length; i++)
		{
			fPath.add(pathArray[i]);
		}
	}

	/**
	 * Constructs a new path by adding the element to the end of the 
	 * parentPath.
	 * 
	 * @param parentPath the elements of the parent path or null
	 * @param element element to add to the end of the path
	 */
	public DefaultPath(Path parentPath, Object element)
	{
		super();
		
		if (element == null)
		{
			throw new IllegalArgumentException("element argument cannot be null");
		}
		
		if (parentPath != null)
		{
			for (int i = 0; i < parentPath.getPathCount(); i++)
			{
				fPath.add(parentPath.getPathComponent(i));
			}
		}
		
		fPath.add(element);
	}

	/**
	 * Constructs a new path by adding the element to the front of the 
	 * childPath.
	 * 
	 * @param childPath the elements of the child path or null
	 * @param element element to add to the front of the path
	 */
	public DefaultPath(Object element, Path childPath)
	{
		super();
		
		if (element == null)
		{
			throw new IllegalArgumentException("element argument cannot be null");
		}
				
		fPath.add(element);
		
		if (childPath != null)
		{
			for (int i = 0; i < childPath.getPathCount(); i++)
			{
				fPath.add(childPath.getPathComponent(i));
			}
		}
	}

    /**
     * Returns an ordered array of Objects containing the components of this
     * Path. The first element (index 0) is the root.
     *
     * @return an array of Objects representing the Path
     */
	public Object[] getPath()
	{
		return fPath.toArray();
	}

    /**
     * Returns the last component of this path.
     *
     * @return the Object at the end of the path.
     */
	public Object getLastPathComponent()
	{
		Object element = null;
		int size = fPath.size();
		
		if (size > 0)
		{
			element = fPath.get(size - 1);
		}
		
		return element;
	}

    /**
     * Returns the first component of this path.
     *
     * @return the Object at the start of the path
     */
    public Object getFirstPathComponent()
    {
		Object element = null;
		int size = fPath.size();
		
		if (size > 0)
		{
			element = fPath.get(0);
		}
		
		return element;
    }

    /**
     * Returns the number of elements in the path.
     *
     * @return an int giving a count of items the path
     */
	public int getPathCount()
	{
		return fPath.size();
	}

    /**
     * Returns the path component at the specified index or null if one does
     * not exist.
     *
     * @param element  an int specifying an element in the path, where
     *                 0 is the first element in the path
     * @return the Object at that index location or null
     */
	public Object getPathComponent(int element)
	{
		if (element < 0 || element > fPath.size() - 1)
		{
			return null;
		}
		
		return fPath.get(element);
	}

    /**
     * Returns a path containing all the elements of this object, except
     * the last path component.
     * 
     * @return the parent Path
     */
	public Path getParentPath()
	{
		int size = fPath.size();
		ArrayList newArray = (ArrayList) fPath.clone();
		
		// Check if the path is empty
		if (size > 0)
		{
			newArray.remove(size - 1);
		}
		
		return new DefaultPath(newArray.toArray());
	}

    /**
     * Returns a path containing all the elements of this path reversed.
     * 
     * @return a new reversed Path
     */
	public Path getReversedPath()
	{
		int size = fPath.size();
		ArrayList newArray = new ArrayList(size);
		
		// Check if the path is empty
		for (int i = size-1; i >= 0; i--)
		{
			newArray.add(fPath.get(i));
		}
		
		return new DefaultPath(newArray.toArray());
	}

    /**
	 * Determine if the given Path is a parent of this Path or equal.
	 * 
	 * @param path the Path to test
	 * @return true if the given path is a subpath or the same path.
	 */
	public boolean startsWith(Path path)
	{
		boolean result = true;
		
		// Check parameter
		if (path == null)
		{
			return false;
		}
		else if (path == this)
		{
			return true;
		}
		
		int pathSize = path.getPathCount();
		
		if (pathSize > getPathCount())
		{
			result = false;
		}
		else
		{
			// Compare each element
			for (int i = 0; i < pathSize && result == true; i++)
			{
				if (!(fPath.get(i).equals(path.getPathComponent(i))))
				{
					result = false;
				}
			}
		}
		
		return result;
	}
	
    /**
	 * Tests two Paths for equality by checking each element of the paths
	 * for equality. Two paths are considered equal if they are of the same
	 * length, and contain the same elements (<code>.equals</code>).
	 * 
	 * @param obj the Object to compare
	 * @return true if equal
	 */
	public boolean equals(Object obj)
	{
		boolean result = true;
		
		if (obj == this)
		{
			return true;
		}
		
		if (obj instanceof Path)
		{
			Path pathObj = (Path) obj;

			if (getPathCount() != pathObj.getPathCount())
			{
				result = false;
			}
			else
			{
				int size = getPathCount();
				
				// Compare each element
				for (int i = 0; i < size && result == true; i++)
				{
					if (!(fPath.get(i).equals(pathObj.getPathComponent(i))))
					{
						result = false;
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 *  Returns a String representation of this Path.
	 * 
	 *  @return A String representation of this Path
	 */
	public String toString()
	{
		return (fPath.toString());
	}
}


//--- Development History ---------------------------------------------------
//
//  $Log: DefaultPath.java,v $
//  Revision 1.3  2006/05/04 14:50:49  tames
//  Fixed null pointer exception with respect to comparing with a null path.
//
//  Revision 1.2  2006/04/21 15:54:11  tames_cvs
//  Added a no arg constructor to create an empty path.
//
//  Revision 1.1  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.1  2006/04/18 03:51:18  tames
//  Relocated or new implementation.
//
//  Revision 1.1  2005/02/04 21:49:46  tames_cvs
//  Relocated file. Also added a startsWith(Path) method.
//
//  Revision 1.2  2005/02/01 16:51:49  tames
//  Added constructor and reverse method.
//
//  Revision 1.1  2005/01/21 20:15:15  tames
//  Initial version
//
//