/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine.extension;

import elliott803.hardware.device.ControlDevice;
import elliott803.machine.Word;

/**
 * This is a special control device that can perform actions on the
 * simulator, mostly to help diagnostics and debugging.
 *
 * It responds to the following '72' instructions:
 *
 *  72 8191  - Stop the simulator (equivalent to pressing the reset key)
 *  72 8190  - Produce a core dump
 *  72 8189  - Enable instruction trace
 *  72 8188  - Disable instruction trace
 *
 * @author Baldwin
 */
public class SimulatorControlDevice extends ControlDevice {

    static final int STOP = 8191;
    static final int DUMP = 8190;
    static final int TRACE_ON = 8189;
    static final int TRACE_OFF = 8188;

    public int addressBase() {
        return 8188;
    }

    public int addressMask() {
        return 0x1FFC;
    }

    // Device control actions
    public long control(int addr, long acc) {
        switch (addr) {
            case STOP:      computer.cpu.reset();   break;
            case DUMP:      computer.dump();        break;
            case TRACE_ON:  computer.traceStart();  break;
            case TRACE_OFF: computer.traceStop();   break;
        }
        return Word.NOTHING;
    }
}
