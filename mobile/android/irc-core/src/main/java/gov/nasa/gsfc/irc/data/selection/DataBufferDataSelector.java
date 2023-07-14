//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/DataBufferDataSelector.java,v 1.2 2005/09/30 20:55:48 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: DataBufferDataSelector.java,v $
//  Revision 1.2  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
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

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataBuffer;
import gov.nasa.gsfc.irc.data.selection.description.DataBufferDataSelectorDescriptor;


/**
 * A DataBufferDataSelector selects a DataBuffer from a BasisSet.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/30 20:55:48 $
 * @author Carl F. Hostetter
 */

public class DataBufferDataSelector extends NamedDataSelector
{
	private static final String CLASS_NAME = DataBufferDataSelector.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private String fDataBufferName;
	
	
	/**
	 * Constructs a new DataBufferDataSelector that will select the DataBuffer 
	 * indicated by the given DataBufferDataSelectorDescriptor.
	 *
	 * @param descriptor A DataBufferDataSelectorDescriptor
	 * @return A new DataBufferDataSelector that will select the DataBuffer 
	 * 		indicated by the given DataBufferDataSelectorDescriptor		
	**/
	
	public DataBufferDataSelector(DataBufferDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fDataBufferName = getName();
	}
	

	/**
	 *  Returns the DataBuffer selected by this DataBufferDataSelector.
	 *  
	 *  @return The DataBuffer selected by this DataBufferDataSelector
	 */
	
	public Object select(Object data)
	{
		DataBuffer result = null;
		
		if (data != null)
		{
			if (data instanceof BasisSet)
			{
				BasisSet dataSet = (BasisSet) data;
				
				result = dataSet.getDataBuffer(fDataBufferName);
				
				if (result == null)
				{
					DataBuffer basisBuffer = dataSet.getBasisBuffer();
					
					String basisBufferName = basisBuffer.getName();
					
					if (basisBufferName.equals(fDataBufferName))
					{
						result = basisBuffer;
					}
				}
				
				if (result == null)
				{
					if (sLogger.isLoggable(Level.WARNING))
					{
						String message = "No DataBuffer is named " + fDataBufferName + 
							"; applying regex pattern...";
						
						sLogger.logp(Level.WARNING, CLASS_NAME, "select", message);
					}
					
					Set matchingNames = dataSet.getDataBufferNames(fDataBufferName);
					
					if (matchingNames.size() == 1)
					{
						String matchingName = (String) 
							matchingNames.iterator().next();
						
						result = dataSet.getDataBuffer(matchingName);
						
						if (sLogger.isLoggable(Level.INFO))
						{
							String message = "Matched DataBuffer " + result;
							
							sLogger.logp(Level.INFO, CLASS_NAME, "select", message);
						}
						
					}
					else if (matchingNames.size() > 1)
					{
						if (sLogger.isLoggable(Level.SEVERE))
						{
							String message = "More than one DataBuffer contains " + 
							fDataBufferName + " in its name";
							
							sLogger.logp(Level.SEVERE, CLASS_NAME, "select", message);
						}
					}
					else if (matchingNames.size() == 0)
					{
						if (sLogger.isLoggable(Level.SEVERE))
						{
							String message = "No DataBuffer contains " + 
								fDataBufferName + " in its name";
							
							sLogger.logp(Level.SEVERE, CLASS_NAME, "select", message);
						}
					}
				}
			}
		}
		else
		{
			super.select(data);
		}
		
		return (result);
	}
}
