//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SwitchedFormatterDescriptor.java,v $
//  Revision 1.1  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
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

import java.util.Map;

import gov.nasa.gsfc.irc.data.state.DataStateDeterminerDescriptor;


/**
 * A SwitchedFormatterDescriptor describes a set of selectable format cases.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/04/27 19:46:21 $ 
 * @author Carl F. Hostetter   
**/

public interface SwitchedFormatterDescriptor extends DataFormatterDescriptor
{
	/** 
	 *  Returns the DeterminerDescriptor of the case selector associated with this 
	 *  SwitchedFormatterDescriptor.
	 *
	 *  @return The DeterminerDescriptor of the case selector associated with this 
	 *  		SwitchedFormatterDescriptor
	**/
	
	public DataStateDeterminerDescriptor getCaseSelector();
	
	
	/** 
	 *  Returns a Map (by name) of the FormatCaseDescriptors associated with this 
	 *  SwitchedFormatterDescriptor.
	 *
	 *  @return A Map (by name) of the FormatCaseDescriptors associated with this 
	 *  		SwitchedFormatterDescriptor
	**/
	
	public Map getFormatCases();
}
