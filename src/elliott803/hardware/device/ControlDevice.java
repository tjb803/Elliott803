/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware.device;

import elliott803.machine.Word;


/**
 * This is the base class for all Control devices.  These are the non-block-transfer
 * type devices that respond to the 72 and 75 instructions.
 *
 * @author Baldwin
 */
public abstract class ControlDevice extends Device {

    /*
     * These methods define the range of addresses associated with this device.  The
     * device will be used if (address & addressMask) == addressBase
     *
     * Subclasses must override these methods.
     */
    public abstract int addressBase();
    public abstract int addressMask();

    /*
     * This method is invoked when a 72 instruction with an address in the range
     * declared by the device is executed.  It is passed the current value in the
     * accumulator.
     * 
     * Subclasses can override this method.
     */
    public void controlWrite(int addr, long acc) {
        return;
    }
    
    /*
     * This method is invoked when a 75 instruction with an address in the range
     * declared by the device is executed.  It can return a value which is 
     * placed in the accumulator.
     * 
     * Subclasses can override this method.
     */
    public long controlRead(int addr) {
        return Word.NOTHING;
    }
}
