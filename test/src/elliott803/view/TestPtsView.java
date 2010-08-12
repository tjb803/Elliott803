/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2010
 */
package elliott803.view;

import elliott803.machine.PaperTapeStation;

/**
 * Unit test for the paper tape station view
 * 
 * @author Baldwin
 */
public class TestPtsView  extends BaseViewTest {
    
    protected void setupTest() {
        ptsView = new PtsView(new PaperTapeStation(testComputer));
        testView.add(ptsView);
    }

    private PtsView ptsView;
    
    public void testRandom() throws Exception {
        while (true) {
            Thread.sleep(50);
        }
    }
}
