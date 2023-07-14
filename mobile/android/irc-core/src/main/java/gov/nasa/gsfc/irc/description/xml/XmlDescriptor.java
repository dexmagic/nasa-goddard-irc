//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 
// for the Instrument Remote Control (IRC) project.
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

import org.jdom.Element;

import gov.nasa.gsfc.commons.xml.SubtreeMap;
import gov.nasa.gsfc.irc.description.Descriptor;

/**
 * An XmlDescriptor is a Descriptor that can be marshalled to and unmarshalled from 
 * JDOM Elements (and this to and from XML representations).
 * 
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version	$Date: 2006/01/23 17:59:54 $
 * @author 	Troy Ames
**/

public interface XmlDescriptor extends Descriptor
{
	/**
	 * Sets the SubtreeMap of this XmlDescriptor to the given SubtreeMap.
	 *
	 * @param subtreeMap The SubtreeMap of this XmlDescriptor
	 **/

	public void setSubtreeMap(SubtreeMap subtreeMap);

	/**
	 * Returns the SubtreeMap of this XmlDescriptor.
	 *
	 * @return The SubtreeMap of this XmlDescriptor
	 **/

	public SubtreeMap getSubtreeMap();

	/**
	 * Returns the parent Descriptor of this XmlDescriptor (if any). 
	 *
	 * @return The parent Descriptor of this XmlDescriptor (if any) 		
	 **/

	public Descriptor getParent();

	/**
	 * Returns the JDOM Element from which this XmlDescriptor was unmarshalled 
	 * (if any).
	 *
	 * @return The JDOM Element from which this XmlDescriptor was unmarshalled 
	 * 		(if any)  
	 **/

	public Element getElement();

	/**
	 * Marshalls this XmlDescriptor to the given JDOM Element.
	 *
	 * @param element The JDOM Element into which to marshall this XmlDescriptor
	 **/

	public void xmlMarshall(Element element);

}