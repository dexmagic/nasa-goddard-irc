//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/NumTokensSelector.java,v 1.1 2005/09/29 18:18:23 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: NumTokensSelector.java,v $
//  Revision 1.1  2005/09/29 18:18:23  chostetter_cvs
//  Various enhancements to data transformation stuff
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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.StringTokenizer;

import gov.nasa.gsfc.irc.data.selection.description.NumTokensSelectorDescriptor;


/**
 * A NumTokensSelector selects only the number of tokens in a given data Object.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2005/09/29 18:18:23 $
 * @author Carl F. Hostetter
 */

public class NumTokensSelector extends CharDataSelector
{
	public static final Integer INTEGER_ZERO = new Integer(0);
	
	private NumTokensSelectorDescriptor fDescriptor;
	private String fTokenSeparator;
	
	
	/**
	 * Constructs a new NumTokensSelector that will select the number of token 
	 * of any given data Object.
	 *
	 * @param descriptor A NumTokensSelectorDescriptor
	 * @return A new NumTokensSelector that will select the name of any given 
	 * 		named data Object		
	**/
	
	public NumTokensSelector(NumTokensSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fDescriptor = descriptor;
		
		configureFromDescriptor();
	}
	

	/**
	 *  Configures this NumTokensSelector in accordance with its current 
	 *  NumTokensSelectorDescriptor.
	 *  
	 */
	
	private void configureFromDescriptor()
	{
		if (fDescriptor != null)
		{
			fTokenSeparator = fDescriptor.getTokenSeparator();
		}
	}
	
	
	/**
	 *  Returns the number of tokens in the given byte array.
	 *  
	 *  @para data A byte array of data
	 *  @return The number of tokens in the given byte array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given byte array
	 */
	
	protected Object select(byte[] data) 
		throws IllegalArgumentException
	{
		Integer result = INTEGER_ZERO;
		
		if (data != null)
		{
			String stringData = null;
			
			try
			{
				if (isAscii())
				{
					stringData = new String(data, "US-ASCII");
				}
				else
				{
					stringData = new String(data);
				}
				
				result = (Integer) select(stringData);
			}
			catch (UnsupportedEncodingException ex)
			{
				
			}
		}
		
		return (result);
	}


	/**
	 *  Returns the number of tokens in the given char array.
	 *  
	 *  @para data A char array of data
	 *  @return The number of tokens in the given char array
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given char array
	 */
	
	protected Object select(char[] data)
	{
		Integer result = INTEGER_ZERO;
		
		if (data != null)
		{
			result = (Integer) select(new String(data));
		}
		
		return (result);
	}


	/**
	 *  Returns the number of tokens in the given ByteBuffer.
	 *  
	 *  @para data A ByteBuffer of data
	 *  @return The number of tokens in the given ByteBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given ByteBuffer
	 */
	
	protected Object select(ByteBuffer data)
	{
		Integer result = INTEGER_ZERO;
		
		if (data != null)
		{
			int numBytes = data.remaining();
			
			byte[] dataBytes = new byte[numBytes];
			
			data.mark();
			data.get(dataBytes);
			data.reset();
			
			result = (Integer) select(dataBytes);
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the number of tokens in the given CharBuffer.
	 *  
	 *  @para data A CharBuffer of data
	 *  @return The number of tokens in the given CharBuffer
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given CharBuffer
	 */
	
	protected Object select(CharBuffer data)
	{
		Integer result = INTEGER_ZERO;
		
		if (data != null)
		{
			result = (Integer) select(data.toString());
		}
		
		return (result);
	}
	
	
	/**
	 *  Returns the number of tokens in the given String.
	 *  
	 *  @para data A String of data
	 *  @return The number of tokens in the given String
	 *  @throws IllegalArgumentException if this selection cannot be applied 
	 *  		to the given String
	 */
	
	protected Object select(String data)
	{
		Integer result = INTEGER_ZERO;
		
		if (data != null)
		{
			StringTokenizer tokenizer = 
				new StringTokenizer(data, fTokenSeparator);
				
			int numTokens = tokenizer.countTokens();
			
			result = new Integer(numTokens);
		}
		
		return (result);
	}
}
