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

package gov.nasa.gsfc.irc.gui.vis;


/**
 * A component that can accept one or more VisRenderers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/04/06 19:31:53 $
 * @author 	Troy Ames
 */
public interface VisComponent
{
	/**
	 * Adds a renderer to this component. When this component is asked to repaint 
	 * itself, each renderer added will have their <code>draw</code> method
	 * called.
	 * 
	 * @param renderer The renderer to add to this component.
	 * @see VisRenderer#draw(Graphics2D, Rectangle2D)
	 */
	public void addRenderer(VisRenderer renderer);

	/**
	 * Get the renderers that have been added to this component.
	 * 
	 * @return an array of known renderers
	 */
	public VisRenderer[] getRenderers();
}

//--- Development History  ---------------------------------------------------
//
//  $Log: VisComponent.java,v $
//  Revision 1.3  2005/04/06 19:31:53  chostetter_cvs
//  Organized imports
//
//  Revision 1.2  2004/12/20 22:41:29  tames
//  Visualization property editing support added.
//
//  Revision 1.1  2004/11/08 23:12:57  tames
//  Initial Version
//
//