//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/ComponentFactory.java,v 1.5 2004/07/12 14:26:24 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: ComponentFactory.java,v $
//	Revision 1.5  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.4  2004/06/03 00:19:59  chostetter_cvs
//	Organized imports
//	
//	Revision 1.3  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.2  2004/05/28 05:58:20  chostetter_cvs
//	More Namespace, DataSpace, Descriptor worl
//	
//	Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//	Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
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

package gov.nasa.gsfc.irc.components;

import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;



/**
 *  A ComponentFactory creates and configures new instances of IRC Components 
 *  upon request.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2004/07/12 14:26:24 $
 *  @author Carl F. Hostetter
 */


public interface ComponentFactory
{
	/**
	 *	Creates and returns a new Component, as described by the given 
	 *	ComponentDescriptor and having the given name.
	 *
	 *	@param descriptor The ComponentDescriptor that describes the
	 *		desired Component
	 *	@param name The name of the new Component
	 *  @return A new Component, as described by the given 
	 *		ComponentDescriptor and having the given name
	 */
	
	public MinimalComponent createComponent
		(ComponentDescriptor descriptor, String name);


	/**
	 *	Creates and returns a new Component, as described by the given 
	 *	ComponentDescriptor.
	 *
	 *	@param descriptor The ComponentDescriptor that describes the
	 *		desired Component
	 *  @return A new Component, as described by the given 
	 *		ComponentDescriptor
	 */
	
	public MinimalComponent createComponent(ComponentDescriptor descriptor);


	/**
	 *	Creates and returns a new, default instantiation of a 
	 *  Component of the type indicated by the given Class name, and 
	 *  having the given name.
	 *
	 *	@param className The name of a Component Class
	 *	@param componentName The name of the new Component
	 *  @return A new Component of the indicated type and having the 
	 * 		given name
	 */
	
	public MinimalComponent createComponent(String className, 
		String componentName);
	
	
	/**
	 *	Creates and returns a new Component of the type indicated by the 
	 *  given Class name.
	 *
	 *	@param className The name of a Component Class
	 *  @return A new Component of the indicated type
	 */
	
	public MinimalComponent createComponent(String className);
}
