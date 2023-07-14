//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/description/xml/AbstractIrcElementFactory.java,v 1.2 2005/09/30 20:55:47 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractIrcElementFactory.java,v $
//  Revision 1.2  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.1  2005/09/14 18:03:11  chostetter_cvs
//  Refactored descriptor-based factories
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
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

package gov.nasa.gsfc.irc.description.xml;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.app.Irc;


/**
 * An IrcElementFactory creates and returns instances of IRC element Objects.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/30 20:55:47 $
 * @author Carl F. Hostetter
 */

public abstract class AbstractIrcElementFactory implements IrcElementFactory
{
	private static final String CLASS_NAME = AbstractIrcElementFactory.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	

	/**
	 *  Creates and returns an IRC element Object appropriate to the given 
	 *  IrcElementDescriptor.
	 *  
	 *  @param descriptor An IrcElementDescriptor describing the desired 
	 *  	element Object
	 *  @return An IRC element Object appropriate to the given 
	 *  		IrcElementDescriptor
	 */
	
	public Object getIrcElement(IrcElementDescriptor descriptor)
	{
		Object result = null;
		
		if (descriptor != null)
		{
			String className = descriptor.getClassName();
			
			if (className != null)
			{
				try
				{
					result = Irc.instantiateClass(className);
				}
				catch (Exception ex)
				{
					if (sLogger.isLoggable(Level.SEVERE))
					{
						String message = "Unable to instantiate class " + 
							className;
						
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"getIrcElement", message, ex);
						
						throw (new IllegalArgumentException(message));
					}
				}
			}
		}

		return (result);
	}
}
