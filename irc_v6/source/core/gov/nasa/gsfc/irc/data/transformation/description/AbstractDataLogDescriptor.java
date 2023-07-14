//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataLogDescriptor.java,v $
//  Revision 1.1  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
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

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataLogDescriptor describes a means of logging data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public abstract class AbstractDataLogDescriptor extends AbstractIrcElementDescriptor 
	implements DataLogDescriptor
{
	protected DataLoggerDescriptor fDataLogger;
	
	
	/**
	 * Constructs a new AbstractDataLogDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new AbstractDataLogDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataLogDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataLogDescriptor		
	**/
	
	public AbstractDataLogDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Datatransml.N_LOGS);
	}
	

	/**
	 * Constructs a (shallow) copy of the given AbstractDataLogDescriptor.
	 *
	 * @param descriptor The AbstractDataLogDescriptor to be copied
	**/
	
	protected AbstractDataLogDescriptor(AbstractDataLogDescriptor descriptor)
	{
		super(descriptor);
		
		fDataLogger = descriptor.fDataLogger;
	}
	

	/**
	 * Returns a (deep) copy of this AbstractDataLogDescriptor.
	 *
	 * @return A (deep) copy of this AbstractDataLogDescriptor
	**/
	
	public Object clone()
	{
		AbstractDataLogDescriptor result = (AbstractDataLogDescriptor) super.clone();
		
		result.fDataLogger = (DataLoggerDescriptor) fDataLogger.clone();
		
		return (result);
	}
	
	
	/**
	 * Sets the DataLoggerDescriptor associated with this AbstractDataLogDescriptor 
	 * to the given DataLoggerDescriptor.
	 *
	 * @param logger The new DataLoggerDescriptor associated with this 
	 * 		AbstractDataLogDescriptor 
	**/
	
	public void setDataLogger(DataLoggerDescriptor logger)
	{
		fDataLogger = logger;
	}
	

	/**
	 * Returns the DataLoggerDescriptor associated with this AbstractDataLogDescriptor 
	 * (if any).
	 *
	 * @return The DataLoggerDescriptor associated with this AbstractDataLogDescriptor 
	 *		(if any) 
	**/
	
	public DataLoggerDescriptor getDataLogger()
	{
		return (fDataLogger);
	}
	

	/** 
	 *  Returns a String representation of this AbstractDataLogDescriptor.
	 *
	 *  @return A String representation of this AbstractDataLogDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataLogDescriptor: ");
		result.append("\n" + super.toString());
		
		if (fDataLogger != null)
		{
			result.append("\nData Logger: " + fDataLogger);
		}
		
		return (result.toString());
	}
}
