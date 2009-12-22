/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import java.util.HashSet;
import java.util.Set;

import elliott803.hardware.device.ControlDevice;

/**
 * This class manages the various control and block mode devices that can be attached
 * to the machine.  Some devices were standard (such as the plotter and the film handlers)
 * other devices less common or even non-standard, but all can be easily simulated and
 * handled by this device manager class.
 *
 * @author Baldwin
 */
public class DeviceManager {

    public Computer computer;

    Set<ControlDevice> controlDevices;

    public DeviceManager(Computer computer) {
        this.computer = computer;
        controlDevices = new HashSet<ControlDevice>();
    }

    /*
     * Register a new device
     */
    public void addControlDevice(ControlDevice device) {
        device.setComputer(computer);
        controlDevices.add(device);
    }

    /*
     * Find the device that handles an specific address and invoke it.
     */
    public long control(int addr, long acc) {
        long word = Word.NOTHING;
        for (ControlDevice device : controlDevices) {
            if ((addr & device.addressMask()) == device.addressBase()) {
                word = device.control(addr, acc);
                break;
            }
        }
        return word;
    }
}
