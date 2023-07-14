//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataFormatterDescriptor.java,v $
//  Revision 1.6  2006/06/01 22:22:43  chostetter_cvs
//  Fixed problems with concatenated data selection and default overriding
//
//  Revision 1.5  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.4  2006/04/07 22:27:18  chostetter_cvs
//  Fixed problem with applying field formatting to all fields, tightened syntax
//
//  Revision 1.3  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
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

import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataFormatterDescriptor describes a formatter of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/01 22:22:43 $ 
 * @author Carl F. Hostetter   
**/

public interface DataFormatterDescriptor extends IrcElementDescriptor
{
	/**
	 * Sets the name of this DataFormatterDescriptor to the given name.
	 *
	 * @param name The new name of this DataFormatterDescriptor
	 **/

	public void setName(String name);
	

	/**
	 * Returns the DataSourceSelectionDescriptor associated with this 
	 * DataFormatterDescriptor (if any).
	 *
	 * @return The DataSourceSelectionDescriptor associated with this 
	 * 		DataFormatterDescriptor (if any)
	 **/

	public DataSourceSelectionDescriptor getSource();
	

	/**
	 * Returns the DataTargetSelectionDescriptor associated with this 
	 * DataFormatterDescriptor (if any).
	 *
	 * @return The DataTargetSelectionDescriptor associated with this 
	 * 		DataFormatterDescriptor (if any)
	 **/

	public DataTargetSelectionDescriptor getTarget();
	

	/**
	 * Returns true if this DataFormatterDescriptor is configured to use the name 
	 * of the data it formats as its own name, false otherwise.
	 *
	 * @return True if this DataFormatterDescriptor is configured to use the name 
	 * 		of the data it formats as its own name, false otherwise
	 **/

	public boolean usesDataNameAsName();
	

	/**
	 * Returns true if this DataFormatterDescriptor is configured to use its name 
	 * as a keyed data selector for its value, false otherwise.
	 *
	 * @return True if this DataFormatterDescriptor is configured to use its name 
	 * 		as a keyed data selector for its value, false otherwise
	 **/

	public boolean usesNameAsKeyedValueSelector();
	

	/**
	 * Returns true if this DataFormatterDescriptor is configured to override 
	 * the use of its name as a keyed data selector for its value, false 
	 * otherwise.
	 *
	 * @return True if this DataFormatterDescriptor is configured to override 
	 * 		the use of its name as a keyed data selector for its value, false 
	 * 		otherwise
	 **/

	public boolean overridesUseOfNameAsKeyedValueSelector();
}