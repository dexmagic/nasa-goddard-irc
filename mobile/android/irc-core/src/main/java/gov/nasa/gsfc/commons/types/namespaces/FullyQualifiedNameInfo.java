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
 *  A FullyQualifiedNameInfo is an Object that maintains fully-qualified name 
 *  information.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public class FullyQualifiedNameInfo extends AbstractNamedObject 
	implements HasFullyQualifiedName
{
	private String fNameQualifier;
	
	
	/**
	 *  DefaultConstructor of a FullyQualifiedNameInfo.
	 * 
	 **/

	public FullyQualifiedNameInfo()
	{
		this(null, null);
	}
	
	
	/**
	 *  Constructs a FullyQualifiedNameInfo for the given base name and no 
	 *  name qualifier.
	 * 
	 *  @param name The base name of the new FullyQualifiedNamedObject
	 **/

	public FullyQualifiedNameInfo(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a FullyQualifiedNamedInfo for the given base name and name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new FullyQualifiedNamedObject
	 *  @param nameQualifier The name qualifier of the new FullyQualifiedNamedObject
	 **/

	public FullyQualifiedNameInfo(String name, String nameQualifier)
	{
		super(name);
		
		fNameQualifier = nameQualifier;
	}
	/**
	 *  Sets the base name of this FullyQualifiedNamedObject to the given name.
	 *
	 *  @param name The new base name of this FullyQualifiedNamedObject
	 **/

	protected void setName(String name)
	{
		super._setName(name);
	}
	
	
	/**
	 *  Sets the name qualifier of this AbstractMember to the 
	 *  given name qualifier.
	 *
	 *  @param nameQualifier The desired new name qualifier of this 
	 *  		AbstractMember
	 **/

	protected void setNameQualifier(String nameQualifier)
	{
		fNameQualifier = nameQualifier;
	}
	
	
	/**
	 *  Returns the name qualifier of this AbstractMember.
	 *
	 *  @return	The name qualifier of this AbstractMember
	 **/

	public String getNameQualifier()
	{
		return (fNameQualifier);
	}
	
	
	/**
	 *  Returns the fully-qualified name of this AbstractMember.
	 *
	 *  @return The fully-qualified name of this AbstractMember
	 **/

	public String getFullyQualifiedName()
	{
		return (Namespaces.formFullyQualifiedName(getName(), fNameQualifier));
	}
	
	
}

//--- Development History ----------------------------------------------------
//
// $Log: FullyQualifiedNameInfo.java,v $
// Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
// Massive Namespace-related changes
//
//
