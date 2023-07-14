//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: Cml.java,v $
//  Revision 1.6  2006/02/04 17:10:04  tames
//  Added start attribute.
//
//  Revision 1.5  2006/01/13 03:20:11  tames
//  Added initial support for Component set descriptions.
//
//  Revision 1.4  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.3  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//  Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
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

package gov.nasa.gsfc.irc.components.description;

import gov.nasa.gsfc.irc.description.xml.xsd.Xsd;


/**
 * The iCML nterface serves as a central location for defining the constants 
 * associated with the Component Markup Language (CML) XML schema.
 *
 * <p><b>Note this class is a place holder for a more robust implementation that 
 * that will be developed in the future. </b>
 * <p>
 * <p>Developers should be certain to refer to the IRC schemas to gain a better 
 * understanding of the structure and content of the data used to build the 
 * descriptors.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/02/04 17:10:04 $
 * @author Carl F. Hostetter
**/

public interface Cml extends Xsd
{
	static final String PKG = "gov.nasa.gsfc.irc.components.description";
	
	//Default Component commands:
	public static final String START_CMD			= "start";
	public static final String STOP_CMD			= "stop";
	public static final String KILL_CMD			= "kill";
	public static final String SET_PROPERTIES_CMD	= "set_properties";

	//--- Attributes 
	public static final String A_STARTED	= "start";

	//--- Elements 
	static final String E_COMPONENT	= "Component";
	static final String E_COMPONENT_SET	= "ComponentSet";
	static final String E_PROPERTY	= "Property";

	//--- Classes
	public static final String C_COMPONENT_SET	 = PKG + DOT + "ComponentSetDescriptor";
	public static final String C_COMPONENT	 = PKG + DOT + "ComponentDescriptor";
	public static final String C_PROPERTY	 = PKG + DOT + "PropertyDescriptor";
	
	//--- CML MML Namespaces
	public static final String N_COMPONENT = "ComponentType";
	public static final String N_COMPONENT_SET = "ComponentSetType";
}
