/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.util.Random;

import elliott803.hardware.Store;

/**
 * Unit test for the StoreView component
 * 
 * @author Baldwin
 */
public class TestStoreView extends BaseViewTest {
    protected void setUp() {
        super.setUp();
        
        // Create the store display component and add to the frame.
        storeView = new StoreView(new Store(testComputer));
        testFrame.getContentPane().add(storeView);
        testFrame.setVisible(true);
    }
    
    private StoreView storeView;

    public void testRandom() throws Exception {
        Random rand = new Random();
        while (true) {
            storeView.updateCore(rand.nextInt(8192), rand.nextLong() & 0x7FFFFFFFFFL);
            Thread.yield();
        }
    }
}
