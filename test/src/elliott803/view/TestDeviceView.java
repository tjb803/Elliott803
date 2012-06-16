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
    
    protected void setupTest() {
        readerView = new ReaderView(new Reader(testComputer, 1), 1);
        punchView = new PunchView(new Punch(testComputer, 2), 2);
        testView.add(readerView);
        testView.add(punchView);
        punchView.setLocation(400, 0);
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
