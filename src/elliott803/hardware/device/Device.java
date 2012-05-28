/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2012
 */
package elliott803.hardware.device;

import elliott803.machine.Computer;

/**
 * This is the base class for all peripheral devices.  These devices can cause the cpu
 * to enter a 'busy' wait condition if they are not in a ready state or need to wait
 * for some operation to occur.
 * 
 * They can also add a delay (measured in microseconds) if running in real-time mode. 
 *
 * @author Baldwin
 */
public abstract class Device {

    public Computer computer;   // The owning computer

    boolean busy;               // Indicate device is busy
    boolean realTime;           // Real-time operation 
    int delay;                  // Delay in microseconds for real-time

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
    
    /*
     * Controls for real-time operation of devices.
     * Speed is provided in device operations per second
     */    
    public void setRealTime(boolean rt) {
        realTime = rt;
    }
    
    protected void setSpeed(int cps) {
        delay = (cps == 0) ? 0 : 1000000/cps;
    }
    
    protected void devicePause() {
        if (realTime && delay > 0) {
            // The timing delays to simulate the real-time behaviour of the device
            // are done by adding extra CPU 'cycles' to the current instruction and
            // allowing the CPU timing loops handle it.  This works better than
            // trying to add a simple pause here (say by a Thread.sleep()) as it 
            // provides a finer level of control and, perhaps more usefully, helps
            // keep the CPU current speed calculation simple as we don't need to
            // exclude the time spent pausing for the device.
            computer.cpu.addDelay(delay);
        }    
    }
}
