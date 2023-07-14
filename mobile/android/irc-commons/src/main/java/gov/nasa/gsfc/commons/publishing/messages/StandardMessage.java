//=== File Prolog ============================================================
//	This code was developed by NASA Goddard Space Flight Center, 
//  Code 580 for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is listed at the end of the file
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

package gov.nasa.gsfc.commons.publishing.messages;


/**
 * A StandardMessage is a concrete subclass of the <code>AbstractMessage</code>
 * class.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version		$Date: 2006/04/18 03:57:06 $
 * @author		Troy Ames
**/
public class StandardMessage extends AbstractMessage implements Message
{
	/**
	 * Default constructor of a new Message.
	**/	
	public StandardMessage()
	{
		this(null, null);
	}
	
	/**
	 * Constructs a new Message having the given base name.
	 *
	 * @param name The base name of the new Message
	**/	
	public StandardMessage(String name)
	{
		this(name, null);
	}		

	/**
	 * Constructs a new Message having the given base name and name qualifier 
	 * (which can be null).
	 *
	 * @param name The base name of the new Message
	 * @param nameQaulifier The name qualifier of the new Message (if any)
	**/	
	public StandardMessage(String name, String nameQualifier)
	{
		super(name, nameQualifier);
	}		
}

//--- Development History ----------------------------------------------------
//
//	$Log: StandardMessage.java,v $
//	Revision 1.1  2006/04/18 03:57:06  tames
//	Relocated implementation.
//	
//	
