//=== File Prolog ============================================================
//
//  This code was developed by NASA Goddard Space Flight Center, Code 588 
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.library.ports;

import gov.nasa.gsfc.commons.types.namespaces.Namespace;
import gov.nasa.gsfc.irc.devices.ports.AbstractCompositePort;
import gov.nasa.gsfc.irc.devices.ports.Port;
import gov.nasa.gsfc.irc.devices.ports.PortDescriptor;

/**
 * This Port implementation will filter Output Messages and only pass on
 * Messages that contain this port in the event path.
 * 
 * @version	$Date: 2006/08/01 19:55:48 $
 * @author		T. Ames
 */
public class FilteringPort extends AbstractCompositePort implements Port
{
	public static final String DEFAULT_NAME = "Filtering Port";
	
	
	/**
	 *	Constructs a new FilteringPort having a default name.
	 *
	 */
	
	public FilteringPort()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 *	Constructs a new FilteringPort having the given base name.
	 *
	 *	@param name The base name of the new FilteringPort
	 */
	
	public FilteringPort(String name)
	{
		super(name);
	}
	
	
	/**
	 *  Constructs a new FilteringPort configured according to the given 
	 *  PortDescriptor.
	 * 
	 *  @param descriptor The PortDescriptor of the new FilteringPort
	 **/

	public FilteringPort(PortDescriptor descriptor)
	{
		super(descriptor);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: FilteringPort.java,v $
//  Revision 1.4  2006/08/01 19:55:48  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.3  2006/03/07 23:32:42  chostetter_cvs
//  NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
//  Revision 1.2  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.1  2005/02/01 18:51:13  tames
//  Initial version.
//
//  Revision 1.8  2004/10/14 15:22:16  chostetter_cvs
//  Port descriptor-oriented refactoring
//
//  Revision 1.7  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.6  2004/09/27 20:39:04  tames
//  Reflects a refactoring of port architecture.
//
//  Revision 1.5  2004/08/03 20:35:28  tames_cvs
//  Many configuration and descriptor changes
//
//  Revision 1.4  2004/07/27 21:11:34  tames_cvs
//  Port redesign implementation
//
//  Revision 1.3  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.2  2004/06/07 14:38:48  tames_cvs
//  Changed return value of open and close
//
//  Revision 1.1  2004/04/30 20:41:08  tames_cvs
//  Initial Version
//
