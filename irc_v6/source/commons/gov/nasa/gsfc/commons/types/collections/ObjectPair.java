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

package gov.nasa.gsfc.commons.types.collections;

/**
 *  The ObjectPair class defines a pair of Objects (first, second).
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/20 08:11:51 $
 * @author 	Troy Ames
 */
public class ObjectPair
{
	public Object first = null;
	public Object second = null;

	/**
	 * Default constructor creates an instance with both elements equal to
	 * <code>null</code>.
	 */
	public ObjectPair()
	{
		super();
	}
	
	/**
	 * Creates an instance with elements equal to
	 * the specified objects.
	 * 
	 * @param first 	first Object of pair
	 * @param second 	second Object of pair
	 */
	public ObjectPair(Object first, Object second)
	{
		super();
		
		this.first = first;
		this.second = second;
	}
	
    /**
	 * Returns a string representation of the object. This method returns a
	 * string equal to the value of: 
	 * 
	 * <blockquote>
	 * <pre>
	 *  '[' + first.toString() + ',' + second.toString() + ']')
	 * </pre>
	 * </blockquote>
	 * 
	 * @return a string representation of the pair.
	 */
	public String toString()
	{
		return ("[" + first + "," + second + "]");
	}
}


//--- Development History ---------------------------------------------------
//
//  $Log: ObjectPair.java,v $
//  Revision 1.1  2005/01/20 08:11:51  tames
//  Initial version
//
//