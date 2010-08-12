/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2010
 */
package elliott803.view.component;

import java.awt.Dimension;


/**
 * Unit test for the DisplayPlot component
 * 
 * @author Baldwin
 */
public class TestDisplayPlot extends BaseComponentTest {
    
    protected void setupTest() {
        displayPlot = new DisplayPlot();
        testView.add(displayPlot);
        testView.setPreferredSize(new Dimension(400, 400));
    }
    
    private DisplayPlot displayPlot;
    
    public void testSquare() throws Exception {
        displayPlot.plotMove(250, -300);
        displayPlot.plotDraw(850, -300);
        displayPlot.plotDraw(850, 300);
        displayPlot.plotDraw(250, 300);
        displayPlot.plotDraw(250, -300);
        displayPlot.plotMove(0, 0, 0);
        while (true) {
            Thread.yield();
        }
    }
    
    public void testSine() throws Exception {
        displayPlot.plotMove(150, 0);
        for (int i = 0; i <= 360; i++) {
            int x = 150 + i*2;
            int y = (int)(300.0 * Math.sin(Math.toRadians(i)));
            displayPlot.plotDraw(x, y);
            Thread.sleep(10);
        }
        while (true) {
            Thread.yield();
        }
    }
}
