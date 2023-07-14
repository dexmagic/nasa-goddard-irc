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

package gov.nasa.gsfc.irc.gui;

import java.awt.Container;
import java.net.URL;

/**
 * @version	$Date: 2006/01/23 17:59:55 $
 * @author T. Ames
 */
public interface GuiFactory
{
	/**
	 * Renders the specified GUI description. To make the rendered
	 * object visible call the <code>setVisible(true)</code> method on the 
	 * returned Container.
	 * 
	 * @param url <code>URL</code> url pointing to an XML descriptor
	 * @return <code>Container</code>- instanced swing object tree root
	 * @throws Exception
	 */
	public Container render(URL url) throws Exception;

	/**
	 * Renders the specified GUI description. To make the rendered
	 * object visible call the <code>setVisible(true)</code> method on the 
	 * returned Container.
	 * 
	 * @param resource resource pointing to an XML descriptor
	 * @return <code>Container</code>- instanced swing object tree root
	 * @throws Exception
	 */
	public Container render(String resource) throws Exception;

	/**
	 * Renders the specified GUI description passing a client to the
	 * builder. To make the rendered
	 * object visible call the <code>setVisible(true)</code> method on the 
	 * returned Container.
	 * 
	 * @param url <code>URL</code> url pointing to an XML descriptor
	 * @param client <code>Object</code> owner of this instance
	 * @return <code>Container</code>- instanced swing object tree root
	 * @throws Exception
	 */
	public Container render(URL url, Object client) 
		throws Exception;

	/**
	 * Renders the specified GUI description passing a client to the
	 * builder. To make the rendered
	 * object visible call the <code>setVisible(true)</code> method on the 
	 * returned Container.
	 * 
	 * @param resource resource pointing to an XML descriptor
	 * @param client <code>Object</code> owner of this instance
	 * @return <code>Container</code>- instanced swing object tree root
	 * @throws Exception
	 */
	public Container render(String resource, Object client) 
		throws Exception;

	/**
	 * Returns the UI component with the given id or null.
	 * 
	 * @param id <code>String</code> assigned name
	 * @return the GUI component with the given name or null if not found.
	 */
	public Object find(String id);

	/**
	 * Returns the root component of the generated Swing UI.
	 * 
	 * @return the root component of the javax.swing ui
	 */
	public Container getRootComponent();
}

//--- Development History  ---------------------------------------------------
//
//  $Log: GuiFactory.java,v $
//  Revision 1.3  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/12/15 15:27:58  tames
//  Changed return type of the find method to allow finding any id Object not
//  just Components.
//
//  Revision 1.1  2005/01/10 23:08:54  tames_cvs
//  Renamed GuiBuilder to GuiFactory to be consistent with other factory
//  names in the IRC framework.
//
//  Revision 1.6  2004/08/31 22:03:09  tames
//  Added find method
//
//  Revision 1.5  2004/08/29 22:45:40  tames
//  Added getRootComponent method
//
//  Revision 1.4  2004/08/26 14:36:08  tames
//  Render no longer sets the frame visible
//
//  Revision 1.3  2004/08/23 13:58:52  tames
//  Changed api and added support for defining custom tags from the
//  global map.
//
//  Revision 1.2  2004/06/08 14:17:52  tames_cvs
//  More partial implementation
//
//  Revision 1.1  2004/06/07 14:36:38  tames_cvs
//  Initial Version
//
