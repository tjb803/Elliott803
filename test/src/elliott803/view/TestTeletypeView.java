/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.util.Random;

import elliott803.hardware.Teletype;

/**
 * Unit test for the teletype view
 * 
 * @author Baldwin
 */
public class TestTeletypeView extends BaseViewTest {
    
    protected void setupTest() {
        // Create the store display component and add to the frame.
        ttView = new TeletypeView(new Teletype(testComputer, 3));
        testView.add(ttView);
    }
    
    private TeletypeView ttView;

    public void testRandom() throws Exception {
        Random rand = new Random();
        int count = 0;
        while (true) {
            ttView.setText(count++ + ": ");
            for (int i = 0; i < 5+rand.nextInt(72); i++) {
                int j = rand.nextInt(35);
                char ch = (j < 26) ? (char)('A'+j) : ' ';
                ttView.setChar(ch);
                Thread.sleep(20);
            }
            ttView.setChar('\n');  
            Thread.sleep(20);
        }
    }
}
