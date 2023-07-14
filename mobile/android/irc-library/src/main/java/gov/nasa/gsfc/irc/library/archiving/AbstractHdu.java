//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.library.archiving;


import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Data;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsUtil;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.ArrayDataOutput;
import nom.tam.util.Cursor;

import org.jdom.Document;
import org.jdom.Element;

import gov.nasa.gsfc.commons.xml.XmlException;
import gov.nasa.gsfc.commons.xml.XmlParser;

/**
 * The AbstractHdu class provides a simple Facade around the 
 * "nom.tam.fits" classes for creating and FITS files. This class also provides
 * the ability to initialize a HDU from an XML file or an existing FITS file.
 * For many implementations
 * subclasses will not need to know the details of the nom.tam.fits classes; 
 * however the <code>buildHdu</code> method must be implemented. The underlying 
 * FITS header and data can still be manipulated for more advanced applications
 * with the <code>getHeader</code> and <code>getData</code> methods.
 * <p>For a discussion of the Flexible Image Transport System (FITS) see 
 * documentation at 
 * <a href="http://fits.gsfc.nasa.gov">http://fits.gsfc.nasa.gov</a>.
 * For a discussion of the nom.tam.fits classes see the documentation at
 * <a href="http://fits.gsfc.nasa.gov/fits_libraries.html#java_tam">
 * http://fits.gsfc.nasa.gov/fits_libraries.html#java_tam</a>.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/05/23 15:33:50 $
 * @author	T. Ames 
**/
abstract public class AbstractHdu 
{
	private static final String CLASS_NAME = AbstractHdu.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);

	//---FITS constants
	//public static String IMAGE_EXTENSION = "IMAGE";
	//---Defaults
	public static final String UNDEFINED = "undefined";
	
	// XML Description constants
	private static final String HDU_ELEMENT = "Hdu";
	private static final String HEADER_ELEMENT = "Header";
	private static final String HEADER_FIELD_ELEMENT = "Field";
	private static final String KEY_ATTR = "key";
	private static final String TYPE_ATTR = "type";
	private static final String DEFAULT_ATTR = "default";
	private static final String COMMENT_ATTR = "comment";
	
	private static final String INTEGER_TYPE = "Integer";
	private static final String LONG_TYPE = "Long";
	private static final String FLOAT_TYPE = "Float";
	private static final String DOUBLE_TYPE = "Double";
	private static final String BOOLEAN_TYPE = "Boolean";
	private static final String STRING_TYPE = "String";
	private static final String COMMENT_KEY = "COMMENT";

	//---HDU
	protected BasicHDU fHdu = null;
	protected Header fHduHeader = new Header();
	protected Data fHduData = null;
	protected Cursor fCardIterator = fHduHeader.iterator();
	protected ArrayDataOutput fOutput = null;
	protected long fWrittenSize = 0;

	public AbstractHdu()
	{
	}
	
	public AbstractHdu(URL url)
	{		
		initFromDescription(url);
	}

	/**
	 * Write the HDU to the output. First calls the <code>buildHdu</code> 
	 * method before writing the resulting HDU.
	 *
	 * @param output ArrayDataOutput to write to.
	 * @throws FitsException if the write fails
	**/
	public synchronized void write(ArrayDataOutput output) throws FitsException
	{
		fHdu = buildHdu();

		if (fHdu != null)
		{
			fOutput = output;
			fHdu.write(output);
			
			// Save size of write to determine if this HDU can be rewritten
			// in place later.
			fWrittenSize = fHdu.getSize();
		}
		else
		{
			System.out.println("hdu is null ... not writing");
		}
	}

	/**
	 * Write the HDU to the output again in place. The size of the HDU 
	 * including any padding must be the same size as the original write.
	 *
	 * @throws FitsException if the HDU is not rewritable.
	 * @throws IOException if the write fails
	**/
	public synchronized void writeAgain() throws FitsException, IOException
	{
		if (fOutput != null)
		{
			// Current size must be the same as the written size.
			if (fHdu.getSize() == fWrittenSize)
			{
				fOutput.flush();
				
				//System.out.println("fHdu.getFileOffset():" + fHdu.getFileOffset());
				FitsUtil.reposition(fOutput, fHdu.getFileOffset());
				fHdu.write(fOutput);
				fOutput.flush();
			}
			else
			{
				System.out.println("fHdu.getSize():" + fHdu.getSize());
				throw new FitsException("HDU cannot be rewritten because it is not the same size");
			}
		}
		else
		{
			throw new FitsException(
				"HDU cannot be rewritten because output is null");
		}
	}

	/**
	 * Build the HDU from the contained header and data. The result from 
	 * this call must be a valid HDU.
	 * 
	 * @return a valid HDU.
	**/
	abstract public BasicHDU buildHdu();

	/**
	 * Build the HDU from an existing FITS file.
	 *
	 * @param file	the file to read
	 * @throws FitsException if the file is not a valid FITS file
	 * @throws IOException if the file could not be read
	**/
	public synchronized void initFromFile(Fits file) throws FitsException, IOException
	{
		//---Build HDU
		fHdu = file.readHDU();

		if (fHdu == null)
		{
			throw (new FitsException("No HDUs remaining or none found"));
				
		}

		//---Build header
		fHduHeader = fHdu.getHeader();

		//---Build data
		fHduData = fHdu.getData();
	}

	/**
	 * Build HDU from an XML description.
	 * 
	 * @param url URL pointing to a file containing the description.
	**/
	public synchronized final void initFromDescription(URL url)
	{
		XmlParser parser = new XmlParser(false);
		Document dom = null;
		
		try
		{
			dom = parser.parse(url);
			
			Element hdu = dom.getRootElement();
			
			//System.out.println("Root Element " + hdu.getName());
			
			if (!HDU_ELEMENT.equals(hdu.getName()))
			{
				// This does not look like a valid file
				// Unknown type
				String message = "Unknown element \"" + hdu.getName() + "\""
						+ " in url:" + url.toString();

				sLogger.logp(
					Level.WARNING, CLASS_NAME, "initFromDescription", message);

				return;
			}
			
			Iterator iter = hdu.getChildren().iterator();
			
			while (iter.hasNext())
			{
				//Object element = (Element) iter.next();
				Element element = (Element) iter.next();
				
				if (HEADER_ELEMENT.equals(element.getName()))
				{
					Iterator fieldIter = element.getChildren().iterator();
				
					while (fieldIter.hasNext())
					{
						Element fieldElem = (Element) fieldIter.next();
						
						String key = fieldElem.getAttributeValue(KEY_ATTR);
						String type = fieldElem.getAttributeValue(TYPE_ATTR);
						String comment = fieldElem.getAttributeValue(COMMENT_ATTR);
						String defaultStr = fieldElem.getAttributeValue(DEFAULT_ATTR);
						//Object defaultValue = defaultStr;
						
						// TODO this could use the LookUpTable class to match a type
						// but the FITS types are more constrained so for know
						// I am just checking here.
						
						// Check type and add to header
						try
						{
							// check if it is a comment entry
							if (COMMENT_KEY.equals(key))
							{
								addComment(key, comment);
							}
							// check if it is a null value entry
							else if (defaultStr == null || defaultStr.equals(""))
							{
								addNullValue(key, comment);
							}
							// Add the type specific entry
							else if (INTEGER_TYPE.equals(type))
							{
								int value = Integer.decode(defaultStr).intValue();
								fHduHeader.addValue(key, value, comment);
							}
							else if (LONG_TYPE.equals(type))
							{
								long value = Long.decode(defaultStr).longValue();
								fHduHeader.addValue(key, value, comment);
							}
							else if (FLOAT_TYPE.equals(type))
							{
								float value = Float.parseFloat(defaultStr);
								fHduHeader.addValue(key, value, comment);
							}
							else if (DOUBLE_TYPE.equals(type))
							{
								double value = Double.parseDouble(defaultStr);
								fHduHeader.addValue(key, value, comment);
							}
							else if (BOOLEAN_TYPE.equals(type))
							{
								boolean value = 
									Boolean.valueOf(defaultStr).booleanValue();
								fHduHeader.addValue(key, value, comment);
							}
							else if (STRING_TYPE.equals(type))
							{
								addString(key, defaultStr, comment);
							}
							else
							{
								// Unknown type
								String message = "Unknown type \""
									+ type + "\"" + " in element "
									+ key + " in url:" + url.toString();
									
								sLogger.logp(Level.WARNING, CLASS_NAME, 
										"initFromDescription", message);
							}
						}
						catch (NumberFormatException e)
						{
							String message = e.getMessage()
									+ " while parsing field element " + key
									+ " in " + url.toString();

							sLogger.logp(Level.WARNING, CLASS_NAME,
								"initFromDescription", message, e);
						}
						catch (HeaderCardException e)
						{
							e.printStackTrace();
						}
					}				
				}
			}
		}
		catch (XmlException e)
		{
			String message = e.getMessage() + " while parsing "
					+ url.toString();

			sLogger.logp(Level.WARNING, CLASS_NAME, "initFromDescription",
				message, e);
		}
	}

	/**
	 * Get the comment associated with the given header key.
	 * 
	 * @param key the header key
	 * @return the comment or null if the key is not in the header 
	 */
	public synchronized String getComment(String key)
	{
		HeaderCard card = fHduHeader.findCard(key);
		String comment = null;
		
		if (card != null)
		{
			comment = card.getComment();
		}

		return comment;
	}

	/**
	 * Add the value to the header using the given key. If a value with 
	 * the given key already exists this will replace the value. Any existing
	 * comment is maintained.
	 *
	 * @param key the content key
	 * @param value content value
	**/
	public synchronized void addFloat(String key, float value)
	{
		String comment = getComment(key);
		
		try
		{
			fHduHeader.addValue(key, value, comment);
		}
		catch (HeaderCardException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Add the value to the header using the given key. If a value with 
	 * the given key already exists this will replace the value. Any existing
	 * comment is maintained.
	 *
	 * @param key the content key
	 * @param value content value
	**/
	public synchronized void addDouble(String key, double value)
	{
		String comment = getComment(key);
		
		try
		{
			fHduHeader.addValue(key, value, comment);
		}
		catch (HeaderCardException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Add the value to the header using the given key. If a value with 
	 * the given key already exists this will replace the value. Any existing
	 * comment is maintained.
	 *
	 * @param key the content key
	 * @param value content value
	**/
	public synchronized void addInteger(String key, int value)
	{
		String comment = getComment(key);

		try
		{
			fHduHeader.addValue(key, value, comment);
		}
		catch (HeaderCardException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Add the value to the header using the given key. If a value with 
	 * the given key already exists this will replace the value. Any existing
	 * comment is maintained.
	 *
	 * @param key the content key
	 * @param value content value
	**/
	public synchronized void addLong(String key, long value)
	{
		String comment = getComment(key);

		try
		{
			fHduHeader.addValue(key, value, comment);
		}
		catch (HeaderCardException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Add the value to the header using the given key. If a value with 
	 * the given key already exists this will replace the value. Any existing
	 * comment is maintained.
	 *
	 * @param key the content key
	 * @param value content value
	**/
	public synchronized void addBoolean(String key, boolean value)
	{
		String comment = getComment(key);
		
		try
		{
			fHduHeader.addValue(key, value, comment);
		}
		catch (HeaderCardException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Add the value to the header using the given key. If a value with 
	 * the given key already exists this will replace the value. Any existing
	 * comment is maintained.
	 *
	 * @param key the content key
	 * @param value content value
	**/
	public synchronized void addString(String key, String value)
	{
		String comment = getComment(key);
		
		addString(key, value, comment);
	}

	/**
	 * Add the value to the header using the given key and comment. If a value 
	 * with the given key already exists this will replace the value. 
	 *
	 * @param key the content key
	 * @param value content value
	 * @param comment content comment
	**/
	public synchronized void addString(String key, String value, String comment)
	{
		boolean validValue = true;
				
		// Check if the value is a valid string 
		// to prevent the fits libraries from throwing an exception
		if (value != null)
		{
			value = value.trim();
			
			// Check if result is an empty string
			if (value.hashCode() == 0)
			{
				// The fits libraries cannot handle empty strings
				validValue = false;
			}
		}
		else
		{
			// The fits libraries cannot handle null strings
			validValue = false;
		}
			
		try
		{
			if (validValue)
			{
				fHduHeader.addValue(key, value, comment);
			}
			else
			{
				//fHduHeader.addLine(new HeaderCard(key, comment, true));				
				//fHduHeader.addValue(key, null, comment);
				fHduHeader.addValue(key, UNDEFINED, comment);
			}
		}
		catch (HeaderCardException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Add a null value to the header using the given key. If a value with 
	 * the given key already exists this will replace the value. Any existing
	 * comment is maintained.
	 *
	 * @param key the content key
	**/
	public synchronized void addNullValue(String key)
	{
		String comment = getComment(key);
		
		addNullValue(key, comment);
	}

	/**
	 * Add null value to the header using the given key. If a value with 
	 * the given key already exists this will replace the value. 
	 *
	 * @param key the content key
	 * @param comment content comment
	**/
	public synchronized void addNullValue(String key, String comment)
	{
		try
		{
			fHduHeader.removeCard(key);
			Cursor cardIterator = fHduHeader.iterator();
			cardIterator.setKey(key);
			cardIterator.add(key, new HeaderCard(key, comment, true));
		}
		catch (HeaderCardException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Add a comment to the header using the given key. This style of comment
	 * has no value. 
	 *
	 * @param key the content key
	 * @param comment content comment
	**/
	public synchronized void addComment(String key, String comment)
	{
			fHduHeader.insertCommentStyle(key, comment);
			//fHduHeader.removeCard(key);
			//fCardIterator.add(key, new HeaderCard(key, comment, false));
	}
	
	/**
	 * Determines if the header block includes any padding to complete the
	 * block. 
	 * 
	 * @return true if the header block does not include any padding.
	 */
	public synchronized boolean isHeaderFull()
	{
		boolean result = true;
		int cards = fHduHeader.getNumberOfCards();
		
		if (FitsUtil.padding(cards * 80) > 0)
		{
			result = false;
		}
		
		return result;
	}

	/**
	 * Determines the number of entries that can be added before the header
	 * block is full. 
	 * 
	 * @return number of empty entries in header.
	 */
	public synchronized int getEmptyHeaderEntries()
	{
		int cards = fHduHeader.getNumberOfCards();
		
		return FitsUtil.padding(cards * 80) / 80;
	}

	public synchronized Header getHeader()
	{
		return fHduHeader;
	}
	
	public synchronized Data getData()
	{
		return fHduData;
	}
	
	public synchronized BasicHDU getHduRepresentation()
	{
		return fHdu;
	}
	
	/**
	 * Dump information about the HDU.
	 *
	**/
	public synchronized void dumpInfo()
	{
		if (fHdu != null)
		{
			fHdu.info();
		}
		else
		{
			System.out.println("HDU is currently null.");
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: AbstractHdu.java,v $
//	Revision 1.3  2005/05/23 15:33:50  tames_cvs
//	Removed reference to deprecated XmlParser constructor.
//	
//	Revision 1.2  2005/01/11 21:35:46  chostetter_cvs
//	Initial version
//	
//	Revision 1.1  2004/11/09 21:24:30  tames
//	Port to V6
//	
//	Revision 1.13  2004/04/14 17:39:59  tames_cvs
//	made hdu written size protected so subclasses can use it
//	
//	Revision 1.12  2004/02/23 06:38:23  tames_cvs
//	Synchronized methods
//	
//	Revision 1.11  2004/02/01 06:39:25  tames_cvs
//	added support for rewriting an HDU, adding comment style fields, 
//	and null valued fields.
//	
//	Revision 1.10  2004/01/15 19:59:00  ctran_cvs
//	Undo from BEFORE_NEW_PIPELINE
//	
//	Revision 1.8  2004/01/09 07:02:18  tames_cvs
//	Added addFloat() and addInteger() methods
//	
//	Revision 1.7  2003/12/23 17:04:31  tames_cvs
//	Added more work arounds for bugs in FITS library.
//	
//	Revision 1.6  2003/09/22 21:42:34  chostetter_cvs
//	Algorithm and Properties related changes
//	
//	Revision 1.5  2003/08/26 15:24:44  tames_cvs
//	As a work around for a fits library bug all strings are trimmed and then checked for an empty or null string.
//	
//	Revision 1.3  2003/08/13 05:30:59  tames_cvs
//	Initial version
//	
//	Revision 1.2  2003/08/07 22:17:58  tames_cvs
//	Initial FITS archiving class
//	
//	Revision 1.1  2003/08/05 18:51:26  tames_cvs
//	Initial adapted from HAWC and SHARC implementations
//	
//	Revision 1.2  2003/07/25 21:45:19  tames_cvs
//	Added capability to read default values from an XML file
//	 
