//=== File Prolog ============================================================
//
//	$Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/components/DefaultComponentFactory.java,v 1.11 2006/02/04 17:09:30 tames Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: DefaultComponentFactory.java,v $
//	Revision 1.11  2006/02/04 17:09:30  tames
//	Added functionality to start component if specified in descriptor.
//	
//	Revision 1.10  2006/01/23 17:59:51  chostetter_cvs
//	Massive Namespace-related changes
//	
//	Revision 1.9  2005/04/06 14:59:46  chostetter_cvs
//	Adjusted logging levels
//	
//	Revision 1.8  2004/10/14 15:16:51  chostetter_cvs
//	Extensive descriptor-oriented refactoring
//	
//	Revision 1.7  2004/08/03 20:28:39  tames_cvs
//	Changed descriptor configuration handling
//	
//	Revision 1.6  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.5  2004/06/03 00:19:59  chostetter_cvs
//	Organized imports
//	
//	Revision 1.4  2004/06/02 23:59:41  chostetter_cvs
//	More Namespace, DataSpace tweaks, created alogirthms package
//	
//	Revision 1.3  2004/05/28 05:58:20  chostetter_cvs
//	More Namespace, DataSpace, Descriptor worl
//	
//	Revision 1.2  2004/05/27 16:10:02  tames_cvs
//	CLASS_NAME assignment fix
//	
//	Revision 1.1  2004/05/14 20:01:00  chostetter_cvs
//	Initial version. Much functionality of implementation classes yet undefined, but many useful interfaces
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

package gov.nasa.gsfc.irc.components;


import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.processing.activity.Startable;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;


/**
 *  A ComponentFactory creates and configures new instances of IRC Components 
 *  upon request.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/02/04 17:09:30 $
 *  @author Carl F. Hostetter
 */

public class DefaultComponentFactory implements ComponentFactory
{
	private static final String CLASS_NAME = 
		DefaultComponentFactory.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);
	

	/**
	 *	Creates and returns a new, default instantiation of a 
	 *  Component of the type indicated by the given Class name.
	 *
	 *	@param className The name of a Component Class
	 *  @return A new Component of the indicated type
	 */
	
	public MinimalComponent createComponent(String className)
	{
		MinimalComponent newComponent = null;
		
		if (sLogger.isLoggable(Level.FINE))
		{
			String message = 
				"Creating instance of Component Class " + 
				className + "...";
			
			sLogger.logp(Level.FINE, CLASS_NAME, 
				"createComponent", message);
		}
		
		try
		{
			newComponent = (MinimalComponent) Irc.instantiateClass(className);
		}
		catch (ClassNotFoundException ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Class " + className + 
					" could not be found";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"createComponent", message, ex);
			}
		}
		catch (Exception ex)
		{
			if (sLogger.isLoggable(Level.SEVERE))
			{
				String message = "Class " + className + 
					" could not be instantiated";
				
				sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"createComponent", message, ex);
			}
		}
			
		return (newComponent);
	}
	
	
	/**
	 *	Creates and returns a new, default instantiation of a 
	 *  Component of the type indicated by the given Class name, and 
	 *  having the given name.
	 *
	 *	@param className The name of a Component Class
	 *	@param componentName The name of the new Component
	 *  @return A new Component of the indicated type and having the 
	 * 		given name
	 */
	
	public MinimalComponent createComponent(String className, 
		String componentName)
	{
		MinimalComponent newComponent = createComponent(className);
		
		if ((componentName != null) && (newComponent != null))
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = 
					"Setting new Component's name to " + 
					componentName + "...";
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"createComponent", message);
			}
			
			try
			{
				newComponent.setName(componentName);
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to set name of new Component to " + 
						componentName;
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"createComponent", message, ex);
				}
			}
		}
			
		return (newComponent);
	}

	
	/**
	 *	Creates and returns a new Component, as described by the given 
	 *	ComponentDescriptor.
	 *
	 *	@param descriptor The ComponentDescriptor that describes the
	 *		desired Component
	 *  @return A new Component, as described by the given 
	 *		ComponentDescriptor
	 */
	
	public MinimalComponent createComponent(ComponentDescriptor descriptor)
	{
		MinimalComponent newComponent = 
			createComponent(descriptor.getClassName());
		
		if (newComponent != null)
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = 
					"Initializing new Component from Descriptor...";
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"createComponent", message);
			}
			
			if (newComponent instanceof IrcComponent)
			{
				((IrcComponent) newComponent).setDescriptor(descriptor);
			}
		}
		
		if (descriptor.isStarted() && newComponent instanceof Startable)
		{
			((Startable) newComponent).start();
		}

		return (newComponent);
	}


	/**
	 *	Creates and returns a new Component, as described by the given 
	 *	ComponentDescriptor and having the given name.
	 *
	 *	@param descriptor The ComponentDescriptor that describes the
	 *		desired Component
	 *	@param name The name of the new Component
	 *  @return A new Component, as described by the given 
	 *		ComponentDescriptor and having the given name
	 */
	
	public MinimalComponent createComponent
		(ComponentDescriptor descriptor, String name)
	{
		MinimalComponent newComponent = createComponent(descriptor);
		
		if ((name != null) && (newComponent != null))
		{
			if (sLogger.isLoggable(Level.FINE))
			{
				String message = 
					"Setting new Component's name to " + 
					name + "...";
				
				sLogger.logp(Level.FINE, CLASS_NAME, 
					"createComponent", message);
			}
			
			try
			{
				newComponent.setName(name);
			}
			catch (PropertyVetoException ex)
			{
				if (sLogger.isLoggable(Level.WARNING))
				{
					String message = "Unable to set name of new Component to " + 
						name;
					
					sLogger.logp(Level.WARNING, CLASS_NAME, 
						"createComponent", message, ex);
				}
			}
		}
		
		if (descriptor.isStarted() && newComponent instanceof Startable)
		{
			((Startable) newComponent).start();
		}

		return (newComponent);
	}
}
