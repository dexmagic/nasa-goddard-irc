//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: DataLoggerDescriptor.java,v $
//  Revision 1.5  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.4  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.3  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.2  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.1  2005/09/13 20:30:12  chostetter_cvs
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

import java.util.logging.Level;

import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataLoggerDescriptor describes a logger of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public interface DataLoggerDescriptor extends IrcElementDescriptor
{
	/**
	 * Sets the name of this DataLoggerDescriptor to the given name.
	 *
	 * @param name The new name of this DataLoggerDescriptor
	 **/

	public void setName(String name);
	

	/**
	 * Returns the log name of this DataLoggerDescriptor.
	 *
	 * @return The log name of this DataLoggerDescriptor
	 **/

	public String getLogName();
	

	/** 
	 *  Returns the logging Level associated with this DataLoggerDescriptor.
	 *
	 *  @return The logging Level associated with this DataLoggerDescriptor
	**/
	
	public Level getLevel();
	
	
	/** 
	 *  Returns the DataFormatDescriptor associated with this 
	 *  DataLoggerDescriptor.
	 *
	 *  @return The DataFormatDescriptor associated with this 
	 *  	DataLoggerDescriptor
	**/
	
	public DataFormatDescriptor getDataFormat();
}