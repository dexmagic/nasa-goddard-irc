//=== File Prolog ============================================================
//
//  $Header: /cvs/IRC/Dev/source/core/gov/nasa/gsfc/irc/data/description/BasisBundleDescriptor.java,v 1.11 2006/05/23 15:59:24 smaher_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.data.description;

import gov.nasa.gsfc.commons.properties.beans.A;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.jdom.Element;


/**
 * A BasisBundleDescriptor is a DataBundleDescriptor that is associated with 
 * exactly one other DataBufferDescriptor describing a basis DataBuffer, the 
 * whole describing a structured set of potentially heterogeneous DataBuffers 
 * that are all correlated with the described BasisBuffer.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/23 15:59:24 $
 * @author Carl F. Hostetter
 */

public class BasisBundleDescriptor extends DataBundleDescriptor
{
	private int fSize = -1;
	private DataBufferDescriptor fBasisBufferDescriptor;
	
	
	/**
	 * Constructs a new BasisBundleDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of this BasisBundleDescriptor (if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		BasisBundleDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		BasisBundleDescriptor		
	**/
	
	public BasisBundleDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		xmlUnmarshall();
	}
	

	/**
	 *	Constructs a BasisBundleDescriptor describing a BasisBundle having 
	 *  the given name, a BasisBuffer described by the given 
	 *  DataBufferDescriptor, and a Set of correlated data units described by 
	 *  the given Set of DataDescriptors.
	 *
	 *  @param name The name of the new BasisBundleDescriptor
	 *  @param basisBufferDescriptor A DataBufferDescriptor
	 *  @param dataDescriptors A Set of DataDescriptors
	 */
	
	public BasisBundleDescriptor(String name, 
		DataBufferDescriptor basisBufferDescriptor, Set dataDescriptors)
	{
		super(name, dataDescriptors);
		
		fBasisBufferDescriptor = basisBufferDescriptor;
	}
	
	
	/**
	 *	Constructs a BasisBundleDescriptor describing a BasisBundle having 
	 *  the given name, and a BasisBuffer described by the given 
	 *  DataBufferDescriptor.
	 *
	 *  @param name The name of the new BasisBundleDescriptor
	 *  @param basisBufferDescriptor A DataBufferDescriptor
	 */
	
	public BasisBundleDescriptor(String name, 
		DataBufferDescriptor basisBufferDescriptor)
	{
		this(name, basisBufferDescriptor, null);
	}
	
	
	/**
	 *	Returns a modifiable copy of this BasisBufferDescriptor.
	 *
	 *  @return A modifiable copy of this BasisBufferDescriptor
	 */
	
	public DataElementDescriptor getModifiableCopy()
	{
		return (new ModifiableBasisBundleDescriptor(this));
	}	
	

	/**
	 * Returns a filtered version of this BasisBundleDescriptor, 
	 * according to the given subset filter, if any. 
	 * 
	 * @param subset A Set of data buffer names (fully qualified or not)
	 * @return a new filtered BasisBundleDescriptor or this.
	 */
	public BasisBundleDescriptor filterDescriptor(Set subset)
	{
		BasisBundleDescriptor result = this;
		
		if (subset != null)
		{
			ModifiableBasisBundleDescriptor newDescriptor = 
				(ModifiableBasisBundleDescriptor) 
					this.getModifiableCopy();
			
			Iterator dataBufferDescriptors = 
				newDescriptor.getDataBufferDescriptors().iterator();
			
			while (dataBufferDescriptors.hasNext())
			{
				DataBufferDescriptor dataBufferDescriptor = 
					(DataBufferDescriptor) dataBufferDescriptors.next();
				
				String descriptorName = 
					dataBufferDescriptor.getFullyQualifiedName();
				
				
				if (! subset.contains(descriptorName))
				{
					newDescriptor.removeDataEntryDescriptor
						(dataBufferDescriptor);
				}
			}
			
			result = newDescriptor;
		}
		
		return (result);
	}

	
	/**
	 * Created a BasisBundleDescriptor that is the same as a source BasisBundle
	 * but has only those data buffers included in the given set of data
	 * buffers.
	 * <p>
	 * <em>Notes from Steve Maher on why this method was implemented</em>
	 * <p>
	 * I tried using BasisBundleDescriptor.filterDescriptor(), but at first it
	 * required fully qualified names for data buffers when checking for
	 * matching names, which the rest of the data buffer filtering system
	 * doesn't seem to use. When I loosened the check to include non-FQ names,
	 * ModifiableBasisBundleDescriptor.removeDataEntryDescriptor() failed to
	 * remove filtered descriptors because the descriptor to remove has a
	 * "valid" "memberid"? (in my case "SensorData") whereas ModifeableBasisBundleDescriptors
	 * memberid? was "Anonymous". Since they didn't match, the descriptor wasn't
	 * deleted and the filterDescriptor method failed. Therefore I added a
	 * filterDescriptor2, which is modelled after the existing filterIn and
	 * filterOut methods and does what I need correctly to propagate filtered
	 * basis sets.
	 * 
	 * @param dataBufferNames
	 *            Set of (non-qualified) data buffer names or delimited regular expressions (e.g., {DAC \[.*,0\]})
	 * @return BasisBundleDescriptor with filtered data buffers
	 */
	public  BasisBundleDescriptor filterDescriptor2(Set dataBufferNames)
	{
		// Default to returning a reference to this descriptor (e.g., if 
		// there is no filtering)
		BasisBundleDescriptor result = this;

		if ((dataBufferNames != null) && (dataBufferNames.size() > 0))
		{
			dataBufferNames = new HashSet(dataBufferNames);			
			Set regexes = new HashSet();
			Set standardBufferNames = new HashSet();			
			
			//
			// Build sets of "normal" and regex data buffer names
			//
			for (Iterator iter = dataBufferNames.iterator(); iter.hasNext();)
			{
				String rawDataBufferName = (String) iter.next();
				if (rawDataBufferName.startsWith(BasisRequest.REGEX_DELIMETER_START) == true &&
						rawDataBufferName.endsWith(BasisRequest.REGEX_DELIMETER_END) == true)
				{
					Pattern pattern = Pattern.compile(rawDataBufferName.substring(1, rawDataBufferName.length() - 1));					
					regexes.add(new DataBufferRegex(pattern));
				}
				else
				{
					standardBufferNames.add(rawDataBufferName);
				}
			}
			
			
			// This is a defensive copy of the basis bundle's set,
			// so we can treat it as our own.
			Set newDataBufferDescriptors = 
				getDataBufferDescriptors();
			
			
			Iterator newDataBufferDescriptorsIter = newDataBufferDescriptors.iterator();
			
			while (newDataBufferDescriptorsIter.hasNext())
			{
				DataBufferDescriptor descriptor = (DataBufferDescriptor) 
					newDataBufferDescriptorsIter.next();
				
				boolean keep = false;
				String bufferNameInMaster = descriptor.getName();
				if (standardBufferNames.contains(bufferNameInMaster) == false)
				{
					keep = false; // unless we find it in regexes below
					for (Iterator iter = regexes.iterator(); iter.hasNext();)
					{
						DataBufferRegex bufferRegex = (DataBufferRegex) iter.next();
						if (bufferRegex.fPattern.matcher(bufferNameInMaster).lookingAt())
						{
							keep = true;
							bufferRegex.fUsed = true;
							break;
						}
					}
				}
				else
				{
					// Found with simple name
					keep = true;
					
					// Remove name so we can check for extras at the end
					standardBufferNames.remove(descriptor.getName());					
				}

				if (keep == false)
				{
					// Remove descriptor from the result
					newDataBufferDescriptorsIter.remove();					
				}
			}

			//
			// Check for unused standard names
			//
			if (standardBufferNames.size() > 0)
			{
				throw new IllegalArgumentException("Data buffer(s) were not found in basis bundle: " + dataBufferNames);
			}
			
			//
			// Check for unused regexes
			Set unusedRegexes = new HashSet();
			for (Iterator iter = regexes.iterator(); iter
					.hasNext();)
			{
				DataBufferRegex regex = (DataBufferRegex) iter.next();
				if (regex.fUsed == false)
				{
					unusedRegexes.add(regex.fPattern.toString());
				}
			}
			
			if (unusedRegexes.size() > 0)
			{
				throw new IllegalArgumentException("Data buffer(s) using regular expressions were not found in basis bundle: " + unusedRegexes);
			}			
			
			
			
			
			result = new BasisBundleDescriptor
				(getName(), getBasisBufferDescriptor(),	newDataBufferDescriptors);
		}
		
		return result;
	}
	
	
	/**
	 *	Sets the DataBufferDescriptor of the basis Buffer of this 
	 *  BasisBundleDescriptor to the given DataBufferDescriptor
	 *
	 *  @param descriptor A DataBufferDescriptor
	 */
	
	protected void setBasisBufferDescriptor(DataBufferDescriptor descriptor)
	{
		fBasisBufferDescriptor = descriptor;
	}


	/**
	 *	Returns the DataBufferDescriptor of the basis Buffer of this 
	 *  BasisBundleDescriptor.
	 *
	 *  @return The DataBufferDescriptor of the basis Buffer of this 
	 *  		BasisBundleDescriptor
	 */
	
	public DataBufferDescriptor getBasisBufferDescriptor()
	{
		return (fBasisBufferDescriptor);
	}


	/**
	 *	Sets the (uniform) size of the DataBuffers of this BasisBundleDescriptor to 
	 *  the given size.
	 *
	 *  @param size The (uniform) size of the DataBuffers of this 
	 *  		BasisBundleDescriptor
	 */
	
	protected void setSize(int size)
	{
		fSize = size;
	}


	/**
	 *	Returns the (uniform) size of the DataBuffers BasisBundleDescriptor.
	 *
	 *  @return The (uniform) size of the DataBuffers of this BasisBundleDescriptor
	 */
	
	public int getSize()
	{
		return (fSize);
	}


	/**
	 *	Returns a String representation of this BasisBundleDescriptor.
	 *
	 *  @return A String representation of this BasisBundleDescriptor
	 */
	
	public String toString()
	{
		StringBuffer stringRep = 
			new StringBuffer("BasisBundleDescriptor Name: " + getName() + "\n");
		
		stringRep.append("BasisBufferDescriptor: " + fBasisBufferDescriptor + "\n");
		
		stringRep.append(super.toString());
		
		return (stringRep.toString());
	}


	/**
	 * Unmarshalls a BasisBundleDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Load the size attribute
		fSize = fSerializer.loadIntAttribute(Dataml.A_SIZE, -1, fElement);
		
		// Load the BasisBuffer
		fBasisBufferDescriptor = (DataBufferDescriptor) 
			fSerializer.loadSingleChildDescriptorElement(Dataml.E_BASIS_BUFFER,
				Dataml.C_DATA_BUFFER, fElement, this, fDirectory);
	}
	
	/**
	 * Simple struct class to retain a compiled regular expression and a boolean
	 * specifying whether the regular expression was used.
	 *
	 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
	 * for the Instrument Remote Control (IRC) project.
	 * 
	 * @version	$Id: BasisBundleDescriptor.java,v 1.11 2006/05/23 15:59:24 smaher_cvs Exp $
	 * @author	$Author: smaher_cvs $
	 */
	private static class DataBufferRegex
	{
		private Pattern fPattern;
		private boolean fUsed;

		/**
		 * @param pattern
		 */
		public DataBufferRegex(Pattern pattern)
		{
			fPattern = pattern;
		}
	}	
}

//--- Development History  ---------------------------------------------------
//
//  $Log: BasisBundleDescriptor.java,v $
//  Revision 1.11  2006/05/23 15:59:24  smaher_cvs
//  Added ability to specify data buffer names using regular expressions.
//
//  Revision 1.10  2006/05/18 13:37:25  smaher_cvs
//  Fixed bug in filterDescriptor2() which failed when the dataSetNames set was null.
//
//  Revision 1.9  2006/05/17 15:29:19  smaher_cvs
//  Added filterDescriptor2() to get filtered basis sets to work.
//
//  Revision 1.8  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.7  2005/09/09 21:39:44  tames
//  Added filterDescriptor method.
//
//  Revision 1.6  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
//
//  Revision 1.5  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.4  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
//  Revision 1.3  2004/09/14 16:02:22  chostetter_cvs
//  Fixed DataBundleDescriptor ordering problem
//
//  Revision 1.2  2004/09/11 19:22:44  chostetter_cvs
//  BasisBundle creation from XML now works, except for DataBuffer units
//
//  Revision 1.1  2004/09/02 19:39:57  chostetter_cvs
//  Initial data-description redesign work
//
//  Revision 1.13  2004/07/21 14:26:14  chostetter_cvs
//  Various architectural and event-passing revisions
//
//  Revision 1.12  2004/07/18 05:14:02  chostetter_cvs
//  Refactoring of data classes
//
//  Revision 1.11  2004/07/17 18:39:02  chostetter_cvs
//  Name, descriptor modification work
//
//  Revision 1.10  2004/07/15 05:44:38  chostetter_cvs
//  Mods to determining new BasisSet structure
//
//  Revision 1.9  2004/07/14 03:09:55  chostetter_cvs
//  More data, Algorithm work
//
//  Revision 1.8  2004/07/12 14:26:23  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.7  2004/07/06 13:40:00  chostetter_cvs
//  Commons package restructuring
//
//  Revision 1.6  2004/06/05 06:49:20  chostetter_cvs
//  Debugged BasisBundle stuff. It works!
//
//  Revision 1.5  2004/05/29 03:07:35  chostetter_cvs
//  Organized imports
//
//  Revision 1.4  2004/05/28 05:58:19  chostetter_cvs
//  More Namespace, DataSpace, Descriptor worl
//
//  Revision 1.3  2004/05/17 22:01:10  chostetter_cvs
//  Further data-related work
//
