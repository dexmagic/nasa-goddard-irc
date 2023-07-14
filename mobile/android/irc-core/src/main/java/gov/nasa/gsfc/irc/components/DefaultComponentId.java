//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/DefaultComponentId.java,v 1.1 2006/01/23 17:59:51 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultComponentId.java,v $
//	Revision 1.1  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.1  2004/07/22 20:14:58  chostetter_cvs
//	Data, Component namespace work
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

import gov.nasa.gsfc.commons.types.namespaces.DefaultNamespaceMemberId;
import gov.nasa.gsfc.commons.types.namespaces.Namespace;
import gov.nasa.gsfc.commons.types.namespaces.Namespaces;


/**
 *  A ComponentId serves as a globally unique and persistent identifier of a 
 *  Component.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:51 $
 *  @author	Carl F. Hostetter
**/

public class DefaultComponentId extends DefaultNamespaceMemberId 
	implements ComponentId
{
	/**
	 *  Constructs a new ComponentId that identifies a Component having the given 
	 *  sequenced name within the given Namespace.
	 * 
	 *  @param sequencedName The sequenced name of the identified Component
	 *  @param namespace The Namespace of the identified Component
	 **/

	public DefaultComponentId(String sequencedName, Namespace namespace)
	{
		super(sequencedName, namespace);
	}
		
	
	/**
	 *  Constructs a new ComponentId that identifies a Component having the given 
	 *  sequenced name within the Namespace having the given fully-qualified name.
	 * 
	 *  @param sequencedName The sequenced name of the identified Component
	 *  @param namespaceName The fully-qualified name of the Namespace of the 
	 *  		identified Component
	 **/

	public DefaultComponentId(String sequencedName, String namespaceName)
	{
		super(Namespaces.formFullyQualifiedName(sequencedName, namespaceName));
	}
		
	
	/**
	 *  Constructs a new ComponentId that identifies a Component having the given 
	 *  fully-qualified name.
	 * 
	 *  @param fullyQualifiedName The fully-qualified name of the identified 
	 *  		Component
	 **/

	public DefaultComponentId(String fullyQualifiedName)
	{
		super(fullyQualifiedName);
	}
}

