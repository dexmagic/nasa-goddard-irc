//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

package gov.nasa.gsfc.commons.types.namespaces;




/**
 *  A NamedObject is an Object that has a name.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public abstract class AbstractNamedObject implements NamedObject
{
	private String fName;
	
	
	/**
	 *  Default constructor of a AbstractNamedObject.
	 *
	 **/

	public AbstractNamedObject()
	{
		this(null);
	}
	
	
	/**
	 *  Constructs a new AbstractNamedObject having the given name.
	 *
	 *  @param name The desired name of the new AbstractNamedObject
	 **/

	public AbstractNamedObject(String name)
	{
		_setName(name);
	}

	
	/**
	 * Returns a clone of this AbstractNamedObject.
	 *
	 * @return A clone of this AbstractNamedObject
	 **/

	protected Object clone()
	{
		AbstractNamedObject result = null;
		
		try
		{
			result = (AbstractNamedObject) super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			
		}

		result.fName = fName;
		
		return (result);
	}
	
	
	/**
	 *  Sets the name of this NamedObject to the given name.
	 *
	 *  @param name The desired new name of this NamedObject
	 **/

	protected void _setName(String name)
	{
		fName = name;
		
		if (fName == null)
		{
			fName = HasName.DEFAULT_NAME;
		}
	}
	
	
	/**
	 *  Returns the name of this AbstractNamedObject.
	 *
	 *  @return	The name of this AbstractNamedObject.
	 **/

	public String getName()
	{
		return (fName);
	}
	
	
	/**
	 *  Returns a String representation of this AbstractNamedObject.
	 *
	 *  @return A String representation of this AbstractNamedObject
	 **/

	public String toString()
	{
		return (fName);
	}
}

//--- Development History ----------------------------------------------------
//
//$Log: AbstractNamedObject.java,v $
//Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//Massive Namespace-related changes
//
//Revision 1.5  2005/12/06 19:24:07  tames_cvs
//File comment header change only. Moved history to end of file.
//
//Revision 1.4  2004/08/09 17:25:09  tames_cvs
//Made serializable
//
//Revision 1.3  2004/07/17 18:39:02  chostetter_cvs
//Name, descriptor modification work
//
//Revision 1.2  2004/07/12 19:04:45  chostetter_cvs
//Added ability to find BasisBundleId, Components by their fully-qualified name
//
//Revision 1.1  2004/07/06 13:40:00  chostetter_cvs
//Commons package restructuring
//
//Revision 1.7  2004/06/02 22:33:27  chostetter_cvs
//Namespace revisions
//
//Revision 1.6  2004/05/28 05:58:20  chostetter_cvs
//More Namespace, DataSpace, Descriptor worl
//
//Revision 1.5  2004/05/27 23:29:26  chostetter_cvs
//More Namespace related changes
//
//Revision 1.4  2004/05/27 19:47:45  chostetter_cvs
//More Namespace, DataSpace changes
//
//Revision 1.3  2004/05/27 18:21:37  tames_cvs
//CLASS_NAME assignment fix
//
//Revision 1.2  2004/05/17 22:01:10  chostetter_cvs
//Further data-related work
//
//Revision 1.1  2004/04/30 21:20:33  chostetter_cvs
//Initial version
//
