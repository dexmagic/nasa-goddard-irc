//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History:
//
//	$Log: Namespaces.java,v $
//	Revision 1.4  2006/03/14 21:26:11  chostetter_cvs
//	Fixed manager proxy issue that was preventing component browser from updating
//	
//	Revision 1.3  2006/03/07 23:32:42  chostetter_cvs
//	NamespaceMembers (Components, Algorithms, etc.) are no longer added to the global Namespace by default
//	
//	Revision 1.2  2006/03/05 16:30:16  tames
//	Fixed StringIndexOutofBoundsException in getSequenceNumber method.
//	
//	Revision 1.1  2006/01/23 17:59:50  chostetter_cvs
//	Massive Namespace-related changes
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

package gov.nasa.gsfc.commons.types.namespaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


/**
 *  Namespaces is a utility class offering static utility methods for and on 
 *  Namespaces.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/03/14 21:26:11 $
 *  @author Carl F. Hostetter
**/

public class Namespaces
{
	public static final String DEFAULT_GLOBAL_NAMESPACE_NAME = "Global Namespace";
	
	private static Namespace sGlobalNamespace;
	
	
	/**
	 *  Returns the (singleton) instance of the current application-global 
	 *  Namespace to which all Members utlimately belong.
	 *  
	 **/

	public static Namespace getGlobalNamespace()
	{
		if (sGlobalNamespace == null)
		{
			sGlobalNamespace = 
				new DefaultNamespace(DEFAULT_GLOBAL_NAMESPACE_NAME);
		}
		
		return (sGlobalNamespace);
	}
	
	
	/**
	 *  Returns the sequenced form of the given base name, according to the 
	 *  given sequence number (which must be greater than 0). Note that the 
	 *  sequenced form of a name whose sequence number is 1 is identical to its 
	 *  base (i.e. unsequenced) name.
	 *  
	 *  @param name A base name
	 *  @param sequenceNumber The desired sequence number
	 **/

	public static String formSequencedName(String name, int sequenceNumber)
	{
		String result = name;
		
		if (name != null && sequenceNumber > 1)
		{
			result += Namespace.S_SEQUENCE_NUMBER_SEPARATOR + sequenceNumber;
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the unified fully-qualified name form of the given (possibly 
	 *  sequenced) name joined with the given name qaulifier.
	 *  
	 *  @param name A (possibly sequenced) name
	 *  @param nameQualifier A name qualifier
	 **/

	public static String formFullyQualifiedName(String name, String nameQualifier)
	{
		String result = name;
		
		if (name != null && nameQualifier != null)
		{
			result += Namespace.S_FULLY_QUALIFIED_NAME_SEPARATOR + nameQualifier;
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the base name from the given (possibly sequenced, and/or possibly 
	 *  fully-qualified) name.
	 *  
	 *  @param name A possibly sequenced, and/or possibly fully-qualified name
	 *  @return The base name from the given (possibly sequenced, and/or possibly 
	 *  		fully-qualified) name
	 **/

	public static String getBaseName(String name)
	{
		String result = name;
		
		if (name != null)
		{
			result = getSequencedName(name);
			
			int sequenceStart = 
				result.indexOf(Namespace.S_SEQUENCE_NUMBER_SEPARATOR);
			
			if (sequenceStart > 0)
			{
				result = name.substring(0, sequenceStart);
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the sequenced name (which may be identical to the base name, if 
	 *  the fully-qualified name is not sequenced) from the given (possibly 
	 *  sequenced, and/or possibly fully-qualified) name
	 *  
	 *  @param name A possibly sequenced, and/or possibly fully-qualified name
	 *  @param nameQualifier A name qualifier
	 **/

	public static String getSequencedName(String name)
	{
		String result = name;
		
		if (name != null)
		{
			int startOfNameQualifier = 
				name.indexOf(Namespace.S_FULLY_QUALIFIED_NAME_SEPARATOR);
			
			if (startOfNameQualifier > -1)
			{
				result = name.substring(0, startOfNameQualifier);
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the sequence number, if any, in the given (possibly sequenced, 
	 *  possibly fully-qualified) name. If the given name is not sequenced, the 
	 *  result is -1.
	 *  
	 *  @param name A possibly sequenced, and/or possibly fully-qualified name
	 *  @return The sequence number of the name, if any
	 **/

	public static int getSequenceNumber(String name)
	{
		int result = -1;
		
		if (name != null)
		{
			int startOfSequenceSeparator = 
				name.indexOf(Namespace.S_SEQUENCE_NUMBER_SEPARATOR);
			
			if (startOfSequenceSeparator > -1)
			{
				int startOfSequenceNumber = startOfSequenceSeparator + 
					Namespace.S_SEQUENCE_NUMBER_SEPARATOR_LENGTH;
				
				String sequenceNumber = name.substring(startOfSequenceNumber);
				
				int endOfSequenceNumber = sequenceNumber.indexOf
					(Namespace.S_FULLY_QUALIFIED_NAME_SEPARATOR);
			
				if (endOfSequenceNumber > 0)
				{
					sequenceNumber = 
						sequenceNumber.substring(0, endOfSequenceNumber);
				}
				
				result = new Integer(sequenceNumber).intValue();
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns true if the given name is sequenced, false otherwise.
	 *  
	 *  @param name A name
	 *  @return True if the given name is sequenced, false otherwise
	 **/

	public static boolean isSequenced(String name)
	{
		boolean result = false;
		
		if (name != null)
		{
			result = 
				(name.indexOf(Namespace.S_SEQUENCE_NUMBER_SEPARATOR) > -1);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns true if the given name is qualified, false otherwise.
	 *  
	 *  @param name A possibly fully-qualified name
	 *  @return True if the given name is qualified, false otherwise
	 **/

	public static boolean isQualified(String name)
	{
		boolean result = false;
		
		if (name != null)
		{
			result = 
				(name.indexOf(Namespace.S_FULLY_QUALIFIED_NAME_SEPARATOR) > -1);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the name qualifier of the given fully-qualified name; i.e., the 
	 *  part of the name following and exclusive of the (possibly sequenced) base 
	 *  name.
	 *  
	 *  @param fullyQualifiedName A fully-qualified name
	 *  @return The name qualifier of the given fully-qualified name; i.e., the 
	 *  		part of the name following and exclusive of the (possibly sequenced) 
	 *  		base name
	 **/

	public static String getNameQualifier(String fullyQualifiedName)
	{
		String result = null;
		
		if (fullyQualifiedName != null)
		{
			int startOfQualifierSeparator = fullyQualifiedName.indexOf
				(Namespace.S_FULLY_QUALIFIED_NAME_SEPARATOR);
			
			if (startOfQualifierSeparator > 0)
			{
				int startOfNameQualifier = startOfQualifierSeparator + 
					Namespace.S_FULLY_QUALIFIED_NAME_SEPARATOR_LENGTH;
			
				result = fullyQualifiedName.substring(startOfNameQualifier);
			}
		}
		
		return (result);
	}
	
	
	/**
	 *  Tokenizes the given (possibly sequenced, and/or possibly fully-qualified) 
	 *  name and returns an ordered List of the tokens. Note that if the given name 
	 *  is sequenced, the first token in the return list will be the sequenced name.
	 *  
	 *  @param name A possibly sequenced, and/or possibly fully-qualified name
	 *  @return An ordered List of the tokens of the given fully-qualified name
	 **/

	public static ArrayList tokenizeName(String name)
	{
		StringTokenizer tokenizer = new StringTokenizer
			(name, Namespace.S_FULLY_QUALIFIED_NAME_SEPARATOR);
		
		ArrayList result = new ArrayList(tokenizer.countTokens());
		
		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			
			result.add(token);
		}
		
		return (result);
	}
	
	
	/**
	 *  Detokenizes the given ordered List of tokens into the form of a 
	 *  fully-qualified name. Note that the first token in the given list must be 
	 *  the base name; and if the fully-qualified name is sequenced, the base name 
	 *  must further be sequenced.
	 *  
	 *  @param orderedTokens An ordered list of constituent fully-qualified name 
	 *  		tokens
	 *  @return The fully-qualified-name form of the given ordered List of tokens
	 **/

	public static String formFullyQualifiedName(List orderedTokens)
	{
		StringBuffer result = new StringBuffer();
		
		Iterator tokens = orderedTokens.iterator();
		
		if (tokens.hasNext())
		{
			String token = (String) tokens.next();
			
			result.append(token);
			
			while (tokens.hasNext())
			{
				token = (String) tokens.next();
				
				result.append(Namespace.S_FULLY_QUALIFIED_NAME_SEPARATOR + token);
			}
		}
		
		return (result.toString());
	}
}
