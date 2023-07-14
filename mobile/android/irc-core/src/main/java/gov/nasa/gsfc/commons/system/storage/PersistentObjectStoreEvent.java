//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/commons/gov/nasa/gsfc/commons/system/storage/PersistentObjectStoreEvent.java,v 1.2 2004/07/12 14:26:24 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: PersistentObjectStoreEvent.java,v $
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
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

import java.util.EventObject;


/**
 *  This class is used to capture information about a changes to the
 *  PersistentObjectStore.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2004/07/12 14:26:24 $
 *  @author Ken Wootton
 */
public class PersistentObjectStoreEvent extends EventObject
{
	private Object fObject = null;
	

	/**
	 *  Create the event.
	 *
	 *  @param source  source of the event
	 *  @param object  the Object that caused/was part of the 
	 *					 event
	 */
	 
	public PersistentObjectStoreEvent(Object source, Object object)
	{
		super(source);

		fObject = object;
	}
	

	/**
	 *  Get the Object within the PersistentObjectStore that caused/was part 
	 *  of the event.
	 *
	 *  @return  the data object that caused the event
	 */
	 
	public Object getObject()
	{
		return fObject;
	}
}

