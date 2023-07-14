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

import java.io.Serializable;


/**
 * Represents a path to a device or node. A path is an ordered array of Objects.
 * The meaning of the order and the individual elements of the Path are
 * application specific; however the typical case is that the first element
 * (index 0) in the path represents the first node in a tree structure leading
 * to a destination. Paths are Serializable.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/04/18 14:02:49 $
 * @author Troy Ames
 */
public interface Path extends Serializable
{
	public static final String DOT = ".";
	
    /**
     * Returns an ordered array of Objects containing the components of this
     * Path.
     *
     * @return an array of Objects representing the Path
     */
    public Object[] getPath();
    
    /**
     * Returns the last component of this path.
     *
     * @return the Object at the end of the path
     */
    public Object getLastPathComponent();
    
    /**
     * Returns the first component of this path.
     *
     * @return the Object at the start of the path
     */
    public Object getFirstPathComponent();
    
    /**
     * Returns the number of elements in the path.
     *
     * @return an int giving a count of items the path
     */
    public int getPathCount();
    
    /**
     * Returns the path component at the specified index.
     *
     * @param element  an int specifying an element in the path, where
     *                 0 is the first element in the path
     * @return the Object at that index location or null if one does not exist
     */
    public Object getPathComponent(int element);
    
    /**
     * Returns a path containing all the elements of this path, except
     * the last path component.
     */
    public Path getParentPath();

    /**
	 * Determine if the given Path is a subpath of or equal to this Path.
	 * 
	 * @param path the Path to test
	 * @return true if the given path is a subpath or the same path.
	 */
	public boolean startsWith(Path path);

	/**
     * Returns a path containing all the elements of this path reversed.
     * 
     * @return a new reversed Path
     */
	public Path getReversedPath();
}


//--- Development History  ---------------------------------------------------
//
//  $Log: Path.java,v $
//  Revision 1.1  2006/04/18 14:02:49  tames
//  Reflects relocated Path and DefaultPath.
//
//  Revision 1.1  2006/04/18 03:51:18  tames
//  Relocated or new implementation.
//
//  Revision 1.2  2006/01/23 17:59:51  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2005/02/04 21:49:46  tames_cvs
//  Relocated file. Also added a startsWith(Path) method.
//
//  Revision 1.2  2005/02/01 16:53:21  tames
//  Added getReversedPath method
//
//  Revision 1.1  2005/01/21 20:15:16  tames
//  Initial version
//
//