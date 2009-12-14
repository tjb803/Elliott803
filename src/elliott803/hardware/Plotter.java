/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware;

import elliott803.hardware.device.ControlDevice;
import elliott803.machine.Computer;
import elliott803.machine.Word;

/**
 * The CalComp plotter.  The plotter has paper 11 inches wide and can move the pen
 * in 1/100 inch increments in each direction (including diagonals).
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

    public Plotter(Computer computer) {
        this.computer = computer;
    }

    public int addressBase() {
        return 7168;
    }

    public int addressMask() {
        return 0x1FC0;          // 0b1111111000000
    }

    public long control(int addr, long acc) {
        // TODO: Plotter not yet implemented
        return Word.NOTHING;
    }
}
