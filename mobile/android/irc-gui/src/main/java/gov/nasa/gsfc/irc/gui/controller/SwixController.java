package gov.nasa.gsfc.irc.gui.controller;

import java.awt.Component;

/**
 * Defines the interface used by the Swix parser for each swing element that 
 * specifies the implementing class as the control class.
 * 
 * @author H Beau Hollis
 * @author	Troy Ames
 */
public interface SwixController
{
	/**
	 * Called with the view or component to control or monitor.
	 * @param component the view to control.
	 */
	public void setView(Component component);
}