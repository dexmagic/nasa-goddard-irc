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

package gov.nasa.gsfc.irc.library.archiving.data;

import gov.nasa.gsfc.commons.numerics.types.Amount;
import gov.nasa.gsfc.irc.algorithms.DefaultAlgorithm;
import gov.nasa.gsfc.irc.algorithms.DefaultInput;
import gov.nasa.gsfc.irc.algorithms.Input;
import gov.nasa.gsfc.irc.algorithms.Processor;
import gov.nasa.gsfc.irc.app.Irc;
import gov.nasa.gsfc.irc.components.description.ComponentDescriptor;
import gov.nasa.gsfc.irc.data.BasisBundleId;
import gov.nasa.gsfc.irc.data.BasisRequest;
import gov.nasa.gsfc.irc.data.DataSpace;

/**
 *  BasisSetFileWriter adds the input and the processor to the algorithm.
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version Mar 2, 2005 10:40:46 AM
 *  @author Peyush Jain
 */

public class BasisSetFileWriter extends DefaultAlgorithm
{
//	private String fBasisBundleName = "DetectorData from Anonymous";
	private String fBasisBundleName = "SensorData.MarkIII";   // Last one here :) S. Maher	
	
    private BasisBundleId fBasisBundleId;
    private int fRequestAmount = 200;
    
    private DataSpace fDataSpace;
    private Input fInput; 
    
    /**
     * Default constructor of a BasisSetFileWriter.
     * 
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *
     */
    public BasisSetFileWriter()
    {
        this("BasisSet File Writer");
    }

    /**
     * Constructs a new writer having the given name.
     * 
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *  <p>Actually, looks the default works fine.  S. Maher
     *
     * @param name The name of the new archiver
     */
    public BasisSetFileWriter(String name)
    {
        super(name);
        
        init();
    }
        
    /**
     * Constructs a new writer configured according to the given Descriptor.
     * 
     *  <p>NOTE that the new Algorithm will need to have its ComponentManager set 
     *  (if any is desired).
     *  <p>Actually, looks the default works fine.  S. Maher     *  
     *
     * @param name The name of the new archiver
     * @param descriptor A Descriptor describing the desired configuration of
     *            the new writer
     */
    public BasisSetFileWriter(ComponentDescriptor descriptor)
    {
        super(descriptor);

        init();
    }

	/**
	 * 
	 */
	private void init()
	{
		fDataSpace = Irc.getDataSpace();
        
        //add input to the algorithm
        fInput = new DefaultInput();
        this.addInput(fInput);

        //add processor to the algorithm
        Processor processor = new BasisSetFileWriteProcessor();        
        this.addProcessor(processor);
        
        //add processor as the listener to input
        fInput.addInputListener(processor);
        
        //Input 1
//        BasisBundleId basisBundleId1 = 
//            dataSpace.getBasisBundleId(getBasisBundleId());//"DetectorData from Anonymous"
//        BasisRequestAmount requestAmount1 = new BasisRequestAmount();
//        requestAmount1.setAmount(200);//requestAmount.setUnit("s");//100
//        BasisRequest basisRequest1 = new BasisRequest(basisBundleId1, requestAmount1);
//        input.addBasisRequest(basisRequest1);    

        //Input 2
//        BasisBundleId basisBundleId2 = dataSpace.getBasisBundleId
//            ("Signals from Output Signals of Algorithm Signal Generator");
//        BasisRequestAmount requestAmount = new BasisRequestAmount();
//        requestAmount.setAmount(100);
//        BasisRequest basisRequest2 = new BasisRequest(basisBundleId2, requestAmount);
//        input.addBasisRequest(basisRequest2);
	}

    /**
     * @return Returns the basisBundleId.
     */
    public String getBasisBundleName()
    {
        return fBasisBundleName;
    }

    /**
     * @param basisBundleId The basisBundleId to set.
     */
    public void setBasisBundleName(String basisBundleName)
    {
        fBasisBundleName = basisBundleName;
    }

    /**
     * @return Returns the requestAmount.
     */
    public int getRequestAmount()
    {
        return fRequestAmount;
    }

    /**
     * @param requestAmount The requestAmount to set.
     */
    public void setRequestAmount(int requestAmount)
    {
        fRequestAmount = requestAmount;
    }

    /**
     * @see gov.nasa.gsfc.irc.components.DefaultComposite#start()
     */
    public void start()
    {
        super.start();

        //get the basisBundleId
        fBasisBundleId = fDataSpace.getBasisBundleId(getBasisBundleName());
        
        //set the request amount
        Amount reqAmt = new Amount();
        reqAmt.setAmount(getRequestAmount());
        
        //create a basis request
        BasisRequest basisRequest = new BasisRequest(fBasisBundleId, reqAmt);
        fInput.addBasisRequest(basisRequest);    
    }

    /**
     * @see gov.nasa.gsfc.irc.components.DefaultComposite#stop()
     */
    public void stop()
    {
        super.stop();
        
        fInput.removeBasisRequests();
    }
}


//--- Development History  ---------------------------------------------------
//
//  $Log: BasisSetFileWriter.java,v $
//  Revision 1.9  2006/06/12 14:10:55  smaher_cvs
//  Made sure all constructors intialize processors, inputs, outputs, etc.
//
//  Revision 1.8  2006/03/14 16:13:18  chostetter_cvs
//  Removed adding Algorithms to default ComponentManger by default, updated docs to reflect, fixed BasisBundle name update bug
//
//  Revision 1.7  2006/03/14 14:57:16  chostetter_cvs
//  Simplified Namespace, Manager, Component-related constructors
//
//  Revision 1.6  2006/01/23 17:59:55  chostetter_cvs
//  Massive Namespace-related changes
//
//  Revision 1.5  2005/10/14 21:47:51  pjain_cvs
//  Added basisBundleName and requestAmount fields along with their getters
//  and setters. Also, added start and stop methods.
//
//  Revision 1.4  2005/10/14 13:57:30  pjain_cvs
//  Removed CLASS_NAME and sLogger fields.
//
//  Revision 1.3  2005/08/29 21:52:51  pjain_cvs
//  Changed constructor access from private to public.
//
//  Revision 1.2  2005/06/15 18:42:37  pjain_cvs
//  Changed the request amount for Input1 to 200
//
//  Revision 1.1  2005/05/02 15:03:37  pjain_cvs
//  Adding to CVS.
//
//