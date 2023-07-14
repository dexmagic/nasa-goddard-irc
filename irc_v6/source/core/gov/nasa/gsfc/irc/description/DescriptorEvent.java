//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   2	IRC	   1.1		 11/14/2001 2:50:55 PMJohn Higinbotham Javadoc /
//		comment update.
//   1	IRC	   1.0		 9/28/2001 1:39:41 PM Bhavana Singh   
//  $
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

package gov.nasa.gsfc.irc.description;

import java.util.EventObject;


/**
 *  A DescriptorEvent is an event produced by the DescriptorLibrary
 *  in response to changes in Paml description
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @author		Bhavana Singh
**/
public class DescriptorEvent extends EventObject
{
	Descriptor fDescriptor = null;

	/**
	 *  Constructs a DescriptorEvent with the given source.
	 *
	 *	@param 	eventSource		source of this event
	 *	@param 	descriptor		cause of this event
	**/
	public DescriptorEvent(Object eventSource, Descriptor descriptor)
	{
		super(eventSource);
		fDescriptor = descriptor;
	}

	/**
	 *  Get the descriptor that caused the event.
	 *
	 *  @return descriptor that caused event 
	**/
	public Descriptor getDescriptor()
	{
		return fDescriptor;
	}
}
