/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2013
 */
package elliott803.hardware;

import java.io.IOException;
import java.io.InputStream;

import elliott803.machine.Computer;
import elliott803.telecode.Telecode;

/**
 * Tape reader.  Currently only supports 5-hole tape read as bytes from an InputStream.
 *
 * @author Baldwin
 */
public class Reader extends TapeDevice {

    InputStream inputTape = null;

    public Reader(Computer computer, int id) {
        super(computer, id);
        setSpeed(500);      // Readers run at 500 cps
    }

    // Load a new tape
    public void setTape(InputStream tape) {
        if (inputTape != null) {
            try {
                inputTape.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        inputTape = tape;
        viewTape(tape);
        deviceReady();
    }

    // Read the next character
    public int read() {
        // Attempt to read the next character, if there is no tape loaded
        // enter a busy wait.  When the busy wait is cleared, attempt to
        // read a character again.  Finally add the device pause if 
        // doing real-time simulation.
        int ch = readCh();
        if (inputTape == null) {
            deviceWait();
            ch = readCh();
        }
        if (inputTape != null) {
            transfer(ch);
        }

        return (ch & Telecode.CHAR_MASK);
    }

    // Read a character
    private int readCh() {
        int ch = 0;
        if (inputTape != null) {
            try {
                ch = inputTape.read();
                if (ch == -1) {
                    setTape(null);
                    ch = 0;
                }
            } catch (IOException e) {
                System.err.println(e);
                setTape(null);
            }
        }
        return ch;
    }

    // Mainly for debugging
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("READER ").append(id).append(":");
        sb.append(" tape=").append(inputTape);
        sb.append(" ").append(deviceBusy() ? "WAITING" : "READY");
        return sb.toString();
    }
}
