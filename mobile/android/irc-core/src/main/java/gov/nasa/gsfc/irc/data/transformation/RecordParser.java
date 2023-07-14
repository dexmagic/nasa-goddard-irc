//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: RecordParser.java,v $
//  Revision 1.8  2006/06/26 20:26:30  chostetter_cvs
//  Fixed parsing error when parsing indeterminate number of fields and field separator regex also matches record terminator regex
//
//  Revision 1.7  2006/05/03 23:20:16  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.6  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.5  2006/02/02 17:04:50  chostetter_cvs
//  Added support for Sequencer in Records
//
//  Revision 1.4  2006/01/25 20:06:04  chostetter_cvs
//  Changed scheme for formatting and parsing arbitrary-lengthsequences of uniform field types
//
//  Revision 1.3  2006/01/25 17:02:23  chostetter_cvs
//  Support for arbitrary-length Message parsing
//
//  Revision 1.2  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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

package gov.nasa.gsfc.irc.data.transformation;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.gsfc.irc.data.selection.DataSelector;
import gov.nasa.gsfc.irc.data.transformation.description.DataParseDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.FieldParserDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.RecordParserDescriptor;


/**
 * A RecordParser parses data according to a record structure.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/06/26 20:26:30 $ 
 * @author Carl F. Hostetter   
**/

public class RecordParser extends AbstractDataParser
{
	private RecordParserDescriptor fDescriptor;
	
	private DataSelector fSource;
	
	private DataParser fSequencer;
	private DataParser fSequencerSeparator;
	private DataParser fInitiator;
	private DataParser fInitiatorSeparator;
	private List fFields = new ArrayList();
	private DataParser fTerminator;
	
	private boolean fUsesInitiatorAsParseName = false;
	
	private Map fLastParse = new LinkedHashMap();
	private Map fTempParse = new LinkedHashMap();
	
	
	/**
	 * Default constructor of a new RecordParser.
	 *
	**/
	
	public RecordParser()
	{
		
	}
	

	/**
	 * Constructs a new RecordParser and configures it in accordance with 
	 * the given RecordParserDescriptor.
	 *
	 * @param descriptor The RecordParserDescriptor according to which to 
	 * 		configure the new RecordParser
	**/
	
	public RecordParser(RecordParserDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 * Configures this RecordParser in accordance with its current 
	 * RecordParserDescriptor.
	**/
	
	private void configureFromDescriptor()
	{
		fSource = getSource();
		
		fUsesInitiatorAsParseName = fDescriptor.usesInitiatorAsParseName();
		
		DataParseDescriptor sequencerDescriptor = fDescriptor.getSequencer();
		
		if (sequencerDescriptor != null)
		{
			fSequencer = sDataParserFactory.getDataParser(sequencerDescriptor);
		}
		
		DataParseDescriptor sequencerSeparatorDescriptor = 
			fDescriptor.getSequencerSeparator();
		
		if (sequencerSeparatorDescriptor != null)
		{
			fSequencerSeparator = 
				sDataParserFactory.getDataParser(sequencerSeparatorDescriptor);
		}
		
		DataParseDescriptor initiatorDescriptor = fDescriptor.getInitiator();
		
		if (initiatorDescriptor != null)
		{
			fInitiator = sDataParserFactory.getDataParser(initiatorDescriptor);
		}
		
		DataParseDescriptor initiatorSeparatorDescriptor = 
			fDescriptor.getInitiatorSeparator();
		
		if (initiatorSeparatorDescriptor != null)
		{
			fInitiatorSeparator = 
				sDataParserFactory.getDataParser(initiatorSeparatorDescriptor);
		}
		
		Iterator fields = fDescriptor.getFields().iterator();

		while (fields.hasNext())
		{
			FieldParserDescriptor fieldDescriptor = 
				(FieldParserDescriptor) fields.next();
			
			FieldParser field = (FieldParser) 
				sDataParserFactory.getDataParser(fieldDescriptor);
			
			fFields.add(field);
		}
		
		DataParseDescriptor terminatorDescriptor = fDescriptor.getTerminator();
		
		if (terminatorDescriptor != null)
		{
			fTerminator = 
				sDataParserFactory.getDataParser(terminatorDescriptor);
		}
	}
	

	/**
	 * Causes this RecordParser to parse the given data Object into a Map as  
	 * specified by its associated RecordParserDescriptor and then return the 
	 * resulting Map.
	 *
	 * @param data The data Object to be parsed
	 * @param context An optional Map of contextual information
	 * @return The result of parsing the given data Object
	 * @throws UnsupportedOperationException if this RecordParser is unable to 
	 * 		parse the given data Object
	**/
	
	public Map parse(Object data, Map context)
	{
		fLastParse.clear();
		
		Object selectedData = data;
		
		if (data instanceof byte[])
		{
			selectedData = ByteBuffer.wrap((byte[]) data);
		}
		else if (data instanceof char[])
		{
			selectedData = CharBuffer.wrap((char[]) data);
		}
		
		if (fSource != null)
		{
			selectedData = fSource.select(selectedData);	
		}
		
		if (fSequencer != null)
		{
			fTempParse.clear();
			fTempParse = fSequencer.parse(selectedData, context);
			
			fLastParse.putAll(fTempParse);
		}
		
		if (fSequencerSeparator != null)
		{
			fTempParse.clear();
			fTempParse = fSequencerSeparator.parse(selectedData, context);
		}
		
		if (fInitiator != null)
		{
			fTempParse.clear();
			fTempParse = fInitiator.parse(selectedData, context);
			
			if (fUsesInitiatorAsParseName && (fTempParse != null) && 
				(fTempParse.size() > 0))
			{
				String name = (String) fTempParse.values().iterator().next();
				
				setName(name);
			}
			
			fLastParse.putAll(fTempParse);
		}
		
		if (fInitiatorSeparator != null)
		{
			fTempParse.clear();
			fTempParse = fInitiatorSeparator.parse(selectedData, context);
		}
		
		Iterator fields = fFields.iterator();
		boolean done = false;
		
		while (fields.hasNext() && ! done)
		{
			FieldParser field = (FieldParser) fields.next();
			
			if (field.applyToRemainingFields())
			{
				while (! done)
				{
					// Look ahead to see whether the next element in the 
					// selected data is the terminator; if so, we're done.
					
					if (fTerminator != null)
					{
						fTempParse.clear();
						fTempParse = fTerminator.parse(selectedData, context);
						
						if ((fTempParse != null) && (fTempParse.size() > 0))
						{
							done = true;
						}
					}
					
					// Otherwise, parse the next field.
					
					if (! done)
					{
						fTempParse.clear();
						fTempParse = field.parse(selectedData, context);
						
						if ((fTempParse != null) && (fTempParse.size() > 0))
						{
							fLastParse.putAll(fTempParse);
						}
						else
						{
							if (fTerminator != null)
							{
								fTempParse.clear();
								fTempParse = fTerminator.parse(selectedData, context);
							}
							
							done = true;
						}
					}
				}
			}
			else
			{
				fTempParse.clear();
				fTempParse = field.parse(selectedData, context);
				
				fLastParse.putAll(fTempParse);
				
				if (fTerminator != null)
				{
					fTempParse.clear();
					fTempParse = fTerminator.parse(selectedData, context);
				}
			}
		}
		
		return (fLastParse);
	}


	/** 
	 *  Returns a String representation of this RecordParser.
	 *
	 *  @return A String representation of this RecordParser
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		
		result.append("RecordParser: ");
		
		if (fDescriptor != null)
		{
			result.append("\nDescriptor: " + fDescriptor);
		}
		
		if (fUsesInitiatorAsParseName)
		{
			result.append("\nUses initiator as parse name");
		}
		
		return (result.toString());
	}
}
