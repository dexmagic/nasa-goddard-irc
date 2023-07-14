//=== File Prolog ============================================================
// This code was developed by NASA Goddard Space Flight Center, Code 588 for 
// the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//  Development history is located at the end of the file.
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	   any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	   explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.data.description;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;

import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.InvalidValueException;
import gov.nasa.gsfc.irc.description.Descriptor;
import gov.nasa.gsfc.irc.description.xml.DescriptorDirectory;


/**
 * A ValueDescriptor describes an abstract value.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2006/01/23 17:59:50 $ 
 * @author Carl F. Hostetter 
**/

public class ValueDescriptor extends DataFeatureDescriptor
{
	private static final String CLASS_NAME = 
		ValueDescriptor.class.getName();
	
	private static final Logger sLogger = 
		Logger.getLogger(CLASS_NAME);

	private String fUnits = "none";   // Units of value
	private Object fDefaultValue = null;   // Default value object
	
	private boolean fIsArray = false;  // True if value actually an array 
	private int[] fDimensions;   // Dimensions of value 
	
	private boolean fIsReadOnly = false;  // True if value is readonly 

	private Map fConstraintDescriptorsByName;
	

	/**
	 * Constructs a new ValueDescriptor having the given parent Descriptor 
	 * and belonging to the given DescriptorDirectory by unmarshalling from the 
	 * given JDOM Element.
	 *
	 * @param parent The parent Descriptor of the new ValueDescriptor 
	 * 		(if any)
	 * @param directory The DescriptorDirectory to which the new 
	 * 		ValueDescriptor will belong
	 * @param element The JDOM Element from which to unmarshall the new 
	 * 		ValueDescriptor		
	**/
	
	public ValueDescriptor(Descriptor parent, DescriptorDirectory directory, 
		Element element)
	{
		super(parent, directory, element, Dataml.N_DATA);
		
		fConstraintDescriptorsByName = new LinkedHashMap();
		
		xmlUnmarshall();
	}
	

	/**
	 * Constructs a new ValueDescriptor having the given name.
	 * 
	 * @param name The name of the new ValueDescriptor
	**/
	
	public ValueDescriptor(String name)
	{
		super(name);
		
		fConstraintDescriptorsByName = new LinkedHashMap();
	}

	
	/**
	 * Determine if the described value is read-only.  
	 *
	 * @return true if readonly, false otherwise. 
	**/
	
	public boolean isReadOnly()
	{
		return (fIsReadOnly);
	}
	

	/**
	 * Set Value readonly flag.  
	 *
	 * @param flag True if readonly, false otherwise. 
	**/
	
	public void setIsReadOnly(boolean flag)
	{
		fIsReadOnly = flag;
	}


	/**
	 * Returns the units of the described value. 
	 *
	 * @return The units of the described value 
	**/
	
	public String getUnits()
	{
		return fUnits;
	}
	

	/**
	 * Set the units of the described value. 
	 *
	 * @param units The units of the described value 
	**/
	
	public void setUnit(String units)
	{
		fUnits = units;
	}


	/**
	 * Returns the implementation Class of the value described by this 
	 * ValueDescriptor.
	 *
	 * @return The implementation Class of the value described by this 
	 * 		ValueDescriptor
	**/
	
	public Class getValueClass()
	{
		return (getTypeClass());
	}
	

	/**
	 * Determine if the Value is an array Value. 
	 *
	 * @return true if the Value is an array Value, false otherwise. 
	 *			
	**/
	public boolean isArray()
	{
		return (fIsArray); 
	}
	

	/**
	 * Mark the Value as an array Value. 
	 *
	 * @param flag True if the Value is an array Value, false otherwise. 
	**/
	
	public void setIsArray(boolean flag)
	{
		fIsArray = flag; 
	}


	/**
	 * Returns the default value (if any). 
	 *
	 * @return The default value (if any)	
	**/
	
	public Object getDefaultValue()
	{
		return (fDefaultValue);
	}
	

	/**
	 * Sets the default value. Callers should be careful to set 
	 * all the constraints prior to calling this method as this method will
	 * also validate the default against existing constraints.
	 *
	 * @param value The default value
	 * @throws InvalidValueValueException if value is not valid
	**/
	
	public void setDefault(Object value) 
		throws InvalidValueException
	{
		fDefaultValue = value;
		
		InvalidValueException e = validateDefault();
		
		if (e != null)
		{
			throw e;
		}
	}
	

	/**
	 * Get the dimensions of the Value. Note that a dimension of -1 is to
	 * be interpreted as a variable size array Value.
	 *
	 * @return Dimensions of the Value		
	**/
	
	public int[] getDimensions()
	{
		return (fDimensions);
	}


	/**
	 * Get the field constraints with the specified name. 
	 *
	 * @param  name Name of constraint. 
	 * @return Constraint with the specified name.		
	**/
	
	public ConstraintDescriptor getConstraintByName(String name)
	{
		return (ConstraintDescriptor) fConstraintDescriptorsByName.get(name);
	}
	

	/**
	 * Get the field's collection of constraint descriptors.
	 *
	 * @return All of the field's constraints descriptors	
	**/
	
	public Iterator getConstraints()
	{
		return fConstraintDescriptorsByName.values().iterator();
	}
	

	/**
	 * Add constraint.
	 *
	 * @param constraint ConstraintDescriptor to add.
	**/
	
	public void addConstraint(ConstraintDescriptor constraint)
	{
		String name = constraint.getName();
		
		if (name != null)
		{
			fConstraintDescriptorsByName.put(name, constraint);
		}
		else 
		{
			fConstraintDescriptorsByName.put(constraint, constraint);
		}
	}


	/**
	 * Helper method to validate that the default value does not violate the current constraints.
	 * If it is violate a constraint, an exception is published to the exception dispatcher and 
	 * a message is logged.
	 *
	 * @return Copy of exception thrown by field validator.
	**/
	
	private InvalidValueException validateDefault()
	{
		InvalidValueException rval = null;
//		if (fDefault != null)
//		{
//			try 
//			{
//				ConstrainedValueValidator.getInstance().validate(fDefault, this);
//			}
//			catch (InvalidConstrainedValueValueException ex)
//			{
//				rval = ex;
//				
//				if (sLogger.isLoggable(Level.SEVERE))
//				{
//					String message = "Default value conflicts with defined constraint for field: " + 
//						getName();
//					
//					sLogger.logp(Level.SEVERE, CLASS_NAME, 
//						"validateDefault", message, ex);
//				}
//			}
//		}
		return rval;
	}
	
	/**
	 * Unmarshall descriptor from XML. 
	 *
	**/
	
	private void xmlUnmarshall()
	{
		fIsReadOnly = fSerializer.loadBooleanAttribute(Dataml.A_READ_ONLY, false, 
			fElement);

		fUnits = fSerializer.loadStringAttribute(Dataml.A_UNITS, "none", fElement);

		String defaultString = 
			fSerializer.loadStringAttribute(Dataml.A_DEFAULT, null, fElement);
		
		if (defaultString != null)
		{
			try
			{
				fDefaultValue = 
					Irc.instantiateClass(
						getTypeClass(), new Object[] {defaultString});
			}
			catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		fDimensions = fSerializer.loadDimensionIntArrayElement
			(Dataml.E_DIMENSION, Dataml.A_SIZE,  fElement);
		
		if (fDimensions != null) 
		{
			fIsArray = true; 
		}
		
		fSerializer.loadChildDescriptorElements(Dataml.E_LIST_CONSTRAINT, 
			fConstraintDescriptorsByName,  Dataml.C_LIST_CONSTRAINT, fElement, this, 
			fDirectory);
		
		fSerializer.loadChildDescriptorElements(Dataml.E_RANGE_CONSTRAINT, 
			fConstraintDescriptorsByName, Dataml.C_RANGE_CONSTRAINT, fElement, this, 
			fDirectory);
		
		fSerializer.loadChildDescriptorElements(Dataml.E_BIT_RANGE_CONSTRAINT, 
			fConstraintDescriptorsByName, Dataml.C_BIT_RANGE_CONSTRAINT, fElement, 
			this, fDirectory);

		validateDefault();
	}
	

	/**
	 * Marshall descriptor to XML. 
	 *
	 * @param element Element to marshall into
	**/
	
	public void xmlMarshall(Element element)
	{
		super.xmlMarshall(element);

		fSerializer.storeAttribute(Dataml.A_READ_ONLY, fIsReadOnly, element);
		
		if (fUnits != null)
		{
			fSerializer.storeAttribute(Dataml.A_UNITS, fUnits.toString(), element);
		}

		if (fDefaultValue != null)
		{
			fSerializer.storeAttribute(Dataml.A_DEFAULT, fDefaultValue.toString(), 
				element);
		}

		// TODO: In the future we may want to support writing out dimensions. None 
		// of the xml currently evens uses this Value and we may want to reconsider 
		// its value here.

		Iterator i = fConstraintDescriptorsByName.values().iterator();
		
		while (i.hasNext())
		{
			Descriptor constraint = (Descriptor) i.next();

			if (constraint instanceof ListConstraintDescriptor)
			{
				fSerializer.storeDescriptorElement(Dataml.E_LIST_CONSTRAINT, 
					constraint, Dataml.C_LIST_CONSTRAINT, element, fDirectory);
			}
			else if (constraint instanceof RangeConstraintDescriptor)
			{
				fSerializer.storeDescriptorElement(Dataml.E_RANGE_CONSTRAINT, 
					constraint, Dataml.C_RANGE_CONSTRAINT, element, fDirectory);
			}
			else if (constraint instanceof BitRangeConstraintDescriptor)
			{
				fSerializer.storeDescriptorElement(Dataml.E_BIT_RANGE_CONSTRAINT, 
					constraint, Dataml.C_BIT_RANGE_CONSTRAINT, element, fDirectory);
			}
			else
			{
				if (sLogger.isLoggable(Level.SEVERE))
				{
					String message = 
						"Internal error marshalling constraint on Value descriptor";
					
					sLogger.logp(Level.SEVERE, CLASS_NAME, 
						"xmlMarshall", message);
				}
			}
		}
	}
}

//--- Development History  ---------------------------------------------------
//
//  $Log: ValueDescriptor.java,v $
//  Revision 1.10  2006/01/23 17:59:50  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.9  2005/12/01 20:56:40  tames_cvs
//  Reflects changes to the instantiateClass method signature.
//
//  Revision 1.8  2005/09/13 20:30:12  chostetter_cvs
//  Made various Descriptor classes into interface and abstract implementation pairs; implemented data logging in data transformation context
//
//  Revision 1.7  2005/01/21 21:53:19  tames
//  Updated to reflect removal of redundant getElementClass method in
//  AbstractIrcElementDescriptor.
//
//  Revision 1.6  2005/01/20 08:10:50  tames
//  Changes to support choice descriptors
//
//  Revision 1.5  2005/01/07 20:29:26  tames
//  Added support for bean feature like attributes.
//
//  Revision 1.4  2004/10/14 23:09:22  chostetter_cvs
//  Message, Adapter descriptor-related changes
//
//  Revision 1.3  2004/10/14 15:16:50  chostetter_cvs
//  Extensive descriptor-oriented refactoring
//
//  Revision 1.2  2004/10/07 21:38:26  chostetter_cvs
//  More descriptor refactoring, initial version of data transformation description
//
//  Revision 1.1  2004/10/01 15:47:40  chostetter_cvs
//  Extensive refactoring of field/property/argument descriptors
//
