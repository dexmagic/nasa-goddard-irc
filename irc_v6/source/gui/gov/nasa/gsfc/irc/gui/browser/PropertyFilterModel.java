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

package gov.nasa.gsfc.irc.gui.browser;

import javax.swing.DefaultComboBoxModel;

import gov.nasa.gsfc.commons.types.collections.ObjectPair;

/**
 * Place holder class for a generic ComboBoxModel that supports localization.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version	$Date: 2005/01/27 21:58:32 $
 * @author 	Troy Ames
 */
public class PropertyFilterModel extends DefaultComboBoxModel
{
	public static final ListEntry ALL = 
		new ListEntry("all", "All");
	public static final ListEntry STANDARD = 
		new ListEntry("standard", "Standard");
	public static final ListEntry PREFERRED = 
		new ListEntry("preferred", "Preferred");
	public static final ListEntry READ_ONLY = 
		new ListEntry("readOnly", "Read Only");
	public static final ListEntry EXPERT = 
		new ListEntry("expert", "Expert");
	public static final ListEntry HIDDEN = 
		new ListEntry("hidden", "Hidden");
	public static final ListEntry CONSTRAINED = 
		new ListEntry("constrained", "Constrained");
	public static final ListEntry BOUND = 
		new ListEntry("bound", "Bound");
	
	private static Object[] fContent = new Object[] 
		{
			ALL,
			STANDARD,
			PREFERRED,
			EXPERT,
			READ_ONLY,
			BOUND,
			CONSTRAINED,
			HIDDEN
		};
			
	/**
	 * Creates a ComboBoxModel with a predefined set of items.
	 */
	public PropertyFilterModel()
	{
		super(fContent);
		setSelectedItem(STANDARD);
	}
	
	static class ListEntry extends ObjectPair
	{
		public ListEntry(Object name, Object displayName)
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
//  $Log: PropertyFilterModel.java,v $
//  Revision 1.1  2005/01/27 21:58:32  tames
//  Initial version
//
//  Revision 1.1  2005/01/20 08:08:14  tames
//  Changes to support choice descriptors and message editing bug fixes.
//
//