//=== File Prolog ============================================================
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//	$Log: PreferenceField.java,v $
//	Revision 1.2  2004/12/01 21:50:58  chostetter_cvs
//	Organized imports
//	
//	Revision 1.1  2004/08/23 13:59:10  tames
//	Initial Version
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

package gov.nasa.gsfc.irc.gui.swing;

import javax.swing.JTextField;

import gov.nasa.gsfc.irc.app.Irc;

/**
 * @version	$Date: 2004/12/01 21:50:58 $
 * @author T. Ames
 */
public class PreferenceField extends JTextField
{
	private String fKey = null;

	/**
	 * 
	 */
	public PreferenceField()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param key
	 */
	public PreferenceField(String key)
	{
		super(Irc.getPreference(key));
		fKey = key;
	}

	/**
	 * @param key
	 */
	public void setKey(String key)
	{
		fKey = key;
		super.setText(Irc.getPreference(key));
	}

	/**
	 * @param text
	 */
	public void setText(String text)
	{
		if (fKey != null)
		{
			super.setText(text);
			Irc.getPreferenceManager().setPreference(fKey, text);
		}
	}
}
