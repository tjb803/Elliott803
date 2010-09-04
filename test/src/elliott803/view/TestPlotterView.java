/**
 * Elliott Model 803B Simulator
 * 
 * (C) Copyright Tim Baldwin 2010
 */
package elliott803.view;

import elliott803.hardware.Plotter;

/**
 * Unit test for the plotter view
 * 
 * @author Baldwin
 */
public class TestPlotterView  extends BaseViewTest {
    
    protected void setupTest() {
        // Create the store display component and add to the frame.
        pView = new PlotterView(new Plotter(testComputer));
        pView.setSize(400, 200);
        testView.add(pView);
        
        lastX = lastY = 0;
    }

    private PlotterView pView;
    private int lastX, lastY;
    
    public void testSquare() throws Exception {
        pView.penMove(250, -300, 0);
        pView.penDraw(850, -300, 0);
        pView.penDraw(850, 300, 0);
        pView.penDraw(250, 300, 0);
        pView.penDraw(250, -300, 0);
        pView.penMove(0, 0, 0);
        while (true) {
            Thread.yield();
        }
    }
    
    public void testSqaureWave() throws Exception {
        plotTo(150, 0, false);
        plotTo(150, 300, true);
        plotTo(450, 300, true);
        plotTo(450, -300, true);
        plotTo(750, -300, true);
        plotTo(750, 0, true);
        plotTo(0, 0, false);
        while (true) {
            Thread.yield();
        }
    }
    
    public void testAsyncWave() throws Exception {
        plotTo(150, 0, false);
        plotTo(150, 200, true);
        plotTo(450, 200, true);
        plotTo(450, -400, true);
        plotTo(750, -400, true);
        plotTo(750, 0, true);
        plotTo(0, 0, false);
        while (true) {
            Thread.yield();
        }
    }

    private void plotTo(int x, int y, boolean draw) throws Exception {
        int incX = (x > lastX) ? 1 : (x < lastX) ? -1 : 0;
        int incY = (y > lastY) ? 1 : (y < lastY) ? -1 : 0;
        int dir = incY*10 + incX;
        
        int start = (x != lastX) ? lastX : lastY;
        int stop = (x != lastX) ? x : y;
        int inc = (x != lastX) ? incX : incY;
        while (start != stop) {
            start += inc;
            lastX += incX;  lastY += incY;
            if (draw)
                pView.penDraw(lastX, lastY, dir);
            else
                pView.penMove(lastX, lastY, dir);
            Thread.sleep(5);
        }
    }
}
