//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDescriptor.java,v $
//  Revision 1.6  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.4  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.3  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.2  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.1  2004/05/28 05:58:20  chostetter_cvs
//  More Namespace, DataSpace, Descriptor worl
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

package gov.nasa.gsfc.irc.description;

import gov.nasa.gsfc.commons.types.namespaces.AbstractMember;


/**
 * A Descriptor is a named Object that describes other Objects.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version  $Date: 2006/01/23 17:59:54 $
 * @author Carl F. Hostetter   
**/

public abstract class AbstractDescriptor extends AbstractMember 
	implements Descriptor
{	
	/**
	 *  Constructs a new Descriptor having the given base name and no name 
	 *  qualifier.
	 * 
	 *  @param name The base name of the new Descriptor
	 **/

	public AbstractDescriptor(String name)
	{
		this(name, null);
	}
	
	
	/**
	 *  Constructs a new Descriptor having the given base name and name qualifier.
	 * 
	 *  @param name The base name of the new Descriptor
	 *  @param nameQualifier The name qualifier of the new Descriptor
	 **/

	public AbstractDescriptor(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}
	
	
	/**
	 * Returns a clone of this Descriptor.
	 *
	 * @return A clone of this Descriptor
	 **/

	public Object clone()
	{
		AbstractDescriptor result = (AbstractDescriptor) super.clone();
		
		return (result);
	}
}
