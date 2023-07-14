//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/algorithms/BasisSetProcessor.java,v 1.10 2006/03/14 14:57:15 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
// $Log: BasisSetProcessor.java,v $
// Revision 1.10  2006/03/14 14:57:15  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.9  2006/03/07 23:32:42  chostetter_cvs
// NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
// Revision 1.8  2006/01/24 16:19:16  chostetter_cvs
// Changed default ComponentManager behavior, default is now none
//
// Revision 1.7  2006/01/23 17:59:53  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.6  2005/04/04 15:40:58  chostetter_cvs
// Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
// Revision 1.5  2005/02/01 20:53:54  chostetter_cvs
// Revised releasable BasisSet design, release policy
//
// Revision 1.4  2005/01/27 21:38:02  chostetter_cvs
// Implemented new exception state and default exception behavior for Objects having ActivityState
//
// Revision 1.3  2004/11/23 04:18:28  tames
// Saved log level of basis sets to finest so the console does not get
// overwhelmed by output.
//
// Revision 1.2  2004/07/22 16:28:03  chostetter_cvs
// Various tweaks
//
// Revision 1.1  2004/07/22 15:10:50  chostetter_cvs
// Added BasisSetProcessor, handles releasing and synchronization
//
//
//--- Warning ----------------------------------------------------------------
//This software is property of the National Aeronautics and Space
//Administration. Unauthorized use or duplication of this software is
//strictly prohibited. Authorized users are subject to the following
//restrictions:
//*	Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
//*	The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
//*	Altered versions of this software must be plainly marked as such.
//*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.algorithms;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataSet;


/**
 *	An BasisSetProcessor is a Processor that processes each BasisSet of each input 
 *  DataSet in order and independently, and releases each BasisSet after it has been 
 *  processed.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version $Date: 2006/03/14 14:57:15 $
 *	@author Carl F. Hostetter
 */

public abstract class BasisSetProcessor extends DefaultProcessor
{
	private static final String CLASS_NAME = BasisSetProcessor.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	
	/**
	 *  Constructs a new BasisSetProcessor having the given base name. Note that 
	 *  the new BasisSetProcessor will need to have its ComponentManager (typically 
	 *  an Algorithm) set (if any is desired).
	 * 
	 *  @param name The base name of the new BasisSetProcessor
	 **/

	public BasisSetProcessor(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new BasisSetProcessor configured according to the given 
	 *  ComponentDescriptor. Note that the new BasisSetProcessor will need to have 
	 *  its ComponentManager (typically an Algorithm) set (if any is desired).
	 *
	 *  @param descriptor The ComponentDescriptor of the new BasisSetProcessor
	 */
	
	public BasisSetProcessor(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Causes this BasisSetProcessor to process the given BasisSet.
	 * 
	 * <p>NOTE!: The integrity of the data in the given BasisSet is <em>not</em> 
	 * guaranteed after this method returns. If you need to carry data over from 
	 * one call to the next, you <em>must</em> copy the data (by calling the 
	 * <code>copy()</code> method on the BasisSet.
	 * 
	 * @param BasisSet A BasisSet
	 */
	
	protected abstract void processBasisSet(BasisSet basisSet);
	
	
	/**
	 * Causes this BasisSetProcessor to process the given DataSet.
	 * 
	 * <p>NOTE!: The integrity of the data in the given DataSet is <em>not</em> 
	 * guaranteed after this method returns. If you need to carry data over from 
	 * one call to the next, you <em>must</em> copy the data (by calling the 
	 * <code>copy()</code> method on the BasisSet(s) to be retained.
	 * 
	 * @param dataSet A DataSet
	 */
	
	public void processDataSet(DataSet dataSet)
	{
		try
		{
			Iterator basisSets = dataSet.getBasisSets().iterator();
			
			while (basisSets.hasNext())
			{
				BasisSet basisSet = (BasisSet) basisSets.next();
				
				if (sLogger.isLoggable(Level.FINEST))
				{
					String message = getFullyQualifiedName() + 
						" processing BasisSet:\n" + basisSet;
					
					sLogger.logp(Level.FINEST, CLASS_NAME, 
						"processDataSet", message);
				}
				
				synchronized (getConfigurationChangeLock())
				{
					processBasisSet(basisSet);
				}
			}
		}
		catch (Exception ex)
		{
			declareException(ex);
		}		
	}
}
