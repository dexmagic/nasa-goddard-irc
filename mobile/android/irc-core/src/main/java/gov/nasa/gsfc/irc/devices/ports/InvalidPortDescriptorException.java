//=== File Prolog ============================================================
//	This code was developed by Century Computing and NASA Goddard Space
//	Flight Center, Code 580 for the Instrument Remote Control (IRC)
//	project.
//--- Notes ------------------------------------------------------------------
//--- Development History:
//	$Log: InvalidPortDescriptorException.java,v $
//	Revision 1.1  2004/10/14 15:22:16  chostetter_cvs
//	Port descriptor-oriented refactoring
//	
//	Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//	Extensive descriptor-oriented refactoring
//	
//	Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Reformatted for tabs
//	
//	Revision 1.1  2004/04/30 20:31:16  tames_cvs
//	Relocated
//	
//	Revision 1.1.2.1  2004/02/03 20:04:40  chostetter_cvs
//	Massive package restructuring
//	
//	Revision 1.2  2003/10/24 08:42:29  mnewcomb_cvs
//	Added $Log: InvalidPortDescriptorException.java,v $
//	Added Revision 1.1  2004/10/14 15:22:16  chostetter_cvs
//	Added Port descriptor-oriented refactoring
//	Added
//	Added Revision 1.1  2004/10/14 15:16:51  chostetter_cvs
//	Added Extensive descriptor-oriented refactoring
//	Added
//	Added Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//	Added Reformatted for tabs
//	Added
//	Added Revision 1.1  2004/04/30 20:31:16  tames_cvs
//	Added Relocated
//	Added
//	Added Revision 1.1.2.1  2004/02/03 20:04:40  chostetter_cvs
//	Added Massive package restructuring
//	Added flag to the header of these files..
//	
//  ---------------------------------------------------
//
//  11/17/98	Lisa Koons
//	  Initial version.
//	04/14/99	T. Ames
//		Moved from nested class to separate file.
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


package gov.nasa.gsfc.irc.devices.ports;


/**
 *  The InvalidPortDescriptorException class is an Exception that is
 *  thrown when an invalid PortDescriptor is specified for this
 *  port (e.g., the PortDescriptor is null).
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center,
 *  Code 580 for the Instrument Remote Control (IRC) project.
 *
 *  @version	11/17/1998
 *  @author		Lisa Koons
**/
public class InvalidPortDescriptorException extends Exception
{
	/**
	 *  Default constructor of an InvalidPortDescriptorException.
	 *
	**/
	public InvalidPortDescriptorException()
	{

	}

	/**
	 *  Constructs an InvalidPortDescriptorException with the given
	 *  Exception detail message.
	 *
	 *  @param message An Exception detail message
	 **/
	public InvalidPortDescriptorException(String message)
	{
		super(message);
	}
}

