//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//--- Development History  ---------------------------------------------------
//
//  $Log: SwitchedDataParser.java,v $
//  Revision 1.5  2006/05/03 23:20:16  chostetter_cvs
//  Redesigned to fix problems with input data buffering
//
//  Revision 1.4  2006/04/27 19:46:21  chostetter_cvs
//  Added support for list value parsing/formatting; string constant parsing; instanceof comparator
//
//  Revision 1.3  2005/09/14 19:05:53  chostetter_cvs
//  Added optional context Map to data transformation operations
//
//  Revision 1.2  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import gov.nasa.gsfc.irc.data.state.DataStateDeterminer;
import gov.nasa.gsfc.irc.data.state.DataStateDeterminerDescriptor;
import gov.nasa.gsfc.irc.data.state.DataStateDeterminerFactory;
import gov.nasa.gsfc.irc.data.state.DefaultDataStateDeterminerFactory;
import gov.nasa.gsfc.irc.data.transformation.description.DataParseDescriptor;
import gov.nasa.gsfc.irc.data.transformation.description.SwitchedParserDescriptor;


/**
 * A SwitchedDataParser selects among a set of switched parse cases.
 *
 *<p>This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/05/03 23:20:16 $ 
 * @author Carl F. Hostetter   
**/

public class SwitchedDataParser extends AbstractDataParser
{
	private static DataStateDeterminerFactory sDataStateDeterminerFactory = 
		DefaultDataStateDeterminerFactory.getInstance();
	
	private SwitchedParserDescriptor fDescriptor;
	
	private DataStateDeterminer fCaseDeterminer;
	private Map fCasesByName = new LinkedHashMap();
	
	
	/**
	 * Default constructor of a new SwitchedDataParser.
	 *
	**/
	
	public SwitchedDataParser()
	{
		
	}
	

	/**
	 * Constructs a new SwitchedDataParser and configures it in accordance with 
	 * the given SwitchedParserDescriptor.
	 *
	 * @param descriptor The SwitchedParserDescriptor according to which to 
	 * 		configure the new SwitchedDataParser
	**/
	
	public SwitchedDataParser(SwitchedParserDescriptor descriptor)
	{
		super(descriptor);
		
		setDescriptor(descriptor);
	}
	

	/**
	 * Configures this SwitchedDataParser in accordance with its current 
	 * SwitchedParserDescriptor.
	 *
	**/
	
	public void setDescriptor(SwitchedParserDescriptor descriptor)
	{
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	
	
	/**
	 * Configures this SwitchedDataParser in accordance with its current 
	 * SwitchedParserDescriptor.
	 *
	**/
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			DataStateDeterminerDescriptor determiner = fDescriptor.getCaseSelector();
			
			if (determiner != null)
			{
				fCaseDeterminer = sDataStateDeterminerFactory.
					getDataStateDeterminer(determiner);
			}
			
			fCasesByName.clear();
			
			Map parseCasesByName = fDescriptor.getParseCases();
			
			Iterator cases = parseCasesByName.entrySet().iterator();
			
			while (cases.hasNext())
			{
				Map.Entry caseEntry = (Map.Entry) cases.next();
				
				String name = (String) caseEntry.getKey();
				
				DataParseDescriptor caseDescriptor = 
					(DataParseDescriptor) caseEntry.getValue();
				
				DataParser parser = 
					sDataParserFactory.getDataParser(caseDescriptor);
				
				fCasesByName.put(name, parser);
			}
		}
	}
	
	
	/**
	 * Causes this SwitchedDataParser to parse the given data Object into a Map as  
	 * specified by its associated SwitchedParserDescriptor and then return the 
	 * resulting Map.
	 *
	 * @param data The data Object to be parsed
	 * @param context An optional Map of contextual information
	 * @return The result of parsing the given data Object
	 * @throws UnsupportedOperationException if this SwitchedDataParser is unable to 
	 * 		parse the given data Object
	**/
	
	public Map parse(Object data, Map context)
	{
		Map result = null;
		
		String caseName = fCaseDeterminer.determineDataState(data);
		
		if (caseName != null)
		{
			DataParser parser = (DataParser) fCasesByName.get(caseName);
			
			result = parser.parse(data, context);
		}
		
		return (result);
	}
	
	
	/** 
	 *  Returns a String representation of this DataStreamParser.
	 *
	 *  @return A String representation of this DataStreamParser
	**/
	
	public String toString()
	{
		StringBuffer result = new StringBuffer("SwitchedDataParser: ");
		result.append("\nDescriptor: " + getDescriptor());
		
		return (result.toString());
	}
}
