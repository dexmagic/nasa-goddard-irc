//=== File Prolog ============================================================
//
//  $Header: /cvs/IRCv6/Dev/source/core/gov/nasa/gsfc/irc/data/selection/BasisSetDataSelector.java,v 1.5 2006/01/23 17:59:54 chostetter_cvs Exp $
//
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetDataSelector.java,v $
//  Revision 1.5  2006/01/23 17:59:54  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.4  2005/09/30 20:55:48  chostetter_cvs
//  Fixed TypeMap issues with data parsing and formatting; various other code cleanups
//
//  Revision 1.3  2005/09/14 21:31:18  chostetter_cvs
//  Fixed BasisBundle name issue in DataSpace
//
//  Revision 1.2  2005/09/14 20:14:47  chostetter_cvs
//  Added ability to use context information to disambiguate (yes, Bob, disambiguate) BasisSet data selection
//
//  Revision 1.1  2005/09/08 22:18:32  chostetter_cvs
//  Massive Data Transformation-related changes
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

package gov.nasa.gsfc.irc.data.selection;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.gsfc.commons.types.namespaces.Namespaces;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.data.BasisBundle;
import gov.nasa.gsfc.irc.data.BasisBundleSource;
import gov.nasa.gsfc.irc.data.BasisSet;
import gov.nasa.gsfc.irc.data.DataSpace;
import gov.nasa.gsfc.irc.data.selection.description.BasisSetDataSelectorDescriptor;


/**
 * A BasisSetDataSelector selects a BasisSet from a BasisBundle in the 
 * Dataspace.
 *
 * <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 * for the Instrument Remote Control (IRC) project.
 *
 * @version $Date: 2006/01/23 17:59:54 $
 * @author Carl F. Hostetter
 */

public class BasisSetDataSelector extends NamedDataSelector
{
	private static final String CLASS_NAME = BasisSetDataSelector.class.getName();
	private static final Logger sLogger = Logger.getLogger(CLASS_NAME);
	
	private static final DataSpace sDataSpace = Irc.getDataSpace();
	
	private String fBasisBundleName;
	
	private BasisBundle fBasisBundle;
	
	
	/**
	 * Constructs a new BasisSetDataSelector that will select the BasisSet 
	 * indicated by the given BasisSetDataSelectorDescriptor.
	 *
	 * @param descriptor A BasisSetDataSelectorDescriptor
	 * @return A new BasisSetDataSelector that will select the BasisSet 
	 * 		indicated by the given BasisSetDataSelectorDescriptor		
	**/
	
	public BasisSetDataSelector(BasisSetDataSelectorDescriptor descriptor)
	{
		super(descriptor);
		
		fBasisBundleName = getName();
	}
	

	/**
	 *  Returns the BasisSet selected by this BasisSetDataSelector.
	 *  
	 *  @param data Here used only to pass in an optional Map of BasisBundle context 
	 *  		information
	 *  @return The BasisSet selected by this BasisSetDataSelector
	 */
	
	public Object select(Object data)
	{
		BasisSet result = null;
		
		if (fBasisBundle == null)
		{
			fBasisBundle = sDataSpace.getBasisBundle(fBasisBundleName);
			
			if (fBasisBundle == null)
			{
				if (sLogger.isLoggable(Level.FINE))
				{
					String message = "No BasisBundle is named " + fBasisBundleName + 
						"; applying context...";
					
					sLogger.logp(Level.FINE, CLASS_NAME, "select", message);
				}
				
				if ((data != null) && (data instanceof Map))
				{
					Map context = (Map) data;
					
					BasisBundleSource source = (BasisBundleSource) 
						context.get(BasisBundleSource.BASIS_BUNDLE_SOURCE_KEY);
					
					if (source != null)
					{
						String sourceName = source.getFullyQualifiedName();
						
						fBasisBundleName = Namespaces.formFullyQualifiedName
							(fBasisBundleName, sourceName);
						
						fBasisBundle = sDataSpace.getBasisBundle(fBasisBundleName);
					}
				}
			}
		}
		
		if (fBasisBundle != null)
		{
			result = fBasisBundle.allocateBasisSet(1);
		}
		else if (sLogger.isLoggable(Level.SEVERE))
		{
			String message = "No BasisBundle is named " + fBasisBundleName;
			
			sLogger.logp(Level.SEVERE, CLASS_NAME, "select", message);
		}
		
		return (result);
	}
}
