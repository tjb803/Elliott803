/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2010, 2012
 */
package elliott803.hardware;

import elliott803.hardware.device.ControlDevice;
import elliott803.machine.Computer;
import elliott803.view.PlotterView;

/**
 * The CalComp plotter.  The plotter has paper 11 inches wide and can move the pen
 * in 1/100 inch increments in each direction (including diagonals).  The basic 
 * plotter device doesn't really do anything, the GUI viewer does all the work.
 * 
 * @author Baldwin
 */
public class Plotter extends ControlDevice {
    
    static final int X_MIN = 0;
    static final int X_MAX = 1100 - 1;
    
    boolean penDown;
    int penX, penY;

    public Plotter(Computer computer) {
        this.computer = computer;
        setSpeed(300);      // Plotter can do 300 steps/second
    }

    public int addressBase() {
        return 7168;
    }

    public int addressMask() {
        return 0x1FC0;          // 0b1111111000000
    }
    
    // TODO: Might be useful to allow for an output file that can capture
    //       the plotter commands in a simple way.  A utility program
    //       could then be added to redraw the output off-line.

    public void controlWrite(int addr, long acc) {
        int x = 0, y = 0;
        switch (addr) {
            case 7184: penDown = false; break;
            case 7200: penDown = true;  break;
            case 7169: x = 1;           break;
            case 7170: x = -1;          break;
            case 7172: y = 1;           break;
            case 7176: y = -1;          break;
            case 7173: x = 1;  y = 1;   break;
            case 7174: x = -1; y = 1;   break;
            case 7177: x = 1;  y = -1;  break;
            case 7178: x = -1; y = -1;  break;
        }
        
        // Move the pen and add the device pause if doing real-time simulation
        if (x != 0 || y != 0) {
            penX += x;  penY += y;
            penX = Math.max(X_MIN, Math.min(X_MAX, penX));
            computer.console.setBlockTr(true);
            devicePause();
            viewPlot(penX, penY, addr);
        }    
    }
    
    // Reset plotter 
    public void reset() {
        penX = penY = 0;
        penDown = false;
    }
    
    /*
     * GUI visualisation
     */

    PlotterView view;

    public void setView(PlotterView view) {
        this.view = view;
    }
    
    void viewPlot(int x, int y, int dir) {
        if (view != null) {
            if (penDown)
                view.penDraw(x, y, dir);
            else
                view.penMove(x, y, dir);
        }    
    }
}
