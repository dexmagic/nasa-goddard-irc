//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//  $Log: HasBoundBeanProperties.java,v $
//  Revision 1.1  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
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

package gov.nasa.gsfc.commons.properties.beans;

import java.beans.PropertyChangeListener;


/**
 *  An Object that HasBoundBeanProperties maintains a set of bound bean 
 *  properties, reporting changes in those bound properties to any interested 
 *  listeners.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:54 $
 *  @author Carl F. Hostetter
**/

public interface HasBoundBeanProperties
{
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in 
	 * any Property of this Object.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addPropertyChangeListener(PropertyChangeListener listener);


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * any Property of this Object.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removePropertyChangeListener(PropertyChangeListener listener);


	/**
	 * Adds the given PropertyChangeListener as a listener for changes in 
	 * the specified Property of this Object.
	 *
	 * @param propertyName The name of a Property of this Object
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addPropertyChangeListener
		(String propertyName, PropertyChangeListener listener);


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in 
	 * the specified property of this Object.
	 *
	 * @param propertyName The name of a property of this Object
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removePropertyChangeListener
		(String propertyName, PropertyChangeListener listener);
}
