/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2013
 */
package elliott803.hardware;

import elliott803.machine.Computer;
import elliott803.telecode.Telecode;

/**
 * Teletype.  Although this extends the punch and is essentially a punch with
 * different output, it does not enter a busy wait if there is no output stream
 * set - it simply throws away and characters printed.  The assumption is that
 * there will be a GUI viewer attached to display the output.
 *
 * @author Baldwin
 */
public class Teletype extends Punch {

    public Teletype(Computer computer, int id) {
        super(computer, id);
        setSpeed(10);       // Teletype runs at 10 cps
    }

    // Override the write() method to avoid busy wait if no output stream
    public void write(int ch) {
        ch &= Telecode.CHAR_MASK;
        writeCh(ch);
        transfer(ch);
    }

    // Mainly for debugging
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TELETYPE ").append(id).append(":");
        sb.append(" tape=").append(outputTape);
        sb.append(" ").append(deviceBusy() ? "WAITING" : "READY");
        return sb.toString();
    }
}

