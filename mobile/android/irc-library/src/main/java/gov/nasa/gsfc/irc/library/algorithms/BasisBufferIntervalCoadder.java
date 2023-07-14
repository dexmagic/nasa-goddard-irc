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

import gov.nasa.gsfc.irc.library.processors.strategies.coadder.BasisBufferIntervalStrategy;

import java.beans.PropertyVetoException;


/**
 * Performs coadding over a certain basis buffer interval (usually time)
 * <p>
 * This code was developed for NASA, Goddard Space Flight Center, Code 588 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/06/30 15:08:36 $
 * @author Steve Maher
 */
public class BasisBufferIntervalCoadder extends Coadder
{
	public static final String DEFAULT_NAME = "Basis Buffer Interval Coadder";
	
	private BasisBufferIntervalStrategy fStrategy;
	

	public BasisBufferIntervalCoadder() 
	{
		this(DEFAULT_NAME);
	}
	
	public BasisBufferIntervalCoadder(String name)
	{
		super(name);
		fStrategy = new BasisBufferIntervalStrategy();
		setStrategy(fStrategy);	
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.library.processors.strategies.coadder.BasisBufferIntervalStrategy#getIntervalInBasisBuffer()
	 */
	public double getIntervalInBasisBuffer()
	{
		return fStrategy.getIntervalInBasisBuffer();
	}

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.library.processors.strategies.coadder.BasisBufferIntervalStrategy#setIntervalInBasisBuffer(double)
	 */
	public void setIntervalInBasisBuffer(double interval) throws PropertyVetoException
	{
		fStrategy.setIntervalInBasisBuffer(interval);
	}
	

}
//--- Development History  ---------------------------------------------------
//
//$Log: BasisBufferIntervalCoadder.java,v $
//Revision 1.2  2006/06/30 15:08:36  smaher_cvs
//Organized imports
//
//Revision 1.1  2006/06/26 20:09:42  smaher_cvs
//Initial
//


