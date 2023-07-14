//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/storage/PersistentObjectStoreListener.java,v 1.1 2004/05/05 19:11:52 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: PersistentObjectStoreListener.java,v $
//  Revision 1.1  2004/05/05 19:11:52  chostetter_cvs
//  Further restructuring
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

package gov.nasa.gsfc.commons.system.storage;

import java.util.EventListener;


/**
 *  This interface defines the interesting events recieved by a listener
 *  of the PersistentObjectStore.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2004/05/05 19:11:52 $
 *  @author Ken Wootton
 */
 
public interface PersistentObjectStoreListener extends EventListener
{
	/**
	 *  Indicates that an Object has been added to the 
	 *  PersistentObjectStore.
	 *
	 *  @param evt  an event containing the added entry
	 */
	 
	public void objectAdded(PersistentObjectStoreEvent evt);
	

	/**
	 *  Indicates that an Object has been removed from the 
	 *  PersistentObjectStore.
	 *
	 *  @param evt  an event containing the removed entry
	 */
	 
	public void objectRemoved(PersistentObjectStoreEvent evt);
	

	/**
	 *  Indicates that an Object within the PersistentObjectStore has 
	 *  changed in some way.
	 *
	 *  @param evt  an event containing the changed entry
	 */
	 
	public void objectChanged(PersistentObjectStoreEvent evt);
}

