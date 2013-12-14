/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.util.Random;

import elliott803.hardware.Console;

/**
 * Unit test for the operator console view
 * 
 * @author Baldwin
 */
public class TestConsoleView extends BaseViewTest {
    
    protected void setupTest() {
        consoleView = new ConsoleView(new Console(testComputer));
        testView.add(consoleView);
    }
    
    private ConsoleView consoleView;

    public void testRandom() throws Exception {
        Random rand = new Random();
        int count = 0;
        while (++count > 0) {
            consoleView.updateLights(rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean());
            consoleView.updateVolume(count%50>25, (count/5)%100);
            Thread.sleep(100);
        }
    }    
}
