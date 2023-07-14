//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	Development history is located at the end of the file.
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

package gov.nasa.gsfc.commons.types.namespaces;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.properties.beans.AbstractConstrainedBean;


/**
 *  A NamedBean is a bean that has a constrained name property.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version $Date: 2006/01/24 20:09:33 $
 *  @author Carl F. Hostetter
**/

public abstract class AbstractNamedBean extends AbstractConstrainedBean 
	implements NamedBean
{
	private static final String CLASS_NAME = AbstractNamedBean.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private String fName;
	
	
	/**
	 *  Default constructor of a new NamedBean.
	 *
	 **/

	public AbstractNamedBean()
	{
		this((String) null);
	}

	
	/**
	 *  Constructs a new NamedBean having the given name but with an anonymous 
	 *  Creator.
	 *
	 *  @param name The desired name of the new NamedBean
	 **/

	public AbstractNamedBean(String name)
	{
		if (name == null)
		{
			fName = HasName.DEFAULT_NAME;
		}
		else
		{
			fName = name;
		}
	}

	
	/**
	 * Constructs a new NamedBean that is a clone of the given NamedBean. 
	 * 
	 * @param bean The NamedBean to clone
	 */
	
	protected AbstractNamedBean(NamedBean bean)
	{
		super((AbstractNamedBean) bean);
		
		fName = bean.getName();
	}
	
	
	/**
	 * Returns a clone of this NamedBean.
	 * 
	 */
	
	protected Object clone()
	{
		AbstractNamedBean clone = (AbstractNamedBean) super.clone();
			
		clone.fName = fName;
		
		return (clone);
	}
	
	
	/**
	 * Attempts to set the name of this NamedBean to the given name. If the change 
	 * is vetoed by any VetoableChangeListener on this NamedBean, no change is made, 
	 * and a PropertyVetoException is thrown. Otherwise, the change is reported 
	 * to any name property change listeners.
	 *
	 * @param name The new name of this NamedBean
	 * @throws PropertyVetoException if the attempted name change is vetoed
	 **/
	
	public void setName(String name)
		throws PropertyVetoException
	{
		String oldName = fName;
		fName = name; 
		
		if (fName == null)
		{
			fName = HasName.DEFAULT_NAME;
		}
		
		if (name != fName)
		{
			try
			{
				fireVetoableChange(NAME_PROPERTY, oldName, fName);
			}
			catch (PropertyVetoException ex)
			{
				String message = "Unable to set name of Object to " + name;
				
				if (sLogger.isLoggable(Level.WARNING))
				{
					sLogger.logp(Level.WARNING, CLASS_NAME, "setName", message, ex);
				}
				
				fName = oldName;
				
				throw (ex);
			}
		}
	}
	
	
	/**
	 *  Returns the name of this NamedBean.
	 *
	 *  @return	The name of this NamedBean
	 **/

	public String getName()
	{
		return (fName);
	}
	
	
	/**
	 * Adds the given PropertyChangeListener as a listener for changes in the name 
	 * of this NamedBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void addNameListener(PropertyChangeListener listener)
	{
		addPropertyChangeListener(NAME_PROPERTY, listener);
	}


	/**
	 * Removes the given PropertyChangeListener as a listener for changes in the 
	 * name of this NamedBean.
	 *
	 * @param listener A PropertyChangeListener
	 **/
	
	public void removeNameListener(PropertyChangeListener listener)
	{
		removePropertyChangeListener(NAME_PROPERTY, listener);
	}
	
	
	/**
	 * Adds the given VetoableChangeListener as a listener for changes in the name 
	 * of this NamedBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void addVetoableNameListener(VetoableChangeListener listener)
	{
		addVetoableChangeListener(NAME_PROPERTY, listener);
	}


	/**
	 * Removes the given VetoableChangeListener as a listener for changes in the 
	 * name of this NamedBean.
	 *
	 * @param listener A VetoableChangeListener
	 **/
	
	public void removeVetoableNameListener(VetoableChangeListener listener)
	{
		removeVetoableChangeListener(NAME_PROPERTY, listener);
	}
}

//--- Development History ----------------------------------------------------
//
// $Log: &
//
