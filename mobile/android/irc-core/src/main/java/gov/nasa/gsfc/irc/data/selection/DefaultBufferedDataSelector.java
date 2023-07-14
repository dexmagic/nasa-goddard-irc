//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/DefaultBufferedDataSelector.java,v 1.3 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultBufferedDataSelector.java,v $
//  Revision 1.3  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.2  2006/03/31 21:57:38  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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

package gov.nasa.gsfc.irc.data.selection;


/**
 * A BufferedDataSelector selects data in a buffered fashion.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public class DefaultBufferedDataSelector extends AbstractBufferedDataSelector
{
	/**
	 * Constructs a new DefaultBufferedDataSelector that will buffer all 
	 * selection data into a buffer of the given size and apply the given 
	 * DataSelector to the buffer.
	 *
	 * @param selector A DataSelector
	 * @param size The size of the data selection buffer
	 * @return A new DefaultBufferedDataSelector that will buffer all 
	 * 		selection data into a buffer of the given size and apply the given 
	 * 		DataSelector to the buffer		
	**/
	
	public DefaultBufferedDataSelector(DataSelector selector, int size)
	{
		super(selector, size);
	}
}
