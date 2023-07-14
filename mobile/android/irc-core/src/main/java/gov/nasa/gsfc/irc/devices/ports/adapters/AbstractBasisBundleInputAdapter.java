//=== File Prolog ============================================================
//
//	$Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/devices/ports/adapters/AbstractBasisBundleInputAdapter.java,v 1.2 2006/08/01 19:55:47 chostetter_cvs Exp $
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


/**
 * A BasisBundleInputAdapter transforms raw input data into a BasisBundle.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/08/01 19:55:47 $
 * @author	Carl F. Hostetter
 */
public abstract class AbstractBasisBundleInputAdapter 
	extends AbstractInputAdapter implements BasisBundleInputAdapter 
{
	public static final String DEFAULT_NAME = "BasisBundle Input Adapter";
	
	/**
	 *  Constructs a new BasisBundleInputAdapter having a default name, belonging 
	 *  to the global Namespace, and managed by the default ComponentManager.
	 * 
	 **/

	public AbstractBasisBundleInputAdapter()
	{
		this(DEFAULT_NAME);
	}
	
	
	/**
	 * Constructs a new BasisBundleInputAdapter having the given base name, 
	 * belonging to the global Namespace, and managed by the default 
	 * ComponentManager.
	 * 
	 * @param name The name of the new BasisBundleInputAdapter
	 */
	
	public AbstractBasisBundleInputAdapter(String name)
	{
		super(name);
	}
	

	/**
	 *	Constructs a new BasisBundleInputAdapter configured according to the 
	 *  given InputAdapterDescriptor, belonging to the global Namespace, and 
	 *  managed by the global ComponentManager.
	 *
	 *  @param descriptor The InputAdapterDescriptor of the new 
	 *  		BasisBundleInputAdapter
	 */
	
	public AbstractBasisBundleInputAdapter(InputAdapterDescriptor descriptor)
	{
		this(descriptor.getName());
		
		setDescriptor(descriptor);
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractBasisBundleInputAdapter.java,v $
//  Revision 1.2  2006/08/01 19:55:47  chostetter_cvs
//  Revised, simplified hierarchy of MemberId/CreatorId/BasisBundleResourceId
//
//  Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//
