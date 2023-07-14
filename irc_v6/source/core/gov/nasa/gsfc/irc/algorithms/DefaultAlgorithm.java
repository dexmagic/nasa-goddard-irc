//=== File Prolog ============================================================
//
// $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/algorithms/DefaultAlgorithm.java,v 1.24 2006/03/14 21:26:11 chostetter_cvs Exp $
//
// This code was developed by NASA Goddard Space Flight Center, Code 588 
// for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
// Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//
// This software is property of the National Aeronautics and Space
// Administration. Unauthorized use or duplication of this software is
// strictly prohibited. Authorized users are subject to the following
// restrictions:
// *  Neither the author, their corporation, nor NASA is responsible for
//	  any consequence of the use of this software.
// *  The origin of this software must not be misrepresented either by
//	  explicit claim or by omission.
// *  Altered versions of this software must be plainly marked as such.
// *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.algorithms;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import gov.nasa.gsfc.commons.types.namespaces.Namespaces;
import gov.nasa.gsfc.irc.components.AbstractManagedCompositeComponent;
import gov.nasa.gsfc.irc.components.ComponentManager;
import gov.nasa.gsfc.irc.components.DefaultComponentManager;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;


/**
 * DefaultAlgorithm.java
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/03/14 21:26:11 $
 * @author carlwork
 */

public class DefaultAlgorithm extends AbstractManagedCompositeComponent 
	implements Algorithm
{
	private ComponentManager fInputs = new DefaultComponentManager(this);
	private ComponentManager fProcessors = new DefaultComponentManager(this);
	private ComponentManager fOutputs = new DefaultComponentManager(this);
	

	/**
	 *	Constructs a new Algorithm having a default name. Note that the new 
	 *  Algorithm will need to have its ComponentManager set (if any is desired).
	 *
	 */
	
	public DefaultAlgorithm()
	{
		this(Algorithm.DEFAULT_NAME);
	}
	
	
	/**
	 *  Constructs a new Algorithm having the given base name. Note that the new 
	 *  Algorithm will need to have its ComponentManager set (if any is desired).
	 * 
	 *  @param name The base name of the new Algorithm
	 **/

	public DefaultAlgorithm(String name)
	{
		super(name);
	}
	
	
	/**
	 *	Constructs a new Algorithm configured according to the given 
	 *  ComponentDescriptor. Note that the new Algorithm will need to have its 
	 *  ComponentManager set (if any is desired).
	 *
	 *  @param descriptor The ComponentDescriptor of the new Algorithm
	 */
	
	public DefaultAlgorithm(ComponentDescriptor descriptor)
	{
		super(descriptor);
	}
	
	
	/**
	 *  Adds the given Input to the Set of Inputs of this Algorithm. If the base 
	 *  name of the given Input already occurs in this Algorithm, it will be 
	 *  sequenced as needed to make it unique within this Algorithm.
	 *
	 *  @param input The Input to be added to this Algorithm
	 *  @return True if the given Input was actually added to this Algorithm
	 **/

	public boolean addInput(Input input)
	{
		boolean result = false;
		
		result = addComponent(input);
		
		if (result != false)
		{
			result = fInputs.addComponent(input);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the current number of Inputs of this Algorithm.
	 *  
	 *  @return	The current number of Inputs of this Algorithm
	 **/

	public final int getNumInputs()
	{
		return (fInputs.size());
	}


	/**
	 *  Returns the Set of Inputs of this Algorithm.
	 *
	 *  @return The Set of Inputs of this Algorithm
	 */

	public final Set getInputs()
	{
		return (fInputs.getMembers());
	}


	/**
	 *  Returns a Map of the Inputs of this Algorithm, keyed by their 
	 *  fully-qualified (and thus globally unique) names.
	 *
	 *  @return A Map of the Inputs of this Algorithm, keyed by their 
	 *  		fully-qualified (and thus globally unique) names
	 */

	public final Map getInputsByFullyQualifiedName()
	{
		return (fInputs.getComponentsByFullyQualifiedName());
	}


	/**
	 *  Returns the Input of this Algorithm that has the given fully-qualified (and 
	 *  thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified name of an Input of this 
	 *  		Algorithm
	 *  @return The Input of this Algorithm that has the given fully-qualified name
	 */

	public final Input getInput(String fullyQualifiedName)
	{
		return ((Input) fInputs.get(fullyQualifiedName));
	}


	/**
	 *  Returns the Input of this Algorithm that has the given sequenced (and thus 
	 *  Algorithm-unique) name.
	 *  
	 *  @param sequencedName The sequenced name of an Input of this Algorithm
	 *  @return The Input of this Algorithm that has the given sequenced name
	 */

	public final Input getInputBySequencedName(String sequencedName)
	{
		return ((Input) fInputs.get(Namespaces.formFullyQualifiedName
			(sequencedName, getFullyQualifiedName())));
	}


	/**
	 *  Starts all of the Inputs of this Algorithm.
	 *
	 */

	protected void startInputs()
	{
		fInputs.startAllComponents();
	}
	
	
	/**
	 *  Stops all of the Inputs of this Algorithm.
	 *
	 */

	protected void stopInputs()
	{
		fInputs.stopAllComponents();
	}
	
	
	/**
	 *  Adds the given Output to the Set of Outputs of this Algorithm. If the base 
	 *  name of the given Output already occurs in this Algorithm, it will be 
	 *  sequenced as needed to make it unique within this Algorithm.
	 *
	 *  @param output The Output to be added to this Algorithm
	 *  @return True if the given Output was actually added to this Algorithm
	 **/

	public boolean addOutput(Output output)
	{
		boolean result = false;
		
		result = addComponent(output);
		
		if (result != false)
		{
			result = fOutputs.addComponent(output);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the current number of Outputs of this Algorithm.
	 *  
	 *  @return	The current number of Outputs of this Algorithm
	 **/

	public final int getNumOutputs()
	{
		return (fOutputs.size());
	}


	/**
	 *  Returns the Set of Outputs of this Algorithm.
	 *
	 *  @return The Set of Outputs of this Algorithm
	 */

	public final Set getOutputs()
	{
		return (fOutputs.getMembers());
	}


	/**
	 *  Returns a Map of the Outputs of this Algorithm, keyed by their 
	 *  fully-qualified (and thus globally unique) names.
	 *
	 *  @return A Map of the Outputs of this Algorithm, keyed by their 
	 *  		fully-qualified (and thus globally unique) names
	 */

	public final Map getOutputsByFullyQualifiedName()
	{
		return (fOutputs.getComponentsByFullyQualifiedName());
	}


	/**
	 *  Returns the Output of this Algorithm that has the given fully-qualified (and 
	 *  thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified name of an Output of this 
	 *  		Algorithm
	 *  @return The Output of this Algorithm that has the given fully-qualified name
	 */

	public final Output getOutput(String fullyQualifiedName)
	{
		return ((Output) fOutputs.get(fullyQualifiedName));
	}


	/**
	 *  Returns the Output of this Algorithm that has the given sequenced (and thus 
	 *  Algorithm-unique) name.
	 *  
	 *  @param sequencedName The sequenced name of an Output of this Algorithm
	 *  @return The Output of this Algorithm that has the given sequenced name
	 */

	public final Output getOutputBySequencedName(String sequencedName)
	{
		return ((Output) fOutputs.get(Namespaces.formFullyQualifiedName
				(sequencedName, getFullyQualifiedName())));
	}


	/**
	 *  Starts all of the Outputs of this Algorithm.
	 *
	 */

	protected void startOutputs()
	{
		fOutputs.startAllComponents();
	}
	
	
	/**
	 *  Stops all of the Outputs of this Algorithm.
	 *
	 */

	protected void stopOutputs()
	{
		fOutputs.stopAllComponents();
	}
	
	
	/**
	 *  Adds the given Processor to the Set of Processors of this Algorithm. If the 
	 *  base name of the given Processor already occurs in this Algorithm, it will 
	 *  be sequenced as needed to make it unique within this Algorithm.
	 *
	 *  @param processor The Processor to be added to this Algorithm
	 *  @return True if the given Processor was actually added to this Algorithm
	 **/

	public boolean addProcessor(Processor processor)
	{
		boolean result = false;
		
		result = addComponent(processor);
		
		if (result != false)
		{
			result = fProcessors.addComponent(processor);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the current number of Processors of this Algorithm.
	 *  
	 *  @return	The current number of Processors of this Algorithm
	 **/

	public final int getNumProcessors()
	{
		return (fProcessors.size());
	}


	/**
	 *  Returns the Set of Processors of this Algorithm.
	 *
	 *  @return The Set of Processors of this Algorithm
	 */

	public final Set getProcessors()
	{
		return (fProcessors.getMembers());
	}


	/**
	 *  Returns a Map of the Processors of this Algorithm, keyed by their 
	 *  fully-qualified (and thus globally unique) names.
	 *
	 *  @return A Map of the Processors of this Algorithm, keyed by their 
	 *  		fully-qualified (and thus globally unique) names
	 */

	public final Map getProcessorsByFullyQualifiedName()
	{
		return (fProcessors.getComponentsByFullyQualifiedName());
	}


	/**
	 *  Returns the Processor of this Algorithm that has the given fully-qualified 
	 *  (and thus globally unique) name.
	 *  
	 *  @param fullyQualifiedName The fully-qualified name of a Processor of this 
	 *  		Algorithm
	 *  @return The Processor of this Algorithm that has the given fully-qualified 
	 *  		name
	 */

	public final Processor getProcessor(String fullyQualifiedName)
	{
		return ((Processor) fProcessors.get(fullyQualifiedName));
	}


	/**
	 *  Returns the Processor of this Algorithm that has the given sequenced (and 
	 *  thus Algorithm-unique) name.
	 *  
	 *  @param sequencedName The sequenced name of a Processor of this Algorithm
	 *  @return The Processor of this Algorithm that has the given sequenced name
	 */

	public final Processor getProcessorBySequencedName(String sequencedName)
	{
		return ((Processor) fProcessors.get(Namespaces.formFullyQualifiedName
			(sequencedName, getFullyQualifiedName())));
	}


	/**
	 *  Starts all of the Processors of this Algorithm.
	 *
	 */

	protected void startProcessors()
	{
		fProcessors.startAllComponents();
	}
	
	
	/**
	 *  Stops all of the Processors of this Algorithm.
	 *
	 */

	protected void stopProcessors()
	{
		fProcessors.stopAllComponents();
	}
	
	
	/**
	 *  Starts all of the Components of this Algorithm.
	 *
	 */

	public void startAllComponents()
	{
		startOutputs();
		startProcessors();
		startInputs();
		
		super.startAllComponents();
	}
	
	
	/**
	 *  Stops all of the Components of this Algorithm.
	 *
	 */

	public void stopAllComponents()
	{
		stopInputs();
		stopProcessors();
		stopOutputs();
		
		super.stopAllComponents();
	}
	
	
	/**
	 *  Causes this Algorithm to immediately cease operation and 
	 *  release any allocated resources. A killed Algorithm cannot 
	 *  subsequently be started or otherwise reused.
	 * 
	 */
	
	public void kill()
	{
		fInputs.killAllComponents();
		fInputs = null;
		
		fProcessors.killAllComponents();
		fProcessors = null;
		
		fOutputs.killAllComponents();
		fOutputs = null;
		
		super.kill();
	}
	
	
	/**
	 *  Returns a String representation of this Algorithm.
	 * 
	 *  @return A String representation of this Algorithm
	 */
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(super.toString());
		
		int numInputs = fInputs.size();
		
		if (numInputs > 0)
		{
			buffer.append("\nHas " + numInputs + " inputs:");
			
			Iterator inputs = fInputs.iterator();
			
			while (inputs.hasNext())
			{
				for (int i = 1; inputs.hasNext(); i++)
				{
					Input input = (Input) inputs.next();
					
					String sequencedName = input.getSequencedName();
					
					buffer.append("\n" + i + ": " + sequencedName);
				}
			}
		}
		else
		{
			buffer.append("\nHas no inputs");
		}
		
		int numProcessors = fProcessors.size();
		
		if (numProcessors > 0)
		{
			buffer.append("\nHas " + numProcessors + " processors:");
			
			Iterator processors = fProcessors.iterator();
			
			while (processors.hasNext())
			{
				for (int i = 1; processors.hasNext(); i++)
				{
					Processor processor = (Processor) processors.next();
					
					String sequencedName = processor.getSequencedName();
					
					buffer.append("\n" + i + ": " + sequencedName);
				}
			}
		}
		else
		{
			buffer.append("\nHas no processors");
		}
				
		int numOutputs = fOutputs.size();
		
		if (numOutputs > 0)
		{
			buffer.append("\nHas " + numOutputs + " outputs:");
			
			Iterator outputs = fOutputs.iterator();
			
			while (outputs.hasNext())
			{
				for (int i = 1; outputs.hasNext(); i++)
				{
					Output output = (Output) outputs.next();
					
					String sequencedName = output.getSequencedName();
					
					buffer.append("\n" + i + ": " + sequencedName);
				}
			}
		}
		else
		{
			buffer.append("\nHas no outputs");
		}
		
		return (buffer.toString());
	}
}

//--- Development History  ---------------------------------------------------
//
// $Log: DefaultAlgorithm.java,v $
// Revision 1.24  2006/03/14 21:26:11  chostetter_cvs
// Fixed manager proxy issue that was preventing component browser from updating
//
// Revision 1.23  2006/03/14 17:33:33  chostetter_cvs
// Beefed up toString() method
//
// Revision 1.22  2006/03/14 16:13:18  chostetter_cvs
// Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
// Revision 1.21  2006/03/14 14:57:15  chostetter_cvs
// Simplified Namespace, Manager, Component-related constructors
//
// Revision 1.20  2006/03/07 23:32:42  chostetter_cvs
// NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
// Revision 1.19  2006/01/24 15:55:14  chostetter_cvs
// Changed default ComponentManager behavior, default is now none
//
// Revision 1.18  2006/01/23 17:59:53  chostetter_cvs
// Massive Namespace-related changes
//
// Revision 1.17  2005/04/12 22:25:45  chostetter_cvs
// Added ordering to component starting to parallel stopping
//
// Revision 1.16  2005/04/12 22:15:16  chostetter_cvs
// Fixed ordering, synchronization issues with including pending data on stop
//
// Revision 1.15  2005/01/11 21:35:46  chostetter_cvs
// Initial version
//
// Revision 1.14  2004/11/30 18:45:16  tames
// Removed the registering of this component with the component manager
// that was done in the constructor. This was causing the new algorithm
// to be added twice. Once  by the object creating the algorithm
// (Component Browser) and the constructor.
//
// Revision 1.13  2004/07/22 22:15:11  chostetter_cvs
// Created Algorithm versions of library Processors
//
// Revision 1.12  2004/07/22 20:14:58  chostetter_cvs
// Data, Component namespace work
//
// Revision 1.11  2004/07/22 17:06:55  chostetter_cvs
// Namespace-related changes
//
// Revision 1.10  2004/07/15 17:48:55  chostetter_cvs
// ComponentManager, property change work
//
// Revision 1.9  2004/07/12 19:04:45  chostetter_cvs
// Added ability to find BasisBundleId, Components by their fully-qualified name
//
// Revision 1.8  2004/07/12 14:26:24  chostetter_cvs
// Reformatted for tabs
//
// Revision 1.7  2004/07/11 21:19:44  chostetter_cvs
// More Algorithm work
//
// Revision 1.6  2004/07/06 13:40:00  chostetter_cvs
// Commons package restructuring
//
// Revision 1.5  2004/07/02 02:33:30  chostetter_cvs
// Renamed DataRequest to BasisRequest
//
// Revision 1.4  2004/06/29 22:46:13  chostetter_cvs
// Fixed broken CVS-generated comments. Grrr.
//
// Revision 1.3  2004/06/29 22:39:39  chostetter_cvs
// Successful testing of data flow from an Output to an Input, 
// with simplest form of BasisRequest (requesting all data). 
// Also tweaked Composites.
//
// Revision 1.2  2004/06/09 03:28:49  chostetter_cvs
// Output-related modifications
//
// Revision 1.1  2004/06/04 05:34:42  chostetter_cvs
// Further data, Algorithm, and Component work
//
