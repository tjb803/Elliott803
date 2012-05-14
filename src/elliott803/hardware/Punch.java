/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.hardware;

import java.io.IOException;
import java.io.OutputStream;

import elliott803.machine.Computer;
import elliott803.telecode.Telecode;

/**
 * Tape punch.  Currently only supports 5-hole tape written as bytes to an OutputStream.
 *
 * @author Baldwin
 */
public class Punch extends TapeDevice {

    OutputStream outputTape = null;

    public Punch(Computer computer, int id) {
        super(computer, id);
    }

    // Set a new output tape
    public void setTape(OutputStream tape) {
        if (outputTape != null) {
            try {
                outputTape.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        outputTape = tape;
        viewTape(tape);
        deviceReady();
    }

    // Write the next character
    public void write(int ch) {
        // Attempt to write the next character, if there is no tape loaded
        // enter a busy wait.  When the busy wait is cleared, attempt to
        // write the character again.
        ch &= Telecode.CHAR_MASK;
        writeCh(ch);
        if (outputTape == null) {
            deviceWait();
            writeCh(ch);
        }
        if (outputTape != null) {
            viewChar(ch);
        }
    }

    // Write a character
    void writeCh(int ch) {
        if (outputTape != null) {
            try {
                outputTape.write(ch);
            } catch (IOException e) {
                System.err.println(e);
                setTape(null);
            }
        }
    }

    // Mainly for debugging
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PUNCH ").append(id).append(":");
        sb.append(" tape=").append(outputTape);
        sb.append(" ").append(deviceBusy() ? "WAITING" : "READY");
        return sb.toString();
    }
}
