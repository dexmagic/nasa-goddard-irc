//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataLoggerDescriptor.java,v $
//  Revision 1.6  2006/05/31 21:14:21  chostetter_cvs
//  Fixed loadStringAttribute call to use null default
//
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

import org.jdom.Element;

import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataLoggerDescriptor describes a logger of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/31 21:14:21 $ 
 * @author Carl F. Hostetter   
**/

public abstract class AbstractDataLoggerDescriptor 
	extends AbstractIrcElementDescriptor implements DataLoggerDescriptor
{
	private String fLogName;
	private Level fLevel;
	private DataFormatDescriptor fFormat;
	
	
	/**
	 * Constructs a new AbstractDataLoggerDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new AbstractDataLoggerDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataLoggerDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataLoggerDescriptor		
	**/
	
	public AbstractDataLoggerDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Datatransml.N_LOGS);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given AbstractDataLoggerDescriptor.
	 *
	 * @param descriptor The AbstractDataLoggerDescriptor to be cloned
	**/
	
	protected AbstractDataLoggerDescriptor(AbstractDataLoggerDescriptor descriptor)
	{
		super(descriptor);
		
		fLevel = descriptor.fLevel;
		fFormat = descriptor.fFormat;
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataLoggerDescriptor#clone()
	 */
	
	public Object clone()
	{
		AbstractDataLoggerDescriptor result = 
			(AbstractDataLoggerDescriptor) super.clone();
		
		result.fLevel = fLevel;
		result.fFormat = fFormat;
		
		return (result);
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataLoggerDescriptor#getLogName()
	 */
	
	public String getLogName()
	{
		return (fLogName);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataLoggerDescriptor#getLevel()
	 */
	
	public Level getLevel()
	{
		return (fLevel);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataLoggerDescriptor#getDataFormat()
	 */
	
	public DataFormatDescriptor getDataFormat()
	{
		return (fFormat);
	}
	

	/** 
	 *  Returns a String representation of this AbstractDataLoggerDescriptor.
	 *
	 *  @return A String representation of this AbstractDataLoggerDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataLoggerDescriptor: ");
		result.append("\n" + super.toString());
		
		result.append("\nLevel = " + fLevel);
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this AbstractDataLoggerDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the level attribute (if any).
		fLogName = fSerializer.loadStringAttribute
			(Datatransml.A_LOG_NAME, null, fElement);
		
		// Unmarshall the level attribute (if any).
		String levelName = fSerializer.loadStringAttribute
			(Datatransml.A_LEVEL, Datatransml.INFO, fElement);
		
		if (levelName != null)
		{
			fLevel = Level.parse(levelName);
		}
		
		// Unmarshall the DataFormatterDescriptor (if any).
		fFormat = (DataFormatDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Datatransml.E_FORMAT, Datatransml.C_FORMAT, 
					fElement, this, fDirectory);
	}
}
