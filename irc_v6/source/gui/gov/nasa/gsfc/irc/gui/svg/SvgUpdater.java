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

package gov.nasa.gsfc.irc.gui.svg;

import gov.nasa.gsfc.irc.data.DataSet;

/**
 * This interface defines the method used by an DataSvgComponent 
 * to update a SVG graphic. Implementing classes can be used
 * in a Decorator pattern for a component that delegates the actual modification
 * to one or more SvgUpdaters. The implementing class can modify the SVG to 
 * influence other renderers that might follow.
 * 
 * <p>Note the parent component may not be a listener for data so implementors
 * of this interface must handle the case where the 
 * <code>update(SvgComponent svg, DataSet dataSet)</code> 
 * method is passed null for the dataSet argument.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/11/07 22:12:02 $
 * @author 	Troy Ames
 */
public interface SvgUpdater
{

	/**
	 * Modify the given svg context.  This method
	 * is used by a parent component to update its SVG context.<p>
	 *  
	 * @param svg  the SVG context on which to update.
	 * @param dataSet the data for the update if applicable, may be null.
	 */
	public void update(SvgComponent svg, DataSet dataSet);
}

//--- Development History  ---------------------------------------------------
//
//  $Log: SvgUpdater.java,v $
//  Revision 1.1  2005/11/07 22:12:02  tames
//  SVG support
//
//