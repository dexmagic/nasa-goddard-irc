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
 * The DataSvgComponent interface defines the methods required by an
 * implementation that accepts 
 * {@link gov.nasa.gsfc.irc.gui.svg.SvgUpdater SvgUpdater} instances for 
 * updating an {@link gov.nasa.gsfc.irc.gui.svg.SvgComponent SvgComponent}.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/11/07 22:12:02 $
 * @author 	Troy Ames
 */
public interface DataSvgComponent
{
    /**
	 * Adds an updater to this Component. When this component receives new data, 
	 * each updater added will have their <code>update</code> method
	 * called in the order they were added.
	 * 
	 * @param updater The updater to add to this component.
	 * @see SvgUpdater#update(SvgComponent, DataSet)
	 */
	public void addUpdater(SvgUpdater updater);

    /**
	 * Removes an updater from this Component.
	 * 
	 * @param updater The updater to remove from this component.
	 */
	public void removeUpdater(SvgUpdater updater);

	/**
	 * Get the updaters that have been added to this component.
	 * 
	 * @return an array of known updaters
	 */
	public SvgUpdater[] getUpdaters();

}


//--- Development History  ---------------------------------------------------
//
//  $Log: DataSvgComponent.java,v $
//  Revision 1.1  2005/11/07 22:12:02  tames
//  SVG support
//
//