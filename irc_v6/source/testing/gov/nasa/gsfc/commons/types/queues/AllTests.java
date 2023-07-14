/*
 * Created on Dec 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.nasa.gsfc.commons.types.queues;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author maher
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "Test for gov.nasa.gsfc.commons.testing.types.queues");
        //$JUnit-BEGIN$
        suite.addTestSuite(DequeuerTest.class);
        //$JUnit-END$
        return suite;
    }
}
