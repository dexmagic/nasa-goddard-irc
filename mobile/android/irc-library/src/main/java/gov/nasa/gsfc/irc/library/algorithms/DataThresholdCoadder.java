//=== File Prolog ============================================================
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *   Neither the author, their corporation, nor NASA is responsible for
//      any consequence of the use of this software.
//  *   The origin of this software must not be misrepresented either by
//      explicit claim or by omission.
//  *   Altered versions of this software must be plainly marked as such.
//  *   This notice may not be removed or altered.
//
//=== End File Prolog ========================================================
package gov.nasa.gsfc.irc.library.algorithms;

import gov.nasa.gsfc.irc.library.processors.strategies.coadder.IntraBasisSetDataThresholdStrategy;

import java.util.ArrayList;


/**
 * Performs coadding when certain data thresholds are met in a basis bundle.  The thresholds
 * are instances of {@link gov.nasa.gsfc.irc.library.processors.strategies.coadder.IntraBasisSetDataThresholdStrategy.Threshold}
 * and specify a numeric value and a direction (rising or falling).  They
 * also specify which data buffers should be checked (in the form of a 
 * name prefix).  Finally, they specify whether the threshold is included
 * in the 'current' coadding group or is padded to the following group.
 * <p>
 * All numeric data buffers in the basis bundle are coadded. 
 * <p>
 * Note, unit tests for this class are currently in the MarkIII project.
 * 
 * This code was developed for NASA, Goddard Space Flight Center, Code 588 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/05/10 17:29:11 $
 * @author Steve Maher
 */
public class DataThresholdCoadder extends Coadder
{
	public static final String DEFAULT_NAME = "Data Threshold Coadder";
	
	protected IntraBasisSetDataThresholdStrategy fStrategy;
	

	/**
	 * 
	 */
	public DataThresholdCoadder()
	{
		this(DEFAULT_NAME);
	}
	
	/**
	 * @param name
	 */
	public DataThresholdCoadder(String name)
	{
		super(name);
		fStrategy = new IntraBasisSetDataThresholdStrategy();
		setStrategy(fStrategy);	
	}

	/**
	 * Return the current set of thresholds for this coadder.
	 * @return array of array of {@link gov.nasa.gsfc.irc.library.processors.strategies.coadder.IntraBasisSetDataThresholdStrategy.Threshold}
	 */
	public ArrayList getThresholds()
	{
		return fStrategy.getThresholds();
	}


	/**
	 * Set the thresholds that should be used in this coadder.
	 * @param thresholds array of {@link gov.nasa.gsfc.irc.library.processors.strategies.coadder.IntraBasisSetDataThresholdStrategy.Threshold}
	 */
	public void setThresholds(ArrayList thresholds)
	{
		fStrategy.setThresholds(thresholds);
	}

}
//--- Development History  ---------------------------------------------------
//
//$Log: DataThresholdCoadder.java,v $
//Revision 1.4  2006/05/10 17:29:11  smaher_cvs
//Changed coadd index array to be a primitive array; fixed some bugs; have a working chop coadder with this checkin.
//
//Revision 1.3  2006/01/24 15:55:14  chostetter_cvs
//Changed default ComponentManager behavior, default is now none
//
//Revision 1.2  2006/01/23 17:59:54  chostetter_cvs
//Massive Namespace-related changes
//
//Revision 1.1  2005/10/07 21:08:30  smaher_cvs
//Finished testing IntraBasisSetDataThresholdStrategy
//
//Revision 1.1  2005/09/23 20:56:45  smaher_cvs
//Implemented and tested new Coadder with strategy pattern
//

