//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/description/PixelBundleDescriptor.java,v 1.9 2004/11/15 19:10:28 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: PixelBundleDescriptor.java,v $
//  Revision 1.9  2004/11/15 19:10:28  chostetter_cvs
//  Fixed log tag, incorporated Steve's changes
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

package gov.nasa.gsfc.irc.data.description;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import gov.nasa.gsfc.commons.numerics.types.Pixel;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A PixelBundleDescriptor describes a homegenous Set of DataBuffers differing 
 * only in the permutation of values of an accompanying Pixel identifier.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2004/11/15 19:10:28 $
 * @author Carl F. Hostetter
 */

public class PixelBundleDescriptor extends DataBufferBundleDescriptor
{
	private DataBufferDescriptor fDataBufferDescriptor;
	private Map fDimensionsByName;
	
	private int[] fSizes;
	private int[] fCurrentCoords;
	
	
	/**
	 * Constructs a new PixelBundleDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new PixelBundleDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		PixelBundleDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		PixelBundleDescriptor		
	**/
	
	public PixelBundleDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element);
		
		fDimensionsByName = new LinkedHashMap();
		
		xmlUnmarshall();
		
		init();
	}
	

	/**
	 *	Constructs a PixelBundleDescriptor describing a logical set of DataBuffers 
	 *  having the given name and varying in each of the dimensions specified by 
	 *  the given List of DimensionDescriptors only by the permutation of indices 
	 *  of an associated Pixel tag.
	 *
	 *  @param name The name of the new PixelBundleDescriptor
	 *  @param dimensions A List of DimensionsDescriptors describing the 
	 * 		dimensions of the new PixelBundleDescriptor
	 */
	
	public PixelBundleDescriptor(DataBufferDescriptor dataBufferDescriptor, 
		List dimensions)
	{
		super(dataBufferDescriptor.getName());
		
		fDimensionsByName = new LinkedHashMap();
		
		fDataBufferDescriptor = dataBufferDescriptor;
		
		Iterator dimensionsIterator = dimensions.iterator();
		
		while (dimensionsIterator.hasNext())
		{
			DimensionDescriptor dimension = (DimensionDescriptor) 
				dimensionsIterator.next();
			
			fDimensionsByName.put(dimension.getName(), dimension);
		}
		
		init();
	}
	
	
	/**
	 *	Initializes a new PixelBundle.
	 *
	 */
	
	private void init()
	{
		setName(fDataBufferDescriptor.getName());
		
		int numDimensions = fDimensionsByName.size();
		
		fSizes = new int[numDimensions];
		
		Iterator dimensions = fDimensionsByName.values().iterator();
		
		for (int i = 0; dimensions.hasNext(); i++)
		{
			DimensionDescriptor dimension = (DimensionDescriptor) 
				dimensions.next();
			
			int size = dimension.getSize();
			
			fSizes[i] = size;
		}
		
		fCurrentCoords = new int[numDimensions];
		
		generatePixels(0);
	}
	
	
    /**
     *  Generates a Pixel and corresponding DataBufferDescriptor for each position 
     *  in the Pixel space specified by this PixelBundleDescriptor.
     *
     *  @param currentDimensionIndex The dimension being generated
     */
    
    protected void generatePixels(int currentDimensionIndex)
    {
        for (int i = 0; i < fSizes[currentDimensionIndex]; i ++)
        {
            // Set the coordinate for this dimension.
        	
            fCurrentCoords[currentDimensionIndex] = i;

            // If we're not on the "last" dimension then
            // recurse to the next dimension.
            
            if (currentDimensionIndex < fSizes.length - 1)
            {
                generatePixels(currentDimensionIndex + 1);
            }
            else if (currentDimensionIndex == fSizes.length - 1)
            {
                // Only when we're on the "last" dimension - after
                // all the other dimension coordinates have been set -
                // do we allocate a pixel.  
                
	        		Pixel pixel = new Pixel(fCurrentCoords);
	        		
	        		DataBufferDescriptor dataBufferDescriptor = 
	        			new DataBufferDescriptor(fDataBufferDescriptor, pixel);
	        		
	        		addDataBufferDescriptor(dataBufferDescriptor);
	        	}           
        }
    }


	/**
	 *	Returns the List of DimensionDescriptors that describe the set of 
	 *  dimensions of this PixelBundleDescriptor.
	 *
	 */
	
	public Collection getDimensions()
	{
		return (Collections.unmodifiableCollection(fDimensionsByName.values()));
	}
	
	
	/**
	 *	Returns a String representation of this PixelBundleDescriptor.
	 *
	 *  @return A String representation of this PixelBundleDescriptor
	 */
	
	public String toString()
	{
		StringBuffer stringRep = 
			new StringBuffer("PixelBundleDescriptor Name: " + getName() + "\n");
		
		stringRep.append("DataBufferDescriptors: ");
		
		Iterator dataDescriptorIterator = getDataEntryDescriptors().iterator();
			
		if (dataDescriptorIterator.hasNext())
		{
			for (int i = 1; dataDescriptorIterator.hasNext(); i++)
			{
				DataBufferDescriptor descriptor = (DataBufferDescriptor) 
					dataDescriptorIterator.next();
				
				stringRep.append("\n" + i + ": " + descriptor);
			}
		}
		else
		{
			stringRep.append("none");
		}
		
		return (stringRep.toString());
	}

	
	/**
	 * Unmarshalls a PixelBundleDescriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		// Load the DataBuffer
		fDataBufferDescriptor = (DataBufferDescriptor) 
			fSerializer.loadSingleChildDescriptorElement(Dataml.E_DATA_BUFFER,
				Dataml.C_DATA_BUFFER, fElement, this, fDirectory);
		
		// Load the Dimensions
		fSerializer.loadChildDescriptorElements(Dataml.E_DIMENSION, 
			fDimensionsByName, Dataml.C_DIMENSION, fElement, this, fDirectory);
	}
}
