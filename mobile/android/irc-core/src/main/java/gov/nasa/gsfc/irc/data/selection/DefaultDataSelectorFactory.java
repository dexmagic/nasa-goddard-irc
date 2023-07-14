//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/DefaultDataSelectorFactory.java,v 1.8 2006/05/03 23:20:17 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DefaultDataSelectorFactory.java,v $
//  Revision 1.8  2006/05/03 23:20:17  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.7  2006/04/27 23:31:09  chostetter_cvs
//  Added support for field value selection in determinants
//
//  Revision 1.6  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.5  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.4  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
//
//  Revision 1.3  2005/09/15 15:34:08  chostetter_cvs
//  Added support for command counter value selection
//
//  Revision 1.2  2005/09/14 18:03:11  chostetter_cvs
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

package gov.nasa.gsfc.irc.data.selection;

import gov.nasa.gsfc.irc.data.selection.description.BasisSetDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.BufferedDataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.ByteDelimitedDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.ByteRangeDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.CharDelimitedDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.CharRangeDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.ClassSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.CounterDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataBufferDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataConcatenationDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataNameSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectionDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.DataValueSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.KeyedDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.NamedDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.NumTokensSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.RegExPatternDataSelectorDescriptor;
import gov.nasa.gsfc.irc.data.selection.description.StringConstantValueSelectorDescriptor;
import gov.nasa.gsfc.irc.description.xml.AbstractIrcElementFactory;
import gov.nasa.gsfc.irc.description.xml.IrcElementDescriptor;


/**
 * A DataSelectorFactory creates and returns instances of Selectors.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:17 $
 * @author Carl F. Hostetter
 */

public class DefaultDataSelectorFactory extends AbstractIrcElementFactory 
	implements DataSelectorFactory
{
	private static DataSelectorFactory fFactory;
	
	
	/**
	 *  Creates and returns a DataSelector appropriate to the given 
	 *  DataSelectorDescriptor.
	 *  
	 *  @param A DataSelectorDescriptor describing the desired selection scheme
	 *  @return A DataSelector appropriate to the given 
	 *  		DataSelectorDescriptor
	 */
	
	protected DefaultDataSelectorFactory()
	{
		
	}
	
	
	/**
	 *  Returns the singleton instance of a DefaultDataSelectorFactory.
	 *  
	 *  @return The singleton instance of a DefaultDataSelectorFactory
	 */
	
	public static DataSelectorFactory getInstance()
	{
		if (fFactory == null)
		{
			fFactory = new DefaultDataSelectorFactory();
		}
		
		return (fFactory);
	}
	
	
	/**
	 *  Creates and returns a DataSelector appropriate to the given 
	 *  DataSelectorDescriptor.
	 *  
	 *  @param A DataSelectorDescriptor describing the desired data selector
	 *  @return A DataSelector appropriate to the given 
	 *  		DataSelectorDescriptor
	 */
	
	public DataSelector getDataSelector(DataSelectorDescriptor descriptor)
	{
		DataSelector selector = null;
		
		if (descriptor != null)
		{
			if (descriptor instanceof IrcElementDescriptor)
			{
				selector = (DataSelector) super.getIrcElement
					((IrcElementDescriptor) descriptor);
			}
			
			if (selector == null)
			{
				if (descriptor instanceof BasisSetDataSelectorDescriptor)
				{
					selector = new BasisSetDataSelector
						((BasisSetDataSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof DataBufferDataSelectorDescriptor)
				{
					selector = new DataBufferDataSelector
						((DataBufferDataSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof StringConstantValueSelectorDescriptor)
				{
					selector = new StringConstantValueSelector
						((StringConstantValueSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof ByteDelimitedDataSelectorDescriptor)
				{
					selector = new ByteDelimitedDataSelector
						((ByteDelimitedDataSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof CharDelimitedDataSelectorDescriptor)
				{
					selector = new CharDelimitedDataSelector
						((CharDelimitedDataSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof ByteRangeDataSelectorDescriptor)
				{
					selector = new ByteRangeDataSelector
						((ByteRangeDataSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof CharRangeDataSelectorDescriptor)
				{
					selector = new CharRangeDataSelector
						((CharRangeDataSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof DataNameSelectorDescriptor)
				{
					selector = new DataNameSelector
						((DataNameSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof DataValueSelectorDescriptor)
				{
					selector = new DataValueSelector
						((DataValueSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof ClassSelectorDescriptor)
				{
					selector = new ClassSelector
						((ClassSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof NumTokensSelectorDescriptor)
				{
					selector = new NumTokensSelector
						((NumTokensSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof NamedDataSelectorDescriptor)
				{
					selector = new NamedDataSelector
						((NamedDataSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof KeyedDataSelectorDescriptor)
				{
					selector = new KeyedDataSelector
						((KeyedDataSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof RegExPatternDataSelectorDescriptor)
				{
					selector = new RegExPatternDataSelector
						((RegExPatternDataSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof CounterDataSelectorDescriptor)
				{
					selector = new CounterDataSelector
						((CounterDataSelectorDescriptor) descriptor);
				}
				else if (descriptor instanceof DataConcatenationDescriptor)
				{
					selector = new ConcatenatingDataSelector
						((DataConcatenationDescriptor) descriptor);
				}
			}
		}
		
		return (selector);
	}
	
	
	/**
	 *  Creates and returns a DataSelector appropriate to the given 
	 *  DataSelectionDescriptor.
	 *  
	 *  @param A DataSelectionDescriptor describing the desired selection scheme
	 *  @return A DataSelector appropriate to the given DataSelectionDescriptor
	 */
	
	public DataSelector getDataSelector
		(DataSelectionDescriptor selection)
	{
		DataSelector selector = null;
		
		if (selection != null)
		{
			DataSelectorDescriptor descriptor = selection.getDataSelector();
			
			selector = getDataSelector(descriptor);

			if (selection instanceof BufferedDataSelectionDescriptor)
			{
				int bufferSize = 
					((BufferedDataSelectionDescriptor) selection).getSize();
				
				selector = 
					new DefaultBufferedDataSelector(selector, bufferSize);
			}
		}
		
		return (selector);
	}
}
