/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware.device;

import elliott803.machine.Computer;

/**
 * This is the base class for all peripheral devices.  These devices can cause the cpu
 * to enter a 'busy' wait condition if they are not in a ready state or need to wait
 * for some operation to occur.
 *
 * @author Baldwin
 */
public abstract class Device {

    public Computer computer;   // The owning computer

    boolean busy;               // Indicate device is busy

    public void setComputer(Computer computer) {
        this.computer = computer;
    }

    /*
     * Get and set busy state
     */
    public boolean deviceBusy() {
        return busy;
    }

    /*
     * Enter a busy wait.  This condition will hold until either the device
     * itself becomes ready or the computer reset operation is performed.
     */
    protected void deviceWait() {
        busy = true;
        computer.busyWait();
    }

    /*
     *  Signal the device is now ready.
     */
    protected void deviceReady() {
        if (busy) {
            computer.busyClear();
            busy = false;
        }
    }
}
