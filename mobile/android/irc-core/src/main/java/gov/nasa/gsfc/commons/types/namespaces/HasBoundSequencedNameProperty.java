//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//	$Log: HasBoundSequencedNameProperty.java,v $
//	Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
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

package gov.nasa.gsfc.commons.types.namespaces;

import java.beans.PropertyChangeListener;


/**
 *  The HasBoundSequencedNameProperty is implemented by all beans that have a 
 *  bound sequenced name property.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/23 17:59:50 $
 *  @author Carl F. Hostetter
**/

public interface HasBoundSequencedNameProperty extends HasSequencedName
{
	public static final String SEQUENCED_NAME_PROPERTY = "Sequenced Name";
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the 
	 * sequenced name of this bean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addSequencedNameListener(PropertyChangeListener listener);


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in the 
	 * sequenced name of this bean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removeSequencedNameListener(PropertyChangeListener listener);
}
