/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2012
 */
package elliott803.view;

import java.util.Random;

/**
 * Unit test for the simulator control panel view
 * 
 * @author Baldwin
 */
public class TestControlView extends BaseViewTest {
    
    protected void setupTest() {
        controlView = new ControlView(testComputer, null);
        testView.add(controlView);
    }
    
    private ControlView controlView;

    public void testRandom() throws Exception {
        Random rand = new Random();
        while (true) {
            float sp = rand.nextFloat()*99999;
            controlView.speed.setText(String.format("%8.2f", sp));
            Thread.sleep(500);
        }
    }  

}
