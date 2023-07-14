//=== File Prolog =============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 588
//	for the Scientist's Expert Assistant (SEA) project.
//
//--- Contents ----------------------------------------------------------------
//	interface ObjectClippable
//
//--- Description -------------------------------------------------------------
//	Classes that wish to potentially send or receive objects to/from the
//	object clipboard should implement this interface.  Normally this would
//	be GUI components, but that's not required.  The BrowserFrame's
//	Cut, Copy, Paste, and Select All commands will automatically call the
//	appropriate ObjectClippable method if the focused GUI component
//	(or its ancestor) implements ObjectClippable.
//
//--- Notes -------------------------------------------------------------------
//
//--- Development History -----------------------------------------------------
//
//	03/11/99	J. Jones / 588
//
//		Original implementation.
//
//--- DISCLAIMER---------------------------------------------------------------
//
//	This software is provided "as is" without any warranty of any kind, either
//	express, implied, or statutory, including, but not limited to, any 
//	warranty that the software will conform to specification, any implied 
//	warranties of merchantability, fitness for a particular purpose, and 
//	freedom from infringement, and any warranty that the documentation will 
//	conform to the program, or any warranty that the software will be error 
//	free.
//
//	In no event shall NASA be liable for any damages, including, but not 
//	limited to direct, indirect, special or consequential damages, arising out 
//	of, resulting from, or in any way connected with this software, whether or 
//	not based upon warranty, contract, tort or otherwise, whether or not 
//	injury was sustained by persons or property or otherwise, and whether or 
//	not loss was sustained from or arose out of the results of, or use of, 
//	their software or services provided hereunder.
//
//=== End File Prolog =========================================================

package gov.nasa.gsfc.commons.types;

/**
 * Classes that wish to potentially send or receive objects to/from the
 * object clipboard should implement this interface.  Normally this would
 * be GUI components, but that's not required.  The BrowserFrame's
 * Cut, Copy, Paste, and Select All commands will automatically call the
 * appropriate ObjectClippable method if the focused GUI component
 * (or its ancestor) implements ObjectClippable.
 *
 * <P>This code was developed by NASA, Goddard Space Flight Center, Code 588
 * for the Scientist's Expert Assistant (SEA) project.
 *
 * @version		03/11/99
 * @author		J. Jones / 588
**/
public interface ObjectClippable
{
	/**
	 * This method instructs the called object to remove its selected object(s)
	 * and send them to the clipboard.
	**/
    public void cutObjects();
    
	/**
	 * This method instructs the called object to send its selected object(s)
	 * to the clipboard.
	**/
    public void copyObjects();
    
	/**
	 * This method instructs the called object to retrieve the set of objects
	 * currently on the clipboard and somehow apply them to the current selection.
	**/
    public void pasteObjects();
    
	/**
	 * This method instructs the called object to select all of its potentially
	 * clippable objects.  In many cases, this method might not make sense for
	 * a particular implementation in which case it would just be ignored.
	**/
    public void selectAllObjects();
}
