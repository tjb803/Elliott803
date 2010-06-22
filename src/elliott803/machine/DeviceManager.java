/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import java.util.ArrayList;
import java.util.Collection;

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

    Collection<ControlDevice> controlDevices;

    public DeviceManager(Computer computer) {
        this.computer = computer;
        controlDevices = new ArrayList<ControlDevice>();
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
    public void controlWrite(int addr, long acc) {
        ControlDevice device = findDevice(addr);
        if (device != null)
            device.controlWrite(addr, acc);
    }
    
    public long controlRead(int addr) {
        long acc = Word.NOTHING;
        ControlDevice device = findDevice(addr);
        if (device != null)
            acc = device.controlRead(addr);
        return acc;
    }
    
    // TODO: Not very efficient, but we don't expect many devices
    private ControlDevice findDevice(int addr) {
        ControlDevice device = null;
        for (ControlDevice dev : controlDevices) {
            if ((addr & dev.addressMask()) == dev.addressBase()) {
                device = dev;
                break;
            }
        }
        return device;
    }
}
