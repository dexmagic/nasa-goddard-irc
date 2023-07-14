//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
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

package gov.nasa.gsfc.irc.messages.description;

import gov.nasa.gsfc.irc.app.Irc;

/**
 *	AbstractMessageDescriptorAdapter is a JavaBean representation of a
 *  AbstractMessageDescriptor. It contains command and instrument properties
 *  which can be set to identify the command.  findCommand() will return the corresponding descriptor.
 *
 *	@version	$Date: 2005/02/01 18:37:42 $
 *	@author	Jeremy Jones
 *  @autoor John Higinbotham (Emergent Space Technologies)
**/
public class AbstractMessageDescriptorAdapter
{
	private String	fMessageName;
	private String	fDeviceName;
	
	/**
	 * Creates a new CommandDescriptorAdapter bean.
	**/
	public AbstractMessageDescriptorAdapter()
	{
		fMessageName = null;
		fDeviceName = null;
	}
	
	/**
	 * Returns the name of the command to locate.
	 * 
	 * @return	name of command or command procedure
	**/
	public String getMessageName()
	{
		return fMessageName;
	}

	/**
	 * Sets the name of the command to locate.
	 * 
	 * @param	commandName	name of command or command procedure
	**/
	public void setMessageName(String commandName)
	{
		fMessageName = commandName;
	}

	/**
	 * Returns the name of the instrument which contains the command description.
	 * 
	 * @return	name of the instrument
	**/
	public String getDeviceName()
	{
		return fDeviceName;
	}

	/**
	 * Sets the name of the instrument which contains the command description.
	 * 
	 * @param	name	name of instrument that contains the command
	**/
	public void setDeviceName(String name)
	{
		fDeviceName = name;
	}
	
	/**
	 * Locates the CommandDescriptor for the adapter and returns it, or null
	 * if no such descriptor is found.
	 * 
	 * @return	CommandDescriptor for the adapter
	**/
	public MessageDescriptor getMessageDescriptor()
	{		
		return Irc.getDescriptorLibrary().getMessageDescriptor(fMessageName);
	}
}

//--- Development History  ---------------------------------------------------
//
//	$Log: AbstractMessageDescriptorAdapter.java,v $
//	Revision 1.4  2005/02/01 18:37:42  tames
//	Changes to reflect DescriptorLibrary changes.
//	
//	Revision 1.3  2004/09/28 19:26:32  tames_cvs
//	Reflects changing the name of Instrument related classes and methods
//	to Device since a device can include sensors, software, simulators etc.
//	Instrument maybe used in the future for a specific device type.
//	
//	Revision 1.2  2004/09/27 22:19:02  tames
//	Reflects the relocation of Instrument descriptors and
//	implementation classes to the devices package.
//	
//	Revision 1.1  2004/09/16 21:14:39  jhiginbotham_cvs
//	Port from IRC v5.
//	
