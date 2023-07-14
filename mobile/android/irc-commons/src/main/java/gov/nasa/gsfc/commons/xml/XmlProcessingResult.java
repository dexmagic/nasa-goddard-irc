//=== File Prolog ============================================================
//  This code was developed by Commerce One and NASA Goddard Space
//  Flight Center, Code 588 for the Instrument Remote Control (IRC)
//  project.
//
//--- Notes ------------------------------------------------------------------
//
//--- Development History  ---------------------------------------------------
//
//  $Log: XmlProcessingResult.java,v $
//  Revision 1.2  2004/07/12 14:26:24  chostetter_cvs
//  Reformatted for tabs
//
//  Revision 1.1  2004/05/12 21:55:40  chostetter_cvs
//  Further tweaks for new structure, design
//
//
//--- Warning ----------------------------------------------------------------
//  This software is property of the National Aeronautics and Space
//  Administration. Unauthorized use or duplication of this software is
//  strictly prohibited. Authorized users are subject to the following
//  restrictions:
//  *  Neither the author, their corporation, nor NASA is responsible for
//	 any consequence of the use of this software.
//  *  The origin of this software must not be misrepresented either by
//	 explicit claim or by omission.
//  *  Altered versions of this software must be plainly marked as such.
//  *  This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.commons.xml;

import org.jdom.Document;


/**
 * The class contains XML processing results for more sophisticated clients.
 * The Document provided by this class is simply a JDOM Document. The subtree
 * map is simply a HashMap of JDOM elements and their corresponding source
 * URL.
 *
 * This code was developed for NASA, Goddard Space Flight Center, Code 588
 * for the Instrument Remote Control (IRC) project. <P>
 *
 * @version $Date: 2004/07/12 14:26:24 $
 * @author John Higinbotham 
**/

public class XmlProcessingResult 
{
	private Document fDocument	 = null;
	private SubtreeMap fSubtreeMap = null;

//---------------------------------------------------------

	/**
	 * Constructor.
	 *
	**/
	public XmlProcessingResult()
	{
	}

	/**
	 * Set document. 
	 * 
	 * @param document Document
	**/
	public void setDocument(Document document)
	{
		fDocument = document;
	}

	/**
	 * Get document. 
	 * 
	 * @return Document 
	**/
	public Document getDocument()
	{
		return fDocument;
	}

	/**
	 * Set subtree map. 
	 * 
	 * @param subtreeMap SubtreeMap 
	**/
	public void setSubtreeMap(SubtreeMap subtreeMap)
	{
		fSubtreeMap = subtreeMap;
	}

	/**
	 * Get subtree map. 
	 * 
	 * @return SubtreeMap 
	**/
	public SubtreeMap getSubtreeMap()
	{
		return fSubtreeMap;
	}
}
