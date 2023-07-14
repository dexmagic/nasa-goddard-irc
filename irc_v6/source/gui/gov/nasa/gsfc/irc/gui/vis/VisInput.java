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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jscience.physics.units.Unit;

import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.BasisRequestAmount;
import gov.nasa.gsfc.irc.data.DefaultBasisBundleId;
import gov.nasa.gsfc.irc.data.HistoryBasisRequest;

/**
 * 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/03/14 14:57:16 $
 * @author 	Troy Ames
 */
public class VisInput extends DefaultInput
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(VisInput.class.getName());
	
	public static final String DEFAULT_NAME = "Vis Input";

	// VisInput related fields
    private String fBasisRequestString = null;
    private Set fDataBufferNames = null;
	private BasisRequestAmount fRequestAmount;
	private Unit fRequestUnit = Unit.ONE;
	private BasisRequest fBasisRequest;
	private double fUpdateInterval;


	/**
	 *	Constructs a new VisInput having a default name and managed by the default 
	 *  ComponentManager.
	 *
	 */
	
	public VisInput()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new VisInput having the given base name and managed by the 
	 *  default ComponentManager.
	 * 
	 *  @param name The base name of the new VisInput
	 **/

	public VisInput(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new VisInput configured according to the given 
	 *  ComponentDescriptor and managed by the default ComponentManager.
	 *
	 *  @param descriptor The ComponentDescriptor of the new VisInput
	 */
	
	public VisInput(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 * Causes this VisInput to start if it has not
	 * been previously killed. This
	 * method will call <code>createBasisRequest</code> and then 
	 * register the request with an input.
	 * 
	 * @see #createBasisRequest()
	 */
	public void start()
	{
		if (!isStarted() && !isKilled())
		{
			if (fBasisRequest != null)
			{
				removeBasisRequests(fBasisRequest.getBasisBundleId());
			}
			
			fBasisRequest = createBasisRequest();
			addBasisRequest(fBasisRequest);
			super.start();
		}
	}

	// TODO a SwixML converter needs to be created so a setBasisRequest(BasisRequest)
	// method can be called directly from SwixML.
	/**
	 * Adds the given BasisRequest to the Set of BasisRequests managed by 
	 * this chart. Note this takes a String representation of a basis request
	 * that has the given fully-qualified name (i.e., of the form
	 * "<BasisBundle name> of <output name> of <algorithm name>".
	 *   
	 * @param basisRequestString String representation of a BasisRequest.
	 */
	public void setBasisRequest(String basisRequestString)
	{
		fBasisRequestString = basisRequestString;
	}

	/**
	 * Sets the Set of DataBuffer names specifying the subset of the DataBuffers of 
	 * the BasisBundle on which the BasisRequest of this Chart is made to the given 
	 * vertical-bar-delimited ("|") String of names. If the given String is null or 
	 * empty, the BasisRequest of this Chart will be configured to select all 
	 * DataBuffers.
	 * 
	 * @param dataBufferNames The vertical-bar-delimited ("|") String of names 
	 * 		specifying the subset of the DataBuffers of the BasisBundle on which 
	 * 		the BasisRequest of this Chart is made
	 */
	public void setDataBufferNames(String dataBufferNames)
	{
		if (dataBufferNames != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(dataBufferNames, "|");
			
			int numNames = tokenizer.countTokens();
			
			fDataBufferNames = new HashSet(numNames);
			
			for (int i = 0; i < numNames; i++)
			{
				String bufferName = tokenizer.nextToken();
				
				fDataBufferNames.add(bufferName);
			}
		}
		else
		{
			fDataBufferNames = null;
		}
	}
	
	/**
	 * Returns the update interval.
	 * 
	 * @return Returns the update interval.
	 */
	public double getUpdateInterval()
	{
		return fUpdateInterval;
	}

	/**
	 * Sets the update interval
	 * @param interval The update Interval to set.
	 */
	public void setUpdateInterval(double interval)
	{
		fUpdateInterval = interval;
	}

	/**
	 * Sets the amount of the data request to the given amount. This specifies
	 * how much data is delivered to the chart at a time. Size is in terms 
	 * of the Unit specified by the <code>setRequestUnit</code> method.
	 *  
	 * @param amount The new amount
	 */	 
	 public void setRequestAmount(double amount)
	 {
	 	if (fRequestAmount == null)
	 	{
	 		fRequestAmount = new BasisRequestAmount();
	 	}
	 	
	 	fRequestAmount.setAmount(amount);
	 }
	 
	/**
	 * Returns the amount of the data request.
	 *  
	 * @return The amount of the data request
	 */	 
	 public double getRequestAmount()
	 {
	 	double result = 0;
	 	
	 	if (fRequestAmount != null)
	 	{
	 		result = fRequestAmount.getAmount();
	 	}
	 	
	 	return (result);
	 } 

	 /**
	  * Sets the Unit of the data request amount to the Unit corresponding to the 
	  * given Unit name.
	  *  
	  * @param unitName The name of the new Unit of the data request amount
	  * @throws IllegalArgumentException if the given Unit name is not a recognized 
	  * 		Unit name
	  */	 
	 public void setRequestUnit(String unitName)
	 {
	 	if (fRequestAmount == null)
	 	{
	 		fRequestAmount = new BasisRequestAmount();
	 	}
	 	
	 	fRequestAmount.setUnit(unitName);
	 }
	 
	 
	/**
	 * Create a basis request for this chart.
	 * 
	 * @return a basis request
	 */
	protected BasisRequest createBasisRequest()
	 {
		BasisRequest basisRequest = null; 
		
		BasisBundleId basisBundleId = 
			Irc.getDataSpace().getBasisBundleId(fBasisRequestString);
		
		if (basisBundleId == null)
		{
			System.out.println("BasisBundleId for " + fBasisRequestString + " is null");
			basisBundleId = new DefaultBasisBundleId(fBasisRequestString);
		}
		
//		System.out.println(Irc.getDataSpace().toString());
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("fBasisRequestString:" + fBasisRequestString);
		}
		
		if (fUpdateInterval > 0)
		{
			basisRequest = new HistoryBasisRequest(basisBundleId); 
			((HistoryBasisRequest) basisRequest).setUpdateInterval(fUpdateInterval);
		}
		else
		{
			basisRequest = new BasisRequest(basisBundleId); 
		}
		
		if (fDataBufferNames != null)
		{
			basisRequest.setDataBufferNames(fDataBufferNames);
		}
		
		basisRequest.setRequestAmount(fRequestAmount);
		// TODO should the basis request downsampling override the inputs?
		basisRequest.setDownsamplingRate(getDownsamplingRate());
		
		return basisRequest;
	 }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: VisInput.java,v $
//  Revision 1.11  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.10  2006/03/07 23:32:42  chostetter_cvs
//  NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
//  Revision 1.9  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.8  2005/12/06 22:48:43  tames_cvs
//  Added some temp debug code for testing the late binding of a BasisRequest.
//
//  Revision 1.7  2005/09/22 18:51:31  tames
//  Added suport of History requests.
//
//  Revision 1.6  2005/03/16 18:36:50  chostetter_cvs
//  Refactored BasisRequestAmount
//
//  Revision 1.5  2005/03/15 00:36:02  chostetter_cvs
//  Implemented covertible Units compliments of jscience.org packages
//
//  Revision 1.4  2005/03/12 18:37:02  tames
//  Fixed bug where a downsample specified in the xml or while the VisInput
//  was stopped was being overridden by the basis request.
//
//  Revision 1.3  2005/03/12 12:43:34  smaher_cvs
//  Reduced System.out use that printed out entire DataSpace.
//
//  Revision 1.2  2005/02/25 22:31:35  tames_cvs
//  Changed the start method to check current state and do nothing
//  if already started.
//
//  Revision 1.1  2004/12/14 21:47:11  tames
//  Refactoring of abstract classes and interfaces.
//
//