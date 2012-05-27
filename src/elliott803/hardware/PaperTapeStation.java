/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009, 2012
 */
package elliott803.hardware;

import java.io.InputStream;
import java.io.OutputStream;

import elliott803.machine.Computer;

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
    
    boolean swapReader, swapPunch;

    public PaperTapeStation(Computer computer) {
        this.computer = computer;

        // Set two tape readers
        readers = new Reader[2];
        readers[READER1] = new Reader(computer, 1);
        readers[READER2] = new Reader(computer, 2);
        swapReader = false;

        // Set two tape punches and a teletype
        punches = new Punch[3];
        punches[PUNCH1] = new Punch(computer, 1);
        punches[PUNCH2] = new Punch(computer, 2);
        punches[TELETYPE] = new Teletype(computer, 3);
        swapPunch = false;
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
     * Swap readers and punches
     */
    public void setReaders(boolean exchange) {
        if (exchange != swapReader) {
            Reader r = readers[0];  readers[0] = readers[1];  readers[1] = r;
            swapReader = exchange;
        }
    }
    
    public void setPunches(boolean exchange) {
        if (exchange != swapPunch) {
            Punch p = punches[0];  punches[0] = punches[1];  punches[1] = p;
            swapPunch = exchange;
        }
    }
    
    /*
     * Set real-time flag on all devices
     */
    public void setRealTime(boolean rt) {
        for (Punch punch : punches)
            punch.setRealTime(rt);
        for (Reader reader : readers) 
            reader.setRealTime(rt);
    }

    /*
     * Read and write characters
     */
    public int read(int addr) {
        int ch = 0;
        if (addr >= 2048 && readers[READER2] != null) {
            ch = readers[READER2].read();
        } else {
            ch = readers[READER1].read();
        }
        return ch;
    }

    public void write(int addr) {
        if (addr >= 4096 && punches[TELETYPE] != null) {
            punches[TELETYPE].write(addr);
        } else if (addr >= 2048 && punches[PUNCH2] != null) {
            punches[PUNCH2].write(addr);
        } else {
            punches[PUNCH1].write(addr);
        }
    }
}
