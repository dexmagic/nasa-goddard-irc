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

import java.beans.XMLEncoder;

/**
 * Wrapper around Java Standard Edition methods of encoding beans (XMLEncoder).
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/03 20:27:25 $
 * @author	smaher
 */

public class JSEBeanEncoderWrapper implements BeanEncoder
{
    private XMLEncoder fEncoder;
    
    public JSEBeanEncoderWrapper(XMLEncoder encoder)
    {
        fEncoder = encoder;
    }
    
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.markIII.commons.BeanEncoder#encode(java.lang.Object)
     */
    public void encode(Object o)
    {
        fEncoder.writeObject(o);
    }
    
    /* (non-Javadoc)
     * @see gov.nasa.gsfc.irc.markIII.commons.BeanEncoder#close()
     */
    public void close()
    {
        fEncoder.close();
    }
    
}



//--- Development History  ---------------------------------------------------
//
//$Log: JSEBeanEncoderWrapper.java,v $
//Revision 1.2  2005/01/03 20:27:25  smaher_cvs
//Spelling correction: BeanPersistanceFactory -> BeanPersistenceFactory.
//
//Revision 1.1  2004/12/21 20:03:05  smaher_cvs
//Initial checkin.  This is a simple framework to support property encoding and decoding.
//
//Revision 1.1  2004/12/20 21:51:21  smaher_cvs
//Added BeanPersistenceFactory.  Checkpointing bean saving.
//