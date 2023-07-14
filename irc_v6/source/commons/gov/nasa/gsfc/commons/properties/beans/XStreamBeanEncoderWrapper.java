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
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Wrapper around XStream methods of encoding beans.
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2006/06/29 13:10:22 $
 * @author	smaher
 */

public class XStreamBeanEncoderWrapper implements BeanEncoder
{
	private OutputStream fOutStream;
    private XStream fXStream;

	/**
	 * Construct an XStream bean encoder.  Optionally a list of classname substrings can
	 * be included that will be ignored during encoding.  This was necessary to avoid
	 * inifinite recursion when encoding some IRC framework classes.  In that situation,
	 * the following were provided as substrings: <code>types.namespaces,irc.components,util.logging</code>.
	 * 
	 * @param outStream
	 * @param skippedClassnameSubstrings list of class name substrings of classes not to encode.
	 */
	public XStreamBeanEncoderWrapper(OutputStream outStream, final List skippedClassnameSubstrings) {
			
        class OmitFieldsWithMyPrefixMapper extends MapperWrapper {
            public OmitFieldsWithMyPrefixMapper(ClassMapper wrapped) {
                super(wrapped);
            }

            public boolean shouldSerializeMember(Class definedIn, String fieldName) {
            	String className = definedIn.getName();
            	
            	boolean shouldSerialize = true;
            	if (skippedClassnameSubstrings != null)
            	{
            		for (Iterator iter = skippedClassnameSubstrings.iterator(); iter.hasNext();)
            		{
            			String substring = (String) iter.next();
            			if (className.indexOf(substring) > 0)
            			{
            				shouldSerialize = false;
            				break;
            			}
            		}
            	}
            	return shouldSerialize;
            }
        }

        // Create a custome XStream with our own MapperWrapper
        fXStream = new XStream() {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new OmitFieldsWithMyPrefixMapper(next);
            }
        };

		if (outStream == null)
		{
			throw new IllegalArgumentException("Null outStream");
		}
		fOutStream = outStream;
	}

	/* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.markIII.commons.BeanEncoder#encode(java.lang.Object)
     */
    public void encode(Object o)
    {
        try
        {
        	fOutStream.write(fXStream.toXML(o).getBytes());
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.markIII.commons.BeanEncoder#close()
     */
    public void close()
    {
        try {
			fOutStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}



//--- Development History  ---------------------------------------------------
//
//$Log: XStreamBeanEncoderWrapper.java,v $
//Revision 1.4  2006/06/29 13:10:22  smaher_cvs
//Moved IRC core dependencies out (they were used for retrieving some preferences)
//
//Revision 1.3  2006/06/09 14:07:32  smaher_cvs
//Fixed bug where omitted class name was hardcoded.
//
//Revision 1.2  2006/06/08 13:35:27  smaher_cvs
//Added ability to ignore package names when persisting components.
//
//Revision 1.1  2005/04/08 13:50:46  smaher_cvs
//Added support for XStream XML property encoding.
//
