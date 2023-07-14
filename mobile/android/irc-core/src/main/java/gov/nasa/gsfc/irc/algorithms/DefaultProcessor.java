//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/algorithms/DefaultProcessor.java,v 1.33 2006/03/14 14:57:15 chostetter_cvs Exp $
//
//  This code was developed by NASA, Goddard Space Flight Center, Code 580
//  for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//  Development history is located at the end of the file.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.components.AbstractManagedComponent;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataSet;
import gov.nasa.gsfc.irc.data.description.BasisBundleDescriptor;
import gov.nasa.gsfc.irc.data.events.BasisBundleEvent;
import gov.nasa.gsfc.irc.data.events.DataSetEvent;


/**
 *	An DefaultProcessor is a default implementation of a Processor.
 *
 *	<P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *	for the Instrument Remote Control (IRC) project.
 *
 *	@version $Date: 2006/03/14 14:57:15 $
 *	@author Carl F. Hostetter
 */

public abstract class DefaultProcessor extends AbstractManagedComponent
	implements Processor
{
	private static final String CLASS_NAME = DefaultProcessor.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	/**
	 *  The Map of Outputs associated with this Processor.
	 */
	private Map fOutputsByName = new HashMap();
	
	private Map fMaxNumInputSamplesByInputBasisBundleId = new HashMap();
	private Map fOutputsByInputBasisBundleId = new HashMap();
	private Map fOutputBasisBundleIdsByInputBasisBundleId = new HashMap();
	
	private Output fFirstOutput;
	
	private static final int DEFAULT_NUM_BASIS_SETS_PER_BASIS_BUNDLE = 6;
	

	/**
	 *  Constructs a new Processor having the given base name. Note that the new 
	 *  Processor will need to have its ComponentManager (typically an Algorithm) 
	 *  set (if any is desired).
	 * 
	 *  @param name The base name of the new Processor
	 **/

	public DefaultProcessor(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new Processor configured according to the given 
	 *  ComponentDescriptor. Note that the new Processor will need to have its 
	 *  ComponentManager (typically an Algorithm) set (if any is desired).
	 *
	 *  @param descriptor The ComponentDescriptor of the new Processor
	 */
	
	public DefaultProcessor(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 *  Associates the given Output with the input BasisBundle indicated by the 
	 *  given input BasisBundleId.
	 * 
	 *  <p>Here, the given Output and BasisBundleId are uniquely associated with 
	 *  one another (i.e., multiple associations are not permitted).
	 * 
	 *  @param inputBasisBundleId The BasisBundleId of a BasisBundle serving as a 
	 * 		source of input BasisSets to this Processor
	 *  @param output The Output to associate with the given input BasisBundleId
	**/

	protected void setAssociatedOutput
		(BasisBundleId inputBasisBundleId, Output output)
	{
		fOutputsByInputBasisBundleId.put(inputBasisBundleId, output);
	}
	
	
	/**
	 *  Returns the (single) Output that has been associated with the input 
	 *  BasisBundle indicated by the given BasisBundleId.
	 * 
	 *  <p>Here, if no such Output association has been set for the given input 
	 *  BasisBundleId, we simply return the current first Output of this Processor. 
	 *  Otherwise, the Output association previously set for the given input 
	 *  BasisBundleId is returned.
	 * 
	 *  <p>If this default behavior is not appropriate for a sublcassing Processor, 
	 *  it must override this method and provide its own association.
	 * 
	 *  @param inputBasisBundleId The BasisBundleId of a BasisBundle serving as a 
	 * 		source of input BasisSets to this Processor
	 *  @return The (single) Output that is currently associated with the input 
	 * 		BasisBundle indicated by the given BasisBundleId
	**/

	protected Output getAssociatedOutput(BasisBundleId inputBasisBundleId)
	{
		Output result = null;
		
		if (fOutputsByInputBasisBundleId != null)
		{
			result = (Output) fOutputsByInputBasisBundleId.get(inputBasisBundleId);
		}
		
		if (result == null)
		{
			result = fFirstOutput;
		}
		
		return (result);
	}
	
	
	/**
	 *  Associates the given output BasisBundleId with the given input 
	 *  BasisBundleId.
	 * 
	 *  @param inputBasisBundleId The BasisBundleId of some input BasisSet
	 *  @return The output BasisBundleId that is associated by this Processor 
	 *  		with the given input BasisBundleId
	 */

	protected void setAssociatedOutputBasisBundleId
		(BasisBundleId inputBasisBundleId, BasisBundleId outputBasisBundleId)
	{
		fOutputBasisBundleIdsByInputBasisBundleId.put
			(inputBasisBundleId, outputBasisBundleId);
	}


	/**
	 *  Returns the output BasisBundleId that is associated by this Processor 
	 *  with the given input BasisBundleId.
	 * 
	 *  @param inputBasisBundleId The BasisBundleId of some input BasisSet
	 *  @return The output BasisBundleId that is associated by this Processor 
	 *  		with the given input BasisBundleId
	 */

	protected BasisBundleId getAssociatedOutputBasisBundleId
		(BasisBundleId inputBasisBundleId)
	{
		BasisBundleId result = null;
		
		if (fOutputBasisBundleIdsByInputBasisBundleId != null)
		{
			result = (BasisBundleId) 
				fOutputBasisBundleIdsByInputBasisBundleId.get(inputBasisBundleId);
		}
		
		return (result);
	}


	/**
	 *  Returns the name of the (single) output DataBuffer of the output BasisBundle 
	 *  indicated by the given output BasisBundleId that has been associated by this 
	 *  Processor with the given input DataBuffer name of the input BasisBundle 
	 *  indicated by the given input BasisBundleId.
	 * 
	 *  <p>Here, we simply return the given input DataBuffer name.
	 * 
	 *  <p>If this default behavior is not appropriate for a sublcassing Processor, 
	 *  it must override this method and provide its own association.
	 * 
	 *  @param inputDataBufferName The name of a DataBuffer in the BasisBundle 
	 *  		indicated by the given input BasisBundleId
	 *  @param inputBasisBundleId The BasisBundleId of some input BasisBundle
	 *  @return The name of the (single) output DataBuffer of the output BasisBundle 
	 *  		indicated by the given output BasisBundleId that has been associated by 
	 *  		this Processor with the given input DataBuffer name of the input 
	 *  		BasisBundle indicated by the given input BasisBundleId
	**/

	protected String getOutputDataBufferName(String inputDataBufferName, 
		BasisBundleId inputBasisBundleId)
	{
		String result = inputDataBufferName;
		
//		if (fOutputsByInputBasisBundleId != null)
//		{
//			Output output = (Output) 
//				fOutputsByInputBasisBundleId.get(inputBasisBundleId);
//			
//			if ((output != null) && 
//				(fOutputBasisBundleIdsByInputBasisBundleId != null))
//			{
//				BasisBundleId outputBasisBundleId = (BasisBundleId) 
//					fOutputBasisBundleIdsByInputBasisBundleId.get(inputBasisBundleId);
//				
//				if (outputBasisBundleId != null)
//				{
//					BasisBundle outputBasisBundle = 
//						output.getBasisBundle(outputBasisBundleId);
//					
//					if (outputBasisBundle != null)
//					{
//						if (outputBasisBundle.g)
//					}
//				}
//			}
//		}
		
		return (result);
	}
	
	
	/**
	 *  Causes this Processor to build a new output BasisBundleDescriptor describing 
	 *  the structure of the output BasisSets that it will create to hold the 
	 *  results of processing input BasisSets of the structure described by the 
	 *  given input BasisBundleDescriptor.
	 * 
	 *  <p>Here, simply returns the given input BasisBundleDescriptor. 
	 * 
	 *  <p>If this default behavior is not appropriate for a sublcassing Processor, 
	 *  it must override this method and provide its own input/output mapping.
	 * 
	 *  @param buildOutputDescriptor A BasisBundleDescriptor describing the 
	 * 		structure of a (potential) input BasisSet to this Processor
	 *  @return A BasisBundleDescriptor describing the structure of the 
	 * 		corresponding ouput BasisSet
	**/

	protected BasisBundleDescriptor buildOutputDescriptor
		(BasisBundleDescriptor inputDescriptor)
	{
		return (inputDescriptor);
	}
	
	
	/**
	 *  Returns the predicted size of the output BasisSets formed by this Processor 
	 * for the given number of input samples.
	 * 
	 *  <p>Here, simply returns the given number.
	 * 
	 *  <p>If this default behavior is not appropriate for a sublcassing Processor, 
	 *  it must override this method and provide its own output size determination.
	 * 
	 *  @return The predicted size of the output BasisSets formed by this 
	 *  		Processor for the given number of input samples
	**/

	protected int getOutputSize(int numInputSamples)
	{
		return (numInputSamples);
	}
	
	
	/**
	 * Causes this Processor to respond as needed to a newly-associated input 
	 * BasisBundle, or to the change in the structure of an already-assocoated 
	 * input BasisBundle.
	 * 
	 * <p>The default behavior implemented here makes the assumptions that 1) 
	 * precisely one output BasisBundle is to be, or has previously been, associated 
	 * with the input BasisBundle indicated by the given BasisBundleId; and 2) the 
	 * BasisBundleId of that uniquely associated output BasisBundle, if previously 
	 * associated, is discoverable by a call of 
	 * <code>getAssociatedOutputBasisBundleId(BasisBundleId)</code> with the 
	 * given input BasisBundleId.
	 * 
	 * <p>If either of these assumptions fails for a given subclassing Processor, 
	 * that Processor must override this method and provide its own input/output 
	 * mapping.
	 * 
	 * @param inputBasisBundleId The BasisBundleId of either a) a newly-associated 
	 * 		input BasisBundle; or b) an already-associated input BasisBundle that 
	 * 		has changed its structure
	 * @param inputDescriptor The (possibly new) BasisBundleDescriptor of the 
	 * 		indicated input BasisBundle
	 */
	
	protected void handleNewInputBasisBundleStructure
		(BasisBundleId inputBasisBundleId, BasisBundleDescriptor inputDescriptor)
	{
		// First, build a BasisBundleDescriptor describing the output data structure 
		// corresponding to the structure of the given input BasisBundle.
		
		BasisBundleDescriptor outputDescriptor = 
			buildOutputDescriptor(inputDescriptor);
		
		if (outputDescriptor != null)
		{
			// Next, see whether we have already associated some BasisBundle as the 
			// output BasisBundle corresponding to the indicated input BasisBundle.
			
			Output output = getAssociatedOutput(inputBasisBundleId);
			
			BasisBundleId outputBasisBundleId = 
				getAssociatedOutputBasisBundleId(inputBasisBundleId);
			
			if (outputBasisBundleId == null)
			{
				// If we haven't then we need to associate an Output and a new 
				// output BasisBundle with the indicated input BasisBundle.
				
				if (output != null)
				{
					// Ask the determined Output to create and manage a new output 
					// BasisBundle of the determined structure.
					
					outputBasisBundleId = output.addBasisBundle(outputDescriptor);
					
					// Associate the new output BasisBundleId with the given input 
					// BasisBundleId.
					
					setAssociatedOutputBasisBundleId
						(inputBasisBundleId, outputBasisBundleId);
				}
			}
			else
			{
				// Otherwise, if we have already associated an output BasisBundle 
				// with the indicated input BasisBundle, then we need only to 
				// update its structure according to the new input BasisBundle 
				// structure.
				
				output.restructureBasisBundle
					(outputBasisBundleId, outputDescriptor);
			}
		}
	}
	
	
	/**
	 * Causes this Processor to respond as needed to the closing of an associated 
	 * input BasisBundle.
	 * 
	 * <p>The default behavior implemented here makes the assumptions that 1) 
	 * precisely one output BasisBundle has previously been associated with the 
	 * input BasisBundle indicated by the given BasisBundleId; and 2) the 
	 * BasisBundleId of that uniquely associated output BasisBundle is discoverable 
	 * by a call of <code>getAssociatedOutputBasisBundleId(BasisBundleId)</code> 
	 * with the given input BasisBundleId.
	 * 
	 * <p>If either of these assumptions fails for a given subclassing Processor, 
	 * that Processor must override this method and provide its own input/output 
	 * mapping.
	 * 
	 * @param inputBasisBundleId The BasisBundleId of an already-associated input 
	 * 		BasisBundle that has closed (i.e., will no longer be producing data)
	 */
	
	protected void handleInputBasisBundleClosing(BasisBundleId inputBasisBundleId)
	{
		fOutputsByInputBasisBundleId.remove(inputBasisBundleId);
		
		fOutputBasisBundleIdsByInputBasisBundleId.remove(inputBasisBundleId);
	}
	
	
	/**
	 * Causes this Processor to receive the given BasisBundleEvent.
	 * 
	 * <p>The default behavior implemented here makes the assumptions that 1) 
	 * precisely one output BasisBundle has previously been associated with the 
	 * input BasisBundle that produced the given BasisBundleEvent; and 2) the 
	 * BasisBundleId of that uniquely associated output BasisBundle is discoverable 
	 * by a call of <code>getAssociatedOutputBasisBundleId(BasisBundleId)</code> 
	 * with the BasisBundleId of the given BasisBundleEvent.
	 * 
	 * <p>If either of these assumptions fails for a given subclassing Processor, 
	 * that Processor must override this method and provide its own input/output 
	 * mapping.
	 * 
	 * @param event A DataListener
	 */
	
	public synchronized void receiveBasisBundleEvent(BasisBundleEvent event)
	{
		if (event != null)
		{
			try
			{
				if (sLogger.isLoggable(Level.FINEST))
				{
					String message = getFullyQualifiedName() + 
						" received BasisBundleEvent:\n" + event;
					
					sLogger.logp(Level.FINEST, CLASS_NAME, 
						"receiveDataEvent", message);
				}
				
				if (event.hasNewStructure())
				{
					// If the given event indicates that the input BasisBundle 
					// that generated it has changed its structure, then we 
					// need to create a new, corresponding output BasisBundle 
					// of a structure appropriate the new input structure.
					
					BasisBundleId inputBundleId = event.getBasisBundleId();
					BasisBundleDescriptor inputDescriptor = event.getDescriptor();
					
					handleNewInputBasisBundleStructure
						(inputBundleId, inputDescriptor);
				}
				else if (event.isClosed())
				{
					// If the given BasisBundleEvent indicated that the input 
					// BasisBundle that generated it has closed (i.e., will no 
					// longer produce any data), then we can remove all 
					// references to it and to its correspinding output 
					// BasisBundle from our mappings.
					
					BasisBundleId inputBasisBundleId = event.getBasisBundleId();
					
					handleInputBasisBundleClosing(inputBasisBundleId);
				}
			}
			catch (Exception ex)
			{
				declareException(ex);
			}
		}
	}
	

	/**
	 *  Causes this Processor to process the given Dataset.
	 * 
	 * <p>NOTE!: The integrity of the data in the given DataSet is <em>not</em> 
	 * guaranteed after this method returns. If you need to carry data over from 
	 * one call to the next, you <em>must</em> copy the data (by calling the 
	 * <code>copy()</code> method on the BasisSet(s) to be retained.
	 * 
	 *  @param dataSet A DataSet
	 */

	protected abstract void processDataSet(DataSet dataSet);
	
	
	/**
	 * Ensures that the output BasisBundle associated with the given input BasisSet 
	 * has the appropriate capacity to handle this Processor's output for the given 
	 * input BasisSet.
	 * 
	 * <p>The default behavior implemented here makes the assumptions that 1) 
	 * precisely one output BasisBundle has previously been associated with the 
	 * input BasisBundle that produced the given BasisSet; and 2) the BasisBundleId 
	 * of that uniquely associated output BasisBundle is discoverable by a call of 
	 * <code>getAssociatedOutputBasisBundleId(BasisBundleId)</code> with the 
	 * BasisBundleId of the given BasisSet; and 3) that that output BasisBundle 
	 * needs capacity to accomodate at least, but not necessarily more than, 
	 * N/2 output BasisSets of a size calculated for the given BasisSet by a call of 
	 * <code>getOutputSize(int)</code> with the size of the given BasisSet, where 
	 * N has the value set for the DEFAULT_NUM_BASIS_SETS_PER_BASIS_BUNDLE constant.
	 * 
	 * <p>If any of these assumptions fails for a given subclassing Processor, that 
	 * Processor must override this method and provide its correct output capacity 
	 * assurance behavior.
	 * 
	 * @param basisSet A BasisSet
	 */
	
	protected void ensureCorrectOutputCapacity(BasisSet basisSet)
	{
		// Determine the size of the given input BasisSet.
		
		int numInputSamples = basisSet.getSize();
		
		// Determine its source BasisBundle.
		
		BasisBundleId inputBasisBundleId = basisSet.getBasisBundleId();
		
		// And determine its corresponding output BasisBundle.
		
		BasisBundleId outputBasisBundleId = 
			getAssociatedOutputBasisBundleId(inputBasisBundleId);
		
		Output output = (Output) getAssociatedOutput(inputBasisBundleId);
		
		// Determine the previous maximum number of input samples encountered in 
		// BasisSets from the source BasisBundle (if any).

		Integer maxNumInputSamples = (Integer) 
			fMaxNumInputSamplesByInputBasisBundleId.get(inputBasisBundleId);
		
		if (maxNumInputSamples == null)
		{
			// If this is the first such input BasisSet from the source 
			// BasisBundle, record its size as the new input max for that 
			// input BasisBundle.
			
			fMaxNumInputSamplesByInputBasisBundleId.put
				(inputBasisBundleId, new Integer(numInputSamples));
			
			// Calculate the number of output samples that will be needed 
			// for the output data corresponding to the number of input 
			// samples in this input BasisSet.
			
			int correspondingOutputSize = getOutputSize(numInputSamples);
			
			// Resize the associated output BasisBundle to accommodate 
			// DEFAULT_NUM_BASIS_SETS_PER_BASIS_BUNDLE BasisSets of this 
			// calculated number of output samples.
			
			if (output != null)
			{
				output.resizeBasisBundle(outputBasisBundleId, 
					correspondingOutputSize * 
					DEFAULT_NUM_BASIS_SETS_PER_BASIS_BUNDLE);
			}
		}
		else if (maxNumInputSamples.intValue() < numInputSamples)
		{
			// Otherwise, if the size of this input BasisSet is greater 
			// than the previous maximum size recorded for input from the 
			// source BasisBundle, record its size as the new such max.
			
			fMaxNumInputSamplesByInputBasisBundleId.put
				(inputBasisBundleId, new Integer(numInputSamples));
			
			// Calculate the number of output samples that will be needed 
			// for the output data corresponding to the number of input 
			// samples in this input BasisSet.
			
			int correspondingOutputSize = getOutputSize(numInputSamples);
			
			if (output != null)
			{
				BasisBundle basisBundle = 
					output.getBasisBundle(outputBasisBundleId);
				
				// Determine the current size of the output BasisBundle 
				// corresponding to the source BasisBundle.
				
				int currentBasisBundleSize = basisBundle.getSize();
				
				// If its current size is insufficient to accommodate at least 
				// half of the DEFAULT_NUM_BASIS_SETS_PER_BASIS_BUNDLE 
				// BasisSets of the calculated corresponding output size for 
				// the input BasisSet, then resize it.
		
				if (currentBasisBundleSize < (correspondingOutputSize * 
					(DEFAULT_NUM_BASIS_SETS_PER_BASIS_BUNDLE / 2)))
				{
					output.resizeBasisBundle(outputBasisBundleId, 
						correspondingOutputSize * 
						DEFAULT_NUM_BASIS_SETS_PER_BASIS_BUNDLE);
				}
			}
		}
	}
	
	
	/**
	 * Ensures that the output BasisBundles corresponding to each of the BasisSets 
	 * in the given input DataSet have appropriate capacities to handle the 
	 * processed output from each such corresponding input BasisSets.
	 * 
	 * <p>Here, we simply iterate over each BasisSet in the given DataSet and call 
	 * <code>ensureCorrectOutputCapacity(BasisSet)</code> with each.
	 * 
	 * <p>If some other behavior is required in a subclassing Processor, the 
	 * subclass must override this method and provide its correct output capacity 
	 * assurance behavior.
	 * 
	 * @param dataSet A DataSet
	 */
	
	protected void ensureCorrectOutputCapacities(DataSet dataSet)
	{
		Iterator basisSets = dataSet.getBasisSets().iterator();
		
		while (basisSets.hasNext())
		{
			BasisSet basisSet = (BasisSet) basisSets.next();
			
			ensureCorrectOutputCapacity(basisSet);
		}
	}
	
	
	/**
	 * Causes this Processor to receive the given DataSetEvent. 
	 * 
	 * <p>Here, we simply extract the input DataSet from the given DataSetEvent, 
	 * calls <code>ensureCorrectOutputCapacities(DataSet)</code> with it, and then 
	 * calls <code>processDataSet(DataSet)</code> with the extracted DataSet. When 
	 * <code>processDataSet(DataSet)</code> returns, the given DataSet is released.
	 * 
	 * <p>NOTE!: The integrity of the data in the given DataSet is <em>not</em> 
	 * guaranteed after this method returns. If you need to carry data over from 
	 * one call to the next, you <em>must</em> copy the data (by calling the 
	 * <code>copy()</code> method on the BasisSet(s) to be retained.
	 * 
	 * @param event A DataSetEvent
	 */
	
	public synchronized void receiveDataSetEvent(DataSetEvent event)
	{
		DataSet dataSet = event.getDataSet();
		
		if ((isStarted() && dataSet != null))
		{
			try
			{
				if (sLogger.isLoggable(Level.FINEST))
				{
					String message = getFullyQualifiedName() + 
						" received DataSet:\n" + dataSet;
					
					sLogger.logp(Level.FINEST, CLASS_NAME, 
						"receiveDataEvent", message);
				}
				
				ensureCorrectOutputCapacities(dataSet);
				
				processDataSet(dataSet);
			}
			catch (Exception ex)
			{
				declareException(ex);
			}
		}
	}

//----------------------------------------------------------------------
//	Output-related methods
//----------------------------------------------------------------------
	
	/**
	 *  Adds the given Output to the Set of Outputs associated with this 
	 *  Processor.
	 *
	 *  @param output The Output to be added to this Processor
	 */

	public void addOutput(Output output)
	{
		if (output != null)
		{
			synchronized (getConfigurationChangeLock())
			{
				fOutputsByName.put(output.getName(), output);
				
				if (fOutputsByName.size() == 1)
				{
					fFirstOutput = output;
				}
			}
		}
	}


	/**
	 *  Returns the default Output to be used by this Processor. Note that this 
	 *  may return null if this Processor does not use any Output, or if no 
	 *  Output has yet been associated with it. 
	 * 
	 *  <p>This method should be used only as a convenience for Processors that 
	 * 	utilize exactly one Output
	 *
	 *  @return The default Output to be used by this Processor
	 */

	protected synchronized Output getOutput()
	{
		return (fFirstOutput);
	}


	/**
	 *  Removes the Output having the given name from the Set of Outputs 
	 *  associated with this Processor.
	 * 
	 *  @param name The name of the Output to be removed from this Processor
	 */

	public void removeOutput(String name)
	{
		synchronized (getConfigurationChangeLock())
		{
			Output removedOutput = (Output) fOutputsByName.remove(name);
			
			if (fFirstOutput == removedOutput)
			{
				if (fOutputsByName.size() > 0)
				{
					fFirstOutput = (Output) 
						fOutputsByName.values().iterator().next();
				}
				else
				{
					fFirstOutput = null;
				}
			}
		}
	}


	/**
	 *  Returns an Iterator over the Set of Outputs associated with this 
	 *  Processor.
	 *
	 * <p>This Iterator should only be obtained and used within a block 
	 * that is <code>synchronized</code> on the <code>ConfigurationChangeLock</code> 
	 * of this Processor. Otherwise, it would be bad.
	 *
	 *  @return An Iterator over the Set of Outputs associated with this 
	 *  		Processor
	 */
	
	protected Iterator getOutputs()
	{
		return (fOutputsByName.values().iterator());
	}
	
	
	/**
	 *  Returns a Map of the Output associated with this Processor keyed on 
	 *  their names.
	 *
	 *  @return A Map of the Output associated with this Processor keyed on 
	 *  		their names
	 */
	
	protected Map getOutputsByName()
	{
		return (Collections.unmodifiableMap(fOutputsByName));
	}
	
	
	/**
	 *	Returns the Output of this Processor that has the given name (if any).
	 *
	 *	@param name The name of an Output
	 *	@return	The Output of this Processor that has the given name (if any)
	 */
	
	protected Output getOutput(String name)
	{
		return ((Output) fOutputsByName.get(name));
	}
	

	/**
	 *  Causes this Processor to immediately cease operation and release any 
	 *  allocated resources. A killed Processor cannot subsequently be started 
	 *  or otherwise reused.
	 * 
	 */
	
	public void kill()
	{
		super.kill();
		
		fFirstOutput = null;
		fOutputsByName = null;
		fOutputBasisBundleIdsByInputBasisBundleId = null;
		fOutputsByInputBasisBundleId = null;
	}
}

//--- Development History  ---------------------------------------------------
//
// $Log: DefaultProcessor.java,v $
// Revision 1.33  2006/03/14 14:57:15  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.32  2006/03/07 23:32:42  chostetter_cvs
// NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
// Revision 1.31  2006/01/24 16:19:16  chostetter_cvs
// Changed default ComponentManager behavior, default is now none
//
// Revision 1.30  2006/01/23 17:59:53  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.29  2005/11/15 23:14:20  chostetter_cvs
// Fixed issue with correct mapping of input DataBuffers to output DataBuffers when input is filtered
//
// Revision 1.28  2005/09/13 22:27:47  tames
// Changes to reflect DataRequester refactoring
//
// Revision 1.27  2005/04/05 20:35:36  chostetter_cvs
// Fixed problem with release status of BasisSets from which a copy was made; fixed problem with BasisSetEvent/BasisBundleEvent relationship and firing.
//
// Revision 1.26  2005/04/04 15:40:58  chostetter_cvs
// Extensive MemoryModel refactoring; removal of "enum" reserved-word variable names
//
// Revision 1.25  2005/03/24 22:28:01  chostetter_cvs
// Fix output BasisBundle resizing logic
//
// Revision 1.24  2005/02/03 01:04:15  chostetter_cvs
// Improved default behavior in case of no Output
//
// Revision 1.23  2005/02/02 22:26:31  chostetter_cvs
// Allow for possibility of no Output being present in default input/output association behavior
//
// Revision 1.22  2005/02/02 21:55:18  chostetter_cvs
// Fixed logic error in ensureCorrectOutputCapacity
//
// Revision 1.21  2005/02/02 21:46:38  chostetter_cvs
// Fixed logic error in getAssociatedOutput
//
// Revision 1.20  2005/02/02 20:47:01  chostetter_cvs
// Revised, refined input/output BasisBundle mapping, access to management methods, and documentation of same
//
// Revision 1.19  2005/02/01 20:53:54  chostetter_cvs
// Revised releasable BasisSet design, release policy
//
// Revision 1.18  2005/01/31 21:35:30  chostetter_cvs
// Fix for data request sequencing, documentation
//
// Revision 1.17  2005/01/27 21:38:02  chostetter_cvs
// Implemented new exception state and default exception behavior for Objects having ActivityState
//
// Revision 1.16  2004/12/01 21:30:59  chostetter_cvs
// Tweak
//
// Revision 1.15  2004/12/01 21:27:11  chostetter_cvs
// Added ability to avoid default input to output BasisBundle mapping
//
// Revision 1.14  2004/11/23 04:20:26  tames
// Changed log level of basis sets to finest so the console does not get
// overwhelmed by output.
//
// Revision 1.13  2004/09/02 19:39:57  chostetter_cvs
// Initial data-description redesign work
//
// Revision 1.12  2004/07/28 20:17:02  chostetter_cvs
// Implemented selectable and adaptive default output BasisBundle sizing
//
// Revision 1.11  2004/07/22 16:28:03  chostetter_cvs
// Various tweaks
//
// Revision 1.10  2004/07/22 15:10:50  chostetter_cvs
// Added BasisSetProcessor, handles releasing and synchronization
//
// Revision 1.9  2004/07/21 14:26:15  chostetter_cvs
// Various architectural and event-passing revisions
//
// Revision 1.8  2004/07/17 01:25:58  chostetter_cvs
// Refactored test algorithms
//
// Revision 1.7  2004/07/15 19:07:47  chostetter_cvs
// Added ability to block while waiting for a BasisBundle to have listeners
//
// Revision 1.6  2004/07/12 19:04:45  chostetter_cvs
// Added ability to find BasisBundleId, Components by their fully-qualified name
//
// Revision 1.5  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.4  2004/07/11 21:19:44  chostetter_cvs
// More Algorithm work
//
// Revision 1.3  2004/06/08 14:21:53  chostetter_cvs
// Added child/parent relationship to Components
//
// Revision 1.2  2004/06/04 05:34:42  chostetter_cvs
// Further data, Algorithm, and Component work
//
// Revision 1.1  2004/06/02 23:59:41  chostetter_cvs
// More Namespace, DataSpace tweaks, created alogirthms package
//
// Revision 1.2  2004/05/27 23:09:26  chostetter_cvs
// More Namespace related changes
//
// Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
// Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
//
