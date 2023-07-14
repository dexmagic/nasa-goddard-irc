//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataTransformerDescriptor.java,v $
//  Revision 1.5  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.transformation.description;

import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.irc.data.selection.description.BufferedDataSelectionDescriptor;


/**
 * A DataTransformerDescriptor describes a data transformer.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public interface DataTransformerDescriptor extends HasName
{
	/**
	 * Returns a (deep) copy of this DataTransformerDescriptor.
	 *
	 * @return A (deep) copy of this DataTransformerDescriptor 
	 **/

	public Object clone();

	
	/**
	 *  Sets the name of the DataTransformer described by this 
	 *  DataTransformerDescriptor to the given name.
	 *
	 *  @param name The desired new name of the DataTransformer described by this 
	 * 		DataTransformerDescriptor
	 **/

	public void setName(String name);
	
	
	/**
	 * Returns true if the DataTransformer described by this 
	 * DataTransformerDescriptor is enabled, false otherwise.
	 *
	 * @return True if the DataTransformer described by this 
	 * 		DataTransformerDescriptor is enabled, false otherwise
	 **/
	
	public boolean isEnabled();
	
	
	/**
	 * Returns true if the DataTransformer described by this 
	 * DataTransformerDescriptor is configured to publish parse results as 
	 * Messages, false otherwise.
	 *
	 * @return True if the data transformer described by this 
	 * 		DataTransformerDescriptor is configured to publish parse results 
	 *  	as Messages, false otherwise
	 **/
	
	public boolean publishesParseAsMessage();
	
	
	/**
	 * Returns the DataSourceSelectionDescriptor associated with this 
	 * DataTransformerDescriptor (if any).
	 *
	 * @return The DataSourceSelectionDescriptor associated with this 
	 * 		DataTransformerDescriptor (if any)
	 **/

	public DataSourceSelectionDescriptor getSource();
	

	/**
	 * Returns the DataTargetSelectionDescriptor associated with this 
	 * DataTransformerDescriptor (if any).
	 *
	 * @return The DataTargetSelectionDescriptor associated with this 
	 * 		DataTransformerDescriptor (if any)
	 **/

	public DataTargetSelectionDescriptor getTarget();
	

	/**
	 * Returns the BufferedDataSelectionDescriptor associated with this 
	 * DataTransformerDescriptor (if any).
	 *
	 * @return The BufferedDataSelectionDescriptor associated with this 
	 * 		DataTransformerDescriptor (if any)
	 **/

	public BufferedDataSelectionDescriptor getBuffer();
	

	/**
	 * Returns the DataParseDescriptor associated with this 
	 * DataTransformerDescriptor (if any).
	 *
	 * @return The DataParseDescriptor associated with this 
	 * 		DataTransformerDescriptor (if any)
	 **/

	public DataParseDescriptor getParse();
	

	/**
	 * Returns the DefaultDataLogDescriptor associated with this 
	 * DataTransformerDescriptor (if any).
	 *
	 * @return The DefaultDataLogDescriptor associated with this 
	 * 		DataTransformerDescriptor (if any)
	 **/

	public DataLogDescriptor getLog();
	

	/**
	 * Returns the DataFormatDescriptor associated with this 
	 * DataTransformerDescriptor (if any).
	 *
	 * @return The DataFormatDescriptor associated with this 
	 * 		DataTransformerDescriptor (if any)
	 **/

	public DataFormatDescriptor getFormat();
}