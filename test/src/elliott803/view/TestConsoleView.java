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
    protected void setUp() {
        super.setUp();
        
        // Create the store display component and add to the frame.
        consoleView = new ConsoleView(new Console(testComputer));
        testFrame.getContentPane().add(consoleView);
        testFrame.setVisible(true);
    }
    
    private ConsoleView consoleView;

    public void testRandom() throws Exception {
        Random rand = new Random();
        while (true) {
            consoleView.updateLights(rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean());
            Thread.sleep(100);
        }
    }    
}