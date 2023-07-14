//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
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

package gov.nasa.gsfc.irc.description.xml;

import java.util.Set;

public interface IrcElementDescriptor extends XmlDescriptor
{
	/**
	 * Sets the type of the IRC element described by this IrcElementDescriptor to 
	 * the given type String. This is the String to be written to the XML for an IRC 
	 * element that is resolved to a Class upon unmarshalling. This will also 
	 * attempt to set the implementation Class name to the corresponding Class for 
	 * the specified type.
	 * 
	 * @param type A String indicating the type (possibly Class) of the IRC 
	 * 		element described by this IrcElementDescriptor 
	 **/

	public void setType(String type);

	/**
	 * Returns the type of the IRC element described by this IrcElementDescriptor.
	 *
	 * @return The type of the IRC element described by this IrcElementDescriptor 
	 **/

	public String getType();

	/**
	 * Returns the class of the IRC element described by this IrcElementDescriptor.
	 *
	 * @return The class of the IRC element described by this IrcElementDescriptor 
	 **/
	public Class getTypeClass();

	/**
	 * Returns the name of the implementation Class of the IRC element described by 
	 * this IrcElementDescriptor.
	 *
	 * @return The name of the implementation Class of the IRC element described by 
	 * 		this IrcElementDescriptor
	 **/

	public String getClassName();

	/**
	 * Get the localized name of the IRC element described by this 
	 * IrcElementDescriptor.
	 *
	 * @return The localized display name for the element.
	 *	This defaults to the same as its programmatic name from getName.
	 */
	public String getDisplayName();

	/**
	 * Sets the localized display name of this element described by this
	 * IrcElementDescriptor.
	 *
	 * @param displayName  The localized display name for the
	 *		element.
	 */
	public void setDisplayName(String displayName);

	/**
	 * Gets the short description of this feature.
	 *
	 * @return  A localized short description associated with this 
	 *   property/method/event.  This defaults to be the display name.
	 */
	public String getShortDescription();

	/**
	 * You can associate a short descriptive string with a feature.  Normally
	 * these descriptive strings should be less than about 40 characters.
	 * @param text  A (localized) short description to be associated with
	 * this property/method/event.
	 */
	public void setShortDescription(String text);

	/**
	 * Returns the tool-tip text of this Object.
	 *
	 * @return The tool-tip text of this Object
	 **/

	public String getToolTip();

	/**
	 * Returns the String value of the Parameter of the IRC element described by this 
	 * IrcElementDescriptor having the given name (if any).
	 *
	 * @param name The name of a Parameter of the IRC element described by this 
	 * 		IrcElementDescriptor
	 * @return The String value of the Parameter of the IRC element described by this 
	 * 		IrcElementDescriptor having the given name (if any)
	 **/

	public String getParameter(String name);

	/**
	 * Returns the (unmodifiable) Set of the names of the parameters of the 
	 * IRC element described by this IrcElementDescriptor (if any).
	 *
	 * @return The (unmodifiable) Set of the names of the parameters of the 
	 * 		IRC element described by this IrcElementDescriptor (if any)
	 **/

	public Set getParameterNames();
}