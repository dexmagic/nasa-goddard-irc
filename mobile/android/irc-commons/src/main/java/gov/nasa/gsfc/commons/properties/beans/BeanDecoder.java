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

/**
 * A BeanDecoder is used to retrieve bean properties from a stream (file).
 * 
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @see BeanPersistenceFactory BeanPersistenceFactory
 * @version	$Date: 2005/01/03 20:27:25 $
 * @author	smaher
 */

public interface BeanDecoder
{
    /**
     * Decode an object from the input stream provide to {@link BeanPersistenceFactory}.
     * 
     * @return the object that was decoded
     */
    public abstract Object decode();

    /**
     * Close the decoder
     */
    public abstract void close();
}

//--- Development History  ---------------------------------------------------
//
//$Log: BeanDecoder.java,v $
//Revision 1.2  2005/01/03 20:27:25  smaher_cvs
//Spelling correction: BeanPersistanceFactory -> BeanPersistenceFactory.
//
//Revision 1.1  2004/12/21 20:03:05  smaher_cvs
//Initial checkin.  This is a simple framework to support property encoding and decoding.
//
//Revision 1.1  2004/12/20 21:51:21  smaher_cvs
//Added BeanPersistenceFactory.  Checkpointing bean saving.
//