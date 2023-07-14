//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.commons.properties.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.thoughtworks.xstream.XStream;

/**
 * Wrapper around XStream methods of decoding beans.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/04/08 13:50:46 $
 * @author	smaher
 */

public class XStreamBeanDecoderWrapper implements BeanDecoder
{
    private InputStreamReader fInputStreamReader;
    private XStream fXStream = new XStream();
    
    public XStreamBeanDecoderWrapper(InputStream inputStream)
    {
		if (inputStream == null)
		{
			throw new IllegalArgumentException("Null inputStream");
		}    	
    	fInputStreamReader = new InputStreamReader(inputStream);
    }
    
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.markIII.commons.BeanDecoder#decode()
     */
    public Object decode()
    {
        return fXStream.fromXML(fInputStreamReader);
    }
    
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.markIII.commons.BeanDecoder#close()
     */
    public void close()
    {
        try {
			fInputStreamReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}



//--- Development History  ---------------------------------------------------
//
//$Log: XStreamBeanDecoderWrapper.java,v $
//Revision 1.1  2005/04/08 13:50:46  smaher_cvs
//Added support for XStream XML property encoding.
//
