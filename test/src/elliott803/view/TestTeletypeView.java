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
            String prefix = count++ + ": ";
            for (int i = 0; i < prefix.length(); i++) {
                ttView.setChar(prefix.charAt(i));
            }
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
    
    public void testLongLines() throws Exception {
        int count = 0;
        while (true) {
            char ch = (char)('0' + count%10);
            ttView.setChar(ch);
            Thread.sleep(5);
            count++;
        }
    }
}
