/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware.device;

/**
 * This is the base class for all Control devices.  These are the non-block-transfer
 * type devices that respond to the 72 instructions.
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
     * accumulator and can return a value which will be stored in the accumulator.
     * Or it can return Word.NOTHING to leave the accumulator unchanged.
     *
     * Subclasses must override this method.
     */
    public abstract long control(int addr, long acc);
}