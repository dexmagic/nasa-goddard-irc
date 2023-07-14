//=== File Prolog ============================================================
//
//	$Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/adapters/AbstractPortAdapter.java,v 1.11 2006/05/03 23:20:17 chostetter_cvs Exp $
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

package gov.nasa.gsfc.irc.devices.ports.adapters;

import gov.nasa.gsfc.irc.components.AbstractManagedComponent;
import gov.nasa.gsfc.irc.components.ComponentId;
import gov.nasa.gsfc.irc.data.selection.DataSelectorFactory;
import gov.nasa.gsfc.irc.data.selection.DefaultDataSelectorFactory;
import gov.nasa.gsfc.irc.data.transformation.DataTransformer;
import gov.nasa.gsfc.irc.data.transformation.DataTransformerFactory;
import gov.nasa.gsfc.irc.data.transformation.DefaultDataTransformerFactory;
import gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;


/**
 * Abstract implementation of the PortAdapter component interface. 
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractPortAdapter extends AbstractManagedComponent
	implements PortAdapter
{
	public static final String DEFAULT_NAME = "Port Adapter";
	
	private static final DataSelectorFactory sDataSelectorFactory = 
		DefaultDataSelectorFactory.getInstance();
	private static final DataTransformerFactory sDataTransformerFactory = 
		DefaultDataTransformerFactory.getInstance();

	private DataTransformer fDataTransformer;
	
	
	/**
	 *  Constructs a new PortAdapter whose ComponentId is the given ComponentId 
	 *  and that is managed by the default ComponentManager.
	 *  
	 *  @param componentId The ComponentId of the new PortAdapter
	 **/

	protected AbstractPortAdapter(ComponentId componentId)
	{
		super(componentId);
	}
		
	
	/**
	 *  Constructs a new PortAdapter having a default base name and managed by 
	 *  the default ComponentManager.
	 * 
	 *  @param name The base name of the new PortAdapter
	 *  @param parent The ComponentManager of the new PortAdapter
	 **/

	public AbstractPortAdapter()
	{
		super(DEFAULT_NAME);
	}
	
	
	/**
	/**
	 * Constructs a new PortAdapter having the given name and managed by the 
	 * default ComponentManager.
	 *
	 */
	
	public AbstractPortAdapter(String name)
	{
		super(name);
	}
	

	/**
	 *	Constructs a new PortAdapter configured according to the given 
	 *  AbstractAdapterDescriptor and managed by the default ComponentManager.
	 *
	 *  @param descriptor The AbstractAdapterDescriptor of the new PortAdapter
	 */
	
	public AbstractPortAdapter(AbstractAdapterDescriptor descriptor)
	{
		super(descriptor);
		
		configureFromDescriptor();
	}


	/**
	 * Causes this AbstractPortAdapter to (re)configure itself in accordance with 
	 * it current Descriptor.
	 *
	 */
	
	private void configureFromDescriptor()
	{
		AbstractAdapterDescriptor descriptor = 
			(AbstractAdapterDescriptor) getDescriptor();
		
		if (descriptor != null)
		{
			DataTransformationDescriptor dataTransformer = 
				descriptor.getDataTransformer();
			
			fDataTransformer = sDataTransformerFactory.getDataTransformer
					(dataTransformer);
		}
	}
	
	
	/**
	 *  Sets the Descriptor of this PortAdapter to the given Descriptor. The 
	 *  PortAdapter will in turn be (re)configured in accordance with the given 
	 *  Descriptor.
	 *  
	 *  @param descriptor A Descriptor
	**/
	
	public void setDescriptor(Descriptor descriptor)
	{
		super.setDescriptor(descriptor);
		
		if (descriptor instanceof AbstractAdapterDescriptor)
		{
			configureFromDescriptor();
		}
	}
	
	
	/**
	 * Returns the DataTransformer associated with this Adapter.
	 *
	 * @return The DataTransformer associated with this Adapter
	 */
	
	public DataTransformer getDataTransformer()
	{
		return (fDataTransformer);
	}
}


//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractPortAdapter.java,v $
//  Revision 1.11  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.10  2006/03/31 21:57:38  chostetter_cvs
//  Finished XML and Schema cleanup, all device descriptions now validate against IML
//
//  Revision 1.9  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.8  2006/03/07 23:32:42  chostetter_cvs
//  NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//
//  Revision 1.7  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.6  2005/09/14 21:31:18  chostetter_cvs
//  Fixed BasisBundle name issue in DataSpace
//
//  Revision 1.5  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.4  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.3  2004/11/10 23:09:56  chostetter_cvs
//  Initial debugging of Message formatting. Mostly works except for C-style formatting.
//
//  Revision 1.2  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
//
//  Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.1  2004/09/27 20:32:45  tames
//  Reflects a refactoring of Port architecture and renaming port parsers
//  to InputAdapters and port formatters to OutputAdapters.
//
//
