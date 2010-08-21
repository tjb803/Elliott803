/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009,2010
 */
package elliott803.machine.extension;

import java.util.Random;

import elliott803.hardware.device.ControlDevice;

/**
 * This is a special control device that can set the accumulator to a
 * random positive integer from 0 to 2^38-1.
 *
 * It responds to the following '75' instruction:
 *
 *  75 8000  - Set ACC to random positive integer
 *
 * I think the machine I used actually had some custom hardware installed that
 * could do this (or something very much like it).
 *
 * @author Baldwin
 */
public class RandomNumberDevice extends ControlDevice {

    Random random = new Random();

    public int addressBase() {
        return 8000;
    }

    public int addressMask() {
        return 0x1FFF;
    }
    
    public long controlRead(int addr) {
        long hiBits = random.nextInt(1<<19);
        long loBits = random.nextInt(1<<19);
        return (hiBits<<19) | loBits; 
    }
}
