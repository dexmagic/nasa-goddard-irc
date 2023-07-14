//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log:
//	 1	IRC	   1.0		 12/11/2001 5:33:10 PMTroy Ames
//	$
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

package gov.nasa.gsfc.commons.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

import net.jxta.document.Advertisement;
import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredTextDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.MessageElementEnumeration;

/**
 * NetUtil is a noninstantiable utility class containing convenience methods
 * for manipulating Jxta objects.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.</p>
 *
 * @version	$Date: 2005/04/04 15:40:58 $
 * @author	Troy Ames
**/
public class NetUtil
{

	/**
	 * Suppress default constructor so class cannot be instantiated.
	 */
	private NetUtil()
	{
	}

	/**
	 * Converts a Document into a String. Returns
	 * null if an exception occurred reading the document.
	 *
	 * @param   doc document to convert
	 * @return  a String representation of the document, null if an
	 *		  exception occurred.
	 */
	public static String docToString(Document doc)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try
		{
			doc.sendToStream(out);
			return out.toString();
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	/**
	 * Converts an Advertisement into a String of the given mime type. Returns
	 * null if an exception occurred reading the advertisement.
	 *
	 * @param   adv advertisement to convert
	 * @param   displayAs   Mime type to format the advertisement String as.
	 * @return  a String representation of the advertisement, null if an
	 *		  exception occurred.
	 */
	public static String advToString(Advertisement adv, MimeMediaType displayAs)
	{
		StringWriter out = new StringWriter();

		try
		{
			StructuredTextDocument doc =
				(StructuredTextDocument) adv.getDocument(displayAs);
			doc.sendToWriter(out);
			return out.toString();
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	/**
	 * Converts a Message into a String. Returns
	 * null if an exception occurred reading the Message.
	 *
	 * @param   msg message to convert
	 * @return  a String representation of the Message, null if an
	 *		  exception occurred.
	 */
	public static String messageToString(Message msg)
	{
		StringWriter out = new StringWriter();

		try
		{
			MessageElementEnumeration elements = msg.getElements();
			out.write("{");

			while (elements.hasMoreMessageElements())
			{
				MessageElement element = elements.nextMessageElement();
				out.write("[Name: " + element.getName()
					+ " Type:" + element.getType() + " Bytes:");
				InputStream elementStream = element.getStream();
				int b = elementStream.read();
				while (b != -1)
				{
					out.write(b);
					b = elementStream.read();
				}
				out.write("]\n");
			}

			out.write("}");
			return out.toString();
		}
		catch (Exception ex)
		{
			return null;
		}
	}

}