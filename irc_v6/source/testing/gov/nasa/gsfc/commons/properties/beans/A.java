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

package gov.nasa.gsfc.commons.properties.beans;

public class A 
{
	public A() {}		
	int a = -1;

	/**
	 * @return Returns the a.
	 */
	public int getA()
	{
		return a;
	}

	/**
	 * @param a The a to set.
	 */
	public void setA(int a)
	{
		this.a = a;
	}
}
//
//--- Development History  ---------------------------------------------------
//
//	$Log: A.java,v $
//	Revision 1.1  2006/01/11 15:45:39  smaher_cvs
//	Polished some bean property methods by changing to better name and exposing exceptions correctly.
//	