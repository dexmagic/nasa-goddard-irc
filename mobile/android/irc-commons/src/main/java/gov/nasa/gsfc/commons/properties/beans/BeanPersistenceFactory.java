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

import java.beans.DefaultPersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

//import JSX.ObjIn;
//import JSX.ObjOut;


/**
 * Factory for BeanEncoders and BeanDecoders. This is a simple implementation
 * used to facilitate comparing native Java Standard Edition (JSE) methods
 * (XMLEncoder and XMLDecoder) and JSX implementations (refer to <a
 * href="http://www.csse.monash.edu.au/~bren/JSX/">this link </a>). XStream format
 * has also been added.
 * <p>
 * TODO: Make the selection of encoding method more flexible.
 * <p>
 * Note, caution must be exercised when using the Java Standard Edition (JSE)
 * method of persisting properties. It requires the following:
 * <p>
 * 1). A constructor such as the following must exist (and is a natural
 * constructor for nameable components with immutable names):
 * 
 * <pre>
 * public MyClass(String name)
 * {
 *     super(name);
 * }
 * </pre>
 * 
 * <p>
 * <p>
 * 2) Any property loading call (for example, <code>processProperyLoading</code>)
 * should <b>not </b> be called from within this constructor.
 * 
 * 
 * <P>
 * Note this is a simple factory mainly built for experimentation. If we
 * actually allow switching between persistance methods something more flexible
 * is appropriate.
 * 
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Date: 2006/06/29 13:10:22 $
 * @author smaher
 */

public class BeanPersistenceFactory
{

    private static BeanPersistenceFactory sIntance;
    
    /** 
     * Note: when switching formats, you must remove all existing
     * saved property XML files. 
     */
    
    
    /**
     * Standard Java XML format.  
     */
    private final static int FORMAT_JSE = 0;

    /**
     * A 3rd party product, but no longer
     * support in IRC b/c of licensing issues
     */
    private final static int FORMAT_JSX = 1; 

    /**
     * 3rd party that digs deep and saves everything
     */
    private final static int FORMAT_XSTREAM = 2;
    
    
    /**
     * Which encoding format to use
     */
    private int fEncodingFormat = FORMAT_XSTREAM;
    
    protected BeanPersistenceFactory()
    {
        
    }
    
    /**
     * @return instance of the factory singleton
     */
    public static BeanPersistenceFactory getInstance()
    {
        if (sIntance == null)
        {
            sIntance = new BeanPersistenceFactory();
        }
        return sIntance;
    }
    
    
    /**
     * Create a BeanEncoder for a particular output stream.
     * <p>
     * Optionally a list of classname substrings can
	 * be included that will be ignored during encoding.  This was necessary to avoid
	 * inifinite recursion when encoding some IRC framework classes.  In that situation,
	 * the following were provided as substrings: <code>types.namespaces,irc.components,util.logging</code>.
     * <p>
     * NOTE: Currently only the XStream encoder supports skipped classes.
     * 
     * @param inStream where to encode to
	 * @param skippedClassnameSubstrings list of class name substrings of classes not to encode.    
	 * @return the encoder
     */
    public BeanEncoder getBeanEncoder(OutputStream outStream, Object objectToBeEncoded, List skippedClassnameSubstrings)
    {
    	switch (fEncodingFormat) {
    	case FORMAT_JSX:
    		return getJSXBeanEncoder(outStream);
    		
    	case FORMAT_JSE:
    		return getJSEBeanEncoder(outStream, objectToBeEncoded);
    		
    	case FORMAT_XSTREAM:
    		return new XStreamBeanEncoderWrapper(outStream, skippedClassnameSubstrings);
    	}
    	
    	throw new IllegalArgumentException("Unidentified encoder format: " + fEncodingFormat);
    }

    /**
     * Create a BeanDecoder for a particular input stream.
     * 
     * @param inStream where to decode from
     * @return the decoder
     */
    public BeanDecoder getBeanDecoder(InputStream inStream)
    {
        
    	switch (fEncodingFormat) {
    	case FORMAT_JSX:
    		return getJSXBeanDecoder(inStream);
    		
    	case FORMAT_JSE:
    		return getJSEBeanDecoder(inStream);
    		
    	case FORMAT_XSTREAM:
    		return new XStreamBeanDecoderWrapper(inStream);
    	}
    	
    	throw new IllegalArgumentException("Unidentified encoder format: " + fEncodingFormat);
        
    }
    
    
    /**
     * @param outStream
     */
    protected BeanDecoder getJSXBeanDecoder(InputStream inStream)
    {
        BeanDecoder b = null;
//        try
//        {
//            b = new JSXBeanDecoderWrapper(new ObjIn(inStream));
        throw new UnsupportedOperationException("JSX no longer supported");
//        } catch (IOException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
//        return b;
        
    }
    /**
     * @param outStream
     * @return
     */
    protected BeanEncoder getJSXBeanEncoder(OutputStream outStream)
    {
        BeanEncoder b = null;
        throw new UnsupportedOperationException("JSX no longer supported");
//        try
//        {
//            b = new JSXBeanEncoderWrapper(new ObjOut(outStream));
//        } catch (IOException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
//        return b;
    }
    
    /**
     * @param inStream
     * @return
     */
    protected BeanDecoder getJSEBeanDecoder(InputStream inStream)
    {
        // Serialize object into XML
        XMLDecoder decoder = new XMLDecoder(inStream);
        
        return new JSEBeanDecoderWrapper(decoder);
    }

    /**
     * @param outStream
     * @return
     */
    protected BeanEncoder getJSEBeanEncoder(OutputStream outStream, Object objectBeingEncoded)
    {
        BeanEncoder b = null; 
        XMLEncoder jseEncoder = new XMLEncoder(outStream);
        // The follow is crucial to being able to put property loading in the default constructor.
        // TODO: more comments
        String [] propertyNames = new String[] {"name"};
        jseEncoder.setPersistenceDelegate(objectBeingEncoded.getClass(), new DefaultPersistenceDelegate(propertyNames));
        b = new JSEBeanEncoderWrapper(new XMLEncoder(outStream));
        return b;
    }
    
    
    /**
     * @param outStream
     * @param skippedClassnameSubstrings
     * @return
     */
    protected BeanEncoder getXStreamBeanEncoder(OutputStream outStream, List skippedClassnameSubstrings)
    {
    	BeanEncoder b = null; 
    	b = new XStreamBeanEncoderWrapper(outStream, skippedClassnameSubstrings);
    	return b;
    }
    
}



//--- Development History  ---------------------------------------------------
//
//$Log: BeanPersistenceFactory.java,v $
//Revision 1.5  2006/06/29 13:10:22  smaher_cvs
//Moved IRC core dependencies out (they were used for retrieving some preferences)
//
//Revision 1.4  2006/06/08 13:35:27  smaher_cvs
//Added ability to ignore package names when persisting components.
//
//Revision 1.3  2005/04/15 13:02:20  smaher_cvs
//Removed JSX-based bean persistence mechanism.
//
//Revision 1.2  2005/04/08 13:50:46  smaher_cvs
//Added support for XStream XML property encoding.
//
//Revision 1.1  2005/01/03 20:27:25  smaher_cvs
//Spelling correction: BeanPersistanceFactory -> BeanPersistenceFactory.
//
//Revision 1.1  2004/12/21 20:03:05  smaher_cvs
//Initial checkin.  This is a simple framework to support property encoding and decoding.
//
//Revision 1.1  2004/12/20 21:51:21  smaher_cvs
//Added BeanPersistenceFactory.  Checkpointing bean saving.
//