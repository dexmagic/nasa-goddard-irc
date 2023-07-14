//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   6	IRC	   1.5		 11/13/2001 9:32:15 AMJohn Higinbotham Javadoc
//		update.
//   5	IRC	   1.4		 11/12/2001 5:01:04 PMJohn Higinbotham Javadoc
//		update.
//   4	IRC	   1.3		 10/19/2001 9:35:53 AMKen Wootton	 Added method to
//		get a double token.
//   3	IRC	   1.2		 9/28/2001 3:13:29 PM Ken Wootton	 Added integer
//		convenience method.
//   2	IRC	   1.1		 7/20/2001 9:19:19 AM Ken Wootton	 Added
//		documentation.
//   1	IRC	   1.0		 7/17/2001 4:28:11 PM Ken Wootton	 
//  $
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

package gov.nasa.gsfc.commons.types.strings;

import java.util.NoSuchElementException;

/**
 *  This class provides a string tokenizer quite similar to the JDK
 *  provided java.util.StringTokenizer.  The biggest difference between
 *  them is that this class expects full strings, not a list of characters, for
 *  its delimeter.  For example, this class can delimit the string 
 *  "helloMY_DELIMITERworld", into the "hello" and "world tokens when
 *  the delimiter "MY_DELIMITER" is used. 
 *	<p>Note that an empty string before or after a token counts as a token.
 *  For example, "MY_DELIMTER" contains two empty string tokens.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/07/12 14:26:24 $
 *  @author		Ken Wootton
 */
public class FullStringTokenizer
{
	//  Failed string search return value
	private static final int SEARCH_FAILED = -1;

	//  Exception messages.
	private static final String BAD_ARGS_MSG	   = "The given string and delimiter must be non-null";
	private static final String NO_MORE_TOKENS_MSG = "No more tokens exist in the string.";

	//  Default delimiter
	private static final String DEF_DELIMITER = " ";

	//  String, delimiter, and pointer within that string.
	private String fStr = null;
	private String fDelimiter = DEF_DELIMITER;
	private int fPtr = 0;

	//  Track whether or not the last token has been retrieved by the
	//  user.
	private boolean fLastTokenRetrieved = false;

	/**
	 *  Create a new tokenizer with the given string and delimiter.
	 *
	 *  @param str  the string to tokenize
	 *  @param delimiter  the delimiter to use to tokenize the string
	 */
	public FullStringTokenizer(String str, String delimiter)
	{
		if (str == null || delimiter == null)
		{
			throw new IllegalArgumentException(BAD_ARGS_MSG);
		}

		fStr = str;
		fDelimiter = delimiter;
	}

	/**
	 *  Count the number of tokens from the current position within the
	 *  string.  This will determine how many times @see #nextToken can be
	 *  called before it throws a NoSuchElementException.
	 *
	 *  @return  the number of tokens left in the string
	 */
	public int countTokens()
	{
		int countPtr = fPtr;
		int count = 0;
		int delimiterIndex = 0;

		//  Just search through for the delimiters.
		delimiterIndex = fStr.indexOf(fDelimiter, countPtr);
		while(delimiterIndex != SEARCH_FAILED)
		{
			count++;

			//  Push the ptr past the token and get the next one.
			countPtr = delimiterIndex + fDelimiter.length();
			delimiterIndex = fStr.indexOf(fDelimiter, countPtr);
		}

		//  Note that there is one more token after the last delimiter.
		return count + 1;
	}

	/**
	 *  Determine if the string has any more tokens.
	 *
	 *  @return  whether or not the string contains any more tokens
	 */
	public boolean hasMoreTokens()
	{
		return !fLastTokenRetrieved;
	}

	/**
	 *  Get the next token in the string.
	 *
	 *  @return  the next token in the string
	 *
	 *  @throws NoSuchElementException  if there are no more tokens
	 */
	public String nextToken() throws NoSuchElementException
	{
		int startIndex = fPtr;

		//  Find the starting index of the next delimiter.  Note that
		//  substrings go to endIndex - 1.
		int endIndex = fStr.indexOf(fDelimiter, fPtr);
		if (endIndex != SEARCH_FAILED)
		{
			//  Update our pointer past this token.
			fPtr = endIndex + fDelimiter.length();
		}

		//  No more delimiters...
		else
		{
			//  If the last token has been retrieved, complain.
			if (fLastTokenRetrieved)
			{
				throw new NoSuchElementException(NO_MORE_TOKENS_MSG);
			}

			//  Otherwise, the token extends from the pointer to the end of 
			//  the string.
			else
			{
				endIndex = fStr.length();
				fPtr = endIndex;

				fLastTokenRetrieved = true;
			}
		}

		return fStr.substring(startIndex, endIndex);
	}
	
	/**
	 *  Get the next token in the string if it is an integer.
	 *
	 *  @return  the next token in the string, if it is an integer
	 *
	 *  @throws NoSuchElementException  if there are no more tokens
	 *  @throws NumberFormatException  if the next token is not an integer
	 */
	public int nextInt() throws NoSuchElementException, NumberFormatException
	{
		return (new Integer(nextToken().trim())).intValue();
	}

	/**
	 *  Get the next token in the string if it is an double.
	 *
	 *  @return  the next token in the string, if it is an double
	 *
	 *  @throws NoSuchElementException  if there are no more tokens
	 *  @throws NumberFormatException  if the next token is not an double
	 */
	public double nextDouble() 
		throws NoSuchElementException, NumberFormatException
	{
		return (new Double(nextToken().trim())).doubleValue();
	}
}

