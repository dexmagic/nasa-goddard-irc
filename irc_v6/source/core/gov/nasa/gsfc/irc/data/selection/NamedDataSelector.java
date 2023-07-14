//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/selection/NamedDataSelector.java,v 1.2 2006/07/18 19:56:38 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: NamedDataSelector.java,v $
//  Revision 1.2  2006/07/18 19:56:38  chostetter_cvs
//  Handles non-valid-reg-ex names
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

import java.util.regex.Pattern;

import gov.nasa.gsfc.commons.types.namespaces.HasName;
import gov.nasa.gsfc.irc.data.selection.description.NamedDataSelectorDescriptor;


/**
 * A NamedDataSelector selects a named data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/07/18 19:56:38 $
 * @author Carl F. Hostetter
 */

public class NamedDataSelector extends AbstractDataSelector
{
	private String fName;
	private Pattern fPattern;
	
	
	/**
	 * Constructs a new NamedDataSelector that will perform the data name 
	 * selection indicated by the given NamedDataSelectorDescriptor.
	 *
	 * @param descriptor A NamedDataSelectorDescriptor
	 * @return A new NamedDataSelector that will perform the data range 
	 * 		selection indicated by the given NamedDataSelectorDescriptor		
	**/
	
	public NamedDataSelector(NamedDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fName = descriptor.getName();
		
		if (fName != null)
		{
			try
			{
				fPattern = Pattern.compile(fName);
			}
			catch (Exception ex)
			{
				// Given name is not a valid reg-ex, skip.
			}
		}
	}
	

	/**
	 *  Returns the name associated with this NamedDataSelector.
	 *  
	 *  @return The name associated with this NamedDataSelector
	 */
	
	public String getName()
	{
		return (fName);
	}
	
	
	/**
	 *  Returns the Pattern associated with this NamedDataSelector.
	 *  
	 *  @return The Pattern associated with this NamedDataSelector
	 */
	
	public Pattern getPattern()
	{
		return (fPattern);
	}
	
	
	/**
	 *  Returns the data selected by this DataSelector from the given data 
	 *  Object, or null if it selects nothing from the given data Object.
	 *  
	 *  @return The data selected by this DataSelector from the given data 
	 *  		Object, or null if it selects nothing from the given data Object
	 */
	
	public Object select(Object data)
	{
		Object result = null;
		
		if (data != null)
		{
			if (data instanceof HasName)
			{
				String dataName = ((HasName) data).getName();
				
				if (fPattern != null)
				{
					if (fPattern.matcher(dataName).matches())
					{
						result = data;
					}
				}
			}
			else
			{
				super.select(data);
			}
		}
		else
		{
			super.select(data);
		}
		
		return (result);
	}
}
