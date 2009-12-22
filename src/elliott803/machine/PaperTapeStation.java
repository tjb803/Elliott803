/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.machine;

import java.io.InputStream;
import java.io.OutputStream;

import elliott803.hardware.Punch;
import elliott803.hardware.Reader;
import elliott803.hardware.Teletype;

/**
 * The Paper Tape Station controlling the tape readers, tape punches and
 * the teletype
 *
 * @author Baldwin
 */
public class PaperTapeStation {

    public static final int READER1 = 0;
    public static final int READER2 = 1;
    public static final int PUNCH1 = 0;
    public static final int PUNCH2 = 1;
    public static final int TELETYPE = 2;

    public Computer computer;                 // The owning computer

    public Reader[] readers;
    public Punch[] punches;

    public PaperTapeStation(Computer computer) {
        this.computer = computer;

        // Set two tape readers, two tape punches and a teletype
        readers = new Reader[2];
        readers[READER1] = new Reader(computer, 1);
        readers[READER2] = new Reader(computer, 2);

        punches = new Punch[3];
        punches[PUNCH1] = new Punch(computer, 1);
        punches[PUNCH2] = new Punch(computer, 2);
        punches[TELETYPE] = new Teletype(computer, 3);
    }

    /*
     * Set input and output tape streams
     */
    public void setReaderTape(int id, InputStream tape) {
        if (id < readers.length && readers[id] != null)
            readers[id].setTape(tape);
    }

    public void setPunchTape(int id, OutputStream tape) {
        if (id < punches.length && punches[id] != null)
            punches[id].setTape(tape);
    }

    /*
     * Read and write characters
     */
    public int read(int addr) {
        int ch = 0;
        if (addr >= 2048 && readers[1] != null) {
            ch = readers[1].read();
        } else {
            ch = readers[0].read();
        }
        return ch;
    }

    public void write(int addr) {
        if (addr >= 4096 && punches[2] != null) {
            punches[2].write(addr);
        } else if (addr >= 2048 && punches[1] != null) {
            punches[1].write(addr);
        } else {
            punches[0].write(addr);
        }
    }
}
