/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
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
 * Not sure whether the pen starts in the middle or at the left?
 *
 * @author Baldwin
 */
public class Plotter extends ControlDevice {

    final static int PEN_E = 0x1;
    final static int PEN_W = 0x2;
    final static int PEN_N = 0x4;
    final static int PEN_S = 0x8;
    final static int PEN_UP = 0x10;
    final static int PEN_DOWN = 0x20;
    final static int PEN_MASK = 0x2F;

    public Plotter(Computer computer) {
        this.computer = computer;
    }

    public int addressBase() {
        return 7168;
    }

    public int addressMask() {
        return 0x1FC0;          // 0b1111111000000
    }

    public void controlWrite(int addr, long acc) {
//        viewPen(addr & PEN_MASK);
    }
    
    /*
     * GUI visualisation
     */

//    PlotterView view;
//
//    public void setView(PlotterView view) {
//        this.view = view;
//    }
//    
//    void viewPen(int command) {
//        if (view != null) 
//            view.updatePen(command);
//    }
}
