/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view.component;

import java.util.Random;

import elliott803.view.component.DisplayCore;

/**
 * Unit test for the DisplayCore component
 * 
 * @author Baldwin
 */
public class TestDisplayCore extends BaseComponentTest {
    
    protected void setupTest() {
        displayStore = new DisplayCore();
        testView.add(displayStore);
    }
    
    private DisplayCore displayStore;
    
    public void testRandom() throws Exception {
        Random rand = new Random();
        while (true) {
            displayStore.setValue(rand.nextInt(8192), 1);
            Thread.yield();
        }
    }
}
