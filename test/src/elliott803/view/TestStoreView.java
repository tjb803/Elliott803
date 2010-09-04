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
    
    protected void setupTest() {
        storeView = new StoreView(new Store(testComputer));
        testView.add(storeView);
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
