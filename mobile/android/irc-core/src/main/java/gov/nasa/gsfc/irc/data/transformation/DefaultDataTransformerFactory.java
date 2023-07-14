//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/transformation/DefaultDataTransformerFactory.java,v 1.6 2006/05/09 23:20:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataTransformerFactory.java,v $
//  Revision 1.6  2006/05/09 23:20:48  chostetter_cvs
//  Fixed parser and formatter instance configuration case bug
//
//  Revision 1.5  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.4  2005/09/30 20:55:47  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.3  2005/09/14 18:03:11  chostetter_cvs
//  Refactored descriptor-based factories
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
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

package gov.nasa.gsfc.irc.data.transformation;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DataTransformerDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.DefaultDataTransformerDescriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementFactory;
import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataTransformerFactory creates and returns instances of DataTransformers.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/09 23:20:48 $
 * @author Carl F. Hostetter
 */

public class DefaultDataTransformerFactory extends AbstractIrcElementFactory 
	implements DataTransformerFactory
{
	private static final String CLASS_NAME = 
		DefaultDataTransformerFactory.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static DataTransformerFactory fFactory;
	
	
	/**
	 *  Default constructor of a DataTransformerFactory.
	 *
	 */
	
	protected DefaultDataTransformerFactory()
	{
		
	}
	
	
	/**
	 *  Returns the singleton instance of a DefaultDataTransformerFactory.
	 *  
	 *  @return The singleton instance of a DefaultDataTransformerFactory
	 */
	
	public static DataTransformerFactory getInstance()
	{
		if (fFactory == null)
		{
			fFactory = new DefaultDataTransformerFactory();
		}
		
		return (fFactory);
	}
	
	
	/**
	 *  Creates and returns a DataTransformer appropriate to the given 
	 *  DataTransformerDescriptor.
	 *  
	 *  @param A DataTransformerDescriptor describing the desired 
	 *  	DataTransformer
	 *  @return A DataTransformer appropriate to the given 
	 *  	DataTransformerDescriptor
	 */
	
	public DataTransformer getDataTransformer
		(DataTransformerDescriptor descriptor)
	{
		DataTransformer transformer = null;
		
		if (descriptor != null)
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				transformer = (DataTransformer) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}	
			
			if (transformer == null)
			{
				if (descriptor instanceof DefaultDataTransformerDescriptor)
				{
					transformer = (DataTransformer) new DefaultDataTransformer
						((DefaultDataTransformerDescriptor) descriptor);
				}
				else
				{
					if (sLogger.isLoggable(Level.SEVERE))
					{
						String message = 
							"Unable to determine appropriate transformer for " + 
								descriptor;
						
						sLogger.logp(Level.SEVERE, CLASS_NAME, 
							"getDataTransformer", message);
					}
				}
			}
			else
			{
				if (transformer instanceof AbstractDataTransformer)
				{
					((AbstractDataTransformer) transformer).setDescriptor
						(descriptor);
				}
			}
		}
		
		return (transformer);
	}
	
	
	/**
	 *  Creates and returns a DataTransformer appropriate to the given 
	 *  DataTransformationDescriptor.
	 *  
	 *  @param A DataTransformationDescriptor describing the desired data 
	 *  	transformation scheme
	 *  @return A DataTransformer appropriate to the given 
	 *  	DataTransformationDescriptor
	 */
	
	public DataTransformer getDataTransformer
		(DataTransformationDescriptor descriptor)
	{
		DataTransformer transformer = null;
		
		if (descriptor != null) 
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				transformer = (DataTransformer) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}	
			
			if (transformer == null)
			{
				DataTransformerDescriptor transformerDescriptor = 
					descriptor.getDataTransformer();
				
				transformer = getDataTransformer(transformerDescriptor);
			}
			else
			{
				if (transformer instanceof AbstractDataTransformer)
				{
					((AbstractDataTransformer) transformer).setDescriptor
						(descriptor.getDataTransformer());
				}
			}
		}
		
		return (transformer);
	}
}
