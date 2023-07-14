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

package gov.nasa.gsfc.irc.gui.messages;

import javax.swing.DefaultComboBoxModel;

import gov.nasa.gsfc.commons.types.collections.ObjectPair;

/**
 * Place holder class for a generic ComboBoxModel
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/20 08:08:14 $
 * @author 	Troy Ames
 */
public class ArgumentFilterModel extends DefaultComboBoxModel
{
	public static final ArgumentEntry ALL = 
		new ArgumentEntry("all", "All");
	public static final ArgumentEntry STANDARD = 
		new ArgumentEntry("standard", "Standard");
	public static final ArgumentEntry PREFERRED = 
		new ArgumentEntry("preferred", "Preferred");
	public static final ArgumentEntry READ_ONLY = 
		new ArgumentEntry("readOnly", "Read Only");
	public static final ArgumentEntry EXPERT = 
		new ArgumentEntry("expert", "Expert");
	public static final ArgumentEntry HIDDEN = 
		new ArgumentEntry("hidden", "Hidden");
	
	private static Object[] fContent = new Object[] 
		{
			ALL,
			STANDARD,
			PREFERRED,
			EXPERT,
			READ_ONLY,
			HIDDEN
		};
			
	/**
	 * Creates a ComboBoxModel with a predefined set of items.
	 */
	public ArgumentFilterModel()
	{
		super(fContent);
		setSelectedItem(STANDARD);
	}
	
	static class ArgumentEntry extends ObjectPair
	{
		public ArgumentEntry(Object name, Object displayName)
		{
			super(name, displayName);
		}

		public String toString()
		{
			return super.second.toString();
		}
	}
}

//--- Development History  ---------------------------------------------------


//--- Development History  ---------------------------------------------------
//
//  $Log: ArgumentFilterModel.java,v $
//  Revision 1.1  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//