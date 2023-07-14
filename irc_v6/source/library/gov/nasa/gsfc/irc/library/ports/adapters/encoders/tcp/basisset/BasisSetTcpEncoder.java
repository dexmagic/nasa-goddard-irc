//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
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

package gov.nasa.gsfc.irc.library.ports.adapters.encoders.tcp.basisset;

import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.DataSet;

/**
 * Defines an encoder to be used with AbstractBasisSetTcpOutputAdapter. The encoder takes
 * basis set information, encodes it, and sends it out on the TCP socket.
 * <p>
 * The interface is slightly contrived. It is a first pass at separating the
 * socket management code from the data encoding code in the (older)
 * BasisSetTcpJavaSerializationOutputAdapter.  After this separation, XDR encoding was
 * implemented alongside Java serialization while sharing socket management.
 * <P>
 * Ideally the encoders would have nothing to do with TCP sockets.  Also
 * the pseudo-thread methods would be, er, changed.
 * <P>
 * This code was developed for NASA, Goddard Space Flight Center, Code 580 for
 * the Instrument Remote Control (IRC) project.
 * 
 * @version $Id: BasisSetTcpEncoder.java,v 1.2 2006/05/16 12:43:18 smaher_cvs Exp $
 * @author $Author: smaher_cvs $
 * @see gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpXdrOutputAdapter
 * @see gov.nasa.gsfc.irc.library.ports.adapters.BasisSetTcpJavaSerializationOutputAdapter
 */
public interface BasisSetTcpEncoder
{
	/**
	 * Start the encoder
	 */
	void start();
	
	/**
	 * Stop/interrup the encoder
	 */
	void interrupt();	
	
	/**
	 * Handle (encode and send) and new dataset
	 * @param dataSet
	 */
	void handleDataSet(DataSet dataSet);	
	
	/**
	 * Handle (encode and send) a basis bundle change
	 * @param inputBasisBundleId
	 */
	void handleBasisBundleChange(BasisBundleId inputBasisBundleId);	
}

//
//--- Development History  ---------------------------------------------------
//
//	$Log: BasisSetTcpEncoder.java,v $
//	Revision 1.2  2006/05/16 12:43:18  smaher_cvs
//	Renamed BasisSet input and output adapters.
//	
//	Revision 1.1  2006/03/31 16:27:23  smaher_cvs
//	Added XDR basis set encoding, which included pulling out some common functionality from BasisSetTcpJavaSerializationOutputAdapter.
//	