/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import java.util.Random;

import elliott803.hardware.Punch;
import elliott803.hardware.Reader;

/**
 * Unit test for the reader and punch views
 * 
 * @author Baldwin
 */
public class TestDeviceView extends BaseViewTest {
    protected void setUp() {
        super.setUp();
        
        // Create the store display component and add to the frame.
        readerView = new ReaderView(new Reader(testComputer, 1), 1);
        punchView = new PunchView(new Punch(testComputer, 2), 2);
        testFrame.getContentPane().add(readerView);
        testFrame.getContentPane().add(punchView);
        testFrame.setVisible(true);
    }
    
    private ReaderView readerView;
    private PunchView punchView;

    public void testRandom() throws Exception {
        Random rand = new Random();
        while (true) {
            readerView.updateWait(rand.nextBoolean());
            Thread.sleep(100);
            punchView.updateWait(rand.nextBoolean());
            Thread.sleep(100);
        }
    }
}
