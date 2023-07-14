//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: AbstractDataTransformerDescriptor.java,v $
//  Revision 1.5  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.4  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.3  2005/09/16 00:29:37  chostetter_cvs
//  Support for initial implementation of SCL response parsing; also fixed issues with data logging
//
//  Revision 1.2  2005/09/14 19:32:07  chostetter_cvs
//  Added ability to publish parse results as a Message
//
//  Revision 1.1  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.1  2004/10/16 22:34:23  chostetter_cvs
//  Extensive data transformation work, not hooked up yet
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

import gov.nasa.gsfc.irc.data.selection.description.BufferedDataSelectionDescriptor;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementDescriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A DataTransformationDescriptor describes a transformer of data.
 *
 * <p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $ 
 * @author Carl F. Hostetter   
**/

public class AbstractDataTransformerDescriptor extends AbstractIrcElementDescriptor 
	implements DataTransformerDescriptor
{
	private DataSourceSelectionDescriptor fSource;
	private DataTargetSelectionDescriptor fTarget;
	private BufferedDataSelectionDescriptor fBuffer;
	private DataParseDescriptor fParse;
	private DataLogDescriptor fLog;
	private DataFormatDescriptor fFormat;
	
	private boolean fEnabled = false;
	private boolean fPublishParseAsMessage = false;
	
	
	/**
	 * Constructs a new AbstractDataTransformerDescriptor having the given parent 
	 * Descriptor and belonging to the given DescriptorDirectory by unmarshalling 
	 * from the given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new AbstractDataTransformerDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataTransformerDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataTransformerDescriptor		
	**/
	
	public AbstractDataTransformerDescriptor(Descriptor parent, 
		DescriptorDirectory directory, Element element)
	{
		super(parent, directory, element, Datatransml.N_TRANSFORMATIONS);
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a (shallow) copy of the given AbstractDataTransformerDescriptor.
	 *
	 * @param descriptor The AbstractDataTransformerDescriptor to be copied
	 * @param directory The DescriptorDirectory to which the new 
	 * 		AbstractDataTransformerDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		AbstractDataTransformerDescriptor		
	**/
	
	protected AbstractDataTransformerDescriptor
		(AbstractDataTransformerDescriptor descriptor)
	{
		super(descriptor);
		
		fSource = descriptor.fSource;
		fTarget = descriptor.fTarget;
		fParse = descriptor.fParse;
		fLog = descriptor.fLog;
		fFormat = descriptor.fFormat;
		
		fPublishParseAsMessage = descriptor.fPublishParseAsMessage;
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor#clone()
	 */
	
	public Object clone()
	{
		AbstractDataTransformerDescriptor result = 
			(AbstractDataTransformerDescriptor) super.clone();
		
		result.fSource = (DataSourceSelectionDescriptor) fSource.clone();
		result.fTarget = (DataTargetSelectionDescriptor) fTarget.clone();
		result.fParse = (DataParseDescriptor) fParse.clone();
		result.fLog = (DataLogDescriptor) fLog.clone();
		result.fFormat = (DataFormatDescriptor) fFormat.clone();
		
		result.fPublishParseAsMessage = fPublishParseAsMessage;
		
		return (result);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor#getSource()
	 */
	
	public DataSourceSelectionDescriptor getSource()
	{
		return (fSource);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor#getTarget()
	 */
	
	public DataTargetSelectionDescriptor getTarget()
	{
		return (fTarget);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor#getBuffer()
	 */
	
	public BufferedDataSelectionDescriptor getBuffer()
	{
		return (fBuffer);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor#getParse()
	 */
	
	public DataParseDescriptor getParse()
	{
		return (fParse);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor#getLog()
	 */
	
	public DataLogDescriptor getLog()
	{
		return (fLog);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor#getFormat()
	 */
	
	public DataFormatDescriptor getFormat()
	{
		return (fFormat);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor#isEnabled()
	 */
	
	public boolean isEnabled()
	{
		return (fEnabled);
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.gsfc.irc.data.transformation.description.DataTransformationDescriptor#publishesParseAsMessage()
	 */
	
	public boolean publishesParseAsMessage()
	{
		return (fPublishParseAsMessage);
	}
	

	/** 
	 *  Returns a String representation of this AbstractDataTransformerDescriptor.
	 *
	 *  @return A String representation of this AbstractDataTransformerDescriptor
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("DataTransformationDescriptor: ");
		
		result.append("\n" + super.toString());
		
		if (! fEnabled)
		{
			result.append("\n>Is disabled");
		}

		if (fSource != null)
		{
			result.append("\n>Source:" + fSource);
		}

		if (fTarget != null)
		{
			result.append("\n>Target:" + fTarget);
		}

		if (fParse != null)
		{
			result.append("\n>Parse: " + fParse);
		}

		if (fPublishParseAsMessage)
		{
			result.append("\n>Publishes parse as Message");
		}

		if (fLog != null)
		{
			result.append("\n>Log: " + fLog);
		}

		if (fFormat != null)
		{
			result.append("\n>Format: " + fFormat);
		}
		
		return (result.toString());
	}
	
	
	/**
	  * Unmarshalls this AbstractDataTransformerDescriptor from its associated JDOM 
	  * Element. 
	  *
	 **/
	 
	private void xmlUnmarshall()
	{
		// Unmarshall the enabled attribute (if any).
		fEnabled =  fSerializer.loadBooleanAttribute
			(Datatransml.A_ENABLED, true, fElement);
		
		// Unmarshall the publishParseAsMessage attribute (if any).
		fPublishParseAsMessage =  fSerializer.loadBooleanAttribute
			(Datatransml.A_PUBLISH_PARSE_AS_MESSAGE, false, fElement);
		
		// Unmarshall the source DataSelectionDescriptor (if any).
		fSource = (DataSourceSelectionDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Datatransml.E_SOURCE, Datatransml.C_SOURCE, fElement, 
					this, fDirectory);
		
		// Unmarshall the target DataSelectionDescriptor (if any).
		fTarget = (DataTargetSelectionDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Datatransml.E_TARGET, Datatransml.C_TARGET, fElement, 
					this, fDirectory);
		
		// Unmarshall the buffer DataSelectionDescriptor (if any).
		fBuffer = (BufferedDataSelectionDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Datatransml.E_BUFFER, Datatransml.C_BUFFER, fElement, 
					this, fDirectory);
		
		// Unmarshall the DataParserDescriptor (if any).
		fParse = (DataParseDescriptor) 
			fSerializer.loadSingleChildDescriptorElement(Datatransml.E_PARSE, 
				Datatransml.C_PARSE, fElement, this, fDirectory);

		// Unmarshall the DataLogDescriptor (if any).
		fLog = (DataLogDescriptor) 
			fSerializer.loadSingleChildDescriptorElement(Datatransml.E_LOG, 
				Datatransml.C_LOG, fElement, this, fDirectory);
		
		// Unmarshall the DataFormatDescriptor (if any).
		fFormat = (DataFormatDescriptor) 
			fSerializer.loadSingleChildDescriptorElement
				(Datatransml.E_FORMAT, Datatransml.C_FORMAT, 
					fElement, this, fDirectory);
	}
}
